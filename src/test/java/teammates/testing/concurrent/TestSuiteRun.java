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
//	CoordCourseListUITest.class,
	
//	CoordCourseViewTest.class,
	
	CoordEvaluationAddCaseSensitivityTest.class,
	CoordEvaluationAddTest.class,
	CoordEvaluationAddWithEmptyTeamNameTest.class,
	CoordEvaluationEditTest.class,
//	CoordEvaluationListTest.class,
	CoordEvaluationResultsEditTest.class,
	CoordEvaluationResultsViewTest.class,
	CoordHelpPageHTMLTest.class,
	CoordHomePageHTMLTest.class,
	CoordHomePageFunctionalityTest.class,
	
	CoordLoginTest.class,
	CoordViewResultsUITest.class,
	
//	StudentCourseJoinTest.class,
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
	StudentEvaluationSubmitTest2.class,
	StudentHelpPageHTMLTest.class,
	StudentHomePageHTMLTest.class,
	StudentHomePageFunctionalityTest.class,
	
	StudentLoginTest.class,
	StudentViewResultsUITest.class,
	
	SystemFooterTest.class,
//	SystemRemindEvaluationBeforeDeadlineTest.class,
	
	CoordTeamFormingSessionChangeStudentTeam.class,
	CoordTeamFormingSessionDeleteTest.class,
	CoordTeamFormingSessionEditTeamProfile.class,
	CoordTeamFormingSessionListTest.class,
	CoordTeamFormingSessionManageTest.class,
	CoordTeamFormingSessionAddTest.class,
	CoordTeamFormingSessionAddWithoutStudentsTest.class,

	StudentTeamFormingSessionActionsTest.class,
	StudentTeamFormingSessionEditProfilesTest.class,
	StudentTeamFormingSessionViewTest.class
	
})

public class TestSuiteRun {

	
	
}
