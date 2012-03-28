package teammates.testing.junit;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Datastore;
import teammates.Evaluations;
import teammates.jdo.Submission;
import teammates.testing.object.Scenario;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ViewEvaluationResultsAPITest extends APITest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	static Scenario scn;
	
	@Before
	public void setUp() {
		helper.setUp();
		Datastore.initialize();
		pm = Datastore.getPersistenceManager();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void testGetSubmissionList() throws Exception {
		testEvaluationsGetSubmissionList();
		testEvaluationsCalculateBumpRatio();
		testSubmissionDetailsForCoordinator();
		testTeamatesServletGetSubmissionList();
	}
	
	public void testEvaluationsGetSubmissionList() throws IOException {
		
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 4);
		prepareSubmissionData(scn);
		
		List<Submission> submissionList = Evaluations.inst().getSubmissionList(scn.evaluation.courseID, scn.evaluation.name);
		
		for(int i = 0; i < submissionList.size(); i++) {
			Submission sub = submissionList.get(i);
			System.out.println("[submission "+i+"] course: " 
					+ sub.getCourseID() + " evaluation" 
					+ sub.getEvaluationName() 
					+ " point: " + sub.getPoints() 
					+ " from: " + sub.getFromStudent() 
					+ " to: " + sub.getToStudent()
					+ " comments: " + sub.getCommentsToStudent()
					+ " justification: " + sub.getJustification());
		}
	}
	
	public void testEvaluationsCalculateBumpRatio() {
		
	}
	
	public void testSubmissionDetailsForCoordinator() {
		
	}
	
	/***
	 * Integrated test
	 */
	public void testTeamatesServletGetSubmissionList() {
		
	}
}
