import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Serveur {	
	private final static int PORT = 10000;
	private final static int MAX_CONNECTION = 10;
	private final static int MAX_JOUEUR = 3;
	private final static int NOMBRE_QUESTIONS = 10;
	private final static int NOMBRE_REPONSES = 4;
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
			
			out.close();
			in.close();
			//recupere la position dans joueurs de la position dans client du joueur
			int iii=parties.get(clients.get(idListe).getIdPartieRej()).joueurs.indexOf(idListe);
			//supprime le joueur de joueurs
			parties.get(clients.get(idListe).getIdPartieRej()).depJoueur(iii);
			//supprime le joueur de clients
			clients.remove(ref);
			actualisationIndices(idListe);
			nbConnection--;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void actualisationIndices(int idClients) {
		int idPartie;
		int idJoueur;
		for(int i=0;i<clients.size();i++) {
			if(i>=idClients) {
				idPartie = clients.get(i).getIdPartieRej();
				idJoueur = parties.get(idPartie).joueurs.indexOf(i);
				if(idJoueur>0) {
					parties.get(idPartie).modJoueur(idJoueur);
				}					
			}
		}
	}
	
	private void recherchePartie(int idClient,Theme t) {
		int i=0;
		while(i<parties.size()) {
			if(!parties.get(i).isEnCours() && parties.get(i).getTheme().equals(t)) {
				parties.get(i).nouvJoueur(idClient);
				clients.get(idClient).setIdPartieRej(i);
				if(parties.get(i).getNbJoueurs()==MAX_JOUEUR) {
					parties.get(i).setEnCours(true);
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
			clients.get(idClient).setIdPartieRej(parties.indexOf(p));
		}
	}

	///R�cuperer la r�ponse d'une URl
	public static String get(String url) throws IOException{

		String source ="";
		URL oracle = new URL(url);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			source +=inputLine;
		in.close();
		return source;
	}

	///Reformuler la reponse JSON en une classe exploitable
	public static FormalismeQuestion obtenirQuestionEtResultats() {
		// Declaration des variables
		String retourDeLaPage;
		FormalismeQuestion questions = new FormalismeQuestion();

		try {
			retourDeLaPage = get("https://opentdb.com/api.php?amount="+NOMBRE_QUESTIONS+"&type=multiple");
			System.out.println(retourDeLaPage);
			System.out.println(" ");

			//On tranforme la r�ponse String de la page en objet Json
			JsonObject reponseRequete = new JsonParser().parse(retourDeLaPage).getAsJsonObject();

			// On verifie qu'on re�oit une r�ponse de la page
			if (reponseRequete.get("response_code").getAsString().equals("0")) {
				System.out.println("on re�oit une r�ponse de la base");

				// On remplit un JSon array avec l'ensemble des resultats
				JsonArray jsonResultatRequete =  reponseRequete.get("results").getAsJsonArray();
				String questionsListe[] = new String[NOMBRE_QUESTIONS];
				String reponsesTab[][] = new String[NOMBRE_QUESTIONS][NOMBRE_REPONSES];
				String bonnesReponsesListe[] = new String[NOMBRE_QUESTIONS];
				for (int i=0; i<NOMBRE_QUESTIONS ; i++) {
					JsonObject groupeQuestionReponses = jsonResultatRequete.get(i).getAsJsonObject();
					questionsListe[i] = replacement(groupeQuestionReponses.get("question").getAsString());
					bonnesReponsesListe[i] = replacement(groupeQuestionReponses.get("correct_answer").getAsString());
					reponsesTab[i][0] = replacement(groupeQuestionReponses.get("correct_answer").getAsString());

					//On remplit le tableau des reponses
					for (int j =1; j<NOMBRE_REPONSES;j++) {

						reponsesTab[i][j] = replacement(groupeQuestionReponses.get("incorrect_answers").getAsJsonArray().get(j-1).getAsString());
					}

					questions = new FormalismeQuestion(questionsListe,bonnesReponsesListe,reponsesTab);
				}
			}
			else {
				// Non g�r� pour le moment
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return(questions);
	}

	public static String replacement(String s) {
		String result ="";
		String r = s.replace("&#039;", "'");
		String ro = r.replace("&quot;", "\"");
		result = ro;

		return(result);
	}
	
	void gestionPartie(int idPartie) {
		int idJoueurIter;
		int idJoueurAns;
		//requete questions
		FormalismeQuestion requete = obtenirQuestionEtResultats();
		String listeQuestions[] = requete.getListeQuestions();
		String listeBonnesReponses[] = requete.getListeBonnesReponses();
		String listeReponses[][] = requete.getListeReponses();
		System.out.println(listeQuestions);

		try {
			System.out.println(parties.get(idPartie).joueurs.size());
			for(int n=0;n<parties.get(idPartie).joueurs.size();n++) {
				idJoueurIter=parties.get(idPartie).joueurs.get(n);
				clients.get(idJoueurIter).getOut().writeUTF(Commandes.getReady.toString());
				System.out.println(clients.get(idJoueurIter).getUserName()+idJoueurIter+"ok");
			}
			System.out.println("attente de 10s");
			Thread.sleep(10000);
			int i=0;
			boolean enAtt = false;
			while(i<10) {
				for(int n=0;n<parties.get(idPartie).joueurs.size();n++) {
					idJoueurIter=parties.get(idPartie).joueurs.get(n);
					System.out.println(clients.get(idJoueurIter).getUserName()+" question posee");
					clients.get(idJoueurIter).getOut().writeUTF(Commandes.question.toString()/* + ";" + listeQuestions[i]+";"+listeReponses[i][0]+";"+listeReponses[i][1]+";"+listeReponses[i][2]+";"+listeReponses[i][3]*/);
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
						while (k < parties.get(idPartie).joueurs.size()-1
								&& (clients.get(parties.get(idPartie).joueurs.get(k)).getAnswer().isEmpty()
										|| clients.get(parties.get(idPartie).joueurs.get(k)).isHasAnswered())) {
							k++;
						}
						idJoueurAns = parties.get(idPartie).joueurs.get(k);
						if (clients.get(idJoueurAns).getAnswer().equals(listeBonnesReponses[i])) {
							clients.get(idJoueurAns).right();
							clients.get(idJoueurAns).getOut().writeUTF(Commandes.right.toString());
							enAtt = false;
							System.out.println(clients.get(idJoueurAns).getUserName() + " bonne reponse");
							for (int n = 0; n < parties.get(idPartie).joueurs.size(); n++) {
								idJoueurIter = parties.get(idPartie).joueurs.get(n);
								if (!clients.get(idJoueurIter).getUserName()
										.equals(clients.get(idJoueurAns).getUserName())) {
									clients.get(idJoueurIter).getOut().writeUTF(Commandes.otherRight.toString()+";" + listeBonnesReponses[i]);
									clients.get(idJoueurIter).setHasAnswered(false);
								}
							}
						} else {
							if (!clients.get(idJoueurAns).isHasAnswered()) {
								clients.get(idJoueurAns).getOut().writeUTF(Commandes.wrong.toString() +";" + listeBonnesReponses[i]);
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
									clients.get(idJoueurIter).getOut().writeUTF(Commandes.allWrong.toString()+";"+listeBonnesReponses[i]);
									clients.get(idJoueurIter).setHasAnswered(false);
								}
							}
						}
					}
				}
				i++;
			}
			parties.get(idPartie).setEnCours(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
