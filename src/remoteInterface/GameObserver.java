package remoteInterface;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

import othelloModel.Game;
import othelloModel.Move;
import Interfaces.IRemoteGame;

/**
 * Class that observes the local board and passes changes onto a remote board
 * @author Alan Dearle
 */

public class GameObserver implements Observer {

	IRemoteGame remoteGame;
	
	
	/**
	 * Constructor takes a remote Othello board as a parameter
	 * @param remoteGame - the board to be updated when local copy changes
	 */
	public GameObserver( IRemoteGame remoteGame ) {
		this.remoteGame = remoteGame;
	}

	
	public void update(Observable arg0, Object arg1) {
		if( arg0 instanceof Game && arg1 instanceof Move ) {
			Move move = (Move) arg1;
			try {
				remoteGame.play(move.row, move.col);
			} catch ( RemoteException e ) {
				System.out.println( "Error making remote move:" );
			}
		} else {
			System.out.println( "Observation error:" );
		}
		
	}
	
}
