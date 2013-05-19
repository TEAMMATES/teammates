package teammates.test.cases.storage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.storage.entity.Evaluation;
import teammates.test.cases.BaseTestCase;

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
