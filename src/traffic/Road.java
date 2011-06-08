package traffic;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
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
	
	public ArrayList<Car> rCars;
	
	Road(String lineElts[]){
		intLoc = Integer.parseInt(lineElts[1]);
		intLength = Integer.parseInt(lineElts[2]);
		//both of the above are in units
		rCars = new ArrayList<Car>();
		
		
	}
	
	public void setLength(int begin, int end){//this is all in "units"!
		start = begin - 5;
		finish = end;
		intLoc -= start;
	}
	
	public int getLength(){//this is in "units"!
		return finish - start;
	}
	
	public void adjust(RoadCanvas c){

		height = c.getHeight()/c.roads.length;
		width = c.getWidth();
		xPos = 0;
		yPos = c.road * height;
		
		sHeight = height/3;
		sWidth = width;
		sXPos = 0;
		sYPos = yPos +(height/3);
		
		if(width > getLength())//This check is probably unnecessary, as the window is usually going to be large enough
			pixelsPerUnit = width/getLength();
		else{/*again, this whole check is probably unnecessary*/}
		
	}
	

	public void paintComponent(Graphics g) {
	
			g.setColor(Color.white);
			g.fillRect(xPos, yPos, width, height);
			
			g.setColor(Color.black);
			g.fillRect(sXPos, sYPos, sWidth, sHeight);
			
			g.setColor(Color.red);
			g.fillRect((intLoc)*pixelsPerUnit,sYPos,intLength*pixelsPerUnit,sHeight);
			
			
			ArrayList<Integer> finishes = new ArrayList<Integer>();
			
			if(!rCars.isEmpty())
			for(int i = rCars.size()-1;i >= 0;i--){
				Car c = rCars.get(i);
				g.setColor(c.color);
				
				int offSet = 0;
				
				while(finishes.contains(c.finish+offSet))//this allows finish lines in the same location to be distinguished
					offSet += 2;
				
				finishes.add(c.finish+offSet);
				c.paintComponent(g,pixelsPerUnit,offSet);
			
			}
		
	}
}


