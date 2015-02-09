package remoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Interfaces.IGame;
import Interfaces.IOthelloFrame;
import Interfaces.IRemoteGame;


/**
 * Remote proxy for Boards
 * @author Alan Dearle
 */

public class RemoteGame extends UnicastRemoteObject implements IRemoteGame {

	private IOthelloFrame localGUI;
	private IGame localgame;

	public RemoteGame(IGame localgame, IOthelloFrame localGUI ) throws RemoteException {
		this.localgame = localgame;
		this.localGUI = localGUI;
	}

	/* (non-Javadoc)
	 * @see remoteInterface.IRemoteGame#isGameOver()
	 */
	public boolean isGameOver() throws RemoteException {
		return localgame.isGameOver();
	}
	
	/* (non-Javadoc)
	 * @see remoteInterface.IRemoteGame#isWhiteToPlay()
	 */
	public boolean isWhiteToPlay() throws RemoteException {
		return localgame.isWhiteToPlay();
	}
	

	/* (non-Javadoc)
	 * @see remoteInterface.IRemoteGame#play(int, int)
	 */
	public boolean play(int row, int col) throws RemoteException {
		boolean result = localgame.remote_play(row, col);
		localGUI.update();
		return result;
	}

	
}
