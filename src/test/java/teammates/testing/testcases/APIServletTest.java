package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.APIServlet;
import teammates.Common;
import teammates.DataBundle;
import teammates.Datastore;
import teammates.exception.EntityDoesNotExistException;
import teammates.jdo.Coordinator;
import teammates.jdo.Course;
import teammates.jdo.Evaluation;
import teammates.jdo.Student;
import teammates.jdo.Submission;
import teammates.jdo.TeamFormingLog;
import teammates.jdo.TeamFormingSession;
import teammates.jdo.TeamProfile;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;

public class APIServletTest extends BaseTestCase {
	private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final static APIServlet apiServlet = new APIServlet();
	private static String TEST_DATA_FOLDER = "src/test/resources/data/";
	private static Gson gson = Common.getTeammatesGson();
	String jsonString = SharedLib.getFileContents(TEST_DATA_FOLDER
			+ "typicalDataBundle.json");
	private DataBundle dataBundle;
	
	
	@BeforeClass
	public static void setUp() {
		printTestClassHeader(getNameOfThisClass());
		/** LocalServiceTestHelper is supposed to run in the same timezone  as Dev server and production server i.e. (i.e. UTC timezone),
		 * as stated in https://developers.google.com/appengine/docs/java/tools/localunittesting/javadoc/com/google/appengine/tools/development/testing/LocalServiceTestHelper#setTimeZone%28java.util.TimeZone%29
		 * 
		 * But it seems Dev server does not run on UTC timezone, but it runs on "GMT+8:00" (Possibly, a bug).
		 * Therefore, I'm changing timeZone of LocalServiceTestHelper to match the Dev server.
		 * But note that tests that run on Dev server might fail on Production server due to this problem.
		 * We need to find a fix. 
		 */
		helper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		helper.setUp();
		try{
			apiServlet.init();
			Datastore.initialize();
		}catch(IllegalStateException e){
			System.out.println("Error in initializing local datastore :");
			e.printStackTrace();
		} catch (ServletException e) {
			System.out.println("Error in initializing servlet");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPersistDataBundle() throws Exception {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, Coordinator> coords = dataBundle.coords;
		for (Coordinator coord : coords.values()) {
			apiServlet.deleteCoord(coord.getGoogleID());
		} 
		apiServlet.persistNewDataBundle(jsonString);
		verifyPresentInDatastore(jsonString);
	}
	
	
	
	private void verifyPresentInDatastore(String dataBundleJsonString) throws EntityDoesNotExistException {

		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, Coordinator> coords = data.coords;
		for (Coordinator expectedCoord : coords.values()) {
			verifyPresentInDatastore(expectedCoord);
		}

		HashMap<String, Course> courses = data.courses;
		for (Course expectedCourse : courses.values()) {
			verifyPresentInDatastore(expectedCourse);
		}

		HashMap<String, Student> students = data.students;
		for (Student expectedStudent : students.values()) {
			verifyPresentInDatastore(expectedStudent);
		}

		HashMap<String, Evaluation> evaluations = data.evaluations;
		for (Evaluation expectedEvaluation : evaluations.values()) {
			verifyPresentInDatastore(expectedEvaluation);
		}

		HashMap<String, Submission> submissions = data.submissions;
		for (Submission expectedSubmission : submissions.values()) {
			verifyPresentInDatastore(expectedSubmission);
		}

		HashMap<String, TeamFormingSession> teamFormingSessions = data.teamFormingSessions;
		for (TeamFormingSession expectedTeamFormingSession : teamFormingSessions
				.values()) {
			verifyPresentInDatastore(expectedTeamFormingSession);
		}

		HashMap<String, TeamProfile> teamProfiles = data.teamProfiles;
		for (TeamProfile expectedTeamProfile : teamProfiles.values()) {
			verifyPresentInDatastore(expectedTeamProfile);
		}

		HashMap<String, TeamFormingLog> teamFormingLogs = data.teamFormingLogs;
		for (TeamFormingLog expectedTeamFormingLogEntry : teamFormingLogs
				.values()) {
			verifyPresentInDatastore(expectedTeamFormingLogEntry);
		}
		
	}



	public void testDeleteStudent() throws Exception{
		printTestCaseHeader(getNameOfThisMethod());
		refreshDataInDatastore();
		
		Submission submissionFromS1C1ToS2C1 = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
		verifyPresentInDatastore(submissionFromS1C1ToS2C1);
		Submission submissionFromS2C1ToS1C1 = dataBundle.submissions.get("submissionFromS2C1ToS1C1");
		verifyPresentInDatastore(submissionFromS2C1ToS1C1);
		Submission submissionFromS1C1ToS1C1 = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);
		
		Student student2InCourse1 = dataBundle.students.get("student2InCourse1");
		verifyPresentInDatastore(student2InCourse1);

		apiServlet.deleteStudent(student2InCourse1.getCourseID(), student2InCourse1.getEmail());
		verifyAbsentInDatastore(student2InCourse1);
		
		// verify that other students in the course are intact
		Student student1InCourse1 = dataBundle.students.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);
		
		// try to delete the student again. should succeed.
		apiServlet.deleteStudent(student2InCourse1.getCourseID(), student2InCourse1.getEmail());
		
		verifyAbsentInDatastore(submissionFromS1C1ToS2C1);
		verifyAbsentInDatastore(submissionFromS2C1ToS1C1);
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);
	}

	private void verifyAbsentInDatastore(Submission submission) {
		assertEquals(null, apiServlet.getSubmission(submission.getCourseID(),submission.getEvaluationName(),submission.getFromStudent(), submission.getToStudent()));
	}

	private void verifyAbsentInDatastore(Student student) {
		assertEquals(null, apiServlet.getStudent(student.getCourseID(), student.getEmail()));
	}

	private void verifyPresentInDatastore(Student expectedStudent) {
		Student actualStudent = apiServlet.getStudent(expectedStudent.getCourseID(), expectedStudent.getEmail());
		assertEquals(gson.toJson(expectedStudent),gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(Submission expected) {
		Submission actual = apiServlet.getSubmission(expected.getCourseID(),expected.getEvaluationName(),expected.getFromStudent(), expected.getToStudent());
		expected.id = actual.id;
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}
	
	private void verifyPresentInDatastore(TeamFormingLog expected) {
		List<TeamFormingLog> actualList = apiServlet.getTeamFormingLog(expected.getCourseID());
		assertTrue(isLogEntryInList(expected,actualList));
	}

	private void verifyPresentInDatastore(TeamProfile expected) {
		TeamProfile actual = apiServlet.getTeamProfile(expected.getCourseID(), expected.getTeamName());
		expected.id = actual.id;
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}

	private void verifyPresentInDatastore(
			TeamFormingSession expected) {
		TeamFormingSession actual = apiServlet.getTfs(expected.getCourseID());
		expected.id = actual.id;
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}

	private void verifyPresentInDatastore(Evaluation expected) {
		Evaluation actual = apiServlet.getEvaluation(expected.getCourseID(), expected.getName());
		expected.id = actual.id;
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}

	private void verifyPresentInDatastore(Course expected) throws EntityDoesNotExistException {
		Course actual = apiServlet.getCourse(expected.getID());
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}

	private void verifyPresentInDatastore(Coordinator expected) {
		Coordinator actual = apiServlet.getCoord(expected.getGoogleID());
		assertEquals(gson.toJson(expected),gson.toJson(actual));
	}

	private void refreshDataInDatastore() throws Exception {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, Coordinator> coords = dataBundle.coords;
		for (Coordinator coord : coords.values()) {
			apiServlet.deleteCoord(coord.getGoogleID());
		} 
		apiServlet.persistNewDataBundle(jsonString);
	}
	
	private boolean isLogEntryInList(TeamFormingLog teamFormingLogEntry,
			List<TeamFormingLog> teamFormingLogEntryList) {
		for (TeamFormingLog logEntryInList : teamFormingLogEntryList) {
			if (teamFormingLogEntry.getCourseID().equals(
					logEntryInList.getCourseID())
					&& teamFormingLogEntry.getMessage().getValue()
							.equals(logEntryInList.getMessage().getValue())
					&& teamFormingLogEntry.getStudentEmail().equals(
							logEntryInList.getStudentEmail())
					&& teamFormingLogEntry.getStudentName().equals(
							logEntryInList.getStudentName())
					&& teamFormingLogEntry.getTime().toString()
							.equals(logEntryInList.getTime().toString())) {
				return true;
			}
		}
		return false;
	}
	
	@AfterClass
	public static void tearDown() {
		apiServlet.destroy();
		helper.tearDown();
		printTestClassFooter("CoordCourseAddApiTest");
	}

}
