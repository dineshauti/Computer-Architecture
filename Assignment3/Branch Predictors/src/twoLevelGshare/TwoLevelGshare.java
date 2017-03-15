package twoLevelGshare;

public class TwoLevelGshare {

	public static void main(String[] args) {
		
		Thread th = new Thread(new TwoLevelGshareThread());
		th.start();
	}

}
