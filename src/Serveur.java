import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Serveur {	
	private final static int PORT = 10000;
	private final static int MAX_CONNECTION = 10;
	private int nbConnection=0;
	private ArrayList<RefClient> clients;
	
	Serveur() {
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
			
			//envoi
			out.writeUTF("Connected");
			
			while(!ref.isDeco()) {
				m=in.readUTF();
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
