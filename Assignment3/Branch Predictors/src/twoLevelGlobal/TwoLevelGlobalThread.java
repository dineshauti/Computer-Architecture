package twoLevelGlobal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TwoLevelGlobalThread implements Runnable{
	
	float missPrediction = 0;
	float percentageMissPrediction = 0;
	
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
			
			
			//Pattern history table initialization
			int[] pht = new int[1024];
			
			for(int i = 0; i < pht.length - 1; i++) {
				pht[i] = 0;
			}
			
			//GHR initilaization
			int ghr = 0;
					
			
			for(int i = 0; i < lines.size(); i++) {
				
				
				ghr = ghr % 1024; // 10 bit GHR value
				String state = lines.get(i).substring(lines.get(i).length() - 1); // Extract the value of T/N
				
				if((state.equalsIgnoreCase("T"))) {
					
					if(pht[ghr] <= 1) {
						missPrediction++;
						pht[ghr]++;					
					} else if(pht[ghr] > 1) {
						if(pht[ghr] == 2) {
							pht[ghr]++;
						}
					}
					
					ghr = ((ghr << 1) % 1024) | 1; // push 1
					
				} else if ((state.equalsIgnoreCase("N"))) {
					
					if(pht[ghr] > 1) {
						missPrediction++;
						pht[ghr]--;						
						
					} else if(pht[ghr] <= 1) {
						if(pht[ghr] == 1) {
							pht[ghr]--;
						}
					}
					
					ghr = ((ghr << 1) % 1024) | 0; // push 0
					
				}
				
			}
			
			br.close();
			
			System.out.println(missPrediction);
			percentageMissPrediction = ((float)missPrediction/(float)lines.size()) * 100;			
			System.out.println((int)percentageMissPrediction + "% mis-prediction" );
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}

}
