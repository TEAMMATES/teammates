package teammates.testing.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.DataBundle;
import teammates.exception.InvalidParametersException;
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
import teammates.testing.testcases.BaseTestCase;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TMAPITest extends BaseTestCase{

	private static Gson gson = Common.getTeammatesGson();
	String jsonString = Common.getFileContents(Common.TEST_DATA_FOLDER + "typicalDataBundle.json");
	private DataBundle dataBundle;

	@BeforeClass
	public static void setUp() {

	}

	@AfterClass
	public static void tearDown() {
		//TODO: clean up data
	}
	
	//-------------------[Testing system-level methods]-------------------------


	@Test
	public void testPersistenceAndDeletion() {
		printTestCaseHeader();
		
		//to avoid clashes with existing data
		TMAPI.deleteCoordinators(jsonString);
		
		//check if deleteCoordinators worked
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		for(Coordinator coord: dataBundle.coords.values()){
			verifyAbsentInDatastore(coord);
		}
		
		//check persisting
		String status = TMAPI.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(jsonString);
		
		// ----------deleting Coordinator entities-------------------------
		Coordinator typicalCoord1 = dataBundle.coords.get("typicalCoord1");
		verifyPresentInDatastore(typicalCoord1);
		status = TMAPI.deleteCoord(typicalCoord1.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord1);
		
		Coordinator typicalCoord2 = dataBundle.coords.get("typicalCoord2");
		status = TMAPI.deleteCoord(typicalCoord2.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord2);
		
		//try to delete again. should succeed.
		status = TMAPI.deleteCoord(typicalCoord2.getGoogleID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		status = TMAPI.deleteCoord("idOfTypicalCoord3");
		
		//recreate data. this should succeed if all previous data were deleted
		status = TMAPI.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		// ----------deleting TeamProfile entities-------------------------
	
		// delete one TeamProfile and confirm it is already deleted
		TeamProfile teamProfileOfTeam1_1 = dataBundle.teamProfiles
				.get("profileOfTeam1.1");
		verifyPresentInDatastore(teamProfileOfTeam1_1);
		status = TMAPI.deleteTeamProfile(teamProfileOfTeam1_1.getCourseID(),
				teamProfileOfTeam1_1.getTeamName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(teamProfileOfTeam1_1);
	
		// verify if the other TeamProfile in the same course is intact
		verifyPresentInDatastore(dataBundle.teamProfiles
				.get("profileOfTeam1.2"));
	
		// try to delete it again, should succeed
		status = TMAPI.deleteTeamProfile(teamProfileOfTeam1_1.getCourseID(),
				teamProfileOfTeam1_1.getTeamName());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
	
		// ----------deleting TeamFormingLog entities-------------------------
	
		// delete TeamFormingLog of one course and verify it is deleted
		TeamFormingLog tfsLogMessage1ForTfsInCourse1 = dataBundle.teamFormingLogs
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
	
		// ----------deleting Evaluation entities-------------------------
	
		// check the existence of a submission that will be deleted along with the evaluation
		Submission subInDeletedEvaluation = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(subInDeletedEvaluation);
		
		//delete the evaluation and verify it is deleted
		Evaluation evaluation1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
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
		Evaluation evaluation2InCourse1 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation2InCourse1);
	
		// ----------deleting Course entities-------------------------
		
		Course course2 = dataBundle.courses.get("course1OfCoord2");
		verifyPresentInDatastore(course2);
		status = TMAPI.deleteCourse(course2.getID());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(course2);
		
		//check if related student entities are also deleted
		Student student2InCourse2 = dataBundle.students.get("student2InCourse2");
		verifyAbsentInDatastore(student2InCourse2);
		
		//check if related evaluation entities are also deleted
		Evaluation evaluation1InCourse2 = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
		verifyAbsentInDatastore(evaluation1InCourse2);
		
		//check if related team profile entities are also deleted
		TeamProfile teamProfileOfTeam2_1 = dataBundle.teamProfiles
				.get("profileOfTeam2.1");
		verifyAbsentInDatastore(teamProfileOfTeam2_1);
		
		//check if related Tfs entities are also deleted
		TeamFormingSession tfsInCourse2 = dataBundle.teamFormingSessions
				.get("tfsInCourse2");
		verifyAbsentInDatastore(tfsInCourse2);
		
		//check if related TeamFormingLog entities are also deleted
		TeamFormingLog tfsLogMessage1ForTfsInCourse2 = dataBundle.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse2");
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse2);
	}
	
	@Test
	public void testDeleteCoords(){
		//already tested by testPersistenceAndDeletion
	}
	
	//-------------------[Testing system-level methods]------------------------

	@Test 
	public void testCreateCoord(){
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		String coordId = "tmapitt.tcc.coord";
		Coordinator coord = new Coordinator(coordId, coordId, "tmapitt.tcc.coord@gmail.com");
		TMAPI.deleteCoord(coordId);
		verifyAbsentInDatastore(coord);
		TMAPI.createCoord(coord);
		verifyPresentInDatastore(coord);
		TMAPI.deleteCoord(coordId);
		verifyAbsentInDatastore(coord);
	}
	
	@Test
	public void testGetCoordAsJason(){
		//already tested by testPersistenceAndDeletion
	}
	
	@Test
	public void testDeleteCoord(){
		//already tested by testPersistenceAndDeletion
	}
	
	@Test
	public void testEditCoord(){
		//method not implemented
	}

	@Test 
	public void testCleanByCoordinator() throws Exception{
		//only minimal testing because this is a wrapper method for
		//other well-tested methods.
		printTestCaseHeader();
		refreshDataInDatastore();
		Coordinator coord = dataBundle.coords.get("typicalCoord1");
		String[] coursesByCoord = TMAPI.getCoursesByCoordId(coord.getGoogleID());
		assertEquals(2,coursesByCoord.length);
		TMAPI.cleanupByCoordinator(coord.getGoogleID());
		coursesByCoord = TMAPI.getCoursesByCoordId(coord.getGoogleID());
		assertEquals(0,coursesByCoord.length);
	}

	@Test
	public void testGetCoursesByCoordId() throws InvalidParametersException{

		String[] courses = TMAPI.getCoursesByCoordId("nonExistentCoord");
		printTestCaseHeader();
		// testing for non-existent coordinator
		assertEquals("[]", Arrays.toString(courses));


		//create a fresh coordinator
		String coord1Id = "AST.TGCBCI.coord1";
		TMAPI.deleteCoord(coord1Id);
		TMAPI.createCoord(new Coordinator(coord1Id, "dummy name", "dummy@email"));
		
		String course1OfCoord1 = "AST.TGCBCI.c1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.c2OfCoord1";
		TMAPI.createCourse(new Course(course1OfCoord1,
				"tmapit tgcbci c1OfCoord1",
				coord1Id));
		TMAPI.createCourse(new Course(course2OfCoord1,
				"tmapit tgcbci c2OfCoord1",
				coord1Id));

		// add a course that belongs to a different coordinator
		String coord2Id = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.c1OfCoord2";
		TMAPI.createCourse(new Course(course1OfCoord2,
				"tmapit tgcbci c1OfCoord2",
				coord2Id));

		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course1OfCoord1 + ", " + course2OfCoord1 + "]",
				Arrays.toString(courses));

		TMAPI.deleteCoord(coord1Id);
		TMAPI.deleteCoord(coord2Id);
	}
	
	// -------------------------[Course-level methods]-------------------------
	
	@Test 
	public void testCreateCourse() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		String courseId = "tmapitt.tcc.course";
		Course course = new Course(courseId, "Name of tmapitt.tcc.coord", "tmapitt.tcc.coord");
		TMAPI.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
		TMAPI.createCourse(course);
		verifyPresentInDatastore(course);
		TMAPI.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
	}

	@Test
	public void testGetCourseAsJason() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditCourse() {
		//not implemented
	}
	
	@Test
	public void testDeleteCourse() {
		// already tested by testPersistenceAndDeletion
	}



	// ------------------------[Student-level methods]-------------------------
	
	@Test 
	public void testCreateStudent() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		Student student = new Student("|name of tcs student|tcsStudent@gmail.com|", "tmapit.tcs.course");
		TMAPI.deleteStudent(student.getCourseID(), student.getEmail());
		verifyAbsentInDatastore(student);
		TMAPI.createStudent(student);
		verifyPresentInDatastore(student);
		TMAPI.deleteStudent(student.getCourseID(), student.getEmail());
		verifyAbsentInDatastore(student);
	}
	
	@Test
	public void testGetStudentAsJason() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditStudent(){
		printTestCaseHeader();
		
		//check for successful edit
		refreshDataInDatastore();
		Student student = dataBundle.students.get("student1InCourse1");
		String originalEmail = student.getEmail();
		student.setName("New name");
		student.setEmail("new@gmail.com");
		student.setComments("new comments");
		student.setProfileDetail(new Text("new profile"));
		student.setTeamName("new team");
		String status = TMAPI.editStudent(originalEmail,student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(student);
		
		//test for unsuccessful edit
		student.setCourseID("non-existent");
		status = TMAPI.editStudent(originalEmail,student);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(student);
	}
	
	@Test
	public void testDeletetStudent() {
		// already tested by testPersistenceAndDeletion
	}
	
	// ------------------------[Evaluation-level methods]-----------------
	
	@Test 
	public void testCreateEvaluation() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		Evaluation evaluation = new teammates.jdo.Evaluation("tmapit.tce.course","Eval for tmapit.tce.course", "inst.", true, Common.getDateOffsetToCurrentTime(1), Common.getDateOffsetToCurrentTime(2), 8.0, 5);
		TMAPI.deleteEvaluation(evaluation.getCourseID(), evaluation.getName());
		verifyAbsentInDatastore(evaluation);
		TMAPI.createEvaluation(evaluation);
		verifyPresentInDatastore(evaluation);
		TMAPI.deleteEvaluation(evaluation.getCourseID(), evaluation.getName());
		verifyAbsentInDatastore(evaluation);
	}
	
	@Test
	public void testGetEvaluationAsJason() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditEvaluation(){
		printTestCaseHeader();
		refreshDataInDatastore();
		
		//check for successful edit
		Evaluation evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		evaluation.setGracePeriod(evaluation.getGracePeriod()+1);
		evaluation.setActivated(!evaluation.isActivated());
		evaluation.setCommentsEnabled(!evaluation.isCommentsEnabled());
		evaluation.setStart(Common.getDateOffsetToCurrentTime(1));
		evaluation.setDeadline(Common.getDateOffsetToCurrentTime(2));
		evaluation.setInstructions(evaluation.getInstructions()+"x");
		evaluation.setPublished(!evaluation.isPublished());
		evaluation.setTimeZone(evaluation.getTimeZone()+0.5);
		String status = TMAPI.editEvaluation(evaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(evaluation);
		
		//test for unsuccessful edit
		evaluation.setName("non existent");
		status = TMAPI.editEvaluation(evaluation);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(evaluation);
	}
	
	@Test
	public void testDeleteEvaluation() {
		// already tested by testPersistenceAndDeletion
	}
	
	@Test
	public void testOpenEvaluation() {
		//TODO:
	}
	
	// ------------------------[Submission-level methods]-----------------
	
	@Test
	public void testCreateSubmission() {
		// not implemented
	}
	
	@Test
	public void testGetSubmission() {
		// already tested by testPersistenceAndDeletion
	}
	
	@Test
	public void testEditSubmission(){
		printTestCaseHeader();
		refreshDataInDatastore();
		
		//check for successful edit
		Submission submission = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
		submission.setJustification(new Text(submission.getJustification().getValue()+"x"));
		submission.setPoints(submission.getPoints()+10);
		String status = TMAPI.editSubmission(submission);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(submission);
		
		//test for unsuccessful edit
		submission.setFromStudent("non-existent@gmail.com");
		status = TMAPI.editSubmission(submission);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(submission);
	}
	
	@Test
	public void testdeleteSubmission() {
		// not implemented
	}
	
	// --------------------------------[Tfs-level methods]----------
	
	@Test
	public void testCreateTfs() {
		// not implemented
	}
	
	@Test
	public void testGetSubmissionAsJason() {
		// already tested by testPersistenceAndDeletion
	}
	
	@Test 
	public void testEditTeamFormingSession(){
		printTestCaseHeader();
		refreshDataInDatastore();
		
		//check for successful edit
		TeamFormingSession tfs = dataBundle.teamFormingSessions.get("tfsInCourse1");
		tfs.setGracePeriod(tfs.getGracePeriod()+1);
		tfs.setStart(Common.getDateOffsetToCurrentTime(1));
		tfs.setDeadline(Common.getDateOffsetToCurrentTime(2));
		tfs.setInstructions(tfs.getInstructions()+"x");
		tfs.setProfileTemplate(tfs.getProfileTemplate()+"x");
		tfs.setTimeZone(tfs.getTimeZone()+1.0);
		tfs.setActivated(!tfs.isActivated());
		String status = TMAPI.editTfs(tfs);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(tfs);
		
		//test for unsuccessful edit
		tfs.setCourseID("non-existent");
		status = TMAPI.editTfs(tfs);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(tfs);
	}
	
	@Test
	public void testDeleteTfs() {
		// not implemented
	}
	
	// --------------------------------[TeamProfile-level methods]----------
	
	@Test
	public void testCreateTeamProfile() {
		// not implemented
	}
	
	@Test
	public void testGetTeamProfileAsJason() {
		// already tested by testPersistenceAndDeletion
	}
	
	@Test 
	public void testEditTeamProfile(){
		printTestCaseHeader();
		refreshDataInDatastore();
		
		//check for successful edit
		TeamProfile teamProfile = dataBundle.teamProfiles.get("profileOfTeam1.1");
		String originalTeamName = teamProfile.getTeamName();
		teamProfile.setTeamName(teamProfile.getTeamName()+"x");
		teamProfile.setTeamProfile(new Text(teamProfile.getTeamProfile().getValue()+"x"));
		String status = TMAPI.editTeamProfile(originalTeamName,teamProfile);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(teamProfile);
		
		//test for unsuccessful edit
		status = TMAPI.editTeamProfile("non-existent",teamProfile);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));

	}
	
	@Test
	public void testDeleteTeamProfileAsJason() {
		// already tested by testPersistenceAndDeletion
	}
	
	// --------------------------------[Testing helper methods]--------------
	
	@Test
	public void testDataBundle() {
		printTestCaseHeader();
		String jsonString = Common.getFileContents(Common.TEST_DATA_FOLDER + "typicalDataBundle.json");
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
	
		Course course1 = data.courses.get("course1OfCoord1");
		assertEquals("idOfCourse1OfCoord1", course1.getID());
		assertEquals("course1OfCoord1 name", course1.getName());
		assertEquals("idOfTypicalCoord1", course1.getCoordinatorID());
	
		Student student1InCourse1 = data.students.get("student1InCourse1");
		assertEquals("student1InCourse1", student1InCourse1.getID());
		assertEquals("student1 In Course1", student1InCourse1.getName());
		assertEquals("Team 1.1", student1InCourse1.getTeamName());
		assertEquals("comment for student1InCourse1",
				student1InCourse1.getComments());
		assertEquals("profile summary for student1InCourse1",
				student1InCourse1.getProfileSummary());
		assertEquals("idOfCourse1OfCoord1", student1InCourse1.getCourseID());
		assertEquals("profiledetail for student1InCourse1", student1InCourse1
				.getProfileDetail().getValue());
	
		Student student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse2", student2InCourse2.getID());
		assertEquals("student2 In Course2", student2InCourse2.getName());
		assertEquals("Team 2.1", student2InCourse2.getTeamName());
	
		Evaluation evaluation1 = data.evaluations.get("evaluation1InCourse1OfCoord1");
		assertEquals("evaluation1 In Course1", evaluation1.getName());
		assertEquals("idOfCourse1OfCoord1", evaluation1.getCourseID());
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
	
		Evaluation evaluation2 = data.evaluations.get("evaluation2InCourse1OfCoord1");
		assertEquals("evaluation2 In Course1", evaluation2.getName());
		assertEquals("idOfCourse1OfCoord1", evaluation2.getCourseID());
	
		Submission submissionFromS1C1ToS2C1 = data.submissions
				.get("submissionFromS1C1ToS2C1");
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.getFromStudent());
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.getToStudent());
		assertEquals("idOfCourse1OfCoord1", submissionFromS1C1ToS2C1.getCourseID());
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
		assertEquals("idOfCourse1OfCoord1", tfsInCourse1.getCourseID());
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
		assertEquals("idOfCourse1OfCoord1", profileOfTeam1_1.getCourseID());
		assertEquals("course1OfCoord1 name", profileOfTeam1_1.getCourseName());
		assertEquals("Team 1.1", profileOfTeam1_1.getTeamName());
		assertEquals("team profile of Team 1.1", profileOfTeam1_1
				.getTeamProfile().getValue());
	
		TeamFormingLog tfsLogMessageForTfsInCourse1 = data.teamFormingLogs
				.get("tfsLogMessage1ForTfsInCourse1");
		assertEquals("idOfCourse1OfCoord1", tfsLogMessageForTfsInCourse1.getCourseID());
		assertEquals("student1 In Course1",
				tfsLogMessageForTfsInCourse1.getStudentName());
		assertEquals("student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.getStudentEmail());
		assertEquals("Sun Jan 01 01:01:00 SGT 2012",
				tfsLogMessageForTfsInCourse1.getTime().toString());
		assertEquals("log message 1 of course1, student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.getMessage().getValue());
	}

	//============================helper methods==============================

	private void refreshDataInDatastore() {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		TMAPI.deleteCoordinators(jsonString); 
		TMAPI.persistNewDataBundle(jsonString);
	}

	private void verifyAbsentInDatastore(Course course) {
		assertEquals("null",TMAPI.getCourseAsJason(course.getID()));
	}

	private void verifyAbsentInDatastore(Student student) {
		assertEquals("null",TMAPI.getStudentAsJason(student.getCourseID(), student.getEmail()));
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

	private void verifyPresentInDatastore(String dataBundleJsonString) {
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
		String studentJsonString = TMAPI.getStudentAsJason(
				expectedStudent.getCourseID(), expectedStudent.getEmail());
		Student actualStudent = gson.fromJson(studentJsonString,
				Student.class);
		assertEquals(gson.toJson(expectedStudent),
				gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(Course expectedCourse) {
		String courseJsonString = TMAPI.getCourseAsJason(expectedCourse
				.getID());
		Course actualCourse = gson.fromJson(courseJsonString, Course.class);
		assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
	}

	private void verifyPresentInDatastore(TeamFormingLog expectedTeamFormingLogEntry) {
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
