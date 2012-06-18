package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.BackDoorLogic;
import teammates.Datastore;
import teammates.EvaluationActivationServlet;
import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.EvaluationData.EvalStatus;
import teammates.manager.Evaluations;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EvaluationsTest extends BaseTestCase{
	
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Evaluations.class);
		Datastore.initialize();
	}
	
	@Before
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	@Test
	public void testSetEvaluationsAsActivated() throws Exception{
		assertEquals(0, Evaluations.inst().setEvaluationsAsActivated().size());
		
		//ensure there are no existing evaluations ready for activation
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		BackDoorLogic backdoor = new BackDoorLogic();
		for(EvaluationData e: dataBundle.evaluations.values()){
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		assertEquals(0, Evaluations.inst().setEvaluationsAsActivated().size());
		
		//reuse an existing evaluation to create a new one
		EvaluationData evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		evaluation.activated = false;
		evaluation.name = "new evaluation";
		evaluation.startTime = Common.getDateOffsetToCurrentTime(0);
		assertEquals(EvalStatus.AWAITING,evaluation.getStatus());
		backdoor.createEvaluation(evaluation);
		assertEquals(1, Evaluations.inst().setEvaluationsAsActivated().size());
		//TODO: more testing
	}
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationActivationServlet.class);
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

}
