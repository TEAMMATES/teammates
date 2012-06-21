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

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentActionData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamProfileData;
import teammates.datatransfer.TfsData;
import teammates.testing.lib.BackDoor;
import teammates.testing.testcases.BaseTestCase;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BackDoorTest extends BaseTestCase{

	private static Gson gson = Common.getTeammatesGson();
	private static String jsonString;
	private DataBundle dataBundle;

	@BeforeClass
	public static void setUp() throws Exception{
		 jsonString = Common.readFile(Common.TEST_DATA_FOLDER + "/typicalDataBundle.json");
	}

	@AfterClass
	public static void tearDown() {
		//TODO: clean up data
	}
	
	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods_________________________________() {
	}
	

	@Test
	public void testPersistenceAndDeletion() {
		printTestCaseHeader();
		
		//to avoid clashes with existing data
		BackDoor.deleteCoordinators(jsonString);
		
		//check if deleteCoordinators worked
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		for(CoordData coord: dataBundle.coords.values()){
			verifyAbsentInDatastore(coord);
		}
		
		//check persisting
		String status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(jsonString);
		
		// ----------deleting Coordinator entities-------------------------
		CoordData typicalCoord1 = dataBundle.coords.get("typicalCoord1");
		verifyPresentInDatastore(typicalCoord1);
		status = BackDoor.deleteCoord(typicalCoord1.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord1);
		
		CoordData typicalCoord2 = dataBundle.coords.get("typicalCoord2");
		status = BackDoor.deleteCoord(typicalCoord2.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(typicalCoord2);
		
		//try to delete again. should succeed.
		status = BackDoor.deleteCoord(typicalCoord2.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		status = BackDoor.deleteCoord("idOfTypicalCoord3");
		
		//recreate data. this should succeed if all previous data were deleted
		status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		// ----------deleting TeamProfile entities-------------------------
	
		// delete one TeamProfile and confirm it is already deleted
		TeamProfileData teamProfileOfTeam1_1 = dataBundle.teamProfiles
				.get("profileOfTeam1.1");
		verifyPresentInDatastore(teamProfileOfTeam1_1);
		status = BackDoor.deleteTeamProfile(teamProfileOfTeam1_1.course,
				teamProfileOfTeam1_1.team);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(teamProfileOfTeam1_1);
	
		// verify if the other TeamProfileData in the same course is intact
		verifyPresentInDatastore(dataBundle.teamProfiles
				.get("profileOfTeam1.2"));
	
		// try to delete it again, should succeed
		status = BackDoor.deleteTeamProfile(teamProfileOfTeam1_1.course,
				teamProfileOfTeam1_1.team);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
	
		// ----------deleting TeamFormingLog entities-------------------------
	
		// delete TeamFormingLog of one course and verify it is deleted
		StudentActionData tfsLogMessage1ForTfsInCourse1 = dataBundle.studentActions
				.get("tfsLogMessage1ForTfsInCourse1");
		verifyPresentInDatastore(tfsLogMessage1ForTfsInCourse1);
		status = BackDoor.deleteTeamFormingLog(tfsLogMessage1ForTfsInCourse1
				.course);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse1);
	
		// try to delete it again, the operation should succeed
		status = BackDoor.deleteTeamFormingLog(tfsLogMessage1ForTfsInCourse1
				.course);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
	
		// ----------deleting Evaluation entities-------------------------
	
		// check the existence of a submission that will be deleted along with the evaluation
		SubmissionData subInDeletedEvaluation = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(subInDeletedEvaluation);
		
		//delete the evaluation and verify it is deleted
		EvaluationData evaluation1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation1InCourse1);
		status = BackDoor.deleteEvaluation(evaluation1InCourse1.course,
				evaluation1InCourse1.name);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(evaluation1InCourse1);
		
		//verify that the submission is deleted too
		verifyAbsentInDatastore(subInDeletedEvaluation);
	
		// try to delete the evaluation again, should succeed
		status = BackDoor.deleteEvaluation(evaluation1InCourse1.course,
				evaluation1InCourse1.name);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		
		//verify that the other evaluation in the same course is intact
		EvaluationData evaluation2InCourse1 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation2InCourse1);
	
		// ----------deleting Course entities-------------------------
		
		CourseData course2 = dataBundle.courses.get("course1OfCoord2");
		verifyPresentInDatastore(course2);
		status = BackDoor.deleteCourse(course2.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(course2);
		
		//check if related student entities are also deleted
		StudentData student2InCourse2 = dataBundle.students.get("student2InCourse2");
		verifyAbsentInDatastore(student2InCourse2);
		
		//check if related evaluation entities are also deleted
		EvaluationData evaluation1InCourse2 = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
		verifyAbsentInDatastore(evaluation1InCourse2);
		
		//check if related team profile entities are also deleted
		TeamProfileData teamProfileOfTeam2_1 = dataBundle.teamProfiles
				.get("profileOfTeam2.1");
		verifyAbsentInDatastore(teamProfileOfTeam2_1);
		
		//check if related Tfs entities are also deleted
		TfsData tfsInCourse2 = dataBundle.teamFormingSessions
				.get("tfsInCourse2");
		verifyAbsentInDatastore(tfsInCourse2);
		
		//check if related TeamFormingLog entities are also deleted
		StudentActionData tfsLogMessage1ForTfsInCourse2 = dataBundle.studentActions
				.get("tfsLogMessage1ForTfsInCourse2");
		verifyTeamFormingLogEmptyInDatastore(tfsLogMessage1ForTfsInCourse2);
	}
	
	@SuppressWarnings("unused")
	private void ____COORD_level_methods_________________________________() {
	}
	
	@Test
	public void testDeleteCoords(){
		//already tested by testPersistenceAndDeletion
	}
	

	@Test 
	public void testCreateCoord(){
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		String coordId = "tmapitt.tcc.coord";
		CoordData coord = new CoordData(coordId, coordId, "tmapitt.tcc.coord@gmail.com");
		BackDoor.deleteCoord(coordId);
		verifyAbsentInDatastore(coord);
		BackDoor.createCoord(coord);
		verifyPresentInDatastore(coord);
		BackDoor.deleteCoord(coordId);
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
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		String[] coursesByCoord = BackDoor.getCoursesByCoordId(coord.id);
		assertEquals(2,coursesByCoord.length);
		BackDoor.cleanupByCoordinator(coord.id);
		coursesByCoord = BackDoor.getCoursesByCoordId(coord.id);
		assertEquals(0,coursesByCoord.length);
	}

	@Test
	public void testGetCoursesByCoordId() throws InvalidParametersException{

		String[] courses = BackDoor.getCoursesByCoordId("nonExistentCoord");
		printTestCaseHeader();
		// testing for non-existent coordinator
		assertEquals("[]", Arrays.toString(courses));


		//create a fresh coordinator
		String coord1Id = "AST.TGCBCI.coord1";
		BackDoor.deleteCoord(coord1Id);
		BackDoor.createCoord(new CoordData(coord1Id, "dummy name", "dummy@email"));
		
		String course1OfCoord1 = "AST.TGCBCI.c1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.c2OfCoord1";
		BackDoor.createCourse(new CourseData(course1OfCoord1,
				"tmapit tgcbci c1OfCoord1",
				coord1Id));
		BackDoor.createCourse(new CourseData(course2OfCoord1,
				"tmapit tgcbci c2OfCoord1",
				coord1Id));

		// add a course that belongs to a different coordinator
		String coord2Id = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.c1OfCoord2";
		BackDoor.createCourse(new CourseData(course1OfCoord2,
				"tmapit tgcbci c1OfCoord2",
				coord2Id));

		courses = BackDoor.getCoursesByCoordId(coord1Id);
		assertEquals("[" + course1OfCoord1 + ", " + course2OfCoord1 + "]",
				Arrays.toString(courses));

		BackDoor.deleteCoord(coord1Id);
		BackDoor.deleteCoord(coord2Id);
	}
	
	@SuppressWarnings("unused")
	private void ____COURSE_level_methods_________________________________() {
	}
	
	@Test 
	public void testCreateCourse() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		String courseId = "tmapitt.tcc.course";
		CourseData course = new CourseData(courseId, "Name of tmapitt.tcc.coord", "tmapitt.tcc.coord");
		BackDoor.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
		BackDoor.createCourse(course);
		verifyPresentInDatastore(course);
		BackDoor.deleteCourse(courseId);
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



	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods_________________________________() {
	}
	
	@Test 
	public void testCreateStudent() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		StudentData student = new StudentData("|name of tcs student|tcsStudent@gmail.com|", "tmapit.tcs.course");
		BackDoor.deleteStudent(student.course, student.email);
		verifyAbsentInDatastore(student);
		BackDoor.createStudent(student);
		verifyPresentInDatastore(student);
		BackDoor.deleteStudent(student.course, student.email);
		verifyAbsentInDatastore(student);
	}
	
	@Test
	public void testGetRegistrationKey() throws InvalidParametersException{
		printTestCaseHeader();
		StudentData student = new StudentData("t1|name of tgsr student|tgsr@gmail.com|", "course1");
		BackDoor.createStudent(student);
		assertEquals(14, BackDoor.getRegistrationKey(student.course, student.email).length());
		
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
		StudentData student = dataBundle.students.get("student1InCourse1");
		String originalEmail = student.email;
		student.name = "New name";
		student.email = "new@gmail.com";
		student.comments = "new comments";
		student.profile = new Text("new profile");
		student.team = "new team";
		String status = BackDoor.editStudent(originalEmail,student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(student);
		
		//test for unsuccessful edit
		student.course ="non-existent";
		status = BackDoor.editStudent(originalEmail,student);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(student);
	}
	
	@Test
	public void testDeleteStudent() {
		// already tested by testPersistenceAndDeletion
	}
	
	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods______________________________() {
	}
	
	@Test 
	public void testCreateEvaluation() throws InvalidParametersException{
		//only minimal testing because this is a wrapper method for
		//another well-tested method.
		printTestCaseHeader();
		EvaluationData e = new EvaluationData();
		e.course = "tmapit.tce.course";
		e.name = "Eval for tmapit.tce.course";
		e.instructions = "inst.";
		e.p2pEnabled = true;
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.timeZone = 8.0;
		e.gracePeriod = 5;
		BackDoor.deleteEvaluation(e.course, e.name);
		verifyAbsentInDatastore(e);
		BackDoor.createEvaluation(e);
		verifyPresentInDatastore(e);
		BackDoor.deleteEvaluation(e.course, e.name);
		verifyAbsentInDatastore(e);
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
		EvaluationData e = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		
		e.gracePeriod = e.gracePeriod + 1;
		e.instructions = e.instructions + "x";
		e.p2pEnabled = (!e.p2pEnabled);
		e.startTime = Common.getDateOffsetToCurrentTime(-2);
		e.endTime = Common.getDateOffsetToCurrentTime(-1);
		e.activated = (!e.activated);
		e.published = (!e.published);
		e.timeZone = e.timeZone+1.0;
		
		String status = BackDoor.editEvaluation(e);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(e);
		
		//test for unsuccessful edit
		e.name = "non existent";
		status = BackDoor.editEvaluation(e);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(e);
	}
	
	@Test
	public void testDeleteEvaluation() {
		// already tested by testPersistenceAndDeletion
	}
	
	@Test
	public void testOpenEvaluation() {
		//TODO:
	}
	
	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods______________________________() {
	}
	
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
		SubmissionData submission = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
		submission.justification = new Text(submission.justification.getValue()+"x");
		submission.points = submission.points+10;
		String status = BackDoor.editSubmission(submission);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(submission);
		
		//test for unsuccessful edit
		submission.reviewer = "non-existent@gmail.com";
		status = BackDoor.editSubmission(submission);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(submission);
	}
	
	@Test
	public void testdeleteSubmission() {
		// not implemented
	}
	
	@SuppressWarnings("unused")
	private void ____TFS_level_methods_________________________________() {
	}
	
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
		TfsData tfs = dataBundle.teamFormingSessions.get("tfsInCourse1");
		tfs.gracePeriod = tfs.gracePeriod+1;
		tfs.startTime = Common.getDateOffsetToCurrentTime(1);
		tfs.endTime = Common.getDateOffsetToCurrentTime(2);
		tfs.instructions = tfs.instructions+"x";
		tfs.profileTemplate = tfs.profileTemplate+"x";
		tfs.timeZone = tfs.timeZone+1.0;
		tfs.activated = (!tfs.activated);
		String status = BackDoor.editTfs(tfs);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(tfs);
		
		//test for unsuccessful edit
		tfs.course = "non-existent";
		status = BackDoor.editTfs(tfs);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));
		verifyAbsentInDatastore(tfs);
	}
	
	@Test
	public void testDeleteTfs() {
		// not implemented
	}
	
	@SuppressWarnings("unused")
	private void ____TEAM_PROFILE_level_methods____________________________() {
	}
	
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
		TeamProfileData teamProfile = dataBundle.teamProfiles.get("profileOfTeam1.1");
		String originalTeamName = teamProfile.team;
		teamProfile.team = teamProfile.team+"x";
		teamProfile.profile = new Text(teamProfile.profile.getValue()+"x");
		String status = BackDoor.editTeamProfile(originalTeamName,teamProfile);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(teamProfile);
		
		//test for unsuccessful edit
		status = BackDoor.editTeamProfile("non-existent",teamProfile);
		assertTrue(status.startsWith(Common.BACKEND_STATUS_FAILURE));

	}
	
	@Test
	public void testDeleteTeamProfileAsJason() {
		// already tested by testPersistenceAndDeletion
	}
	
	@SuppressWarnings("unused")
	private void ____helper_methods_________________________________() {
	}
	
	@Test
	public void testDataBundle() throws Exception{
		printTestCaseHeader();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER + "/typicalDataBundle.json");
		Gson gson = Common.getTeammatesGson();
	
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
	
		CoordData typicalCoord1 = data.coords.get("typicalCoord1");
		assertEquals("idOfTypicalCoord1", typicalCoord1.id);
		assertEquals("Typical Coordinator1", typicalCoord1.name);
		assertEquals("typicalCoord1@gmail.com", typicalCoord1.email);
	
		CoordData typicalCoord2 = data.coords.get("typicalCoord2");
		assertEquals("idOfTypicalCoord2", typicalCoord2.id);
		assertEquals("Typical Coordinator2", typicalCoord2.name);
		assertEquals("typicalCoord2@gmail.com", typicalCoord2.email);
	
		CourseData course1 = data.courses.get("course1OfCoord1");
		assertEquals("idOfCourse1OfCoord1", course1.id);
		assertEquals("course1OfCoord1 name", course1.name);
		assertEquals("idOfTypicalCoord1", course1.coord);
	
		StudentData student1InCourse1 = data.students.get("student1InCourse1");
		assertEquals("student1InCourse1", student1InCourse1.id);
		assertEquals("student1 In Course1", student1InCourse1.name);
		assertEquals("Team 1.1", student1InCourse1.team);
		assertEquals("comment for student1InCourse1",
				student1InCourse1.comments);
		assertEquals("idOfCourse1OfCoord1", student1InCourse1.course);
		assertEquals("profiledetail for student1InCourse1", student1InCourse1
				.profile.getValue());
	
		StudentData student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse1", student2InCourse2.id);
		assertEquals("student2 In Course2", student2InCourse2.name);
		assertEquals("Team 2.1", student2InCourse2.team);
	
		EvaluationData evaluation1 = data.evaluations.get("evaluation1InCourse1OfCoord1");
		assertEquals("evaluation1 In Course1", evaluation1.name);
		assertEquals("idOfCourse1OfCoord1", evaluation1.course);
		assertEquals("instructions for evaluation1InCourse1",
				evaluation1.instructions);
		assertEquals(10, evaluation1.gracePeriod);
		assertEquals(true, evaluation1.p2pEnabled);
		assertEquals("Sun Apr 01 23:59:00 SGT 2012", evaluation1.startTime
				.toString());
		assertEquals("Tue Apr 30 23:59:00 SGT 2013", evaluation1.endTime
				.toString());
		assertEquals(true, evaluation1.activated);
		assertEquals(false, evaluation1.published);
		assertEquals(2.0, evaluation1.timeZone, 0.01);
	
		EvaluationData evaluation2 = data.evaluations.get("evaluation2InCourse1OfCoord1");
		assertEquals("evaluation2 In Course1", evaluation2.name);
		assertEquals("idOfCourse1OfCoord1", evaluation2.course);
	
		SubmissionData submissionFromS1C1ToS2C1 = data.submissions
				.get("submissionFromS1C1ToS2C1");
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.reviewer);
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.reviewee);
		assertEquals("idOfCourse1OfCoord1", submissionFromS1C1ToS2C1.course);
		assertEquals("evaluation1 In Course1",
				submissionFromS1C1ToS2C1.evaluation);
		assertEquals(10, submissionFromS1C1ToS2C1.points);
		assertEquals("Team 1.1", submissionFromS1C1ToS2C1.team);
		// since justification filed is of Text type, we have to use it's
		// .getValue() method to access the string contained inside it
		assertEquals(
				"justification of student1InCourse1 rating to student2InCourse1",
				submissionFromS1C1ToS2C1.justification.getValue());
		assertEquals("comments from student1InCourse1 to student2InCourse1",
				submissionFromS1C1ToS2C1.p2pFeedback.getValue());
	
		SubmissionData submissionFromS2C1ToS1C1 = data.submissions
				.get("submissionFromS2C1ToS1C1");
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS2C1ToS1C1.reviewer);
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS2C1ToS1C1.reviewee);
	
		TfsData tfsInCourse1 = data.teamFormingSessions
				.get("tfsInCourse1");
		assertEquals("idOfCourse1OfCoord1", tfsInCourse1.course);
		assertEquals(8.0, tfsInCourse1.timeZone, 0.01);
		assertEquals("Sun Apr 01 23:59:00 SGT 2012", tfsInCourse1.startTime
				.toString());
		assertEquals("Sun Apr 15 23:59:00 SGT 2012", tfsInCourse1.endTime
				.toString());
		assertEquals("instructions for tfsInCourse1",
				tfsInCourse1.instructions);
		assertEquals("profile template for tfsInCourse1",
				tfsInCourse1.profileTemplate);
		assertEquals(10, tfsInCourse1.gracePeriod);
		assertEquals(false, tfsInCourse1.activated);
	
		TeamProfileData profileOfTeam1_1 = data.teamProfiles
				.get("profileOfTeam1.1");
		assertEquals("idOfCourse1OfCoord1", profileOfTeam1_1.course);
		assertEquals("Team 1.1", profileOfTeam1_1.team);
		assertEquals("team profile of Team 1.1", profileOfTeam1_1
				.profile.getValue());
	
		StudentActionData tfsLogMessageForTfsInCourse1 = data.studentActions
				.get("tfsLogMessage1ForTfsInCourse1");
		assertEquals("idOfCourse1OfCoord1", tfsLogMessageForTfsInCourse1.course);
		assertEquals("student1 In Course1",
				tfsLogMessageForTfsInCourse1.name);
		assertEquals("student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.email);
		assertEquals("Sun Jan 01 01:01:00 SGT 2012",
				tfsLogMessageForTfsInCourse1.time.toString());
		assertEquals("log message 1 of course1, student1InCourse1@gmail.com",
				tfsLogMessageForTfsInCourse1.action.getValue());
	}

	//============================helper methods==============================

	private void refreshDataInDatastore() {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString); 
		BackDoor.persistNewDataBundle(jsonString);
	}

	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals("null",BackDoor.getCourseAsJason(course.id));
	}

	private void verifyAbsentInDatastore(StudentData student) {
		assertEquals("null",BackDoor.getStudentAsJason(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationData evaluation1InCourse1) {
		assertEquals("null", BackDoor.getEvaluationAsJason(
				evaluation1InCourse1.course,
				evaluation1InCourse1.name));
	}

	private void verifyAbsentInDatastore(SubmissionData subInDeletedEvaluation) {
		String submissionAsJason = BackDoor.getSubmissionAsJason(
				subInDeletedEvaluation.course,
				subInDeletedEvaluation.evaluation,
				subInDeletedEvaluation.reviewer,
				subInDeletedEvaluation.reviewee);
		assertEquals("null", submissionAsJason);
	}

	private void verifyAbsentInDatastore(TfsData tfsInCourse3) {
		assertEquals("null", BackDoor.getTfsAsJason(tfsInCourse3.course));
	}

	private void verifyTeamFormingLogEmptyInDatastore(
			StudentActionData tfsLogMessage1ForTfsInCourse1) {
		assertEquals("[]",
				BackDoor.getTeamFormingLogAsJason(tfsLogMessage1ForTfsInCourse1
						.course));
	}

	private void verifyAbsentInDatastore(
			TeamProfileData teamProfileOfTeam1_1) {
		assertEquals("null", BackDoor.getTeamProfileAsJason(
				teamProfileOfTeam1_1.course,
				teamProfileOfTeam1_1.team));
	}

	private void verifyPresentInDatastore(String dataBundleJsonString) {
		Gson gson = Common.getTeammatesGson();

		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, CoordData> coords = data.coords;
		for (CoordData expectedCoord : coords.values()) {
			verifyPresentInDatastore(expectedCoord);
		}

		HashMap<String, CourseData> courses = data.courses;
		for (CourseData expectedCourse : courses.values()) {
			verifyPresentInDatastore(expectedCourse);
		}

		HashMap<String, StudentData> students = data.students;
		for (StudentData expectedStudent : students.values()) {
			verifyPresentInDatastore(expectedStudent);
		}

		HashMap<String, EvaluationData> evaluations = data.evaluations;
		for (EvaluationData expectedEvaluation : evaluations.values()) {
			verifyPresentInDatastore(expectedEvaluation);
		}

		HashMap<String, SubmissionData> submissions = data.submissions;
		for (SubmissionData expectedSubmission : submissions.values()) {
			verifyPresentInDatastore(expectedSubmission);
		}

		HashMap<String, TfsData> teamFormingSessions = data.teamFormingSessions;
		for (TfsData expectedTeamFormingSession : teamFormingSessions
				.values()) {
			verifyPresentInDatastore(expectedTeamFormingSession);
		}

		HashMap<String, TeamProfileData> teamProfiles = data.teamProfiles;
		for (TeamProfileData expectedTeamProfile : teamProfiles.values()) {
			verifyPresentInDatastore(expectedTeamProfile);
		}

		HashMap<String, StudentActionData> teamFormingLogs = data.studentActions;
		for (StudentActionData expectedTeamFormingLogEntry : teamFormingLogs
				.values()) {
			verifyPresentInDatastore(expectedTeamFormingLogEntry);
		}

	}

	private void verifyPresentInDatastore(TfsData expectedTeamFormingSession) {
		String teamFormingSessionsJsonString = BackDoor
				.getTfsAsJason(expectedTeamFormingSession.course);
		TfsData actualTeamFormingSession = gson.fromJson(
				teamFormingSessionsJsonString, TfsData.class);
		assertEquals(gson.toJson(expectedTeamFormingSession),
				gson.toJson(actualTeamFormingSession));
	}

	private void verifyPresentInDatastore(SubmissionData expectedSubmission) {
		String submissionsJsonString = BackDoor.getSubmissionAsJason(
				expectedSubmission.course,
				expectedSubmission.evaluation,
				expectedSubmission.reviewer,
				expectedSubmission.reviewee);
		SubmissionData actualSubmission = gson.fromJson(submissionsJsonString,
				SubmissionData.class);
		assertEquals(gson.toJson(expectedSubmission),
				gson.toJson(actualSubmission));
	}

	private void verifyPresentInDatastore(EvaluationData expectedEvaluation) {
		String evaluationJsonString = BackDoor.getEvaluationAsJason(
				expectedEvaluation.course,
				expectedEvaluation.name);
		EvaluationData actualEvaluation = gson.fromJson(evaluationJsonString,
				EvaluationData.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		assertEquals(gson.toJson(expectedEvaluation),
				gson.toJson(actualEvaluation));
	}

	private void verifyPresentInDatastore(StudentData expectedStudent) {
		String studentJsonString = BackDoor.getStudentAsJason(
				expectedStudent.course, expectedStudent.email);
		StudentData actualStudent = gson.fromJson(studentJsonString,
				StudentData.class);
		//TODO: this is for backward compatibility with old system. to be removed.
		if((expectedStudent.id==null)&&(actualStudent.id.equals(""))){
			actualStudent.id=null;
		}
		if((expectedStudent.team==null)&&(actualStudent.team.equals(""))){
			actualStudent.team=null;
		}
		if((expectedStudent.comments==null)&&(actualStudent.comments.equals(""))){
			actualStudent.comments=null;
		}
		assertEquals(gson.toJson(expectedStudent),
				gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(CourseData expectedCourse) {
		String courseJsonString = BackDoor.getCourseAsJason(expectedCourse
				.id);
		CourseData actualCourse = gson.fromJson(courseJsonString, CourseData.class);
		assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
	}

	private void verifyPresentInDatastore(StudentActionData expectedTeamFormingLogEntry) {
		String teamFormingLogJsonString = BackDoor
				.getTeamFormingLogAsJason(expectedTeamFormingLogEntry
						.course);
		Type collectionType = new TypeToken<ArrayList<StudentActionData>>() {
		}.getType();
		ArrayList<StudentActionData> actualTeamFormingLogsForCourse = gson
				.fromJson(teamFormingLogJsonString, collectionType);
		String errorMessage = gson.toJson(expectedTeamFormingLogEntry)
				+ "\n--> was not found in -->\n"
				+ BackDoor.reformatJasonString(teamFormingLogJsonString,
						collectionType);
		assertTrue(
				errorMessage,
				isLogEntryInList(expectedTeamFormingLogEntry,
						actualTeamFormingLogsForCourse));
	}

	private void verifyPresentInDatastore(TeamProfileData expectedTeamProfile) {
		String teamProfileJsonString = BackDoor.getTeamProfileAsJason(
				expectedTeamProfile.course,
				expectedTeamProfile.team);
		TeamProfileData actualTeamProfile = gson.fromJson(
				teamProfileJsonString, TeamProfileData.class);
		assertEquals(gson.toJson(expectedTeamProfile),
				gson.toJson(actualTeamProfile));
	}

	private void verifyPresentInDatastore(CoordData expectedCoord) {
		String coordJsonString = BackDoor.getCoordAsJason(expectedCoord
				.id);
		CoordData actualCoord = gson.fromJson(coordJsonString,
				CoordData.class);
		assertEquals(gson.toJson(expectedCoord), gson.toJson(actualCoord));
	}
	
	private void verifyAbsentInDatastore(CoordData expectedCoord) {
		assertEquals("null", BackDoor.getCoordAsJason(expectedCoord.id));
	}

	private boolean isLogEntryInList(StudentActionData teamFormingLogEntry,
			ArrayList<StudentActionData> teamFormingLogEntryList) {
		for (StudentActionData logEntryInList : teamFormingLogEntryList) {
			if (teamFormingLogEntry.course.equals(
					logEntryInList.course)
					&& teamFormingLogEntry.action.getValue()
							.equals(logEntryInList.action.getValue())
					&& teamFormingLogEntry.email.equals(
							logEntryInList.email)
					&& teamFormingLogEntry.name.equals(
							logEntryInList.name)
					&& teamFormingLogEntry.time.toString()
							.equals(logEntryInList.time.toString())) {
				return true;
			}
		}
		return false;
	}

}
