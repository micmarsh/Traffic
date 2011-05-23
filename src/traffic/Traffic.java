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

public class Traffic {
	boolean play;
	
	//JButton buttons[];
	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;
		ButtonPanel b;
		ArrayList<Car> cars;
		
		MainFrame(String title,String input){
			super(title);
			setSize(500, 400);
		//	JCheckBox[] checks = {new JCheckBox("foo"),new JCheckBox("bar"),new JCheckBox("bar")};
			//JComboCheckBox foo = new JComboCheckBox(checks);
			FlowLayout L = new FlowLayout();
			L.setAlignment(L.LEFT);
			setLayout(L);
			
			cars = new ArrayList<Car>();
			
			
			
			c = new RoadCanvas(input,cars);
			b = new ButtonPanel(/*buttons,*/this);//,foo);//TODO:will take arguments eventually
			
			this.addComponentListener(this);
			sizeComponents();
			
			add(c);
			add(b);
		}
		
		private void sizeComponents(){//int cHeight,int cWidth,int pHeight,int pWidth){
			c.adjustSize(getWidth()-120,getHeight());//TODO:Add an "upause" to start of this function, 
			b.adjustSize(100,getHeight());//TODO:continued: catch Road.adjust()'s exception, pause program
		//	System.out.println(cars.size());
			for(Car car:cars)
				car.adjust(c);
		}

		public void next(){//TODO: this will take arguments
			for (Car car : cars){
				car.move();
				car.adjust(c);
			}
			c.justPaintCars = true;
			c.repaint();
			c.justPaintCars = false;
	
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
		MainFrame m = new MainFrame("Traffic Simulator",args[0]);//TODO:this indexing is suspicious, investigate further
	
		m.setVisible(true);
		//TODO: main loop and all other such things
		
		return 0;
		
	}
	
	
}
