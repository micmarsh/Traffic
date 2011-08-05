package traffic;

import java.io.PrintStream;

public class Pair {
	String name;
	PrintStream stream;
	
	Pair(String name, PrintStream stream) {
		this.name = name;
		this.stream = stream;
	}
}