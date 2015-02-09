package othelloAI;

import java.io.Serializable;

public class killerTableHistory implements Serializable {

	int[][] moves;
	public killerTableHistory() {
		moves = new int[64][64];
	}
	
	protected void incrementEntry(int move, int responseMove) {
		moves[move][responseMove]++;
	}
	
	protected void print() {
		for(int move = 0; move < moves.length; move++) {
			for(int responseMove = 0; responseMove < moves[0].length; responseMove++) {
				System.out.print(moves[move][responseMove] + ", ");
			}
			
			System.out.println("");
		}
	}
}
