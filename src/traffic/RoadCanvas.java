package traffic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RoadCanvas extends Canvas {
	Road roads[];
	int road;
	public boolean justPaintCars;
	public Car[] crashed;
	//Graphics g;
	
	RoadCanvas(String input, ArrayList<Car> cars){
		System.out.println("Constructing Road Canvas...");
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
				
				Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,
						Color.orange,Color.cyan};
				int i = 0;
				
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
						i = 0;
					}
					
					if(lineElts[0].equals("car")){
						Car c = new Car(lineElts,this,road);
						cars.add(c);
						roads[road].rCars.add(c);
						if(c.finish > maxEnd)
							maxEnd = c.finish;
						if(minStart == 0 || c.start < minStart)
							minStart = c.start;
						c.color = colors[i];
						i = (i+1)%7;
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
		crashed = new Car[2];
		crashed[0] = null;
		crashed[1] = null;
		System.out.println("Road Canvas Complete!");
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
		
		//if(crashed[1] == null){
			for(Road r:roads){
				if(justPaintCars){
	//				System.out.println("painted!");

					for(Car car : r.rCars)
					//	if(!car.deleted)
							car.paintCar(g);
				}
				else{

					r.paintComponent(g);
				}
					
			}
		//	if(crashed[1] != null){
			//	System.out.println("String drawn?");
				//g.setColor(Color.RED);
				//g.drawString(colorName(crashed[0].color)+" car and "+colorName(crashed[1].color)+" car crashed on" +
					//	" road "+(crashed[0].roadIndex+1)+ " at position "+crashed[0].start, 10, this.getHeight()/2);
				
				
	//		}
	//	}
	//	else{
		
		//}
	}
	
	
	//Color[] colors = {Color.yellow,Color.blue,Color.green,Color.white,Color.magenta,Color.pink,
	//Color.orange,Color.cyan};
	
	
}
