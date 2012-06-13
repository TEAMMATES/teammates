package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import static teammates.TeamEvalResult.NSB;
import static teammates.TeamEvalResult.NSU;
import static teammates.TeamEvalResult.NA;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Datastore;
import teammates.TeamEvalResult;
import teammates.api.*;
import teammates.datatransfer.*;
import teammates.datatransfer.EvaluationData.EvalStatus;
import teammates.datatransfer.StudentData.UpdateStatus;
import teammates.persistent.Student;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;

public class APIServletTest extends BaseTestCase {

	private static LocalServiceTestHelper helper;
	private final static APIServlet apiServlet = new APIServlet();
	private static Gson gson = Common.getTeammatesGson();
	static String jsonString;
	static {
		try {
			jsonString = Common.readFile(Common.TEST_DATA_FOLDER
					+ "/typicalDataBundle.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private DataBundle dataBundle;

	private static String queueXmlFilePath = System.getProperty("user.dir")
			+ File.separator + "src" + File.separator + "main" + File.separator
			+ "webapp" + File.separator + "WEB-INF" + File.separator
			+ "queue.xml";

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(APIServlet.class, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);
		Datastore.initialize();
	}

	@Before
	public void caseSetUp() throws ServletException, IOException {
		/*
		 * We have to explicitly set the path of queue.xml because the test
		 * environment cannot find it. Apparently, this is a bug in the test
		 * environment (as mentioned in
		 * http://turbomanage.wordpress.com/2010/03/
		 * 03/a-recipe-for-unit-testing-appengine-task-queues/ The bug might get
		 * fixed in future SDKs.
		 */
		LocalTaskQueueTestConfig ltqtc = new LocalTaskQueueTestConfig();
		ltqtc.setQueueXmlPath(queueXmlFilePath);
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		LocalUserServiceTestConfig localUserServiceTestConfig = new LocalUserServiceTestConfig();
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig(),
				new LocalMailServiceTestConfig(), localUserServiceTestConfig,
				ltqtc);

		/**
		 * LocalServiceTestHelper is supposed to run in the same timezone as Dev
		 * server and production server i.e. (i.e. UTC timezone), as stated in
		 * https
		 * ://developers.google.com/appengine/docs/java/tools/localunittesting
		 * /javadoc/com/google/appengine/tools/development/testing/
		 * LocalServiceTestHelper#setTimeZone%28java.util.TimeZone%29
		 * 
		 * But it seems Dev server does not run on UTC timezone, but it runs on
		 * "GMT+8:00" (Possibly, a bug). Therefore, I'm changing timeZone of
		 * LocalServiceTestHelper to match the Dev server. But note that tests
		 * that run on Dev server might fail on Production server due to this
		 * problem. We need to find a fix.
		 */
		helper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		helper.setUp();
		apiServlet.init();

	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods___________________________________() {
	}

	@Test
	public void testCoordGetLoginUrl() {
		printTestCaseHeader();
		assertEquals("/_ah/login?continue=www.abc.com",
				APIServlet.getLoginUrl("www.abc.com"));
	}

	@Test
	public void testCoordGetLogoutUrl() {
		printTestCaseHeader();
		assertEquals("/_ah/logout?continue=www.def.com",
				APIServlet.getLogoutUrl("www.def.com"));
	}

	@Test
	public void testPersistDataBundle() throws Exception {
		printTestCaseHeader();
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		// clean up the datastore first, to avoid clashes with existing data
		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			apiServlet.deleteCoord(coord.id);
		}

		// try with empty dataBundle
		String status = apiServlet.persistNewDataBundle(new DataBundle());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// try with typical dataBundle
		apiServlet.persistNewDataBundle(dataBundle);
		verifyPresentInDatastore(jsonString);

		// try again, should throw exception
		try {
			apiServlet.persistNewDataBundle(dataBundle);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		// try with null
		DataBundle nullDataBundle = null;
		try {
			apiServlet.persistNewDataBundle(nullDataBundle);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		// try with invalid parameters in an entity
		CourseData invalidCourse = new CourseData();
		dataBundle = new DataBundle();
		dataBundle.courses.put("invalid", invalidCourse);
		try {
			apiServlet.persistNewDataBundle(dataBundle);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		// Not checking for invalid values in other entities because they
		// should be checked at lower level methods
	}

	@Test
	public void testGetLoggedInUser() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// also make this user a student
		StudentData coordAsStudent = new StudentData(
				"|Coord As Student|coordasstudent@yahoo.com|", "some-course");
		coordAsStudent.id = coord.id;
		apiServlet.createStudent(coordAsStudent);

		helper.setEnvIsLoggedIn(true);
		helper.setEnvIsAdmin(true);

		helper.setEnvEmail(coord.id);
		helper.setEnvAuthDomain("gmail.com");
		UserData user = apiServlet.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(true, user.isStudent);

		// this user is no longer a student
		apiServlet.deleteStudent(coordAsStudent.course, coordAsStudent.email);
		// this user is no longer an admin
		helper.setEnvIsAdmin(false);

		user = apiServlet.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(false, user.isStudent);

		// check for unregistered student
		helper.setEnvEmail("unknown");
		helper.setEnvAuthDomain("gmail.com");
		user = apiServlet.getLoggedInUser();
		assertEquals("unknown", user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(false, user.isStudent);

		// check for user who is only a student
		StudentData student = dataBundle.students.get("student1InCourse1");
		helper.setEnvEmail(student.id);
		helper.setEnvAuthDomain("gmail.com");
		user = apiServlet.getLoggedInUser();
		assertEquals(student.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(true, user.isStudent);

		// check for user not logged in
		helper.setEnvIsLoggedIn(false);
		assertEquals(null, apiServlet.getLoggedInUser());
		assertEquals(null, apiServlet.getLoggedInUser());
		assertEquals(null, apiServlet.getLoggedInUser());
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods____________________________________() {
	}

	@Test
	public void testCreateCoord() throws InvalidParametersException,
			EntityAlreadyExistsException {
		printTestCaseHeader();

		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// delete, to avoid clashes with existing data
		apiServlet.deleteCoord(coord.id);
		verifyAbsentInDatastore(coord);
		// create
		apiServlet.createCoord(coord.id, coord.name, coord.email);
		// read existing coord
		verifyPresentInDatastore(coord);
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		// create a course to check cascade delete later
		apiServlet.createCourse(coord.id, course.id, course.name);
		verifyPresentInDatastore(course);
		// delete existing
		apiServlet.deleteCoord(coord.id);
		// read non-existent coord
		verifyAbsentInDatastore(coord);
		// check for cascade delete
		verifyAbsentInDatastore(course);
		// delete non-existent (fails silently)
		apiServlet.deleteCoord(coord.id);

		// try one invalid input for each parameter
		try {
			apiServlet.createCoord("valid-id", "", "valid@email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_EMPTY_STRING, e.errorCode);
			Common.assertContains("Coordinator name", e.getMessage());
		}

		try {
			apiServlet.createCoord("valid-id", "valid name",
					"invalid email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_EMAIL, e.errorCode);
			Common.assertContains("Email address", e.getMessage());
		}

		try {
			apiServlet.createCoord("invalid id", "valid name",
					"valid@email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			Common.assertContains("Google ID", e.getMessage());
		}

	}

	@Test
	public void testGetCoord() {
		// already tested in testCreateCoord
	}

	@Test
	public void testEditCoord() {
		// method not implemented
	}

	@Test
	public void testDeleteCoord() {
		// already tested in testCreateCoord
	}

	@Test
	public void testGetCourseListForCoord() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// coord with 2 courses
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		HashMap<String, CourseData> courseList = apiServlet
				.getCourseListForCoord(coord.id);
		assertEquals(2, courseList.size());
		for (CourseData item : courseList.values()) {
			// check if course belongs to this coord
			assertEquals(coord.id, apiServlet.getCourse(item.id).coord);
		}

		// coord with 0 courses
		coord = dataBundle.coords.get("typicalCoord3");
		courseList = apiServlet.getCourseListForCoord(coord.id);
		assertEquals(0, courseList.size());

		// check for null parameter
		assertEquals(null, apiServlet.getCourseListForCoord(null));

		// non-existent coord
		try {
			apiServlet.getCourseListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testGetCourseDetailsListForCoord() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		HashMap<String, CourseData> courseListForCoord = apiServlet
				.getCourseDetailsListForCoord("idOfTypicalCoord1");
		assertEquals(2, courseListForCoord.size());
		String course1Id = "idOfCourse1OfCoord1";

		// course with 2 evaluations
		ArrayList<EvaluationData> course1Evals = courseListForCoord
				.get(course1Id).evaluations;
		assertEquals(2, course1Evals.size());
		assertEquals(course1Id, course1Evals.get(0).course);
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation1InCourse1OfCoord1"),
				course1Evals);
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation2InCourse1OfCoord1"),
				course1Evals);

		// course with 1 evaluation
		assertEquals(course1Id, course1Evals.get(1).course);
		ArrayList<EvaluationData> course2Evals = courseListForCoord
				.get("idOfCourse2OfCoord1").evaluations;
		assertEquals(1, course2Evals.size());
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation1InCourse2OfCoord1"),
				course2Evals);

		// course with 0 evaluations
		courseListForCoord = apiServlet
				.getCourseDetailsListForCoord("idOfTypicalCoord2");
		assertEquals(2, courseListForCoord.size());
		assertEquals(0,
				courseListForCoord.get("idOfCourse2OfCoord2").evaluations
						.size());

		// coord with 0 courses
		apiServlet.createCoord("coordWith0course", "Coord with 0 courses",
				"coordWith0course@gmail.com");
		courseListForCoord = apiServlet
				.getCourseDetailsListForCoord("coordWith0course");
		assertEquals(0, courseListForCoord.size());

		// null parameters
		assertEquals(null, apiServlet.getCourseDetailsListForCoord(null));

		// non-existent coord
		try {
			apiServlet.getCourseDetailsListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}

	}

	@Test
	public void testGetEvalListForCoord() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// coord with 3 Evals
		CoordData coord1 = dataBundle.coords.get("typicalCoord1");
		ArrayList<EvaluationData> evalList = apiServlet
				.getEvaluationsListForCoord(coord1.id);
		assertEquals(3, evalList.size());
		for (EvaluationData ed : evalList) {
			assertTrue(ed.course.contains("Coord1"));
		}

		// coord with 1 eval
		CoordData coord2 = dataBundle.coords.get("typicalCoord2");
		evalList = apiServlet.getEvaluationsListForCoord(coord2.id);
		assertEquals(1, evalList.size());
		for (EvaluationData ed : evalList) {
			assertTrue(ed.course.contains("Coord2"));
		}

		// coord with 0 eval
		CoordData coord3 = dataBundle.coords.get("typicalCoord3");
		evalList = apiServlet.getEvaluationsListForCoord(coord3.id);
		assertEquals(0, evalList.size());

		// null parameter
		assertEquals(null, apiServlet.getEvaluationsListForCoord(null));

		// non-existent coord
		try {
			apiServlet.getEvaluationsListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testTfsListForCoord() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// coord with 2 Tfs
		verifyTfsListForCoord(dataBundle.coords.get("typicalCoord2").id, 2);
		// coord with 0 Tfs
		verifyTfsListForCoord(dataBundle.coords.get("typicalCoord3").id, 0);

		// null parameters
		assertEquals(null, apiServlet.getTfsListForCoord(null));

		try {
			apiServlet.getTfsListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods___________________________________() {
	}

	@Test
	public void testCreateCourse() throws InvalidParametersException,
			EntityAlreadyExistsException {
		printTestCaseHeader();

		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// delete, to avoid clashes with existing data
		apiServlet.deleteCoord(coord.id);

		CourseData course = dataBundle.courses.get("course1OfCoord1");

		// read non-existent course
		verifyAbsentInDatastore(course);

		// create and read
		apiServlet.createCourse(course.coord, course.id, course.name);
		verifyPresentInDatastore(course);

		// try to create again
		try {
			apiServlet.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		// create with invalid coord-id
		course.coord = "invalid id";
		try {
			apiServlet.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			Common.assertContains("Google ID", e.getMessage());
		}

		// create with invalid course ID
		course.coord = "typicalCoord1";
		course.id = "invalid id";
		try {
			apiServlet.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			Common.assertContains("Course ID", e.getMessage());
		}

		// create with invalid course ID
		course.id = "valid-course-id";
		course.name = "";
		try {
			apiServlet.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_EMPTY_STRING, e.errorCode);
			Common.assertContains("Course name", e.getMessage());
		}
	}

	@Test
	public void testGetCourse() {
		// mostly tested in testCreateCourse
		assertEquals(null, apiServlet.getCourse(null));
	}

	@Test
	public void testGetCourseDetails() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		CourseData course = dataBundle.courses.get("course1OfCoord1");
		CourseData courseDetials = apiServlet.getCourseDetails(course.id);
		assertEquals(course.id, courseDetials.id);
		assertEquals(course.name, courseDetials.name);
		assertEquals(2, courseDetials.teamsTotal);
		assertEquals(5, courseDetials.studentsTotal);
		assertEquals(0, courseDetials.unregisteredTotal);

		// TODO: more testing e.g, course without students etc.

	}

	@Test
	public void testEditCourse() {
		// method not implemented
	}

	@Test
	public void testDeleteCourse() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		CourseData course1OfCoord = dataBundle.courses.get("course1OfCoord1");

		// ensure there are entities in the datastore under this course
		assertTrue(apiServlet.getStudentListForCourse(course1OfCoord.id).size() != 0);
		verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyPresentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1"));
		verifyPresentInDatastore(dataBundle.teamFormingSessions
				.get("tfsInCourse1"));

		StudentData studentInCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(course1OfCoord.id, studentInCourse.course);
		verifyPresentInDatastore(studentInCourse);

		apiServlet.deleteCourse(course1OfCoord.id);

		// ensure the course and related entities are deleted
		verifyAbsentInDatastore(course1OfCoord);
		verifyAbsentInDatastore(studentInCourse);
		verifyAbsentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyAbsentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1"));
		verifyAbsentInDatastore(dataBundle.teamFormingSessions
				.get("tfsInCourse1"));

		// try to delete again. Should fail silently.
		apiServlet.deleteCourse(course1OfCoord.id);

		// try null parameter. Should fail silently.
		apiServlet.deleteCourse(null);
	}

	@Test
	public void testGetStudentListForCourse() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// course with multiple students
		CourseData course1OfCoord1 = dataBundle.courses.get("course1OfCoord1");
		List<StudentData> studentList = apiServlet
				.getStudentListForCourse(course1OfCoord1.id);
		assertEquals(5, studentList.size());
		for (StudentData s : studentList) {
			assertEquals(course1OfCoord1.id, s.course);
		}

		// course with 0 students
		CourseData course2OfCoord1 = dataBundle.courses.get("course2OfCoord1");
		studentList = apiServlet.getStudentListForCourse(course2OfCoord1.id);
		assertEquals(0, studentList.size());

		assertEquals(null, apiServlet.getStudentListForCourse(null));

		// non-existent course
		try {
			apiServlet.getStudentListForCourse("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testEnrollStudents() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		String coordId = "coordForEnrollTesting";
		apiServlet.createCoord(coordId, "Coord for Enroll Testing",
				"coordForEnrollTestin@gmail.com");
		String courseId = "courseForEnrollTest";
		apiServlet.createCourse(coordId, courseId, "Course for Enroll Testing");
		String EOL = Common.EOL;

		// all valid students, but contains blank lines
		String line0 = "t1|n1|e1@g|c1";
		String line1 = " t2|  n2|  e2@g|  c2";
		String line2 = "t3|n3|e3@g|c3  ";
		String line3 = "t4|n4|  e4@g|c4";
		String line4 = "t5|n5|e5@g  |c5";
		String lines = line0 + EOL + line1 + EOL + line2 + EOL
				+ "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
				+ EOL + "    " + EOL + EOL;
		List<StudentData> enrollResults = apiServlet.enrollStudents(lines,
				courseId);

		assertEquals(5, enrollResults.size());
		assertEquals(5, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(new StudentData(line0, courseId),
				enrollResults.get(0), StudentData.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentData(line1, courseId),
				enrollResults.get(1), StudentData.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentData(line4, courseId),
				enrollResults.get(4), StudentData.UpdateStatus.NEW);

		// includes a mix of unmodified, modified, and new
		String line0_1 = "t3|modified name|e3@g|c3";
		String line5 = "t6|n6|e6@g|c6";
		lines = line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
		enrollResults = apiServlet.enrollStudents(lines, courseId);
		assertEquals(6, enrollResults.size());
		assertEquals(6, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(new StudentData(line0, courseId),
				enrollResults.get(0), StudentData.UpdateStatus.UNMODIFIED);
		verifyEnrollmentResultForStudent(new StudentData(line0_1, courseId),
				enrollResults.get(1), StudentData.UpdateStatus.MODIFIED);
		verifyEnrollmentResultForStudent(new StudentData(line1, courseId),
				enrollResults.get(2), StudentData.UpdateStatus.UNMODIFIED);
		verifyEnrollmentResultForStudent(new StudentData(line5, courseId),
				enrollResults.get(3), StudentData.UpdateStatus.NEW);
		assertEquals(StudentData.UpdateStatus.NOT_IN_ENROLL_LIST,
				enrollResults.get(4).updateStatus);
		assertEquals(StudentData.UpdateStatus.NOT_IN_ENROLL_LIST,
				enrollResults.get(5).updateStatus);

		// includes an incorrect line, no changes should be done to the database
		String incorrectLine = "incorrectly formatted line";
		lines = "t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
				+ line3;
		try {
			enrollResults = apiServlet.enrollStudents(lines, courseId);
			fail("Did not throw exception for incorrectly formatted line");
		} catch (EnrollException e) {
			assertTrue(e.getMessage().contains(incorrectLine));
		}
		assertEquals(6, apiServlet.getStudentListForCourse(courseId).size());

		// try null parameters
		try {
			apiServlet.enrollStudents(null, courseId);
			fail();
		} catch (EnrollException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("Enroll text", e.getMessage());
		}

		try {
			apiServlet.enrollStudents("any text", null);
			fail();
		} catch (EnrollException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("Course ID", e.getMessage());
		}

		// same student added, modified and unmodified in one shot
		apiServlet.createCourse("tes.coord", "tes.course", "TES Course");
		lines = "t8|n8|e8@g|c1" + EOL + "t8|n8a|e8@g|c1" + EOL
				+ "t8|n8a|e8@g|c1";
		enrollResults = apiServlet.enrollStudents(lines, "tes.course");

		assertEquals(3, enrollResults.size());
		assertEquals(StudentData.UpdateStatus.NEW,
				enrollResults.get(0).updateStatus);
		assertEquals(StudentData.UpdateStatus.MODIFIED,
				enrollResults.get(1).updateStatus);
		assertEquals(StudentData.UpdateStatus.UNMODIFIED,
				enrollResults.get(2).updateStatus);
	}

	@Test
	public void testSendRegistrationInviteForCourse() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();
		CourseData course1 = dataBundle.courses.get("course1OfCoord1");

		// send registration key to a class in which all are registered
		apiServlet.sendRegistrationInviteForCourse(course1.id);
		assertEquals(0, getNumberOfEmailTasksInQueue());

		// modify two students to make them 'unregistered' and send again
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		student1InCourse1.id = "";
		apiServlet.editStudent(student1InCourse1.email, student1InCourse1);
		StudentData student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		student2InCourse1.id = "";
		apiServlet.editStudent(student2InCourse1.email, student2InCourse1);
		apiServlet.sendRegistrationInviteForCourse(course1.id);
		assertEquals(2, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student1InCourse1);
		verifyRegistrationEmailToStudent(student2InCourse1);

		// send again
		apiServlet.sendRegistrationInviteForCourse(course1.id);
		assertEquals(4, getNumberOfEmailTasksInQueue());

		// try null parameters
		try {
			apiServlet.sendRegistrationInviteForCourse(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}
	}

	@Test
	public void testGetTeamsForCourse() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// testing for typical course
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		apiServlet.createStudent(new StudentData("|s1|s1@e|", course.id));
		apiServlet.createStudent(new StudentData("|s2|s2@e|", course.id));
		CourseData courseAsTeams = apiServlet.getTeamsForCourse(course.id);
		assertEquals(2, courseAsTeams.teams.size());

		String team1Id = "Team 1.1";
		assertEquals(team1Id, courseAsTeams.teams.get(0).name);
		assertEquals(team1Id, courseAsTeams.teams.get(0).profile.team);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(0).team);
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(1).team);

		String team2Id = "Team 1.2";
		assertEquals(team2Id, courseAsTeams.teams.get(1).name);
		assertEquals(team2Id, courseAsTeams.teams.get(1).profile.team);
		assertEquals(1, courseAsTeams.teams.get(1).students.size());
		assertEquals(team2Id, courseAsTeams.teams.get(1).students.get(0).team);

		assertEquals(2, courseAsTeams.loners.size());
		assertEquals("s1@e", courseAsTeams.loners.get(0).email);
		assertEquals("s2@e", courseAsTeams.loners.get(1).email);

		// try again without the loners
		refreshDataInDatastore();
		courseAsTeams = apiServlet.getTeamsForCourse(course.id);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(0, courseAsTeams.loners.size());

		assertEquals(null, apiServlet.getTeamsForCourse(null));

		// course without teams
		apiServlet.createCourse("coord1", "course1", "Course 1");
		assertEquals(0, apiServlet.getTeamsForCourse("course1").teams.size());

		// non-existent course
		try {
			apiServlet.getTeamsForCourse("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	@Test
	public void testCreateStudent() throws Exception {
		printTestCaseHeader();

		StudentData newStudent = new StudentData("t1|n1|e@com|c1",
				"tcs.course1");
		verifyAbsentInDatastore(newStudent);

		apiServlet.createStudent(newStudent);
		verifyPresentInDatastore(newStudent);

		// try to create the same student
		try {
			apiServlet.createStudent(newStudent);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		try {
			apiServlet.createStudent(null);
			fail();
		} catch (InvalidParametersException e) {
		}

	}

	@Test
	public void testGetStudent() {
		// mostly tested in testCreateStudent
		assertEquals(null, apiServlet.getStudent(null, "email@email.com"));
		assertEquals(null, apiServlet.getStudent("course-id", null));
	}

	@Test
	public void testGetStudentWithId() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("student in one course");
		StudentData studentInOneCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(1, apiServlet.getStudentsWithId(studentInOneCourse.id)
				.size());
		assertEquals(
				studentInOneCourse.email,
				apiServlet.getStudentsWithId(studentInOneCourse.id).get(0).email);
		assertEquals(studentInOneCourse.name,
				apiServlet.getStudentsWithId(studentInOneCourse.id).get(0).name);
		assertEquals(
				studentInOneCourse.course,
				apiServlet.getStudentsWithId(studentInOneCourse.id).get(0).course);

		______TS("student in two courses");
		// this student is in two courses, course1 and course 2.

		// get list using student data from course 1
		StudentData studentInTwoCoursesInCourse1 = dataBundle.students
				.get("student2InCourse1");
		ArrayList<StudentData> listReceivedUsingStudentInCourse1 = apiServlet
				.getStudentsWithId(studentInTwoCoursesInCourse1.id);
		assertEquals(2, listReceivedUsingStudentInCourse1.size());

		// get list using student data from course 2
		StudentData studentInTwoCoursesInCourse2 = dataBundle.students
				.get("student2InCourse2");
		ArrayList<StudentData> listReceivedUsingStudentInCourse2 = apiServlet
				.getStudentsWithId(studentInTwoCoursesInCourse2.id);
		assertEquals(2, listReceivedUsingStudentInCourse2.size());

		// check the content from first list (we assume the content of the
		// second list is similar.

		StudentData firstStudentReceived = listReceivedUsingStudentInCourse1
				.get(0);
		// First student received turned out to be the one from course 2
		assertEquals(studentInTwoCoursesInCourse2.email,
				firstStudentReceived.email);
		assertEquals(studentInTwoCoursesInCourse2.name,
				firstStudentReceived.name);
		assertEquals(studentInTwoCoursesInCourse2.course,
				firstStudentReceived.course);

		// then the second student received must be from course 1
		StudentData secondStudentReceived = listReceivedUsingStudentInCourse1
				.get(1);
		assertEquals(studentInTwoCoursesInCourse1.email,
				secondStudentReceived.email);
		assertEquals(studentInTwoCoursesInCourse1.name,
				secondStudentReceived.name);
		assertEquals(studentInTwoCoursesInCourse1.course,
				secondStudentReceived.course);

		______TS("non existent student");
		assertEquals(null, apiServlet.getStudentsWithId("non-existent"));
	}

	@Test
	public void testEditStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("typical edit");
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);
		String originalEmail = student1InCourse1.email;
		student1InCourse1.name = student1InCourse1.name + "x";
		student1InCourse1.id = student1InCourse1.id + "x";
		student1InCourse1.comments = student1InCourse1.comments + "x";
		student1InCourse1.email = student1InCourse1.email + "x";
		student1InCourse1.team = student1InCourse1.team + "x";
		student1InCourse1.profile = new Text("new profile detail abc ");
		apiServlet.editStudent(originalEmail, student1InCourse1);
		verifyPresentInDatastore(student1InCourse1);

		______TS("check for KeepExistingPolicy");
		// try changing email only
		StudentData copyOfStudent1 = new StudentData();
		copyOfStudent1.course = student1InCourse1.course;
		originalEmail = student1InCourse1.email;

		student1InCourse1.email = student1InCourse1.email + "y";
		copyOfStudent1.email = student1InCourse1.email;

		apiServlet.editStudent(originalEmail, copyOfStudent1);
		verifyPresentInDatastore(student1InCourse1);

		______TS("non-existent student");
		student1InCourse1.course = "new-course";
		verifyAbsentInDatastore(student1InCourse1);
		try {
			apiServlet.editStudent(originalEmail, student1InCourse1);
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("new-course", e.getMessage());
		}

		// no need to check for cascade delete/creates due to LazyCreationPolicy
		// and TolerateOrphansPolicy.
	}

	@Test
	public void testDeleteStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("typical delete");
		// this is the student to be deleted
		StudentData student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		verifyPresentInDatastore(student2InCourse1);

		// ensure student-to-be-deleted has some submissions
		SubmissionData submissionFromS1C1ToS2C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");
		verifyPresentInDatastore(submissionFromS1C1ToS2C1);

		SubmissionData submissionFromS2C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");
		verifyPresentInDatastore(submissionFromS2C1ToS1C1);

		SubmissionData submissionFromS1C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);

		// ensure student-to-be-deleted has some log entries
		verifyPresenceOfTfsLogsForStudent(student2InCourse1.course,
				student2InCourse1.email);

		apiServlet.deleteStudent(student2InCourse1.course,
				student2InCourse1.email);
		verifyAbsentInDatastore(student2InCourse1);

		// verify that other students in the course are intact
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);

		// verify that submissions are deleted
		verifyAbsentInDatastore(submissionFromS1C1ToS2C1);
		verifyAbsentInDatastore(submissionFromS2C1ToS1C1);

		// verify other student's submissions are intact
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);

		// verify that log entries belonging to the student was deleted
		verifyAbsenceOfTfsLogsForStudent(student2InCourse1.course,
				student2InCourse1.email);

		// verify that log entries belonging to another student remain intact
		verifyPresenceOfTfsLogsForStudent(student1InCourse1.course,
				student1InCourse1.email);

		______TS("delete non-existent student");
		// should fail silently.
		apiServlet.deleteStudent(student2InCourse1.course,
				student2InCourse1.email);

		______TS("null parameters");
		// should fail silently.
		apiServlet.deleteStudent(null, student1InCourse1.email);
		apiServlet.deleteStudent(student1InCourse1.course, null);

		// No need to test for cascade delete of TeamProfiles because we follow
		// tolerateOrphansPolicy for TeamProfiles
	}

	@Test
	public void testEnrollStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		String coordId = "coordForEnrollTesting";
		apiServlet.deleteCoord(coordId);
		apiServlet.createCoord(coordId, "Coord for Enroll Testing",
				"coordForEnrollTestin@gmail.com");
		String courseId = "courseForEnrollTest";
		apiServlet.createCourse(coordId, courseId, "Course for Enroll Testing");

		______TS("add student into empty course");
		StudentData student1 = new StudentData("t|n|e@g|c", courseId);

		// check if the course is empty
		assertEquals(0, apiServlet.getStudentListForCourse(courseId).size());

		// add a new student and verify it is added and treated as a new student
		StudentData enrollmentResult = apiServlet.enrollStudent(student1);
		assertEquals(1, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.NEW);
		verifyPresentInDatastore(student1);

		______TS("add existing student");
		// Verify it was not added
		enrollmentResult = apiServlet.enrollStudent(student1);
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.UNMODIFIED);

		______TS("modify info of existing student");
		// verify it was treated as modified
		StudentData student2 = dataBundle.students.get("student1InCourse1");
		student2.name = student2.name + "y";
		StudentData studentToEnroll = new StudentData(student2.email,
				student2.name, student2.comments, student2.course,
				student2.team);
		enrollmentResult = apiServlet.enrollStudent(studentToEnroll);
		verifyEnrollmentResultForStudent(studentToEnroll, enrollmentResult,
				StudentData.UpdateStatus.MODIFIED);
		// check if the student is actually modified in datastore and existing
		// values not specified in enroll action (e.g, id) prevail
		verifyPresentInDatastore(student2);

		______TS("add student into non-empty course");
		StudentData student3 = new StudentData("t3|n3|e3@g|c3", courseId);
		enrollmentResult = apiServlet.enrollStudent(student3);
		assertEquals(2, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student3, enrollmentResult,
				StudentData.UpdateStatus.NEW);

		______TS("add student without team");
		StudentData student4 = new StudentData("|n4|e4@g", courseId);
		enrollmentResult = apiServlet.enrollStudent(student4);
		assertEquals(3, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student4, enrollmentResult,
				StudentData.UpdateStatus.NEW);
	}

	@Test
	public void testSendRegistrationInviteToStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("send to existing student");
		StudentData student1 = dataBundle.students.get("student1InCourse1");
		apiServlet.sendRegistrationInviteToStudent(student1.course,
				student1.email);

		assertEquals(1, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student1);

		// send to another student
		StudentData student2 = dataBundle.students.get("student2InCourse1");
		apiServlet.sendRegistrationInviteToStudent(student2.course,
				student2.email);

		assertEquals(2, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student2);

		______TS("send to non-existing student");
		try {
			apiServlet.sendRegistrationInviteToStudent(student1.course,
					"non@existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non@existent", e.getMessage());
			Common.assertContains(student1.course, e.getMessage());
		}
		assertEquals(2, getNumberOfEmailTasksInQueue());

		______TS("try with null parameters");
		try {
			apiServlet.sendRegistrationInviteToStudent(student1.course, null);
			fail();
		} catch (InvalidParametersException e) {
		}
		try {
			apiServlet.sendRegistrationInviteToStudent(null, student1.email);
			fail();
		} catch (InvalidParametersException e) {
		}
	}

