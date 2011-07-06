package traffic;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import traffic.Traffic.MainFrame;

public class Editor{
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
	        
	    ActionListener newCar = new RoadDialogListener(contentPane,toEdit,toDisplay);
	        
	    ok.addActionListener(newCar);
	    cancel.addActionListener(newCar);
	        
	    contentPane.add(ok);
	    contentPane.add(cancel);
		
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
		    GridLayout layout = new GridLayout(8,2);
		    contentPane.setLayout(layout);
		    String curVal ="";
		    
		    contentPane.add(new JLabel("Road: "));
		    if(toEdit != null)
		    	curVal = ""+(toEdit.road.index+1);
		    else 
		    	curVal = ""+(lastAdded+1);
		    contentPane.add(new JTextField(""+curVal, 15));
		    
	        if(toEdit != null)
	        	curVal = ""+toEdit.start;
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
	        
	        contentPane.add(new JLabel("Finish Line: "));
	        if(toEdit != null)
	        	curVal = ""+toEdit.finish;
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
	        
	        ActionListener newCar = new CarDialogListener(contentPane,toEdit,toDisplay);
	        
	        ok.addActionListener(newCar);
	        cancel.addActionListener(newCar);
	        
	        contentPane.add(ok);
	        contentPane.add(cancel);
	        
	        
	        
	     //   setCarConstraints(layout,contentPane);

	        toDisplay.pack();
	        m.toggleListeners();
	        toDisplay.setVisible(true);
	        
	        
		
	}

	
	
	
	class CarDialogListener implements ActionListener{
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
			
			if(source.getText().equals("Ok")){
				int start, minVel, velocity, maxVel,finish,controlled;
				Road road;
				String[] values = new String[7];
				try{
					
					for(int i = 1 ; i < 13;i += 2)
						values[i/2] = ((JTextField)contentPane.getComponents()[i]).getText();
					if(((JComboBox)contentPane.getComponents()[13]).getSelectedItem().equals("Yes"))
						controlled = 1;
					else
						controlled = 0;
					road = m.c.roads.get(Integer.parseInt(values[0])-1);
					start = Integer.parseInt(values[1]);
					minVel = Integer.parseInt(values[2]);
					velocity = Integer.parseInt(values[3]);
					maxVel = Integer.parseInt(values[4]);
					finish = Integer.parseInt(values[5]);
					
					String [] safeVals = {"",""+start,""+minVel,""+velocity,""+maxVel,""+finish,""+controlled};
					
					if(car == null){
						Car toAdd = new Car(safeVals,m.c,road);
						toAdd.color = Constants.colors[colorInd];
						
						colorInd = (colorInd+1)%7;
						
						m.cars.add(toAdd);
						road.rCars.add(toAdd);
						
						
						Constants.p("Index right after added: "+m.cars.indexOf(road.rCars.get(0)));
						if(toAdd.start < road.start){
							road.setLength(toAdd.start,road.finish);
						}
						if(toAdd.finish > road.finish + road.start){
							road.setLength(road.start,toAdd.finish +road.start);
						}
						
						lastAdded = road.index;

						toAdd.normalize();
						
						
						//m.repaint();
				/*		m.componentResized(new ComponentEvent(contentPane, controlled));
						m.c.redraw(false, false);
					//	m.repaint();
						m.toggleListeners();
						this.source.dispose();*/
					}else{
						car.road = m.c.roads.get(Integer.parseInt(values[0])-1);
						car.start = start;
						car.minVel = minVel;
						car.velocity = velocity;
						car.maxVel = maxVel;
						car.finish = finish;
						if(controlled == 1)
							car.controlled = true;
						else
							car.controlled = false;
						
						
						
					}
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
					this.source.dispose();
					
				}catch(Exception e){JOptionPane.showMessageDialog(this.source,
					    "You have one or more errors in your input.\n" +
					    "Please make sure you're using whole numbers,\n" +
					    "and the specified road actually exists.","Input Error",2);}
					
						
				
			}else{
				m.toggleListeners();
				this.source.dispose();
				
			}
			
			Constants.p("Size of car array after added: "+m.cars.size());
		}
		
	}
	
	class RoadDialogListener implements ActionListener{
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
				try{
					
					for(int i = 1 ; i < 5;i += 2)
						values[i/2] = ((JTextField)contentPane.getComponents()[i]).getText();
					
					intLoc = Integer.parseInt(values[0]);
					intLen = Integer.parseInt(values[1]);
					
					String [] safeVals = {"",""+intLoc,""+intLen};
					
					if(road == null){
						Road toAdd = new Road(safeVals,m.c.roads.size());
					
						m.c.roads.add(toAdd);
						
						toAdd.setLength(0,intLoc + 50);//These are quit arbitrary right now
						
						

					
						
						
						//m.repaint();
				/*		m.componentResized(new ComponentEvent(contentPane, controlled));
						m.c.redraw(false, false);
					//	m.repaint();
						m.toggleListeners();
						this.source.dispose();*/
					}else{
						road.intLoc = (Integer.parseInt(values[0])-road.start);
						road.intLength = (Integer.parseInt(values[1]));
						lastAdded = road.index;
						
						
					}
					m.componentResized(new ComponentEvent(contentPane, 2));
					m.c.redraw(false, false);
					m.toggleListeners();
					frame.dispose();
					
				}catch(Exception e){JOptionPane.showMessageDialog(frame,
					    "You have one or more errors in your input.\n" +
					    "Please make sure you're using whole numbers.\n",
					    "Input Error",2);
						e.printStackTrace();}
					
						
				
			}else{
				m.toggleListeners();
				frame.dispose();
			}
			
		}
	
	}
	
}
