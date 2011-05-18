package traffic;

import java.awt.Canvas;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RoadCanvas extends Canvas {
	Road roads[];
	int road;
	
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
				do{
					
					line = infile.readLine();
					if(line == null)
						break;
					line = line.replaceAll(" ", "");
					String[] lineElts = line.split(",");
				//	System.out.println(line);
					if(lineElts[0].equals("road"))
						roads[road] = new Road(lineElts,this);
					
					if(lineElts[0].equals("car"))
						cars.add(new Car(lineElts,road));
						
					
				}while(!line.equals("endroad"));
				road++;
			}while(line != null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void adjustSize(int width, int height){
		setSize(width,height);
		for(Road r:roads){
			r.adjust(this);//TODO: will take arguments eventually
		}
	}
}
