package traffic;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Road {
	public int intLoc;//these are in "units"!
	public int intLength;//these are in "units"!
	private int width,xPos;//these are in pixels
	public int height,yPos,sYPos,sHeight;//these are in pixels
	private int sWidth,sXPos;//these are in pixels
	public int start,finish;//these are in "units"!
	public int pixelsPerUnit;
	private int index;//TODO: all of this^ shit is going to be changed
	
	private Polygon pavement,intersection;
	public double theta,startAngle;
	public ArrayList<Car> rCars;

	public int unitCenter;
	
	Road(String lineElts[],int i){//this looks pretty good right now
		intLoc = Integer.parseInt(lineElts[1]);
		intLength = Integer.parseInt(lineElts[2]);
		//both of the above are in units
		rCars = new ArrayList<Car>();
		index = i;
		pavement = new Polygon();
		intersection = new Polygon();
		
		
	}
	
	public void setLength(int begin, int end){//this is going to be changed according to that one style
		
		start = begin - 5;
		finish = end;
		intLoc -= start;//TODO: re-think 'normalizing' at some point, this could be a problem if all cars start after intersection
		unitCenter = intLoc + intLength/2;
		
	}
	
/*	public int getLength(){//this is in "units"!
		return finish - start;
	}*/
	
	public void adjust(RoadCanvas c){//TODO: re-do all this shit to handle the shiny new polygons
		int[] middle = {c.getWidth()/2,c.getHeight()/2};
		makeRect(c,pavement,1000,middle);
		makeRect(c,intersection,intLength*4,middle);
		
		double ppu;
		Constants.p("Unit center: "+unitCenter);
		if(startAngle > Math.tanh(c.getHeight()/c.getWidth()) && startAngle <= Math.tanh(c.getHeight()/c.getWidth()) +Math.PI/2){
			if(Math.sin(startAngle) == 0)
				ppu = ( c.getHeight()/2)/(unitCenter);
			else{
				ppu = ( c.getHeight()/2)/(unitCenter * Math.abs(Math.sin(startAngle)));
				Constants.p("Sine: "+Math.sin(startAngle));
			}
		}else{
			if(Math.cos(startAngle) == 0)
				ppu = ( c.getWidth()/2)/(unitCenter);
			else{
				ppu = ( c.getWidth()/2)/(unitCenter * Math.abs(Math.cos(startAngle)));
				Constants.p("PPU: "+ppu);
			}
		}
		
		
		
		pixelsPerUnit = (int)ppu;
		
	/*	for(Car car:rCars){
			makeRect(c,car.getImage(),pixelsPerUnit*4,car.translate());
		}*/
		
	}
	
	private void makeRect(RoadCanvas c, Polygon subject, int halfLength,int[] middle){
		subject.reset();
		theta = 2*Math.PI/(2*c.roads.length);
		
		startAngle = (index*theta) + theta/2;
		
		double a = startAngle;
		
		int halfWidth = 20;//(c.getWidth()+c.getHeight())/100;
		for (int i = 0; i < 2; i++){

			int[] center = middle.clone();
			Constants.polarMove(center, a, halfLength);
			a -= Math.PI/2;
			Constants.polarMove(center,a,halfWidth);
			subject.addPoint(center[0],center[1]);
			a += Math.PI;
			Constants.polarMove(center,a,halfWidth*2);
			subject.addPoint(center[0], center[1]);
			a = startAngle + Math.PI;
		}
	}
	

	public void paintComponent(Graphics g,boolean firstPass) {
			
			if(firstPass){
				g.setColor(Color.black);
				g.fillPolygon(pavement);
			}else{
				g.setColor(Color.red);
				g.fillPolygon(intersection);
			}
			
			
			
			
			/*ArrayList<Integer> finishes = new ArrayList<Integer>();
			
			if(!rCars.isEmpty())//Hopefully all of this can be unchanged, let the car class handle it
			for(int i = rCars.size()-1;i >= 0;i--){
				Car c = rCars.get(i);
				g.setColor(c.color);
				
				int offSet = 0;
				
				while(finishes.contains(c.finish+offSet))//this allows finish lines in the same location to be distinguished
					offSet += 2;
				
				finishes.add(c.finish+offSet);
				c.paintComponent(g,pixelsPerUnit,offSet);
			
			}*/
		
	}
}


