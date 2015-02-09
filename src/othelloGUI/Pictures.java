package othelloGUI;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Pictures {

	public static ImageIcon blackPiece;
	public static ImageIcon whitePiece;
	
	private final static int PIECE_SIZE = 45;

	public Pictures() {

		// String that gets printed in case of IOException while loading pictures.
		String error = "ERROR: while loading picture";

		// Loading the paddle image.
		try {
			blackPiece = new ImageIcon(ImageIO.read(this.getClass().getResource("/Resources/black_piece.png")).getScaledInstance(PIECE_SIZE,
					PIECE_SIZE, Image.SCALE_SMOOTH));
		}
		catch (IOException e) {
			System.out.println(error);
		}
		
		// Loading the paddle image.
		try {
			whitePiece = new ImageIcon(ImageIO.read(this.getClass().getResource("/Resources/white_piece.png")).getScaledInstance(PIECE_SIZE,
					PIECE_SIZE, Image.SCALE_SMOOTH));
		}
		catch (IOException e) {
			System.out.println(error);
		}

		

	}
}
