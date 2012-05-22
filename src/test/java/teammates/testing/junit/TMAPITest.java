package teammates.testing.junit;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import teammates.Common;
import teammates.DataBundle;
import teammates.jdo.*;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import static org.junit.Assert.*;

public class TMAPITest {

	// TODO: change this to 'target' directory
	private String TEST_DATA_FOLDER = "src/test/resources/data/";

	@BeforeClass
	public static void setUp() {

	}

	@AfterClass
	public static void tearDown() {

	}

	@Test
	public void testGetCoursesByCoordId() {

		String[] courses = TMAPI.getCoursesByCoordId("nonExistentCoord");
		// testing for non-existent coordinator
		assertEquals("[]", Arrays.toString(courses));

		// TODO: cascade delete coordinators and recreate
		String coord1Id = "AST.TGCBCI.coord1";
		String course1OfCoord1 = "AST.TGCBCI.course1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.course2OfCoord1";
		TMAPI.createCourse(new teammates.testing.object.Course(course1OfCoord1,
				"APIServletTest testGetCoursesByCoordId course1OfCoord1"),
				coord1Id);
		TMAPI.createCourse(new teammates.testing.object.Course(course2OfCoord1,
				"APIServletTest testGetCoursesByCoordId course2OfCoord1"),
				coord1Id);

		// add a course that belongs to a different coordinator
		String coord2Id = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.course1OfCoord2";
		TMAPI.createCourse(new teammates.testing.object.Course(course1OfCoord2,
				"APIServletTest testGetCoursesByCoordId course1OfCoord2"),
				coord2Id);

		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course1OfCoord1 + ", " + course2OfCoord1 + "]",
				Arrays.toString(courses));

