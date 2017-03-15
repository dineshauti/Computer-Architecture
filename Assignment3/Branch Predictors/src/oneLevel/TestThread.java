package oneLevel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TestThread implements Runnable {

	double missPrediction = 0;
	double percentageMissPrediction = 0;
	static int[] pht = new int[1024];
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader("branch-trace-gcc.txt"));
			ArrayList<String> lines = new ArrayList<String>();

			while ((line = br.readLine()) != null) {
				lines.add(line);

			}
			
			//Pattern history table initialization			
			for(int i = 0; i < pht.length - 1; i++) {
				pht[i] = 0;
			}
			
			for (int i = 0; i < lines.size(); i++) {
				
				String []values = lines.get(i).split(" ");
				String hex = values[0];
				String state = values[1];
				
				long hexVal = Long.parseLong(hex);
				int pc = (int) ((hexVal >> 2) % 1024);
				if(!levelOne(pc,state))
				{
					missPrediction++;
				}
			}
			
			System.out.println(missPrediction);
			System.out.println(lines.size());
			percentageMissPrediction = ((double)missPrediction/(double)lines.size()) * 100;
			
			System.out.println(percentageMissPrediction + "% mis-prediction" );
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
private boolean levelOne(int pc, String state) {
		// TODO Auto-generated method stub
		boolean prediction = false;
		if(pht[pc] <= 1)
		{
			if(state.equalsIgnoreCase("T"))
			{
				pht[pc]++;
				prediction = false;
			}
			else if(state.equalsIgnoreCase("N"))
			{
				if(pht[pc]==1)
					pht[pc]--;
				prediction = true;
			}
		}
		else if(pht[pc] >= 2)
		{
			if(state.equalsIgnoreCase("N"))
			{
				pht[pc]--;
				prediction = false;
			}
			if(state.equalsIgnoreCase("T"))
			{
				if(pht[pc]==2)
					pht[pc]++;
				prediction = true;
			}
			
		}
		
		return prediction;
	}

}
