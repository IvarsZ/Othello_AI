package othelloModel;

/**
 * Value class used to encode updates to board
 * @author Alan Dearle
 */

public class Move {
	
	public int row;
	public int col;

	public Move(int row, int col) {
		this.row = row;
		this.col = col;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move) {
			return ((Move) obj).col == col && ((Move) obj).row == row;
		}
		
		return false;
	}
	
	
}
