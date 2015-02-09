package othelloAI;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable {
	
	protected static final int NO_SCORE = -1000000000;
	
	private static final int MAX_SIZE = 100000;
	
	private LinkedHashMap<BitBoard, TranspositionTableEntry> table;
	
	protected TranspositionTable() {
		
		new LinkedHashMap<BitBoard, TranspositionTableEntry>();

		
		table = new LinkedHashMap<BitBoard, TranspositionTableEntry>(16, 0.7f, true) {
		      protected boolean removeEldestEntry(Map.Entry<BitBoard, TranspositionTableEntry> eldest)
		      {
		        return size() > MAX_SIZE;
		      }
		  };

	}
	
	protected int checkForScore(BitBoard bitBoard, int requiredDepth, boolean isWhiteNextToMove, int alpha, int beta) {

		TranspositionTableEntry entry = table.get(bitBoard);
		
		if (entry != null) {
			
			if (entry.getDepth() >= requiredDepth) {
				
				if (isWhiteNextToMove == entry.isWhiteNextToMove() && entry.isInBounds(alpha, beta)) {
					return entry.getScore();
				}
			}
		}

		return NO_SCORE;
	}
	
	protected void add(BitBoard bitBoard, int score, int depth, boolean isWhiteNextToMove, int alpha, int beta) {
		
		table.put(bitBoard, new TranspositionTableEntry(score, isWhiteNextToMove, depth, alpha, beta));
	}
	
	protected void refresh(int numberOfPieces) {
		
		long time = System.nanoTime();
		System.out.println("tt size " + table.size());
		
		Iterator<Map.Entry<BitBoard, TranspositionTableEntry>> it = table.entrySet().iterator();

		int a;
		BitBoard bitBoard;
		while (it.hasNext()) {
		  Map.Entry<BitBoard, TranspositionTableEntry> entry = it.next();

		  bitBoard = entry.getKey();
		  a = bitBoard.numberOfMovesMade();
		  if (bitBoard.numberOfMovesMade() <= numberOfPieces) {
		    it.remove();
		  }
		}
		
		System.out.println("tt size " + table.size());
		System.out.println((System.nanoTime() - time) / 1000000.0);
	}
	
	
}
