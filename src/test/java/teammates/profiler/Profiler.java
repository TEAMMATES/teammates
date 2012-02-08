package teammates.profiler;

import java.util.Stack;

/**
 * Profiling class. Used to measure performance. Can use to track 
 * many snippets of code. Think of it like a stack.
 * 
 * 	Profiler.begin("Add Student to Database");
 * 	Profiler.begin("Database Initialization");
 * 	Database db = new Database();
 * 	Profiler.end();
 *  >> (stdout) [Profiler] Database Initialization - 2.0s
 *  db.add(new Student());
 *  Profiler.end();
 *  >> (stdout) [Profiler] Add Student to Database - 2.0s
 *
 */
public class Profiler {

	static Stack<ProfilerItem> stack = new Stack<ProfilerItem>();

	public static void begin(String label) {
		long startTime = System.nanoTime();
		ProfilerItem item = new ProfilerItem(label, startTime);
		stack.push(item);
	}

	public static void end() {
		long endTime = System.nanoTime();
		ProfilerItem top = stack.pop();
		double timeDiff = (endTime - top.startTime) / 1e9;

		System.out.println("[Profiler] " + top.label + " - " + timeDiff);
	}
}