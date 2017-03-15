package oneLevel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OneLevelBranchThread implements Runnable {
	
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		float missPrediction = 0;
		float percentageMissPrediction = 0;
			
		try {
			
			//File read	
			String line;			
			BufferedReader br = new BufferedReader(new FileReader("branch-trace-gcc.txt"));
			ArrayList<String> lines = new ArrayList<String>();
			
			while(( line = br.readLine()) != null) {
			    lines.add(line);
			    
			}
			
			//Pattern history table initialization
			int[] pht = new int[1024];
			
			for(int i = 0; i < pht.length - 1; i++) {
				pht[i] = 0;
			}
			
			/*
			 * The below for loop goes through the file row by row and implements the 
			 * predictor logic
			 */
			for(int i = 0; i < lines.size(); i++) {
				
				// Parse the file to get the hex and T/N
				String [] values = lines.get(i).split(" ");
				String hex = values[0];
				String state = values[1];
				long val = Long.parseLong(hex);
			    
				int index = (int) ((val >> 2) % 1024); // Extract 10 bits from PC

			    
			    /*
			     * Missprediction Logic			     * 
			     * */				
				
				if(pht[index] <= 1)
				{
					if(state.equalsIgnoreCase("T"))
					{
						pht[index]++;
						missPrediction++;
					}
					else if(state.equalsIgnoreCase("N"))
					{
						if(pht[index]==1)
							pht[index]--;
						
					}
				}
				else if(pht[index] >= 2)
				{
					if(state.equalsIgnoreCase("N"))
					{
						pht[index]--;
						missPrediction++;
					}
					if(state.equalsIgnoreCase("T"))
					{
						if(pht[index]==2)
							pht[index]++;
						
					}
					
				}
				

			} // for loop ends

			    
			
			br.close();
			
			System.out.println(missPrediction);
			System.out.println(lines.size());
			percentageMissPrediction = ((float)missPrediction/(float)lines.size()) * 100;			
			System.out.println(percentageMissPrediction + "% mis-prediction" );
			
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
			
		
	}

}
