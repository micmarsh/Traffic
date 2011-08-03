package traffic;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JOptionPane;

import traffic.Traffic.MainFrame;
//import traffic.Traffic.MainFrame.IntComparator;
//import traffic.Traffic.MainFrame.RoadAndInt;


public class MController extends Controller {
	
	/*
	 * My placeholder controller that doesn't really actually do anything 
	 */
	
	int gamma, delta;
	ArrayList<Car> cars;
	MController(ArrayList<Car> c, int g, int d){
		cars = c;
		gamma = g;
		delta = d;
	}
	@Override
	public boolean hasSolution(ArrayList<Car> cars) {

		
		return false;//true;
		
	}
/*
	public boolean checkLoop(){//TODO: big changes, most likely undoing all of this
		Car inIntersection = null;
		Car intTaken = null;
		
	
		
		for(Road r : cars.get(0).c.roads){
			if(inIntersection != null){
				if(intTaken != null )//once there's a car in two intersections, it crashes
					return false;
				intTaken = inIntersection;
				inIntersection = null;
			}
			
			for(int i = 0; i < r.rCars.size();i++){
				if(r.rCars.get(i).start >= r.rCars.get(i).finish){
					r.rCars.get(i).deleted = true;
					continue;
				}
				for(int j = i+1; j<r.rCars.size();j++)
					if(!r.rCars.get(i).deleted && !r.rCars.get(j).deleted && collision(r.rCars.get(i),r.rCars.get(j))){
						return false;
					}
					
				if(!r.rCars.get(i).deleted && r.rCars.get(i).start +2 >= r.intLoc && r.rCars.get(i).start - 2 <= r.intLength + r.intLoc){
					if(inIntersection == null)//the 2s above are because of the arbitrary "crash range" of 2 (units)
						inIntersection = r.rCars.get(i);
					else
						return false;
				}
					
			}
			
		
		}
		
		
		return true;
		
	}*/
	
	public void rewind(int times){
		for (int i = 0; i < times; i++)
			for(Car car : cars){
				car.start -= car.velocity;
				car.deleted = false;
			}
				
			
	}
	
	/*
	public boolean collision(Car c1, Car c2){
		int offSet = 2*Constants.CRASH_OFFSET;//this gives each car a 2-unit (extending from either side) "crash zone"
		if(c1.start > c2.start - offSet && c1.start < c2.start + offSet){
			//	System.out.println("Collision!");
			return true;
		}
		else
			return false;
	}*/
	@Override
	public String next(ArrayList<Car> ALnext)  {
	
		
		for (Car car: cars)
			car.move();
		return "I did it for the lulz";
	}
	
	
	
}
