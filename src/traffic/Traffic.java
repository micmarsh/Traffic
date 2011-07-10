package traffic;


import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.lang.reflect.Constructor;

public class Traffic {
	

	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;		
		ButtonPanel b;
		Menu m;
		ArrayList<Car> cars;
		ArrayList<SnapShot[]> memory;//Used to record car actions, in case of a rewind in the future
		boolean play,sim;
		int miliSecondsPerFrame;
		CanvasInterface listener;
		String lastOpened;
		Controller con;
		
		
		MainFrame(String title,String input){//Most of the interesting stuff happens in the RoadCanvas constructor
			super(title);
			configure();
			System.out.println("Contructing Main Window...");
			setSize(900, 600);
			FlowLayout L = new FlowLayout();
			L.setAlignment(L.LEFT);
			setLayout(L);
			
			//TODO: this will vary with command line args
			
			cars = new ArrayList<Car>();
			memory = new ArrayList<SnapShot[]>();
			play = false;
	
			c = new RoadCanvas(lastOpened,cars,this);//"NONE" for now, will eventually remember last opened file
			b = new ButtonPanel(this,sim);
			m = new Menu(this,sim);
			
			con = new MController(cars, c.gamma, c.delta);
			
			listener  = new CanvasInterface(this);
			
			c.addMouseListener(listener);
			c.addMouseMotionListener(listener);
			
			this.addComponentListener(this);
			sizeComponents();
			
			add(c);
			add(b);
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
			      public void windowClosing(WindowEvent e) {
			    	writeToConfig();
			        System.exit(0);
			      }
			});
			
			this.setJMenuBar(m);

			
			System.out.println("Main Window Complete!");
		}
		
		private void configure(){
			try{
			BufferedReader infile = new BufferedReader(new FileReader("config"));
			String line = infile.readLine();
			lastOpened = line;
			line = infile.readLine();
			if(line.equals("true"))
				sim = true;
			else
				sim = false;
			line = infile.readLine();
			miliSecondsPerFrame = (int)(1000/Double.parseDouble(line));
			}catch(FileNotFoundException ex){
			
			}catch(IOException ex){}
		}
		
		void writeToConfig(){
			try{
				BufferedWriter  config = new BufferedWriter (new FileWriter("config"));
				config.write(lastOpened+"\n");
				config.write(sim+"\n");
				config.write(((double)1000/(double)miliSecondsPerFrame)+"\n");
				config.close();
				}catch(FileNotFoundException ex){
				
				}catch(IOException ex){}
		}
		
		private void sizeComponents(){
			c.adjustSize(getWidth()-135,getHeight());//135
			b.adjustSize(130,getHeight());//130
		//	for(Car car:cars)
		//		car.adjust(c);
			listener.updateCars();
		}

		public void next(){//TODO: this will take arguments, and change accordingly
			
			SnapShot[] current = new SnapShot[cars.size()];
			if(!con.hasSolution(current))
				c.status = "Calculating";
			
			
		
			
			
			int i = 0;
			
			for (Car car : cars){
					current[i] = new SnapShot(car.velocity,0,car);//TODO:create a better way to keep track of pos and vel changes
					car.move();
			//		car.adjust(c);
					i++;
			}
			
			c.redraw(true,false);

			String message = "";
			if(memory.size() >= 1)
				message = con.next(memory.get(memory.size()-1),current);
			else
				message = con.next(null, current);//this could be an issue, must discuss
			
			c.stat2ndLine = message;
		//	checkLoop(current);
		//	listener.updateCars();
			memory.add(current);
		}
		
	/*	public class RoadAndInt{//Helps, ultimately, to remove cars from "cars" arraylist in proper order, eliminating indexing issues
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
						
					if(r.rCars.get(i).start>= r.intLoc && r.rCars.get(i).start  <= r.intLength + r.intLoc){
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
					array[index].road = array[index].source.road.index;
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
			
		}*/
		
		public void rewind(){
			if(!memory.isEmpty()){
				SnapShot[] restore = memory.remove(memory.size()-1);
				
				try{
					EditShot changes = (EditShot) restore[0];
					if(changes.road != null){//we're rolling back the changes to a road
						if(changes.created){
							c.roads.remove(changes.road);
							this.componentResized(new ComponentEvent(new Container(), 2));
						}
						else{
							changes.road.intLoc -= changes.locChange;
							changes.road.intLength -= changes.lenChange;
							if(changes.deleted){
								c.roads.add(changes.road.index,changes.road);
								this.componentResized(new ComponentEvent(new Container(), 2));
								c.repaint();
							}
							
						}
					}else{//rolling back the changes to a car
						if(changes.created){
							cars.remove(changes.source);
							changes.source.road.rCars.remove(changes.source);
							
							MController Mcon = (MController)con;//TODO: this specificity is obviously a big issue, resolve later
							Mcon.cars.remove(changes.source);
							
							listener.updateCars();
						}
						else{
							Car car = changes.source;
							car.road = c.roads.get(car.road.index - changes.rChange);
							car.minVel -= changes.minVchange;
							car.maxVel -= changes.maxVchange;
							car.finish -= changes.finChange;
							car.controlled = changes.controlled;
							car.start -= changes.posChange;
							car.velocity -= changes.velChange;
							if(changes.deleted){
								cars.add(changes.place,car);
								car.road.rCars.add(car);
							}
						}
					}
				}catch(Exception e){
					
					for(int i = 0; i < restore.length;i++){
							Car car = restore[i].source;
							if(car != null){
								car.start -= restore[i].posChange;
								car.velocity -= restore[i].velChange;
								if(restore[i].deleted){
									cars.add(i,car);
									car.road.rCars.add(car);
								}
							}
						}
				//	car.adjust(c);
				}
				
				c.redraw(true,false);
				
				listener.updateCars();
			}
		}
		
		public void reset(){
			while(!memory.isEmpty())
				rewind();
		}
		
		public void toggleListeners(){
			m.listen = !m.listen;
			b.listen = !b.listen;
			listener.listen = !listener.listen;
		}
		
		public boolean collision(Car c1, Car c2){
			int offSet = c.gamma;
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
				message = Constants.colorName(c1.color)+" car "+ctrlStr(c1.controlled)+" and "+Constants.colorName(c2.color)+" car "+ctrlStr(c2.controlled)+" crashed on" +
				" road "+(c1.road.index+1)+ " at position "+(c2.start+c2.road.start)+".";
			else if (intersection == 1)
				message = Constants.colorName(c1.color)+" car "+ctrlStr(c1.controlled)+"on road "+(c1.road.index+1)+ " and "+Constants.colorName(c2.color)+" car "+ctrlStr(c2.controlled)+" on" +
				" road "+(c2.road.index+1)+" crashed in the intersection.";
			else
				message = Constants.colorName(c1.color)+ " car "+ctrlStr(c1.controlled)+" and "+Constants.colorName(c2.color)+" car "+(c2.controlled)+" cannot both be in road "+
				(c1.road.index+1)+"'s intersection at once";
			
				
			int pressed = JOptionPane.showConfirmDialog(null,message+"\nDo you want to continue?",null,JOptionPane.YES_NO_OPTION);
			
			//JOptionPane.
			if (pressed==JOptionPane.YES_OPTION){
				if(play)
					b.mouseClicked(new MouseEvent(b.play, 0,0,0,0,0,0, false));
			}
			if(pressed == JOptionPane.NO_OPTION){
				writeToConfig();
				System.exit(0);
			}
		}
		
		private String ctrlStr(boolean controlled){
			if(controlled)
				return "(controlled)";
			else
				return "(uncontrolled)";
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
			
			if(m.cars.isEmpty() && m.b.play != null){
				m.play = false;
				m.b.play.setText("Play");
				m.c.status = "paused";
				m.miliSecondsPerFrame = 1000;
				m.c.redraw(false,true);
			}
			
			if(m.play){
				m.next();
			}
			
		}
		
		return 0;
		
	}
	
	
}
