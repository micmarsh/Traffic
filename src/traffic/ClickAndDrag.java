package traffic;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ClickAndDrag implements MouseListener {//Will actually handle more than just clicking and dragging.
	RoadCanvas canvas;
	ArrayList<Rectangle> cars;
	
	
	ClickAndDrag(RoadCanvas c){
		canvas = c;
		cars = getCars();
		
	}
	
	private ArrayList<Rectangle> getCars(){
		ArrayList<Rectangle> c = new ArrayList<Rectangle>();
		
		for (Road r:canvas.roads)
			for(Car car: r.rCars)
				c.add(car.getImage().getBounds());
		return c;
	}
	
	public void updateCars(){
		cars = getCars();
		System.out.println("Foo bar");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
