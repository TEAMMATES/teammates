package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
		//@formatter:off
		// TODO: there seems to be a state leak from this test to LogicTest if 
		// this test is put right above LogicTest.
		AccountsDbTest.class,
		CoursesDbTest.class,
		EvaluationsDbTest.class,
		SubmissionsDbTest.class,
		
		EvaluationsStorageTest.class,
		CommonTest.class,
		HelperTest.class,
		CoordEvalHelperTest.class,
		BuildPropertiesTest.class,
		EmailsTest.class,
		StudentTest.class, 
		TeamDataTest.class,
		CourseDataTest.class,
		EvaluationTest.class,
		EvaluationDataTest.class, 
		EvalResultDataTest.class,
		SubmissionDataTest.class,
		TeamEvalResultTest.class, 
		HtmlHelperTest.class,
		TestPropertiesTest.class,
		LogicTest.class})
		//@formatter:on
public class AllComponentTestsSuite {

}
