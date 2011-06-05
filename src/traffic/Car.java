package traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Car {
	
	/*public class Body extends Polygon implements MouseListener{
		Body(){
			super();
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
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
		
	}*/
	
	public int start;//denoted in UNITS
	int velocity;//denoted in UNITS
	public int finish;//denoted in UNITS
	int roadIndex;
	
	private Rectangle crashRange;

	private Polygon image;
	private int[] wheelStats;// index 0: size index, 1-4: locations of (1-2) first wheel, (3-4) second wheel, all in pixels
	public Color color;
	
	public int minVel,maxVel;
	public boolean controlled;
//public boolean deleted;
	Car(String lineElts[],RoadCanvas c,int r){
		start = Integer.parseInt(lineElts[1]);
		minVel = Integer.parseInt(lineElts[2]);
		velocity = Integer.parseInt(lineElts[3]);
		maxVel = Integer.parseInt(lineElts[4]);
		finish = Integer.parseInt(lineElts[5]);
		if(lineElts[6].equals("0"))
			controlled = false;
		else
			controlled = true;
		roadIndex = r;
		image = new Polygon();
		wheelStats = new int[5];
	
//		deleted = false;
		adjust(c);
		
	}
	
	public void normalize(RoadCanvas c,int r){
		start -= c.roads[r].start;
		finish -= c.roads[r].start;
	}
	
	public Polygon getImage(){
		return image;
	}
	
	public void adjust(RoadCanvas c){
		
		int yPos = c.roads[roadIndex].sYPos;
		int ppu = c.roads[roadIndex].pixelsPerUnit;
		int offSet = 2*ppu;
		
	//	System.out.println("c.WIDTH "+offSet);
		int begin = start*ppu - offSet*3/2;
		//int end = start*c.roads[roadIndex].pixelsPerUnit + offSet;
		
		offSet /= 3;
		
		crashRange = new Rectangle((start-2)*ppu,yPos,4*ppu,offSet*2);//the '2' and the '4' are b/c 2 is the crash range on either side
//		System.out.println("c.WIDTH divided by 20, then by six: "+offSet);
		//int[] Xs = {begin,begin+offSet,begin+offSet*3/2,begin+offSet*9/2,begin+offSet*5,begin+offSet*6,
			//	begin+offSet*6,begin};
		
		int[] Xs = {begin,begin+offSet,begin+offSet*3/2,begin+offSet*3/2+4*ppu,begin+offSet*2+4*ppu,
				begin+4*ppu+offSet*3,begin+4*ppu+offSet*3,begin};
		
		wheelStats[1] = begin + offSet/2;
		wheelStats[3] = wheelStats[1]+4*ppu;
		
		
		
		offSet = c.roads[roadIndex].sHeight/3;
	//	System.out.println("yPos: "+yPos+"\noffSet: "+offSet);
		int[] Ys = {yPos+offSet,yPos+offSet,yPos,yPos,yPos+offSet,yPos+offSet,yPos+2*offSet,yPos+2*offSet};
		wheelStats[0] = offSet;
		wheelStats[2] = yPos+2*offSet;
		wheelStats[4] = yPos+2*offSet;
		image.reset();
		
	
		for(int i = 0; i<8;i++){
			image.addPoint(Xs[i], Ys[i]);
			//System.out.println("Ys[i]: "+Ys[i]);
		}
		
		
	}
	
	//public void setColor(Color c){
		//color = c;
	//}
	
	public void paintCar(Graphics g){
	//	System.out.println("Car Painted");
		g.fillPolygon(image);
		g.fillOval(wheelStats[1], wheelStats[2], wheelStats[0], wheelStats[0]);
		g.fillOval(wheelStats[3], wheelStats[4], wheelStats[0], wheelStats[0]);
		
	}
	
	public void move(){
		start += velocity;
		//System.out.println("This Car's position is: "+start);
	}
	
	
	public void paintComponent(Graphics g,int pixelsPerUnit,int offSet){
	//	System.out.println("Approximate image X position: "+image.getBounds().width);
	//	System.out.println("Approximate image Y position: "+image.getBounds().height);
		g.drawLine(finish*pixelsPerUnit + offSet, wheelStats[2]-2*wheelStats[0], finish*pixelsPerUnit+offSet, wheelStats[2]+wheelStats[0]);
		
		paintCar(g);
		
	//	g.setColor(Color.darkGray);
		//g.drawLine(start*pixelsPerUnit,wheelStats[2]-2*wheelStats[0] , start*pixelsPerUnit, wheelStats[2]+wheelStats[0]);
	//	g.fillRect(crashRange.x, crashRange.y, crashRange.width, crashRange.height);
		
		//g.fillRect(image.getBounds().x, image.getBounds().y, image.getBounds().width, image.getBounds().height);
	}
}
