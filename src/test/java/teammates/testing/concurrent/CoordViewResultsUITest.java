package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class CoordViewResultsUITest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn;
	static int FIRST_STUDENT = 0;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 0);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);

		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitDynamicFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name, scn.submissionPoints);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);

		TMAPI.createEvaluation(scn.evaluation2);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.studentsSubmitDynamicFeedbacks(scn.students, scn.course.courseId, scn.evaluation2.name, scn.submissionPoints);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation2.name);

		TMAPI.disableEmail();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
		TMAPI.enableEmail();
	}

	@Test
	public void testFirstDataSet0() throws Exception {

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		
		// coordinator page testing:
		coordViewRevieweeSummaryPoints(scn.evaluation);
		coordViewRevieweeSummaryPoints(scn.evaluation2);
		
		coordViewReviewerIndividualPoints(scn.evaluation);
		coordViewReviewerIndividualPoints(scn.evaluation2);
		
		coordViewRevieweeIndividualPoints(scn.evaluation);
		coordViewRevieweeIndividualPoints(scn.evaluation2);
		
		coordViewReviewerDetailPoints(scn.evaluation);
		coordViewReviewerDetailPoints(scn.evaluation2);
		
		coordViewRevieweeDetailPoints(scn.evaluation);
		coordViewRevieweeDetailPoints(scn.evaluation2);
		

	}

	public void coordViewRevieweeSummaryPoints(Evaluation eval) throws Exception {
		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(scn.course.courseId, eval.name);
		//select reviewee summary view
		bi.waitAndClick(bi.resultRevieweeRadio);
		bi.waitAndClick(bi.resultStudentSorting);

		for (Student s : scn.students) {
			int studentIndex = scn.students.indexOf(s);

			String claimedPoints = TMAPI.coordGetClaimedPoints(scn.submissionPoints, studentIndex);
			assertEquals(claimedPoints, bi.getRevieweeSummaryClaimed(studentIndex));
			
			String diff = TMAPI.coordGetPointDifference(scn.submissionPoints, studentIndex);
			assertEquals(diff, bi.getRevieweeSummaryDifference(studentIndex));
		}
	}

	public void coordViewReviewerIndividualPoints(Evaluation eval) throws Exception {
		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(scn.course.courseId, eval.name);
		bi.waitAndClick(bi.resultStudentSorting);// make sure Alice is the first
		bi.clickReviewerSummaryView(FIRST_STUDENT);

		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, FIRST_STUDENT);
		assertEquals(claimedPoints.toUpperCase(), bi.getReviewerIndividualClaimedPoint());

		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, FIRST_STUDENT);
		assertEquals(perceivedPoints.toUpperCase(), bi.getReviewerIndividualPerceivedPoint());

		// check normalized points given TO teammates:
		List<String> pointList1 = TMAPI.coordGetPointsToOthersOneLine(scn.submissionPoints, FIRST_STUDENT);
		List<String> pointList2 = TMAPI.coordGetPointsToOthersTwoLines(scn.submissionPoints, FIRST_STUDENT);

		for (int i = 0; i < pointList1.size(); i++) {
			String student = bi.getElementText(bi.getReviewerIndividualToStudent(i));
			String toStudent1 = "";
			String toStudent2 = "";
			if (!student.equalsIgnoreCase(scn.students.get(FIRST_STUDENT).name)) {
				for (int j = 0; j < scn.students.size(); j++) {
					if (scn.students.get(j).name.equalsIgnoreCase(student)) {
						toStudent1 = pointList1.get(j);
						toStudent2 = pointList2.get(j);
						continue;
					}
				}
				bi.assertEqualsOr(toStudent1, toStudent2, bi.getElementText(bi.getReviewerIndividualToStudentPoint(i)));
			}
		}
	}
	
	public void coordViewRevieweeIndividualPoints(Evaluation eval) throws Exception {
		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(scn.course.courseId, eval.name);
		bi.waitAndClick(bi.resultRevieweeRadio);
		bi.waitAndClick(bi.resultStudentSorting);
		bi.clickRevieweeSummaryView(FIRST_STUDENT);

		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, FIRST_STUDENT);
		assertEquals(claimedPoints.toUpperCase(), bi.getRevieweeIndividualClaimedPoint());

		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, FIRST_STUDENT);
		assertEquals(perceivedPoints.toUpperCase(), bi.getRevieweeIndividualPerceivedPoint());

		// check normalized points get FROM teammates:
		List<String> pointList1 = TMAPI.coordGetPointsFromOthersOneLine(scn, FIRST_STUDENT);
		List<String> pointList2 = TMAPI.coordGetPointsFromOthersTwoLines(scn, FIRST_STUDENT);

		for (int i = 0; i < pointList1.size(); i++) {
			String student = bi.getElementText(bi.getRevieweeIndividualFromStudent(i));
			String fromStudent1 = "";
			String fromStudent2 = "";
			if (!student.equalsIgnoreCase(scn.students.get(FIRST_STUDENT).name)) {
				for (int j = 0; j < scn.students.size(); j++) {
					if (scn.students.get(j).name.equalsIgnoreCase(student)) {
						fromStudent1 = pointList1.get(j);
						fromStudent2 = pointList2.get(j);
						continue;
					}
				}
				bi.assertEqualsOr(fromStudent1, fromStudent2, bi.getElementText(bi.getRevieweeIndividualFromStudentPoint(i)));
			}
		}
	}

	public void coordViewReviewerDetailPoints(Evaluation eval) throws Exception {
		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(eval.courseID, eval.name);
		//sort student list
		bi.waitAndClick(bi.resultStudentSorting);
		//select detail view
		bi.waitAndClick(bi.resultDetailRadio);
		
		// check points
		for (Student s : scn.students) {
			int studentIndex = scn.students.indexOf(s);
			String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, studentIndex);
			String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, studentIndex);
			List<String> pointList2 = TMAPI.coordGetPointsToOthersTwoLines(scn.submissionPoints, studentIndex);
			List<String> pointList1 = TMAPI.coordGetPointsToOthersOneLine(scn.submissionPoints, studentIndex);

			List<Student> teammates = getStudentTeammates(scn.students, studentIndex);
			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) + 1;
			assertEquals(claimedPoints.toUpperCase(), bi.getElementText(bi.getReviewerDetailClaimedPoint(teamIndex, position)));
			assertEquals(perceivedPoints.toUpperCase(), bi.getElementText(bi.getReviewerDetailPerceived(teamIndex, position)));
			for (int i = 0; i < pointList1.size(); i++) {
				String student = bi.getElementText(bi.getReviewerDetailToStudent(teamIndex, position, i));
				String toStudent1 = "";
				String toStudent2 = "";
				if (!student.equalsIgnoreCase(s.name)) {
					for (int j = 0; j < teammates.size(); j++) {
						if (teammates.get(j).name.equalsIgnoreCase(student)) {
							toStudent1 = pointList1.get(j);
							toStudent2 = pointList2.get(j);
							continue;
						}
					}
					bi.assertEqualsOr(toStudent1, toStudent2, bi.getElementText(bi.getReviewerDetailToStudentPoint(teamIndex, position, i)));
				}
			}
		}
	}

	public void coordViewRevieweeDetailPoints(Evaluation eval) throws Exception {
		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(eval.courseID, eval.name);
		// click Reviewee:
		bi.waitAndClick(By.id("radio_reviewee"));
		// click sort by name:
		bi.waitAndClick(By.id("button_sortname"));
		// click Detail radio (Reviewer x Detail):
		bi.waitAndClick(By.id("radio_detail"));

		// check points
		for (Student s : scn.students) {
			int studentIndex = scn.students.indexOf(s);
			String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, studentIndex);
			String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, studentIndex);
			List<String> pointList1 = TMAPI.coordGetPointsFromOthersOneLine(scn, studentIndex);
			List<String> pointList2 = TMAPI.coordGetPointsFromOthersTwoLines(scn, studentIndex);
			int teamIndex = getTeamIndex(s.teamName) + 1;
			int position = getStudentIndexInTeam(s.name, s.teamName) + 1;
			List<Student> teammates = getStudentTeammates(scn.students, studentIndex);

			assertEquals(claimedPoints.toUpperCase(),
					bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 2))));
			assertEquals(perceivedPoints.toUpperCase(),
					bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 3))));
			for (int i = 0; i < pointList2.size(); i++) {
				String student = bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position,
						i + 4, 1)));
				String fromStudent1 = "";
				String fromStudent2 = "";
				if (!student.equalsIgnoreCase(s.name)) {
					for (int j = 0; j < teammates.size(); j++) {
						if (teammates.get(j).name.equalsIgnoreCase(student)) {
							fromStudent1 = pointList1.get(j);
							fromStudent2 = pointList2.get(j);

							continue;
						}
					}
					bi.assertEqualsOr(fromStudent1, fromStudent2, bi.getElementText(By.xpath(String.format(
							"//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 2))));
				}
			}
		}
	}


	private int getStudentIndexInTeam(String stuName, String teamName) {
		int idx = 0;
		boolean start = false;
		for (Student s : scn.students) {
			if (s.teamName.equalsIgnoreCase(teamName)) {
				start = true;
			}

			if (start) {
				if (s.name.equalsIgnoreCase(stuName)) {
					return idx;
				} else {
					idx++;
				}
			}
		}
		return -1;
	}

	private int getTeamIndex(String teamName) {
		Iterator<String> it = scn.teams.keySet().iterator();
		int idx = 0;

		while (it.hasNext()) {
			if (it.next().equalsIgnoreCase(teamName)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	private List<Student> getStudentTeammates(List<Student> students, int index) {
		List<Student> list = new ArrayList<Student>();
		Student student = students.get(index);
		for (Student s : students) {
			if (s.teamName.equalsIgnoreCase(student.teamName)) {
				list.add(s);
			}
		}
		return list;
	}

}
