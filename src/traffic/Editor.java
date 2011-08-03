package traffic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import traffic.Traffic.MainFrame;

public class Editor{
	/*
	 * Generates popup windows for all of the "editor" sidebar's functionality, and the listeners
	 * which execute said functions
	 */
	
	MainFrame m;
	int lastAdded;
	int colorInd;
	
	Editor(MainFrame parent){
		m = parent;
		lastAdded = 1;
		colorInd = 0;
	}
	
	public void alterCar(){}
	
	public void alterRoad(){}
	public void roadDialog(Road toEdit){
		String title;
		if(toEdit == null)
			title = "New Road";
		else
			title = "Adjust Road";
		JFrame toDisplay = new JFrame(title);
		Container contentPane = toDisplay.getContentPane();
		GridLayout layout = new GridLayout(3,2);
		contentPane.setLayout(layout);
		String curVal = "";
		
		contentPane.add(new JLabel("Intersection Location: "));
		if(toEdit != null)
			curVal = ""+(toEdit.intLoc + toEdit.start);
		else
			curVal = "";
		contentPane.add(new JTextField(""+curVal,15));
		
		contentPane.add(new JLabel("Intersection Length: "));
		if(toEdit != null)
			curVal = ""+(toEdit.intLength);
		else
			curVal = "";
		contentPane.add(new JTextField(""+curVal,15));
		
		JButton ok = new JButton("Ok");
	    JButton cancel = new JButton("Cancel");
	        
	    RoadDialogListener newRoad = new RoadDialogListener(contentPane,toEdit,toDisplay);
	        
	    ok.addActionListener(newRoad);
	    cancel.addActionListener(newRoad);
	        
	    contentPane.add(ok);
	    contentPane.add(cancel);
		
	    toDisplay.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    toDisplay.addWindowListener(newRoad);
	    
	    toDisplay.pack();
        m.toggleListeners();
        toDisplay.setVisible(true);
		
			
	}
	public void carDialog(Car toEdit){
			String title;
			if(toEdit == null)
				title = "New Car";
			else
				title = "Adjust Car";
			JFrame toDisplay = new JFrame(title);
		    Container contentPane = toDisplay.getContentPane();
		       // SpringLayout layout = new SpringLayout();
		    GridLayout layout = new GridLayout(10,2);
		    contentPane.setLayout(layout);
		    String curVal ="";
		    
		    contentPane.add(new JLabel("Road: "));
		    if(toEdit != null)
		    	curVal = ""+(toEdit.road.index+1);
		    else 
		    	curVal = ""+(lastAdded+1);
		    contentPane.add(new JTextField(""+curVal, 15));
		    
	        if(toEdit != null)
	        	curVal = ""+(toEdit.start+toEdit.road.start);
	        else 
	        	curVal = "";
	        
	        contentPane.add(new JLabel("Position: "));
	        contentPane.add(new JTextField(""+curVal,15));
	        
	        contentPane.add(new JLabel("Minimum Velocity: "));
	        if(toEdit != null)
	        	curVal = ""+toEdit.minVel;
	        else 
	        	curVal = "";
	        contentPane.add(new JTextField(""+curVal,15));
	        
	        contentPane.add(new JLabel("Current Velocity: "));
	        if(toEdit != null)
	        	curVal = ""+toEdit.velocity;
	        else 
	        	curVal = "";
	        contentPane.add(new JTextField(""+curVal,15));
	        
	        contentPane.add(new JLabel("Maximum Velocity: "));
	        if(toEdit != null)
	        	curVal = ""+toEdit.maxVel;
	        else 
	        	curVal = "";
	        contentPane.add(new JTextField(""+curVal,15));
	        
	        contentPane.add(new JLabel("Maximum Acceleration: "));
	        if(toEdit != null)
	        	curVal = ""+toEdit.maxAccel;
	        else 
	        	curVal = "";
	        contentPane.add(new JTextField(""+curVal,15));
	        	        	        	        
	        contentPane.add(new JLabel("Finish Line: "));
	        if(toEdit != null)
	        	curVal = ""+(toEdit.finish+toEdit.road.start);
	        else 
	        	curVal = "";
	        contentPane.add(new JTextField(""+curVal,15));
	        
	        contentPane.add(new JLabel("Controlled? "));
	        String[] yesNo = {"Yes","No"};
	        JComboBox box = new JComboBox(yesNo);
	        if(toEdit != null){
	        	if(toEdit.controlled)
	        		box.setSelectedItem("Yes");
	        	else
	        		box.setSelectedItem("No");
	        }
	        contentPane.add(box);
	        
	       
	        JButton ok = new JButton("Ok");
	        JButton cancel = new JButton("Cancel");
	        
	        CarDialogListener newCar = new CarDialogListener(contentPane,toEdit,toDisplay);
	        
	        toDisplay.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	        toDisplay.addWindowListener(newCar);
	        
	        ok.addActionListener(newCar);
	        cancel.addActionListener(newCar);
	        
	        contentPane.add(ok);
	        contentPane.add(cancel);
	        
	        toDisplay.pack();
	        m.toggleListeners();
	        toDisplay.setVisible(true);
	       
	}

