import java.io.*;
import java.net.*;

import javax.swing.JFrame;

public class Client extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket sock;
	private Boolean connected;
	private final static int PORT = 10000;
	private String server_name;
	private String client_name;
	private Commandes clientState;
	private boolean newClientState = false;
	String question;
	String reponseHG;
	String reponseHD;
	String reponseBG;
	String reponseBD;

	public int port;
	String etat="";

	Gui g;
	
	Client() {
		g = new Gui(this);
		setContentPane(g);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Client();
	}
	
	void getClientInformation(String portGUI, String serveur, String pseudo) {
		System.out.println("appuye sur connexion");
		setPort(Integer.parseInt(portGUI));
		setServer_name(serveur);
		setClient_name(pseudo);
		connect(server_name, port, client_name);
	}
		
	public void connect(String serv,int port,String name) {
		System.out.println("Connection to server");
		System.out.println("serv = " + serv);
		System.out.println("port = " + port);
		System.out.println("name = " + name);
		miseEnFormeQuestion(""); //pour le debug suelement
		try {
			//gestion socket
			sock = new Socket(serv,port);
			
			//gestion streams
			in = new DataInputStream(sock.getInputStream());
			out = new DataOutputStream(sock.getOutputStream());
			
			//envoi
			out.writeUTF(name);
			
			g.setIHMSelectTheme();
			System.out.println("affichage supposé IHM");
			//System.out.println(inf);
			connected=true;
			Thread th = new Thread(() -> {
				while(connected) {
					//System.out.println("test authentification");
					//setClientState(Commandes.waitToJoinPartie,true);
	
					
					String[] sComm;
					String m;
					try {
						m = in.readUTF();
						sComm=m.split(";");
						Commandes comm = Commandes.valueOf(sComm[0]);
						switch(comm) {
							case connect:
								System.out.println("Connecte");
								setClientState(Commandes.connect, true);
								break;
							case disconnect:
								disconnect();
								System.out.println("Deconnexion");
								break;
							case question:
								System.out.println("Reception de la question");
								question = sComm[1];
								reponseBD = sComm[2];
								reponseBG = sComm[3];
								reponseHD = sComm[4];
								reponseHG = sComm[5];
								g.setIHMQuestion(question, reponseHG, reponseHD, reponseBG, reponseBD);
								System.out.println("affichage question");
								setClientState(Commandes.question, false);
								break;
							case right:
								g.noAnswer(sComm[1]);
								System.out.println("reponse juste");
								break;
							case allWrong:
								g.noAnswer(sComm[1]);
								System.out.println("fin du temps ou tout faux");
								break;
							case otherRight:
								g.noAnswer(sComm[1]);
								System.out.println("trop lent");
								break;
							case wrong:
								g.badAnswer(sComm[1]);
								System.out.println("faux bobo");
								break;
							case getReady:
								System.out.println("démarrage question imminent");
								g.setIHMGetReady();
								System.out.println("affichage getReady");
								setClientState(Commandes.getReady, false);
								break;
							default:
								System.out.println("non valide");
						}
						//g.nouvM(innf);
					} catch(EOFException e) {
						//System.out.println("no m");
					} catch (IOException e) {
						e.printStackTrace();
					} catch(IllegalArgumentException e) {
						e.printStackTrace();
					}
					//this.disconnect();
				}
				try {
					in.close();
					out.close();
					sock.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			});
			th.start();
		}
		catch(ConnectException e) {
			System.out.println("Connection failed");
		}
		catch(UnknownHostException e) {
			System.out.println("Connection failed");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}	
	
	private void miseEnFormeQuestion(String chaineDeCaractere) {
		//TODO decoupage entre questions et 4 reponses possibles
		question = "Comment écrit on Jérémy ?";
		reponseBD = "jeremie";
		reponseBG = "jeremye";
		reponseHD = "jeremi";
		reponseHG = "berthier";
	}
	
	public void send(String m) {
		try {
			out.writeUTF(m);
			System.out.println("send m : " + m);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void disconnect() {
		try {
			out.writeUTF(Commandes.disconnect.toString());
			connected=false;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}


	

	public void setPort(int port) {
		this.port = port;
	}

	public String getServer_name() {
		return server_name;
	}

	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public static int getPort() {
		return PORT;
	}
	
	public void setClientState(Commandes stateName, boolean stateBoolean) {
		clientState = stateName;
		newClientState = stateBoolean;
	}
	
	public void setboolClientState(boolean etat) {
		newClientState = etat;
	}
	
	public boolean getboolClientState() {
		return newClientState;
	}
	

	public Commandes getClientState() {
		return clientState;
	}
	
}
