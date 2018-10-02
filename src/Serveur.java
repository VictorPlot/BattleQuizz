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
			nbConnection++;
			
			String[] sComm;			
			
			while(!ref.isDeco()) {
				try {
					m = in.readUTF();
					sComm = m.split(";");
					Commandes comm = Commandes.valueOf(sComm[0]);
					switch(comm) {
						case joinPartie:
							recherchePartie(clients.indexOf(ref),Theme.valueOf(sComm[1]));
						case disconnect:
							ref.setDeco(true);
							System.out.println(ref.getUserName()+" : Deconnexion");
						case answer:
							System.out.println(ref.getUserName()+" : a repondu");
						default:
							System.out.println(ref.getUserName()+" : Commmande de serveur");
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
		//requete questions
		
		//get ready decompte
		
		//question (while)
		//envoi Commandes.question
		//attente Commandes.answer ou fin temps
		//prise en compte de Commandes.disconnect
	}
	
	private void send(String m) {
		for(int i=0;i<clients.size();i++) {
			try {
				System.out.println(clients.get(i).getOut());
				clients.get(i).getOut().writeUTF(m);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
