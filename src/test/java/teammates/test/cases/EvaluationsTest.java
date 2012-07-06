package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.EvaluationData.EvalStatus;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.manager.EvaluationsManager;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EvaluationsTest extends BaseTestCase{
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsManager.class);
		Datastore.initialize();
	}
	
	@Before
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	
	@Test
	public void testGetReadyEvaluations() throws Exception {
		
		loginAsAdmin("admin.user");
		
		______TS("no evaluations activated");
		//ensure there are no existing evaluations ready for activation
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		BackDoorLogic backdoor = new BackDoorLogic();
		for(EvaluationData e: dataBundle.evaluations.values()){
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		assertEquals(0, EvaluationsManager.inst().getReadyEvaluations().size());
		
		______TS("typical case, two evaluations activated");
		// Reuse an existing evaluation to create a new one that is ready to 
		//  activate. Put this evaluation in a negative time zone.
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		String nameOfEvalInCourse1 = "new-evaluation-in-course-1-tGRE";
		evaluation.name = nameOfEvalInCourse1;
		
		evaluation.activated = false;
		
		double timeZone = -1.0;
		evaluation.timeZone = timeZone; 

		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(0, timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(2);
		
		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students.
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseData course1 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course1.unregisteredTotal);

		// Create another evaluation in another course in similar fashion. 
		// Put this evaluation in a positive time zone.
		// This one too is ready to activate. 
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
		evaluation.activated = false;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tGRE";
		evaluation.name = nameOfEvalInCourse2;
		
		timeZone = 2.0;
		evaluation.timeZone = timeZone;
		
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(0, timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(2);
		
		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseData course2 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course2.unregisteredTotal);
		
		// Create another evaluation not ready to be activated yet.
		// Put this evaluation in same time zone.
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
		evaluation.activated = false;
		evaluation.name = "new evaluation - start time in future";
		
		
		timeZone = 0.0;
		evaluation.timeZone = timeZone;
		
		int oneSecondInMs = 1000;
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(oneSecondInMs, timeZone);
		backdoor.createEvaluation(evaluation);

		//verify number of ready evaluations.
		assertEquals(2, EvaluationsManager.inst().getReadyEvaluations().size());
		
		// Other variations of ready/not-ready states should be checked at 
		//   Evaluation level
	}
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationOpeningRemindersServlet.class);
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

}
