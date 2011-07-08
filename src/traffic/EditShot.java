package traffic;

import java.util.ArrayList;

public class EditShot extends SnapShot {
	int rChange,minVchange,maxVchange,finChange,place;
	int locChange,lenChange;
	boolean created,controlled;
	Road road;
	EditShot(int[] changes, boolean[] CC, Car c,ArrayList<Car> cars,Road r) {//CC: created?, controlled?
		super(changes[1],changes[3], c);//^of (c,r), only one of these is ever going to be non-null at at time. EVER.
		rChange = changes[0];
		minVchange = changes[2];
		maxVchange = changes[4];
		finChange = changes[5];
		
		created = CC[0];
		controlled = CC[1];
		
		locChange = changes[6];
		lenChange = changes[7];
		
		road = r; 
		
		if(c != null)
			place = cars.indexOf(c);
	}

}
