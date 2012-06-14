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
		CoordEvalPageUiTest.class,
		CoordEvalResultsPageUiTest.class,
		CoordHomePageUiTest.class,
		
		StudentHomePageUiTest.class
})

//@formatter:on
public class AllUiTests {

}
