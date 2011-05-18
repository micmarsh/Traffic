package traffic;

public class Road {
	private int intLoc;
	private int intLength;
	private int HWXY[];
	private int sHWXY[];
	Road(String lineElts[],RoadCanvas c){
		intLoc = Integer.parseInt(lineElts[1]);
		intLength = Integer.parseInt(lineElts[2]);
		HWXY = new int[4];
		sHWXY = new int[4];
		
		adjust(c);
		
	}
	
	public void adjust(RoadCanvas c){

		HWXY[0] = c.getHeight()/c.roads.length;
		HWXY[1] = c.getWidth();
		HWXY[2] = 0;
		HWXY[3] = c.road * HWXY[0];
		
		sHWXY[0] = HWXY[0]/3;
		sHWXY[1] = HWXY[1];
		sHWXY[2] = 0;
		sHWXY[3] = HWXY[3] +(HWXY[0]/3);
		
	/*	for(int i = 0; i<4;i++){//TODO: these values do need to be normalized at some point, but not now
			HWXY[i] /= 100;
			sHWXY[i] /= 100;
		}*/
	}
}


