package teammates.testing.script;

import teammates.api.Common;
import teammates.testing.lib.TMAPI;

/**
* Case description:
* 2 courses
* Course 1 with 2 evaluations and 4 students
* Course 2 with 3 evaluations and 4 students
* Each evaluation is in one of the 4 states - AWAITING, OPEN, CLOSED, PUBLISHED
* Only 3 students submitted evaluations on time for the PUBLISHED evaluation state
 * 
 * @author xialin
 * @author Shakthi
 */

public class ImportTestData {

	public static void main(String args[]) throws Exception{
		System.out.println("====[START of Importing test data]====");
		long start = System.currentTimeMillis();
		setupPageVerificationData();
		System.out.println("Finished importing data in "+(System.currentTimeMillis()-start)+" ms");
//		setupOtherTestData();
		System.out.println("====[END of Importing test data]====");	
	}

//	private static void setupOtherTestData() {
//		System.out.println("Creating coordinators for testing ...");
//		TMAPI.deleteCoord(Config.inst().TEAMMATES_COORD_ID);
//		TMAPI.deleteCoord("teammates.test");
//		TMAPI.createCoord(new Coordinator(Config.inst().TEAMMATES_COORD_ID, "Coordinator", "teammates.coord@gmail.com"));
//		TMAPI.createCoord(new Coordinator("teammates.test", "Coordinator", "teammates.test@gmail.com"));
//	}

	private static void setupPageVerificationData() throws Exception{
		System.out.println("Importing data for page verification ...");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"page_verificationNew.json");
		TMAPI.deleteCoordinators(jsonString);
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
//		Scenario sc = Scenario.scenarioForPageVerification(Common.TEST_DATA_FOLDER+"page_verification.json");
//		TMAPI.deleteCoord(sc.coordinator.username);
//		TMAPI.createCoord(sc.coordinator.username, sc.coordinator.name, sc.coordinator.email);
//
//		// -----Course 1-----//
//		TMAPI.createCourse(sc.course, sc.coordinator.username);
//		TMAPI.enrollStudents(sc.course.courseId, sc.course.students);
//		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
//		
//		// ..evaluation 1 OPEN
//		sc.evaluation.p2pcomments = "false";
//		TMAPI.createEvaluation(sc.evaluation);
//		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
//		TMAPI.studentsSubmitFeedbacks(sc.course.students, sc.course.courseId, sc.evaluation.name);
//
//		// ..evaluation 2 PUBLISHED
//		TMAPI.createEvaluation(sc.evaluation2);
//		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);
//		TMAPI.studentsSubmitFeedbacks(sc.students.subList(1, sc.students.size()), sc.course.courseId, sc.evaluation2.name);
//		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);
//		TMAPI.publishEvaluation(sc.course.courseId, sc.evaluation2.name);
//		// -----Course 2-----//
//		TMAPI.createCourse(sc.course2, sc.coordinator.username);
//		TMAPI.enrollStudents(sc.course2.courseId, sc.course2.students);
//		TMAPI.studentsJoinCourse(sc.students, sc.course2.courseId);
//		// ..evaluation 3 CLOSED
//		TMAPI.createEvaluation(sc.evaluation3);
//		TMAPI.openEvaluation(sc.course2.courseId, sc.evaluation3.name);
//		TMAPI.studentsSubmitFeedbacks(sc.course2.students, sc.course2.courseId, sc.evaluation3.name);
//		TMAPI.closeEvaluation(sc.course2.courseId, sc.evaluation3.name);
//
//		// ..evaluation 4 AWAITING
//		TMAPI.createEvaluation(sc.evaluation4);
//		// ..evaluation 5 OPEN
//		TMAPI.createEvaluation(sc.evaluation5);
//		TMAPI.openEvaluation(sc.course2.courseId, sc.evaluation5.name);
	}
}