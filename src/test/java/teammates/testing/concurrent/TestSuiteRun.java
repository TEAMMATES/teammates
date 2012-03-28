package teammates.testing.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	
	CoordAddCoursePageHTMLTest.class,
	CoordCourseAddUITest.class,
	CoordCourseDeleteStudentsTest.class,
	CoordCourseDeleteUITest.class,
	CoordCourseEditStudentsTeamTest.class,
	CoordCourseEditStudentsTest.class,
	CoordCourseEnrolCaseSensitivityTest.class,
	CoordCourseEnrolStudentsUITest.class,
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
	CoordViewResultsUITest.class,
	
//	StudentCourseJoinTest.class,
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
	StudentEvaluationSubmitTest2.class,
	
	StudentLoginTest.class,
	StudentViewResultsUITest.class,

	SystemFooterTest.class,
//	SystemRemindEvaluationBeforeDeadlineTest.class,
	SystemVerifyHelpPageTest.class
	
})

public class TestSuiteRun {

	
	
}
