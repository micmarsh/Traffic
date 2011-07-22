package traffic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


import traffic.CanvasInterface.CarAndBound;
import traffic.Traffic.MainFrame;
public class Menu extends JMenuBar implements ActionListener {
	JMenuBar mainBar;
	JMenu file,mode,adjust;
	JMenuItem newC,load,save,adjCar,adjRoad;
	JRadioButton sim,edit;
	MainFrame parent;
	Editor e;
	boolean listen;
	
	Menu(MainFrame m,boolean simulator){
		parent = m;
		mainBar = new JMenuBar();
		e = parent.b.e;
		//configure and add 'File' menu:
		file = new JMenu("File");
		newC = new JMenuItem("New");
		newC.addActionListener(this);
		load = new JMenuItem("Load");
		load.addActionListener(this);
		save = new JMenuItem("Save");
		save.addActionListener(this);
		file.add(newC);
		file.add(load);
		file.add(save);
		add(file);
		
		//configure and add 'Mode' menu:
		mode = new JMenu("Mode");
		sim = new JRadioButton("Simulator");
		sim.addActionListener(this);
		edit = new JRadioButton("Editor");
		edit.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(sim);
		group.add(edit);
		
		mode.add(sim);
		mode.add(edit);
		add(mode);
		
		//Add 'Adjust Car' and 'Adjust Road' buttons
		adjCar = new JMenuItem("Adjust Car");
		adjCar.addActionListener(this);
		adjCar.setEnabled(false);
		adjRoad = new JMenuItem("Adjust Road");
		adjRoad.addActionListener(this);
		add(adjCar);
		add(adjRoad);
		
		//adjust things to match previous configuratoin
		sim.setSelected(simulator);
		edit.setSelected(!simulator);
		adjRoad.setEnabled(!simulator);
		
		listen = true;
	//	submenu.add(new JRadioButtonMenuItem("Another one"));
	//	submenu.add(new JCheckBoxMenuItem("A check box menu item"));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!listen)
			return;
		Object source = null;
		try{
			source = (JMenuItem)e.getSource();
		}catch(ClassCastException ex){
			source = (JRadioButton)e.getSource();
		}
		String command = (String)((AbstractButton) source).getText();
		if(command.equals("New")){
			int n = JOptionPane.showConfirmDialog(
				    this,
				    "If you create a new canvas, you will"+
				    "\nlose any unsaved changes to this one."+
				    "\nAre you sure you want to continue?",
				    "Continue?",
				    JOptionPane.YES_NO_OPTION);
			if(n == JOptionPane.YES_OPTION){
				wipe();
			}
		}
		if(command.equals("Load")){
			JFileChooser fc = new JFileChooser(parent.lastOpened);
			 
			 if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				 try{
				 String filePath = fc.getSelectedFile().getAbsolutePath();
				 BufferedReader reader = new BufferedReader(new FileReader(filePath));
				 parent.c.loadCanvas(reader,parent.cars);
				 parent.lastOpened = filePath;
				 }
				 catch(FileNotFoundException ex){}
				 
				 
			 }
			
		}
		if(command.equals("Save")){
			JFileChooser fc = new JFileChooser(parent.lastOpened);
			if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
				String filePath = fc.getSelectedFile().getAbsolutePath();
				try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
				saveFile(writer);
				writer.close();
				parent.lastOpened = filePath;
				}catch(IOException ex){}
			}
			 
		}
		if(command.equals("Simulator")){
			
			
			
			parent.sim = true;
			adjRoad.setEnabled(false);
			parent.b.loadPanel(true);
		}
		if(command.equals("Editor")){
			parent.sim = false;
			adjRoad.setEnabled(true);
			if(parent.play)
				parent.b.mouseClicked(new MouseEvent(parent.b.play, 0,0,0,0,0,0,false));
			parent.b.loadPanel(false);
			
				
		}
		
		if(command.equals("Adjust Car")){
			parent.m.e.carDialog(parent.listener.clicked);
		}
		
		if(command.equals("Adjust Road")){
			Object[] possibilities = new Object[parent.c.roads.size()];
			for (int i = 1; i <= possibilities.length; i++)
				possibilities[i-1] = ("Road "+i);
			
			String s = (String)JOptionPane.showInputDialog(
			                    parent,
			                    "Choose a road to edit:",
			                    "Customized Dialog",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    null);
			
			if(s != null){
				int roadIndex = Integer.parseInt(s.substring(5, s.length()))-1;
				
				parent.m.e.roadDialog(parent.c.roads.get(roadIndex));
			}
			
		}
		
	}
	
	
	private int boolToInt(boolean bool){
		if(bool)
			return 1;
		else
			return 0;
	}
	
	public void saveFile(BufferedWriter writer){
		String toWrite = "";
		
		toWrite += parent.c.delta + "\n"+parent.c.gamma+"\n";
		
		for (Road r:parent.c.roads){
			toWrite += "road,"+r.intLoc+","+r.intLength+"\n";
			for(Car car:r.rCars)
				toWrite += " car,"+car.start+","+car.minVel+","+car.velocity+","+car.maxVel
				+","+car.finish+","+boolToInt(car.controlled)+"\n";
			toWrite += "endroad"+"\n";
		}
		try{
		writer.write(toWrite);
		}catch(IOException ex){}
	}
	
	public void wipe(){
		parent.cars = new ArrayList<Car>();
		parent.memory = new ArrayList<SnapShot[]>();
		parent.c.roads = new ArrayList<Road>();
		parent.listener.cars = new ArrayList<CarAndBound>();
		parent.memory = new ArrayList<SnapShot[]>();
		parent.con = new MController(parent.cars,parent.c.gamma,parent.c.delta);//TODO: this will most def change once actual gamma and delta changability is implemented
		//also TODO^:make this choose the appropriate controller
		parent.play = false;
		parent.miliSecondsPerFrame = 1000;
		parent.c.adjustSize(parent.c.getWidth(), parent.c.getHeight());
		parent.c.redraw(false,false);
	}

}