	public void deleteDialog(){
		JFrame toDisplay = new JFrame("Delete a Car or Road");
		
		toDisplay.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
	    Container contentPane = toDisplay.getContentPane();
	       // SpringLayout layout = new SpringLayout();
	    GridLayout layout = new GridLayout(3,2);
	    contentPane.setLayout(layout);
	   
	   toDisplay.add(new JLabel("Select Road:"));
	   
	   String [] roads = new String[m.c.roads.size()];
	   
	   for (int i = 1; i<= roads.length;i++)
		   roads[i-1] = ("Road "+i);
	   
	   DeletionListener listen = new DeletionListener(contentPane,toDisplay);
	   
	   toDisplay.addWindowListener(listen);
	   
	   JComboBox roadBox = new JComboBox(roads);
	   toDisplay.add(roadBox);
	   roadBox.addActionListener(listen);
	   
	   toDisplay.add(new JLabel("Select Car:"));
	   
	  // String [] DERT = {"(Delete Entire Road)"};
	   JComboBox cars = new JComboBox();
	   String [] strings = populateCars("1");
	   for (String s:strings)
		   cars.addItem(s);
	   
	   
	   
	   toDisplay.add(cars);
	   cars.addActionListener(listen);
	   
	   JButton ok = new JButton("Ok");
	   JButton cancel = new JButton("Cancel");
	   toDisplay.add(ok);
	   toDisplay.add(cancel);
	   ok.addActionListener(listen);
	   cancel.addActionListener(listen);
	   
	   
	   
	   toDisplay.pack();
       m.toggleListeners();
       toDisplay.setVisible(true);
	}
	
	
	public void carSelector(){
		JFrame toDisplay = new JFrame("Select a Car to Adjust");
				
	    Container contentPane = toDisplay.getContentPane();
	       // SpringLayout layout = new SpringLayout();
	    GridLayout layout = new GridLayout(3,2);
	    contentPane.setLayout(layout);
	   
	   toDisplay.add(new JLabel("Select Road:"));
	   
	   String [] roads = new String[m.c.roads.size()];
	   
	   for (int i = 1; i<= roads.length;i++)
		   roads[i-1] = ("Road "+i);
	   
	   SelectionListener listen = new SelectionListener(contentPane,toDisplay);
	
	   toDisplay.addWindowListener(listen);
	   
	   JComboBox roadBox = new JComboBox(roads);
	   toDisplay.add(roadBox);
	   roadBox.addActionListener(listen);
	   
	   toDisplay.add(new JLabel("Select Car:"));
	   
	  // String [] DERT = {"(Delete Entire Road)"};
	   JComboBox cars = new JComboBox();
	   String [] strings = populateCars("1");
	   for (int i = 1; i < strings.length; i++)
		   cars.addItem(strings[i]);
	   
	   
	   
	   toDisplay.add(cars);
	   cars.addActionListener(listen);
	   
	   JButton ok = new JButton("Ok");
	   JButton cancel = new JButton("Cancel");
	   toDisplay.add(ok);
	   toDisplay.add(cancel);
	   ok.addActionListener(listen);
	   cancel.addActionListener(listen);
	   
	   toDisplay.setSize(200,400);
	   
	   toDisplay.pack();
       m.toggleListeners();
       toDisplay.setVisible(true);
	}
	
