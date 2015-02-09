package remoteInterface;

import java.rmi.RemoteException;
import java.util.Observer;

import Interfaces.IGame;
import Interfaces.IGameBroker;
import Interfaces.IOthelloFrame;
import Interfaces.IRemoteGame;

import othelloModel.Game;

/*
 * A networked server to play Othello
 * @author Alan Dearle
 */


public class GameBroker implements IGameBroker {
	
	private IRemoteGame remotegame;
	private boolean quit = false;
	private IGameBroker remote_broker;
	private IOthelloFrame localGUI;
	private IGame localgame;

	public GameBroker() throws RemoteException {}
	
	/**
	 * Constructor for server. 
	 */
	public GameBroker( IGame localgame, IOthelloFrame localGUI ) throws RemoteException {
		super();
		this.localgame = localgame;
		this.localGUI = localGUI;
	}
	
	/* (non-Javadoc)
	 * @see remoteInterface.IGameBroker#getGame()
	 */
	public IRemoteGame getGame() throws RemoteException {
		return new RemoteGame( localgame,localGUI );
	}
	
	/* (non-Javadoc)
	 * @see remoteInterface.IGameBroker#makeConnection(remoteInterface.IGameBroker)
	 */
	public void makeConnection(IGameBroker gm) throws RemoteException {
		remote_broker = gm;
		remotegame = gm.getGame();
		
		Observer observer = new GameObserver( remotegame );
		
		localgame.addObserver(observer);
	}
	
	/* (non-Javadoc)
	 * @see remoteInterface.IGameBroker#quitGame()
	 */
	public void quitGame() throws RemoteException {
		quit = true;
		// TODO tidy stuff up.
	}

	
}
