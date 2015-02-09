package Interfaces;

import java.util.Observer;

import othelloModel.SquareContents;
import remoteInterface.GameObserver;

public interface IGame {
	
	/**
	 * Accessor for game over
	 */
	public abstract boolean isGameOver();
	
	/**
	 * Accessor for whiteToPlay
	 */
	public abstract boolean isWhiteToPlay();
	
	/**
	 * Play the colour whose turn it is at specified square. Return
	 * true if successful. Updates gameOver if necessary.
	 * Notifies observers
	 */
	public abstract boolean play(int row, int col);
	
	/**
	 * Play the colour whose turn it is at specified square. Return
	 * true if successful. Updates gameOver if necessary.
	 */
	public abstract boolean remote_play(int row, int col);
	
	/**
	 * Return the height of the board
	 */
	public abstract int getBoardHeight();
	
	/**
	 * Return the width of the board
	 */
	public abstract int getBoardWidth();

	/**
	 * Return the contents of a square
	 */
	public abstract SquareContents getSquareContents(int row, int col);

	public abstract void addObserver(Observer observer);
	
}