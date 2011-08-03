package traffic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import traffic.Traffic.MainFrame;

class RoadCanvas extends Canvas {
	
	/*
	 * Renders roads and cars, handles file I/O
	 */
	
	ArrayList<Road> roads;
	int delta,gamma;
	double epsilon;
	private boolean justPaintCars,updateStatus;
	String status,stat2ndLine;
	MainFrame parent;
	
	RoadCanvas(String input, ArrayList<Car> cars, MainFrame m){
		
		status = "paused";
		stat2ndLine = "";
		roads = new ArrayList<Road>();
		parent = m;
		if(!input.equals("NONE")){
			System.out.println("Constructing Road Canvas...");
			BufferedReader infile = null;
			String line = null;
			 try {
				infile = new BufferedReader(new FileReader(input));
				line = infile.readLine();//Read in "delta" value
				delta = Integer.parseInt(line);
				line = infile.readLine();//Read in "gamma" value
				gamma= Integer.parseInt(line);
				line = infile.readLine();//Read in "epsilon" value
				epsilon = Double.parseDouble(line);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		
			
			int road = 0;
			
			try {//Populates global road array public and "super global" car array.
				int i = 0;
				
				int maxEnd = 0;
				int minStart = -1;//used to set screen to correct size
				do{
				
					do{
						
						line = infile.readLine();
						if(line == null)
							break;
						line = line.replaceAll(" ", "");
						String[] lineElts = line.split(",");
						
						if(lineElts[0].equals("road")){
							//System.out.println("Road number: "+road);
							roads.add(new Road(lineElts,road));
							//i = 0;
						}
						
						if(lineElts[0].equals("car")){
							Car c = new Car(lineElts,this,roads.get(road));
							cars.add(c);
							roads.get(road).rCars.add(c);
							
							if(c.finish > maxEnd)
								maxEnd = c.finish;
							if(minStart == -1 || c.start < minStart)
								minStart = c.start;
							//^When everything is initialised, the end of every road (on the screen) will
							//be the farthest finish line among all of the cars
							
							
							c.color = Constants.colors[i];
							roads.get(road).colors.add(Constants.colors[i]);
							//^Coloring is handled outside the car contructor since each road needs to keep
							//track of each color of car it contains, to ensure there are no two cars with the 
							//same color
							
							i = (i+1)%7;
						}
						
					}while(!line.equals("endroad"));
				
					road++;
				}while(line != null);
				
				for (Road r:roads){//set the length of every road, normalize the cars.
					if(minStart == -1 && maxEnd == 0)
						r.setLength(0,r.intLoc+50);//still arbitrary
					else
						r.setLength(minStart,maxEnd);
					for (Car c: r.rCars)
						c.normalize();//Initializes car's image and Subtracts the start of each road from each car's position
						//^ may not be necessary anymore, as it was originally needed for the horizontal roads
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			System.out.println("Road Canvas Complete!");
		}
		
	}
	
	public void loadCanvas(BufferedReader infile, ArrayList<Car> cars){
		String line = null;
		roads = new ArrayList<Road>();
		
		int road = 0;
		
		try {//Populates global road array public and "super global" car array.
			int i = 0;
			
			int maxEnd = 0;
			int minStart = -1;//used to set screen to correct size
			do{
			
				do{
					
					line = infile.readLine();
					if(line == null)
						break;
					line = line.replaceAll(" ", "");
					String[] lineElts = line.split(",");
					
					if(lineElts[0].equals("road")){
						//System.out.println("Road number: "+road);
						roads.add(new Road(lineElts,road));
						//i = 0;
					}
					
					if(lineElts[0].equals("car")){
						Car c = new Car(lineElts,this,roads.get(road));
						cars.add(c);
						roads.get(road).rCars.add(c);
						
						if(c.finish > maxEnd)
							maxEnd = c.finish;
						if(minStart == -1 || c.start < minStart)
							minStart = c.start;
						//^When everything is initialised, the end of every road (on the screen) will
						//be the farthest finish line among all of the cars
						
						
						c.color = Constants.colors[i];
						roads.get(road).colors.add(Constants.colors[i]);
						//^Coloring is handled outside the car contructor since each road needs to keep
						//track of each color of car it contains, to ensure there are no two cars with the 
						//same color
						
						i = (i+1)%7;
					}
					
				}while(!line.equals("endroad"));
			
				road++;
			}while(line != null);
			
			for (Road r:roads){//set the length of every road, normalize the cars.
				if(minStart == -1 && maxEnd == 0)
					r.setLength(0,r.intLoc+50);//still arbitrary
				else
					r.setLength(minStart,maxEnd);
				for (Car c: r.rCars)
					c.normalize();//Initializes car's image and Subtracts the start of each road from each car's position
					//^ may not be necessary anymore, as it was originally needed for the horizontal roads
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		adjustSize(getWidth(),getHeight());
		repaint();
	}
	
	public void redraw(boolean cars, boolean update){//used to make handling these booleans easier
		updateStatus = update;
		justPaintCars = cars;
		repaint();
		updateStatus = false;
		justPaintCars = false;
	}
	
	public void adjustSize(int width, int height){//exactly what it sounds like
		setSize(width,height);
		for(Road r:roads)
			r.adjust(this);
			
	}
	
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(-2, -2, this.getWidth()+5,this.getHeight()+5);
		if(updateStatus){//in many situations, we only need to update the status or the car's positions
			drawStatus(g);//so we use theses booleans to avoid wasting resources drawing the roads and background			
		}else{
			if(justPaintCars)
				for(Road r:roads){//^see comment above
					for(Car car : r.rCars)
							car.paintCar(g);
					r.drawFinishes(g);
							
			}else
				for(Road r:roads)
					r.paintComponent(g,true,getWidth()/2,getHeight()/2);
				for(Road r:roads){
					for(Car car : r.rCars)
						car.paintCar(g);
					r.drawFinishes(g);
				}
					
			
			
				
		//	for (Road r:roads)
		//		r.paintComponent(g,false,getWidth()/2,getHeight()/2);
				
		//	for (Road r:roads)
			//	r.drawFinishes(g);
			
			drawStatus(g);
		

		}	
				

	}
	
	private void drawStatus(Graphics g){
		g.setColor(Color.white);
		g.fillRect(10,10,500,26);
		
		
		g.setColor(Color.black);
		g.drawString("Delta: "+delta+"     Status: "+status,10,20);
		g.drawString(stat2ndLine,10,33);
		
	}
	
		
	
}
