package traffic;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
		
		MainFrame(String title,String input){
			super(title);
			System.out.println("Contructing Main Window...");
			setSize(500, 400);
		//	JCheckBox[] checks = {new JCheckBox("foo"),new JCheckBox("bar"),new JCheckBox("bar")};
			//JComboCheckBox foo = new JComboCheckBox(checks);
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
			
			c.addListener(listener);
			
			this.addComponentListener(this);
			sizeComponents();
			
			add(c);
			add(b);
			System.out.println("Main Window Complete!");
		}
		
		private void sizeComponents(){//int cHeight,int cWidth,int pHeight,int pWidth){
			c.adjustSize(getWidth()-120,getHeight());//TODO:Add an "upause" to start of this function, 
			b.adjustSize(100,getHeight());//TODO:continued: catch Road.adjust()'s exception, pause program
		//	System.out.println(cars.size());
			for(Car car:cars)
				car.adjust(c);
			listener.updateCars();
		}

		public void next(){//TODO: this will take arguments
			
			SnapShot[] current = new SnapShot[cars.size()];
			
			int i = 0;
			
			for (Car car : cars){
					current[i] = new SnapShot(car.velocity,0,car);//TODO:going to need a better way to keep track of pos and vel changes
				//	System.out.println("Velocity: "+car.velocity);
					car.move();
					car.adjust(c);
					i++;
			}
			
			c.justPaintCars = true;
			c.repaint();
			c.justPaintCars = false;

			checkLoop(current);
			
			memory.add(current);
		}
		
		private class RoadAndInt{
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
			//int index = 0;
			for(Road r : c.roads){
				if(inIntersection != null){
					if(intTaken != null )//once there's a car in two intersections, it crashes
				;//		crash(inIntersection,intTaken,true);
					intTaken = inIntersection;
					inIntersection = null;
				}
				
				for(int i = 0; i < r.rCars.size();i++){
					if(r.rCars.get(i).start >= r.rCars.get(i).finish){
					//	System.out.println(colorName(r.rCars.get(i).color)+" car at position "+r.rCars.get(i).start+
						//		" on road "+(r.rCars.get(i).roadIndex+1) +", finish line at "+r.rCars.get(i).finish);
						toRemove.add(new RoadAndInt(i,r));
					}
					for(int j = i+1; j<r.rCars.size();j++)
						if(collision(r.rCars.get(i),r.rCars.get(j))){
			//				System.out.println("Index i: "+i+" Index j: "+j);
						//	crash(r.rCars.get(i),r.rCars.get(j),false);
						}
						
					if(r.rCars.get(i).start >= r.intLoc && r.rCars.get(i).start <= r.intLength + r.intLoc)
						inIntersection = r.rCars.get(i);
						
						
				}
				
			
			}
			//System.out.println("\n\n");
			
			RoadAndInt [] toR = new RoadAndInt[toRemove.size()];//All of this new stuff: solved old problem, created new one.
			for(int i = 0; i< toR.length;i++)
				toR[i] = toRemove.get(i);
			
			Comparator<RoadAndInt> intcomp = new IntComparator();
			Arrays.sort(toR,intcomp);
			Integer[] absoluteIndices = new Integer[toR.length];
			int j = 0;
			
			for (RoadAndInt r:toR){
			//	System.out.println(r.integer+" "+toR.length);
				int i = r.integer;
				
				Car c = r.road.rCars.get(i); 
			//	if(c.start >= c.finish){
					
					int index = cars.indexOf(c);
					array[index].changed = true;
					array[index].road = array[index].source.roadIndex;
					//array[index].source = null;
					
					//System.out.println("Lulz! "+r.rCars.get(i).finish);
				//	System.out.println("Size before: "+r.road.rCars.size());
					r.road.rCars.remove(i);
				//	System.out.println("Size after: "+r.road.rCars.size());
					//checkLoop(array);
					absoluteIndices[j] = index;
					j++;
		//		}
			}
	
			Arrays.sort(absoluteIndices);
			for(int i = absoluteIndices.length - 1; i >= 0;i--){
		//		System.out.println("Size before: "+cars.size());
				
				cars.remove(absoluteIndices[i].intValue());
			//	System.out.println("Size after: "+cars.size());
			}
			
			if(!toRemove.isEmpty())
				listener.updateCars();
		}
		
		public class IntComparator implements Comparator<RoadAndInt>{

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
				//	System.out.println("Index when it all goes to shit: "+i);
					Car car = restore[i].source;
				//	if(car == null)
					//	System.out.println("aaaaaagh!");
					car.start -= restore[i].posChange;
					car.velocity -= restore[i].velChange;
					if(restore[i].changed){
						cars.add(i,car);
						c.roads[car.roadIndex].rCars.add(car);
					}
					car.adjust(c);
				}
				c.justPaintCars = true;
				c.repaint();
				c.justPaintCars = false;
			}
		}
		
		public void reset(){
			while(!memory.isEmpty())
				rewind();
		}
		
		
		public boolean collision(Car c1, Car c2){
			int offSet = 2;
			if(c1.start > c2.start - offSet && c1.start < c2.start + offSet){
			//	System.out.println("Collision!");
				return true;
			}
			else
				return false;
		}
		
		
		public void crash(Car c1, Car c2,boolean intersection){
			//stop loop, kill listeners somehow
		//	c.crashed[0] = c1;
		//	c.crashed[1] = c2;
			String message = "";
			if(!intersection)
				message = colorName(c1.color)+" car and "+colorName(c2.color)+" car crashed on" +
				" road "+(c1.roadIndex+1)+ " at position "+c2.start+".";
			else
				message = colorName(c1.color)+" car on road "+(c1.roadIndex+1)+" and "+colorName(c2.color)+" car on " +
						"road "+(c2.roadIndex+1)+" crashed in the intersection.";
				
			int pressed=JOptionPane.showConfirmDialog(null,message+"\nDo you want to continue?",null,JOptionPane.YES_NO_OPTION);
			
			//JOptionPane.
			if (pressed==JOptionPane.YES_OPTION){
			//		c.crashed[0] = null;
			//		c.crashed[1] = null;
			}
			if(pressed == JOptionPane.NO_OPTION){
				System.exit(0);
			}
			//c.repaint();
		//	System.out.println("Crash! "+c2.color.toString()+ " "+c.crashed[1].color.toString()+" Road1: "+c1.roadIndex+" Road2: "+
			//		c2.roadIndex);
		//	c.crashed[0] = null;
		//	c.crashed[1] = null;
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			sizeComponents();			
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void main(String args[]){
		(new Traffic()).run(args);
	}
	
	public int run(String args[]){
		//System.out.println(args[0]);
		System.out.println("Initializing program...");
		MainFrame m = new MainFrame("Traffic Simulator",args[0]);//TODO:this indexing is suspicious, investigate further
	
		m.setVisible(true);
		
		System.out.println("Program loaded, starting up!");
		
		while(true){
			
			try {
				Thread.sleep(m.miliSecondsPerFrame);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			
			if(m.cars.isEmpty()){
				m.play = false;
				m.b.play.setText("Play");
			}
			
			if(m.play)
				m.next();

		}
		//TODO: main loop and all other such things
	
		return 0;
		
	}
	
	
}
