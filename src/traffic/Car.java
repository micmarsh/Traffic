package traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;


public class Car {
	/*
	 * Stores any and all information relating to the current state of a car as well as the functions 
	 * that help render it on the canvas (each car's history is recorded in and by MainFrame)
	 * 
	 */
	
	public int start;//denoted in UNITS
	int velocity;//denoted in UNITS
	public int finish;//denoted in UNITS
	Road road;//pixels
	public boolean deleted;
	
	private Rectangle crashRange;//may not be necessary anymore due to re-structuring of car image

	BufferedImage image;
	private int[] wheelStats;// index 0: size index, 1-4: locations of (1-2) first wheel, (3-4) second wheel, all in pixels
	public Color color;
	
	public int minVel,maxVel;
	public double maxAccel;
	public boolean controlled;
	public RoadCanvas c;
	
	
	
	
	Car(String lineElts[],RoadCanvas c,Road r){//Everything in units!
		start = Integer.parseInt(lineElts[1]);
		minVel = Integer.parseInt(lineElts[2]);
		velocity = Integer.parseInt(lineElts[3]);
		maxVel = Integer.parseInt(lineElts[4]);
		maxAccel = Double.parseDouble(lineElts[5]);
		finish = Integer.parseInt(lineElts[6]);
		if(lineElts[7].equals("0"))
			controlled = false;
		else
			controlled = true;
		road = r;
		wheelStats = new int[4];//Except for this, this will eventually be pixels
		
		this.c = c;
		
		deleted = false;
	//	adjust();
	}
	
	public void normalize(){//may be necessary in order for cars to be rendered in correct locations
		
		//finish -= road.start;
		//start -= road.start;
	//Constants.p("Start: "+start+" Finish: "+finish);
		try{
		image = ImageIO.read(new File("car/"+Constants.colorName(color)+".png"));
		
		}catch(Exception e){
			Constants.p("lulzception!");
		}
	}
	
	public Car copy(){
		String[] stats = {"",""+start,""+minVel,""+velocity,""+maxVel,""+maxAccel,""+finish,""};
		if(controlled)
			stats[7] = "1";
		else
			stats[7] = "0";
		Car toRet = new Car(stats,c,road);
		toRet.image = image;
		toRet.color = color;
		return toRet;
	}
	
	public String toString() {
				String s = start + "," + minVel + "," + velocity + "," + maxVel + ","+maxAccel+"," + finish + "," + (controlled ? 1 : 0) + "\n";
		 		return s;
	}

	
	public Image getImage(){
		return image;
	}
	
	public int[] translate(){//returns a "tuple" with the x and y position of the center of the car, in pixels
	
		double ppu = road.pixelsPerUnit;
		double x = Math.cos(road.startAngle)*(road.unitCenter-start);
		x = c.getWidth()/2 - x*ppu;
		
		double y = Math.sin(road.startAngle)*(road.unitCenter-start);
		y = c.getHeight()/2 - y*ppu;
		int[] toRet = {(int)x,(int)y};
		Constants.polarMove(toRet,road.startAngle-Math.PI/2,25);
		Constants.polarMove(toRet, road.startAngle + Math.PI, 20);
		return toRet;
		
	}
	
	
	
	public void paintCar(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		int [] toPlace = translate();
	
		
		g2.rotate(road.startAngle,toPlace[0],toPlace[1]);
		
		g2.drawImage(image,toPlace[0],toPlace[1],image.getWidth()/3,image.getHeight()/3,c);
		g2.rotate(2*Math.PI-road.startAngle,toPlace[0],toPlace[1]);
		
		g.setColor(Color.red);
		toPlace = translate();
		
		Constants.polarMove(toPlace, road.startAngle*.8 + Math.PI/2, 30);
		Constants.polarMove(toPlace, road.startAngle, 20);
		
		String toDraw;
		
		if(start < road.intLoc)
			toDraw = ""+(road.intLoc - start);
		else if(start >= road.intLoc && start < (road.intLoc + road.intLength))
			toDraw = "0";
		else
			toDraw = ""+(start - road.intLoc);
		
		g.drawString(toDraw, toPlace[0],toPlace[1]);

	}
	
	
	public void move(){
		start += velocity;
	}
	
	
	public void paintComponent(Graphics g,int offSet){
		
		int[] point = translate();
		
		Constants.polarMove(point, road.startAngle,(int)((finish-start+5)*road.pixelsPerUnit + offSet));//The 5 is an approximate offset to make the finish line show up in the right place
		
		int [] point1 = point.clone();
		
		Constants.polarMove(point, road.startAngle + Math.PI/2, (int)(Constants.ROAD_WIDTH/10));
		Constants.polarMove(point1, road.startAngle + Math.PI/2, (int)(Constants.ROAD_WIDTH*10/9));

		g.drawLine(point[0], point[1], point1[0], point1[1]);
		paintCar(g);
		
	}
}
