package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.storage.entity.Evaluation;

public class EvaluationTest extends BaseTestCase {
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Evaluation.class);
	}


	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassHeader();
		turnLoggingDown(Evaluation.class);
	}

}
