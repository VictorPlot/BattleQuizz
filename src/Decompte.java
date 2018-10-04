import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

public class Decompte extends JLabel {
	private int c;
	private Timer t;
	
	private static final long serialVersionUID = 1L;
	
	Decompte() {		
		this.setText(String.valueOf(c));
		
		t= new Timer(1000, taskPerformer);
	} 
	
	private ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			c--;
			setLabel(String.valueOf(c));
		}
	};
	
	public void startTimer() {
		c=10;
		t.start();
		System.out.println("Start");
	}
	
	public void stopTimer() {
		t.stop();
		System.out.println("Stop");
	}
	
	private void setLabel(String s) {
		this.setText(s);
	}
}