package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off 
		// Ui tests
		LoginPageUiTest.class,
		
		CoordCourseAddPageUiTest.class,
		CoordCourseEnrollPageUiTest.class,
		CoordCourseDetailsPageUiTest.class,
		CoordCourseStudentDetailsPageUiTest.class,
		
		CoordEvalPageUiTest.class,
		CoordEvalEditPageUiTest.class,
		CoordEvalResultsPageUiTest.class,
		CoordEvalSubmissionPageUiTest.class,
		
		CoordHomePageUiTest.class,
		
		StudentCourseDetailsPageUiTest.class,
		StudentEvalEditPageUiTest.class,
		StudentEvalResultsPageUiTest.class,
		
		StudentHomePageUiTest.class,
		SystemErrorEmailReportTest.class,

})

//@formatter:on
public class AllUiTestsSuite {

}
