package teammates.testing.concurrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.AddCourseAPITest;
import teammates.testing.junit.CoordViewResultsAPITest;
import teammates.testing.junit.DeleteCourseAPITest;
import teammates.testing.junit.EnrollStudentsAPITest;
import teammates.testing.junit.ListCoursesAPITest;
import teammates.testing.junit.StudentViewResultsAPITest;
import teammates.testing.junit.TeammatesServletTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	
	CoordCourseAddPageHTMLTest.class,
	StudentHelpPageHTMLTest.class,
//	StudentHomePageHTMLTest.class, //this page is sensitive to other test and the date on which test data was imported
	CoordHelpPageHTMLTest.class,
//	CoordHomePageHTMLTest.class, //this page is sensitive to version number, need to fix json file
	
	CoordCourseAddUITest.class,
	CoordCourseDeleteStudentsTest.class,
	CoordCourseDeleteUITest.class,
	CoordCourseEditStudentsTeamTest.class,
	CoordCourseEditStudentsTest.class,
	CoordCourseEnrollCaseSensitivityTest.class,
	CoordCourseEnrollStudentsUITest.class,
	CoordCourseListUITest.class,
	
	CoordCourseViewTest.class, // has email tests
	CoordEvaluationAddCaseSensitivityTest.class,
	CoordEvaluationAddTest.class,
	CoordEvaluationAddWithEmptyTeamNameTest.class,
	CoordEvaluationEditTest.class,
//	CoordEvaluationResultsSortTest.class, //empty class, to be filled later
//	CoordEvaluationListTest.class, //why disabled? has email tests, seems broken
	CoordEvaluationResultsEditTest.class,
	CoordEvaluationResultsViewTest.class,
//	CoordHomePageFunctionalityTest.class, // The Home page (landing page) has some issue (see todo inside the file)

	CoordLoginTest.class,
	CoordViewResultsUITest.class,  
	
//	StudentCourseJoinTest.class,  //why disabled? broken
	StudentEvaluationEditTest2.class,
	StudentEvaluationResultsTest2.class,
	StudentEvaluationSubmitTest2.class,
	StudentHomePageFunctionalityTest.class,
	
	StudentLoginTest.class,
	StudentViewResultsUITest.class,
	
//	SystemFooterTest.class, //broken, need to fix soon
	
	
	SystemRemindEvaluationBeforeDeadlineTest.class, //has email tests, did not work last time
	
	CoordTeamFormingSessionChangeStudentTeam.class,
	CoordTeamFormingSessionDeleteTest.class,
	CoordTeamFormingSessionEditTeamProfile.class,
	CoordTeamFormingSessionListTest.class,
	CoordTeamFormingSessionManageTest.class,
	CoordTeamFormingSessionAddTest.class,
	CoordTeamFormingSessionAddWithoutStudentsTest.class,
	SystemTeamFormingOpenAndRemindTest.class,

	StudentTeamFormingSessionActionsTest.class,
	StudentTeamFormingSessionEditProfilesTest.class,
	StudentTeamFormingSessionViewTest.class,
	
	
	//JUnit API tests
	TeammatesServletTest.class,
	AddCourseAPITest.class,
	ListCoursesAPITest.class,
	DeleteCourseAPITest.class,
	EnrollStudentsAPITest.class,
	CoordViewResultsAPITest.class,
	StudentViewResultsAPITest.class
	
})

public class TestSuiteRun {	
	
}
