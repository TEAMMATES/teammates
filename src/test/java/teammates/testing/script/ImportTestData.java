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

public class ImportTestData {

	public static void main(String args[]) {
		Scenario sc = Scenario.scenarioForPageVerification("target/test-classes/data/page_verification.json");
		TMAPI.cleanupByCoordinator(sc.coordinator.username);

		// -----Course 1-----//
		TMAPI.createCourse(sc.course, sc.coordinator.username);
		TMAPI.enrollStudents(sc.course.courseId, sc.course.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		
		// ..evaluation 1 OPEN
		sc.evaluation.p2pcomments = "false";
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.course.students, sc.course.courseId, sc.evaluation.name);

		// ..evaluation 2 PUBLISHED
		TMAPI.createEvaluation(sc.evaluation2);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(sc.students.subList(1, sc.students.size() - 1), sc.course.courseId, sc.evaluation2.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.publishEvaluation(sc.course.courseId, sc.evaluation2.name);

		// ..evaluation 3 CLOSED
		TMAPI.createEvaluation(sc.evaluation3);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(sc.course.students.subList(1, sc.course.students.size() - 1), sc.course.courseId, sc.evaluation3.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation3.name);

		// ..evaluation 4 AWAITING
		TMAPI.createEvaluation(sc.evaluation4);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation4.name);

		// -----Course 2-----//
		TMAPI.createCourse(sc.course2, sc.coordinator.username);

	}
}