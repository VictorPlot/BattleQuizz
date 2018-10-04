import java.util.ArrayList;

public class Partie {
	private Theme theme;
	private int nbJoueurs;
	private boolean enCours;
	ArrayList<Integer> joueurs;
	public Object lock;
	private  Difficulty diff;
	
	Partie(Theme t,Difficulty d) {
		theme = t;
		diff = d;
		joueurs = new ArrayList<Integer>();
		lock = new Object();
	}

	public Theme getTheme() {
		return theme;
	}
	
	public Difficulty getDiff() {
		return diff;
	}
	
	public int getNbJoueurs() {
		return nbJoueurs;
	}

	public boolean isEnCours() {
		return enCours;
	}
	
	public void setEnCours(boolean b) {
		enCours = b;
	}
	
	public void nouvJoueur(int idClient) {
		nbJoueurs++;
		joueurs.add(idClient);
	}
	
	public void depJoueur(int idJoueur) {
		nbJoueurs--;
		joueurs.remove(idJoueur);
	}
	
	public void modJoueur(int idJoueur) {
		joueurs.set(idJoueur, idJoueur-1);
	}
}