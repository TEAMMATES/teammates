package teammates.testing.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.old.TestEvaluationResultPoints;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	
	CoordAddCoursePageHTMLTest.class,
	CoordCourseAddUITest.class,
	CoordCourseDeleteStudentsTest.class,
	CoordCourseDeleteUITest.class,
	CoordCourseEditStudentsTeamTest.class,
	CoordCourseEditStudentsTest.class,
	CoordCourseEnrolCaseSensitivityTest.class,
	CoordCourseEnrolStudentsTest.class,
	CoordCourseListUITest.class,
	
//	CoordCourseViewTest.class,
	
	CoordEvaluationAddCaseSensitivityTest.class,
	CoordEvaluationAddTest.class,
	CoordEvaluationAddWithEmptyTeamNameTest.class,
	CoordEvaluationEditTest.class,
//	CoordEvaluationListTest.class,
	CoordEvaluationResultsEditTest.class,
	CoordEvaluationResultsViewTest.class,
	
	CoordLoginTest.class,
	
//	StudentCourseJoinTest.class,
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
	StudentEvaluationSubmitTest2.class,
	
	StudentLoginTest.class,

	SystemFooterTest.class,
//	SystemRemindEvaluationBeforeDeadlineTest.class,
	SystemVerifyHelpPageTest.class,
	TestEvaluationResultPoints.class
//	SystemEvaluationResultsCalculationTest.class,
	
})

public class TestSuiteRun {

	
	
}