		// TODO: delete coordinators
	}

	@Test
	public void testDeleteCourseByIdNonCascade() throws InterruptedException {
		// TODO: cascade delete coordinators and recreate
		String coord1Id = "AST.TDCBINC.coord1";
		String course1OfCoord1 = "AST.TDCBINC.course1OfCoord1";
		String course2OfCoord1 = "AST.TDCBINC.course2OfCoord1";
		TMAPI.createCourse(
				new teammates.testing.object.Course(course1OfCoord1,
						"APIServletTest testDeleteCourseByIdNonCascade course1OfCoord1"),
				coord1Id);
		TMAPI.createCourse(
				new teammates.testing.object.Course(course2OfCoord1,
						"APIServletTest testDeleteCourseByIdNonCascade course2OfCoord1"),
				coord1Id);

		String[] courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course1OfCoord1 + ", " + course2OfCoord1 + "]",
				Arrays.toString(courses));

		TMAPI.deleteCourseByIdNonCascade(course1OfCoord1);
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course2OfCoord1 + "]", Arrays.toString(courses));

		// trying to delete non-existent course
		TMAPI.deleteCourseByIdNonCascade("AST.TDCBINC.nonexistentcourse");
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course2OfCoord1 + "]", Arrays.toString(courses));

		TMAPI.deleteCourseByIdNonCascade(course2OfCoord1);
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[]", Arrays.toString(courses));

	}

	@Test
	public void testCoordManipulation() {

		String coord1Id = "AST.testCoordManipulation.coord1@somemail.com";
		String coord1Name = "AST TCM Coordinator1";
		String coord1Email = "AST.testCoordManipulation.coord1@gmail.com";

		// test for accessing non-existent coord
		assertEquals(
				"null",
				TMAPI.getCoordAsJason("AST.testCoordManipulation.nonexistentId"));

		// delete coord if already exists
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		String coordAsJason = TMAPI.getCoordAsJason(coord1Id);
		assertEquals("null", coordAsJason);

		// try to delete again, to ensure it does not crash
		TMAPI.deleteCoordByIdNonCascading(coord1Id);

		// test creation, and accessing existing coord
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);
		Coordinator expectedCoord1 = new Coordinator(coord1Id, coord1Name,
				coord1Email);
		String expectedCoord1JsonPrettyPrinted = Common.getTeammatesGson()
				.toJson(expectedCoord1);
		String actualCoord1JsonPrettyPrinted = TMAPI.reformatJasonString(
				TMAPI.getCoordAsJason(coord1Id), Coordinator.class);
		assertEquals(expectedCoord1JsonPrettyPrinted,
				actualCoord1JsonPrettyPrinted);

		// creating the same coord, to ensure it does not crash
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);

		// delete existing coord
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		assertEquals("null", coordAsJason);

		// TODO: test for coord cascade delete
	}

	@Test
	public void testDataBundle() {
		String jsonString = SharedLib.getFileContents(TEST_DATA_FOLDER
				+ "typicalDataBundle.json");
		Gson gson = Common.getTeammatesGson();
	
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
	
		Coordinator typicalCoord1 = data.coords.get("typicalCoord1");
		assertEquals("idOfTypicalCoord1", typicalCoord1.getGoogleID());
		assertEquals("Typical Coordinator1", typicalCoord1.getName());
		assertEquals("typicalCoord1@gmail.com", typicalCoord1.getEmail());
	
		Coordinator typicalCoord2 = data.coords.get("typicalCoord2");
		assertEquals("idOfTypicalCoord2", typicalCoord2.getGoogleID());
		assertEquals("Typical Coordinator2", typicalCoord2.getName());
		assertEquals("typicalCoord2@gmail.com", typicalCoord2.getEmail());
	
		Course course1 = data.courses.get("course1");
		assertEquals("idOfCourse1", course1.getID());
		assertEquals("course 1 name", course1.getName());
		assertEquals("idOfTypicalCoord1", course1.getCoordinatorID());
	
		Student student1InCourse1 = data.students.get("student1InCourse1");
		assertEquals("student1InCourse1", student1InCourse1.getID());
		assertEquals("student1 In Course1", student1InCourse1.getName());
		assertEquals("Team 1.1", student1InCourse1.getTeamName());
		assertEquals("comment for student1InCourse1",
				student1InCourse1.getComments());
		assertEquals("profile summary for student1InCourse1",
				student1InCourse1.getProfileSummary());
		assertEquals("idOfCourse1", student1InCourse1.getCourseID());
		assertEquals("profiledetail for student1InCourse1", student1InCourse1
				.getProfileDetail().getValue());
	
		Student student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse2", student2InCourse2.getID());
		assertEquals("student2 In Course2", student2InCourse2.getName());
		assertEquals("Team 2.1", student2InCourse2.getTeamName());
	
		Evaluation evaluation1 = data.evaluations.get("evaluation1InCourse1");
		assertEquals("evaluation1 In Course1", evaluation1.getName());
		assertEquals("idOfCourse1", evaluation1.getCourseID());
		assertEquals("instructions for evaluation1InCourse1",
				evaluation1.getInstructions());
		assertEquals(10, evaluation1.getGracePeriod());
		assertEquals(true, evaluation1.isCommentsEnabled());
		assertEquals("Sun Apr 01 23:59:00 SGT 2012", evaluation1.getStart()
				.toString());
		assertEquals("Tue Apr 30 23:59:00 SGT 2013", evaluation1.getDeadline()
				.toString());
		assertEquals(true, evaluation1.isActivated());
		assertEquals(false, evaluation1.isPublished());
		assertEquals(2.0, evaluation1.getTimeZone(), 0.01);
	
		Evaluation evaluation2 = data.evaluations.get("evaluation2InCourse1");
		assertEquals("evaluation2 In Course1", evaluation2.getName());
		assertEquals("idOfCourse1", evaluation2.getCourseID());
	
		Submission submissionFromS1C1ToS2C1 = data.submissions
				.get("submissionFromS1C1ToS2C1");
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.getFromStudent());
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.getToStudent());
		assertEquals("idOfCourse1", submissionFromS1C1ToS2C1.getCourseID());
		assertEquals("evaluation1 In Course1",
				submissionFromS1C1ToS2C1.getEvaluationName());
		assertEquals(10, submissionFromS1C1ToS2C1.getPoints());
		assertEquals("Team 1.1", submissionFromS1C1ToS2C1.getTeamName());
		// since justification filed is of Text type, we have to use it's
		// .getValue() method to access the string contained inside it
		assertEquals(
				"justification of student1InCourse1 rating to student2InCourse1",
				submissionFromS1C1ToS2C1.getJustification().getValue());
		assertEquals("comments from student1InCourse1 to student2InCourse1",
				submissionFromS1C1ToS2C1.getCommentsToStudent().getValue());
	
		Submission submissionFromS2C1ToS1C1 = data.submissions
				.get("submissionFromS2C1ToS1C1");
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS2C1ToS1C1.getFromStudent());
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS2C1ToS1C1.getToStudent());
	
		TeamFormingSession tfsInCourse1 = data.teamFormingSessions
				.get("tfsInCourse1");
		assertEquals("idOfCourse1", tfsInCourse1.getCourseID());
		assertEquals(8.0, tfsInCourse1.getTimeZone(), 0.01);
		assertEquals("Sun Apr 01 23:59:00 SGT 2012", tfsInCourse1.getStart()
				.toString());
		assertEquals("Sun Apr 15 23:59:00 SGT 2012", tfsInCourse1.getDeadline()
				.toString());
		assertEquals("instructions for tfsInCourse1",
				tfsInCourse1.getInstructions());
		assertEquals("profile template for tfsInCourse1",
				tfsInCourse1.getProfileTemplate());
		assertEquals(10, tfsInCourse1.getGracePeriod());
		assertEquals(false, tfsInCourse1.isActivated());
	
		TeamProfile profileOfTeam1_1 = data.teamProfiles
				.get("profileOfTeam1.1");
		assertEquals("idOfCourse1", profileOfTeam1_1.getCourseID());
		assertEquals("course 1 name", profileOfTeam1_1.getCourseName());
		assertEquals("Team 1.1", profileOfTeam1_1.getTeamName());
		assertEquals("team profile of Team 1.1", profileOfTeam1_1
				.getTeamProfile().getValue());
	
		TeamFormingLog tfsLogMessageForTfsInCourse1 = data.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse1");
		assertEquals("idOfCourse1", tfsLogMessageForTfsInCourse1.getCourseID());
		assertEquals("student1 In Course1",
				tfsLogMessageForTfsInCourse1.getStudentName());
		assertEquals("student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.getStudentEmail());
		assertEquals("Sun Jan 01 01:01:00 SGT 2012",
				tfsLogMessageForTfsInCourse1.getTime().toString());
		assertEquals("log message 1 of course1, student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.getMessage().getValue());
	}

	@Test
	public void testPersistenceAndDeletion() {
		String jsonString = SharedLib.getFileContents(TEST_DATA_FOLDER
				+ "typicalDataBundle.json");
		Gson gson = Common.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		
		//delete coordinators to avoid clashing with existing data
		TMAPI.deleteCoordinators(jsonString);

		// persist test data 
		String status = TMAPI.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyExistInDatastore(jsonString);
		
		// ----------deleting Coordinator entities-------------------------
		Coordinator typicalCoord1 = data.coords.get("typicalCoord1");
		verifyPresentInDatastore(typicalCoord1);
		status = TMAPI.deleteCoord(typicalCoord1.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord1);
		
		Coordinator typicalCoord2 = data.coords.get("typicalCoord2");
		status = TMAPI.deleteCoord(typicalCoord2.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord2);
		
		//try to delete again. should succeed.
		status = TMAPI.deleteCoord(typicalCoord2.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		//recreate data. this should succeed if all previous data were deleted
		status = TMAPI.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		// ----------deleting TeamProfile entities-------------------------

		// delete one TeamProfile and confirm it is already deleted
		TeamProfile teamProfileOfTeam1_1 = data.teamProfiles
				.get("profileOfTeam1.1");
		verifyPresentInDatastore(teamProfileOfTeam1_1);
		status = TMAPI.deleteTeamProfile(teamProfileOfTeam1_1.getCourseID(),
				teamProfileOfTeam1_1.getTeamName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(teamProfileOfTeam1_1);

		// verify if the other TeamProfile in the same course is intact
		verifyPresentInDatastore(data.teamProfiles
				.get("profileOfTeam1.2"));

		// try to delete it again, should succeed
		status = TMAPI.deleteTeamProfile(teamProfileOfTeam1_1.getCourseID(),
				teamProfileOfTeam1_1.getTeamName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// ----------deleting TeamFormingLog entities-------------------------

		// delete TeamFormingLog of one course and verify it is deleted
		TeamFormingLog tfsLogMessage1ForTfsInCourse1 = data.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse1");
		verifyPresentInDatastore(tfsLogMessage1ForTfsInCourse1);
		status = TMAPI.deleteTeamFormingLog(tfsLogMessage1ForTfsInCourse1
				.getCourseID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse1);

		// try to delete it again, the operation should succeed
		status = TMAPI.deleteTeamFormingLog(tfsLogMessage1ForTfsInCourse1
				.getCourseID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// ----------deleting TeamFormingSession entities------------------
		
		//verify at least one team profile exists for this Tfs
		TeamProfile profileOfTeam3_1 = data.teamProfiles.get("profileOfTeam3.1");
		verifyPresentInDatastore(profileOfTeam3_1);
		//verify at least one log entry exists for this Tfs
		TeamFormingLog tfsLogMessage1ForTfsInCourse3 = data.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse3");
		verifyPresentInDatastore(tfsLogMessage1ForTfsInCourse3);
		
		TeamFormingSession tfsInCourse3 = data.teamFormingSessions
				.get("tfsInCourse3");
		verifyPresentInDatastore(tfsInCourse3);
		status = TMAPI.deleteTfs(tfsInCourse3.getCourseID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(tfsInCourse3);

		// just to be sure, check if another Tfs remains intact
		verifyPresentInDatastore(data.teamFormingSessions.get("tfsInCourse2"));

		// try to delete it again. should succeed.
		status = TMAPI.deleteTfs(tfsInCourse3.getCourseID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		verifyAbsentInDatastore(profileOfTeam3_1);
		
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse3);
		
		//TODO: check for resetting submissions
		//TODO: check/implement cascade delete for students

		// ----------deleting Evaluation entities-------------------------

		// check the existence of a submission that will be deleted along with the evaluation
		Submission subInDeletedEvaluation = data.submissions
				.get("submissionFromS1C1ToS2C1");
		verifyPresentInDatastore(subInDeletedEvaluation);
		
		//delete the evaluation and verify it is deleted
		Evaluation evaluation1InCourse1 = data.evaluations
				.get("evaluation1InCourse1");
		verifyPresentInDatastore(evaluation1InCourse1);
		status = TMAPI.deleteEvaluation(evaluation1InCourse1.getCourseID(),
				evaluation1InCourse1.getName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(evaluation1InCourse1);
		
		//verify that the submission is deleted too
		verifyAbsentInDatastore(subInDeletedEvaluation);

		// try to delete the evaluation again, should succeed
		status = TMAPI.deleteEvaluation(evaluation1InCourse1.getCourseID(),
				evaluation1InCourse1.getName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		//verify that the other evaluation in the same course is intact
		Evaluation evaluation2InCourse1 = data.evaluations
				.get("evaluation2InCourse1");
		verifyPresentInDatastore(evaluation2InCourse1);
		
		// ----------deleting Student entities-------------------------
		Student student1InCourse1 = data.students.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);
		status = TMAPI.deleteStudent(student1InCourse1.getCourseID(), student1InCourse1.getEmail());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(student1InCourse1);
		
		// verify that other students in the course are intact
		Student student2InCourse1 = data.students.get("student2InCourse1");
		verifyPresentInDatastore(student2InCourse1);
		
		// try to delete the student again. should succeed.
		status = TMAPI.deleteStudent(student1InCourse1.getCourseID(), student1InCourse1.getEmail());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);	
		
		// ----------deleting Course entities-------------------------
		
		Course course2 = data.courses.get("course2");
		verifyPresentInDatastore(course2);
		status = TMAPI.deleteCourse(course2.getID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(status, course2);
		
		//check if related student entities are also deleted
		Student student2InCourse2 = data.students.get("student2InCourse2");
		verifyAbsentInDatastore(student2InCourse2);
		
		//check if related evaluation entities are also deleted
		Evaluation evaluation1InCourse2 = data.evaluations.get("evaluation1InCourse2");
		verifyAbsentInDatastore(evaluation1InCourse2);
		
		//check if related team profile entities are also deleted
		TeamProfile teamProfileOfTeam2_1 = data.teamProfiles
				.get("profileOfTeam2.1");
		verifyAbsentInDatastore(teamProfileOfTeam2_1);
		
		//check if related Tfs entities are also deleted
		TeamFormingSession tfsInCourse2 = data.teamFormingSessions
				.get("tfsInCourse2");
		verifyAbsentInDatastore(tfsInCourse2);
		
		//check if related TeamFormingLog entities are also deleted
		TeamFormingLog tfsLogMessage1ForTfsInCourse2 = data.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse2");
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse2);
	}

	private void verifyAbsentInDatastore(String status, Course course2) {
		assertTrue("unexpected status"+status,TMAPI.getCourseAsJason(course2.getID()).startsWith(Common.BACKEND_STATUS_FAILURE));
	}

	private void verifyAbsentInDatastore(Student student1InCourse1) {
		assertEquals("null",TMAPI.getStudentAsJason(student1InCourse1.getCourseID(), student1InCourse1.getEmail()));
	}

	private void verifyAbsentInDatastore(Evaluation evaluation1InCourse1) {
		assertEquals("null", TMAPI.getEvaluationAsJason(
				evaluation1InCourse1.getCourseID(),
				evaluation1InCourse1.getName()));
	}

	private void verifyAbsentInDatastore(Submission subInDeletedEvaluation) {
		String submissionAsJason = TMAPI.getSubmissionAsJason(
				subInDeletedEvaluation.getCourseID(),
				subInDeletedEvaluation.getEvaluationName(),
				subInDeletedEvaluation.getFromStudent(),
				subInDeletedEvaluation.getToStudent());
		assertEquals("null", submissionAsJason);
	}

	private void verifyAbsentInDatastore(TeamFormingSession tfsInCourse3) {
		assertEquals("null", TMAPI.getTfsAsJason(tfsInCourse3.getCourseID()));
	}

	private void verifyTeamFormingLogEmptyInDatastore(
			TeamFormingLog tfsLogMessage1ForTfsInCourse1) {
		assertEquals("[]",
				TMAPI.getTeamFormingLogAsJason(tfsLogMessage1ForTfsInCourse1
						.getCourseID()));
	}

	private void verifyAbsentInDatastore(
			TeamProfile teamProfileOfTeam1_1) {
		assertEquals("null", TMAPI.getTeamProfileAsJason(
				teamProfileOfTeam1_1.getCourseID(),
				teamProfileOfTeam1_1.getTeamName()));
	}

	private void verifyExistInDatastore(String dataBundleJsonString) {
		Gson gson = Common.getTeammatesGson();

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

	private void verifyPresentInDatastore(TeamFormingSession expectedTeamFormingSession) {
		Gson gson = Common.getTeammatesGson();
		String teamFormingSessionsJsonString = TMAPI
				.getTfsAsJason(expectedTeamFormingSession.getCourseID());
		TeamFormingSession actualTeamFormingSession = gson.fromJson(
				teamFormingSessionsJsonString, TeamFormingSession.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		expectedTeamFormingSession.id = actualTeamFormingSession.id;
		assertEquals(gson.toJson(expectedTeamFormingSession),
				gson.toJson(actualTeamFormingSession));
	}

	private void verifyPresentInDatastore(Submission expectedSubmission) {
		Gson gson = Common.getTeammatesGson();
		String submissionsJsonString = TMAPI.getSubmissionAsJason(
				expectedSubmission.getCourseID(),
				expectedSubmission.getEvaluationName(),
				expectedSubmission.getFromStudent(),
				expectedSubmission.getToStudent());
		Submission actualSubmission = gson.fromJson(submissionsJsonString,
				Submission.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		expectedSubmission.id = actualSubmission.id;
		assertEquals(gson.toJson(expectedSubmission),
				gson.toJson(actualSubmission));
	}

	private void verifyPresentInDatastore(Evaluation expectedEvaluation) {
		Gson gson = Common.getTeammatesGson();
		String evaluationJsonString = TMAPI.getEvaluationAsJason(
				expectedEvaluation.getCourseID(),
				expectedEvaluation.getName());
		Evaluation actualEvaluation = gson.fromJson(evaluationJsonString,
				Evaluation.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		expectedEvaluation.id = actualEvaluation.id;
		assertEquals(gson.toJson(expectedEvaluation),
				gson.toJson(actualEvaluation));
	}

	private void verifyPresentInDatastore(Student expectedStudent) {
		Gson gson = Common.getTeammatesGson();
		String studentJsonString = TMAPI.getStudentAsJason(
				expectedStudent.getCourseID(), expectedStudent.getEmail());
		Student actualStudent = gson.fromJson(studentJsonString,
				Student.class);
		assertEquals(gson.toJson(expectedStudent),
				gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(Course expectedCourse) {
		Gson gson = Common.getTeammatesGson();
		String courseJsonString = TMAPI.getCourseAsJason(expectedCourse
				.getID());
		Course actualCourse = gson.fromJson(courseJsonString, Course.class);
		assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
	}

	private void verifyPresentInDatastore(TeamFormingLog expectedTeamFormingLogEntry) {
		Gson gson = Common.getTeammatesGson();
		String teamFormingLogJsonString = TMAPI
				.getTeamFormingLogAsJason(expectedTeamFormingLogEntry
						.getCourseID());
		Type collectionType = new TypeToken<ArrayList<TeamFormingLog>>() {
		}.getType();
		ArrayList<TeamFormingLog> actualTeamFormingLogsForCourse = gson
				.fromJson(teamFormingLogJsonString, collectionType);
		String errorMessage = gson.toJson(expectedTeamFormingLogEntry)
				+ "\n--> was not found in -->\n"
				+ TMAPI.reformatJasonString(teamFormingLogJsonString,
						collectionType);
		assertTrue(
				errorMessage,
				isLogEntryInList(expectedTeamFormingLogEntry,
						actualTeamFormingLogsForCourse));
	}

	private void verifyPresentInDatastore(TeamProfile expectedTeamProfile) {
		Gson gson = Common.getTeammatesGson();
		String teamProfileJsonString = TMAPI.getTeamProfileAsJason(
				expectedTeamProfile.getCourseID(),
				expectedTeamProfile.getTeamName());
		TeamProfile actualTeamProfile = gson.fromJson(
				teamProfileJsonString, TeamProfile.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		expectedTeamProfile.id = actualTeamProfile.id;
		assertEquals(gson.toJson(expectedTeamProfile),
				gson.toJson(actualTeamProfile));
	}

	private void verifyPresentInDatastore(Coordinator expectedCoord) {
		Gson gson = Common.getTeammatesGson();
		String coordJsonString = TMAPI.getCoordAsJason(expectedCoord
				.getGoogleID());
		Coordinator actualCoord = gson.fromJson(coordJsonString,
				Coordinator.class);
		assertEquals(gson.toJson(expectedCoord), gson.toJson(actualCoord));
	}
	
	private void verifyAbsentInDatastore(Coordinator expectedCoord) {
		assertEquals("null", TMAPI.getCoordAsJason(expectedCoord.getGoogleID()));
	}

	private boolean isLogEntryInList(TeamFormingLog teamFormingLogEntry,
			ArrayList<TeamFormingLog> teamFormingLogEntryList) {
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

}