	@Test
	public void testKeyGeneration() {
		long key = 5;
		String longKey = KeyFactory.createKeyString(
				Student.class.getSimpleName(), key);
		long reverseKey = KeyFactory.stringToKey(longKey).getId();
		assertEquals(key, reverseKey);
		assertEquals("Student", KeyFactory.stringToKey(longKey).getKind());
	}

	@Test
	public void testJoinCourse() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("register an unregistered student");

		// make a student 'unregistered'
		StudentData student = dataBundle.students.get("student1InCourse1");
		String googleId = "student1InCourse1";
		long keyLong = Long.parseLong(apiServlet.getKeyForStudent(
				student.course, student.email));
		String key = KeyFactory.createKeyString(Student.class.getSimpleName(),
				keyLong);
		student.id = "";
		apiServlet.editStudent(student.email, student);
		assertEquals("",
				apiServlet.getStudent(student.course, student.email).id);

		helper.setEnvIsLoggedIn(true);
		helper.setEnvEmail(googleId);
		helper.setEnvAuthDomain("gmail.com");

		apiServlet.joinCourse(googleId, key);
		assertEquals(googleId,
				apiServlet.getStudent(student.course, student.email).id);

		______TS("try to register again with a valid key");

		try {
			apiServlet.joinCourse(googleId, key);
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_ALREADY_JOINED, e.errorCode);
		}
		assertEquals(googleId,
				apiServlet.getStudent(student.course, student.email).id);

		______TS("use a valid key belonging to a different user");

		helper.setEnvEmail("student2InCourse1");
		helper.setEnvAuthDomain("gmail.com");
		try {
			apiServlet.joinCourse("student2InCourse1", key);
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER,
					e.errorCode);
		}
		assertEquals(googleId,
				apiServlet.getStudent(student.course, student.email).id);

		______TS("try to register with invalid key");

		// make a student 'unregistered'
		student.id = "";
		apiServlet.editStudent(student.email, student);

		try {
			apiServlet.joinCourse(googleId, "invalidkey");
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_INVALID_KEY, e.errorCode);
		}

		assertEquals("",
				apiServlet.getStudent(student.course, student.email).id);

		______TS("null parameters");

		try {
			apiServlet.joinCourse(googleId, null);
			fail();
		} catch (InvalidParametersException e) {
		}
		try {
			apiServlet.joinCourse(null, null);
			fail();
		} catch (InvalidParametersException e) {
		}

	}

	@Test
	public void testGetKeyForStudent() {
		// mostly tested in testJoinCourse()
		printTestCaseHeader();

		______TS("null parameters");
		StudentData student = dataBundle.students.get("student1InCourse1");
		assertEquals(null, apiServlet.getKeyForStudent(student.course, null));
		assertEquals(null, apiServlet.getKeyForStudent(null, student.email));
		assertEquals(null, apiServlet.getKeyForStudent(null, null));

		______TS("non-existent student");
		assertEquals(null,
				apiServlet.getKeyForStudent(student.course, "non@existent"));
	}

	@Test
	public void testGetCourseListForStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("student having two courses");
		StudentData studentInTwoCourses = dataBundle.students
				.get("student2InCourse1");
		List<CourseData> courseList = apiServlet
				.getCourseListForStudent(studentInTwoCourses.id);
		assertEquals(2, courseList.size());
		CourseData course1 = dataBundle.courses.get("course1OfCoord2");
		assertEquals(course1.id, courseList.get(0).id);
		assertEquals(course1.name, courseList.get(0).name);

		CourseData course2 = dataBundle.courses.get("course1OfCoord1");
		assertEquals(course2.id, courseList.get(1).id);
		assertEquals(course2.name, courseList.get(1).name);

		______TS("student having one course");
		StudentData studentInOneCourse = dataBundle.students
				.get("student1InCourse1");
		courseList = apiServlet.getCourseListForStudent(studentInOneCourse.id);
		assertEquals(1, courseList.size());
		course1 = dataBundle.courses.get("course1OfCoord1");
		assertEquals(course1.id, courseList.get(0).id);
		assertEquals(course1.name, courseList.get(0).name);

		// student having zero courses is not applicable

		______TS("non-existent student");
		try {
			apiServlet.getCourseListForStudent("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}

		______TS("null parameter");
		try {
			apiServlet.getCourseListForStudent(null);
			fail();
		} catch (InvalidParametersException e) {
			Common.assertContains(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}
	}

	@Test
	public void testGetCourseDetailsListForStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("student having multiple evaluations in multiple courses");

		// Let's call this course 1. It has 2 evaluations.
		CourseData expectedCourse1 = dataBundle.courses.get("course1OfCoord1");

		EvaluationData expectedEval1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		EvaluationData expectedEval2InCourse1 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");

		// Let's call this course 2. I has only 1 evaluation.
		CourseData expectedCourse2 = dataBundle.courses.get("course1OfCoord2");

		EvaluationData expectedEval1InCourse2 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord2");

		// This student is in both course 1 and 2
		StudentData studentInTwoCourses = dataBundle.students
				.get("student2InCourse1");

		// Make sure all evaluations in course1 are visible (i.e., not AWAITING)
		expectedEval1InCourse1.startTime = Common
				.getDateOffsetToCurrentTime(-2);
		expectedEval1InCourse1.endTime = Common.getDateOffsetToCurrentTime(-1);
		expectedEval1InCourse1.published = false;
		assertEquals(EvalStatus.CLOSED, expectedEval1InCourse1.getStatus());
		apiServlet.editEvaluation(expectedEval1InCourse1);

		expectedEval2InCourse1.startTime = Common
				.getDateOffsetToCurrentTime(-1);
		expectedEval2InCourse1.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, expectedEval2InCourse1.getStatus());
		apiServlet.editEvaluation(expectedEval2InCourse1);

		// Make sure all evaluations in course2 are still AWAITING
		expectedEval1InCourse2.startTime = Common.getDateOffsetToCurrentTime(1);
		expectedEval1InCourse2.endTime = Common.getDateOffsetToCurrentTime(2);
		assertEquals(EvalStatus.AWAITING, expectedEval1InCourse2.getStatus());
		apiServlet.editEvaluation(expectedEval1InCourse2);

		// Get course details for student
		List<CourseData> courseList = apiServlet
				.getCourseDetailsListForStudent(studentInTwoCourses.id);

		// verify number of courses received
		assertEquals(2, courseList.size());

		// verify details of course 1 (note: index of course 1 is not 0)
		CourseData actualCourse1 = courseList.get(1);
		assertEquals(expectedCourse1.id, actualCourse1.id);
		assertEquals(expectedCourse1.name, actualCourse1.name);
		assertEquals(2, actualCourse1.evaluations.size());

		// verify details of evaluation 1 in course 1
		EvaluationData actualEval1InCourse1 = actualCourse1.evaluations.get(1);
		verifySameEvaluationData(expectedEval1InCourse1, actualEval1InCourse1);

		// verify some details of evaluation 2 in course 1
		EvaluationData actualEval2InCourse1 = actualCourse1.evaluations.get(0);
		verifySameEvaluationData(expectedEval2InCourse1, actualEval2InCourse1);

		// for course 2, verify no evaluations returned (because the evaluation
		// in this course is still AWAITING.
		CourseData actualCourse2 = courseList.get(0);
		assertEquals(expectedCourse2.id, actualCourse2.id);
		assertEquals(expectedCourse2.name, actualCourse2.name);
		assertEquals(0, actualCourse2.evaluations.size());

		______TS("student in a course with no evaluations");

		StudentData studentWithNoEvaluations = dataBundle.students
				.get("student1InCourse2");
		courseList = apiServlet
				.getCourseDetailsListForStudent(studentWithNoEvaluations.id);
		assertEquals(1, courseList.size());
		assertEquals(0, courseList.get(0).evaluations.size());

		// student with 0 courses not applicable

		______TS("non-existent student");

		try {
			apiServlet.getCourseDetailsListForStudent("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent", e.getMessage());
		}

		______TS("null parameter");

		try {
			apiServlet.getCourseDetailsListForStudent(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("google id", e.getMessage().toLowerCase());
		}

	}

	@Test
	public void testGetEvauationResultForStudent() throws Exception {

		printTestCaseHeader();
		refreshDataInDatastore();
		
		______TS("typical case");

		// reconfigure points of an existing evaluation in the datastore
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		//@formatter:off
		setPointsForSubmissions(new int[][] { 
				{ 100, 100, 100, 100 },
				{ 110, 110, NSU, 110 }, 
				{ NSB, NSB, NSB, NSB },
				{ 70, 80, 110, 120 } });
		//@formatter:on

		String student1email = "student1InCourse1@gmail.com";
		// "idOfCourse1OfCoord1", "evaluation1 In Course1",

		EvalResultData result = apiServlet.getEvaluationResultForStudent(
				course.id, evaluation.name, student1email);

		// expected result:
		// [100, 100, 100, 100]
		// [100, 100, NSU, 100]
		// [NSB, NSB, NSB, NSB]
		// [74, 84, 116, 126]
		// =======================
		// [91, 96, 114, 100]
		// =======================
		// [91, 96, 114, 100]
		// [105, 110, 131, 115]
		// [91, 96, 114, 100]
		// [86, 91, 108, 95]

		//check calculated values
		assertEquals(student1email, result.getOwnerEmail());
		assertEquals(100, result.claimedFromStudent);
		assertEquals(100, result.claimedToCoord);
		assertEquals(91, result.perceivedToCoord);
		assertEquals(91, result.perceivedToStudent);
		int teamSize = 4;
		
		//check size of submission lists
		assertEquals(teamSize, result.outgoing.size());
		assertEquals(teamSize, result.incoming.size());
		assertEquals(teamSize, result.selfEvaluations.size());
		
		//check reviewee of incoming
		assertEquals("student1InCourse1@gmail.com", result.outgoing.get(0).reviewee);
		assertEquals("student2InCourse1@gmail.com", result.outgoing.get(1).reviewee);
		assertEquals("student3InCourse1@gmail.com", result.outgoing.get(2).reviewee);
		assertEquals("student4InCourse1@gmail.com", result.outgoing.get(3).reviewee);

		// check sorting of 'incoming' (should be sorted feedback)
		String feedback1 = result.incoming.get(0).p2pFeedback.getValue();
		String feedback2 = result.incoming.get(1).p2pFeedback.getValue();
		String feedback3 = result.incoming.get(2).p2pFeedback.getValue();
		String feedback4 = result.incoming.get(3).p2pFeedback.getValue();
		assertTrue(0 > feedback1
				.compareTo(feedback2));
		assertTrue(0 > feedback2
				.compareTo(feedback3));
		assertTrue(0 >  feedback3
				.compareTo(feedback4));
		
		//check reviewer of outgoing
		assertEquals("student3InCourse1@gmail.com", result.incoming.get(0).reviewer);
		assertEquals("student2InCourse1@gmail.com", result.incoming.get(1).reviewer);
		assertEquals("student4InCourse1@gmail.com", result.incoming.get(2).reviewer);
		assertEquals("student1InCourse1@gmail.com", result.incoming.get(3).reviewer);
		
		//check some random values from submission lists
		assertEquals(100, result.outgoing.get(1).points); //reviewee=student2
		assertEquals(NSB, result.incoming.get(0).points); //reviewer=student3
		assertEquals(114, result.incoming.get(0).normalized); //reviewer=student3
		assertEquals("justification of student1InCourse1 rating to student1InCourse1", 
				result.selfEvaluations.get(0).justification.getValue()); //student2
		
		______TS("null parameter");
		
		try {
			apiServlet.getEvaluationResultForStudent(null,"eval name","e@gmail.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("course id", e.getMessage().toLowerCase());
		}
		
		try {
			apiServlet.getEvaluationResultForStudent("course-id",null,"e@gmail.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("evaluation name", e.getMessage().toLowerCase());
		}
		
		try {
			apiServlet.getEvaluationResultForStudent("course-id","eval name",null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("student email", e.getMessage().toLowerCase());
		}
		
		______TS("non-existent course");
		
		try {
			apiServlet.getEvaluationResultForStudent("non-existent-course",evaluation.name,student1email);
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent-course", e.getMessage().toLowerCase());
		}
		
		______TS("non-existent evaluation");
		
		try {
			apiServlet.getEvaluationResultForStudent(course.id,"non existent eval",student1email);
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non existent eval", e.getMessage().toLowerCase());
		}
		
		______TS("non-existent student");
		
		try {
			apiServlet.getEvaluationResultForStudent(course.id,evaluation.name,"non-existent@email.com");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent@email.com", e.getMessage().toLowerCase());
		}
		
		______TS("student added after evaluation");
		
		//TODO: test this after implementing lazy creation of submissions

	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods_______________________________() {
		
	}

	@Test
	public void testCreateEvaluation() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		______TS("typical case");

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation);
		apiServlet.deleteEvaluation(evaluation.course, evaluation.name);
		verifyAbsentInDatastore(evaluation);
		apiServlet.createEvaluation(evaluation);
		verifyPresentInDatastore(evaluation);

		______TS("Duplicate evaluation name");

		try {
			apiServlet.createEvaluation(evaluation);
			fail();
		} catch (EntityAlreadyExistsException e) {
			assertEquals(Common.MESSAGE_EVALUATION_EXISTS, e.getMessage());
		}

		______TS("invalid parameters");
		try {
			apiServlet.createEvaluation(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		evaluation.name = evaluation.name + "new";
		evaluation.course = null;
		try {
			apiServlet.createEvaluation(evaluation);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			Common.assertContains("course id", e.getMessage().toLowerCase());
		}
		// invalid values to other parameters should be checked in lower level
		// unit tests.

	}

	@Test
	public void testGetEvaluation() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();
		
		dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		// TODO: implement this
	}

	@Test
	public void testEditEvaluation() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		EvaluationData eval1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		eval1.gracePeriod = eval1.gracePeriod + 1;
		eval1.instructions = eval1.instructions + "x";
		eval1.p2pEnabled = (!eval1.p2pEnabled);
		eval1.startTime = Common.getDateOffsetToCurrentTime(1);
		eval1.endTime = Common.getDateOffsetToCurrentTime(2);
		apiServlet.editEvaluation(eval1);
		verifyPresentInDatastore(eval1);

		// TODO: more testing

	}

	@Test
	public void testDeleteEvaluation() {
		// TODO: implement this
	}

	@Test
	public void testPublishAndUnpublishEvaluation() throws Exception {

		printTestCaseHeader();
		refreshDataInDatastore();
		EvaluationData eval1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		assertEquals(false,
				apiServlet.getEvaluation(eval1.course, eval1.name).published);
		apiServlet.publishEvaluation(eval1.course, eval1.name);
		assertEquals(true,
				apiServlet.getEvaluation(eval1.course, eval1.name).published);
		apiServlet.unpublishEvaluation(eval1.course, eval1.name);
		assertEquals(false,
				apiServlet.getEvaluation(eval1.course, eval1.name).published);
		// TODO: more testing
	}

	@Test
	public void testGetEvaluationResult() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// reconfigure points of an existing evaluation in the datastore
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");

		//@formatter:off
		setPointsForSubmissions(new int[][] { 
				{ 100, 100, 100, 100 },
				{ 110, 110, NSU, 110 }, 
				{ NSB, NSB, NSB, NSB },
				{ 70, 80, 110, 120 } });
		//@formatter:on

		EvaluationData result = apiServlet.getEvaluationResult(course.id,
				evaluation.name);

		// no need to sort, the result should be sorted by default

		// check for evaluation details
		assertEquals(evaluation.course, result.course);
		assertEquals(evaluation.name, result.name);
		assertEquals(evaluation.startTime, result.startTime);
		assertEquals(evaluation.endTime, result.endTime);
		assertEquals(evaluation.gracePeriod, result.gracePeriod);
		assertEquals(evaluation.instructions, result.instructions);
		assertEquals(evaluation.timeZone, result.timeZone, 0.1);
		assertEquals(evaluation.p2pEnabled, result.p2pEnabled);
		assertEquals(evaluation.published, result.published);
		assertEquals(Common.UNINITIALIZED_INT, result.submittedTotal);
		assertEquals(Common.UNINITIALIZED_INT, result.expectedTotal);

		// check number of teams and team sizes
		assertEquals(2, result.teams.size());

		// check students in team 1.1
		TeamData team1_1 = result.teams.get(0);
		assertEquals(4, team1_1.students.size());

		int S1_POS = 0;
		int S2_POS = 1;
		int S3_POS = 2;
		int S4_POS = 3;

		StudentData s1 = team1_1.students.get(S1_POS);
		StudentData s2 = team1_1.students.get(S2_POS);
		StudentData s3 = team1_1.students.get(S3_POS);
		StudentData s4 = team1_1.students.get(S4_POS);

		assertEquals("student1InCourse1", s1.id);
		assertEquals("student2InCourse1", s2.id);
		assertEquals("student3InCourse1", s3.id);
		assertEquals("student4InCourse1", s4.id);

		// check self-evaluations of some students
		assertEquals(s1.name, s1.result.getSelfEvaluation().revieweeName);
		assertEquals(s1.name, s1.result.getSelfEvaluation().reviewerName);
		assertEquals(s3.name, s3.result.getSelfEvaluation().revieweeName);
		assertEquals(s3.name, s3.result.getSelfEvaluation().reviewerName);

		// check individual values for s1
		assertEquals(100, s1.result.claimedFromStudent);
		assertEquals(100, s1.result.claimedToCoord);
		assertEquals(91, s1.result.perceivedToStudent);
		assertEquals(91, s1.result.perceivedToCoord);
		// check some more individual values
		assertEquals(110, s2.result.claimedFromStudent);
		assertEquals(NSB, s3.result.claimedToCoord);
		assertEquals(95, s4.result.perceivedToStudent);
		assertEquals(96, s2.result.perceivedToCoord);

		// check outgoing submissions (s1 more intensely than others)

		assertEquals(4, s1.result.outgoing.size());

		SubmissionData s1_s1 = s1.result.outgoing.get(S1_POS);
		assertEquals(100, s1_s1.normalized);
		String expected = "justification of student1InCourse1 rating to student1InCourse1";
		assertEquals(expected, s1_s1.justification.getValue());
		expected = "student1InCourse1 view of team dynamics";
		assertEquals(expected, s1_s1.p2pFeedback.getValue());

		SubmissionData s1_s2 = s1.result.outgoing.get(S2_POS);
		assertEquals(100, s1_s2.normalized);
		expected = "justification of student1InCourse1 rating to student2InCourse1";
		assertEquals(expected, s1_s2.justification.getValue());
		expected = "comments from student1InCourse1 to student2InCourse1";
		assertEquals(expected, s1_s2.p2pFeedback.getValue());

		assertEquals(100, s1.result.outgoing.get(S3_POS).normalized);
		assertEquals(100, s1.result.outgoing.get(S4_POS).normalized);

		assertEquals(NSU, s2.result.outgoing.get(S3_POS).normalized);
		assertEquals(100, s2.result.outgoing.get(S4_POS).normalized);
		assertEquals(NSB, s3.result.outgoing.get(S2_POS).normalized);
		assertEquals(84, s4.result.outgoing.get(S2_POS).normalized);

		// check incoming submissions (s2 more intensely than others)

		assertEquals(4, s1.result.incoming.size());
		assertEquals(91, s1.result.incoming.get(S1_POS).normalized);
		assertEquals(100, s1.result.incoming.get(S4_POS).normalized);

		SubmissionData s2_s1 = s1.result.incoming.get(S2_POS);
		assertEquals(96, s2_s1.normalized);
		expected = "justification of student2InCourse1 rating to student1InCourse1";
		assertEquals(expected, s2_s1.justification.getValue());
		expected = "comments from student2InCourse1 to student1InCourse1";
		assertEquals(expected, s2_s1.p2pFeedback.getValue());
		assertEquals(115, s2.result.incoming.get(S4_POS).normalized);

		SubmissionData s3_s1 = s1.result.incoming.get(S3_POS);
		assertEquals(114, s3_s1.normalized);
		assertEquals("", s3_s1.justification.getValue());
		assertEquals("", s3_s1.p2pFeedback.getValue());
		assertEquals(114, s3.result.incoming.get(S3_POS).normalized);

		assertEquals(108, s4.result.incoming.get(S3_POS).normalized);

		// check team 1.2
		TeamData team1_2 = result.teams.get(1);
		assertEquals(1, team1_2.students.size());
		StudentData team1_2student = team1_2.students.get(0);
		assertEquals(NSB, team1_2student.result.claimedFromStudent);
		assertEquals(1, team1_2student.result.outgoing.size());
		assertEquals(NSB, team1_2student.result.claimedToCoord);
		assertEquals(NSB, team1_2student.result.outgoing.get(0).points);
		assertEquals(NA, team1_2student.result.incoming.get(0).normalized);

		// try with null parameters
		assertEquals(null,
				apiServlet.getEvaluationResult(null, evaluation.name));
		assertEquals(null, apiServlet.getEvaluationResult(course.id, null));

		// try for non-existent courses/evaluations
		try {
			apiServlet
					.getEvaluationResult(course.id, "non existent evaluation");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non existent evaluation", e.getMessage());
		}

		try {
			apiServlet.getEvaluationResult("non-existent-course", "any name");
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("non-existent-course", e.getMessage());
		}

		// TODO: reduce rounding off error during
		// "self rating removed and normalized"

	}

	@Test
	public void testCalculateTeamResult() throws Exception {
		printTestCaseHeader();

		assertEquals(null, invokeCalclulateTeamResult(null));

		TeamData team = new TeamData();
		StudentData s1 = new StudentData("t1|s1|e1@c", "course1");
		s1.result = new EvalResultData();
		StudentData s2 = new StudentData("t1|s2|e2@c", "course1");
		s2.result = new EvalResultData();
		StudentData s3 = new StudentData("t1|s3|e3@c", "course1");
		s3.result = new EvalResultData();

		SubmissionData s1_to_s1 = createSubmission(1, 1);
		SubmissionData s1_to_s2 = createSubmission(1, 2);
		SubmissionData s1_to_s3 = createSubmission(1, 3);

		SubmissionData s2_to_s1 = createSubmission(2, 1);
		SubmissionData s2_to_s2 = createSubmission(2, 2);
		SubmissionData s2_to_s3 = createSubmission(2, 3);

		SubmissionData s3_to_s1 = createSubmission(3, 1);
		SubmissionData s3_to_s2 = createSubmission(3, 2);
		SubmissionData s3_to_s3 = createSubmission(3, 3);

		// These additions are randomly ordered to ensure that the
		// method works even when submissions are added in random order

		s1.result.outgoing.add(s1_to_s2.getCopy());
		s1.result.incoming.add(s2_to_s1.getCopy());
		s1.result.incoming.add(s3_to_s1.getCopy());
		s3.result.outgoing.add(s3_to_s3.getCopy());
		s2.result.outgoing.add(s2_to_s1.getCopy());
		s1.result.outgoing.add(s1_to_s3.getCopy());
		s2.result.incoming.add(s3_to_s2.getCopy());
		s2.result.outgoing.add(s2_to_s3.getCopy());
		s3.result.outgoing.add(s3_to_s1.getCopy());
		s2.result.incoming.add(s2_to_s2.getCopy());
		s3.result.incoming.add(s1_to_s3.getCopy());
		s1.result.outgoing.add(s1_to_s1.getCopy());
		s3.result.incoming.add(s2_to_s3.getCopy());
		s3.result.outgoing.add(s3_to_s2.getCopy());
		s2.result.incoming.add(s1_to_s2.getCopy());
		s1.result.incoming.add(s1_to_s1.getCopy());
		s2.result.outgoing.add(s2_to_s2.getCopy());
		s3.result.incoming.add(s3_to_s3.getCopy());

		team.students.add(s2);
		team.students.add(s1);
		team.students.add(s3);

		TeamEvalResult teamResult = invokeCalclulateTeamResult(team);
		// note the pattern in numbers. due to the way we generate submissions,
		// 110 means it is from s1 to s1 and
		// should appear in the 1,1 location in the matrix.
		int[][] expected = { { 110, 120, 130 }, { 210, 220, 230 },
				{ 310, 320, 330 } };
		assertEquals(TeamEvalResult.pointsToString(expected),
				TeamEvalResult.pointsToString(teamResult.claimedToStudents));

		// expected result
		// claimedToCoord [ 92, 100, 108]
		// [ 95, 100, 105]
		// [ 97, 100, 103]
		// ===============
		// perceivedToCoord [ 97, 99, 105]
		// ===============
		// perceivedToStudents [116, 118, 126]
		// [213, 217, 230]
		// [309, 316, 335]

		int S1_POS = 0;
		int S2_POS = 1;
		int S3_POS = 2;

		// verify incoming and outgoing do not refer to same copy of submissions
		s1.result.sortIncomingByStudentNameAscending();
		s1.result.sortOutgoingByStudentNameAscending();
		s1.result.incoming.get(S1_POS).normalized = 0;
		s1.result.outgoing.get(S1_POS).normalized = 1;
		assertEquals(0, s1.result.incoming.get(S1_POS).normalized);
		assertEquals(1, s1.result.outgoing.get(S1_POS).normalized);

		invokePopulateTeamResult(team, teamResult);

		s1 = team.students.get(S1_POS);
		assertEquals(110, s1.result.claimedFromStudent);
		assertEquals(92, s1.result.claimedToCoord);
		assertEquals(116, s1.result.perceivedToStudent);
		assertEquals(97, s1.result.perceivedToCoord);
		assertEquals(92, s1.result.outgoing.get(S1_POS).normalized);
		assertEquals(100, s1.result.outgoing.get(S2_POS).normalized);
		assertEquals(108, s1.result.outgoing.get(S3_POS).normalized);
		assertEquals(s1.name, s1.result.incoming.get(S1_POS).revieweeName);
		assertEquals(s1.name, s1.result.incoming.get(S1_POS).reviewerName);
		assertEquals(116, s1.result.incoming.get(S1_POS).normalized);
		assertEquals(118, s1.result.incoming.get(S2_POS).normalized);
		assertEquals(126, s1.result.incoming.get(S3_POS).normalized);

		s2 = team.students.get(S2_POS);
		assertEquals(220, s2.result.claimedFromStudent);
		assertEquals(100, s2.result.claimedToCoord);
		assertEquals(217, s2.result.perceivedToStudent);
		assertEquals(99, s2.result.perceivedToCoord);
		assertEquals(95, s2.result.outgoing.get(S1_POS).normalized);
		assertEquals(100, s2.result.outgoing.get(S2_POS).normalized);
		assertEquals(105, s2.result.outgoing.get(S3_POS).normalized);
		assertEquals(213, s2.result.incoming.get(S1_POS).normalized);
		assertEquals(217, s2.result.incoming.get(S2_POS).normalized);
		assertEquals(230, s2.result.incoming.get(S3_POS).normalized);

		s3 = team.students.get(S3_POS);
		assertEquals(330, s3.result.claimedFromStudent);
		assertEquals(103, s3.result.claimedToCoord);
		assertEquals(335, s3.result.perceivedToStudent);
		assertEquals(105, s3.result.perceivedToCoord);
		assertEquals(97, s3.result.outgoing.get(S1_POS).normalized);
		assertEquals(100, s3.result.outgoing.get(S2_POS).normalized);
		assertEquals(103, s3.result.outgoing.get(S3_POS).normalized);
		assertEquals(309, s3.result.incoming.get(S1_POS).normalized);
		assertEquals(316, s3.result.incoming.get(S2_POS).normalized);
		assertEquals(335, s3.result.incoming.get(S3_POS).normalized);

	}

	@Test
	public void testPopulateResults() {
		// tested in testCalculateTeamResult()
	}

	@Test
	public void testGetSubmissoinsForEvaluation() {
		// TODO: test this
	}

	@Test
	public void testGetSubmissionsFromStudent() {
		// TODO: test this
	}

	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods_______________________________() {
	}

	@Test
	public void testCreateSubmission() {
		// method not implemented
	}

	@Test
	public void testGetSubmission() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		SubmissionData submissionData = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionData);
		// TODO: more testing
		// e.g., lazyCreation
	}

	@Test
	public void testEditSubmission() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		ArrayList<SubmissionData> submissionContainer = new ArrayList<SubmissionData>();

		// try without empty list. Nothing should happen
		apiServlet.editSubmissions(submissionContainer);

		SubmissionData sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		SubmissionData sub2 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");

		// checking editing of one of the submissions
		alterSubmission(sub1);

		submissionContainer.add(sub1);
		apiServlet.editSubmissions(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		// check editing both submissions
		alterSubmission(sub1);
		alterSubmission(sub2);

		submissionContainer = new ArrayList<SubmissionData>();
		submissionContainer.add(sub1);
		submissionContainer.add(sub2);
		apiServlet.editSubmissions(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		// TODO: more testing
		// check for lazyCreationPolicy

	}

	@Test
	public void testDeleteSubmission() {
		// method not implemented
	}

	@SuppressWarnings("unused")
	private void ____TFS_level_methods______________________________________() {
	}

	@Test
	public void testCreateTfs() {
		// TODO: implement this
	}

	@Test
	public void testGetTfs() {
		// TODO: implement this
		// ensure LazyCreation
	}

	@Test
	public void testEditTfs() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		TfsData tfs1 = dataBundle.teamFormingSessions.get("tfsInCourse1");
		tfs1.gracePeriod = tfs1.gracePeriod + 1;
		tfs1.instructions = tfs1.instructions + "x";
		tfs1.profileTemplate = tfs1.profileTemplate + "y";
		tfs1.startTime = Common.getDateOffsetToCurrentTime(1);
		tfs1.endTime = Common.getDateOffsetToCurrentTime(2);
		apiServlet.editTfs(tfs1);
		verifyPresentInDatastore(tfs1);

		// TODO: more testing
	}

	@Test
	public void testDeleteTfs() {
		// TODO: implement this
	}

	@Test
	public void testRenameTeam() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		String originalTeamName = student1InCourse1.team;
		String newTeamName = originalTeamName + "x";
		String courseID = student1InCourse1.course;
		verifyTeamNameChange(courseID, originalTeamName, newTeamName);

		refreshDataInDatastore();
		originalTeamName = student1InCourse1.team;
		courseID = student1InCourse1.course;
		verifyTeamNameChange(courseID, "nonExisentTeam", "newTeamName");

		// TODO: more testing

	}

	@SuppressWarnings("unused")
	private void ____TEAM_PROFILE_level_methods_____________________________() {
	}

	@Test
	public void testCreateTeamProfile() {
		// TODO: implement this
	}

	@Test
	public void testGetTeamProfile() {
		// TODO: implement this
	}

	@Test
	public void testEditTeamProfile() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		TeamProfileData teamProfile1 = dataBundle.teamProfiles
				.get("profileOfTeam1.1");
		String originalTeamName = teamProfile1.team;
		teamProfile1.team = teamProfile1.team + "new";
		teamProfile1.profile = new Text(teamProfile1.profile.getValue() + "x");
		apiServlet.editTeamProfile(originalTeamName, teamProfile1);
		verifyPresentInDatastore(teamProfile1);

	}

	@Test
	public void testDeleteTeamProfile() {
		// TODO: implement this
	}

	@SuppressWarnings("unused")
	private void ____TEAM_FORMING_LOG_level_methods_________________________() {
	}

	@Test
	public void testCreateTeamFormingLogEntry() {
		// TODO: implement this
	}

	@Test
	public void testGetTeamFormingLogEntry() {
		// TODO: implement this
	}

	@Test
	public void testEditTeamFormingLogEntry() {
		// method not implemented
	}

	@Test
	public void testDeleteTeamFormingLogEntry() {
		// TODO: implement this
	}

	@SuppressWarnings("unused")
	private void ____helper_methods_________________________________________() {
	}

	private void verifyEvaluationInfoExistsInList(EvaluationData evaluation,
			ArrayList<EvaluationData> evalInfoList) {

		for (EvaluationData ed : evalInfoList) {
			if (ed.name.equals(evaluation.name))
				return;
		}
		fail("Did not find " + evaluation.name + " in the evaluation info list");
	}

	private void verifyEnrollmentResultForStudent(StudentData expectedStudent,
			StudentData enrollmentResult, StudentData.UpdateStatus status) {
		String errorMessage = "mismatch! \n expected:\n"
				+ Common.getTeammatesGson().toJson(expectedStudent)
				+ "\n actual \n"
				+ Common.getTeammatesGson().toJson(enrollmentResult);
		expectedStudent.updateStatus = status;
		assertEquals(errorMessage, true,
				enrollmentResult.isEnrollmentInfoMatchingTo(expectedStudent));
	}

	private void verifyRegistrationEmailToStudent(StudentData student) {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get("email-queue");

		List<TaskStateInfo> taskInfoList = qsi.getTaskInfo();
		for (TaskStateInfo tsi : taskInfoList) {
			String emailTaskBody = tsi.getBody();
			if (emailTaskBody.contains("email="
					+ student.email.replace("@", "%40"))
					&& emailTaskBody.contains("courseid=" + student.course)) {
				return;
			}
		}
		fail();
	}

	private int getNumberOfEmailTasksInQueue() {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get("email-queue");
		return qsi.getTaskInfo().size();
	}

	private void verifyTeamNameChange(String courseID, String originalTeamName,
			String newTeamName) throws InvalidParametersException,
			EntityDoesNotExistException {
		List<StudentData> studentsInClass = apiServlet
				.getStudentListForCourse(courseID);
		List<StudentData> studentsInTeam = new ArrayList<StudentData>();
		List<StudentData> studentsNotInTeam = new ArrayList<StudentData>();
		for (StudentData s : studentsInClass) {
			if (s.team.equals(originalTeamName)) {
				studentsInTeam.add(s);
			} else {
				studentsNotInTeam.add(s);
			}
		}
		apiServlet.renameTeam(courseID, originalTeamName, newTeamName);
		for (StudentData s : studentsInTeam) {
			assertEquals(newTeamName,
					apiServlet.getStudent(s.course, s.email).team);
		}
		for (StudentData s : studentsNotInTeam) {
			String teamName = apiServlet.getStudent(s.course, s.email).team;
			assertTrue("unexpected team name: " + teamName,
					!teamName.equals(newTeamName));
		}
		// TODO: check for changes in team profile
	}

	private void verifyTfsListForCoord(String coordId, int noOfTfs)
			throws EntityDoesNotExistException {
		List<TfsData> tfsList = apiServlet.getTfsListForCoord(coordId);
		assertEquals(noOfTfs, tfsList.size());
		for (TfsData tfs : tfsList) {
			assertEquals(coordId, apiServlet.getCourse(tfs.course).coord);
		}
	}

	private void alterSubmission(SubmissionData submission) {
		submission.points = submission.points + 10;
		submission.p2pFeedback = new Text(submission.p2pFeedback.getValue()
				+ "x");
		submission.justification = new Text(submission.justification.getValue()
				+ "y");
	}

	private void verifyPresentInDatastore(String dataBundleJsonString)
			throws EntityDoesNotExistException {

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
		for (TfsData expectedTeamFormingSession : teamFormingSessions.values()) {
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

	private void verifyAbsentInDatastore(SubmissionData submission) {
		assertEquals(null,
				apiServlet.getSubmission(submission.course,
						submission.evaluation, submission.reviewer,
						submission.reviewee));
	}

	private void verifyAbsentInDatastore(CoordData expectedCoord) {
		assertEquals(null, apiServlet.getCoord(expectedCoord.id));
	}

	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals(null, apiServlet.getCourse(course.id));
	}

	private void verifyAbsentInDatastore(StudentData student) {
		assertEquals(null, apiServlet.getStudent(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationData evaluation) {
		assertEquals(null,
				apiServlet.getEvaluation(evaluation.course, evaluation.name));
	}

	private void verifyAbsentInDatastore(TfsData tfs) {
		assertEquals(null, apiServlet.getTfs(tfs.course));
	}

	private void verifyAbsentInDatastore(TeamProfileData profile) {
		assertEquals(null,
				apiServlet.getTeamProfile(profile.course, profile.team));
	}

	private void verifyAbsenceOfTfsLogsForStudent(String courseId,
			String studentEmail) throws EntityDoesNotExistException {
		List<StudentActionData> teamFormingLogs = apiServlet
				.getStudentActions(courseId);
		for (StudentActionData tfl : teamFormingLogs) {
			String actualEmail = tfl.email;
			assertTrue("unexpected email:" + actualEmail,
					!actualEmail.equals(studentEmail));
		}

	}

	private void verifyPresenceOfTfsLogsForStudent(String courseId,
			String studentEmail) throws EntityDoesNotExistException {
		List<StudentActionData> teamFormingLogs = apiServlet
				.getStudentActions(courseId);
		for (StudentActionData tfl : teamFormingLogs) {
			if (tfl.email.equals(studentEmail))
				return;
		}
		fail("No log messages found for " + studentEmail + " in " + courseId);
	}

	private void verifyPresentInDatastore(StudentData expectedStudent) {
		StudentData actualStudent = apiServlet.getStudent(
				expectedStudent.course, expectedStudent.email);
		expectedStudent.updateStatus = UpdateStatus.UNKNOWN;
		// TODO: this is for backward compatibility with old system. to be
		// removed.
		if ((expectedStudent.id == null) && (actualStudent.id.equals(""))) {
			actualStudent.id = null;
		}
		if ((expectedStudent.team == null) && (actualStudent.team.equals(""))) {
			actualStudent.team = null;
		}
		if ((expectedStudent.comments == null)
				&& (actualStudent.comments.equals(""))) {
			actualStudent.comments = null;
		}
		assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(SubmissionData expected) {
		SubmissionData actual = apiServlet.getSubmission(expected.course,
				expected.evaluation, expected.reviewer, expected.reviewee);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(StudentActionData expected)
			throws EntityDoesNotExistException {
		List<StudentActionData> actualList = apiServlet
				.getStudentActions(expected.course);
		assertTrue(isLogEntryInList(expected, actualList));
	}

	private void verifyPresentInDatastore(TeamProfileData expected) {
		TeamProfileData actual = apiServlet.getTeamProfile(expected.course,
				expected.team);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(TfsData expected) {
		TfsData actual = apiServlet.getTfs(expected.course);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(EvaluationData expected) {
		EvaluationData actual = apiServlet.getEvaluation(expected.course,
				expected.name);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(CourseData expected) {
		CourseData actual = apiServlet.getCourse(expected.id);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(CoordData expected) {
		CoordData actual = apiServlet.getCoord(expected.id);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifySameEvaluationData(EvaluationData expected,
			EvaluationData actual) {
		assertEquals(expected.course, actual.course);
		assertEquals(expected.name, actual.name);
		assertSameDates(expected.startTime, actual.startTime);
		assertSameDates(expected.endTime, actual.endTime);
		assertEquals(expected.timeZone, actual.timeZone, 0.1);
	}

	private void refreshDataInDatastore() throws Exception {
		setGeneralLoggingLevel(Level.SEVERE);
		setLogLevelOfClass(APIServlet.class, Level.SEVERE);
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			apiServlet.deleteCoord(coord.id);
		}
		DataBundle data = Common.getTeammatesGson().fromJson(jsonString,
				DataBundle.class);
		apiServlet.persistNewDataBundle(data);
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(APIServlet.class, Level.FINE);
	}

	private boolean isLogEntryInList(StudentActionData teamFormingLogEntry,
			List<StudentActionData> teamFormingLogEntryList) {
		for (StudentActionData logEntryInList : teamFormingLogEntryList) {
			if (teamFormingLogEntry.course.equals(logEntryInList.course)
					&& teamFormingLogEntry.action.getValue().equals(
							logEntryInList.action.getValue())
					&& teamFormingLogEntry.email.equals(logEntryInList.email)
					&& teamFormingLogEntry.name.equals(logEntryInList.name)
					&& teamFormingLogEntry.time.toString().equals(
							logEntryInList.time.toString())) {
				return true;
			}
		}
		return false;
	}

	private TeamEvalResult invokeCalclulateTeamResult(TeamData team)
			throws Exception {
		Method privateMethod = APIServlet.class.getDeclaredMethod(
				"calculateTeamResult", new Class[] { TeamData.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { team };
		return (TeamEvalResult) privateMethod.invoke(apiServlet, params);
	}

	// TODO: try to generalize invoke*() methods and push to parent class
	private void invokePopulateTeamResult(TeamData team,
			TeamEvalResult teamResult) throws Exception {
		Method privateMethod = APIServlet.class.getDeclaredMethod(
				"populateTeamResult", new Class[] { TeamData.class,
						TeamEvalResult.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { team, teamResult };
		privateMethod.invoke(apiServlet, params);
	}

	private SubmissionData createSubmission(int from, int to) {
		SubmissionData submission = new SubmissionData();
		submission.course = "course1";
		submission.evaluation = "eval1";
		submission.points = from * 100 + to * 10;
		submission.reviewer = "e" + from + "@c";
		submission.reviewerName = "s" + from;
		submission.reviewee = "e" + to + "@c";
		submission.revieweeName = "s" + to;
		return submission;
	}

	private void setPointsForSubmissions(int[][] points) {
		int teamSize = points.length;
		ArrayList<SubmissionData> submissions = new ArrayList<SubmissionData>();
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				SubmissionData s = apiServlet.getSubmission(
						"idOfCourse1OfCoord1", "evaluation1 In Course1",
						"student" + (i + 1) + "InCourse1@gmail.com", "student"
								+ (j + 1) + "InCourse1@gmail.com");
				s.points = points[i][j];
				submissions.add(s);
			}
		}
		apiServlet.editSubmissions(submissions);
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter("CoordCourseAddApiTest");
		setLogLevelOfClass(APIServlet.class, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}

	@After
	public void caseTearDown() {
		apiServlet.destroy();
		helper.tearDown();
	}

}
