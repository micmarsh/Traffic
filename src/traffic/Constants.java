package traffic;

import java.awt.Color;

public final class Constants {
	//TODO: crash offset is going to be stored somewhere other than here, as it's going to be read in.
	static final int CRASH_OFFSET = 2;//The length of units (on both sides) that denotes the "crash range" of all cars
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
	//	if(c.equals(Color.pink))
	//		return "Pink";
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
	//	if(c.equals(Color.pink))
	//		return "Pink";
		if(s.equals("Orange"))
			return Color.orange;
		if(s.equals("Cyan"))
			return Color.cyan;
		Constants.p("Returning null");
		return null;
	}
	public static void p(String s){
		System.out.println(s);
	}
}
