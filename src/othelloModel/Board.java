package othelloModel;

import java.util.ArrayList;

import othelloAI.BitBoard;
import othelloAI.OthelloAI;

import Interfaces.IBoard;

public class Board implements IBoard{

	// Determines the size of board.
	public static final int ROW_COUNT = 8;
	public static final int COLUMN_COUNT = 8;

	public static final boolean WHITE = true;
	public static final boolean BLACK = false;

	// 2D array of Othello cells that represents content it.
	private SquareContents[][] cells;

	// Stores scores for players.
	private int blackScore;
	private int whiteScore;

	private ArrayList<Move> whitePossibleMoves;
	private ArrayList<Move> blackPossibleMoves;

	private BitBoard bitBoard;
	
	private boolean isWhiteLastToMove;
	
	private Move lastMove;
	
	private OthelloAI othelloAI;
	
	private int stage;

	public Board() {
		
		isWhiteLastToMove = true;
		stage = 0;
		
		lastMove = new Move(3,3);

		// Creates the cells for the board and places 4 starting pieces.
		cells = new SquareContents[ROW_COUNT][COLUMN_COUNT];

		// Initialises all cells to be empty.
		for (int row = 0; row < ROW_COUNT; row ++) {
			for (int column = 0; column < COLUMN_COUNT; column ++) {
				cells[row][column] = SquareContents.EMPTY;
			}
		}

		// Places the starting pieces on the board.
		cells[3][3] = SquareContents.WHITE_PIECE;
		cells[3][4] = SquareContents.BLACK_PIECE;
		cells[4][4] = SquareContents.WHITE_PIECE;
		cells[4][3] = SquareContents.BLACK_PIECE;

		blackScore = 2;
		whiteScore = 2;

		whitePossibleMoves = new ArrayList<Move>();
		blackPossibleMoves = new ArrayList<Move>();

		bitBoard = new BitBoard(cells);
		
		// TODO: rename.
		
		long whiteMoves = bitBoard.findPossibleMoves(WHITE);
		whitePossibleMoves.clear();

		// Converting from long to board.
		String boardBits = Long.toBinaryString(whiteMoves);

		String zeroString = "";
		for (int i = 0; i < 64 - boardBits.length(); i++) {
			zeroString += "0";
		}
		boardBits = zeroString + boardBits;

		int stringIndex = 0;
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++) {
				if(boardBits.charAt(stringIndex) == '1') {
					whitePossibleMoves.add(new Move(i, j));
				}
				stringIndex++;
			}
		}

		/*
		 * 
		 * BLACK
		 * 
		 */
		long blackMoves = bitBoard.findPossibleMoves(BLACK);
		blackPossibleMoves.clear();

		// Converting from long to board.
		boardBits = Long.toBinaryString(blackMoves);

		zeroString = "";
		for (int i = 0; i < 64 - boardBits.length(); i++) {
			zeroString += "0";
		}
		boardBits = zeroString + boardBits;

		stringIndex = 0;
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++) {
				if(boardBits.charAt(stringIndex) == '1') {
					blackPossibleMoves.add(new Move(i, j));
				}
				stringIndex++;
			}
		}
		
		// Creates AI.
		othelloAI = new OthelloAI();
		
	}
	
	public OthelloAI getOthelloAI() {
		return othelloAI;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public int getHeight() {
		return cells.length;
	}

	public SquareContents getSquareContents(int row, int col) {
		return cells[row][col];
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public int getWidth() {
		return cells[0].length;
	}

	public boolean hasMove(SquareContents piece) {

		if (piece.isBlack()) {
			return !blackPossibleMoves.isEmpty();
		}
		else if (piece.isWhite()) {
			return !whitePossibleMoves.isEmpty();
		}

		System.out.println("Piece of empty was passed to hasMove method.");
		return false;
	}

	public boolean play(SquareContents piece, int row, int col) {

		// Check if move is possible.
		if (piece.isBlack()) {
			if (!blackPossibleMoves.contains(new Move(row, col))) {
				return false;
			}
		}
		else if (piece.isWhite()) {
			if (!whitePossibleMoves.contains(new Move(row, col))) {
				return false;
			}
		}
		
		// Do the move
		bitBoard.playMove(row, col, piece.isWhite());
		lastMove = new Move(row, col);
		
		// Goes to the next stage of the game.
		stage++;
		
		/*
		 * update BLACK
		 */
		
		// Converting from long to board.
		String blackBoardBits = Long.toBinaryString(bitBoard.getBlackPieces());

		String zeroString = "";
		for (int i = 0; i < 64 - blackBoardBits.length(); i++) {
			zeroString += "0";
		}
		blackBoardBits = zeroString + blackBoardBits;
		
		// Converting from long to board.
		String whiteBoardBits = Long.toBinaryString(bitBoard.getWhitePieces());

		zeroString = "";
		for (int i = 0; i < 64 - whiteBoardBits.length(); i++) {
			zeroString += "0";
		}
		whiteBoardBits = zeroString + whiteBoardBits;

		int stringIndex = 0;
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++) {
				if (blackBoardBits.charAt(stringIndex) == '1') {
					cells[i][j] = SquareContents.BLACK_PIECE;
				}
				else if (whiteBoardBits.charAt(stringIndex) == '1') {
					cells[i][j] = SquareContents.WHITE_PIECE;
				}
				else {
					cells[i][j] = SquareContents.EMPTY;
				}
				stringIndex++;
			}
		}

		// Update possible moves.
		long whiteMoves = bitBoard.findPossibleMoves(WHITE);
		whitePossibleMoves.clear();

		// Converting from long to board.
		String whiteMovesBits = Long.toBinaryString(whiteMoves);

		zeroString = "";
		for (int i = 0; i < 64 - whiteMovesBits.length(); i++) {
			zeroString += "0";
		}
		whiteMovesBits = zeroString + whiteMovesBits;

		stringIndex = 0;
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++) {
				if(whiteMovesBits.charAt(stringIndex) == '1') {
					whitePossibleMoves.add(new Move(i, j));
				}
				stringIndex++;
			}
		}

		/*
		 * 
		 * BLACK
		 * 
		 */
		long blackMoves = bitBoard.findPossibleMoves(BLACK);
		blackPossibleMoves.clear();

		// Converting from long to board.
		String blackMovesBits = Long.toBinaryString(blackMoves);

		zeroString = "";
		for (int i = 0; i < 64 - blackMovesBits.length(); i++) {
			zeroString += "0";
		}
		blackMovesBits = zeroString + blackMovesBits;

		stringIndex = 0;
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++) {
				if(blackMovesBits.charAt(stringIndex) == '1') {
					blackPossibleMoves.add(new Move(i, j));
				}
				stringIndex++;
			}
		}
		
		// TODO: what if both cannot make a move?
		if ((piece.isWhite() && !hasMove(SquareContents.BLACK_PIECE)) || (piece.isBlack() && !hasMove(SquareContents.WHITE_PIECE))) {
			isWhiteLastToMove = !isWhiteLastToMove;
		}

		return true;
	}

	public void reset() {

		// Creates the cells for the board and places 4 starting pieces.
		cells = new SquareContents[ROW_COUNT][COLUMN_COUNT];

		// Initialises all cells to be empty.
		for (int row = 0; row < ROW_COUNT; row ++) {
			for (int column = 0; column < COLUMN_COUNT; column ++) {
				cells[row][column] = SquareContents.EMPTY;
			}
		}

		cells[3][3] = SquareContents.WHITE_PIECE;
		cells[3][4] = SquareContents.BLACK_PIECE;
		cells[4][3] = SquareContents.WHITE_PIECE;
		cells[4][4] = SquareContents.BLACK_PIECE;

		blackScore = 2;
		whiteScore = 2;

		whitePossibleMoves = new ArrayList<Move>();
		blackPossibleMoves = new ArrayList<Move>();
	}

	public BitBoard getBitBoard() {
		return bitBoard;
	}
	
	public boolean isWhiteLastToMove() {
		return isWhiteLastToMove;
	}
	
	public Move getLastMove() {
		return lastMove;
	}

	public int getStage() {
		return stage;
	}
}
