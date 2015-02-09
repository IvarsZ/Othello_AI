package othelloAI;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import othelloModel.Board;
import othelloModel.Move;

/**
 * Class managing the AI.
 */
public class OthelloAI {

	protected static final int END_OF_GAME_STAGE = -1;

	private static final int START_DEPTH = 6;
	private static final int START_ALPHA = Integer.MIN_VALUE + 1;
	private static final int START_BETA = Integer.MAX_VALUE;

	private static final double DEPTH_GAIN_FROM_NEGASCOUT = 3;

	private static final double TIME_LIMIT = 10.0;
	private static final double NANO_FACTOR = 1000000000.0;

	private static final String MOVE_SEPARATOR = " ";

	private ArrayList<ArrayList<Integer>> killerMoveTable;

	// Fields used for time-management.
	private double branchingFactor;
	private int totalNumberOfNodesSearched;
	private int totalNumberOfChildren;
	private double timeSpent;
	private double timeSpentPerNode = 0;
	private boolean interruptNegascout;
	private int depthToUse;
	private int numberOfChildren;

	//private TranspositionTable transpositionTable;
	private killerTableHistory tableHistory;

	/**
	 * Initializes the AI. 
	 */
	public OthelloAI() {
		
		// Table history for ordering.
		try {
		FileInputStream fileObjectReader = new FileInputStream(new File("historyTables.txt"));
		ObjectInputStream objectInput = new ObjectInputStream(fileObjectReader);
		
		tableHistory = (killerTableHistory)  objectInput.readObject();
		
		objectInput.close();
		} catch(EOFException eofe) {
			System.out.println("empty");
			tableHistory = new killerTableHistory();
		} catch(IOException ioe) {
			System.out.println("Exception while opening historyTabels.txt");
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception while reading from historyTabels.txt");
			e.printStackTrace();
		}

		// Initialize killerMoveTable.
		killerMoveTable = new ArrayList<ArrayList<Integer>>();

		// Reads the file for the killerTable.
		BufferedReader input = null;
		try {
			FileReader fileReader = new FileReader(new File("killerTable.txt"));
			input = new BufferedReader(fileReader);
		}
		catch (IOException ioe) {
			System.out.println("Unable to open the killerTable.txt file");
			ioe.printStackTrace();
		}

		String lineRead = "";

		// Read the file only if the BufferedReader was successfully created.
		if (input != null) {
			try {
				while ((lineRead = input.readLine()) != null) {

					ArrayList<Integer> listOfMoves = new ArrayList<Integer>();
					String[] stringMoves;

					// Splits the string about the comma separator.
					Pattern pattern = Pattern.compile(MOVE_SEPARATOR);
					stringMoves = pattern.split(lineRead);

					// Add the moves to the ArrayList of moves.
					for (int i = 0; i < stringMoves.length; i++) {
						listOfMoves.add(Integer.parseInt(stringMoves[i]));
					}

					killerMoveTable.add(listOfMoves);
				}
			}
			catch (IOException e) {
				System.out.println("IOException while reading the file killerTable.txt");
				e.printStackTrace();
			}
		}

		//transpositionTable = new TranspositionTable();
	}

	/**
	 * Searched for the best move and plays it.
	 * @param board
	 * @param isWhiteToMove
	 * @return
	 */
	public Move nextAIMove(Board board, boolean isWhiteToMove) {
		
		timeSpent = System.nanoTime();

		interruptNegascout = false;

		// This is not the first search
		if (timeSpentPerNode != 0) {

			// Calculates branching factor and the depth.
			branchingFactor = totalNumberOfChildren / (totalNumberOfNodesSearched * 1.0);
			depthToUse = (int) (Math.log10(TIME_LIMIT / timeSpentPerNode) / Math.log10(branchingFactor) + DEPTH_GAIN_FROM_NEGASCOUT);
		}

		// If this is the first search, then just use starting depth.
		else {
			depthToUse = START_DEPTH;
		}

		System.out.println("depth is " + depthToUse);


		// Creates the root node.
		Node root = new Node(board.getBitBoard(), isWhiteToMove, board.isWhiteLastToMove());

		// Resets the numbers for nodes,
		totalNumberOfNodesSearched = 1;
		totalNumberOfChildren = 0;

		// Variables used in the next loop.
		Node child;
		int lastMove = board.getLastMove().row * 8 + board.getLastMove().col;
		killerMoveTable.remove((Integer) lastMove);
		int bestMove = 0;
		int bestMoveValue = Integer.MIN_VALUE;
		int score;
		
		// Loops over the possible moves in order of the killer table.
		for (Integer possibleMove : killerMoveTable.get(lastMove)) {
			if (root.isMovePossible(possibleMove)) {

				// Makes a child node with that move made and runs negascout on it.
				child = new Node(root, possibleMove);
				totalNumberOfChildren++;

				score = -negascout(child, depthToUse, START_ALPHA, START_BETA, possibleMove, board.getStage());

				// Chooses the move with the best value.
				if (score > bestMoveValue) {
					bestMoveValue = score;
					bestMove = possibleMove;
				}

				// Interrupts the search if it's over time.
				if (interruptNegascout) {
					System.out.println("break");
					break;
				}
				
			}
		}

		timeSpent = System.nanoTime() - timeSpent;
		timeSpentPerNode = (timeSpent / totalNumberOfNodesSearched) / NANO_FACTOR;
		
		System.out.println((board.getStage() + depthToUse + 1 + 3) / 8 + " " + (board.getStage() + 1) + " move " + bestMove);
		
		// delete entries from killer table.
		killerMoveTable.remove((Integer) bestMove);

		tableHistory.incrementEntry(board.getLastMove().col + (board.getLastMove().row * 8), bestMove);
		return new Move(bestMove / Board.COLUMN_COUNT, bestMove % Board.COLUMN_COUNT);
	}

