import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket sock;
	private Boolean connected=false;
	private final static int PORT = 10000;
	private String server_name;
	private String client_name;

	public int port;

	Gui g;
	
	Client() {
		this.setSize(new Dimension(500,500));
		this.addWindowListener(new WindowEventHandler());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("BattleQuiz Client");
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
			connected=true;
			Thread th = new Thread(() -> {
				while(connected) {					
					String[] sComm;
					String m;
					try {
						m = in.readUTF();
						sComm=m.split(";");
						Commandes comm = Commandes.valueOf(sComm[0]);
						switch(comm) {
							case connect:
								System.out.println("Connecte");
								break;
							case disconnect:
								g.setFinalView(Integer.parseInt(sComm[1]),Integer.parseInt(sComm[2]),Integer.parseInt(sComm[3]));
								disconnect();
								System.out.println("Deconnexion");
								break;
							case question:
								System.out.println("Reception de la question");
								g.setIHMQuestion(sComm[1],sComm[2],sComm[3],sComm[4],sComm[5]);
								System.out.println("affichage question");
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
								break;
							default:
								System.out.println("non valide");
						}
					} catch(EOFException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch(IllegalArgumentException e) {
						e.printStackTrace();
					}
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

	public int getPort() {
		return PORT;
	}
	
	public boolean isConnected() {
		return connected;
	}
}

class WindowEventHandler extends WindowAdapter {
	  public void windowClosing(WindowEvent evt) {
	    System.out.println("fermeture fenetre");
	    if(((Client) evt.getSource()).isConnected()) {
	    	((Client) evt.getSource()).disconnect();
	    }
	  }
}
//icone