package traffic;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Arrays;

public class CeComp extends Controller {
	
	Car[] cars;
	int gamma, delta;
	TreeMap<int[], Node> nodes;
	
	void PrintVec(int[] vec, String s, PrintStream ps) {
		ps.print(s);
		for (int i = 0; i < vec.length; i++) ps.print(" " + vec[i]);
		ps.print("\n");
	}
	
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
	
	class Node {
		ArrayList<Edge> edges;
		boolean safe;
		
		public Node() {
			safe = false;
			edges = new ArrayList<Edge>(1);
		}
	}
	
	class Edge {
		int[] vel;
		int safe;
		
		public Edge(int[] vel, int safe) {
			this.safe = safe;
			this.vel = new int[vel.length];
			for (int i = 0; i < vel.length; i++) {
				this.vel[i] = vel[i];
			}
		}
	}
	
	public CeComp(ArrayList<Car> cars, int gamma, int delta) {
		this.cars = new Car[cars.size()];
		for (int i = 0; i < cars.size(); i++) this.cars[i] = cars.get(i).copy();
		Arrays.sort(this.cars, new CarComp());
		this.gamma = gamma;
		this.delta = delta;
		nodes = new TreeMap<int[], Node>(new PosComp());
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

	@Override
	public String next(ArrayList<Car> carAL) {
		int[] pos = GetPos(carAL);
		Compute(pos);
		if (Terminal(pos)) return "Safe\n";
		Node node = nodes.get(pos);
		Random generator = new Random();
		if (node.safe) { // Choose a random safe control action
			int r = generator.nextInt(node.edges.size());
			int[] vel = node.edges.get(r).vel;
			int min, max;
			for (int i = 0; i < vel.length; i++) {
				if (vel[i] == 0) continue;
				min = Math.max(this.cars[i].minVel, vel[i] - delta);
				max = Math.min(this.cars[i].maxVel, vel[i] + delta);
				vel[i] = min + generator.nextInt(max - min + 1);
			}
			Update(carAL, pos, vel);
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
		if (Debug.debug.containsKey("PrintPos")) {
			PrintVec(pos, "Pos:", Debug.debug.get("PrintPos").stream);
		}
		if (Terminal(pos)) return true; // Check if this node is terminal. It is safe if it is terminal.
		if (nodes.containsKey(pos)) { // If I've already computed the safety properties of this node, return node.safe.
			Node node = nodes.get(pos);
			return node.safe;
		}
		// If I'm here, then I don't know the safety properties of this node yet.
		Node n = new Node();
		int[] key = new int[pos.length];
		for (int i = 0; i < pos.length; i++) key[i] = pos[i];
		nodes.put(key, n);
		boolean done = false;
		// First try the setting all the velocities to minVel, except for those cars that have finished.
		int[] vel = new int[pos.length];
		int[] out = new int[pos.length];
		for (int i = 0; i < vel.length; i++) {
			if (pos[i] >= cars[i].finish) vel[i] = 0;
			else vel[i] = cars[i].minVel;
		}
		while (!done) {
			// If the transition doesn't cross the bad set, then recursively check the next node.
			if (SafeEdge(pos, vel)) {
				for (int i = 0; i < pos.length; i++) out[i] = Math.min(pos[i] + vel[i], cars[i].finish);
				if(Compute(out)) n.edges.add(new Edge(vel, delta + 1));
				else n.edges.add(new Edge(vel, 0));
			} else n.edges.add(new Edge(vel, 0));
			// Compute the next feasible velocity vector, if there is one.
			done = true;
			for (int i = 0; i < vel.length; i++) {
				if (vel[i] == 0) continue;
				if (vel[i] == cars[i].maxVel) {
					vel[i] = cars[i].minVel;
					continue;
				}
				vel[i]++;
				done = false;
				break;
			}
		}
		int md;
		Iterator<Edge> iter1, iter2;
		Edge e1, e2;
		int[] vel2;
		iter1 = n.edges.iterator();
		while (iter1.hasNext()) {
			e1 = iter1.next();
			if (e1.safe > 0) continue;
			vel = e1.vel;
			iter2 = n.edges.iterator();
			while (iter2.hasNext()) {
				e2 = iter2.next();
				if (e2.safe == 0) continue;
				vel2 = e2.vel;
				md = 0;
				for (int k = 0; k < vel.length; k++) {
					md = Math.max(md, Math.abs(vel[k] - vel2[k]));
				}
				e2.safe = Math.min(e2.safe, md);
			}
		}
		iter1 = n.edges.iterator();
		while (iter1.hasNext()) {
			e1 = iter1.next();
			if (e1.safe <= delta) iter1.remove();
		}
		n.edges.trimToSize();
		if (n.edges.size() > 0) n.safe = true;
		if (!n.safe) n.edges = null;
		if (Debug.debug.containsKey("PrintCon")) {
			PrintStream ps = Debug.debug.get("PrintCon").stream;
			PrintVec(pos, "Pos:", ps);
			if (!n.safe) ps.print("Node Unsafe\n\n");
			else {
				ps.print("Safe Edges:\n");
				iter1 = n.edges.iterator();
				while (iter1.hasNext()) {
					PrintVec(iter1.next().vel, "Vel:", ps);
				}
				ps.print("\n");
			}
		}
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
	boolean SafeEdge(int[] in, int[] vel) {
		int Aij1, Aij2, Aij3, Aij4, Aij12, Aij34, Cij1n, Cij2n, Cijd;
		for (int i = 0; i < cars.length; i++) {
			if (vel[i] == 0) continue;
			for (int j = i + 1; j < cars.length; j++) {
				if (vel[j] == 0) continue;
				if (cars[i].road.index == cars[j].road.index) {
					Cij1n = -gamma - in[i] + in[j];
					Cij2n = gamma - in[i] + in[j];
					Cijd = vel[i] - vel[j];
					if ((Cij2n > Cij1n && Cij2n > 0 && Cij1n < Cijd && Cijd > 0)
							|| (Cij2n > Cij1n && Cij1n < 0 && Cij2n > Cijd && Cijd < 0)
							|| (Cij1n < 0 && Cij2n > 0)) return false;
				} else {
					Aij1 = (cars[i].road.intLoc - in[i])*vel[j];
					Aij2 = (cars[j].road.intLoc - in[j])*vel[i];
					Aij3 = (cars[i].road.intLoc + cars[i].road.intLength - in[i])*vel[j];
					Aij4 = (cars[j].road.intLoc + cars[j].road.intLength - in[j])*vel[i];
					Aij12 = (Aij1 > Aij2 ? Aij1 : Aij2);
					Aij34 = (Aij3 < Aij4 ? Aij3 : Aij4);
					if (Aij34 > Aij12 && Aij34 > 0 && Aij12 < vel[i]*vel[j]) return false;
				}
			}
		}
		return true;
	}
}