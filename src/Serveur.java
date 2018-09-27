import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Serveur {	
	private final static String BDD_URL = "jdbc:mysql://localhost:3306/bdd_auth?user=java&password=password&useSSL=false&serverTimezone=Europe/Paris";
	private final static String REQUETE_SIGNIN = "SELECT mot_de_passe FROM Compte WHERE nom= ?;";
	private final static String REQUETE_SIGNUP = "INSERT INTO Compte (mot_de_passe, nom) VALUES (MD5(?),?)";
	private final static int PORT = 10000;
	private final static int MAX_CONNECTION = 10;
	private int nbConnection=0;
	private ArrayList<RefClient> clients;
	private Connection connexion;
	private PreparedStatement preInStatement;
	private PreparedStatement preUpStatement;
	
	Serveur() {
		/* Chargement du driver JDBC pour MySQL */
		try {
		    Class.forName( "com.mysql.cj.jdbc.Driver" );
		} catch ( ClassNotFoundException e ) {
		    System.out.println("pas trouve");
		}
		//connexion pour authentification
		try {
			connexion = DriverManager.getConnection(BDD_URL);
			preInStatement = connexion.prepareStatement(REQUETE_SIGNIN);
			preUpStatement = connexion.prepareStatement(REQUETE_SIGNUP);
			System.out.println("connexion sql reussie");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		clients = new ArrayList<RefClient>();
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
			System.out.println("max number of connection reached");
			
			sSock.close();
			preInStatement.close();
			preUpStatement.close();
			connexion.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
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
			

			String[] methNomPass = new String[3];
			methNomPass = m.split(";",3);
			try {
				if(methNomPass[0].equals("/signin")) {
					preInStatement.setString(1,methNomPass[1]);
					ResultSet resReq = preInStatement.executeQuery();
					resReq.next();
					System.out.println(methNomPass[2]+" "+resReq.getString("mot_de_passe"));
					if(!methNomPass[2].equals(resReq.getString("mot_de_passe"))) {
						ref.setDeco(true);
						out.writeUTF("Authentification failed");
					}
					else {
						out.writeUTF("Connected");
					}
				}
				else if(methNomPass[0].equals("/signup")) {
					preUpStatement.setString(1,methNomPass[1]);
					preUpStatement.setString(2,methNomPass[2]);
					if(1==preUpStatement.executeUpdate()) {
						out.writeUTF("Connected");
					}
					else {
						ref.setDeco(true);
						out.writeUTF("Authentification failed, name already in use");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			while(!ref.isDeco()) {
				System.out.println("hm");
				m=in.readUTF();
				System.out.println("hmm");
				if(m.equals("/quit")) {
					send(ref.getUserName()+" has left the server.");
					ref.setDeco(true);
				}
				else if(!m.isEmpty()) {
					send(ref.getUserName()+" : "+m);
				}
			}
			System.out.println("adieu"+nbConnection);
			out.writeUTF("Disconnected");
			out.close();
			in.close();
			clients.remove(ref);
			nbConnection--;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
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
