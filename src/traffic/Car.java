package traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Car {
	
	public int start;//denoted in UNITS
	int velocity;//denoted in UNITS
	public int finish;//denoted in UNITS
	int roadIndex;//pixels
	
	private Rectangle crashRange;//may not be necessary anymore due to re-structuring of car image

	private Polygon image;
	private int[] wheelStats;// index 0: size index, 1-4: locations of (1-2) first wheel, (3-4) second wheel, all in pixels
	public Color color;
	
	public int minVel,maxVel;
	public boolean controlled;

	Car(String lineElts[],RoadCanvas c,int r){//Everything in units!
		start = Integer.parseInt(lineElts[1]);
		minVel = Integer.parseInt(lineElts[2]);
		velocity = Integer.parseInt(lineElts[3]);
		maxVel = Integer.parseInt(lineElts[4]);
		finish = Integer.parseInt(lineElts[5]);
		if(lineElts[6].equals("0"))
			controlled = false;
		else
			controlled = true;
		roadIndex = r;
		image = new Polygon();
		wheelStats = new int[4];//Except for this, this will eventually be pixels
	
		adjust(c);
		
	}
	
	public void normalize(RoadCanvas c,int r){//necessary in order for cars to be rendered in correct locations
		start -= c.roads[r].start;
		finish -= c.roads[r].start;
	}
	
	public Polygon getImage(){
		return image;
	}
	
	public void adjust(RoadCanvas c){
		//TODO: document this mess "tomorrow"
		int yPos = c.roads[roadIndex].sYPos;
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
		}
		
		
	}
	
	
	public void paintCar(Graphics g){

		g.fillPolygon(image);
		g.fillOval(wheelStats[1], wheelStats[2], wheelStats[0], wheelStats[0]);
		g.fillOval(wheelStats[3], wheelStats[2], wheelStats[0], wheelStats[0]);
		
	}
	
	public void move(){
		start += velocity;
	}
	
	
	public void paintComponent(Graphics g,int pixelsPerUnit,int offSet){

		g.drawLine(finish*pixelsPerUnit + offSet, wheelStats[2]-2*wheelStats[0], finish*pixelsPerUnit+offSet, wheelStats[2]+wheelStats[0]);
		
		paintCar(g);
		
	}
}
