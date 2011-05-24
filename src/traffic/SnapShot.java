package traffic;

public class SnapShot {
	public int posChange,velChange,road;
	boolean changed;
	Car source;//given the changing size of the snapshot array, this may not even be necessary 
	SnapShot(int pC,int vC, Car c){
		posChange = pC;
		velChange = vC;
		road = -1;
		changed = false;
		source = c;
	}
}
