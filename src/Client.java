import java.io.*;
import java.net.*;

import javax.swing.JFrame;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Gui g;
	private DataInputStream in;
	private DataOutputStream out;
	private Socket sock;
	private Boolean co;
	private final static int PORT = 10000;
	private final static String SERVER_NAME = "localhost";
	
	Client() {
		//g = new Gui(this);
		//setContentPane(g);
		//setSize(300,300);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//pack();
		//setVisible(true);
		this.connect(SERVER_NAME,PORT,"lol");
	}
	
	public static void main(String[] args) {
		new Client();
	}
	
	public void connect(String serv,int port,String name) {
		System.out.println("Connection to server");
		try {
			//gestion socket
			sock = new Socket(serv,port);
			
			//gestion streams
			in = new DataInputStream(sock.getInputStream());
			out = new DataOutputStream(sock.getOutputStream());
			
			//envoi
			out.writeUTF(name);
			
			//System.out.println(inf);
			co=true;
			
			Thread th = new Thread(() -> {
				while(co) {
					System.out.println("test authentification");
					String innf;
					try {
						innf = in.readUTF();
						//g.nouvM(innf);
					}
					catch(EOFException e) {
						//System.out.println("no m");
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					this.disconnect();
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
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			out.writeUTF("/quit");
			co=false;
			System.out.println("disconnection");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
