package othelloGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

/**
 * Class representing the button used for each square in the board.
 *
 */
public class OthelloSquare extends JButton{

	private int row;
	private int column;
	
	/**
	 * Initialises the button.
	 * @param row
	 * @param column
	 */
	public OthelloSquare(int row, int column) {
		this.row = row;
		this.column = column;
		
		setBackground(new Color(48, 128, 20));
		setBorder(LineBorder.createBlackLineBorder());
		setOpaque(true);
		
	}

	/**
	 * Gets the row at which the button is.
	 */
	protected int getRow() {
		return row;
	}

	/**
	 * Gets the column at which the button is.
	 * @return
	 */
	protected int getColumn() {
		return column;
	}
	
	
}

