package remoteInterface;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observer;

import Interfaces.IGame;
import Interfaces.IGameBroker;
import Interfaces.IOthelloFrame;
import Interfaces.IRemoteGame;

import factory.GUIFactory;

import othelloModel.Game;
import util.PropertiesWrapper;


public class OthelloClient {
	
	public static void main(String[] args) throws RemoteException {
		
		PropertiesWrapper pw = null;
		try {
			pw = new PropertiesWrapper( new File( "othello.properties" ) );
		} catch ( IOException e1 ) {
			System.out.println("Cannot find properties file: othello.properties");
			System.exit(1);
		}
		IGame model = new Game( pw ) ;
		
		IOthelloFrame view = GUIFactory.makeGUI(pw,model);
		
		IGameBroker client = new GameBroker( model, view );
		IGameBroker client_stub = (IGameBroker) UnicastRemoteObject.exportObject(client,0);
		
		//Registry registry = LocateRegistry.getRegistry(35678);
		Registry registry = LocateRegistry.getRegistry("mac1-040-m",35678);
		try {
			IGameBroker server = (IGameBroker) registry.lookup("OthelloServer");
			
			server.makeConnection(client_stub);
			
			IRemoteGame remotegame = server.getGame();
			
			Observer observer = new GameObserver( remotegame );
			
			model.addObserver(observer);
		}
		catch (NotBoundException nbe) {
			System.out.println(nbe);
		}
		
		
	}
}
