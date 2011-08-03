package traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashSet;

public class Road {
	/*
	 * Contains all information relating to a road, as well as the functions that help render it on the canvas
	 */
	
	
	public int intLoc;//these are in "units"!
	public int intLength;//these are in "units"!
	//public int height,yPos,sYPos,sHeight;//these are in pixels
	public int start,finish;//these are in "units"!
	public double pixelsPerUnit;
	public int index;//TODO: most of this^ shit is going to be changed
	
	private Polygon pavement;//,intersection;
	public double theta,startAngle;
	public ArrayList<Car> rCars;
	public int unitCenter;
	
	HashSet<Color> colors;
	
	Road(String lineElts[],int i){//this looks pretty good right now
		intLoc = Integer.parseInt(lineElts[1]);
		intLength = Integer.parseInt(lineElts[2]);
		//both of the above are in units
		rCars = new ArrayList<Car>();
		index = i;
		pavement = new Polygon();
		//intersection = new Polygon();
		colors = new HashSet<Color>();
		
		
	}
	
	public void setLength(int begin, int end){
		Constants.p("Road: "+(index+1));
		start = begin;
		finish = end;
		int startToMid = intLoc - start;
		int finToMid = finish - intLoc;
		Constants.p("Start begins at: "+start+" Finish begins at: "+finish);
	//	if(finToMid > startToMid)
	//		start = intLoc - finToMid;
	//	else
	//		finish = intLoc + startToMid;
		
		intLoc -= start;//TODO: re-think 'normalizing' at some point, this could be a problem if all cars start after intersection
		unitCenter = intLoc + intLength/2;
		Constants.p("Start ends at: "+start+" Finish ends at: "+finish);
		Constants.p("\n\n\n");
	//	Constants.p("Road: "+index+" beginning: "+begin+" end: "+end);
	}
	
	public int getLength(){//this is in "units"!
		return finish - start;
	}
	
	public void adjust(RoadCanvas c){
		int[] middle = {c.getWidth()/2,c.getHeight()/2};
		makeRect(c,pavement,1000,middle);
		
		
		
		double ppu;
	//	Constants.p("Starting Angle: "+startAngle);
		if(startAngle > Math.atan(c.getHeight()/c.getWidth()) && startAngle <= Math.atan(c.getHeight()/c.getWidth()) +Math.PI/2){
			if(Math.abs(Math.sin(startAngle)) <= 1)
				ppu = ( (double)c.getHeight())/(getLength());
			else
				ppu = ( (double)c.getHeight())/(getLength() * Math.abs(Math.sin(startAngle)));
	//		Constants.p("Sine Used!");
		}else{
			if(Math.abs(Math.cos(startAngle)) <= 1)
				ppu = ( (double)c.getWidth())/(getLength());
			else
				ppu = ((double) c.getWidth())/(getLength() * Math.abs(Math.cos(startAngle)));
	//		Constants.p("Cosine Used!");
			
		}
		
		
		
		pixelsPerUnit = ppu;
		

	//	makeRect(c,intersection,intLength*pixelsPerUnit/2,middle);

		
	}
	
	private void makeRect(RoadCanvas c, Polygon subject, int halfLength,int[] middle){
		subject.reset();
		theta = 2*Math.PI/(2*c.roads.size());
		
		startAngle = (index*theta) + theta/2;
		
		double a = startAngle;
		
		int halfWidth = Constants.ROAD_WIDTH/2;
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
	

	public void paintComponent(Graphics g,boolean firstPass,int centerX,int centerY) {
			
			if(firstPass){
				g.setColor(Color.black);
				g.fillPolygon(pavement);
			}else{
				//g.setColor(Color.red);
				//g.fillPolygon(intersection);
				int[] point = {centerX,centerY};
				Constants.polarMove(point, startAngle+Math.PI,(int)((centerX+centerY)/3));
				g.setColor(Color.white);
				//g.fillRect(-2, -2, this.getWidth()+5,this.getHeight()+5);
				g.fillRect(point[0],point[1]-13,50,13);
				g.setColor(Color.black);
				g.drawString("Road "+(index+1),point[0],point[1]);
					
			}
		
	}
	
	public void drawFinishes(Graphics g){
		ArrayList<Integer> finishes = new ArrayList<Integer>();
		
		if(!rCars.isEmpty())//Hopefully all of this can be unchanged, let the car class handle it
		for(int i = rCars.size()-1;i >= 0;i--){
			Car c = rCars.get(i);
			g.setColor(c.color);
			
			int offSet = 0;
			
			while(finishes.contains(c.finish+offSet))//this allows finish lines in the same location to be distinguished
				offSet += 2;
			
			finishes.add(c.finish+offSet);
			c.paintComponent(g,offSet);
		}
	}
}


