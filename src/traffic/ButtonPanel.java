package traffic;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import traffic.Traffic.MainFrame;

public class ButtonPanel extends JPanel implements MouseListener,DocumentListener {
	MainFrame parent;
//	JButton buttons[];
	JButton play;
	JTextField fps;
	ButtonPanel(MainFrame m){//,JComboCheckBox combo){
		System.out.println("Constructing Button Panel...");
		parent = m;
		GridLayout L1 = new GridLayout();
		L1.setRows(13);//get an actual number, probably from the arguments that don't yet exist
		setLayout(L1);
	//	this.add(combo);
		JButton next = new JButton("Step");
		next.addMouseListener(this);
		
		JButton rewind = new JButton("Rewind");
		rewind.addMouseListener(this);
		
		play = new JButton("Play");
		play.addMouseListener(this);
		
		fps = new JTextField("1");
		
		JButton update = new JButton("Update");
		update.addMouseListener(this);
		
		JButton reset = new JButton("Reset");
		reset.addMouseListener(this);
		
		this.add(next);
		this.add(play);
		this.add(new JLabel("FPS:"));
		this.add(fps);
		this.add(rewind);
		this.add(reset);
		
		
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
		}
		if(text.equals("Pause")){
			parent.play = false;
			b.setText("Play");
		}
		//if(text.equals("Update")){
			//parent.miliSecondsPerFrame = 1000/Integer.parseInt(fps.getText());		
		//}
		if(text.equals("Reset")){
			parent.reset();
		}
		//parent.sizeComponents();//TODO: this is obviously the worst way to do this, still lots of work/optimization to be done
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
