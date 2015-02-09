package othelloAI;

import othelloModel.Board;

/**
 * This class represents the node in the negascout tree.
 * @author simone
 *
 */
public class Node {

	private static final int WHITE_WINS = 10000000;
	private static final int DRAW = -10;
	private static final int BLACK_WINS = -10000000;

	/*
	 * BitMasks. 
	 */
	private static final long CORNERS_BITMASK = -9151314442816847743L;
	private static final long C_SQUARE_BITMASK = 4792111478498951490L;
	private static final long X_SQUARE_BITMASK = 18577348462920192L;
	private static final long A_SQUARE = 2594215222373842980L;
	private static final long B_SQUARE = 1729382813125312536L;

	private static final long UP_LEFT_CORNER_BITMASK = -9223372036854775808L;
	private static final long UP_RIGHT_CORNER_BITMASK = 72057594037927936L;
	private static final long DOWN_LEFT_CORNER_BITMASK = 128L;
	private static final long DOWN_RIGHT_CORNER_BITMASK = 1L;

	// BitsMasks used to extract pieces at each position.
	private static final long[] BIT_EXTRACTOR = {

		-9223372036854775808L, 4611686018427387904L, 2305843009213693952L, 1152921504606846976L, 576460752303423488L, 288230376151711744L,
		144115188075855872L, 72057594037927936L, 36028797018963968L, 18014398509481984L, 9007199254740992L, 4503599627370496L, 2251799813685248L,
		1125899906842624L, 562949953421312L, 281474976710656L, 140737488355328L, 70368744177664L, 35184372088832L, 17592186044416L,
		8796093022208L, 4398046511104L, 2199023255552L, 1099511627776L, 549755813888L, 274877906944L, 137438953472L, 68719476736L, 34359738368L,
		17179869184L, 8589934592L, 4294967296L, 2147483648L, 1073741824, 536870912, 268435456, 134217728, 67108864, 33554432, 16777216, 8388608,
		4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1 };

	/*
	 * Heuristics.
	 */
	private static final int[] CORNER_HEURISTICS = { 70, 70, 100, 100, 100, 100, 100, 100, 0};
	private static final int[] C_SQUARE_HEURISTICS = { -1, -1, -1, -1, -1, -1, -1, -1, 0};
	private static final int[] X_SQUARE_HEURISTICS = { -16, -16, -16, -16, -16, -16, -16, -16, 0};
	private static final int[] A_SQUARE_HEURISTICS = { 0, 0, 6, 6, 8, 8, 10, 10, 0};
	private static final int[] B_SQUARE_HEURISTICS = { 0, 0, 6, 6, 8, 8, 10, 10, 0};

	private static final int[] MOBILITY_HEURISTICS = { 14, 14, 12, 12, 10, 10, 8, 8, 0};
	private static final int[] FRONTIER_HEURISTICS = { 10, 10, 8, 8, 6, 6, 4, 4, 0};
	private static final int[] PIECES_DIFFERENTIAL_HEURISTICS = { 0, 0, 0, 0, 0, 0, 6, 8, 8};

	private static final int[] STABILITY_HEURISTICS = {2, 2, 2, 2, 2, 2, 3, 3, 0};
	private static final int[] PARITY_HEURISTICS = {10, 10, 10, 10, 10, 10, 7, 5, 0};

	/*
	 * BitBoards.
	 */
	private BitBoard bitBoard;

	private long whitePossibleMoves;
	private long blackPossibleMoves;

	private boolean isWhiteLastToMove;
	private boolean isWhiteNextToMove;

	/**
	 * Constructor for the node.
	 */
	protected Node(BitBoard bitBoard, boolean isWhiteNextToMove, boolean isWhiteLastToMove) {

		this.bitBoard = bitBoard.clone();
		this.isWhiteLastToMove = isWhiteLastToMove;
		this.isWhiteNextToMove = isWhiteNextToMove;

		// Finds the possible moves for both players.
		findPossibleMoves();
	}

	/**
	 * Constructor for the node given a parent.
	 * @param parent
	 * @param moveToMake
	 */
	protected Node(Node parent, int moveToMake) {

		// Copies the board from parent node and makes the move.
		this.bitBoard = parent.bitBoard.clone();
		bitBoard.playMove(moveToMake / Board.COLUMN_COUNT, moveToMake % Board.COLUMN_COUNT, parent.isWhiteNextToMove);

		isWhiteNextToMove = !parent.isWhiteNextToMove;
		isWhiteLastToMove = parent.isWhiteLastToMove;

		findPossibleMoves();
	}

	/**
	 * Finds possible moves for both players.
	 */
	private void findPossibleMoves() {

		// Finds the possible moves for both players.
		whitePossibleMoves = bitBoard.findPossibleMoves(Board.WHITE);
		blackPossibleMoves = bitBoard.findPossibleMoves(Board.BLACK);
	}

