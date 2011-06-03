package traffic;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import traffic.Traffic.MainFrame;

public class CanvasInterface implements MouseListener,MouseMotionListener {
	MainFrame frame;
	ArrayList<CarAndBound> cars;
	private int prevLoc,prevVel;
	Car clicked;
	boolean paused;
	
	private class CarAndBound{
		Rectangle boundary;
		Car car;
		CarAndBound(Car c){
			boundary = c.getImage().getBounds();
			car = c;
		}
	}
	
	CanvasInterface(MainFrame c){
		frame = c;
		cars = getCars();
		clicked = null;
		
	}
	
	private ArrayList<CarAndBound> getCars(){
		ArrayList<CarAndBound> c = new ArrayList<CarAndBound>();
		
		for (Road r:frame.c.roads)
			for(Car car: r.rCars)
				c.add(new CarAndBound(car));
		return c;
	}
	
	public void updateCars(){
		cars = getCars();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("teh mouse was clicked!");
		
		if(clicked != null){
			System.out.println("*******CLICKED IS NOT NULL*******\n");
		//	foundACar = true;
		//	clicked.velocity = prevVel;
			
			//SnapShot[] jump = new SnapShot[1];
			frame.b.carStats[5].setText("Selected: No");
			int newLoc = (e.getPoint().x)/frame.c.roads[clicked.roadIndex].pixelsPerUnit ;
			int diff = newLoc - clicked.start;
			clicked.start = newLoc;
			
			
			
			SnapShot[] current = new SnapShot[frame.cars.size()];
			
			int i = 0;
			
			for (Car car : frame.cars){
					if(car.equals(clicked)){
						current[i] = new SnapShot(diff,0,car);//TODO:going to need a better way to keep track of pos and vel changes
						car.adjust(frame.c);
					}
					else
						current[i] = new SnapShot(0,0,car);
					//	System.out.println("Velocity: "+car.velocity);
				//	car.move();
					
					i++;
			}
			
			frame.c.justPaintCars = true;
			frame.c.repaint();
			frame.c.justPaintCars = false;

			//checkLoop(current);
			
			frame.memory.add(current);	
			
			//clicked.adjust(frame.c);
			
			clicked = null;
			
			if(paused){
				frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
				paused = false;
				frame.b.status.setText("playing");
			}else
				frame.b.status.setText("paused");
			
			updateCars();
			
			frame.c.justPaintCars = true;
			frame.repaint();
			frame.c.justPaintCars = false;
			
		}else{

			Point loc = e.getPoint();
			System.out.println(""+loc.x);
			for(CarAndBound cb : cars)
				if(cb.boundary.contains(loc)){
					System.out.println("*******CLICKED IS NULL********");
			//		foundACar = true;
					clicked = cb.car;
				//	prevVel = clicked.velocity;
					//clicked.velocity = 0;
					prevLoc = clicked.start;
					if(frame.play){
						frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
						paused = true;
						
					}
					frame.b.carStats[5].setText("Selected: Yes");
					break;
				}
			frame.b.status.setText("car selected");
		}
	//	if(!foundACar)
		//	mouseClicked(e);
		
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
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//String[] carStrings = {"Car: ","Road #: ", "Position: ","Velocity: ","Finish: "};
		Point loc = arg0.getPoint();
		for(CarAndBound cb : cars)
			if(clicked == null && cb.boundary.contains(loc)){//TODO:"clicked == null" may not be needed for final version
				Car c = cb.car;
				frame.b.carStats[0].setText("Car: "+colorName(c.color));
				frame.b.carStats[1].setText("Road #: "+(c.roadIndex+1));
				frame.b.carStats[2].setText("Position: "+(c.start+frame.c.roads[c.roadIndex].start));
				frame.b.carStats[3].setText("Velocity: "+c.velocity);
				frame.b.carStats[4].setText("Finish: "+(c.finish+frame.c.roads[c.roadIndex].start));
				
			}
	}
	
	public String colorName(Color c){
		if(c.equals(Color.yellow))
			return "Yellow";
		if(c.equals(Color.blue))
			return "Blue";
		if(c.equals(Color.green))
			return "Green";
		if(c.equals(Color.white))
			return "White";
		if(c.equals(Color.magenta))
			return "Pink";
	//	if(c.equals(Color.pink))
	//		return "Pink";
		if(c.equals(Color.orange))
			return "Orange";
		if(c.equals(Color.cyan))
			return "Cyan";
		return "NONE";
	}

}
