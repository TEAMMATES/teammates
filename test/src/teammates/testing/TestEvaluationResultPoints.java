package teammates.testing;


import static org.junit.Assert.assertEquals;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Test calculation of evaluation points: 
 * student view results, 
 * coordinator view reviewee-summary report
 * coordinator view detail reports by reviewer/reviewee
 * coordinator view individual report by reviewer/reviewee
 * 
 * @author Xialin
 * 
 */
public class TestEvaluationResultPoints extends BaseTest {
	
	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenarioForBumpRatioTest(0);
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitDynamicFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation.name, sc.submissionPoints);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);
		
		TMAPI.createEvaluation(sc.evaluation2);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.studentsSubmitDynamicFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation2.name, sc.submissionPoints);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);
		

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testCoordViewReviewerIndividualPoints() throws Exception {
		coordViewReviewerIndividualPoints(0);
		coordViewReviewerIndividualPoints(1);
	}
		
	public void coordViewReviewerIndividualPoints(int evalIndex) throws Exception {
		cout("Test: Coordinator View Evaluation Submission Points by Reviewer ");
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		//click View Results:
		waitAndClick(By.id("viewEvaluation" + evalIndex));
		//click sort by name
		waitAndClick(By.id("button_sortname"));//make sure Alice is the first
		//click View (Reviewer x Summary)
		waitAndClick(By.id("viewEvaluationResults0"));

		//check claimed points:
		String claimedPoints = TMAPI.coordGetClaimedPoints(sc.submissionPoints, 0);
		assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 2, 2))));
		
		//check perceived points:
		String perceivedPoints = TMAPI.coordGetPerceivedPoints(sc.submissionPoints, 0);
		assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
		
		//check normalized points given TO teammates:
		List<String> pointList = TMAPI.coordGetPointsToOthers(sc.submissionPoints, 0);
		for(int i = 0; i < pointList.size(); i++){
			String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String toStudent = "";
			if(!student.equalsIgnoreCase(sc.students.get(0).name)){
				for(int j = 0; j < sc.students.size(); j++){
					if(sc.students.get(j).name.equalsIgnoreCase(student)){
						toStudent = pointList.get(j);
						continue;
					}
				}
				assertEquals(toStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}
		}
	}
	
	@Test
	public void testCoordViewRevieweeSummaryPoints() throws Exception {
		coordViewRevieweeSummaryPoints(0);
		coordViewRevieweeSummaryPoints(1);
	}
		
	public void coordViewRevieweeSummaryPoints(int evalIndex) throws Exception {
		cout("Test: Coordinator View Evaluation Result Summary Points by Reviewee ");
		
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		//click View Results:
		waitAndClick(By.id("viewEvaluation" + evalIndex));
		//click Reviewee radio: (Reviewee x Summary)
		waitAndClick(By.id("radio_reviewee"));
		//click sort by name:
		waitAndClick(By.id("button_sortname"));
		
		for(Student s: sc.students){
			int studentIndex = sc.students.indexOf(s);
			//check claimed contribution:
			String claimedPoints = TMAPI.coordGetClaimedPoints(sc.submissionPoints, studentIndex);
			assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 3))));
			//check |perceived - claimed|:
			String diff = TMAPI.coordGetPointDifference(sc.submissionPoints, studentIndex);
			assertEquals(diff, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 4))));
		}
		
	}
	
	@Test
	public void testCoordViewRevieweeIndividualPoints() throws Exception {
		coordViewRevieweeIndividualPoints(0);
		coordViewRevieweeIndividualPoints(1);
	}
		
	public void coordViewRevieweeIndividualPoints(int evalIndex) throws Exception {
		cout("Test: Coordinator View Evaluation Submission Points by Reviewee ");

		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		//click View Results:
		waitAndClick(By.id("viewEvaluation" + evalIndex));
		//click Reviewee radio: (Reviewee x Summary)
		waitAndClick(By.id("radio_reviewee"));
		//click sort by name:
		waitAndClick(By.id("button_sortname"));
		//click View:
		waitAndClick(By.id("viewEvaluationResults0"));
		//check claimed points:
		String claimedPoints = TMAPI.coordGetClaimedPoints(sc.submissionPoints, 0);
		assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
		//check perceived points:
		String perceivedPoints = TMAPI.coordGetPerceivedPoints(sc.submissionPoints, 0);
		assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 4, 2))));
		//check normalized points get FROM teammates:
		List<String> pointList = TMAPI.coordGetPointsFromOthers(sc, 0);
		for(int i = 0; i < pointList.size(); i++){
			String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String fromStudent = "";
			if(!student.equalsIgnoreCase(sc.students.get(0).name)){
				for(int j = 0; j < sc.students.size(); j++){
					if(sc.students.get(j).name.equalsIgnoreCase(student)){
						fromStudent = pointList.get(j);
						continue;
					}
				}
				assertEquals(fromStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}
			
		}
	}
	
	@Test
	public void testCoordViewReviewerDetailPoints() throws Exception {
		coordViewReviewerDetailPoints(0);
		coordViewReviewerDetailPoints(1);
	}
	public void coordViewReviewerDetailPoints(int evalIndex) throws Exception {
		cout("Test: Coordinator View Evaluation Result Detail Points by Reviewer ");
		
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		//click View Results:
		waitAndClick(By.id("viewEvaluation" + evalIndex));
		//click sort by name:
		waitAndClick(By.id("button_sortname"));
		//click Detail radio (Reviewer x Detail):
		waitAndClick(By.id("radio_detail"));
		//check points
		for(Student s: sc.students){
			int studentIndex = sc.students.indexOf(s);
			String claimedPoints = TMAPI.coordGetClaimedPoints(sc.submissionPoints, studentIndex);
			String perceivedPoints = TMAPI.coordGetPerceivedPoints(sc.submissionPoints, studentIndex);
			List<String> pointList = TMAPI.coordGetPointsToOthers(sc.submissionPoints, studentIndex);

			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) * 8;
			assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex ,  position + 2, 2))));
			assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 3, 2))));
			for(int i = 0; i < pointList.size(); i++){
				String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, i + 2, 1)));
				String toStudent = "";
				if(!student.equalsIgnoreCase(s.name)){
					for(int j = 0; j < sc.students.size(); j++){
						if(sc.students.get(j).name.equalsIgnoreCase(student)){
							toStudent = pointList.get(j);
							continue;
						}
					}
					assertEquals(toStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, i + 2, 2))));
				}
			}
		}
	}
	
	
	
	@Test
	public void testCoordViewRevieweeDetailPoints() throws Exception{
		coordViewRevieweeDetailPoints(0);
		coordViewRevieweeDetailPoints(1);
	}
	public void coordViewRevieweeDetailPoints(int evalIndex) throws Exception {
		cout("Test: Coordinator View Evaluation Result Detail Points by Reviewee ");
		
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		//click View Results:
		waitAndClick(By.id("viewEvaluation" + evalIndex));
		//click Reviewee:
		waitAndClick(By.id("radio_reviewee"));
		//click sort by name:
		waitAndClick(By.id("button_sortname"));
		//click Detail radio (Reviewer x Detail):
		waitAndClick(By.id("radio_detail"));
		//check points
		for(Student s: sc.students){
			int studentIndex = sc.students.indexOf(s);
			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) * 8;
			String claimedPoints = TMAPI.coordGetClaimedPoints(sc.submissionPoints, studentIndex);
			String perceivedPoints = TMAPI.coordGetPerceivedPoints(sc.submissionPoints, studentIndex);
			List<String> pointList = TMAPI.coordGetPointsFromOthers(sc, studentIndex);
			
			assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 2, 2))));
			assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 3, 2))));
			for(int i = 0; i < pointList.size(); i++){
				String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, i + 2, 1)));
				String fromStudent = "";
				if(!student.equalsIgnoreCase(s.name)){
					for(int j = 0; j < sc.students.size(); j++){
						if(sc.students.get(j).name.equalsIgnoreCase(student)){
							fromStudent = pointList.get(j);
							continue;
						}
					}
					assertEquals(fromStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, i + 2, 2))));
				}
			}
		}
	}
	
	@Test
	public void testCoordPublishResults() throws Exception {
		coordPublishResults(0);
		coordPublishResults(1);
		//coordinator log out
		logout();
	}
		
	public void coordPublishResults(int evalIndex) throws Exception {
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		clickAndConfirm(By.className("t_eval_publish"));
	}
	
	@Test
	public void testStudentViewResultPoints() throws Exception {
		for(int i = 0; i < sc.students.size(); i++){
			Student s = sc.students.get(i);
			studentLogin(s.email, s.password);
			studentViewResultPoints(0, i);//first evaluation
			studentViewResultPoints(1, i);//second evaluation
			logout();
		}
	}
	public void studentViewResultPoints(int evalIndex, int studentIndex) throws Exception {
		cout("function: testStudentViewResultPoints");
		// Click Evaluations
		waitAndClick(By.className("t_evaluations"));
		// Click View Results
		waitAndClick(By.id("viewEvaluation" + evalIndex));

		String claimed = TMAPI.studentGetClaimedPoints(sc.submissionPoints, studentIndex);
		assertEquals(claimed, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@id='data']//tr[%d]//td[%d]", 2, 2))));
		
		String perceived = TMAPI.studentGetPerceivedPoints(sc.submissionPoints,studentIndex);
		assertEquals(perceived, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
	}

	private int getStudentIndexInTeam(String stuName, String teamName) {
		
		int idx = 0;
		boolean start = false;
		for(Student s : sc.students) {
			if(s.teamName.equalsIgnoreCase(teamName)) {
				start = true;
			}
			
			if(start) { 
				if(s.name.equalsIgnoreCase(stuName)) {
					return idx;	
				}else{
					idx++;	
				}
			}
		}
		return -1;
	}
	private int getTeamIndex(String teamName){
		Iterator<String> it = sc.teams.keySet().iterator();
		int idx = 0;
		
		while(it.hasNext()) {
			if(it.next().equalsIgnoreCase(teamName)) {
				return idx;
			}
			idx++;
		}
		
		return -1;
	}
}