	/**
	 * @return true if the specified move of the player who has to play is valid.
	 */
	protected boolean isMovePossible(int move) {

		if (isWhiteNextToMove) {
			return (whitePossibleMoves & BIT_EXTRACTOR[move]) != 0;
		}
		else {
			return (blackPossibleMoves & BIT_EXTRACTOR[move]) != 0;
		}
	}

	protected boolean isGameOver() {
		return whitePossibleMoves == 0 && blackPossibleMoves == 0;
	}

	protected BitBoard getBitBoard() {
		return bitBoard;
	}

	protected boolean isWhiteNextToMove() {
		return isWhiteNextToMove;
	}

	protected void negateIsWhiteNextToMove() {
		isWhiteNextToMove = !isWhiteNextToMove;
	}

	protected void negateIsWhiteLastToMove() {
		isWhiteLastToMove = !isWhiteLastToMove;
	}

	/**
	 * Evaluates a certain position on the bitBoard.
	 * @param stage - adjusts evaluation depending on number of moves made.
	 * @return returns the heuristics value of this position.
	 */
	protected int evaluatePosition(int stage) {

		// Adjusts the evaluation for white or black.
		int colour = 1;
		if (!isWhiteNextToMove) {
			colour = -1;
		}

		int positionValue = 0;

		if (stage == OthelloAI.END_OF_GAME_STAGE) {
			int numberOfWhitePieces = Long.bitCount(bitBoard.getWhitePieces());
			int numberOfBlackPieces = Long.bitCount(bitBoard.getBlackPieces());
			if (numberOfWhitePieces > numberOfBlackPieces) {
				return colour * WHITE_WINS;
			}
			else if (numberOfWhitePieces == numberOfBlackPieces) {
				return DRAW;
			}
			else {
				return colour * BLACK_WINS;
			}
		}

		// Gets the stage of the game and use the right weights for the different evaluation functions.
		stage = (stage + 1) / 8;

		positionValue += discsSquareEvaluation(stage);
		positionValue += MOBILITY_HEURISTICS[stage] * evaluateMobility();
		positionValue += PIECES_DIFFERENTIAL_HEURISTICS[stage] * evaluatePieceDifferential();
		positionValue += evaluateParity(stage);
		positionValue += STABILITY_HEURISTICS[stage] * stabilityEvaluation();
		positionValue += FRONTIER_HEURISTICS[stage] * frontierDifferentialEvaluation();

		return colour * positionValue;
	}

	/**
	 * Evaluates the move depending on certain squares in the board.
	 * @param stage
	 * @return
	 */
	private int discsSquareEvaluation(int stage) {
		int evaluationValue = 0;

		long whitePieces = bitBoard.getWhitePieces();
		long blackPieces = bitBoard.getBlackPieces();

		evaluationValue += CORNER_HEURISTICS[stage] * (Long.bitCount(whitePieces & CORNERS_BITMASK) - Long.bitCount(blackPieces & CORNERS_BITMASK));

		evaluationValue += C_SQUARE_HEURISTICS[stage] * (Long.bitCount(whitePieces & C_SQUARE_BITMASK) - Long.bitCount(blackPieces &
				C_SQUARE_BITMASK));


		evaluationValue += X_SQUARE_HEURISTICS[stage] * (Long.bitCount(whitePieces & X_SQUARE_BITMASK) - Long.bitCount(blackPieces & X_SQUARE_BITMASK));

		evaluationValue += A_SQUARE_HEURISTICS[stage] * (Long.bitCount(whitePieces & A_SQUARE) - Long.bitCount(blackPieces & A_SQUARE));

		evaluationValue += B_SQUARE_HEURISTICS[stage] * (Long.bitCount(whitePieces & B_SQUARE) - Long.bitCount(blackPieces & B_SQUARE));

		return evaluationValue;
	}

	/**
	 * Evaluates mobility.
	 * @return
	 */
	private int evaluateMobility() {

		return Long.bitCount(whitePossibleMoves) - Long.bitCount(blackPossibleMoves);
	}

	/**
	 * Evaluates pieces differential.
	 * @return
	 */
	private int evaluatePieceDifferential() {

		return Long.bitCount(bitBoard.getWhitePieces()) - Long.bitCount(bitBoard.getBlackPieces());
	}

	/**
	 * Evaluates frontier differential.
	 * @return
	 */
	private int frontierDifferentialEvaluation() {
		int frontierDifferentialEvaluation = 0;

		long whiteEmptySquares;
		long blackEmptySquares;

		whiteEmptySquares = blackEmptySquares = ~(bitBoard.getWhitePieces() | bitBoard.getBlackPieces());

		long temp = 0;

		for (int i = 0; i < BitBoard.DIRECTIONS; i++) {
			temp = BitBoard.shiftPieces(bitBoard.getWhitePieces(), i) & whiteEmptySquares;
			whiteEmptySquares = whiteEmptySquares - temp;

			frontierDifferentialEvaluation -= Long.bitCount(temp);

			temp = BitBoard.shiftPieces(bitBoard.getBlackPieces(), i) & blackEmptySquares;
			blackEmptySquares = blackEmptySquares - temp;

			frontierDifferentialEvaluation += Long.bitCount(temp);
		}

		return frontierDifferentialEvaluation;
	}

