package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameBroker extends Remote {
	
	public abstract IRemoteGame getGame() throws RemoteException;
	
	/**
	 * Make a connection to a game server - only called by client.
	 * @param IGameBroker gm 
	 */
	public abstract void makeConnection(IGameBroker gm) throws RemoteException;
	
	/**
	 * Quit the game for both players. 
	 */
	public abstract void quitGame() throws RemoteException;
	
}