import java.util.ArrayList;

public class Partie {
	private Theme theme;
	private int nbJoueurs;
	private boolean enCours;
	ArrayList<Integer> joueurs;
	public Object lock;
	
	Partie(Theme t) {
		theme = t;
		joueurs = new ArrayList<Integer>();
		lock = new Object();
	}

	public Theme getTheme() {
		return theme;
	}

	public int getNbJoueurs() {
		return nbJoueurs;
	}

	public boolean isEnCours() {
		return enCours;
	}
	
	public void nouvJoueur(int idClient) {
		nbJoueurs++;
		joueurs.add(idClient);
	}
	
	public void depJoueur(int idClient) {
		nbJoueurs--;
		joueurs.remove(idClient);
	}
}