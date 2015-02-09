package othelloAI;

import othelloModel.SquareContents;

/**
 * This class represent the bitBoard.
 *
 */
public class BitBoard implements Cloneable{
	
	private long whitePieces;
	private long blackPieces;

	protected static final int DIRECTIONS = 8;
	
	/*
	 * Bit masks used for shifting in the 8 direcitons.
	 */
	private static final long REMOVE_FIRST_COLUMN_BITMASK = 9187201950435737471L;
	private static final long REMOVE_LAST_COLUMN_BITMASK = -72340172838076674L; 
	private static final long REMOVE_FIRST_ROW_BITMASK = 72057594037927935L;
	private static final long REMOVE_LAST_ROW_BITMASK = -256L;
	private static final long REMOVE_LAST_ROW_AND_FIRST_COLUMN_BITMASK = 9187201950435737344L;
	private static final long REMOVE_LAST_ROW_AND_LAST_COLUMN_BITMASK = -72340172838076928L;
	private static final long REMOVE_FIRST_ROW_AND_LAST_COLUMN_BITMASK = 71775015237779198L; 
	private static final long REMOVE_FIRST_ROW_AND_FIRST_COLUMN_BITMASK = 35887507618889599L;
	
	/**
	 * This constructor initializes the bitBoard.
	 * @param cells
	 */
	public BitBoard(SquareContents[][] cells) {

		whitePieces = 0;
		blackPieces = 0;

		long power2 = 1;
		for (int i = cells.length - 1; i >= 0; i--) {
			for (int j = cells[0].length - 1; j >= 0; j--) {

				if (cells[i][j].isBlack()) {
					blackPieces += power2;
				}
				else if (cells[i][j].isWhite()) {
					whitePieces += power2;
				}

				power2 *= 2;
			}
		}
	}
	
	/**
	 * This constructor creates a bitBoard given the black and white pieces bitBoards.
	 * @param whitePieces
	 * @param blackPieces
	 */
	public BitBoard(long whitePieces, long blackPieces) {
		this.whitePieces = whitePieces;
		this.blackPieces = blackPieces;
	}

	/**
	 * Finds the possible moves for one of the player.
	 * @param isWhiteMoves
	 * @return
	 */
	public long findPossibleMoves(boolean isWhiteMoves) {

		// Sets the colours right for players and opponents pieces.
		long playersPieces;
		long opponentsPieces;
		if (isWhiteMoves) {
			playersPieces = whitePieces;
			opponentsPieces = blackPieces;
		}
		else {
			playersPieces = blackPieces;
			opponentsPieces = whitePieces;
		}

		// Bitboards representing the empty cells and possible moves.
		long emptyCells = ~(playersPieces | opponentsPieces);
		long possibleMoves = 0L;
		
		// Loops over all 8 directions and shifts players pieces in that direction to check for possible moves.
		long shiftedPieces;
		for (int i = 0; i < DIRECTIONS; i++) {

			// Shifts the players pieces in a specific direction.
			shiftedPieces = shiftPieces(playersPieces, i);

			// Removes players pieces that didn't overlap with opponents pieces after the shift.
			shiftedPieces = shiftedPieces & opponentsPieces;

			// Check if there are actually opponent's adjacent pieces.
			while(shiftedPieces != 0) {

				// Keeps shifting the players pieces in the specific direction.
				shiftedPieces = shiftPieces(shiftedPieces, i);

				// If after shifting and overlapping with opponent's a player's piece goes on empty cell, it's possible move, so adds it.
				possibleMoves = possibleMoves | (shiftedPieces & emptyCells);

				// Removes players pieces that didn't overlap with opponents pieces after the shift.
				shiftedPieces = shiftedPieces & opponentsPieces;
			}
		}

		return possibleMoves;
	}
	
