package teammates.testing.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.old.TestEvaluationResultPoints;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	
	CoordLoginTest.class,
	CoordCourseAddCaseSensitivityTest.class,
	CoordCourseAddTest.class,
	CoordCourseDeleteStudentsTest.class,
	CoordCourseEditStudentsTeamTest.class,
	CoordCourseEditStudentsTest.class,
	CoordCourseEnrolCaseSensitivityTest.class,
	CoordCourseEnrolStudentsTest.class,
	CoordCourseListTest.class,
//	CoordCourseViewTest.class,
	
	CoordEvaluationAddCaseSensitivityTest.class,
	CoordEvaluationAddTest.class,
	CoordEvaluationEditTest.class,
//	CoordEvaluationListTest.class,
	CoordEvaluationResultsViewTest.class,
	CoordEvaluationResultsEditTest.class,
	
	StudentLoginTest.class,
//	StudentCourseJoinTest.class,
	StudentEvaluationSubmitTest2.class,
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
		
	SystemFooterTest.class,
	SystemVerifyHelpPageTest.class,
	SystemVerifyCourseAddPageTest.class,
	TestEvaluationResultPoints.class
//	SystemEvaluationResultsCalculationTest.class,
	
})

public class TestSuiteRun {

	
	
}
