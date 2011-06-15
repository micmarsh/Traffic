package traffic;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Traffic {
	

	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;		
		ButtonPanel b;
		ArrayList<Car> cars;
		ArrayList<SnapShot[]> memory;//Used to record car actions, in case of a rewind in the future
		boolean play;
		int miliSecondsPerFrame;
		CanvasInterface listener;
		
		MainFrame(String title,String input){//Most of the interesting stuff happens in the RoadCanvas constructor
			super(title);
			System.out.println("Contructing Main Window...");
			setSize(500, 400);
			FlowLayout L = new FlowLayout();
			L.setAlignment(L.LEFT);
			setLayout(L);
			
			cars = new ArrayList<Car>();
			memory = new ArrayList<SnapShot[]>();
			play = false;
			miliSecondsPerFrame = 1000;
			
			
			c = new RoadCanvas(input,cars);
			b = new ButtonPanel(this);
			
			listener = new CanvasInterface(this);
			
			c.addMouseListener(listener);
			c.addMouseMotionListener(listener);
			
			this.addComponentListener(this);
			sizeComponents();
			
			add(c);
			add(b);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			
			System.out.println("Main Window Complete!");
		}
		
		private void sizeComponents(){
			c.adjustSize(getWidth()-120,getHeight());
			b.adjustSize(110,getHeight());
			for(Car car:cars)
				car.adjust(c);
			listener.updateCars();
		}

		public void next(){//TODO: this will take arguments, and change accordingly
			
			SnapShot[] current = new SnapShot[cars.size()];
			
			int i = 0;
			
			for (Car car : cars){
					current[i] = new SnapShot(car.velocity,0,car);//TODO:create a better way to keep track of pos and vel changes
					car.move();
					car.adjust(c);
					i++;
			}
			
			c.redraw(true,false);

			checkLoop(current);
			listener.updateCars();
			memory.add(current);
		}
		
		private class RoadAndInt{//Helps, ultimately, to remove cars from "cars" arraylist in proper order, eliminating indexing issues
			public Road road;
			public int integer;
			RoadAndInt(int num, Road r){
				road = r;
				integer = num;
			}
		}
		
		public void checkLoop(SnapShot[] array){
			Car inIntersection = null;
			Car intTaken = null;
			ArrayList<RoadAndInt> toRemove = new ArrayList<RoadAndInt>();
			
			for(Road r : c.roads){
				if(inIntersection != null){
					if(intTaken != null )//once there's a car in two intersections, it crashes
						crash(inIntersection,intTaken,1);
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
							crash(r.rCars.get(i),r.rCars.get(j),0);
						}
						
					if(r.rCars.get(i).start +2 >= r.intLoc && r.rCars.get(i).start - 2 <= r.intLength + r.intLoc){
						if(inIntersection == null)//the 2s above are because of the arbitrary "crash range" of 2 (units)
							inIntersection = r.rCars.get(i);
						else
							crash(inIntersection,r.rCars.get(i),42);
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
				
				Car c = r.road.rCars.get(i); 
					
					int index = cars.indexOf(c);
					array[index].changed = true;
					array[index].road = array[index].source.roadIndex;
					
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
				listener.updateCars();
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
		
		public void rewind(){
			if(!memory.isEmpty()){
				SnapShot[] restore = memory.remove(memory.size()-1);
				for(int i = 0; i < restore.length;i++){
					
					Car car = restore[i].source;
					
					car.start -= restore[i].posChange;
					car.velocity -= restore[i].velChange;
					if(restore[i].changed){
						cars.add(i,car);
						c.roads[car.roadIndex].rCars.add(car);
					}
					car.adjust(c);
				}
				
				c.redraw(true,false);
				
				listener.updateCars();
			}
		}
		
		public void reset(){
			while(!memory.isEmpty())
				rewind();
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
		
		
		public void crash(Car c1, Car c2,int intersection){
			String message = "";
			if(intersection == 0)
				message = colorName(c1.color)+" car and "+colorName(c2.color)+" car crashed on" +
				" road "+(c1.roadIndex+1)+ " at position "+c2.start+".";
			else if (intersection == 1)
				message = colorName(c1.color)+" car on road "+(c1.roadIndex+1)+" and "+colorName(c2.color)+" car on " +
						"road "+(c2.roadIndex+1)+" crashed in the intersection.";
			else
				message = colorName(c1.color)+ " car and "+colorName(c2.color)+" car cannot both be in road "+
				(c1.roadIndex+1)+"'s intersection at once";
			
				
			int pressed = JOptionPane.showConfirmDialog(null,message+"\nDo you want to continue?",null,JOptionPane.YES_NO_OPTION);
			
			//JOptionPane.
			if (pressed==JOptionPane.YES_OPTION){
				if(play)
					b.mouseClicked(new MouseEvent(b.play, 0,0,0,0,0,0, false));
			}
			if(pressed == JOptionPane.NO_OPTION){
				System.exit(0);
			}
		}
		
		public String colorName(Color c){
			if(c.equals(Color.yellow))
				return "Yellow";
			if(c.equals(Color.blue))
				return "Blue";
			if(c.equals(Color.green))
				return "Green";
			if(c.equals(Color.white))
				return "White";
			if(c.equals(Color.magenta))
				return "Pink";
		//	if(c.equals(Color.pink))
		//		return "Pink";
			if(c.equals(Color.orange))
				return "Orange";
			if(c.equals(Color.cyan))
				return "Cyan";
			return "NONE";
		}
		
		
		@Override
		public void componentHidden(ComponentEvent arg0) {
			// Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			sizeComponents();			
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// Auto-generated method stub
			
		}
	}
	
	public static void main(String args[]){
		(new Traffic()).run(args);
	}
	
	public int run(String args[]){
		//System.out.println(args[0]);
		System.out.println("Initializing program...");
		MainFrame m = new MainFrame("Traffic Simulator",args[0]);
	
		m.setVisible(true);
		
		System.out.println("Program loaded, starting up!");
		
		while(true){
			
			try {
				Thread.sleep(m.miliSecondsPerFrame);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			if(m.cars.isEmpty()){
				m.play = false;
				m.b.play.setText("Play");
				m.c.status = "paused";
				
				m.c.redraw(false,true);
			}
			
			if(m.play)
				m.next();

		}
		
		return 0;
		
	}
	
	
}
