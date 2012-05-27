package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	CommonTest.class,
	StudentTest.class,
	AllJsUnitTests.class,
	TMAPITest.class,
	APIServletTest.class,
	CoordCourseAddPageHtmlTest.class,
	CoordCourseAddPageUiTest.class,
	CoordCourseAddApiTest.class
})

public class AllCasesTestSuite {	
	
}
