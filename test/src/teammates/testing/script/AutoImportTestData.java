package teammates.testing.script;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Case description: 4 students in a course, only 3 submitted evaluations on
 * time. The evaluation is closed and ready for view
 * 
 * Test: 1.edit empty evaluation record 2.edit submitted evaluation results
 * 3.publish evaluation results after editing
 * 
 * @author xialin
 * 
 */

public class AutoImportTestData {

	public static void main(String args[]) {
		Scenario sc = Scenario.fromJSONFile("./scenario2.json");
		TMAPI.cleanup();

		// -----Course 1-----//
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.course.students);
		// ..evaluation 1 OPEN
		sc.evaluation.p2pcomments = "false";
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.course.students, sc.course.courseId,
				sc.evaluation.name);

		// ..evaluation 2 PUBLISHED
		TMAPI.createEvaluation(sc.evaluation2);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);

		// TMAPI.firstStudentDidNotSubmitFeedbacks(sc.students, sc.course.courseId,
		// sc.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(
				sc.students.subList(1, sc.students.size() - 1), sc.course.courseId,
				sc.evaluation2.name);

		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.publishEvaluation(sc.course.courseId, sc.evaluation2.name);

		Scenario nsc = Scenario.newScenario("./scenario.json");

		// -----Course 2-----//
		TMAPI.createCourse(nsc.course2);
		TMAPI.enrollStudents(nsc.course2.courseId, nsc.course2.students);
		// ..evaluation 3 CLOSED
		TMAPI.createEvaluation(nsc.evaluation3);
		TMAPI.studentsJoinCourse(nsc.course2.students, nsc.course2.courseId);
		TMAPI.openEvaluation(nsc.course2.courseId, nsc.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(
				nsc.course2.students.subList(1, nsc.course2.students.size() - 1),
				nsc.course2.courseId, nsc.evaluation3.name);
		TMAPI.closeEvaluation(nsc.course2.courseId, nsc.evaluation3.name);

		// ..evaluation 4 AWAITING
		TMAPI.createEvaluation(nsc.evaluation4);
		TMAPI.studentsJoinCourse(nsc.course2.students, nsc.course2.courseId);
		// TMAPI.openEvaluation(nsc.course2.courseId, nsc.evaluation4.name);
		// TMAPI.studentsSubmitFeedbacks(nsc.course2.students, nsc.course2.courseId,
		// nsc.evaluation4.name);
		// TMAPI.closeEvaluation(nsc.course2.courseId, nsc.evaluation4.name);
		// TMAPI.publishEvaluation(nsc.course2.courseId, nsc.evaluation4.name);

	}
}