package teammates.testing;


import static org.junit.Assert.assertEquals;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class TestEvaluationPointCalculation extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}
	
	@Test
	public void testFirstDataSet0() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 0);
		setupScenarioForBumpRatioTest(0);
		testScenario();
	}
	
	@Test
	public void testFirstDataSet1() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 1);
		setupScenarioForBumpRatioTest(1);
		testScenario();
	}
	@Test
	public void testFirstDataSet2() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 2);
		setupScenarioForBumpRatioTest(2);
		testScenario();
	}
	@Test
	public void testFirstDataSet3() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 3);
		setupScenarioForBumpRatioTest(3);
		testScenario();
	}
	@Test
	public void testFirstDataSet4() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 4);
		setupScenarioForBumpRatioTest(4);
		testScenario();
	}
	@Test
	public void testFirstDataSet5() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 5);
		setupScenarioForBumpRatioTest(5);
		testScenario();
	}
	@Test
	public void testFirstDataSet6() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 6);
		setupScenarioForBumpRatioTest(6);
		testScenario();
	}
	@Test
	public void testFirstDataSet7() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 7);
		setupScenarioForBumpRatioTest(7);
		testScenario();
	}
	@Test
	public void testFirstDataSet8() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 8);
		setupScenarioForBumpRatioTest(8);
		testScenario();
	}
	@Test
	public void testFirstDataSet9() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 9);
		setupScenarioForBumpRatioTest(9);
		testScenario();
	}
	@Test
	public void testFirstDataSet10() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 10);
		setupScenarioForBumpRatioTest(10);
		testScenario();
	}
	@Test
	public void testFirstDataSet11() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 11);
		setupScenarioForBumpRatioTest(11);
		testScenario();
	}
	@Test
	public void testFirstDataSet12() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 12);
		setupScenarioForBumpRatioTest(12);
		testScenario();
	}
	@Test
	public void testFirstDataSet13() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 13);
		setupScenarioForBumpRatioTest(13);
		testScenario();
	}
	@Test
	public void testFirstDataSet14() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 14);
		setupScenarioForBumpRatioTest(14);
		testScenario();
	}
	@Test
	public void testFirstDataSet15() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 15);
		setupScenarioForBumpRatioTest(15);
		testScenario();
	}
	@Test
	public void testFirstDataSet16() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 16);
		setupScenarioForBumpRatioTest(16);
		testScenario();
	}
	@Test
	public void testFirstDataSet17() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 17);
		setupScenarioForBumpRatioTest(17);
		testScenario();
	}
	@Test
	public void testFirstDataSet18() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 18);
		setupScenarioForBumpRatioTest(18);
		testScenario();
	}
	@Test
	public void testFirstDataSet19() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 19);
		setupScenarioForBumpRatioTest(19);
		testScenario();
	}
	@Test
	public void testFirstDataSet20() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 20);
		setupScenarioForBumpRatioTest(20);
		testScenario();
	}
	@Test
	public void testFirstDataSet21() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 21);
		setupScenarioForBumpRatioTest(21);
		testScenario();
	}
	@Test
	public void testFirstDataSet22() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 22);
		setupScenarioForBumpRatioTest(22);
		testScenario();
	}
	@Test
	public void testFirstDataSet23() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 23);
		setupScenarioForBumpRatioTest(23);
		testScenario();
	}
	
	@Test
	public void testFirstDataSet24() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 24);
		setupScenarioForBumpRatioTest(24);
		testScenario();
	}
	@Test
	public void testFirstDataSet25() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 25);
		setupScenarioForBumpRatioTest(25);
		testScenario();
	}
	
	@Test
	public void testFirstDataSet26() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 26);
		setupScenarioForBumpRatioTest(26);
		testScenario();
	}
	@Test
	public void testFirstDataSet27() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 27);
		setupScenarioForBumpRatioTest(27);
		testScenario();
	}
	@Test
	public void testFirstDataSet28() throws Exception {
		cout("TestEvalutionPointCalculation: scenario " + 28);
		setupScenarioForBumpRatioTest(28);
		testScenario();
	}
	private void testScenario() throws Exception {
		//before:
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitDynamicFeedbacks(sc.students, sc.course.courseId, sc.evaluation.name, sc.submissionPoints);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.createEvaluation(sc.evaluation2);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.studentsSubmitDynamicFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation2.name, sc.submissionPoints);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);
		
		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
		
		//test:
		testCoordViewReviewerIndividualPoints();
		testCoordViewRevieweeSummaryPoints();
		testCoordViewRevieweeIndividualPoints();
		testCoordViewReviewerDetailPoints();
		testCoordViewRevieweeDetailPoints();
		testCoordPublishResults();
		testStudentViewResultPoints();
		
		//after:
		wrapUp();
	}

//	@Test
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
		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(sc.submissionPoints, 0);
		assertEquals(claimedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2))));
		//assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", 2, 3))));
		
		//check perceived points:
		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(sc.submissionPoints, 0);
		assertEquals(perceivedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3))));
		//assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
		
		//check normalized points given TO teammates:
		List<String> pointList1 = TMAPI.coordGetPointsToOthersOneLine(sc.submissionPoints, 0);
		List<String> pointList2 = TMAPI.coordGetPointsToOthersTwoLines(sc.submissionPoints, 0);
		
		for(int i = 0; i < pointList1.size(); i++) {
			String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 1)));
			//String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String toStudent1 = "";
			String toStudent2 = "";
			if(!student.equalsIgnoreCase(sc.students.get(0).name)) {
				for(int j = 0; j < sc.students.size(); j++){
					if(sc.students.get(j).name.equalsIgnoreCase(student)) {
						toStudent1 = pointList1.get(j);
						toStudent2 = pointList2.get(j);
						continue;
					}
				}
				assertEqualsOr(toStudent1, toStudent2, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 2))));
				//assertEquals(toStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}
		}
	}
	
