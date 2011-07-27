package traffic;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Arrays;

public class SimpComp extends Controller {
	Car[] cars;
	int gamma, delta;
	int span;
	TreeMap<int[], Node> nodes;
	
	public class PosComp implements Comparator<int[]> {
		public int compare(int[] p1, int[] p2) {
			for (int i = 0; i < p1.length; i++) {
				if (p1[i] < p2[i]) return -1;
				else if (p1[i] > p2[i]) return 1;
			}
			return 0;
		}
	}
	
	public class CarComp implements Comparator<Car> {
		public int compare(Car c1, Car c2) {
			if (c1.controlled && !c2.controlled) return -1;
			else if (c2.controlled && !c1.controlled) return 1;
			if (c1.road.index < c2.road.index) return -1;
			else if (c1.road.index > c2.road.index) return 1;
			if (c1.maxVel < c2.maxVel) return -1;
			else if (c1.maxVel > c2.maxVel) return 1;
			if (c1.minVel < c2.minVel) return -1;
			else if (c1.minVel > c2.minVel) return 1;
			if (c1.finish < c2.finish) return -1;
			else if (c1.finish > c2.finish) return 1;
			return 0;
		}
	}
	
	public SimpComp(ArrayList<Car> cars, int gamma, int delta, double epsilon) {
		this.cars = new Car[cars.size()];
		for (int i = 0; i < cars.size(); i++) this.cars[i] = cars.get(i).copy();
		Arrays.sort(this.cars, new CarComp());
		this.gamma = gamma;
		this.delta = delta;
		span = 1;
		for (Car c : cars) {
			span *= (c.maxVel - c.minVel + 1);
		}
		nodes = new TreeMap<int[], Node>(new PosComp());
	}
	
	class Node {
		ArrayList<Edge> edges;
		boolean safe;
		
		public Node() {
			safe = false;
			edges = new ArrayList<Edge>(span);
		}
	}
	
	class Edge {
		int[] vel;
		int[] in, out;
		
		public Edge(int[] in, int[] vel) {
			this.in = new int[in.length];
			this.out = new int[in.length];
			this.vel = new int[vel.length];
			for (int i = 0; i < vel.length; i++) {
				this.in[i] = in[i];
				this.vel[i] = vel[i];
				this.out[i] = in[i] + vel[i];
			}
		}
	}
	
	int[] GetPos(ArrayList<Car> carAL) {
		Car[] carAR = new Car[cars.length];
		for (int i = 0; i < carAL.size(); i++) {
			carAR[i] = carAL.get(i);
		}
		CarComp cc = new CarComp();
		Arrays.sort(carAR, 0, carAL.size(), cc);
		for (int i = 0, j = 0, k = carAL.size(); i < carAL.size() || j < cars.length; i++, j++) {
			while (j < cars.length && (i >= carAL.size() || cc.compare(carAL.get(i), cars[j]) != 0)) {
				carAR[k] = cars[j];
				carAR[k].start = carAR[k].finish;
				j++;
				k++;
			}
		}
		Arrays.sort(carAR, cc);
		int[] pos = new int[carAR.length];
		for (int i = 0; i < pos.length; i++) {
			pos[i] = carAR[i].start;
		}
		return pos;
	}

	@Override
	public boolean hasSolution(ArrayList<Car> carAL) {
		int[] pos = GetPos(carAL);
		return nodes.containsKey(pos);
	}

	public String next(ArrayList<Car> carAL) {
		int[] pos = GetPos(carAL);
		Compute(pos);
		if (Terminal(pos)) return "Safe\n";
		Node node = nodes.get(pos);
		Random generator = new Random();
		if (node.safe) { // Choose a random safe control action
			int r = generator.nextInt(node.edges.size());
			Update(carAL, pos, node.edges.get(r).vel);
			return "Safe\n";
		} // Choose a random feasible control action.
		int[] vel = new int[cars.length];
		for (int i = 0; i < vel.length; i++) {
			if (pos[i] == cars[i].finish) vel[i] = 0;
			else vel[i] = cars[i].minVel + generator.nextInt(cars[i].maxVel - cars[i].minVel + 1);
		}
		Update(carAL, pos, vel);
		return "Unsafe\n";
	}
	
