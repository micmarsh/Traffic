package traffic;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import traffic.Traffic.MainFrame;

public class ButtonPanel extends JPanel implements MouseListener,DocumentListener {
	MainFrame parent;
//	JButton buttons[];
	JButton play;
	JTextField fps;
	JLabel status;
	JLabel[] carStats;
	ButtonPanel(MainFrame m){//,JComboCheckBox combo){
		System.out.println("Constructing Button Panel...");
		parent = m;
		
		GridLayout L1 = new GridLayout(14,1);
		
		
	//	this.add(combo);
		JButton next = new JButton("Step");
		next.addMouseListener(this);
		
		JButton rewind = new JButton("Rewind");
		rewind.addMouseListener(this);
		
		play = new JButton("Play");
		play.addMouseListener(this);
		
		status = new JLabel("paused");
		status.setFont(new Font("foo", Font.ITALIC,15));
		
		String[] carStrings = {"Car: ","Road #: ", "Position: ","Velocity: ","Finish: ","Selected: ","Mouse: "};
		int i = 0;
		carStats = new JLabel[carStrings.length];
		for(String s:carStrings){
			carStats[i] = new JLabel(s+"N/A");
			i++;
		}
		
		fps = new JTextField("1");
		
	
		JButton reset = new JButton("Reset");
		reset.addMouseListener(this);
		
		JButton cancel = new JButton("Cancel Click");
		
		
		setLayout(L1);
		
		//this.add(new JLabel("Status: "));
		//this.add(status);
		this.add(next);
		this.add(play);
		this.add(new JLabel("FPS:"));
		this.add(fps);
		this.add(rewind);
		this.add(reset);
		for (JLabel j:carStats)
			this.add(j);
		
		
	/*	System.out.println(((JLabel) (this.getComponent(0))).getText());
		
		//SpringUtilities.makeGrid(this, 15,1, // rows, cols
		  //      0,0, // initialX, initialY
		    //    0,0);// xPad, yPad
		
		L1.putConstraint(SpringLayout.EAST, this.getComponent(0), 1, SpringLayout.EAST, this);
		L1.putConstraint(SpringLayout.NORTH, this.getComponent(0), 1, SpringLayout.NORTH, this);
		
		for(i = 0; i<this.getComponentCount()-1;i++){//TODO: this!
			L1.putConstraint(SpringLayout.NORTH,this.getComponent(i+1),1,SpringLayout.SOUTH,this.getComponent(i));
			L1.putConstraint(SpringLayout.EAST, this.getComponent(i+1), 1, SpringLayout.EAST, this);
		}*/
		
		//this.add(update); //doesn't seem may not be necesary
		//this.add(new JButton("heh"));
		System.out.println("Button Panel Completed!");
	}
	
	public void adjustSize(int width,int height){
		setSize(width,height);
		revalidate();
		//TODO: possibly other stuff
	}
	
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	//	JButton b = (JButton) arg0.getComponent();
		
		JButton b = (JButton)arg0.getComponent();
		String text = b.getText();
		
		if(text.equals("Step"))
			parent.next();
		if(text.equals("Rewind"))
			parent.rewind();
		if(text.equals("Play")){
			parent.miliSecondsPerFrame = 1000/Integer.parseInt(fps.getText());		
			parent.play = true;
			b.setText("Pause");
			parent.c.status = "playing";
			if(parent.listener.clicked != null){
				parent.b.carStats[5].setText("Selected: No");
			//	if(parent.listener.paused)
				//	parent.listener.paused = false;
			//	else if(!parent.c.status.equals("playing"))
			//		parent.c.status = "paused";
				
				parent.listener.clicked = null;
			}
		}
		if(text.equals("Pause")){
			parent.play = false;
			b.setText("Play");
			parent.c.status = "paused";
			parent.c.redraw(false,true);
		}
		if(text.equals("Reset")){
			parent.reset();
			if(play.getText().equals("Pause")){
				parent.play = false;
				play.setText("Play");
				parent.c.status = "paused";
			}
		}
	}
	
	public void updateInfo(Car c){
		status.setText(c.color.toString());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
			
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
