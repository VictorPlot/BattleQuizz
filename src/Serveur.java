import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Serveur {	
	private final static int PORT = 10000;
	private final static int MAX_CONNECTION = 10;
	private final static int MAX_JOUEUR = 3;
	private int nbConnection=0;
	private ArrayList<RefClient> clients;
	private ArrayList<Partie> parties;
	
	Serveur() {
		clients = new ArrayList<RefClient>();
		parties = new ArrayList<Partie>();
		System.out.println("Server launch");
		try {
			//gestion socket et des connections clients
			ServerSocket sSock = new ServerSocket(PORT);
			
			while(nbConnection<MAX_CONNECTION) {
				System.out.println("waiting for connection");
				Socket sock = sSock.accept();
				Thread th = new Thread(() -> {
					accConnection(sock);
				});
				th.start();
			}
			System.out.println("max number of connections reached");
			
			sSock.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Serveur();
	}
	
	void accConnection(Socket sock) {
		try {
			//gestion streams
			DataInputStream in = new DataInputStream(sock.getInputStream());
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
			//reception
			String m = in.readUTF();
			System.out.println("New client connected : "+m);
			RefClient ref = new RefClient(in,out,m);
			clients.add(ref);
			int idListe=clients.indexOf(ref);
			System.out.println(idListe);
			nbConnection++;
			
			String[] sComm;			
			
			while(!ref.isDeco()) {
				try {
					m = in.readUTF();
					sComm = m.split(";");
					Commandes comm = Commandes.valueOf(sComm[0]);
					switch(comm) {
						case joinPartie:
							recherchePartie(idListe,Theme.valueOf(sComm[1]));
							break;
						case disconnect:
							ref.setDeco(true);
							System.out.println(clients.get(idListe).getUserName()+" : Deconnexion");
							break;
						case answer:
							synchronized(parties.get(clients.get(idListe).getIdPartieRej()).lock) {
								parties.get(clients.get(idListe).getIdPartieRej()).lock.notify();
								System.out.println(clients.get(idListe).getUserName()+" : a repondu");
							}
							break;
						default:
							System.out.println(clients.get(idListe).getUserName()+" : Commmande de serveur");
					}
				} catch(EOFException e) {
				} catch (IOException e) {
					e.printStackTrace();
				} catch(IllegalArgumentException e) {
					System.out.println(ref.getUserName()+" : Commande non reconnue");
					e.printStackTrace();
				}
			}
			//out.writeUTF(Commandes.disconnect.toString());
			out.close();
			in.close();
			clients.remove(ref);
			nbConnection--;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	void recherchePartie(int idClient,Theme t) {
		int i=0;
		while(i<parties.size()) {
			if(!parties.get(i).isEnCours() && parties.get(i).getTheme().equals(t)) {
				parties.get(i).nouvJoueur(idClient);
				clients.get(idClient).setIdPartieRej(i);
				if(parties.get(i).getNbJoueurs()==MAX_JOUEUR) {
					int idPartie=i;
					Thread th = new Thread(() -> {
						gestionPartie(idPartie);
					});
					th.start();
				}
			}
			i++;
		}
		if(i>=parties.size()) {
			Partie p = new Partie(t);
			p.nouvJoueur(idClient);
			parties.add(p);
		}
	}
	
	void gestionPartie(int idPartie) {
		int idJoueurIter;
		int idJoueurAns;
		//requete questions
		
		try {
			for(int n=0;n<parties.get(idPartie).joueurs.size();n++) {
				idJoueurIter=parties.get(idPartie).joueurs.get(n);
				clients.get(idJoueurIter).getOut().writeUTF(Commandes.getReady.toString());
			}
			System.out.println("attente de 10s");
			Thread.sleep(10000);
			int i=0;
			boolean enAtt = false;
			while(i<10) {
				for(int n=0;n<parties.get(idPartie).joueurs.size();n++) {
					idJoueurIter=parties.get(idPartie).joueurs.get(n);
					System.out.println(clients.get(idJoueurIter).getUserName()+" question posee");
					clients.get(idJoueurIter).getOut().writeUTF(Commandes.question.toString() + ";");
				}
				new java.util.Timer().schedule(
					    new java.util.TimerTask() {
					        @Override
					        public void run() {
					        	for(int n=0;n<parties.get(idPartie).joueurs.size();n++) {
									int idJoueurIter=parties.get(idPartie).joueurs.get(n);
									clients.get(idJoueurIter).setHasAnswered(true);
								}
					        	synchronized(parties.get(idPartie).lock) {
					        		parties.get(idPartie).lock.notify();
					        	}
					        }
					    }, 
					    10000 
				);
				enAtt=true;
				while(enAtt) {
					synchronized (parties.get(idPartie).lock) {
						parties.get(idPartie).lock.wait();
						int k = 0;
						while (k < parties.get(idPartie).joueurs.size()
								&& (clients.get(parties.get(idPartie).joueurs.get(k)).getAnswer().isEmpty()
										|| clients.get(parties.get(idPartie).joueurs.get(k)).isHasAnswered())) {
							k++;
						}
						idJoueurAns = parties.get(idPartie).joueurs.get(k);
						if (clients.get(idJoueurAns).getAnswer().equals("correct")) {
							clients.get(idJoueurAns).right();
							clients.get(idJoueurAns).getOut().writeUTF(Commandes.right.toString());
							enAtt = false;
							System.out.println(clients.get(idJoueurAns).getUserName() + " bonne reponse");
							for (int n = 0; n < parties.get(idPartie).joueurs.size(); n++) {
								idJoueurIter = parties.get(idPartie).joueurs.get(n);
								if (!clients.get(idJoueurIter).getUserName()
										.equals(clients.get(idJoueurAns).getUserName())) {
									clients.get(idJoueurIter).getOut().writeUTF(Commandes.otherRight.toString()+";");
									clients.get(idJoueurIter).setHasAnswered(false);
								}
							}
						} else {
							if (!clients.get(idJoueurAns).isHasAnswered()) {
								clients.get(idJoueurAns).getOut().writeUTF(Commandes.wrong.toString() +";");
								System.out.println(clients.get(idJoueurAns).getUserName() + " mauvaise reponse");
								clients.get(idJoueurAns).setHasAnswered(true);
							}
							enAtt = false;
							for (int n = 0; n < parties.get(idPartie).joueurs.size(); n++) {
								idJoueurIter = parties.get(idPartie).joueurs.get(n);
								if (!clients.get(idJoueurIter).isHasAnswered()) {
									enAtt = true;
								}
							}
							if (!enAtt) {
								for (int n = 0; n < parties.get(idPartie).joueurs.size(); n++) {
									idJoueurIter = parties.get(idPartie).joueurs.get(n);
									clients.get(idJoueurIter).getOut().writeUTF(Commandes.allWrong.toString()+";");
									clients.get(idJoueurIter).setHasAnswered(false);
								}
							}
						}
					}
				}
				i++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
