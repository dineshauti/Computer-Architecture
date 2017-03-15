package tournament;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class tournamentThread implements Runnable {
	
	float missPrediction = 0;
	float percentageMissPrediction = 0;
	String state;
	int indexLocal, indexGshare;
	int pc, pcMeta;
	int[] metaPredictor = new int[4096];
	int choose = 1;
	
	
	//Pattern history table declaration
	int[] pht = new int[1024];
	
	//GHR declaration & initilaization
	int ghrGshare = 0;
	
	//GHR initilaization
	int[] ghrLocal = new int[128];
	
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
			
			// Meta-predictor initialization
			for(int i = 0; i < metaPredictor.length - 1; i++) {
				metaPredictor[i] = 0;
			}
			
			for(int i = 0; i < lines.size(); i++) {
				
				// Parse the file to get the hex and T/N
				String [] values = lines.get(i).split(" ");
				String hex = values[0];
				state = values[1];
				long val = Long.parseLong(hex);
				
				
				// Gshare Calculations				
				indexGshare = (int) ((ghrGshare ^ val) % 1024);
				
				// Local Calculation
				pc = (int) ((val >> 2) % 128); // Extract 10 bits from PC				
				indexLocal = ghrLocal[pc] % 1024; // 10 bit GHR value
				
				
				pcMeta = (int) ((val >> 2) % 4096);
				
				if(metaPredictor[pcMeta] <= 1) {
					if(!Gshare()) {
						metaPredictor[pcMeta]++;
						missPrediction++;		

					} else {
						metaPredictor[pcMeta]--;
					}
				} else if (metaPredictor[pcMeta] > 1) {
					if(!Local()) {
						metaPredictor[pcMeta]--;
						missPrediction++;
					} else {
						metaPredictor[pcMeta]++;
					}
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
	
public boolean Gshare() {
		
		boolean prediction = false;
		
		if((state.equalsIgnoreCase("T"))) {
			
			if(pht[indexGshare] <= 1) {
				//missPrediction++;
				prediction = false;
				pht[indexGshare]++;					
			} else if(pht[indexGshare] > 1) {
				if(pht[indexGshare] == 2) {
					pht[indexGshare]++;
				}
				prediction = true;
			}
			
			ghrGshare = ((ghrGshare << 1) % 1024) | 1; // push 1
			
		} else if ((state.equalsIgnoreCase("N"))) {
			
			if(pht[indexGshare] > 1) {
				//missPrediction++;
				prediction = false;
				pht[indexGshare]--;						
				
			} else if(pht[indexGshare] <= 1) {
				if(pht[indexGshare] == 1) {
					pht[indexGshare]--;
				}
				prediction = true;
			}
			
			ghrGshare = ((ghrGshare << 1) % 1024) | 0; // push 0
			
		}					
		
		return prediction;
	}

public boolean Local() {
	
	boolean prediction = false;
	
	if((state.equalsIgnoreCase("T"))) {
		
		if(pht[indexLocal] <= 1) {
			//missPrediction++;
			prediction = false;
			pht[indexLocal]++;					
		} else if(pht[indexLocal] > 1) {
			if(pht[indexLocal] == 2) {
				pht[indexLocal]++;
			}
			prediction = true;
		}
		
		ghrLocal[pc] = ((ghrLocal[pc] << 1) % 1024) | 1; // push 1
		
	} else if ((state.equalsIgnoreCase("N"))) {
		
		if(pht[indexLocal] > 1) {
			//missPrediction++;
			prediction = false;
			pht[indexLocal]--;						
			
		} else if(pht[indexLocal] <= 1) {
			if(pht[indexLocal] == 1) {
				pht[indexLocal]--;
			}
			prediction = true;
		}
		
		ghrLocal[pc] = ((ghrLocal[pc] << 1) % 1024) | 0; // push 0
		
	}	
	
	return prediction;
	
}
}
