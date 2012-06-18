package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off 
		// Ui tests
		CoordCourseAddPageUiTest.class,
		CoordCourseEnrollPageUiTest.class,
		CoordCourseDetailsPageUiTest.class,
		CoordCourseStudentDetailsPageUiTest.class,
		
		CoordEvalPageUiTest.class,
		CoordEvalEditPageUiTest.class,
		CoordEvalResultsPageUiTest.class,
		CoordEvalSubmissionEditPageUiTest.class,
		CoordEvalSubmissionViewPageUiTest.class,
		
		CoordHomePageUiTest.class,
		
		StudentCourseDetailsPageUiTest.class,
		StudentEvalEditPageUiTest.class,
		StudentEvalResultsPageUiTest.class,
		
		StudentHomePageUiTest.class
})

//@formatter:on
public class AllUiTests {

}
