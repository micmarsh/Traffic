package traffic;

abstract class Controller {
	abstract public boolean hasSolution(SnapShot [] curSS);
	abstract public String next(SnapShot[] curSS,SnapShot[] nextSS);
}