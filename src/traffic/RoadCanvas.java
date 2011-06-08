package traffic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

public class RoadCanvas extends Canvas {
	Road roads[];
	int road;
	int delta;
	public boolean justPaintCars,updateStatus;//these will be private once wrapper for "repaint()" is written
	String status;
	
	
	RoadCanvas(String input, ArrayList<Car> cars){//TODO: this could very well all be re-written once actual input file
		System.out.println("Constructing Road Canvas...");//format is revealed, don't really need to document yet.
		BufferedReader infile = null;
		String line = null;
		 try {
			infile = new BufferedReader(new FileReader(input));
			line = infile.readLine();//Read in "delta" value
			delta = Integer.parseInt(line);
			line = infile.readLine();//Read in number of roads
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		status = "paused";
		
		roads = new Road[Integer.parseInt(line)];
		
		road = 0;
		
		try {//Populates global road array and "super global" car array.
			int i = 0;
			do{
				int maxEnd = 0;
				int minStart = -1;
				
				Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,
						Color.orange,Color.cyan};
				
				
				do{
					
					line = infile.readLine();
					if(line == null)
						break;
					line = line.replaceAll(" ", "");
					String[] lineElts = line.split(",");
				//	System.out.println(line);
					if(lineElts[0].equals("road")){
						//System.out.println("Road number: "+road);
						roads[road] = new Road(lineElts);
						//i = 0;
					}
					
					if(lineElts[0].equals("car")){
						Car c = new Car(lineElts,this,road);
						cars.add(c);
						roads[road].rCars.add(c);
						if(c.finish > maxEnd)
							maxEnd = c.finish;
						if(minStart == -1 || c.start < minStart)
							minStart = c.start;
						c.color = colors[i];
						i = (i+1)%7;
					}
					
				}while(!line.equals("endroad"));
				if(road < roads.length){
					roads[road].setLength(minStart,maxEnd);//Like all numbers in this loop, this is in UNITS.
					for(Car car: roads[road].rCars)
						car.normalize(this,road);
				}
				road++;
			}while(line != null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		justPaintCars = false;
		
		
		System.out.println("Road Canvas Complete!");

	}
	
	
	public void adjustSize(int width, int height){
		setSize(width,height);
		road = 0;
		for(Road r:roads){
			r.adjust(this);
			road++;
		}
	}
	
	public void paint(Graphics g){//TODO: this is likely not going to change, but it(repaint()) will get a nice wrapper function!
		if(updateStatus)
			g.drawString("Delta: "+delta+"     Status: "+status, 10,roads[0].sHeight/2);
		else{
			for(Road r:roads)
				if(justPaintCars)
					for(Car car : r.rCars)
							car.paintCar(g);
				else
					r.paintComponent(g);
				
					
			
			g.setColor(Color.black);

		    g.drawString("Delta: "+delta+"     Status: "+status, 10,roads[0].sHeight/2);
		

		}	
				

	}
	
		
	
}
