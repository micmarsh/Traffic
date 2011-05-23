package traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Car {
	
	public int start;
	private int velocity;
	public int finish;//All of these: all in units!
	private int roadIndex;
//	private int begin,end;//in pixels!
	private Polygon image;
	private int[] wheelStats;// index 0: size index, 1-4: locations of (1-2) first wheel, (3-4) second wheel, all in pixels
	private Color color;
	
	Car(String lineElts[],RoadCanvas c,int r){
		start = Integer.parseInt(lineElts[1]);
		velocity = Integer.parseInt(lineElts[2]);
		finish = Integer.parseInt(lineElts[3]);
		roadIndex = r;
		image = new Polygon();
		wheelStats = new int[5];
		adjust(c);
		
	}
	
	
	public void adjust(RoadCanvas c){
		int offSet = c.roads[roadIndex].sHeight*4/3;
	//	System.out.println("c.WIDTH "+offSet);
		int begin = start*c.roads[roadIndex].pixelsPerUnit - offSet;
		int end = start*c.roads[roadIndex].pixelsPerUnit + offSet;
		
		offSet /= 6;
//		System.out.println("c.WIDTH divided by 20, then by six: "+offSet);
		int[] Xs = {begin,begin+offSet,begin+offSet*3/2,begin+offSet*9/2,begin+offSet*5,begin+offSet*6,
				begin+offSet*6,begin};
		
		wheelStats[1] = begin;
		wheelStats[3] = begin+offSet*4;
		
		int yPos = c.roads[roadIndex].sYPos;
		
		offSet = c.roads[roadIndex].sHeight/3;
	//	System.out.println("yPos: "+yPos+"\noffSet: "+offSet);
		int[] Ys = {yPos+offSet,yPos+offSet,yPos,yPos,yPos+offSet,yPos+offSet,yPos+2*offSet,yPos+2*offSet};
		wheelStats[0] = offSet;
		wheelStats[2] = yPos+2*offSet;
		wheelStats[4] = yPos+2*offSet;
		image.reset();
		
		for(int i = 0; i<8;i++){
			image.addPoint(Xs[i], Ys[i]);
			//System.out.println("Ys[i]: "+Ys[i]);
		}
		
		
	}
	
	public void setColor(Color c){
		color = c;
	}
	
	public void paintCar(Graphics g){
	//	System.out.println("Car Painted");
		g.fillPolygon(image);
		g.fillOval(wheelStats[1], wheelStats[2], wheelStats[0], wheelStats[0]);
		g.fillOval(wheelStats[3], wheelStats[4], wheelStats[0], wheelStats[0]);
	}
	
	public void move(){
		start += velocity;
		//System.out.println("This Car's position is: "+start);
	}
	
	
	public void paintComponent(Graphics g,int pixelsPerUnit,int offSet){
	//	System.out.println("Approximate image X position: "+image.getBounds().width);
	//	System.out.println("Approximate image Y position: "+image.getBounds().height);
		g.drawLine(finish*pixelsPerUnit + offSet, wheelStats[2]-2*wheelStats[0], finish*pixelsPerUnit+offSet, wheelStats[2]+wheelStats[0]);
		paintCar(g);
		
		
		//g.fillRect(image.getBounds().x, image.getBounds().y, image.getBounds().width, image.getBounds().height);
	}
}
