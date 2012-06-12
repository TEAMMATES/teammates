package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;
import teammates.testing.lib.HtmlHelperTest;

//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		// Unit tests
		AllUnitTests.class,
		// Js tests
		AllJsUnitTests.class,
		// Api tests
		TMAPITest.class, 
		APIServletTest.class,
		// Ui tests
		CoordCourseAddPageUiTest.class, 
		CoordCourseEnrollPageUiTest.class,
		CoordHomePageUiTest.class })

//@formatter:on
public class AllCasesTestSuite {

}
