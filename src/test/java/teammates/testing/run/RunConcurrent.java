package teammates.testing.run;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import teammates.profiler.Profiler;
import teammates.testing.lib.ParallelComputer2;
import teammates.testing.lib.TMAPI;

public class RunConcurrent {

	public static void main(String args[]) {

		Profiler.begin("Runs the tests concurrently");

		Class[] cls = { 
//				TestCoordCourseConcurrent.class,
//				TestCoordDeleteStudentsConcurrent.class,
//				TestCoordEnrollStudentsConcurrent.class,
//				TestCoordEvaluationConcurrent.class,
//				TestCoordSubmitFeedbacksConcurrent.class,
//				TestStudentLogin.class,
//				TestCoordLogin.class,
		};

		// Parallel among classes
		Result r = JUnitCore.runClasses(ParallelComputer2.classes(), cls);

		if (r.getFailureCount() > 0) {
			for (Failure f : r.getFailures()) {
				System.err.println(f.toString());
			}
		}

		TMAPI.cleanup();

		Profiler.end();
		
		// Somehow when done, the program doesn't end.
	}
}
