package factory;

import java.lang.reflect.Constructor;

import util.PropertiesWrapper;
import Interfaces.IGame;
import Interfaces.IOthelloFrame;

public class GUIFactory {
	
	public static IOthelloFrame makeGUI( PropertiesWrapper pw, IGame model ) {
		
		// return new OthelloFrame(model);
		
		String classname = pw.getProperty("GUI_CLASS");
		if( classname == null ) {
			System.out.println( "Cannot load GUI class" );
			System.exit(0);
		}
		Class<? extends IOthelloFrame> clazz;
		try {
			clazz = (Class<? extends IOthelloFrame>) Class.forName(classname);
			Constructor<? extends IOthelloFrame> cons = clazz.getConstructor(IGame.class);
			return (IOthelloFrame) cons.newInstance(model);
			
		} catch ( Exception e ) {
			System.out.println( "GUI Factory Cannot instantiate class" );
			e.printStackTrace();
			return null;
		}
	}
}
