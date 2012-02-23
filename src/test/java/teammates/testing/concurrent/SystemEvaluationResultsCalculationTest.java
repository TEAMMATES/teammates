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
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class SystemEvaluationResultsCalculationTest extends TestCase {

	BrowserInstance bi;
	Scenario scn;

	@BeforeClass
	public static void classSetup() throws Exception {
		TMAPI.disableEmail();
	}

	@AfterClass
	public static void classTearDown() throws Exception {

	}

	// @Test
	// public void testEvaluationResultsPointCalculation() throws Exception {
	//
	// }

	@Test
	public void testFirstDataSet0() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 0);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 0);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet1() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 1);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 1);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet2() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 2);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 2);
		testScenario();
		BrowserInstancePool.release(bi);

	}

	@Test
	public void testFirstDataSet3() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 3);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 3);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet4() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 3);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 3);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet5() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 4);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 4);
		testScenario();
		BrowserInstancePool.release(bi);

	}

	@Test
	public void testFirstDataSet6() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 5);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 5);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet7() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 6);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 6);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet8() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 7);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 7);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet9() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 8);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 8);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet10() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 9);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 9);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet11() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 10);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 10);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet12() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 11);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 11);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet13() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 12);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 12);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet14() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 13);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 13);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet15() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 14);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 14);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet16() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 15);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 15);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet17() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 16);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 16);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet18() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 17);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 17);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet19() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 18);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 18);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet20() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 19);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 19);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet21() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 20);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 20);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet22() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 21);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 21);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet23() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 22);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 22);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet24() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 23);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 23);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet25() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 24);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 24);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet26() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 25);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 25);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet27() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 26);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 27);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testFirstDataSet28() throws Exception {
		bi = BrowserInstancePool.request();
		System.out.println("TestEvalutionPointCalculation: scenario " + 28);
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 28);
		testScenario();
		BrowserInstancePool.release(bi);
	}

	private void testScenario() throws Exception {
		// before:
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

		// coordinator page testing:
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
		testCoordViewReviewerIndividualPoints();
		testCoordViewRevieweeIndividualPoints();

		testCoordViewRevieweeSummaryPoints();

		testCoordViewReviewerDetailPoints();
		testCoordViewRevieweeDetailPoints();

		// publish result for student page testing:
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation2.name);
		bi.logout();

		testStudentViewResultPoints();

		// after:
		TMAPI.cleanupCourse(scn.course.courseId);
	}

	public void testCoordViewReviewerIndividualPoints() throws Exception {
		coordViewReviewerIndividualPoints(0);
		coordViewReviewerIndividualPoints(1);
	}

	public void coordViewReviewerIndividualPoints(int evalIndex) throws Exception {
		// click Evaluation Tab:
		bi.waitAndClickAndCheck(By.className("t_evaluations"), By.id("viewEvaluation" + evalIndex));
		// click View Results:
		bi.waitAndClickAndCheck(By.id("viewEvaluation" + evalIndex), By.id("viewEvaluationResults0"));
		// click sort by name
		bi.waitAndClick(By.id("button_sortname"));// make sure Alice is the first
		// click View (Reviewer x Summary)
		bi.waitAndClick(By.id("viewEvaluationResults0"));

		// check claimed points:
		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, 0);
		assertEquals(claimedPoints.toUpperCase(), bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2))));
		// assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", 2, 3))));

		// check perceived points:
		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, 0);
		assertEquals(perceivedPoints.toUpperCase(), bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3))));
		// assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));

		// check normalized points given TO teammates:
		List<String> pointList1 = TMAPI.coordGetPointsToOthersOneLine(scn.submissionPoints, 0);
		List<String> pointList2 = TMAPI.coordGetPointsToOthersTwoLines(scn.submissionPoints, 0);

		for (int i = 0; i < pointList1.size(); i++) {
			String student = bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 1)));
			// String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String toStudent1 = "";
			String toStudent2 = "";
			if (!student.equalsIgnoreCase(scn.students.get(0).name)) {
				for (int j = 0; j < scn.students.size(); j++) {
					if (scn.students.get(j).name.equalsIgnoreCase(student)) {
						toStudent1 = pointList1.get(j);
						toStudent2 = pointList2.get(j);
						continue;
					}
				}
				bi.assertEqualsOr(toStudent1, toStudent2,
						bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 2))));
				// assertEquals(toStudent, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}
		}
	}

	public void testCoordViewRevieweeSummaryPoints() throws Exception {
		coordViewRevieweeSummaryPoints(0);
		coordViewRevieweeSummaryPoints(1);
	}

	public void coordViewRevieweeSummaryPoints(int evalIndex) throws Exception {
		// click Evaluation Tab:
		bi.waitAndClickAndCheck(By.className("t_evaluations"), By.id("viewEvaluation" + evalIndex));
		// click View Results:
		bi.justWait();
		bi.waitAndClickAndCheck(By.id("viewEvaluation" + evalIndex), By.id("radio_reviewee"));
		// click Reviewee radio: (Reviewee x Summary)
		bi.waitAndClick(By.id("radio_reviewee"));
		// click sort by name:
		bi.waitAndClick(By.id("button_sortname"));

		for (Student s : scn.students) {
			int studentIndex = scn.students.indexOf(s);
			// check claimed contribution:
			String claimedPoints = TMAPI.coordGetClaimedPoints(scn.submissionPoints, studentIndex);
			assertEquals(claimedPoints, bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 3))));
			// check |perceived - claimed|:
			String diff = TMAPI.coordGetPointDifference(scn.submissionPoints, studentIndex);
			assertEquals(diff, bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 4))));
		}

	}

	public void testCoordViewRevieweeIndividualPoints() throws Exception {
		coordViewRevieweeIndividualPoints(0);
		coordViewRevieweeIndividualPoints(1);
	}

	public void coordViewRevieweeIndividualPoints(int evalIndex) throws Exception {
		// click Evaluation Tab:
		bi.waitAndClickAndCheck(By.className("t_evaluations"), By.id("viewEvaluation" + evalIndex));
		// click View Results:
		bi.waitAndClickAndCheck(By.id("viewEvaluation" + evalIndex), By.id("radio_reviewee"));
		// click Reviewee radio: (Reviewee x Summary)
		bi.waitAndClick(By.id("radio_reviewee"));
		// click sort by name:
		bi.waitAndClick(By.id("button_sortname"));
		// click View:
		bi.waitAndClick(By.id("viewEvaluationResults0"));

		// check claimed points:
		String claimedPoints = "CLAIMED CONTRIBUTIONS: " + TMAPI.coordGetClaimedPoints(scn.submissionPoints, 0);
		assertEquals(claimedPoints.toUpperCase(), bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2))));
		// assertEquals(claimedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2))));

		// check perceived points:
		String perceivedPoints = "PERCEIVED CONTRIBUTIONS: " + TMAPI.coordGetPerceivedPoints(scn.submissionPoints, 0);
		assertEquals(perceivedPoints.toUpperCase(), bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3))));
		// assertEquals(perceivedPoints, getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 4, 2))));

		// check normalized points get FROM teammates:
		List<String> pointList1 = TMAPI.coordGetPointsFromOthersOneLine(scn, 0);
		List<String> pointList2 = TMAPI.coordGetPointsFromOthersTwoLines(scn, 0);

		for (int i = 0; i < pointList1.size(); i++) {
			String student = bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 1)));
			// String student = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 1)));
			String fromStudent1 = "";
			String fromStudent2 = "";
			if (!student.equalsIgnoreCase(scn.students.get(0).name)) {
				for (int j = 0; j < scn.students.size(); j++) {
					if (scn.students.get(j).name.equalsIgnoreCase(student)) {
						fromStudent1 = pointList1.get(j);
						fromStudent2 = pointList2.get(j);
						continue;
					}
				}
				bi.assertEqualsOr(fromStudent1, fromStudent2,
						bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", i + 4, 2))));
				// assertEquals(fromStudent, bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", i + 2, 2))));
			}

		}
	}

	public void testCoordViewReviewerDetailPoints() throws Exception {
		coordViewReviewerDetailPoints(0);
		coordViewReviewerDetailPoints(1);
	}

	public void coordViewReviewerDetailPoints(int evalIndex) throws Exception {
		// click Evaluation Tab:
		bi.waitAndClickAndCheck(By.className("t_evaluations"), By.id("viewEvaluation" + evalIndex));
		// click View Results:
		bi.waitAndClickAndCheck(By.id("viewEvaluation" + evalIndex), By.id("radio_detail"));
		// click sort by name:
		bi.waitAndClick(By.id("button_sortname"));
		// click Detail radio (Reviewer x Detail):
		bi.waitAndClick(By.id("radio_detail"));

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
			assertEquals(claimedPoints.toUpperCase(),
					bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 2))));
			assertEquals(perceivedPoints.toUpperCase(),
					bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIndex, position, 3))));
			for (int i = 0; i < pointList1.size(); i++) {
				String student = bi.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position,
						i + 4, 1)));
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
					bi.assertEqualsOr(toStudent1, toStudent2, bi.getElementText(By.xpath(String.format(
							"//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIndex, position, i + 4, 2))));
				}
			}
		}
	}

	public void testCoordViewRevieweeDetailPoints() throws Exception {
		coordViewRevieweeDetailPoints(0);
		coordViewRevieweeDetailPoints(1);
	}

	public void coordViewRevieweeDetailPoints(int evalIndex) throws Exception {
		// click Evaluation Tab:
		bi.waitAndClickAndCheck(By.className("t_evaluations"), By.id("viewEvaluation" + evalIndex));
		// click View Results:
		bi.waitAndClickAndCheck(By.id("viewEvaluation" + evalIndex), By.id("radio_reviewee"));
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

	public void testStudentViewResultPoints() throws Exception {
		for (int i = 0; i < scn.students.size(); i++) {
			Student s = scn.students.get(i);
			bi.studentLogin(s.email, s.password);

			studentViewResultPoints(scn.course.courseId, scn.evaluation.name, i);
			studentViewResultPoints(scn.course.courseId, scn.evaluation2.name, i);

			bi.logout();
		}
	}

	public void studentViewResultPoints(String courseId, String evalName, int studentIndex) throws Exception {
		bi.clickEvaluationTab();
		// check evaluation table loaded
		bi.waitForElementPresent(By.id("viewEvaluation0"));

		bi.studentClickEvaluationViewResults(courseId, evalName);
		bi.waitForElementPresent(By.className("result_studentform"));

		String claimed = TMAPI.studentGetClaimedPoints(scn.submissionPoints, studentIndex);
		assertEquals(claimed, bi.studentGetEvaluationResultClaimedPoints());

		String perceived = TMAPI.studentGetPerceivedPoints(scn.submissionPoints, studentIndex);
		assertEquals(perceived, bi.studentGetEvaluationResultPerceivedPoints());
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
