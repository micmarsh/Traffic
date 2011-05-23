package traffic;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import traffic.Traffic.MainFrame;

public class ButtonPanel extends JPanel implements MouseListener {
	MainFrame parent;
//	JButton buttons[];
	
	ButtonPanel(MainFrame m){//,JComboCheckBox combo){
		parent = m;
		GridLayout L1 = new GridLayout();
		L1.setRows(13);//get an actual number, probably from the arguments that don't yet exist
		setLayout(L1);
	//	this.add(combo);
		JButton next = new JButton("Next");
		next.addMouseListener(this);
		
		this.add(next);
		//this.add(new JButton("heh"));
	}
	
	public void adjustSize(int width,int height){
		setSize(width,height);
		revalidate();
		//TODO: possibly other stuff
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		JButton b = (JButton) arg0.getComponent();
		if(b.getText().equals("Next"))
			parent.next();
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
}