//	@Test
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
	
//	@Test
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
		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(sc.submissionPoints, 0);
		assertEquals(claimedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2))));
		//assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
		
		//check perceived points:
		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(sc.submissionPoints, 0);
		assertEquals(perceivedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3))));
		//assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 4, 2))));
		
		//check normalized points get FROM teammates:
		List<String> pointList1 = TMAPI.coordGetPointsFromOthersOneLine(sc, 0);
		List<String> pointList2 = TMAPI.coordGetPointsFromOthersTwoLines(sc, 0);

		for(int i = 0; i < pointList1.size(); i++) {
			String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 1)));
			//String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String fromStudent1 = "";
			String fromStudent2 = "";
			if(!student.equalsIgnoreCase(sc.students.get(0).name)) {
				for(int j = 0; j < sc.students.size(); j++) {
					if(sc.students.get(j).name.equalsIgnoreCase(student)) {
						fromStudent1 = pointList1.get(j);
						fromStudent2 = pointList2.get(j);
						continue;
					}
				}
				assertEqualsOr(fromStudent1, fromStudent2, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 2))));
				//assertEquals(fromStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}
			
		}
	}
	
//	@Test
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
		for(Student s: sc.students) {
			int studentIndex = sc.students.indexOf(s);
			String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(sc.submissionPoints, studentIndex);
			String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(sc.submissionPoints, studentIndex);
			List<String> pointList2 = TMAPI.coordGetPointsToOthersTwoLines(sc.submissionPoints, studentIndex);
			List<String> pointList1 = TMAPI.coordGetPointsToOthersOneLine(sc.submissionPoints, studentIndex);

			List<Student> teammates = getStudentTeammates(sc.students, studentIndex);
			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) + 1;
			cout("team index: " + teamIndex);
			cout("position: " + position);
			assertEquals(claimedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 2))));
			assertEquals(perceivedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 3))));
			for(int i = 0; i < pointList1.size(); i++) {
				String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 1)));
				String toStudent1 = "";
				String toStudent2 = "";
				if(!student.equalsIgnoreCase(s.name)) {
					for(int j = 0; j < teammates.size(); j++) {
						if(teammates.get(j).name.equalsIgnoreCase(student)) {
							toStudent1 = pointList1.get(j);
							toStudent2 = pointList2.get(j);
							cout("toStudent:" + toStudent1);
							continue;
						}
					}
					assertEqualsOr(toStudent1, toStudent2, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 2))));
				}
			}
		}
	}	
	
//	@Test
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
		for(Student s: sc.students) {
			int studentIndex = sc.students.indexOf(s);
			String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(sc.submissionPoints, studentIndex);
			String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(sc.submissionPoints, studentIndex);
			List<String> pointList1 = TMAPI.coordGetPointsFromOthersOneLine(sc, studentIndex);
			List<String> pointList2 = TMAPI.coordGetPointsFromOthersTwoLines(sc, studentIndex);
			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) + 1;
			List<Student> teammates = getStudentTeammates(sc.students, studentIndex);
			
			assertEquals(claimedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 2))));
			assertEquals(perceivedPoints.toUpperCase(), getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 3))));
			for(int i = 0; i < pointList2.size(); i++) {
				String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 1)));
				String fromStudent1 = "";
				String fromStudent2 = "";
				if(!student.equalsIgnoreCase(s.name)) {
					for(int j = 0; j < teammates.size(); j++) {
						if(teammates.get(j).name.equalsIgnoreCase(student)) {
							fromStudent1 = pointList1.get(j);
							fromStudent2 = pointList2.get(j);

							continue;
						}
					}
					assertEqualsOr(fromStudent1, fromStudent2, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 2))));
				}
			}
		}
	}
	
//	@Test
	public void testCoordPublishResults() throws Exception {
		coordPublishResults(0);
		coordPublishResults(1);
		logout();
	}
		
	public void coordPublishResults(int evalIndex) throws Exception {
		//click Evaluation Tab:
		waitAndClick(By.className("t_evaluations"));
		clickAndConfirm(By.className("t_eval_publish"));
	}
	
//	@Test
	public void testStudentViewResultPoints() throws Exception {
		for(int i = 0; i < sc.students.size(); i++) {
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
		assertEquals(claimed, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 3, 2))));
		//assertEquals(claimed, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@id='data']//tr[%d]//td[%d]", 2, 2))));
		
		String perceived = TMAPI.studentGetPerceivedPoints(sc.submissionPoints,studentIndex);
		assertEquals(perceived, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 4, 2))));
		//assertEquals(perceived, getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));
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
				} else {
					idx++;	
				}
			}
		}
		return -1;
	}
	
	private int getTeamIndex(String teamName) {
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
	
	private List<Student> getStudentTeammates(List<Student> students, int index) {
		List<Student> list = new ArrayList<Student>();
		Student student = students.get(index);
		for (Student s : students){
			if(s.teamName.equalsIgnoreCase(student.teamName)){
				list.add(s);
			}
		}
		return list;
	}
}