	// Computes the new SnapShots array. 
	void Update(ArrayList<Car> carAL, int[] pos, int[] vel) {
		CarComp cc = new CarComp();
		boolean[] done = new boolean[pos.length];
		for (int i = 0; i < done.length; i++) done[i] = false;
		for (int i = 0; i < carAL.size(); i++) {
			for (int j = 0; j < done.length; j++) {
				if (done[j]) continue;
				if (cc.compare(carAL.get(i), cars[j]) == 0 && carAL.get(i).start == pos[j]) {
					carAL.get(i).start = pos[j] + vel[j];
					carAL.get(i).velocity = vel[j];
					done[j] = true;
				}
			}
		}
	}
	
	/* Checks if the node is terminal (return true) or if I've already computed safety properties (return node.safe).
	 * If not, then it computes the set of safe control decisions and stores them in the nodes structure */
	boolean Compute(int[] pos) {
		if (Terminal(pos)) return true; // Check if this node is terminal. It is safe if it is terminal.
		if (nodes.containsKey(pos)) { // If I've already computed the safety properties of this node, return node.safe.
			Node node = nodes.get(pos);
			return node.safe;
		}
		// If I'm here, then I don't know the safety properties of this node yet.
		Node n = new Node();
		nodes.put(pos, n);
		boolean done = false;
		// First try the setting all the velocities to minVel, except for those cars that have finished.
		int[] vel = new int[pos.length];
		for (int i = 0; i < vel.length; i++) {
			if (pos[i] >= cars[i].finish) vel[i] = 0;
			else vel[i] = cars[i].minVel;
		}
		while (!done) {
			Edge e = new Edge(pos, vel);
			// If the transition doesn't cross the bad set, then recursively check the next node.
			if (SafeEdge(e)) { 
				n.edges.add(e);
				if(Compute(e.out)) n.safe = true; // A safe solution has been found which uses this transition.
				else n.edges.remove(e); // I remove unsafe transitions.
			}
			// Compute the next feasible velocity vector, if there is one.
			done = true;
			for (int i = 0; i < vel.length; i++) {
				if (vel[i] == 0) continue;
				if (vel[i] == cars[i].maxVel || pos[i] + vel[i] >= cars[i].finish) {
					vel[i] = cars[i].minVel;
					continue;
				}
				vel[i]++;
				done = false;
				break;
			}
		}
		n.edges.trimToSize();
		return n.safe;
	}
	
	// Checks if a node is terminal, in the sense that all vehicles have crossed the finish line.
	boolean Terminal(int[] pos) {
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] < cars[i].finish) {
				return false;
			}
		}
		return true;
	}
	
	// Checks if a transition avoids the bad set. 
	boolean SafeEdge(Edge e) {
		double Aij1, Aij2, Aij3, Aij4, Aij12, Aij34, Cij1, Cij2;
		for (int i = 0; i < cars.length; i++) {
			if (e.vel[i] == 0) continue;
			for (int j = i + 1; j < cars.length; j++) {
				if (e.vel[j] == 0) continue;
				if (cars[i].road.index == cars[j].road.index) {
					Cij1 = ((double)(-gamma - e.in[i] - e.in[j]))/((double)(e.vel[i] - e.vel[j]));
					Cij2 = ((double)(gamma - e.in[i] - e.in[j]))/((double)(e.vel[i] - e.vel[j]));
					if ((Cij2 > Cij1 && Cij2 > 0 && Cij1 < 1 && e.vel[i] - e.vel[j] > 0)
							|| (Cij2 < Cij1 && Cij2 < 0 && Cij1 > 1 && e.vel[i] - e.vel[j] < 0)
							|| (-gamma < e.in[i] - e.in[j] && e.in[i] - e.in[j] < gamma)) return false;
				} else {
					Aij1 = ((double)(cars[i].road.intLoc - e.in[i]))/((double)e.vel[i]);
					Aij2 = ((double)(cars[j].road.intLoc - e.in[j]))/((double)e.vel[j]);
					Aij3 = ((double)(cars[i].road.intLoc + cars[i].road.intLength - e.in[i]))/((double)e.vel[i]);
					Aij4 = ((double)(cars[j].road.intLoc + cars[j].road.intLength - e.in[j]))/((double)e.vel[j]);
					Aij12 = (Aij1 > Aij2 ? Aij1 : Aij2);
					Aij34 = (Aij3 < Aij4 ? Aij3 : Aij4);
					if (Aij34 > Aij12 && Aij34 > 0 && Aij12 < 1) return false;
				}
			}
		}
		return true;
	}
}