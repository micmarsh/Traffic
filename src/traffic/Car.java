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
import javax.imageio.ImageIO;


public class Car {
	
	public int start;//denoted in UNITS
	int velocity;//denoted in UNITS
	public int finish;//denoted in UNITS
	Road road;//pixels
	
	private Rectangle crashRange;//may not be necessary anymore due to re-structuring of car image

	private BufferedImage image;
	private int[] wheelStats;// index 0: size index, 1-4: locations of (1-2) first wheel, (3-4) second wheel, all in pixels
	public Color color;
	
	public int minVel,maxVel;
	public boolean controlled;
	private RoadCanvas c;
	
	
	Car(String lineElts[],RoadCanvas c,Road r){//Everything in units!
		start = Integer.parseInt(lineElts[1]);
		minVel = Integer.parseInt(lineElts[2]);
		velocity = Integer.parseInt(lineElts[3]);
		maxVel = Integer.parseInt(lineElts[4]);
		finish = Integer.parseInt(lineElts[5]);
		if(lineElts[6].equals("0"))
			controlled = false;
		else
			controlled = true;
		road = r;
		wheelStats = new int[4];//Except for this, this will eventually be pixels
		
		this.c = c;
	//	adjust();
		
	}
	
	public void normalize(){//necessary in order for cars to be rendered in correct locations
		start -= road.start;
		finish -= road.start;
		//Constants.p("Start: "+start+" Finish: "+finish);
		try{
		image = ImageIO.read(new File("car/"+Constants.colorName(color)+".png"));
		
		}catch(Exception e){
			
		}
	}
	
	public Image getImage(){
		return image;
	}
	
	public int[] translate(){//returns a "tuple" with the x and y position of the center of the car, in pixels
	
		int ppu = road.pixelsPerUnit;
		double x = Math.cos(road.startAngle)*(road.unitCenter-start);
		x = c.getWidth()/2 - x*ppu;
		
		double y = Math.sin(road.startAngle)*(road.unitCenter-start);
		y = c.getHeight()/2 - y*ppu;
		int[] toRet = {(int)x,(int)y};
		Constants.polarMove(toRet,road.startAngle-Math.PI/2,25);
		Constants.polarMove(toRet, road.startAngle + Math.PI, 20);
		return toRet;
		
	}
	
	/*public void adjust(){
		//TODO: document this mess "tomorrow"
		/*int yPos = c.roads[roadIndex].sYPos;
		int ppu = c.roads[roadIndex].pixelsPerUnit;
		int offSet = Constants.CRASH_OFFSET*ppu;
		
		int begin = start*ppu - offSet*3/2;
				
		offSet /= 3;
		int cRange = 2*Constants.CRASH_OFFSET*ppu;
		crashRange = new Rectangle((start-2)*ppu,yPos,cRange,offSet*Constants.CRASH_OFFSET);

		
		int[] Xs = {begin,begin+offSet,begin+offSet*3/2,begin+offSet*3/2+cRange,begin+offSet*2+cRange,
				begin+cRange+offSet*3,begin+cRange+offSet*3,begin};
		
		wheelStats[1] = begin + offSet/2;
		wheelStats[3] = wheelStats[1]+cRange;
		
		
		
		offSet = c.roads[roadIndex].sHeight/3;
	
		int[] Ys = {yPos+offSet,yPos+offSet,yPos,yPos,yPos+offSet,yPos+offSet,yPos+2*offSet,yPos+2*offSet};
		wheelStats[0] = offSet;
		wheelStats[2] = yPos+2*offSet;
		image.reset();
		
	
		for(int i = 0; i<8;i++){
			image.addPoint(Xs[i], Ys[i]);
		}*/
	//}
	
	
	public void paintCar(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		int [] toPlace = translate();
	//	Constants.p("Car's position: "+this.start+" Translated indices: ("+toPlace[0]+","+toPlace[1]+")");
		
		g2.rotate(road.startAngle,toPlace[0],toPlace[1]);
		
		g2.drawImage(image,toPlace[0],toPlace[1],image.getWidth()/3,image.getHeight()/3,c);
		g2.rotate(2*Math.PI-road.startAngle,toPlace[0],toPlace[1]);
		
	//	Constants.p("("+image.getWidth()/3+","+image.getHeight()/3+")");
	/*	
		int [] center = this.translate();
		double startAngle = c.roads[this.roadIndex].startAngle;
		Constants.polarMove(center, startAngle+Math.PI/2, Constants.ROAD_WIDTH/2);
		Constants.polarMove(center, startAngle, 20);
		
		g2.drawRect(center[0]-23,center[1]-23,47,47);*/
		
	//	Constants.p("Adjusted center: ("+center[0]+","+center[1]+")");
			
		
	//	g.fillOval(wheelStats[1], wheelStats[2], wheelStats[0], wheelStats[0]);
	//	g.fillOval(wheelStats[3], wheelStats[2], wheelStats[0], wheelStats[0]);
		
	}
	
	
	public void move(){
		start += velocity;
	}
	
	
	public void paintComponent(Graphics g,Road r,int offSet){
		
		int[] point = translate();
		
		Constants.polarMove(point, r.startAngle,(int)(finish-start+3)*r.pixelsPerUnit + offSet);//The 1.5 is an approximate offset to make the finish line show up in the right place
		
		int [] point1 = point.clone();
		
		Constants.polarMove(point, r.startAngle + Math.PI/2, (int)(Constants.ROAD_WIDTH/10));
		Constants.polarMove(point1, r.startAngle + Math.PI/2, (int)(Constants.ROAD_WIDTH*10/9));

		g.drawLine(point[0], point[1], point1[0], point1[1]);
		paintCar(g);
		
	}
}
