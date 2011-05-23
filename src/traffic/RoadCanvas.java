package traffic;

import java.awt.Canvas;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RoadCanvas extends Canvas {
	Road roads[];
	int road;
	public boolean justPaintCars;
	//Graphics g;
	
	RoadCanvas(String input, ArrayList<Car> cars){
		BufferedReader infile = null;
		String line = null;
		 try {
			infile = new BufferedReader(new FileReader(input));
			line = infile.readLine();//Read in number of roads
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		roads = new Road[Integer.parseInt(line)];
		
		road = 0;
		
		try {//Populates global road array and "super global" car array.
			
			do{
				int maxEnd = 0;
				int minStart = 0;
				
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
					}
					
					if(lineElts[0].equals("car")){
						Car c = new Car(lineElts,this,road);
						cars.add(c);
						roads[road].rCars.add(c);
						if(c.finish > maxEnd)
							maxEnd = c.finish;
						if(minStart == 0 || c.start < minStart)
							minStart = c.start;
					}
					
				}while(!line.equals("endroad"));
				if(road < roads.length)
					roads[road].setLength(minStart,maxEnd);//Like all numbers in this loop, this is in UNITS.
				road++;
			}while(line != null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		justPaintCars = false;
		/*cars.get(cars.size()-1).finish = 69;
		System.out.println(cars.get(cars.size()-1).finish);
		System.out.println(roads[roads.length-1].rCars.get(roads[roads.length-1].rCars.size()-1).finish);
		
		roads[roads.length-1].rCars.get(roads[roads.length-1].rCars.size()-1).finish = 42;
		System.out.println(cars.get(cars.size()-1).finish);
		System.out.println(roads[roads.length-1].rCars.get(roads[roads.length-1].rCars.size()-1).finish);
		
		System.out.print(roads[2].getLength());*/
	}
	
	public void adjustSize(int width, int height){
		setSize(width,height);
		road = 0;
		for(Road r:roads){
			r.adjust(this);//TODO: will take arguments eventually
			road++;
		}
		//repaint();
	}
	/*public void update(Graphics g){
		super.update(g);
		paint(g);
	}*/
	public void paint(Graphics g){
	//	System.out.println("painted!");
		for(Road r:roads){
			if(justPaintCars)
				for(Car car : r.rCars)
					car.paintCar(g);
			else
				r.paintComponent(g);
		}
	}
}
