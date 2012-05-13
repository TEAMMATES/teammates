package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllJsUnitTests.class,
	CoordCourseAddPageHtmlTest.class,
	CoordCourseAddPageUiTest.class,
	CoordCourseAddApiTest.class
})

public class AllCasesTestSuite {	
	
}
