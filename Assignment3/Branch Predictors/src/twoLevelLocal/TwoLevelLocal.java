package twoLevelLocal;

public class TwoLevelLocal {

	public static void main(String[] args) {
		
		Thread th = new Thread(new TwoLevelLocalThread());
		th.start();

	}

}
