import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RefClient {
	private DataInputStream in;
	private DataOutputStream out;
	private String userName;
	private boolean deco;

	RefClient(DataInputStream inn,DataOutputStream outt,String userN) {
		in=inn;
		out=outt;
		userName=userN;
	}

	public DataInputStream getIn() {
		return in;
	}
	
	public DataOutputStream getOut() {
		return out;
	}

	public String getUserName() {
		return userName;
	}
	
	public boolean isDeco() {
		return deco;
	}

	public void setDeco(boolean deco) {
		this.deco = deco;
	}
}
