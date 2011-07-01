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
import javax.swing.JRadioButton;

import java.io.FileNotFoundException;
import java.io.IOException;


import traffic.Traffic.MainFrame;
public class Menu extends JMenuBar implements ActionListener {
	JMenuBar mainBar;
	JMenu file,mode,adjust;
	JMenuItem load,save,adjCar,adjRoad;
	JRadioButton sim,edit;
	MainFrame parent;
	Menu(MainFrame m,boolean simulator){
		parent = m;
		mainBar = new JMenuBar();
		
		//configure and add 'File' menu:
		file = new JMenu("File");
		load = new JMenuItem("Load");
		load.addActionListener(this);
		save = new JMenuItem("Save");
		save.addActionListener(this);
		
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
		
	//	submenu.add(new JRadioButtonMenuItem("Another one"));
	//	submenu.add(new JCheckBoxMenuItem("A check box menu item"));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = null;
		try{
			source = (JMenuItem)e.getSource();
		}catch(ClassCastException ex){
			source = (JRadioButton)e.getSource();
		}
		String command = (String)((AbstractButton) source).getText();
		if(command.equals("Load")){
			JFileChooser fc = new JFileChooser();
			 
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
			JFileChooser fc = new JFileChooser();
			if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
				try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(fc.getSelectedFile().getAbsolutePath()));
				saveFile(writer);
				writer.close();
				}catch(IOException ex){}
			}
			 
		}
		if(command.equals("Simulator")){
			parent.sim = true;
			adjRoad.setEnabled(false);
		//	Constants.p("Sim, muthafuckaaaaaaa!!!!!");
			parent.b.loadPanel(true);
		}
		if(command.equals("Editor")){
			parent.sim = false;
			adjRoad.setEnabled(true);
		//	Constants.p("Edit, muthafuckaaaaaaa!!!!!");
			if(parent.play)
				parent.b.mouseClicked(new MouseEvent(parent.b.play, 0,0,0,0,0,0,false));
			parent.b.loadPanel(false);
			
				
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
		
		toWrite += parent.c.delta + "\n";
		
		for (Road r:parent.c.roads){
			toWrite += "road,"+r.intLoc+","+r.intLength+"\n";
			for(Car car:r.rCars)
				toWrite += "car,"+car.start+","+car.minVel+","+car.velocity+","+car.maxVel
				+","+car.finish+","+boolToInt(car.controlled)+"\n";
			toWrite += "endroad"+"\n";
		}
		try{
		writer.write(toWrite);
		}catch(IOException ex){}
	}

}
