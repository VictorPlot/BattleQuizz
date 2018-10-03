import java.util.Random;

public class FormalismeQuestion {
	private static int NOMBRE_QUESTIONS = 10;

	private String listeQuestions[];
	private String listeBonnesReponses[];
	private String listeReponses[][];

	FormalismeQuestion(String listeQ[], String listeBR[], String listeR[][]  ){

		listeReponses=listeR;
		for (int i=0;i<NOMBRE_QUESTIONS;i++) {
			Random rand = new Random();
			int index = rand.nextInt(4);
			if(index!=0) {
				String temp = listeReponses[i][index];
				listeReponses[i][index] = listeR[i][0];
				listeReponses[i][0] = temp;
			}
			else {
				//ne rien faire
			}
		}
		listeQuestions = listeQ;
		listeBonnesReponses = listeBR;
	}

	FormalismeQuestion(){
		listeQuestions = null;
		listeBonnesReponses = null;
		listeReponses = null;
	}

	public void affichage() {
		for (int i =0; i < NOMBRE_QUESTIONS;i++) {
			System.out.println(listeQuestions[i]);
			System.out.println(listeBonnesReponses[i]);
			System.out.println(listeReponses[i][0]+ " ; " +listeReponses[i][1]+ " ; " +listeReponses[i][2]+ " ; " +listeReponses[i][3]);
			System.out.println(" ");		
		}

	}

	public String[] getListeQuestions() {
		return listeQuestions;
	}

	public void setListeQuestions(String[] listeQuestions) {
		this.listeQuestions = listeQuestions;
	}

	public String[] getListeBonnesReponses() {
		return listeBonnesReponses;
	}

	public void setListeBonnesReponses(String[] listeBonnesReponses) {
		this.listeBonnesReponses = listeBonnesReponses;
	}

	public String[][] getListeReponses() {
		return listeReponses;
	}

	public void setListeReponses(String[][] listeReponses) {
		this.listeReponses = listeReponses;
	}

}
