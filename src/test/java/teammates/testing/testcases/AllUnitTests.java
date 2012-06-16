package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.lib.HtmlHelperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off
		CommonTest.class, 
		StudentTest.class, 
		TeamDataTest.class,
		EvaluationDataTest.class, 
		EvalResultDataTest.class,
		TeamEvalResultTest.class, 
		HtmlHelperTest.class,
		LogicTest.class})
		//@formatter:on
public class AllUnitTests {

}
