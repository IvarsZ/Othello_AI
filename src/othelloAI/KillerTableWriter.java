package othelloAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class KillerTableWriter {

	public static void main(String[] arg) {
		
		// Initialize killerMoveTable.
		int[][] killerMoveTable = new int[64][63];
		int[][] historyTable = new int[64][64];

		// Reads the file for the killerTable.
		Scanner scanner = null;
		try {
			FileReader fileReader = new FileReader(new File("killerTableHistoryBackUp.txt"));
			BufferedReader input = new BufferedReader(fileReader);
			scanner = new Scanner(input);
		}
		catch (IOException ioe) {
			System.out.println("Unable to open the killerTableHistoryBackUp.txt file");
			ioe.printStackTrace();
		}
		
		int max = -1;
		int indexOfMax = -1;
		if (scanner != null) {
			
			for (int  i = 0; i < 64; i++) {
				for (int j = 0; j < 64; j++) {
					historyTable[i][j] = scanner.nextInt();
				}
			}
			
			try {
				FileWriter fileWriter = new FileWriter("killerTableBackUp.txt");
				
				for (int  i = 0; i < 64; i++) {
					for (int j = 0; j < 63; j++) {
						
						max = -1;
						indexOfMax = -1;
						for (int k = 0; k < 64; k++) {
							if (historyTable[i][k] > max) {
								max = historyTable[i][k];
								indexOfMax = k;
							}
						}
						
						killerMoveTable[i][j] = indexOfMax;
						historyTable[i][indexOfMax] = -1;
						fileWriter.write(Integer.toString(indexOfMax) + " ");
					}
					fileWriter.write("\n");
				}
				
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException e) {
				System.out.println("Unable to open the killerTableBackUp.txt file");
				e.printStackTrace();
			}
			
		}
	}
	
}
