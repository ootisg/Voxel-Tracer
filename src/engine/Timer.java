package engine;

public class Timer {
	
	private static long startTime;
	private static long totalTime;
	
	public static void reset () {
		totalTime = 0;
	}
	
	public static void start () {
		startTime = System.nanoTime ();
	}
	
	public static void lap () {
		totalTime += System.nanoTime () - startTime;
	}
	
	public static void printTime () {
		new NullPointerException ().printStackTrace ();
		System.out.println (totalTime);
	}
}
