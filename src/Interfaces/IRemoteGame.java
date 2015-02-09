package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteGame extends Remote {
	
	public abstract boolean isGameOver() throws RemoteException;
	
	public abstract boolean isWhiteToPlay() throws RemoteException;
	
	public abstract boolean play(int row, int col) throws RemoteException;
	
}