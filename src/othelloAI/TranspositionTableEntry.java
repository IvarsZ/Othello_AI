package othelloAI;

public class TranspositionTableEntry {
	
	private int depth;
	private int score;
	private boolean isWhiteNextToMove;
	private int alpha;
	private int beta;
	
	protected TranspositionTableEntry(int score, boolean isWhiteNextToMove, int depth, int alpha, int beta) {
		this.depth = depth;
		this.score = score;
		this.isWhiteNextToMove = isWhiteNextToMove;
		this.alpha = alpha;
		this.beta = beta;
	}

	public int getDepth() {
		return depth;
	}

	public boolean isWhiteNextToMove() {
		return isWhiteNextToMove;
	}

	public int getScore() {
		return score;
	}
	
	public boolean isInBounds(int alpha, int beta) {
		return (this.alpha <= alpha && beta <= this.beta);
	}
}
