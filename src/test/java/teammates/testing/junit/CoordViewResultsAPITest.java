package teammates.testing.junit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Datastore;
import teammates.TeammatesServlet;
import teammates.manager.Evaluations;
import teammates.persistent.Submission;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class CoordViewResultsAPITest extends APITest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private Scenario scn;
	
	
	@Before
	public void setUp() {
		helper.setUp();
		try{
			Datastore.initialize();
		} catch(Exception e) {
			System.out.println("PersistenceManagerFactory has been called once.");
		}
		pm = Datastore.getPersistenceManager();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}
	
//	@Test
//	public void testGetSubmissionList0(){
//		testGetSubmissionList(0);
//	}
	
	@Test
	public void testGetSubmissionList1(){
		testGetSubmissionList(1);
	}
	
	@Test
	public void testGetSubmissionList2(){
		testGetSubmissionList(2);
	}
	
	@Test
	public void testGetSubmissionList3(){
		testGetSubmissionList(3);
	}
	
	@Test
	public void testGetSubmissionList4(){
		testGetSubmissionList(4);
	}
	
	@Test
	public void testGetSubmissionList5(){
		testGetSubmissionList(5);
	}
	
	@Test
	public void testGetSubmissionList6(){
		testGetSubmissionList(6);
	}
	
	@Test
	public void testGetSubmissionList7(){
		testGetSubmissionList(7);
	}
	
	@Test
	public void testGetSubmissionList8(){
		testGetSubmissionList(8);
	}
	
	@Test
	public void testGetSubmissionList9(){
		testGetSubmissionList(9);
	}
	
	@Test
	public void testGetSubmissionList10(){
		testGetSubmissionList(10);
	}
	
	@Test
	public void testGetSubmissionList11(){
		testGetSubmissionList(11);
	}
	
	@Test
	public void testGetSubmissionList12(){
		testGetSubmissionList(12);
	}
	
	@Test
	public void testGetSubmissionList13(){
		testGetSubmissionList(13);
	}
	
	@Test
	public void testGetSubmissionList14(){
		testGetSubmissionList(14);
	}
	
	@Test
	public void testGetSubmissionList15(){
		testGetSubmissionList(15);
	}
	
	@Test
	public void testGetSubmissionList16(){
		testGetSubmissionList(16);
	}
	
	@Test
	public void testGetSubmissionList17(){
		testGetSubmissionList(17);
	}
	
	@Test
	public void testGetSubmissionList18(){
		testGetSubmissionList(18);
	}
	
	@Test
	public void testGetSubmissionList19(){
		testGetSubmissionList(19);
	}
	
	@Test
	public void testGetSubmissionList20(){
		testGetSubmissionList(20);
	}
	
	@Test
	public void testGetSubmissionList21(){
		testGetSubmissionList(21);
	}
	
	@Test
	public void testGetSubmissionList22(){
		testGetSubmissionList(22);
	}
	
	@Test
	public void testGetSubmissionList23(){
		testGetSubmissionList(23);
	}
	
	@Test
	public void testGetSubmissionList24(){
		testGetSubmissionList(24);
	}
	
	@Test
	public void testGetSubmissionList25(){
		testGetSubmissionList(25);
	}
	
	@Test
	public void testGetSubmissionList26(){
		testGetSubmissionList(26);
	}
	
	@Test
	public void testGetSubmissionList27(){
		testGetSubmissionList(27);
	}
	
	@Test
	public void testGetSubmissionList28(){
		testGetSubmissionList(28);
	}
	
	
	public void testGetSubmissionList(int index) {
		
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", index);
		try{
			prepareSubmissionData(scn);
			testEvaluationsGetSubmissionList();
			testEvaluationsCalculateBumpRatio();
			testTeammatesServletCoordinatorGetSubmissionList();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void testEvaluationsGetSubmissionList() throws IOException {
		
		//function under test
		List<Submission> submissionList = Evaluations.inst().getSubmissionList(scn.evaluation.courseID, scn.evaluation.name);

		//test output
		int size = getTeamSizeFromSubmissionPoints(scn.submissionPoints);
		int index = 0;
		for(int from = 0; from < size; from++) {
			String points = TMAPI.getSubmissionPoints(scn.submissionPoints[from]);
			for(int to = 0; to < size; to++) {
				int actual = submissionList.get(index++).getPoints();
				int expected = Integer.valueOf(points.split(", ")[to]);
				assertEquals(expected, actual);
			}
		}
	}

	public void testEvaluationsCalculateBumpRatio() {
		float pointsBumpRatio;
		List<Submission> submissionList = Evaluations.inst().getSubmissionList(scn.evaluation.courseID, scn.evaluation.name);
		Map<String, Float> bumpRatioMap = new HashMap<String, Float>();
		
		for(int i = 0; i < submissionList.size(); i++){
			Submission s = submissionList.get(i);
			
			//prepare submissionList grouped by fromStudent
			List<Submission> fromList = new ArrayList<Submission>();
			for(Submission from: submissionList) {
				if(from.getFromStudent().equals(s.getFromStudent()))
					fromList.add(from);
			}
			
			//function under test
			pointsBumpRatio = Evaluations.inst().calculatePointsBumpRatio(scn.evaluation.courseID, scn.evaluation.name, s.getFromStudent(), fromList);
			bumpRatioMap.put(s.getFromStudent(), pointsBumpRatio);
		}
		
		for(int i = 0; i < bumpRatioMap.size(); i++){
			float expected = Float.valueOf(TMAPI.coordGetSubmissionBumpRatio(scn.submissionPoints, i));
			float actual = bumpRatioMap.remove(scn.students.get(i).email);
			assertEquals(String.valueOf(expected), String.valueOf(actual));
		}
	}
	
	public void testTeammatesServletCoordinatorGetSubmissionList() throws Exception {
		//function under test
		TeammatesServlet ts = new TeammatesServlet();
		String response = ts.coordinatorGetSubmissionList(scn.evaluation.courseID, scn.evaluation.name);
		
		//expected data
		List<Integer> pointList = getPointListFromServerResponse(response);
		
		//test output
		int size = getTeamSizeFromSubmissionPoints(scn.submissionPoints);
		int index = 0;
		for(int from = 0; from < size; from++) {
			String[] points = prepareSubmissionPoints(scn.submissionPoints[from]);
			for(int to = 0; to < size; to++) {
				int actual = pointList.get(index++);
				int expected = Integer.valueOf(points[to]);
				assertEquals(expected, actual);
			}
		}
	}
}
