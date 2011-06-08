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
	MainFrame frame;//used to access the parent MainFrame, and, by extension, most of the rest of the program
	ArrayList<CarAndBound> cars;//helps associate the car's image with the bounding rectangle the listener will use to keep track of it's location
	private int prevLoc,prevVel;//^mostly just reduces amount of typing done
	Car clicked;//curently "clicked on" car
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
	
	private ArrayList<CarAndBound> getCars(){//used to generate/update the CarAndBound array list
		ArrayList<CarAndBound> c = new ArrayList<CarAndBound>();
		
		for (Road r:frame.c.roads){
			
			for(int i = r.rCars.size()-1;i >=0; i--)//reverse order due to issues with clicking on multiple cars in
				c.add(new CarAndBound(r.rCars.get(i)));//similar locations at one point
			
		}
		return c;
	}
	
	public void updateCars(){
		cars = getCars();
	}

	@Override
	public void mouseClicked(MouseEvent e) {//TODO: documentation of this WHEN: "tomorrow"
		if(e.getButton() == MouseEvent.BUTTON3){//cancels a "clicking on" of a car
			frame.b.carStats[5].setText("Selected: No");
			if(paused){
				frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
				paused = false;
				frame.c.status = "playing";
			}else
				frame.c.status = "paused";
			clicked = null;
		}
		else{
			if(clicked != null){
				
				frame.b.carStats[5].setText("Selected: No");
				int newLoc = (e.getPoint().x)/frame.c.roads[clicked.roadIndex].pixelsPerUnit ;
				int diff = newLoc - clicked.start;
				clicked.start = newLoc;
				
				frame.b.carStats[2].setText("Position: "+(clicked.start+frame.c.roads[clicked.roadIndex].start));
				
				SnapShot[] current = new SnapShot[frame.cars.size()];
				
				int i = 0;
				
				for (Car car : frame.cars){
						if(car.equals(clicked)){
							current[i] = new SnapShot(diff,0,car);//TODO:going to need a better way to keep track of pos and vel changes
							car.adjust(frame.c);
						}
						else
							current[i] = new SnapShot(0,0,car);
						i++;
				}
				
				frame.c.justPaintCars = true;
				frame.c.repaint();//TODO: replace with that wrapper you're going to write
				frame.c.justPaintCars = false;
	
				
				
				frame.memory.add(current);	
				
				clicked = null;
				
				if(paused){
					frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
					paused = false;
					frame.c.status = "playing";
				}else
					frame.c.status = "paused";
				
				updateCars();
				
				frame.c.justPaintCars = true;
				frame.repaint();//TODO: replace with that wrapper you're going to write
				frame.c.justPaintCars = false;
				
			}else{
	
				Point loc = e.getPoint();
			//	System.out.println(""+loc.x);
				for(int i = cars.size()-1; i >= 0; i--){
					CarAndBound cb = cars.get(i);
					if(cb.boundary.contains(loc)){
					//	System.out.println("*******CLICKED IS NULL********");
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
						frame.c.status = "car selected  (right-click to cancel)";
						break;
					}
				}
				
			}
		}
		
		frame.c.updateStatus = true;
		frame.c.repaint();
		frame.c.updateStatus = false;
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
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
		int i = 0;
		for(Road r : frame.c.roads){
			if(loc.y >= r.yPos && loc.y < r.yPos + r.height)
				frame.b.carStats[6].setText("Mouse: "+(r.start + loc.x/frame.c.roads[i].pixelsPerUnit));
			
			i++;
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