	private int[] initChanges(Object o){
			Road road;
			Car car;
			int[] startVals = new int[8];
		
			for (int i = 0; i < 8; i++)
				startVals[i] = 0;
		if(o != null){
			try{
				car = (Car)o;
				startVals[0] = car.road.index;
				startVals[1] = car.start;
				startVals[2] = car.minVel;
				startVals[3] = car.velocity;
				startVals[4] = car.maxVel;
				startVals[5] = car.finish;
			}catch(Exception e){
				road = (Road)o;
				startVals[6] = road.intLoc;
				startVals[7] = road.intLength;
			}
		}
		return startVals;
		
	}
	
	
	class CarDialogListener implements ActionListener,WindowListener{
		Container contentPane;
		Car car;
		JFrame source;
		CarDialogListener(Container c,Car automobile,JFrame f){
			contentPane = c;
			car = automobile;
			source = f;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JButton source = (JButton) arg0.getSource();
			
			int[] changes = initChanges(car);
			
			int startRoad;
			
			if(source.getText().equals("Ok")){
				int start, minVel, velocity, maxVel,finish,controlled;
				double maxAccel;
				Road road;
				String[] values = new String[8];
				try{
					
					for(int i = 1 ; i < 15;i += 2)
						values[i/2] = ((JTextField)contentPane.getComponents()[i]).getText();
					if(((JComboBox)contentPane.getComponents()[15]).getSelectedItem().equals("Yes"))
						controlled = 1;
					else
						controlled = 0;
					road = m.c.roads.get(Integer.parseInt(values[0])-1);
					start = Integer.parseInt(values[1]);
					minVel = Integer.parseInt(values[2]);
					velocity = Integer.parseInt(values[3]);
					maxVel = Integer.parseInt(values[4]);
					maxAccel = Double.parseDouble(values[5]);
					finish = Integer.parseInt(values[6]);
					
					String [] safeVals = {"",""+start,""+minVel,""+velocity,""+maxVel,""+maxAccel,""+finish,""+controlled};
					
					boolean [] bools = new boolean[2];
					
					
					
					if(car == null){
						bools[0] = true;
						Car toAdd = new Car(safeVals,m.c,road);
						toAdd.color = Constants.colors[colorInd];
						
						startRoad = -1;//so "color check" loop will trigger
						
						m.cars.add(toAdd);
						road.rCars.add(toAdd);
						
						
						//Constants.p("Index right after added: "+m.cars.indexOf(road.rCars.get(0)));
						if(toAdd.start < road.start){
							road.setLength(toAdd.start,road.finish);
						}
						if(toAdd.finish > road.finish + road.start){
							
							road.setLength(road.start,toAdd.finish +road.start);
						}
						
						lastAdded = road.index;

						toAdd.normalize();
						
						car = toAdd;
						//m.repaint();
				/*		m.componentResized(new ComponentEvent(contentPane, controlled));
						m.c.redraw(false, false);
					//	m.repaint();
						m.toggleListeners();
						this.source.dispose();*/
					}else{
						
						bools[0] = false;
						int prevRoad = car.road.index;
						startRoad = prevRoad;
						int newRoad = Integer.parseInt(values[0])-1;
						car.road = m.c.roads.get(newRoad);
						
						//Constants.p("Checkpoint 1: "+car.start);
						
						if(newRoad != prevRoad){
							car.road.rCars.add(car);
							m.c.roads.get(prevRoad).rCars.remove(car);
						}
						
						car.start = start - car.road.start;
						car.minVel = minVel;
						car.velocity = velocity;
						car.maxVel = maxVel;
						car.maxAccel = maxAccel;
						car.finish = finish - car.road.start;
						
						if(controlled == 1)
							car.controlled = true;
						else
							car.controlled = false;
						
						
						if(car.start < car.road.start){
							car.road.setLength(car.start,car.road.finish);
						}

						//Constants.p("Road start: "+car.road.start);
					
						if(car.finish  > car.road.finish){
						//	Constants.p("Hell yeah!");
						//	car.road.intLoc += car.road.start;
							car.road.setLength(car.road.start,car.finish);// +car.road.start);
						}

					//	Constants.p("Road start: "+car.road.start);
						
						
						
						
						
					}
					
					int newRoad = car.road.index;
					
					changes[0] -= car.road.index;
					changes[1] -= car.start;
					changes[2] -= car.minVel;
					changes[3] -= car.velocity;
					changes[4] -= car.maxVel;
					changes[5] -= car.finish;
					
					bools[1] = car.controlled;
					
					Color first = car.color;
					
					if(startRoad != newRoad){
						while(road.colors.contains(car.color)){
							//Constants.p("lulz");
							colorInd = (colorInd+1)%7;
							car.color = Constants.colors[colorInd];
							try{
								car.image = ImageIO.read(new File("car/"+Constants.colorName(car.color)+".png"));
								
								}catch(Exception e){
						//			Constants.p("lulzception!");
								}
							if(first.equals(car.color))
								break;
							
						}
						road.colors.add(car.color);
						if(startRoad != -1)
							m.c.roads.get(startRoad).colors.remove(car.color);
					}
					
					
					EditShot[] pretendArray = {new EditShot(changes,bools,car,m.cars,null)};
					
					m.memory.add(pretendArray);
					
					
					m.componentResized(new ComponentEvent(contentPane, controlled));
					m.c.redraw(false, false);
					m.toggleListeners();
					m.listener.mouseClicked(new MouseEvent(m,
			                  0,
			                  0,
			                  42,
			                  69,
			                  1337,
			                  3,
			                  false,
			                  MouseEvent.BUTTON3));
					//reloadCanvas();
					this.source.dispose();
				
					
				}catch(Exception e){JOptionPane.showMessageDialog(source,
					    "You have one or more errors in your input.\n" +
					    "Please make sure you're using whole numbers,\n" +
					    "and the specified road actually exists.","Input Error",2);}
				//e.printStackTrace();}
					
						
				
			}else{
				if(!m.b.listen)
					m.toggleListeners();
				this.source.dispose();
				
			}
			
		//	Constants.p("Size of car array after added: "+m.cars.size());
		}
		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowClosing(WindowEvent e) {
			if(!m.b.listen)
				m.toggleListeners();
			source.dispose();
			
		}
		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RoadDialogListener implements ActionListener,WindowListener{
		Container contentPane;
		Road road;
		JFrame frame;
		
		RoadDialogListener(Container c,Road toEdit,JFrame source){
			contentPane = c;
			road = toEdit;
			frame = source;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton source = (JButton) arg0.getSource();
			
			if(source.getText().equals("Ok")){
				int intLoc,intLen;
				
				String[] values = new String[2];
				
				boolean[] bools = {false,false};
				try{
					
					for(int i = 1 ; i < 5;i += 2)
						values[i/2] = ((JTextField)contentPane.getComponents()[i]).getText();
					
					intLoc = Integer.parseInt(values[0]);
					intLen = Integer.parseInt(values[1]);
					
					String [] safeVals = {"",""+intLoc,""+intLen};
					
					int [] changes = initChanges(road);
					
					if(road == null){
						Road toAdd = new Road(safeVals,m.c.roads.size());
					
						m.c.roads.add(toAdd);
						
						toAdd.setLength(0,intLoc + 50);//These are quite arbitrary right now
						road = toAdd;
						bools[0] = true;
						
					}else{
						road.intLoc = (Integer.parseInt(values[0])-road.start);
						road.intLength = (Integer.parseInt(values[1]));
						lastAdded = road.index;
						
						
					}
					
					changes[6] -= road.intLoc ;
					changes[7] -= road.intLength;
					
					EditShot[] pretendArray = {new EditShot(changes,bools,null,null,road)};
					
					m.memory.add(pretendArray);
					
					m.componentResized(new ComponentEvent(contentPane, 2));
					m.c.redraw(false, false);
					m.toggleListeners();
					//reloadCanvas();
					frame.dispose();
					
				}catch(Exception e){JOptionPane.showMessageDialog(frame,
					    "You have one or more errors in your input.\n" +
					    "Please make sure you're using whole numbers.\n",
					    "Input Error",2);
				}//	e.printStackTrace();}
					
						
				
			}else{
				m.toggleListeners();
				frame.dispose();
			}
			
		}
		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowClosing(WindowEvent e) {
			if(!m.b.listen)
				m.toggleListeners();
			frame.dispose();
			
		}
		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
	class SelectionListener implements ActionListener,WindowListener{

		Container contentPane;
		JFrame frame;
		
		SelectionListener(Container c, JFrame f){
			contentPane = c;
			frame = f;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try{
				JButton source = (JButton) arg0.getSource();
				
				String text = source.getText();
				
				if(text.equals("Ok")){
					Car toPass =  (Car)parseCombos((JComboBox)frame.getContentPane().getComponent(1),(JComboBox)frame.getContentPane().getComponent(3));
					m.toggleListeners();
					frame.dispose();
					carDialog(toPass);
					
				}else{
					m.toggleListeners();
					frame.dispose();
				}
			}catch(Exception e){
				JComboBox box = (JComboBox) arg0.getSource();
				
				if(box.equals(frame.getContentPane().getComponent(1))){
					JComboBox cars = (JComboBox) frame.getContentPane().getComponent(3);
					cars.removeAllItems();
					String [] strings = populateCars(""+((JComboBox) frame.getContentPane().getComponent(1)).getSelectedItem().toString().charAt(5));
					for (int i = 1; i < strings.length; i++)
						   cars.addItem(strings[i]);
					
				}
			}
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if(!m.b.listen)
				m.toggleListeners();
			frame.dispose();
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
					
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class DeletionListener implements ActionListener,WindowListener {

		Container contentPane;
		JFrame frame;
		
		DeletionListener(Container c, JFrame f){
			contentPane = c;
			frame = f;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			try{
				JButton source = (JButton) arg0.getSource();
				
				String text = source.getText();
				
				if(text.equals("Ok")){
					Object toDelete = parseCombos((JComboBox)frame.getContentPane().getComponent(1),(JComboBox)frame.getContentPane().getComponent(3));
					try{
						Car car = (Car)toDelete;
						//if(car == null)
					//		Constants.p("nullzLulz");
						SnapShot[] carDel ={ new SnapShot(0,0,car)};
						carDel[0].deleted = true;
						car.road.rCars.remove(car);
						m.cars.remove(car);
						m.memory.add(carDel);
				//		Constants.p("Car Deleted?");
						
					}catch(Exception e){
					//	e.printStackTrace();
						Road road = (Road)toDelete;
						int[] ints = {0,0,0,0,0,0,0,0};
						boolean[] bools = {false,false};
						EditShot[] roadDel = {new EditShot(ints,bools,null,null,road)};
						
						for (int i = road.index; i < m.c.roads.size(); i++)
							m.c.roads.get(i).index--;
						
						m.c.roads.remove(road);
						roadDel[0].deleted = true;
						m.memory.add(roadDel);
						
					}
					m.componentResized(new ComponentEvent(contentPane, 2));
					m.c.redraw(false, false);				
					m.toggleListeners();
					frame.dispose();
				}
				else{
					m.toggleListeners();
					frame.dispose();
				}
			}catch(Exception e){
				JComboBox box = (JComboBox) arg0.getSource();
				
				if(box.equals(frame.getContentPane().getComponent(1))){
					JComboBox cars = (JComboBox) frame.getContentPane().getComponent(3);
					cars.removeAllItems();
					String [] strings = populateCars(""+((JComboBox) frame.getContentPane().getComponent(1)).getSelectedItem().toString().charAt(5));
					   for (String s:strings)
						   cars.addItem(s);
					
				}
			}
			
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			if(!m.b.listen)
				m.toggleListeners();
			frame.dispose();
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method s  tub
			
		}
		
	}
	
	private String [] populateCars(String roadStr){
			int road = Integer.parseInt(roadStr);
			Road roadObj = m.c.roads.get(road-1);
			String[] toRet = new String[roadObj.rCars.size()+1];
			
			toRet[0] = "(Delete Entire Road)";
			for(int i = 1; i < toRet.length; i++)
				toRet[i]  = Constants.colorName(roadObj.rCars.get(i-1).color);
			
			return toRet;
		
	}
	
	public Object parseCombos(JComboBox roads, JComboBox cars){
		char roadChar = roads.getSelectedItem().toString().charAt(5);
		int roadInt = Integer.parseInt(""+roadChar);
		String carString = cars.getSelectedItem().toString();
		
		Road road = m.c.roads.get(roadInt-1);
		
		if(carString.charAt(0) == '('){
			return road;
		}else{
			Color toComp = Constants.nameToColor(carString);
			
			for( Car c: road.rCars)
				if(c.color.equals(toComp))
					return c;
		}
		return null;
	}
	
	
}
