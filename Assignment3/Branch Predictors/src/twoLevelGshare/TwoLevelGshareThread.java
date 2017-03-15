package twoLevelGshare;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TwoLevelGshareThread implements Runnable {
	
	float missPrediction = 0;
	float percentageMissPrediction = 0;
	String state;
	int index;
	
	//Pattern history table declaration
	int[] pht = new int[1024];
	
	//GHR declaration & initilaization
	int ghrGshare = 0;
	
	
	@Override
	public void run() {
		
		try {
			
			// File read
			String line;			
			BufferedReader br = new BufferedReader(new FileReader("branch-trace-gcc.txt"));
			ArrayList<String> lines = new ArrayList<String>();
			
			while(( line = br.readLine()) != null) {
			    lines.add(line);			    
			}
			
			
			// PHT initialization
			for(int i = 0; i < pht.length - 1; i++) {
				pht[i] = 0;
			}
						
			
			for(int i = 0; i < lines.size(); i++) {
				
				
				// Parse the file to get the hex and T/N
				String [] values = lines.get(i).split(" ");
				String hex = values[0];
				state = values[1];
				long val = Long.parseLong(hex);
				
				// Gshare Calculation
				//ghrGshare = ghrGshare % 1024; // 10 bit GHR value
				index = (int) ((ghrGshare ^ val) % 1024);
				
				if(!twoLevelGsharePredictor()) {
					missPrediction++;
				}
				
				
				
			}
			
			br.close();
			
			System.out.println(missPrediction);
			percentageMissPrediction = ((float)missPrediction/(float)lines.size()) * 100;			
			System.out.println(percentageMissPrediction + "% mis-prediction" );
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
	
	
	public boolean twoLevelGsharePredictor() {
		
		boolean prediction = false;
		
		if((state.equalsIgnoreCase("T"))) {
			
			if(pht[index] <= 1) {
				//missPrediction++;
				prediction = false;
				pht[index]++;					
			} else if(pht[index] > 1) {
				if(pht[index] == 2) {
					pht[index]++;
				}
				prediction = true;
			}
			
			ghrGshare = ((ghrGshare << 1) % 1024) | 1; // push 1
			
		} else if ((state.equalsIgnoreCase("N"))) {
			
			if(pht[index] > 1) {
				//missPrediction++;
				prediction = false;
				pht[index]--;						
				
			} else if(pht[index] <= 1) {
				if(pht[index] == 1) {
					pht[index]--;
				}
				prediction = true;
			}
			
			ghrGshare = ((ghrGshare << 1) % 1024) | 0; // push 0
			
		}
		return prediction;
	}

}
