package traffic;

public class Car {
	private int position;
	private int velocity;
	private int finish;
	private int road;
	Car(String lineElts[],int r){
		road = r;
		position = Integer.parseInt(lineElts[1]);
		velocity = Integer.parseInt(lineElts[2]);
		finish = Integer.parseInt(lineElts[3]);
	}
	
	public void adjust(RoadCanvas c){
	//	System.out.println("called");
	}
	
	public int convert(int value,boolean pixelsToUnits){
		if(pixelsToUnits)
			return value/100;
		else
			return value*100;
	}
}
