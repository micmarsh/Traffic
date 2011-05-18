package traffic;

import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Traffic {
	
	ArrayList<Car> cars;
	JButton buttons[];
	
	public class MainFrame extends JFrame implements ComponentListener {
		RoadCanvas c;
		ButtonPanel b;
		MainFrame(String title,String input){
			super(title);
			setSize(500, 400);
			
			FlowLayout L = new FlowLayout();
			L.setAlignment(L.LEFT);
			setLayout(L);
			
			cars = new ArrayList<Car>();
			
			c = new RoadCanvas(input,cars);
			b = new ButtonPanel(buttons);//TODO:will take arguments eventually
			
			sizeComponents();
			
			add(c);
			add(b);
		}
		
		private void sizeComponents(){//int cHeight,int cWidth,int pHeight,int pWidth){
			c.adjustSize(getWidth()-120,getHeight());
			b.adjustSize(100,getHeight());
		//	System.out.println(cars.size());
			for(Car car:cars)
				car.adjust(c);
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
		
		//TODO: main loop and all other such things
		
		return 0;
		
	}
	
	
}