	/**
	 * Evaluates parity.
	 * @param stage
	 * @return
	 */
	private int evaluateParity(int stage) {
		if (isWhiteLastToMove)
			return PARITY_HEURISTICS[stage];
		else
			return -PARITY_HEURISTICS[stage];
	}

	/**
	 * Evaluates stability.
	 * @return
	 */
	private int stabilityEvaluation() {
		int totalStability = 0;

		/*
		 * Player
		 */
		int numberCorners = Long.bitCount(bitBoard.getWhitePieces() & CORNERS_BITMASK);
		long temp = 0;
		long updatedBoard;
		// Check if there are corners or not.
		if (numberCorners != 0) {
			updatedBoard = bitBoard.getWhitePieces();
			if ((updatedBoard & UP_LEFT_CORNER_BITMASK) != 0) {
				// Increase stability because of the corner itself.
				totalStability++;

				// Move right
				temp = UP_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 0);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability++;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 0);
					temp = temp & updatedBoard;
				}

				// Move down
				temp = UP_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 6);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability++;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 6);
					temp = temp & updatedBoard;
				}
			}

			if ((updatedBoard & UP_RIGHT_CORNER_BITMASK) != 0) {
				totalStability++;

				// Move left
				temp = UP_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 4);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability++;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 4);
					temp = temp & updatedBoard;
				}

				// Move down
				temp = UP_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 6);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability++;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 6);
					temp = temp & updatedBoard;
				}
			}

			if ((updatedBoard & DOWN_LEFT_CORNER_BITMASK) != 0) {
				totalStability++;

				// Move right
				temp = DOWN_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 0);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability++;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 0);
					temp = temp & updatedBoard;
				}

				// Move up
				temp = DOWN_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 2);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability++;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 2);
					temp = temp & updatedBoard;
				}
			}

			if ((updatedBoard & DOWN_RIGHT_CORNER_BITMASK) != 0) {
				totalStability++;

				// Move left
				temp = DOWN_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 4);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability++;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 4);
					temp = temp & updatedBoard;
				}

				// Move up
				temp = DOWN_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 2);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability++;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 2);
					temp = temp & updatedBoard;
				}
			}
		}

		/*
		 * Opponent
		 */
		numberCorners = Long.bitCount(bitBoard.getBlackPieces() & CORNERS_BITMASK);
		temp = 0;
		if (numberCorners != 0) {
			updatedBoard = bitBoard.getBlackPieces();

			if ((updatedBoard & UP_LEFT_CORNER_BITMASK) != 0) {
				totalStability--;

				// Move right
				temp = UP_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 0);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability--;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 0);
					temp = temp & updatedBoard;
				}

				// Move down
				temp = UP_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 6);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability--;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 6);
					temp = temp & updatedBoard;
				}
			}

			if ((updatedBoard & UP_RIGHT_CORNER_BITMASK) != 0) {
				totalStability--;

				// Move left
				temp = UP_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 4);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability--;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 4);
					temp = temp & updatedBoard;
				}

				// Move down
				temp = UP_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 6);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability--;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 6);
					temp = temp & updatedBoard;
				}
			}

			if ((updatedBoard & DOWN_LEFT_CORNER_BITMASK) != 0) {
				totalStability--;

				// Move right
				temp = DOWN_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 0);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability--;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 0);
					temp = temp & updatedBoard;
				}

				// Move up
				temp = DOWN_LEFT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 2);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability--;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 2);
					temp = temp & updatedBoard;
				}
			}
			if ((updatedBoard & DOWN_RIGHT_CORNER_BITMASK) != 0) {
				totalStability--;

				// Move left
				temp = DOWN_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 4);
				temp = temp & updatedBoard;
				while (temp != 0) {
					// Increases the level of stability for the player.
					totalStability--;
					// Take off the pieces just analysed.
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 4);
					temp = temp & updatedBoard;
				}

				// Move up
				temp = DOWN_RIGHT_CORNER_BITMASK;
				temp = BitBoard.shiftPieces(temp, 2);
				temp = temp & updatedBoard;
				while (temp != 0) {
					totalStability--;
					updatedBoard = updatedBoard - temp;
					temp = BitBoard.shiftPieces(temp, 2);
					temp = temp & updatedBoard;

				}
			}
		}

		return totalStability;
	}

	/**
	 * Gets the number of children for this node.
	 * @return
	 */
	protected int getNumberOfChildren() {
		if (isWhiteNextToMove) {
			return Long.bitCount(whitePossibleMoves);
		}
		else {
			return Long.bitCount(blackPossibleMoves);
		}
	}

}