	/**
	 * The negascout Searches for the best move.
	 */
	private int negascout(Node parent, int depth, int alpha, int beta, int lastMove, int stage) {

		// If the game is over or depth has been reached, returns the heuristics value.		
		if (parent.isGameOver()) {
			return parent.evaluatePosition(END_OF_GAME_STAGE);
		}
		if (depth <= 0) {
			return parent.evaluatePosition(stage);
		}

		
		if ((System.nanoTime() - timeSpent) > (TIME_LIMIT * NANO_FACTOR)) {
			interruptNegascout = true;
			return parent.evaluatePosition(stage);
		}
		

		totalNumberOfNodesSearched++;
		numberOfChildren =  parent.getNumberOfChildren();
		totalNumberOfChildren += numberOfChildren;

		Node child;
		int score;
		int b = beta; // lower bound of the search, initial window is (-beta, -alpha).
		boolean isFirstChild = true;

		if (numberOfChildren > 0) {
		
			// Loops over the possible moves in order of the killer table.
			for (Integer possibleMove : killerMoveTable.get(lastMove)) {
				if (parent.isMovePossible(possibleMove)) {

					// Makes a child node with that move made.
					child = new Node(parent, possibleMove);

					/*
					 * Uses transposition tables.
					if (depth <= 2) {
						score = transpositionTable.checkForScore(child.getBitBoard(), depth - 1, child.isWhiteNextToMove(), -b, -alpha);
						if (score == TranspositionTable.NO_SCORE) {
							score = -negascout(child, depth - 1, -b, -alpha, possibleMove, stage + 1);
							transpositionTable.add(child.getBitBoard(), score, depth - 1, child.isWhiteNextToMove(), -b, -alpha);
						}
					}
					else {
						score = -negascout(child, depth - 1, -b, -alpha, possibleMove, stage + 1);
					}
					*/
					
					score = -negascout(child, depth - 1, -b, -alpha, possibleMove, stage + 1);
					
					// Check if null-window failed high
					if (alpha < score && score < beta && !isFirstChild) {

						// Recreates the child node with that move made.
						child = new Node(parent, possibleMove);

						// Full re-search
						score = -negascout(child, depth - 1, -beta, -alpha, possibleMove, stage + 1);
					}

					alpha = Math.max(alpha, score);
					isFirstChild = false;

					// Beta cut-off.
					if (alpha >= beta) {
						return alpha;
					}

					// Sets a new null window.
					b = alpha + 1;
				}
			}
			
			return alpha;
		}
		else {
			parent.negateIsWhiteNextToMove();
			parent.negateIsWhiteLastToMove();
			return -negascout(parent, depth, -beta, -alpha, lastMove, stage);
		}

		/*
		 * Negascout pseudocode.
		 b := β
		    foreach child of node
		        a := -negascout (child, depth - 1, -b, -α)
		        if α < a < β and child is not first child
		            a := -negascout(child, depth - 1, -β, -α)
		        α := max(α, a)
		        if α ≥ β
		            return α
		        b := α + 1          
		    return α;
		 */
	}
	
	/**
	 * Saves the response table into a file.
	 */
	public void saveHistoryTable() {
		// Table history for ordering.
		try {
		FileOutputStream fileObjectWriter = new FileOutputStream(new File("historyTables.txt"));
		ObjectOutputStream objectOutput = new ObjectOutputStream(fileObjectWriter);
		
		objectOutput.writeObject(tableHistory);
		} catch(IOException ioe) {
			System.out.println("unable to save tableHistory");
		}
		
		tableHistory.print();
	}
}
