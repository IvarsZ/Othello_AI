package othelloGUI;

import java.awt.BorderLayout;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import Interfaces.IGame;
import Interfaces.IOthelloFrame;

import othelloAI.OthelloAI;
import othelloModel.Board;
import othelloModel.Game;
import othelloModel.Move;

/**
 * This class represent the main frame for the GUI. 
 * Here all the components of the GUI are added.
 *
 */
public class OthelloJFrame extends JFrame implements Observer, IOthelloFrame{

	private final int JLABEL_CELL_SIZE = 50;

	/*
	 * Components of the GUI. 
	 */
	private JPanel othelloPanel;
	private JPanel buttonsPanel;
	private OthelloSquare[][] othelloSquares;
	private JLabel whoIsToPlay;
	private JLabel score;
	private JButton playBlackButton;
	private JButton playWhiteButton;
	private JEditorPane editorPane;

	/*
	 * The game.
	 */
	private IGame game;

	private String informations;

	private DecimalFormat df;

	/**
	 * This constructor initializes the GUI. 
	 * @param game
	 */
	public OthelloJFrame(final IGame game) {

		this.game = game;
		df = new DecimalFormat("#.######");

		// Adding the othelloPanel to the frame. 
		othelloPanel = new JPanel(new GridLayout(Board.ROW_COUNT, Board.COLUMN_COUNT));
		buttonsPanel = new JPanel();

		// Initializes the buttons and adds them to the Panel.
		othelloSquares = new OthelloSquare[Board.ROW_COUNT][Board.COLUMN_COUNT];
		for (int row = 0; row < othelloSquares.length; row++) {
			for (int column = 0; column < othelloSquares[0].length; column++) {
				othelloSquares[row][column]  = new OthelloSquare(row, column);
				othelloSquares[row][column].addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						OthelloSquare pressedButton = (OthelloSquare) e.getSource();
						game.play(pressedButton.getRow(), pressedButton.getColumn());
						//System.out.println("r " + pressedButton.getRow() + " c " + pressedButton.getColumn());
					}

				});

