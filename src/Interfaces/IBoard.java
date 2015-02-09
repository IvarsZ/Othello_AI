package Interfaces;

import othelloModel.SquareContents;

public interface IBoard {
	
	/**
	 * Accessor method for height
	 */
	public abstract int getHeight();
	
	/**
	 * Accessor method for width
	 */
	public abstract int getWidth();
	
	/**
	 * Reset the board to its initial state
	 */
	public abstract void reset();
	
	/**
	 * Beautifully rendered ASCii-art version of the board state
	 */
	public abstract String toString();
	
	/**
	 * Accessor method for white's score
	 */
	public abstract int getWhiteScore();
	
	/**
	 * Accessor méthod for black's score
	 */
	public abstract int getBlackScore();
	
	/**
	 * Accessor method for contents of a particular square
	 */
	public abstract SquareContents getSquareContents(int row, int col);
	
	/**
	 * Play a piece of the given colour at the given coordinates. Returns
	 * true if successful
	 */
	public abstract boolean play(SquareContents piece, int row, int col);
	
	/**
	 * Returns true if the player of the specified piece colour has a move.
	 */
	public abstract boolean hasMove(SquareContents piece);
	
}