package traffic;

public class SnapShot {//Records changes in position, velocity, and whether or not the car finished in a given "turn
	public int posChange,velChange,road;
	boolean changed;
	Car source;
	SnapShot(int pC,int vC, Car c){
		posChange = pC;
		velChange = vC;
		road = -1;
		changed = false;
		source = c;
	}
}
