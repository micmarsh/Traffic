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
	
	/*
	 * Contains MainFrame, the class that houses all of the other classes in the program, and runs
	 * most of the functions associated with the simulator buttons
	 */

	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;		
		ButtonPanel b;
		Menu m;
		ArrayList<Car> cars;
		ArrayList<SnapShot[]> memory;//Used to record car actions, in case of a rewind in the future
		boolean play,sim;
		int miliSecondsPerFrame;
		CanvasInterface listener;
		String lastOpened,controllerName;
		Controller con;
		
		
		
		MainFrame(String title,String input){//Most of the interesting stuff happens in the RoadCanvas constructor
			super(title);
			controllerName = input;
			configure();
			System.out.println("Contructing Main Window...");
			setSize(900, 600);
			FlowLayout L = new FlowLayout();
			L.setAlignment(L.LEFT);
			setLayout(L);
			
			cars = new ArrayList<Car>();
			memory = new ArrayList<SnapShot[]>();
			play = false;
	

			c = new RoadCanvas(lastOpened,cars,this);//"NONE" for now, will eventually remember last opened file
			b = new ButtonPanel(this,sim);
			m = new Menu(this,sim);
			
			if(controllerName.equals("SimpComp"))
				con = new SimpComp(cars,c.gamma,c.delta,c.epsilon);
			
			//TODO: add other controllers, or Class and Constructor objects
			else
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
			lastOpened = line;//filepath to current simulation's save file
			line = infile.readLine();
			if(line.equals("true"))
				sim = true;
			else
				sim = false;
			
			line = infile.readLine();
			miliSecondsPerFrame = (int)(1000/Double.parseDouble(line));
			//^Inverse of 'frames per second' field
			
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<String> files = new ArrayList<String>();
			boolean boolDebug = false;
			while((line = infile.readLine()) != null){
				if(line.equals("DEBUG")){boolDebug = true;}
				else{
					String[] optFile = line.split(" ");
					
					names.add(optFile[0]);
					
					files.add(optFile[1]);
				}
			}
			
			if(boolDebug){
				Debug.debug = new String[names.size()];
				Debug.debugfiles = new String[names.size()];
				for(int i = 0; i < names.size(); i++){
					Debug.debug[i] = names.get(i);
					Debug.debugfiles[i] = files.get(i);
				}
			}
				
			}catch(FileNotFoundException ex){
			
			}catch(IOException ex){}
		}
		
		void writeToConfig(){
			try{
				BufferedWriter  config = new BufferedWriter (new FileWriter("config"));
				config.write(lastOpened+"\n");
				config.write(sim+"\n");
				config.write(((double)1000/(double)miliSecondsPerFrame)+"\n");
				if(Debug.debug != null){
					config.write("DEBUG\n");
					for(int i = 0; i < Debug.debug.length; i++)
						config.write(Debug.debug[i]+" "+Debug.debugfiles[i]+"\n");
				}
				
				config.close();
				
				}catch(FileNotFoundException ex){
				
				}catch(IOException ex){}
		}
		
		private void sizeComponents(){
			c.adjustSize(getWidth()-135,getHeight());
			b.adjustSize(130,getHeight());
		//	for(Car car:cars)
		//		car.adjust(c);
			listener.updateCars();
		}

		public void next(){

			SnapShot[] current = new SnapShot[cars.size()];
			
			String oldStat = c.status;
			
			if(!con.hasSolution(cars))
				c.status = "Calculating";//This will display if con.next() below takes a significant amount of time to calculate
			
			String message = "";
			
			
			ArrayList<Car> oldCars = new ArrayList<Car>();
			for (Car car : cars)
					oldCars.add(car.copy());//need to record previous positions of cars so we can calculate the difference below
			
			
			message = con.next(cars);//moves cars, amoung many other things
			
			c.status = oldStat;//This will usually appear instantaneously, see note above
			
			for (int i = 0; i < cars.size(); i++){//Snapshot array recording changes to cars
				Car oldCar = oldCars.get(i);
				Car car = cars.get(i);
				current[i] = new SnapShot(car.start - oldCar.start,car.velocity - oldCar.velocity, car );
			
			}
			
			
			
			c.redraw(true,false);
			
			c.stat2ndLine = message;
			
			checkLoop(current);//check to see if any cars have crossed the finish line, deleting if so
			//^also checks to see if any cars have crashed, prompting user for input if so
			memory.add(current);
		}
		
		public class RoadAndInt{//Helps, ultimately, to remove cars from "cars" arraylist in proper order, eliminating indexing issues
			public Road road;////(continued from above)associated with removing several objects from an arraylist at once
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
				
				for(int i = 0; i < r.rCars.size();i++){
					if(r.rCars.get(i).start >= r.rCars.get(i).finish){
						toRemove.add(new RoadAndInt(i,r));//records the road and the index in the road's array
						
					}
					for(int j = i+1; j<r.rCars.size();j++)
						if(collision(r.rCars.get(i),r.rCars.get(j))){
							crash(r.rCars.get(i),r.rCars.get(j),0);//crashed if two cars are too close to each other
						}
						
					if(r.rCars.get(i).start>= r.intLoc && r.rCars.get(i).start  <= r.intLength + r.intLoc){
						if(inIntersection == null)
							inIntersection = r.rCars.get(i);//remembers if a car is in this road, so subsequent loops can compare
						else
							crash(inIntersection,r.rCars.get(i),42);//crashes if two cars are in the same intersection at once
					}
						
				}
				
				if(inIntersection != null){
					if(intTaken != null )//once there's a car in two intersections, it crashes
						crash(inIntersection,intTaken,1);
					
					intTaken = inIntersection;//"moves" car to second variable, when first variable becomes non-null again, there's an intersection crash
					inIntersection = null;
				}
				
			
			}
			
			RoadAndInt [] toR = new RoadAndInt[toRemove.size()];
			
			for(int i = 0; i< toR.length;i++)
				toR[i] = toRemove.get(i);//transfer elements so array to take advantage of java's built-in sorting function
			
			Comparator<RoadAndInt> intcomp = new IntComparator();
			Arrays.sort(toR,intcomp);//sorts the new "toRemove" array by the "integer" field
			Integer[] absoluteIndices = new Integer[toR.length];//used to store the "cars" array index of each car to be removed
			int j = 0;
			
			for (RoadAndInt r:toR){//removes each element from road's car array in reverse index order.
				int i = r.integer;
				
				Car c = r.road.rCars.get(i); 
					
					int index = cars.indexOf(c);
					array[index].deleted = true;//records the fact that this car was removed with the snapshot array
			
					r.road.rCars.remove(i);

					absoluteIndices[j] = index;//use for removal below
					j++;
		//		}
			}
	
			
			Arrays.sort(absoluteIndices);
			for(int i = absoluteIndices.length - 1; i >= 0;i--){
				cars.remove(absoluteIndices[i].intValue());//removes car from larger "cars" arraylist
			}
			
			if(!toRemove.isEmpty())
				listener.updateCars();//removes ability to click on a car, since it's been removed
		}
		
		public class IntComparator implements Comparator<RoadAndInt>{//allows Array.sort() to sort RoadAndInt objects by the "integer" field

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
			if(!memory.isEmpty()){//<-If it's empty, we're at the beginning of the program
				SnapShot[] restore = memory.remove(memory.size()-1);
				
				try{
					EditShot changes = (EditShot) restore[0];//<- This will throw its exception if we're undo a simple call to next(), jumping to code block below
					if(changes.road != null){//<- If road is not null, the EditShot refers to a road
						if(changes.created){//<- a road was created: remove the road
							c.roads.remove(changes.road);
							this.componentResized(new ComponentEvent(new Container(), 2));
						}
						else{//<- a road was modified: undo changes
							changes.road.intLoc -= changes.locChange;
							changes.road.intLength -= changes.lenChange;
							if(changes.deleted){
								c.roads.add(changes.road.index+1,changes.road);
								for(int i = changes.road.index+1; i < c.roads.size();i++)
									c.roads.get(i).index++;//Adjust indices of reads to compensate for the re-insertion
								this.componentResized(new ComponentEvent(new Container(), 2));
								c.repaint();
							}
							
						}
					}else{//"road" field was null, the Editshot refers to a car
						
						if(changes.created){//a new car was added, remove it now.
							cars.remove(changes.source);
							changes.source.road.rCars.remove(changes.source);
							changes.source.road.colors.remove(changes.source.color);
							
							MController Mcon = (MController)con;//TODO: make cars array part of the prototype?
							Mcon.cars.remove(changes.source);
							
							listener.updateCars();
						}
						else{//<-The car was only adjusted, undo modifications
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
								car.road.colors.add(car.color);
							}
						}
					}
				}catch(ClassCastException e){//<- now we're just rewinding a regular move
					
					for(int i = 0; i < restore.length;i++){
							Car car = restore[i].source;
							if(car != null){
								car.start -= restore[i].posChange;
								car.velocity -= restore[i].velChange;
								if(restore[i].deleted){
									cars.add(i,car);
									car.road.rCars.add(car);
									car.road.colors.add(car.color);
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
		
		public void toggleListeners(){//Called when custom dialog frames open/close
			m.listen = !m.listen;
			b.listen = !b.listen;
			listener.listen = !listener.listen;
		}
		
		public boolean collision(Car c1, Car c2){//checks for a collision on one road
			int offSet = c.gamma;
			if(c1.start > c2.start - offSet && c1.start < c2.start + offSet){
			//	System.out.println("Collision!");
				return true;
			}
			else
				return false;
		}
		
		
		public void crash(Car c1, Car c2,int intersection){//Displays crash alert dialog
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
		
		private String ctrlStr(boolean controlled){//converts "controlled" boolean to an appropriate string
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
		String input = "";
		try{
			input = args[0];
		}catch(Exception e){
			Constants.p("No arguments found, defaulting to MController");
		}
		
		
		
		MainFrame m = new MainFrame("Traffic Simulator",input);
	
		m.setVisible(true);
		
		System.out.println("Program loaded, starting up!");
		
		while(true){//Main loop
			
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
