package traffic;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {
	ButtonPanel(JButton buttons[]){
		GridLayout L1 = new GridLayout();
		L1.setRows(42);//get an actual number, probably from the arguments that don't yet exist
		setLayout(L1);
	}
	
	public void adjustSize(int width,int height){
		setSize(width,height);
		//TODO: possibly other stuff
	}
}
