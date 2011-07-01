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

class RoadCanvas extends Canvas {
	ArrayList<Road> roads;
	int delta,gamma;
	private boolean justPaintCars,updateStatus;
	String status;
	
	RoadCanvas(String input, ArrayList<Car> cars){//TODO: this could very well all be re-written once actual input file
		
		status = "paused";
		roads = new ArrayList<Road>();
		
		if(!input.equals("NONE")){
			System.out.println("Constructing Road Canvas...");//format is revealed, don't really need to document yet.
			BufferedReader infile = null;
			String line = null;
			 try {
				infile = new BufferedReader(new FileReader(input));
				line = infile.readLine();//Read in "delta" value
				delta = Integer.parseInt(line);
				line = infile.readLine();//Read in "gamma" value
				gamma= Integer.parseInt(line);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		
			
			
			
			int road = 0;
			
			try {//Populates global road array public and "super global" car array.
				int i = 0;
				Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,
						Color.orange,Color.cyan};
				int maxEnd = 0;
				int minStart = -1;
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
							c.color = colors[i];
							i = (i+1)%7;
						}
						
					}while(!line.equals("endroad"));
					/*if(road < roads.size()){
						roads.get(road).setLength(minStart,maxEnd);//Like all numbers in this loop, this is in UNITS.
						for(Car car: roads.get(road).rCars)
							car.normalize();
					}*/
					road++;
				}while(line != null);
				
				for (Road r:roads){
					r.setLength(minStart,maxEnd);
					for (Car c: r.rCars)
						c.normalize();
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
		
		 
				try {
					line = infile.readLine();
					delta = Integer.parseInt(line);
					line = infile.readLine();
					gamma = Integer.parseInt(line);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//Read in "delta" value
				
				
		
		try {//Populates global road array public and "super global" car array.
			int i = 0;
			Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,
					Color.orange,Color.cyan};//this will probably change, since the user may be able to customize colors
			int maxEnd = 0;
			int minStart = -1;
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
						c.color = colors[i];
						i = (i+1)%7;
					}
					
				}while(!line.equals("endroad"));
				/*if(road < roads.size()){
					roads.get(road).setLength(minStart,maxEnd);//Like all numbers in this loop, this is in UNITS.
					for(Car car: roads.get(road).rCars)
						car.normalize();
				}*/
				road++;
			}while(line != null);
			
			for (Road r:roads){
				r.setLength(minStart,maxEnd);
				for (Car c: r.rCars)
					c.normalize();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		repaint();
		adjustSize(getWidth(),getHeight());
		
	}
	
	public void redraw(boolean cars, boolean update){
		updateStatus = update;
		justPaintCars = cars;
		repaint();
		updateStatus = false;
		justPaintCars = false;
	}
	
	public void adjustSize(int width, int height){
		setSize(width,height);
		for(Road r:roads)
			r.adjust(this);
			
	}
	
	public void paint(Graphics g){//TODO: this is likely not going to change, but it(repaint()) will get a nice wrapper function!
		g.setColor(Color.white);
		g.fillRect(-2, -2, this.getWidth()+5,this.getHeight()+5);
		if(updateStatus){
			g.fillRect(10,10,500,13);
			g.setColor(Color.black);
			g.drawString("Delta: "+delta+"     Status: "+status,10,20);
			
		}else{
			for(Road r:roads)
				if(justPaintCars)
					for(Car car : r.rCars)
							car.paintCar(g);
				else
					r.paintComponent(g,true,getWidth()/2,getHeight()/2);
				
			for (Road r:roads)
				r.paintComponent(g,false,getWidth()/2,getHeight()/2);
				
			for (Road r:roads)
				r.drawFinishes(g);
			
			g.setColor(Color.white);
			g.fillRect(10,10,500,13);
			
			g.setColor(Color.black);
		    g.drawString("Delta: "+delta+"     Status: "+status, 10,20);
		

		}	
				

	}
	
		
	
}
