import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RefClient {
	private DataInputStream in;
	private DataOutputStream out;
	private String userName;
	private boolean deco;
	private int  idPartieRej;
	private String answer;
	private boolean hasAnswered;
	private int score;

	RefClient() {
		answer="";
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
	
	public void setIn(DataInputStream inn) {
		in=inn;
	}
	
	public void setOut(DataOutputStream outt) {
		out=outt;
	}

	public void setUserName(String n) {
		userName=n;
	}
	
	public boolean isDeco() {
		return deco;
	}

	public void setDeco(boolean deco) {
		this.deco = deco;
	}
	
	public void setIdPartieRej(int i) {
		this.idPartieRej = i;
	}
	
	public int getIdPartieRej() {
		return idPartieRej;
	}
	
	public void setAnswer(String s) {
		answer = s;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public boolean isHasAnswered() {
		return hasAnswered;
	}

	public void setHasAnswered(boolean hasAnswered) {
		this.hasAnswered = hasAnswered;
	}

	public void right() {
		score++;
	}
	
	public int getScore() {
		return score;
	}
}