	/**
	 * Plays the move in the bitBoard and flips the pieces that are captured.
	 * @param row
	 * @param column
	 * @param isWhiteMoves
	 */
	public void playMove(int row, int column, boolean isWhiteMoves) {
		
		// Creates a long representing this move.
		long move = 1L << ((7 - row) * 8 + (7 - column));
		
		// Sets the colours right for players and opponents pieces.
		long playersPieces;
		long opponentsPieces;
		if (isWhiteMoves) {
			playersPieces = whitePieces;
			opponentsPieces = blackPieces;
		}
		else {
			playersPieces = blackPieces;
			opponentsPieces = whitePieces;
		}
		
		// Loops over all 8 directions.
		long shiftedMove;
		long piecesToChange = 0;
		long piecesToPossiblyChange = 0;
		for (int i = 0; i < DIRECTIONS; i++) {
			
			// Shifts the move piece in a specific direction.
			shiftedMove = shiftPieces(move, i);
			
			// Checks if the move piece overlaps with some opponents piece after shifting.
			shiftedMove = shiftedMove & opponentsPieces;
			
			// If overlapped adds to piecesToPossiblyChange.
			piecesToPossiblyChange = shiftedMove;
			
			while (shiftedMove != 0) {
				
				// Shifts the move piece in a specific direction.
				shiftedMove = shiftPieces(shiftedMove, i);
				
				// Checks if the move piece overlaps with players piece after shifting.
				if ((shiftedMove & playersPieces) != 0) {
					
					// If so adds piecesToPossiblyChange to piecesToChange.
					piecesToChange = piecesToChange | piecesToPossiblyChange;
				}
				
				// Checks if the move piece overlaps with some opponents piece after shifting.
				shiftedMove = shiftedMove & opponentsPieces;
				
				// If overlapped adds to piecesToPossiblyChange.
				piecesToPossiblyChange = piecesToPossiblyChange | shiftedMove;
			}
		}
		
		playersPieces = playersPieces | piecesToChange;
		opponentsPieces = opponentsPieces - piecesToChange;
		
		// Places the piece of the move.
		playersPieces = playersPieces | move;
		
		// Applies the changed pieces.
		if (isWhiteMoves) {
			whitePieces = playersPieces;
			blackPieces = opponentsPieces;
		}
		else {
			blackPieces = playersPieces;
			whitePieces = opponentsPieces;
		}
		
	}

	/**
	 * Prints the board from long to binary.
	 * @param board
	 * @param type
	 */
	public static void printBoard(long board, String type) {

		System.out.println(type);

		String boardBits = Long.toBinaryString(board);

		String zeroString = "";
		for (int i = 0; i < 64 - boardBits.length(); i++) {
			zeroString += "0";
		}
		boardBits = zeroString + boardBits;
		for (int i = 0; i < 8; i++) {
			System.out.println(boardBits.substring(i * 8, i * 8 + 8));
		}
	}
	
	

	/**
	 * Gets the white pieces bitBoard.
	 * @return
	 */
	public long getWhitePieces() {
		return whitePieces;
	}

	/**
	 * Gets the black pieces bitBoard.
	 * @return
	 */
	public long getBlackPieces() {
		return blackPieces;
	}
	
	/**
	 * Gets the number of moves made in the game.
	 * @return
	 */
	protected int numberOfMovesMade() {
		return Long.bitCount(whitePieces) + Long.bitCount(blackPieces);
	}
	
	/**
	 * Shifts all the pieces in the passed bitBoard in the specified direction.
	 * @param direction
	 * 0 is East.
	 * 1 is NE
	 * 2 is N
	 * 3 is NW
	 * 4 is W
	 * 5 is SW
	 * 6 is S
	 * 7 is SE
	 * @return long representing shifted Bitboard.
	 */	
	protected static long shiftPieces(long bitBoard, int direction) {
		switch (direction) {
		case 0:
			return (bitBoard >>> 1) & REMOVE_FIRST_COLUMN_BITMASK;
		case 1:
			return (bitBoard << 7) & REMOVE_LAST_ROW_AND_FIRST_COLUMN_BITMASK;
		case 2:
			return (bitBoard << 8) & REMOVE_LAST_ROW_BITMASK;
		case 3:
			return (bitBoard << 9) & REMOVE_LAST_ROW_AND_LAST_COLUMN_BITMASK;
		case 4:
			return (bitBoard << 1) & REMOVE_LAST_COLUMN_BITMASK;
		case 5:
			return (bitBoard >>> 7) & REMOVE_FIRST_ROW_AND_LAST_COLUMN_BITMASK;
		case 6:
			return (bitBoard >>> 8) & REMOVE_FIRST_ROW_BITMASK;
		case 7:
			return (bitBoard >>> 9) & REMOVE_FIRST_ROW_AND_FIRST_COLUMN_BITMASK;
		default:
			System.out.println("Wrong direction passed to the shiftPieces method.");
			break;
		}
		
		return bitBoard;
	}
	
	@Override
	public BitBoard clone() {
		return new BitBoard(whitePieces, blackPieces);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitBoard other = (BitBoard) obj;
		if (blackPieces != other.blackPieces)
			return false;
		if (whitePieces != other.whitePieces)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blackPieces ^ (blackPieces >>> 32));
		result = prime * result + (int) (whitePieces ^ (whitePieces >>> 32));
		return result;
	}
	
}
