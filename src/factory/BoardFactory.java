package factory;

import Interfaces.IBoard;
import util.PropertiesWrapper;
import java.lang.Class;

public class BoardFactory {
	public static IBoard makeBoard( PropertiesWrapper pw ) {
		String classname = pw.getProperty("BOARD_CLASS");
		if( classname == null ) {
			System.out.println( "Cannot load board class" );
			System.exit(0);
		}
		Class clazz;
		try {
			clazz = Class.forName(classname);
			return (IBoard) clazz.newInstance();
		} catch ( Exception e ) {
			System.out.println( "Board Factory Cannot instantiate class" );
			return null;
		}
	}
}
