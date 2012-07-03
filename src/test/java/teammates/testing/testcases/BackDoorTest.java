package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.testing.lib.BackDoor;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class BackDoorTest extends BaseTestCase {

	private static Gson gson = Common.getTeammatesGson();
	private static String jsonString;
	private DataBundle dataBundle;

	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER
				+ "/typicalDataBundle.json");
	}

	@AfterClass
	public static void tearDown() {
		BackDoor.deleteCoordinators(jsonString);
		printTestClassFooter();
	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods_________________________________() {
	}

	@Test
	public void testPersistenceAndDeletion() {

		dataBundle = gson.fromJson(jsonString, DataBundle.class);

		// Clean up to avoid clashes with existing data.
		// We delete courses first in case the same course ID exists under a
		// different coord not listed in our databundle.
		// check if deleteCoordinators worked

		for (CourseData course : dataBundle.courses.values()) {
			BackDoor.deleteCourse(course.id);
		}

		BackDoor.deleteCoordinators(jsonString);

		// ensure clean up worked
		for (CoordData coord : dataBundle.coords.values()) {
			verifyAbsentInDatastore(coord);
		}
		for (CourseData course : dataBundle.courses.values()) {
			verifyAbsentInDatastore(course);
		}

		// check persisting
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

		// try to delete again. should succeed.
		status = BackDoor.deleteCoord(typicalCoord2.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		status = BackDoor.deleteCoord("idOfTypicalCoord3");

		// recreate data. this should succeed if all previous data were deleted
		status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// ----------deleting Evaluation entities-------------------------

		// check the existence of a submission that will be deleted along with
		// the evaluation
		SubmissionData subInDeletedEvaluation = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(subInDeletedEvaluation);

		// delete the evaluation and verify it is deleted
		EvaluationData evaluation1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation1InCourse1);
		status = BackDoor.deleteEvaluation(evaluation1InCourse1.course,
				evaluation1InCourse1.name);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(evaluation1InCourse1);

		// verify that the submission is deleted too
		verifyAbsentInDatastore(subInDeletedEvaluation);

		// try to delete the evaluation again, should succeed
		status = BackDoor.deleteEvaluation(evaluation1InCourse1.course,
				evaluation1InCourse1.name);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// verify that the other evaluation in the same course is intact
		EvaluationData evaluation2InCourse1 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation2InCourse1);

		// ----------deleting Course entities-------------------------

		CourseData course2 = dataBundle.courses.get("course1OfCoord2");
		verifyPresentInDatastore(course2);
		status = BackDoor.deleteCourse(course2.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(course2);

		// check if related student entities are also deleted
		StudentData student2InCourse2 = dataBundle.students
				.get("student2InCourse2");
		verifyAbsentInDatastore(student2InCourse2);

		// check if related evaluation entities are also deleted
		EvaluationData evaluation1InCourse2 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord2");
		verifyAbsentInDatastore(evaluation1InCourse2);
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods_________________________________() {
	}

	@Test
	public void testDeleteCoords() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testCreateCoord() {
		// only minimal testing because this is a wrapper method for
		// another well-tested method.

		String coordId = "tmapitt.tcc.coord";
		CoordData coord = new CoordData(coordId, coordId,
				"tmapitt.tcc.coord@gmail.com");
		BackDoor.deleteCoord(coordId);
		verifyAbsentInDatastore(coord);
		BackDoor.createCoord(coord);
		verifyPresentInDatastore(coord);
		BackDoor.deleteCoord(coordId);
		verifyAbsentInDatastore(coord);
	}

	@Test
	public void testGetCoordAsJson() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testDeleteCoord() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditCoord() {
		// method not implemented
	}

	@Test
	public void testCleanByCoordinator() throws Exception {
		// only minimal testing because this is a wrapper method for
		// other well-tested methods.

		refreshDataInDatastore();
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		String[] coursesByCoord = BackDoor.getCoursesByCoordId(coord.id);
		assertEquals(2, coursesByCoord.length);
		BackDoor.cleanupCoord(coord.id);
		coursesByCoord = BackDoor.getCoursesByCoordId(coord.id);
		assertEquals(0, coursesByCoord.length);
	}

	@Test
	public void testGetCoursesByCoordId() throws InvalidParametersException {

		String[] courses = BackDoor.getCoursesByCoordId("nonExistentCoord");

		// testing for non-existent coordinator
		assertEquals("[]", Arrays.toString(courses));

		// create a fresh coordinator
		String coord1Id = "AST.TGCBCI.coord1";
		BackDoor.deleteCoord(coord1Id);
		BackDoor.createCoord(new CoordData(coord1Id, "dummy name",
				"dummy@email"));

		String course1OfCoord1 = "AST.TGCBCI.c1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.c2OfCoord1";
		BackDoor.createCourse(new CourseData(course1OfCoord1,
				"tmapit tgcbci c1OfCoord1", coord1Id));
		BackDoor.createCourse(new CourseData(course2OfCoord1,
				"tmapit tgcbci c2OfCoord1", coord1Id));

		// add a course that belongs to a different coordinator
		String coord2Id = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.c1OfCoord2";
		BackDoor.createCourse(new CourseData(course1OfCoord2,
				"tmapit tgcbci c1OfCoord2", coord2Id));

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
	public void testCreateCourse() throws InvalidParametersException {
		// only minimal testing because this is a wrapper method for
		// another well-tested method.

		String courseId = "tmapitt.tcc.course";
		CourseData course = new CourseData(courseId,
				"Name of tmapitt.tcc.coord", "tmapitt.tcc.coord");
		BackDoor.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
		BackDoor.createCourse(course);
		verifyPresentInDatastore(course);
		BackDoor.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
	}

	@Test
	public void testGetCourseAsJson() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditCourse() {
		// not implemented
	}

	@Test
	public void testDeleteCourse() {
		// already tested by testPersistenceAndDeletion
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods_________________________________() {
	}

	@Test
	public void testCreateStudent() throws InvalidParametersException {
		// only minimal testing because this is a wrapper method for
		// another well-tested method.

		StudentData student = new StudentData(
				"|name of tcs student|tcsStudent@gmail.com|",
				"tmapit.tcs.course");
		BackDoor.deleteStudent(student.course, student.email);
		verifyAbsentInDatastore(student);
		BackDoor.createStudent(student);
		verifyPresentInDatastore(student);
		BackDoor.deleteStudent(student.course, student.email);
		verifyAbsentInDatastore(student);
	}

	@Test
	public void testGetKeyForStudent() throws InvalidParametersException {

		StudentData student = new StudentData(
				"t1|name of tgsr student|tgsr@gmail.com|", "course1");
		BackDoor.createStudent(student);
		String key = BackDoor.getKeyForStudent(student.course, student.email);
		System.out.println("Key for " + student.email + " is:" + key);
		// check for some characteristics of the key
		String errorMessage = key + "[length="+key.length()+"] is not as expected";
		assertTrue(errorMessage,key.length() > 30 && key.length() < 60);
		assertTrue(errorMessage, key.indexOf(" ") < 0);
		
		//clean up student as this is an orphan entity
		BackDoor.deleteStudent(student.course, student.email);
	}

	@Test
	public void testGetStudentAsJson() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditStudent() {

		// check for successful edit
		refreshDataInDatastore();
		StudentData student = dataBundle.students.get("student1InCourse1");
		String originalEmail = student.email;
		student.name = "New name";
		student.email = "new@gmail.com";
		student.comments = "new comments";
		student.profile = new Text("new profile");
		student.team = "new team";
		String status = BackDoor.editStudent(originalEmail, student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(student);

		// test for unsuccessful edit
		student.course = "non-existent";
		status = BackDoor.editStudent(originalEmail, student);
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
	public void testCreateEvaluation() throws InvalidParametersException {
		// only minimal testing because this is a wrapper method for
		// another well-tested method.

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
	public void testGetEvaluationAsJson() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditEvaluation() {

		refreshDataInDatastore();

		// check for successful edit
		EvaluationData e = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");

		e.gracePeriod = e.gracePeriod + 1;
		e.instructions = e.instructions + "x";
		e.p2pEnabled = (!e.p2pEnabled);
		e.startTime = Common.getDateOffsetToCurrentTime(-2);
		e.endTime = Common.getDateOffsetToCurrentTime(-1);
		e.activated = (!e.activated);
		e.published = (!e.published);
		e.timeZone = e.timeZone + 1.0;

		String status = BackDoor.editEvaluation(e);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(e);

		// not testing for unsuccesful edit because this does 
		//  not go through the Logic API (i.e., no error checking done)

	}

	@Test
	public void testDeleteEvaluation() {
		// already tested by testPersistenceAndDeletion
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
	public void testEditSubmission() {

		refreshDataInDatastore();

		// check for successful edit
		SubmissionData submission = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		submission.justification = new Text(submission.justification.getValue()
				+ "x");
		submission.points = submission.points + 10;
		String status = BackDoor.editSubmission(submission);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(submission);

		// test for unsuccessful edit
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
	private void ____helper_methods_________________________________() {
	}

	@Test
	public void testDataBundle() throws Exception {

		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER
				+ "/typicalDataBundle.json");
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
		assertEquals("profiledetail for student1InCourse1",
				student1InCourse1.profile.getValue());

		StudentData student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse1", student2InCourse2.id);
		assertEquals("student2 In Course2", student2InCourse2.name);
		assertEquals("Team 2.1", student2InCourse2.team);

		EvaluationData evaluation1 = data.evaluations
				.get("evaluation1InCourse1OfCoord1");
		assertEquals("evaluation1 In Course1", evaluation1.name);
		assertEquals("idOfCourse1OfCoord1", evaluation1.course);
		assertEquals("instructions for evaluation1InCourse1",
				evaluation1.instructions);
		assertEquals(10, evaluation1.gracePeriod);
		assertEquals(true, evaluation1.p2pEnabled);
		assertEquals("Sun Apr 01 23:59:00 SGT 2012",
				evaluation1.startTime.toString());
		assertEquals("Tue Apr 30 23:59:00 SGT 2013",
				evaluation1.endTime.toString());
		assertEquals(true, evaluation1.activated);
		assertEquals(false, evaluation1.published);
		assertEquals(2.0, evaluation1.timeZone, 0.01);

		EvaluationData evaluation2 = data.evaluations
				.get("evaluation2InCourse1OfCoord1");
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
	}

	private void refreshDataInDatastore() {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		BackDoor.persistNewDataBundle(jsonString);
	}

	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals("null", BackDoor.getCourseAsJson(course.id));
	}

	private void verifyAbsentInDatastore(StudentData student) {
		assertEquals("null",
				BackDoor.getStudentAsJson(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationData evaluation1InCourse1) {
		assertEquals("null", BackDoor.getEvaluationAsJson(
				evaluation1InCourse1.course, evaluation1InCourse1.name));
	}

	private void verifyAbsentInDatastore(SubmissionData subInDeletedEvaluation) {
		String submissionAsJson = BackDoor.getSubmissionAsJson(
				subInDeletedEvaluation.course,
				subInDeletedEvaluation.evaluation,
				subInDeletedEvaluation.reviewer,
				subInDeletedEvaluation.reviewee);
		assertEquals("null", submissionAsJson);
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
	}

	private void verifyPresentInDatastore(SubmissionData expectedSubmission) {
		String submissionsJsonString = BackDoor.getSubmissionAsJson(
				expectedSubmission.course, expectedSubmission.evaluation,
				expectedSubmission.reviewer, expectedSubmission.reviewee);
		SubmissionData actualSubmission = gson.fromJson(submissionsJsonString,
				SubmissionData.class);
		assertEquals(gson.toJson(expectedSubmission),
				gson.toJson(actualSubmission));
	}

	private void verifyPresentInDatastore(EvaluationData expectedEvaluation) {
		String evaluationJsonString = BackDoor.getEvaluationAsJson(
				expectedEvaluation.course, expectedEvaluation.name);
		EvaluationData actualEvaluation = gson.fromJson(evaluationJsonString,
				EvaluationData.class);
		// equalize id field before comparing (because id field is
		// autogenerated by GAE)
		assertEquals(gson.toJson(expectedEvaluation),
				gson.toJson(actualEvaluation));
	}

	private void verifyPresentInDatastore(StudentData expectedStudent) {
		String studentJsonString = BackDoor.getStudentAsJson(
				expectedStudent.course, expectedStudent.email);
		StudentData actualStudent = gson.fromJson(studentJsonString,
				StudentData.class);
		StudentData.equalizeIrrelevantData(expectedStudent, actualStudent);
		assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(CourseData expectedCourse) {
		String courseJsonString = BackDoor.getCourseAsJson(expectedCourse.id);
		CourseData actualCourse = gson.fromJson(courseJsonString,
				CourseData.class);
		assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
	}

	private void verifyPresentInDatastore(CoordData expectedCoord) {
		String coordJsonString = BackDoor.getCoordAsJson(expectedCoord.id);
		CoordData actualCoord = gson.fromJson(coordJsonString, CoordData.class);
		assertEquals(gson.toJson(expectedCoord), gson.toJson(actualCoord));
	}

	private void verifyAbsentInDatastore(CoordData expectedCoord) {
		assertEquals("null", BackDoor.getCoordAsJson(expectedCoord.id));
	}

}
