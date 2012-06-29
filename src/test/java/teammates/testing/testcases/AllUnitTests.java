package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.lib.HtmlHelperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off
	//TODO: there seems to be a state leak from this test to LogicTest if 
	//  this test is put right above LogicTest.
	EvaluationsTest.class,
		CommonTest.class,
		HelperTest.class,
		CoordEvalHelperTest.class,
		ConfigTest.class,
		EmailsTest.class,
		StudentTest.class, 
		TeamDataTest.class,
		EvaluationTest.class,
		EvaluationActivationServletTest.class,
		EvaluationDataTest.class, 
		EvalResultDataTest.class,
		TeamEvalResultTest.class, 
		HtmlHelperTest.class,
		LogicTest.class})
		//@formatter:on
public class AllUnitTests {

}
