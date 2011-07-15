package traffic;

import java.util.ArrayList;

abstract class Controller {
	abstract public boolean hasSolution(ArrayList<Car> cars);
	abstract public String next(ArrayList<Car> ALnext);
}