				othelloPanel.add(othelloSquares[row][column]);
			}
		}

		int gridPanelWidth = (JLABEL_CELL_SIZE + 10) * Board.COLUMN_COUNT;
		int gridPanelHeight = (JLABEL_CELL_SIZE + 10) * Board.ROW_COUNT;

		// Adds the panel to the Dialog. 
		add(othelloPanel, BorderLayout.CENTER);
		othelloPanel.setPreferredSize(new Dimension(gridPanelWidth, gridPanelHeight));

		/*
		 * The buttons and Labels
		 * 
		 */

		// Adds a label showing whose turn is to play
		whoIsToPlay = new JLabel("----BLACK----");
		whoIsToPlay.setBorder(LineBorder.createGrayLineBorder());
		buttonsPanel.add(whoIsToPlay);

		// Adds the plays button for the black.
		playBlackButton = new JButton("Move Black");
		playBlackButton.addActionListener(new MoveBlack());
		buttonsPanel.add(playBlackButton);
		//buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		// Adds the plays button for the white.
		playWhiteButton = new JButton("Move White");
		playWhiteButton.addActionListener(new MoveWhite());
		buttonsPanel.add(playWhiteButton);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		// Adds a label showing the score
		score = new JLabel("Black: 2 - White: 2");
		score.setBorder(LineBorder.createGrayLineBorder());
		buttonsPanel.add(score);

		informations = "";
		editorPane = new JEditorPane();
		// Makes the Pane Scrollable when needed.
		JScrollPane scrollPane2 = new JScrollPane(editorPane);
		scrollPane2.setPreferredSize(new Dimension(175, 0));
		buttonsPanel.add(scrollPane2, BorderLayout.PAGE_END);

		// Adds the buttonsPanel to the dialog
		add(buttonsPanel, BorderLayout.EAST);

		// Sets the location of the dialog and makes it visible.
		setLocation(new Point(350, 100));
		pack();
		setVisible(true);

		// Makes the GUI to observe the game
		game.addObserver(this);

		// Loads the pictures for the game.
		new Pictures();

		for (int row = 0; row < Board.ROW_COUNT; row ++) {
			for (int column = 0; column < Board.COLUMN_COUNT; column ++) {

				if (game.getSquareContents(row, column).isBlack()) {
					othelloSquares[row][column].setIcon(Pictures.blackPiece);
				} 
				else if (game.getSquareContents(row, column).isWhite()) {
					othelloSquares[row][column].setIcon(Pictures.whitePiece);
				}
			}
		}

	}

	/**
	 * Calls the AI to play black pieces.
	 *
	 */
	class MoveBlack implements ActionListener {
		double time;
		public void actionPerformed(ActionEvent arg0) {

			Board board = (Board) ((Game) game).getBoard();

			// Calculates move and time to find it.
			if(!game.isWhiteToPlay() && !game.isGameOver()) {
				time = System.nanoTime();
				Move move = board.getOthelloAI().nextAIMove(board, false);
				time = System.nanoTime() - time;

				informations += "Black. sec: " + df.format(time/1000000000.0) + "\n";
				editorPane.setText(informations);

				game.play(move.row, move.col);
			}
		}

	}

	/**
	 * Calls the AI to play white pieces.
	 *
	 */
	class MoveWhite implements ActionListener {
		double time;
		public void actionPerformed(ActionEvent arg0) {

			Board board = (Board) ((Game) game).getBoard();

			if(game.isWhiteToPlay() && !game.isGameOver()) {
				// Calculates move and time to find it.
				time = System.nanoTime();
				Move move = board.getOthelloAI().nextAIMove(board, true);
				time = System.nanoTime() - time;

				informations += "White. sec: " + df.format(time/1000000000.0) + "\n";
				editorPane.setText(informations);

				game.play(move.row, move.col);
			}
		}

	}

	/**
	 * Updates the GUI from the Game Model.
	 */
	public void update(Observable arg0, Object arg1) {

		if (arg1 instanceof Move) {
			update();
		}

	}

	/**
	 * Updates the GUI.
	 */
	public void update() {

		// Loops over the squareContents and updates the GUI with the right pictures.
		for (int row = 0; row < Board.ROW_COUNT; row ++) {
			for (int column = 0; column < Board.COLUMN_COUNT; column ++) {

				if (game.getSquareContents(row, column).isBlack()) {
					othelloSquares[row][column].setIcon(Pictures.blackPiece);
				} 
				else if (game.getSquareContents(row, column).isWhite()) {
					othelloSquares[row][column].setIcon(Pictures.whitePiece);
				}
			}
		}

		// Adds more informations about the game on the right-JPanel.
		
		Board board = (Board) ((Game) game).getBoard();
		score.setText("Black: " + Long.bitCount(board.getBitBoard().getBlackPieces()) + " White: " + Long.bitCount(board.getBitBoard().getWhitePieces()));

		if (game.isGameOver()) {
			OthelloAI othelloAI = ((Board) ((Game) game).getBoard()).getOthelloAI();
			othelloAI.saveHistoryTable();
			whoIsToPlay.setText("--GAME OVER--");
		}else {
				if (game.isWhiteToPlay()) {
					whoIsToPlay.setText("----WHITE----");
				} else {
					whoIsToPlay.setText("----BLACK----");
				}
			}
		}


		public void updateMsg() {}

		public void windowActivated(WindowEvent e) {}

		public void windowDeactivated(WindowEvent e) {}

		public void windowDeiconified(WindowEvent e) {}

		public void windowIconified(WindowEvent e) {}

		// Close the program on closing.
		public void windowClosing(WindowEvent e) { System.exit(0);}

		// Close the program.
		public void windowClosed(WindowEvent e) { System.exit(0); }

		public void windowOpened(WindowEvent e) {}

	}
