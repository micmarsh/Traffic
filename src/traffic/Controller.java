package traffic;

import java.util.ArrayList;

abstract class Controller {
	/*
	 * Subclass of all controllers used with this program
	 */
	abstract public boolean hasSolution(ArrayList<Car> cars);
	abstract public String next(ArrayList<Car> ALnext);
}