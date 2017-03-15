package tournament;

public class tournament {

	public static void main(String[] args) {
		
		Thread th = new Thread(new tournamentThread());
		th.start();


	}

}
