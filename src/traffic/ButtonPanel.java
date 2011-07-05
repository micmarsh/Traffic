package traffic;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Frame;
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
	Editor e;
	boolean listen;
	
	ButtonPanel(MainFrame m,boolean sim){
		System.out.println("Constructing Button Panel...");
		parent = m;
		e = new Editor(m);
		loadPanel(sim);
		
		listen = true;
		
		System.out.println("Button Panel Completed!");
	}
	
	public void loadPanel(boolean sim){
		this.removeAll();
		String[] carStrings = {"Car: ","Road #: ", "Position: ","Velocity: ","Finish: ","Selected: ","Mouse: "};
		int i = 0;
		carStats = new JLabel[carStrings.length];
		for(String s:carStrings){
			carStats[i] = new JLabel(s+"N/A");
			i++;
		}
		
		if(sim){
			GridLayout L1 = new GridLayout(14,1);
			
			
			JButton next = new JButton("Step");
			next.addMouseListener(this);
			
			JButton rewind = new JButton("Rewind");
			rewind.addMouseListener(this);
			
			play = new JButton("Play");
			play.addMouseListener(this);
			
			status = new JLabel("paused");
			status.setFont(new Font("foo", Font.ITALIC,15));
			
			
			fps = new JTextField(""+(1000/parent.miliSecondsPerFrame));
			
		
			JButton reset = new JButton("Reset");
			reset.addMouseListener(this);
			
			
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
		}else{
			GridLayout L1 = new GridLayout(10,1);
			setLayout(L1);
			
			JButton newR = new JButton("New Road");
			newR.addMouseListener(this);
			this.add(newR);
			
			JButton newC = new JButton("New Car");
		    newC.addMouseListener(this);
			this.add(newC);
			for (JLabel j:carStats)
				this.add(j);
			
			JButton undo = new JButton("Undo");
			undo.addMouseListener(this);
			this.add(undo);
			
		}
		if(parent.m != null){
			parent.m.mode.setSelected(false);
			parent.m.mode.setPopupMenuVisible(false);
		}
		revalidate();
	}
	
	public void adjustSize(int width,int height){
		setSize(width,height);
		revalidate();
		//TODO: possibly other stuff
	}
	
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	//	JButton b = (JButton) arg0.getComponent();
		if(!listen)
			return;
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
		
		if(text.equals("New Car")){
		   e.carDialog(null);
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
