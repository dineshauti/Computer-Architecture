package twoLevelGlobal;

public class TwoLevelGlobal {

	public static void main(String[] args) {
		
		Thread th = new Thread(new TwoLevelGlobalThread());
		th.start();

	}

}
