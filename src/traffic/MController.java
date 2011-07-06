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
	int gamma, delta;
	ArrayList<Car> cars;
	MController(ArrayList<Car> c, int g, int d){
		cars = c;
		gamma = g;
		delta = d;
	}
	@Override
	public boolean hasSolution(SnapShot [] curSS) {
		
	/*	ArrayList<Car> cars = new ArrayList<Car>();
		
		for(Car car: cars)
			cars.add((Car) car.clone());*/
	/*	int i = 0; 
		while(true){
			//SnapShot[] current = new SnapShot[cars.size()];
			
			
			
			for (Car car : cars){
			//		current[i] = new SnapShot(car.velocity,0,car);//TODO:create a better way to keep track of pos and vel changes
					car.move();
			//		car.adjust(c);
					
			}
			
			if(!checkLoop()){
				rewind(i);
				return false;
			}
			
			boolean breakout = true;
			
			
			for (Car car : cars)
				if(!car.deleted){
					breakout = false;
					break;
				}
			
			if(breakout)
				break;
			i++;
		}
		
		rewind(i);*/
		
		return true;
		
	}

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
		
	}
	
	public void rewind(int times){
		for (int i = 0; i < times; i++)
			for(Car car : cars){
				car.start -= car.velocity;
				car.deleted = false;
			}
				
			
	}
	
	
	public boolean collision(Car c1, Car c2){
		int offSet = 2*Constants.CRASH_OFFSET;//this gives each car a 2-unit (extending from either side) "crash zone"
		if(c1.start > c2.start - offSet && c1.start < c2.start + offSet){
			//	System.out.println("Collision!");
			return true;
		}
		else
			return false;
	}
	@Override
	public String next(SnapShot[] curSS,SnapShot [] nextSS ) {
		Car inIntersection = null;
		Car intTaken = null;
		
		RoadCanvas c = cars.get(0).c;
		MainFrame parent = c.parent;
		
		ArrayList<RoadAndInt> toRemove = new ArrayList<RoadAndInt>();
	
		for(Road r : c.roads){
			if(inIntersection != null){
				if(intTaken != null )//once there's a car in two intersections, it crashes
					crash(inIntersection,intTaken,1,parent);
				intTaken = inIntersection;
				inIntersection = null;
			}
			
			for(int i = 0; i < r.rCars.size();i++){
				if(r.rCars.get(i).start >= r.rCars.get(i).finish){
					toRemove.add(new RoadAndInt(i,r));//records the road and the index in the road's array
					
					continue;
				}
				for(int j = i+1; j<r.rCars.size();j++)
					if(collision(r.rCars.get(i),r.rCars.get(j))){
						crash(r.rCars.get(i),r.rCars.get(j),0,parent);
					}
					
				if(r.rCars.get(i).start>= r.intLoc && r.rCars.get(i).start  <= r.intLength + r.intLoc){
					if(inIntersection == null)//the 2s above are because of the arbitrary "crash range" of 2 (units)
						inIntersection = r.rCars.get(i);
					else
						crash(inIntersection,r.rCars.get(i),42,parent);
				}
					
			}
			
		
		}
		
		RoadAndInt [] toR = new RoadAndInt[toRemove.size()];
		
		for(int i = 0; i< toR.length;i++)
			toR[i] = toRemove.get(i);
		
		Comparator<RoadAndInt> intcomp = new IntComparator();
		Arrays.sort(toR,intcomp);//sorts the new "toRemove" array by the "integer" field
		Integer[] absoluteIndices = new Integer[toR.length];//used to store the "cars" array index of each car to be removed
		int j = 0;
		
		for (RoadAndInt r:toR){//removes each element from road's car array in reverse index order.
			int i = r.integer;
			
			Car car = r.road.rCars.get(i); 
				
				int index = cars.indexOf(car);
				Constants.p("A whole lotta stuff: \ni: "+i+"\nColor: "+Constants.colorName(car.color)+
						"\nPosition: "+car.start+"\nVelocity: "+car.velocity+"\nFinish: "+
						car.finish);
				Constants.p("Index of car to be removed: "+index);
				nextSS[index].changed = true;
				//nextSS[index].road = nextSS[index].source.road.index;
				r.road.rCars.remove(i);

				absoluteIndices[j] = index;
				j++;
	//		}
		}

		
		Arrays.sort(absoluteIndices);
		for(int i = absoluteIndices.length - 1; i >= 0;i--){
			cars.remove(absoluteIndices[i].intValue());//Does the same as above for "cars" ArrayList
		}
		
		if(!toRemove.isEmpty())
			parent.listener.updateCars();
		return "I did it for the lulz";
	}
	
	public void crash(Car c1, Car c2,int intersection,MainFrame m){
		String message = "";
		if(intersection == 0)
			message = Constants.colorName(c1.color)+" car "+ctrlStr(c1.controlled)+" and "+Constants.colorName(c2.color)+" car "+ctrlStr(c2.controlled)+" crashed on" +
			" road "+(c1.road.index+1)+ " at position "+(c2.start+c2.road.start)+".";
		else if (intersection == 1)
			message = Constants.colorName(c1.color)+" car "+ctrlStr(c1.controlled)+"on road "+(c1.road.index+1)+ "and "+Constants.colorName(c2.color)+" car "+ctrlStr(c2.controlled)+" on" +
			" road "+(c2.road.index+1)+" crashed in the intersection.";
		else
			message = Constants.colorName(c1.color)+ " car "+ctrlStr(c1.controlled)+" and "+Constants.colorName(c2.color)+" car "+(c2.controlled)+" cannot both be in road "+
			(c1.road.index+1)+"'s intersection at once";
		
			
		int pressed = JOptionPane.showConfirmDialog(null,message+"\nDo you want to continue?",null,JOptionPane.YES_NO_OPTION);
		
		//JOptionPane.
		if (pressed==JOptionPane.YES_OPTION){
			if(m.play)
				m.b.mouseClicked(new MouseEvent(m.b.play, 0,0,0,0,0,0, false));
		}
		if(pressed == JOptionPane.NO_OPTION){
			m.writeToConfig();
			System.exit(0);
		}
	}

	private String ctrlStr(boolean controlled){
		if(controlled)
			return "(controlled)";
		else
			return "(uncontrolled)";
	}
	
	public class RoadAndInt{//Helps, ultimately, to remove cars from "cars" arraylist in proper order, eliminating indexing issues
		public Road road;
		public int integer;
		RoadAndInt(int num, Road r){
			road = r;
			integer = num;
		}
	}
	
	public class IntComparator implements Comparator<RoadAndInt>{//allows Array.sort() to sort RoadAndInt's by the "integer" 

		@Override
		public int compare(RoadAndInt arg0, RoadAndInt arg1) {
			if(arg0.integer >= arg1.integer)
				return 0;
			if(arg0.integer < arg1.integer)
				return 1;
			return -1;
		}
		
	}
	
}
