package traffic;

import java.awt.Color;

public final class Constants {
	/*
	 * Stores various things that it's useful for many other things is the program to be able to access
	 */
	static final int ROAD_WIDTH = 40;
	
	static Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,
			Color.orange,Color.cyan};
	
	public static void polarMove(int[] point,double angle,int distance){
		point[0] += (distance*Math.cos(angle));
		point[1] += (distance*Math.sin(angle));
	}
	
	public static String colorName(Color c){
		if(c.equals(Color.yellow))
			return "Yellow";
		if(c.equals(Color.blue))
			return "Blue";
		if(c.equals(Color.green))
			return "Green";
		if(c.equals(Color.white))
			return "White";
		if(c.equals(Color.magenta))
			return "Pink";

		if(c.equals(Color.orange))
			return "Orange";
		if(c.equals(Color.cyan))
			return "Cyan";
		return "NONE";
	}
	
	public static Color nameToColor(String s){
		if(s.equals("Yellow"))
			return Color.yellow;
		if(s.equals("Blue"))
			return Color.blue;
		if(s.equals("Green"))
			return Color.green;
		if(s.equals("White"))
			return Color.white;
		if(s.equals("Pink"))
			return Color.magenta;

		if(s.equals("Orange"))
			return Color.orange;
		if(s.equals("Cyan"))
			return Color.cyan;
		
		Constants.p("Returning null");
		return null;
	}
	
	public static void p(String s){//Because typing out all 20+ characters of System.out.println(...) is
								//far too tedious
		System.out.println(s);
	}
}
