package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;
import teammates.testing.lib.HtmlHelperTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	CommonTest.class,
	StudentTest.class,
	TeamDataTest.class,
	EvalResultDataTest.class,
	HtmlHelperTest.class,
	AllJsUnitTests.class,
	TMAPITest.class,
	APIServletTest.class,
	CoordCourseAddPageUiTest.class,
	CoordCourseAddApiTest.class,
	CoordHomePageUiTest.class
})

public class AllCasesTestSuite {

}
