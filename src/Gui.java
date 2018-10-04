import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private Client cli;
	private JButton jBConnexion;
	private JButton JBTheme;
	private JButton JBAnswerHG;
	private JButton JBAnswerHD;
	private JButton JBAnswerBG;
	private JButton JBAnswerBD;
	//private JButton JBAnswered;
	private static JTextField jTextPseudo;
	private JTextField jTextMDP;
	private JTextField jTextPort;
	private JTextField jTextServeur;
	private static JPanel mainPanel;
	private static JPanel northPanel;
	private static JPanel centerPanel;
	private static JPanel southPanel;
	private JTextArea jTAQuestion;
	private JLabel jNumeroQuestion;
	private JComboBox<Difficulty> jListeDeroulanteDifficulty;
	private JComboBox<Theme> jListeDeroulanteTheme;
	
	Gui(Client client){
		cli = client;
		setIHMAuthentification();
	}

	private void clearIHM() {
		mainPanel.repaint();
		mainPanel.removeAll();
		northPanel.removeAll();
		centerPanel.removeAll();
		southPanel.removeAll();
	}
	
	private void enableIHM() {
		mainPanel.repaint();
		mainPanel.setVisible(true);
		mainPanel.revalidate();
	}
	
	public void setIHMSelectTheme() {
		clearIHM();
		centerPanel = new JPanel(new GridLayout(2,2));		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		JLabel jLabelConsigne = new JLabel("Choississez votre thème et niveau."); 
		JLabel jTitre = new JLabel("CONNEXION A UNE SALLE");
		northPanel.add(jTitre, BorderLayout.NORTH);
		northPanel.add(jLabelConsigne, BorderLayout.SOUTH);
		
		JLabel jDifficulty= new JLabel("Difficulté : ");
		JLabel jTheme = new JLabel("Thème : ");
		
		jListeDeroulanteDifficulty = new JComboBox<Difficulty>(); 
		jListeDeroulanteDifficulty.setBounds(new Rectangle(21, 92, 130, 25)); 
		for (Difficulty dir : Difficulty.values()) {
			jListeDeroulanteDifficulty.addItem(dir); 
		}
				
		jListeDeroulanteTheme = new JComboBox<Theme>(); 
		jListeDeroulanteTheme.setBounds(new Rectangle(21, 92, 130, 25)); 
		for (Theme dir : Theme.values()) {
			jListeDeroulanteTheme.addItem(dir); 
		}
		
		centerPanel.add(jTheme);
		centerPanel.add(jListeDeroulanteTheme);
		centerPanel.add(jDifficulty);
		centerPanel.add(jListeDeroulanteDifficulty);
		
		JBTheme = new JButton("Pret pour une partie");
		JBTheme.addActionListener(this);
		southPanel.add(JBTheme);
		enableIHM();		
	}



	private void setIHMAuthentification() {
		mainPanel = new JPanel(new BorderLayout());
		northPanel = new JPanel(new BorderLayout());
		centerPanel = new JPanel(new GridLayout(5,1));
		southPanel = new JPanel(new GridLayout());
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		mainPanel.setSize(300, 300);
		add(mainPanel);
		
		JLabel jTitre = new JLabel("AUTHENTIFICATION"); 
		northPanel.add(jTitre);
		JLabel jLabelPseudo = new JLabel("Pseudo : "); 
		jTextPseudo = new JTextField("Hugo");
		JLabel jLabelMDP = new JLabel("Mot de Passe : "); 
		jTextMDP = new JTextField("*****");
		JLabel jLabelServeur = new JLabel("Serveur : ");
		jTextServeur = new JTextField("localhost");
		JLabel jLabelPort = new JLabel("Port : ");
		jTextPort = new JTextField("10000");
				
		centerPanel.add(jLabelPseudo);
		centerPanel.add(jTextPseudo);
		centerPanel.add(jLabelMDP);
		centerPanel.add(jTextMDP);
		centerPanel.add(jLabelServeur);
		centerPanel.add(jTextServeur);
		centerPanel.add(jLabelPort);
		centerPanel.add(jTextPort);
		
		jBConnexion = new JButton("Connexion");
		jBConnexion.addActionListener(this);
		southPanel.add(jBConnexion);
		System.out.println("fin init GUI");
	}
	

	public void setIHMGetReady() {
		clearIHM();
		mainPanel = new JPanel(new BorderLayout());
		northPanel = new JPanel(new BorderLayout());
		centerPanel = new JPanel(new BorderLayout());
		southPanel = new JPanel(new GridLayout(2,2));
		JPanel northPanelEast = new JPanel(new GridLayout(1,2));
		JPanel northPanelWest = new JPanel(new GridLayout(1,2));
		
		northPanel.add(northPanelEast, BorderLayout.EAST);
		northPanel.add(northPanelWest, BorderLayout.WEST);
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		mainPanel.setSize(300, 300);
		add(mainPanel);
		
		JLabel jTitre = new JLabel("Quizz"); 
		northPanel.add(jTitre);
		JLabel jTimer = new JLabel("Temps : ");
		JLabel jTextTimer = new JLabel("-");
		jNumeroQuestion = new JLabel("Question 0/10");
		northPanelEast.add(jTimer);
		northPanelEast.add(jTextTimer);
		northPanelWest.add(jNumeroQuestion);

		jTAQuestion = new JTextArea("Le Jeu va bientôt commencer.");
		centerPanel.add(jTAQuestion);

		JBAnswerHG = new JButton(" - ");
		JBAnswerHD = new JButton(" - ");
		JBAnswerBG = new JButton(" - ");
		JBAnswerBD = new JButton(" - ");
		JBAnswerHG.addActionListener(this);
		JBAnswerHD.addActionListener(this);
		JBAnswerBG.addActionListener(this);
		JBAnswerBD.addActionListener(this);
		southPanel.add(JBAnswerHG);
		southPanel.add(JBAnswerHD);
		southPanel.add(JBAnswerBG);
		southPanel.add(JBAnswerBD);
		System.out.println("fin init QuestionIHM");
		enableIHM();		
	}
	
	
	private void setIHMattendreSalle() {
		clearIHM();
		JLabel jText = new JLabel("En attente d'une salle de jeu");
		mainPanel.add(jText);
		enableIHM();
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == jBConnexion){
			System.out.println("appuie sur bouton connexion");
			
			cli.setboolClientState(false); //assure qu'une nouvelle fenetre sera traitée ensuite
			cli.getClientInformation(jTextPort.getText(), jTextServeur.getText(), jTextPseudo.getText());
		}
		if (e.getSource() == JBTheme){
			System.out.println("appuie sur bouton theme choisi");
			cli.setboolClientState(false); //assure qu'une nouvelle fenetre sera traitée ensuite
			cli.setClientState(Commandes.question, true); //pour le débug 
			// envoie du theme au client puis serveur
			cli.send(Commandes.joinPartie.toString()+";"+jListeDeroulanteTheme.getSelectedItem().toString());
		}
		if (e.getSource() == JBAnswerHG){
			//JBAnswered=JBAnswerHG;
			JBAnswerHG.setBackground(Color.ORANGE);
			cli.send(Commandes.answer.toString()+";"+JBAnswerHG.getText());
			disableButtons();
			System.out.println("1");
		}
		if (e.getSource() == JBAnswerHD){
			//JBAnswered=JBAnswerHD;
			JBAnswerHD.setBackground(Color.ORANGE);
			cli.send(Commandes.answer.toString()+";"+JBAnswerHD.getText());
			disableButtons();
			System.out.println("2");
		}
		if (e.getSource() == JBAnswerBG){
			//JBAnswered=JBAnswerBG;
			JBAnswerBG.setBackground(Color.ORANGE);
			cli.send(Commandes.answer.toString()+";"+JBAnswerBG.getText());
			disableButtons();
			System.out.println("3");
		}
		if (e.getSource() == JBAnswerBD){
			//JBAnswered=JBAnswerBD;
			JBAnswerBD.setBackground(Color.ORANGE);
			cli.send(Commandes.answer.toString()+";"+JBAnswerBD.getText());
			disableButtons();
			System.out.println("4");
		}
		
		/*while (!cli.getboolClientState()) { //permet d'attendre la nouvelle fenetre a afficher
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}*/
		
		/*if (cli.getClientState() == Commandes.waitToJoinPartie){
			setIHMSelectTheme();
			System.out.println("affichage supposé IHM");
		}*/
		/*if (cli.getClientState() ==Commandes.connect){
			//setIHMattendreSalle();
			System.out.println("affichage attente salle");
		}*/
		/*if (cli.getClientState() == Commandes.question){
			setIHMQuestion(cli.question, cli.reponseHG, cli.reponseHD, cli.reponseBG, cli.reponseBD);
			System.out.println("affichage question");
		}*/
		/*if (cli.getClientState() == Commandes.getReady){
			setIHMGetReady();
			System.out.println("affichage getReady");
		}*/
		cli.setboolClientState(false);
	}

	public void setIHMQuestion(String question, String reponse1, String reponse2, String reponse3, String reponse4) {
		int indexbarre = jNumeroQuestion.getText().indexOf("/");
		String s=jNumeroQuestion.getText().substring(9,indexbarre);
		int nQ=Integer.parseInt(s);
		jNumeroQuestion.setText("Question "+Integer.toString(nQ+1)+"/10");
		jTAQuestion.setText(question);
		JBAnswerHG.setText(reponse1);
		JBAnswerHG.setEnabled(true);
		JBAnswerHG.setBackground(Color.LIGHT_GRAY);
		JBAnswerHD.setText(reponse2);
		JBAnswerHD.setEnabled(true);
		JBAnswerHD.setBackground(Color.LIGHT_GRAY);
		JBAnswerBG.setText(reponse3);
		JBAnswerBG.setEnabled(true);
		JBAnswerBG.setBackground(Color.LIGHT_GRAY);
		JBAnswerBD.setText(reponse4);
		JBAnswerBD.setEnabled(true);
		JBAnswerBD.setBackground(Color.LIGHT_GRAY);
		//enableIHM();
	}
	
	public void disableButtons() {
		JBAnswerHG.setEnabled(false);
		JBAnswerBG.setEnabled(false);
		JBAnswerHD.setEnabled(false);
		JBAnswerBD.setEnabled(false);
	}
	
	/*public void goodAnswer() {
		JBAnswered.setBackground(Color.GREEN);
	}*/
	
	public void badAnswer(String good) {
		//JBAnswered.setBackground(Color.RED);
		if (JBAnswerHG.getText().equals(good)){
			JBAnswerHG.setBackground(Color.GREEN);
		}
		if (JBAnswerHD.getText().equals(good)){
			JBAnswerHD.setBackground(Color.GREEN);
		}
		if (JBAnswerBG.getText().equals(good)){
			JBAnswerBG.setBackground(Color.GREEN);
		}
		if (JBAnswerBD.getText().equals(good)){
			JBAnswerBD.setBackground(Color.GREEN);
		}
	}
	
	public void noAnswer(String good) {
		if (JBAnswerHG.getText().equals(good)){
			JBAnswerHG.setBackground(Color.GREEN);
		}
		if (JBAnswerHD.getText().equals(good)){
			JBAnswerHD.setBackground(Color.GREEN);
		}
		if (JBAnswerBG.getText().equals(good)){
			JBAnswerBG.setBackground(Color.GREEN);
		}
		if (JBAnswerBD.getText().equals(good)){
			JBAnswerBD.setBackground(Color.GREEN);
		}
	}
}
