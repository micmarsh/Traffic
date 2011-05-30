package traffic;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import traffic.Traffic.MainFrame;

public class CanvasInterface implements MouseListener {
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
			clicked.velocity = prevVel;
			
			//SnapShot[] jump = new SnapShot[1];
			int newLoc = (e.getPoint().x+ (clicked.getImage().xpoints[5]-clicked.getImage().xpoints[2]))/frame.c.roads[clicked.roadIndex].pixelsPerUnit ;
			int diff = newLoc - clicked.start;
			clicked.start = newLoc;
			
			/*
			jump[0]= new SnapShot(diff,0,clicked);
			frame.memory.add(jump);//TODO: these two lines need to be better executed
			*/
			
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
			}
			
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
					clicked = cb.car;
					prevVel = clicked.velocity;
					clicked.velocity = 0;
					prevLoc = clicked.start;
					if(frame.play){
						frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
						paused = true;
					}
					break;
				}
		}
		
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

}
