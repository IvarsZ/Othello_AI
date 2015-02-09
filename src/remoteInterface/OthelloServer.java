package remoteInterface;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Interfaces.IGameBroker;
import Interfaces.IOthelloFrame;

import factory.GUIFactory;

import othelloModel.Game;
import util.PropertiesWrapper;


/**
 * An Othello Server class
 * Must be run before the client
 * @author Alan Dearle
 */

public class OthelloServer {
	public static void main(String[] args) throws RemoteException {
		PropertiesWrapper pw = null;
		try {
			pw = new PropertiesWrapper( new File( "othello.properties" ) );
		} catch ( IOException e1 ) {
			System.out.println("Cannot find properties file: othello.properties");
			System.exit(1);
		}
		Game model = new Game(pw);
		IOthelloFrame view = GUIFactory.makeGUI(pw,model);
		
		GameBroker server = new GameBroker( model, view );
		IGameBroker server_stub = (IGameBroker) UnicastRemoteObject.exportObject(server,0);
		
		Registry registry = LocateRegistry.createRegistry(35678);
		
		try {
			registry.bind("OthelloServer", server_stub ); 
		}
		catch ( AlreadyBoundException e ) {
			e.printStackTrace();
		}
	}
}
