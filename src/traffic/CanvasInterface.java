package traffic;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.AbstractButton;

import traffic.Traffic.MainFrame;

public class CanvasInterface implements MouseListener,MouseMotionListener {
	MainFrame frame;//used to access the parent MainFrame, and, by extension, most of the rest of the program
	ArrayList<CarAndBound> cars;//helps associate the car's image with the bounding rectangle the listener will use to keep track of it's location
	private int prevLoc,prevVel;//^mostly just reduces amount of typing done
	Car clicked;//curently "clicked on" car
	boolean paused,listen;
	
	
	public class CarAndBound{//TODO: change to private in final version
		Rectangle boundary;
		Car car;
		CarAndBound(Car c){
			int [] center = c.translate();
			double startAngle = c.road.startAngle;
			Constants.polarMove(center, startAngle+Math.PI/2, Constants.ROAD_WIDTH/2);
			Constants.polarMove(center, startAngle, 25);
			
			boundary = new Rectangle(center[0]-23,center[1]-23,47,47);
			
			//Constants.p("Adjusted center: ("+center[0]+","+center[1]+")");
			
			car = c;
		}
	}
	
	CanvasInterface(MainFrame c){
		frame = c;
		cars = getCars();
		clicked = null;
		listen = true;
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
		
		if(!listen)
			return;
		if(e.getButton() == MouseEvent.BUTTON3){//cancels a "clicking on" of a car
			frame.b.carStats[5].setText("Selected: No");
			if(paused){
				frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
				paused = false;
				frame.c.status = "playing";
			}else
				frame.c.status = "paused";
			clicked = null;
			frame.m.adjCar.setEnabled(false);
		}
		else{
			if(clicked != null){
				
				frame.b.carStats[5].setText("Selected: No");
				int newLoc = this.pointToPos(e.getX(), e.getY(), clicked.road)-clicked.road.start;
				int diff = newLoc - clicked.start;
				clicked.start = newLoc;
				
				frame.b.carStats[2].setText("Position: "+(clicked.start+clicked.road.start));
				
				SnapShot[] current = new SnapShot[frame.cars.size()];
				
				int i = 0;
				
				for (Car car : frame.cars){
						if(car.equals(clicked)){
							current[i] = new SnapShot(diff,0,car);//TODO:going to need a better way to keep track of pos and vel changes
					//		car.adjust(frame.c);
						}
						else
							current[i] = new SnapShot(0,0,car);
						i++;
				}
				
				frame.c.redraw(true,false);
	
				
				
				frame.memory.add(current);	
				
				clicked = null;
				
				if(paused){
					frame.b.mouseClicked(new MouseEvent(frame.b.play, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, prevLoc, false));
					paused = false;
					frame.c.status = "playing";
				}else
					frame.c.status = "paused";
				
				updateCars();
				
				frame.c.redraw(true,false);
				frame.m.adjCar.setEnabled(false);
				
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
						frame.m.adjCar.setEnabled(true);
						frame.c.status = "car selected  (right-click to cancel)";
						break;
					}
				}
				
			}
		}
		
		frame.c.redraw(false,true);
		
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
		if(!listen)
			return;
		Point loc = arg0.getPoint();
		for(CarAndBound cb : cars)
			if(clicked == null && cb.boundary.contains(loc)){//TODO:"clicked == null" may not be needed for final version
				Car c = cb.car;
				frame.b.carStats[0].setText("Car: "+colorName(c.color));
				frame.b.carStats[1].setText("Road #: "+(c.road.index+1));
				frame.b.carStats[2].setText("Position: "+(c.start+c.road.start));
				frame.b.carStats[3].setText("Velocity: "+c.velocity);
				frame.b.carStats[4].setText("Finish: "+(c.finish+c.road.start));
				
			}
		int i = 0;
		
		double angle = Angle(loc.x,loc.y);
		if(angle < 0)
			angle += Math.PI;
		//Constants.p("Angle!: "+angle);
		
		for(Road r : frame.c.roads){
			//Constants.p("Start Angle: "+r.startAngle);
		//	Constants.p("This zone is angle "+(r.startAngle - r.theta/2)+" through "+(r.startAngle + r.theta/2));
			if(angle >= r.startAngle - r.theta/2 && angle < r.startAngle + r.theta/2){
			
				frame.b.carStats[6].setText("Mouse: "+(pointToPos(loc.x,loc.y,r)));
			}
			
			i++;
		}
	}
	
	private double Angle(int pointX, int pointY){
		double x = pointX - frame.c.getWidth()/2;
		double y = pointY - frame.c.getHeight()/2;
		return Math.atan(y/x);
	}
	
	private int pointToPos(int pointX, int pointY, Road r){
		/*int[] toPrint = new int[2];
		
		int[] point = {pointX,pointY};
		
		Constants.polarMove(point, Math.PI/2 - r.startAngle , 25);
		
		double tempY = (frame.c.getHeight()/2 - pointY)/(double)r.pixelsPerUnit;
		toPrint[0] =  (int)(r.unitCenter - tempY/Math.sin(r.startAngle));
		
		double tempX = (frame.c.getWidth()/2 - pointX)/(double)r.pixelsPerUnit;
		toPrint[1] = (int)(r.unitCenter - tempX/Math.cos(r.startAngle));
		Constants.p("The two position values!: ("+toPrint[1]+","+toPrint[0]+")");
		return toPrint[1];*/
		
	//	double arcTan = Math.atan(frame.c.getHeight()/frame.c.getWidth());
		
	/*	if(r.startAngle >= Math.PI*3/2){
			double tempY = (frame.c.getHeight()/2 - pointY)/(double)r.pixelsPerUnit;
			return (int)(r.unitCenter - tempY/Math.sin(r.startAngle));
		}else{
			double tempX = (frame.c.getWidth()/2 - pointX)/(double)r.pixelsPerUnit;
			return (int)(r.unitCenter - tempX/Math.cos(r.startAngle));
		}*//*
		
		int [] ref = new int[2];
		
	//	if(r.rCars.isEmpty()){
			ref[0] = frame.c.getWidth()/2;
			ref[1] = frame.c.getHeight()/2;
	//	}
	//	else{
	//		ref = r.rCars.get(0).translate();
	//	}
		
		double distance = Point.distance(pointX, pointY, ref[0], ref[1]);
		
			int divisor = pointX - ref[0];
		
		double deviation;
		
		
		
		if (divisor == 0)
			deviation = 0;
		else{
			deviation = Math.atan((pointY - ref[1])/divisor);
		}
		
		double pixelDist = distance ;//* Math.cos(deviation);		
		
		double unitDist = pixelDist/r.pixelsPerUnit;
		
		if(pointY <= frame.c.getHeight()/2)
			return (int)(r.unitCenter - unitDist + r.start);
		else 
			return (int)(r.unitCenter + unitDist + r.start);*/
		
		double hyp = Point.distance(pointX, pointY, frame.c.getWidth()/2,frame.c.getHeight()/2);
	//	Constants.p("Hypotenuse: "+hyp);
		int[] point = {frame.c.getWidth()/2,frame.c.getHeight()/2};
		
		int[] point1 = point.clone();
		
		Constants.polarMove(point, r.startAngle, 1000);
		Constants.polarMove(point1, Math.PI+r.startAngle, 1000);
		
		//Constants.p("Point 1: ("+point[0]+","+point[1]+") Point 2: ("+point1[0]+","+point1[1]+")");
		
		double distFromRoad = Line2D.ptLineDist(point[0], point[1], point1[0], point1[1], pointX, pointY);
	//	Constants.p("Distance from road: "+distFromRoad);
		double distance = Math.pow((Math.pow(hyp,2)-Math.pow(distFromRoad, 2)),0.5)/r.pixelsPerUnit;	
	//	Constants.p("Distance: "+distance);
		if(pointY <= frame.c.getHeight()/2)
			return (int)(r.unitCenter - distance + r.start);
		else 
			return (int)(r.unitCenter + distance + r.start);
		
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
