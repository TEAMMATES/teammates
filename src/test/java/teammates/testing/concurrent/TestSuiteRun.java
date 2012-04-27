package teammates.testing.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.AddCourseAPITest;
import teammates.testing.junit.CoordViewResultsAPITest;
import teammates.testing.junit.DeleteCourseAPITest;
import teammates.testing.junit.EnrolStudentsAPITest;
import teammates.testing.junit.ListCoursesAPITest;
import teammates.testing.junit.StudentViewResultsAPITest;
import teammates.testing.junit.TeammatesServletTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	
	CoordAddCoursePageHTMLTest.class,
	StudentHelpPageHTMLTest.class,
//	StudentHomePageHTMLTest.class, //this page is sensitive to other test and the date on which test data was imported
	CoordHelpPageHTMLTest.class,
	CoordHomePageHTMLTest.class, //this page is sensitive to version number
	
	CoordCourseAddUITest.class,
	CoordCourseDeleteStudentsTest.class,
	CoordCourseDeleteUITest.class,
	CoordCourseEditStudentsTeamTest.class,
	CoordCourseEditStudentsTest.class,
	CoordCourseEnrolCaseSensitivityTest.class,
	CoordCourseEnrolStudentsUITest.class,
//	CoordCourseListUITest.class, //why disabled? seems broken
	
	CoordCourseViewTest.class, // has email tests
	CoordEvaluationAddCaseSensitivityTest.class,
	CoordEvaluationAddTest.class,
	CoordEvaluationAddWithEmptyTeamNameTest.class,
	CoordEvaluationEditTest.class,
//	CoordEvaluationListTest.class, //why disabled? has email tests, seems broken
	CoordEvaluationResultsEditTest.class,
	CoordEvaluationResultsViewTest.class,
//	CoordHomePageFunctionalityTest.class, // JS alerts get disabled in testDeleteEvaluationLink()

	CoordLoginTest.class,
	CoordViewResultsUITest.class,  
	
//	StudentCourseJoinTest.class,  //why disabled? broken
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
	StudentEvaluationSubmitTest2.class,
	StudentHomePageFunctionalityTest.class,
	
	StudentLoginTest.class,
	StudentViewResultsUITest.class,
	
	SystemFooterTest.class,
	
	
	SystemRemindEvaluationBeforeDeadlineTest.class, //has email tests, did not work last time
	
	CoordTeamFormingSessionChangeStudentTeam.class,
	CoordTeamFormingSessionDeleteTest.class,
	CoordTeamFormingSessionEditTeamProfile.class,
	CoordTeamFormingSessionListTest.class,
	CoordTeamFormingSessionManageTest.class,
	CoordTeamFormingSessionAddTest.class,
	CoordTeamFormingSessionAddWithoutStudentsTest.class,

	StudentTeamFormingSessionActionsTest.class,
	StudentTeamFormingSessionEditProfilesTest.class,
	StudentTeamFormingSessionViewTest.class,
	
	
	//JUnit API tests
	TeammatesServletTest.class,
	AddCourseAPITest.class,
	ListCoursesAPITest.class,
	DeleteCourseAPITest.class,
	EnrolStudentsAPITest.class,
	CoordViewResultsAPITest.class,
	StudentViewResultsAPITest.class
	
})

public class TestSuiteRun {	
	
}
