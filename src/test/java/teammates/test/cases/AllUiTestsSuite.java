package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off 
		// Ui tests
		LoginPageUiTest.class,
		
		InstructorCourseAddPageUiTest.class,
		InstructorCourseEnrollPageUiTest.class,
		InstructorCourseDetailsPageUiTest.class,
		InstructorCourseEditPageUiTest.class,
		InstructorCourseStudentDetailsPageUiTest.class,
		
		InstructorEvalPageUiTest.class,
		InstructorEvalEditPageUiTest.class,
		InstructorEvalResultsPageUiTest.class,
		InstructorEvalSubmissionPageUiTest.class,
		
		InstructorHomePageUiTest.class,
		
		StudentCourseDetailsPageUiTest.class,
		StudentEvalEditPageUiTest.class,
		StudentEvalResultsPageUiTest.class,
		
		
		StudentHomePageUiTest.class,
		SystemErrorEmailReportTest.class,
		
		AdminHomePageUiTest.class,
		AdminAccountManagementUiTest.class,
		
		TableSortTest.class,
})

//@formatter:on
public class AllUiTestsSuite {

}
