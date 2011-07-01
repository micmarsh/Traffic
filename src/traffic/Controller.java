package traffic;

abstract class Controller {
	abstract public boolean hasSolution(Car[] cars);
	abstract public SnapShot[] next(Car[] cars);
}