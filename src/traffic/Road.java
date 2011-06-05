package traffic;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

public class Road {
	public int intLoc;//these are in "units"!
	public int intLength;//these are in "units"!
	private int width,xPos;
	public int height,yPos,sYPos,sHeight;
	private int sWidth,sXPos;
	public int start,finish;//these are in "units"!
	public int pixelsPerUnit;
	
	public ArrayList<Car> rCars;
	
	Road(String lineElts[]){
		intLoc = Integer.parseInt(lineElts[1]);
		intLength = Integer.parseInt(lineElts[2]);
		
		rCars = new ArrayList<Car>();
		//adjust(c);
		
	}
	
	public void setLength(int begin, int end){//this is in "units"!
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
		
	//	System.out.println("yPos just set to "+yPos+" and Road number is: "+c.road);
		
		sHeight = height/3;
		sWidth = width;
		sXPos = 0;
		sYPos = yPos +(height/3);
		
		if(width > getLength())
			pixelsPerUnit = width/getLength();
		else{}//TODO: Throw an exception up to "Traffic", which should pause program and say window is too small.
		
		
		
		
	/*	for(int i = 0; i<4;i++){//TODO: these values do need to be normalized at some point, but not now
			HWXY[i] /= 100;
			sHWXY[i] /= 100;
		}*/
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
			for(Car c:rCars){
				g.setColor(c.color);
				
				int offSet = 0;
				
				while(finishes.contains(c.finish+offSet))
					offSet += 2;
				
				finishes.add(c.finish+offSet);
				c.paintComponent(g,pixelsPerUnit,offSet);
				
				/*B += incr;
				if(B > 250){
					B = 0;
					G += incr;
					if(G > 250){
						G = 0;
						R += incr;
						if(R > 250){
							R = 0;
							G = 0;
							B = 0;
						}
					}
				}*/
				
			}
			/* g.setColor(Color.white);
			    g.fillRect(0, 0, getWidth(), getHeight());

			    // yellow circle
			    g.setColor(Color.yellow);
			    g.fillOval(0, 0, 240, 240);

			    // magenta circle
			    g.setColor(Color.magenta);
			    g.fillOval(160, 160, 240, 240);


			    // transparent red square
			    g.setColor(Color.red);
			    g.fillRect(width/4, 220, width/2, 120);

			    // transparent green circle
			    g.setColor(Color.blue);
			    g.fillOval(140, 140, 120, 120);

			    // transparent blue square
			    g.setColor(Color.green);
			    g.fillRect(220, 60, 120, height/3);
			    
			    g.setColor(Color.black);
*/
			 /*   FontMetrics fm = g.getFontMetrics();
			    int w = fm.stringWidth(""+(new Random()).nextInt());
			    int h = fm.getAscent();
		//	    System.out.println("yPos is:" +yPos);
			    g.drawString((intLoc-start)+" "+pixelsPerUnit, yPos/2,yPos+h);*/

			
		
	}
}


