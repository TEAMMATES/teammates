package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.InvalidParametersException;
import teammates.test.driver.BackDoor;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class BackDoorTest extends BaseTestCase {

	private static Gson gson = Common.getTeammatesGson();
	private static String jsonString;
	private static DataBundle dataBundle;

	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER
				+ "/typicalDataBundle.json");

		dataBundle = gson.fromJson(jsonString, DataBundle.class);
	}

	@AfterClass
	public static void tearDown() {
		BackDoor.deleteInstructors(jsonString);
		printTestClassFooter();
	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods_________________________________() {
	}

	@Test
	public void testPersistenceAndDeletion() {
		// Clean up to avoid clashes with existing data.
		// We delete courses first in case the same course ID exists under a
		// different instructor not listed in our databundle.
		// check if deleteInstructors worked

		for (CourseData course : dataBundle.courses.values()) {
			BackDoor.deleteCourse(course.id);
		}

		BackDoor.deleteInstructors(jsonString);

		// ensure clean up worked
		for (InstructorData instructor : dataBundle.instructors.values()) {
			verifyAbsentInDatastore(instructor);
		}
		for (CourseData course : dataBundle.courses.values()) {
			verifyAbsentInDatastore(course);
		}

		// check persisting
		String status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyPresentInDatastore(jsonString);

		// ----------deleting Instructor entities-------------------------
		InstructorData instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		verifyPresentInDatastore(instructor1OfCourse1);
		status = BackDoor.deleteInstructor(instructor1OfCourse1.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor1OfCourse1);
		
		InstructorData instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
		verifyPresentInDatastore(instructor2OfCourse1);
		status = BackDoor.deleteInstructor(instructor2OfCourse1.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor2OfCourse1);
		
		InstructorData instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
		verifyPresentInDatastore(instructor1OfCourse2);
		status = BackDoor.deleteInstructor(instructor1OfCourse2.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor1OfCourse2);

		InstructorData instructor2OfCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
		verifyPresentInDatastore(instructor2OfCourse2);
		status = BackDoor.deleteInstructor(instructor2OfCourse2.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor2OfCourse2);
		
		// Instructor of 2 courses - Deleting the instructor once removes all instructors of this google id
		// TODO: deleteInstructor(String instructorId, String courseId)
		InstructorData instructor3OfCourse1 = dataBundle.instructors.get("instructor3OfCourse1");
		verifyPresentInDatastore(instructor3OfCourse1);
		status = BackDoor.deleteInstructor(instructor3OfCourse1.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor3OfCourse1);
		
		// This is already deleted in the previous operation, when `googleId` = "idOfInstructor3" was deleted
		InstructorData instructor3OfCourse2 = dataBundle.instructors.get("instructor3OfCourse2");
		//verifyPresentInDatastore(instructor3OfCourse2);
		status = BackDoor.deleteInstructor(instructor3OfCourse2.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(instructor3OfCourse2);

		// try to delete again. should succeed.
		status = BackDoor.deleteInstructor(instructor2OfCourse2.googleId);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		status = BackDoor.deleteInstructor("idOfInstructor4");
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// ----------deleting Evaluation entities-------------------------

		// check the existence of a submission that will be deleted along with
		// the evaluation
		SubmissionData subInDeletedEvaluation = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(subInDeletedEvaluation);

		// delete the evaluation and verify it is deleted
		EvaluationData evaluation1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
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
				.get("evaluation2InCourse1");
		verifyPresentInDatastore(evaluation2InCourse1);

		// ----------deleting Course entities-------------------------

		// #COURSE 2
		CourseData course2 = dataBundle.courses.get("typicalCourse2");
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
				.get("evaluation1InCourse1");
		verifyAbsentInDatastore(evaluation1InCourse2);
		
		// #COURSE 1
		CourseData course1 = dataBundle.courses.get("typicalCourse1");
		verifyPresentInDatastore(course1);
		status = BackDoor.deleteCourse(course1.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(course1);
		
		// check if related student entities are also deleted
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		verifyAbsentInDatastore(student1InCourse1);
		
		// previously not deleted evaluation should be deleted now since the course has been deleted
		verifyAbsentInDatastore(evaluation2InCourse1);
		
		// #COURSE NO EVALS
		CourseData courseNoEvals = dataBundle.courses.get("courseNoEvals");
		verifyPresentInDatastore(courseNoEvals);
		status = BackDoor.deleteCourse(courseNoEvals.id);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyAbsentInDatastore(courseNoEvals);
		
		//-------------------------------------------------------------------------
		// RECREATE ALL DATA. this should succeed if all previous data were deleted
		status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
	}
	
	@SuppressWarnings("unused")
	private void ____ACCOUNT_level_methods_________________________________() {
	}
	
	@Test
	public void testCreateAccount() {
		AccountData newAccount = dataBundle.accounts.get("instructor1OfCourse1");
		BackDoor.deleteAccount(newAccount.googleId);
		verifyAbsentInDatastore(newAccount);
		BackDoor.createAccount(newAccount);
		verifyPresentInDatastore(newAccount);
	}
	
	@Test
	public void testGetAccountAsJson() {
		AccountData testAccount = dataBundle.accounts.get("instructor1OfCourse1");
		verifyPresentInDatastore(testAccount);
		String actualString = BackDoor.getAccountAsJson(testAccount.googleId);
		AccountData actualAccount = gson.fromJson(actualString, AccountData.class);
		assertEquals(gson.toJson(testAccount), gson.toJson(actualAccount));
	}
	
	@Test
	public void testEditAccount() {
		AccountData testAccount = dataBundle.accounts.get("instructor1OfCourse1");
		verifyPresentInDatastore(testAccount);
		testAccount.name = "New name";
		BackDoor.editAccount(testAccount);
		verifyPresentInDatastore(testAccount);
	}
	
	@Test
	public void testDeleteAccount() {
		AccountData testAccount = dataBundle.accounts.get("instructor2OfCourse1");
		BackDoor.createAccount(testAccount);
		verifyPresentInDatastore(testAccount);
		BackDoor.deleteAccount(testAccount.googleId);
		verifyAbsentInDatastore(testAccount);
	}

	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_level_methods_________________________________() {
	}

	@Test
	public void testDeleteInstructors() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testCreateInstructor() {
		// only minimal testing because this is a wrapper method for
		// another well-tested method.

		String instructorId = "tmapitt.tcc.instructor";
		String courseId = "tmapitt.tcc.course";
		String name = "Tmapitt testInstr Name";
		String email = "tmapitt@tci.com";
		InstructorData instructor = new InstructorData(instructorId, courseId, name, email);
		
		// Make sure not already inside
		BackDoor.deleteInstructor(instructorId);
		verifyAbsentInDatastore(instructor);
		
		// Perform creation
		BackDoor.createInstructor(instructor);
		verifyPresentInDatastore(instructor);
		
		// Clean up
		BackDoor.deleteInstructor(instructorId);
		verifyAbsentInDatastore(instructor);
	}

	@Test
	public void testGetInstructorAsJson() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testDeleteInstructor() {
		// already tested by testPersistenceAndDeletion
	}

	@Test
	public void testEditInstructor() {
		// method not implemented
	}

	

	@Test
	public void testGetCoursesByInstructorId() throws InvalidParametersException {

		// testing for non-existent instructor
		String[] courses = BackDoor.getCoursesByInstructorId("nonExistentInstructor");
		assertEquals("[]", Arrays.toString(courses));

		// Create 2 courses for a new instructor
		String course1 = "AST.TGCBCI.course1";
		String course2 = "AST.TGCBCI.course2";
		BackDoor.createCourse(new CourseData(course1, "tmapit tgcbci c1OfInstructor1"));
		BackDoor.createCourse(new CourseData(course2, "tmapit tgcbci c2OfInstructor1"));
		
		// create a fresh instructor with relations for the 2 courses
		String instructor1Id = "AST.TGCBCI.instructor1";
		String instructor1name = "AST TGCBCI Instructor";
		String instructor1email = "instructor1@ast.tgcbi";
		BackDoor.deleteInstructor(instructor1Id);
		BackDoor.createInstructor(new InstructorData(instructor1Id, course1, instructor1name, instructor1email));
		BackDoor.createInstructor(new InstructorData(instructor1Id, course2, instructor1name, instructor1email));

		//============================================================================
		// Don't be confused by the following: it has no relation with the above instructor/course(s)
		
		// add a course that belongs to a different instructor
		String instructor2Id = "AST.TGCBCI.instructor2";
		String course3 = "AST.TGCBCI.course3";
		BackDoor.createCourse(new CourseData(course3, "tmapit tgcbci c1OfInstructor2"));

		courses = BackDoor.getCoursesByInstructorId(instructor1Id);
		assertEquals("[" + course1 + ", " + course2 + "]", Arrays.toString(courses));

		BackDoor.deleteInstructor(instructor1Id);
		BackDoor.deleteInstructor(instructor2Id);
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
				"Name of tmapitt.tcc.instructor");
		
		// Make sure not already inside
		BackDoor.deleteCourse(courseId);
		verifyAbsentInDatastore(course);
		
		// Perform creation
		BackDoor.createCourse(course);
		verifyPresentInDatastore(course);
		
		// Clean up
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
		
		String pattern = "(\\w*)";
		// check for some characteristics of the key, key should be url-safe, refer to app-engine doc
		//
		//A key can be converted to a string by passing the Key object to str(). The string is 
		//"urlsafe"—it uses only characters valid for use in URLs. The string representation of 
		//the key can be converted back to a Key object by passing it 
		//to the Key constructor (the encoded argument).
		String errorMessage = key + "[length="+key.length()+"][reg="+key.matches(pattern)+"] is not as expected";
		assertTrue(errorMessage,key.length() > 30 && key.matches(pattern));
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
				.get("evaluation1InCourse1");

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

		// INSTRUCTORS
		InstructorData instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		assertEquals("idOfInstructor1OfCourse1", instructor1OfCourse1.googleId);
		assertEquals("idOfTypicalCourse1", instructor1OfCourse1.courseId);

		InstructorData instructor2OfCourse1 = data.instructors.get("instructor2OfCourse1");
		assertEquals("idOfInstructor2OfCourse1", instructor2OfCourse1.googleId);
		assertEquals("idOfTypicalCourse1", instructor2OfCourse1.courseId);
		
		InstructorData instructor1OfCourse2 = data.instructors.get("instructor1OfCourse2");
		assertEquals("idOfInstructor1OfCourse2", instructor1OfCourse2.googleId);
		assertEquals("idOfTypicalCourse2", instructor1OfCourse2.courseId);
		
		InstructorData instructor2OfCourse2 = data.instructors.get("instructor2OfCourse2");
		assertEquals("idOfInstructor2OfCourse2", instructor2OfCourse2.googleId);
		assertEquals("idOfTypicalCourse2", instructor2OfCourse2.courseId);
		
		InstructorData instructor3OfCourse1 = data.instructors.get("instructor3OfCourse1");
		assertEquals("idOfInstructor3", instructor3OfCourse1.googleId);
		assertEquals("idOfTypicalCourse1", instructor3OfCourse1.courseId);
		
		InstructorData instructor3OfCourse2 = data.instructors.get("instructor3OfCourse2");
		assertEquals("idOfInstructor3", instructor3OfCourse2.googleId);
		assertEquals("idOfTypicalCourse2", instructor3OfCourse2.courseId);
		
		InstructorData instructor4 = data.instructors.get("instructor4");
		assertEquals("idOfInstructor4", instructor4.googleId);
		assertEquals("idOfCourseNoEvals", instructor4.courseId);
		
		// COURSES
		CourseData course1 = data.courses.get("typicalCourse1");
		assertEquals("idOfTypicalCourse1", course1.id);
		assertEquals("Typical Course 1 with 2 Evals", course1.name);
		
		CourseData course2 = data.courses.get("typicalCourse2");
		assertEquals("idOfTypicalCourse2", course2.id);
		assertEquals("Typical Course 2 with 1 Evals", course2.name);

		// STUDENTS
		StudentData student1InCourse1 = data.students.get("student1InCourse1");
		assertEquals("student1InCourse1", student1InCourse1.id);
		assertEquals("student1 In Course1", student1InCourse1.name);
		assertEquals("Team 1.1", student1InCourse1.team);
		assertEquals("comment for student1InCourse1",
				student1InCourse1.comments);
		assertEquals("idOfTypicalCourse1", student1InCourse1.course);
		
		StudentData student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse1", student2InCourse2.id);
		assertEquals("student2 In Course2", student2InCourse2.name);
		assertEquals("Team 2.1", student2InCourse2.team);

		// EVALUATIONS
		EvaluationData evaluation1 = data.evaluations
				.get("evaluation1InCourse1");
		assertEquals("evaluation1 In Course1", evaluation1.name);
		assertEquals("idOfTypicalCourse1", evaluation1.course);
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
				.get("evaluation2InCourse1");
		assertEquals("evaluation2 In Course1", evaluation2.name);
		assertEquals("idOfTypicalCourse1", evaluation2.course);

		// SUBMISSIONS
		SubmissionData submissionFromS1C1ToS2C1 = data.submissions
				.get("submissionFromS1C1ToS2C1");
		assertEquals("student1InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.reviewer);
		assertEquals("student2InCourse1@gmail.com",
				submissionFromS1C1ToS2C1.reviewee);
		assertEquals("idOfTypicalCourse1", submissionFromS1C1ToS2C1.course);
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
		BackDoor.deleteInstructors(jsonString);
		BackDoor.deleteCourses(jsonString);
		String status = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
	}

	private void verifyAbsentInDatastore(AccountData account) {
		assertEquals("null", BackDoor.getAccountAsJson(account.googleId));
	}
	
	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals("null", BackDoor.getCourseAsJson(course.id));
	}
	
	private void verifyAbsentInDatastore(InstructorData expectedInstructor) {
		assertEquals("null", BackDoor.getInstructorAsJson(expectedInstructor.googleId, expectedInstructor.courseId));
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
		HashMap<String, AccountData> accounts = data.accounts;
		for (AccountData expectedAccount : accounts.values()) {
			verifyPresentInDatastore(expectedAccount);
		}

		HashMap<String, CourseData> courses = data.courses;
		for (CourseData expectedCourse : courses.values()) {
			verifyPresentInDatastore(expectedCourse);
		}
		
		HashMap<String, InstructorData> instructors = data.instructors;
		for (InstructorData expectedInstructor : instructors.values()) {
			verifyPresentInDatastore(expectedInstructor);
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
		// Ignore time field as it is stamped at the time of creation in testing
		actualCourse.createdAt = expectedCourse.createdAt;
		assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
	}

	private void verifyPresentInDatastore(InstructorData expectedInstructor) {
		String instructorJsonString = BackDoor.getInstructorAsJson(expectedInstructor.googleId, expectedInstructor.courseId);
		InstructorData actualInstructor = gson.fromJson(instructorJsonString, InstructorData.class);
		assertEquals(gson.toJson(expectedInstructor), gson.toJson(actualInstructor));
	}
	
	private void verifyPresentInDatastore(AccountData expectedAccount) {
		String accountJsonString = BackDoor.getAccountAsJson(expectedAccount.googleId);
		AccountData actualAccount = gson.fromJson(accountJsonString, AccountData.class);
		assertEquals(gson.toJson(expectedAccount), gson.toJson(actualAccount));
	}
}
