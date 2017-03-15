package oneLevel;

public class OneLevelBranch {
	
	 
	
	public static void main(String[] args) {
		
		Thread th = new Thread(new OneLevelBranchThread());
		th.start();
		
		
	}
	
	
}
