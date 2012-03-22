package teammates.testing.run;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import teammates.profiler.Profiler;
import teammates.testing.concurrent.CoordCourseAddCaseSensitivityTest;
import teammates.testing.concurrent.CoordCourseAddUITest;
import teammates.testing.concurrent.CoordCourseDeleteStudentsTest;
import teammates.testing.concurrent.CoordCourseEditStudentsTest;
import teammates.testing.concurrent.CoordCourseEnrolCaseSensitivityTest;
import teammates.testing.concurrent.CoordCourseEnrolStudentsTest;
import teammates.testing.concurrent.CoordCourseDeleteUITest;
import teammates.testing.concurrent.CoordEvaluationAddCaseSensitivityTest;
import teammates.testing.concurrent.CoordEvaluationAddTest;
import teammates.testing.concurrent.CoordEvaluationEditTest;
import teammates.testing.concurrent.CoordEvaluationResultsEditTest;
import teammates.testing.concurrent.CoordEvaluationResultsViewTest;
import teammates.testing.concurrent.CoordLoginTest;
import teammates.testing.concurrent.StudentLoginTest;
import teammates.testing.concurrent.SystemFooterTest;
import teammates.testing.lib.ParallelComputer2;
import teammates.testing.lib.TMAPI;

public class ConcurrentRun {
	public static void main(String args[]) {

		Profiler.begin("========== Start: Runs the tests concurrently ==========");

		Class[] cls = { 
				CoordLoginTest.class,
				CoordCourseAddCaseSensitivityTest.class,
				CoordCourseAddUITest.class,
				CoordCourseDeleteStudentsTest.class,
//				CoordCourseEditStudentsTeamTest.class,
				CoordCourseEditStudentsTest.class,
				CoordCourseEnrolCaseSensitivityTest.class,
				CoordCourseEnrolStudentsTest.class,
				CoordCourseDeleteUITest.class,
//				CoordCourseViewTest.class,
				
				CoordEvaluationAddCaseSensitivityTest.class,
				CoordEvaluationAddTest.class,
				CoordEvaluationEditTest.class,
//				CoordEvaluationIndividualSubmissionTest.class,
//				CoordEvaluationListTest.class,
				CoordEvaluationResultsViewTest.class,
//				CoordEvaluationResultsSortTest.class,
				CoordEvaluationResultsEditTest.class,
				
				StudentLoginTest.class,
//				StudentCourseJoinTest.class,
//				StudentCourseListTest.class,
//				StudentEvaluationSubmitTest.class,
//				StudentEvaluationEditTest.class,
//				StudentEvaluationResultsTest.class,
				
				SystemFooterTest.class,
//				SystemEvaluationResultsCalculationTest.class,
				
		};

		// Parallel among classes
		Result r = JUnitCore.runClasses(ParallelComputer2.classes(), cls);

		if (r.getFailureCount() > 0) {
			for (Failure f : r.getFailures()) {
				
				System.err.println("Failure Message: " + f.toString());
			}
		}
		
		TMAPI.cleanup();

		Profiler.end();
	}
}
