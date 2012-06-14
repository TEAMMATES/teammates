package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;

//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		// Unit tests
		AllUnitTests.class,
		// Js tests
		AllJsUnitTests.class,
		// Api tests
		TMAPITest.class, 
		// Ui tests
		CoordCourseAddPageUiTest.class, 
		CoordCourseEnrollPageUiTest.class,
		CoordCourseDetailsPageUiTest.class,
		CoordEvalPageUiTest.class,
		CoordEvalResultsPageUiTest.class,
		CoordHomePageUiTest.class })

//@formatter:on
public class AllCasesTestSuite {

}
