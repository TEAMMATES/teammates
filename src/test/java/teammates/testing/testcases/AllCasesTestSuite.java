package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;
import teammates.testing.lib.HtmlHelperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//Unit tests
	CommonTest.class,
	StudentTest.class,
	TeamDataTest.class,
	EvalResultDataTest.class,
	TeamEvalResultTest.class,
	HtmlHelperTest.class,
	//Js tests
	AllJsUnitTests.class,
	//Api tests
	TMAPITest.class,
	APIServletTest.class,
	//Ui tests
	CoordCourseAddPageUiTest.class,
	CoordCourseEnrollPageUiTest.class,
	CoordEvalPageUiTest.class,
	CoordEvalResultsPageUiTest.class,
	CoordHomePageUiTest.class
})

public class AllCasesTestSuite {

}
