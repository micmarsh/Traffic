package traffic;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Traffic {
	boolean play;
	
	//JButton buttons[];
	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;
		ButtonPanel b;
		ArrayList<Car> cars;
		ArrayList<SnapShot[]> memory;
		
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
			
			
			c = new RoadCanvas(input,cars);
			b = new ButtonPanel(/*buttons,*/this);//,foo);//TODO:will take arguments eventually
			
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
		}

		public void next(){//TODO: this will take arguments
			
			SnapShot[] current = new SnapShot[cars.size()];
			
			int i = 0;
			
			for (Car car : cars){
					current[i] = new SnapShot(car.velocity,0,car);//going to need a better way to keep track of pos and vel changes
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
		
		public void rewind(){
			if(!memory.isEmpty()){
				SnapShot[] restore = memory.remove(memory.size()-1);
				for(int i = 0; i < restore.length;i++){
					System.out.println("Index when it all goes to shit: "+i);
					Car car = restore[i].source;
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
		
		public void checkLoop(SnapShot[] array){//NOTE: a car finishes before it would crash, 
			Car inIntersection = null;//if both of those things were to happen at once
			Car intTaken = null;
			//int index = 0;
			for(Road r : c.roads){
				for(int i = 0; i < r.rCars.size();i++){
						Car c = r.rCars.get(i); 
						if(c.start >= c.finish){
							
							int index = cars.indexOf(c);
							array[index].changed = true;
							array[index].road = array[index].source.roadIndex;
							array[index].source = null;
							
							//System.out.println("Lulz! "+r.rCars.get(i).finish);
							r.rCars.remove(i);
							checkLoop(array);
							cars.remove(c);
							return;
						}
						if(r.rCars.get(i).start >= r.intLoc && r.rCars.get(i).start <= r.intLength + r.intLoc)
							inIntersection = r.rCars.get(i);
						
						for(int j = i+1; j<r.rCars.size();j++)
							if(collision(r.rCars.get(i),r.rCars.get(j))){
								System.out.println("Index i: "+i+" Index j: "+j);
						//		crash(r.rCars.get(i),r.rCars.get(j),false);
							}
				}
				
				if(inIntersection != null){
					if(intTaken != null )
						crash(intTaken,inIntersection,true);
					intTaken = inIntersection;
					inIntersection = null;
				}
			}
			
			
		}
		
		public boolean collision(Car c1, Car c2){
			int offSet = 2;
			if(c1.start > c2.start - offSet && c1.start < c2.start + offSet){
				System.out.println("Collision!");
				return true;
			}
			else
				return false;
		}
		
		public void crash(){
			//this needs some serious fixage
			System.out.println("It crashed!");
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
		//TODO: main loop and all other such things
		System.out.println("Program loaded, starting up!");
		return 0;
		
	}
	
	
}
