
public enum Theme {
	GK("General Knowledge","9"),
	BOOKS("Entertainment: Books","10"),
	FILM("Entertainment: Film","11"),	
	MUSIC("Entertainment: Music","12"),
	THEATRE("Entertainment: Musicals & Theatres","13"),
	TELE("Entertainment: Television","14"),
	GAMES("Entertainment: Video Games","15"),
	BOARD("Entertainment: Board Games","16"),
	NATURE("Science & Nature","17"),
	COMPUTERS("Science: Computers","18"),
	MATH("Science: Mathematics","19"),
	MYTH("Mythology","20"),
	SPORTS("Sports","21"),
	GEO("Geography","22"),
	HIST("History","23"),
	POL("Politics","24"),
	ART("Art","25"),
	CELEBS("Celebrities","26"),
	ANIMALS("Animals","27"),
	VEHICLES("Vehicles","28"),
	COMICS("Entertainment: Comics","29"),
	GADGETS("Science: Gadgets","30"),
	ANIME("Entertainment: Japanese Anime & Manga","31"),
	CARTOON("Entertainment: Cartoon & Animations","32");
	
	private String sAff;
	private String sCode;
	
	Theme(String a,String c) {
		this.sAff=a;
		this.sCode=c;
	}
	
	public String aff() {
		return sAff;
	}
	
	public String code() {
		return sCode;
	}
}
