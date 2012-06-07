package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Datastore;
import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.api.EnrollException;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.*;
import teammates.datatransfer.StudentData.UpdateStatus;

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
	static{
		try{
			jsonString = Common.readFile(Common.TEST_DATA_FOLDER + "typicalDataBundle.json");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private DataBundle dataBundle;

	private static String queueXmlFilePath = System.getProperty("user.dir")
			+ File.separator + "src" + File.separator + "main" + File.separator
			+ "webapp" + File.separator + "WEB-INF" + File.separator
			+ "queue.xml";

	@BeforeClass
	public static void classSetUp() {
		printTestClassHeader();
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
				new LocalMailServiceTestConfig(),localUserServiceTestConfig, ltqtc);

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
		
		//try with null
		DataBundle nullDataBundle = null;
		try {
			apiServlet.persistNewDataBundle(nullDataBundle);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}
		
		//try with invalid parameters in an entity
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
		//   should be checked at lower level methods
	}
	
	@Test
	public void testGetLoggedInUser() throws Exception{
		printTestCaseHeader();
		refreshDataInDatastore();
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		//also make this user a student
		StudentData coordAsStudent = new StudentData("|Coord As Student|coordasstudent@yahoo.com|", "some-course");
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
		
		//this user is no longer a student
		apiServlet.deleteStudent(coordAsStudent.course, coordAsStudent.email);
		//this user is no longer an admin
		helper.setEnvIsAdmin(false);
		
		user = apiServlet.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(false, user.isStudent);

		//check for unregistered student
		helper.setEnvEmail("unknown");
		helper.setEnvAuthDomain("gmail.com");
		user = apiServlet.getLoggedInUser();
		assertEquals("unknown", user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(false, user.isStudent);
		
		//check for user who is only a student
		StudentData student = dataBundle.students.get("student1InCourse1");
		helper.setEnvEmail(student.id);
		helper.setEnvAuthDomain("gmail.com");
		user = apiServlet.getLoggedInUser();
		assertEquals(student.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(true, user.isStudent);
		
		//check for user not logged in
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
		//create a course to check cascade delete later
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

		// TODO: check for exception when coord does not exist
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
		verifyEvauationInfoExistsInList(
				dataBundle.evaluations.get("evaluation1InCourse1OfCoord1"),
				course1Evals);
		verifyEvauationInfoExistsInList(
				dataBundle.evaluations.get("evaluation2InCourse1OfCoord1"),
				course1Evals);

		// course with 1 evaluation
		assertEquals(course1Id, course1Evals.get(1).course);
		ArrayList<EvaluationData> course2Evals = courseListForCoord
				.get("idOfCourse2OfCoord1").evaluations;
		assertEquals(1, course2Evals.size());
		verifyEvauationInfoExistsInList(
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

		// non existent coord
		courseListForCoord = apiServlet
				.getCourseDetailsListForCoord("nonexistentcoord");
		assertEquals(0, courseListForCoord.size());

		// null parameters
		assertEquals(null, apiServlet.getCourseDetailsListForCoord(null));

		// TODO: change to above to expect exception when coord does not exist

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

		// non-existent coord
		evalList = apiServlet.getEvaluationsListForCoord("nonExistentCoord");
		assertEquals(0, evalList.size());

		// null parameter
		assertEquals(null, apiServlet.getEvaluationsListForCoord(null));

		// TODO: change to above to expect exception when coord does not exist
	}

	@Test
	public void testTfsListForCoord() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		// coord with 2 Tfs
		verifyTfsListForCoord(dataBundle.coords.get("typicalCoord2").id, 2);
		// coord with 0 Tfs
		verifyTfsListForCoord("typicalCoord3", 0);

		// null parameters
		assertEquals(null, apiServlet.getTfsListForCoord(null));

		// FIXME: check for non-existent coord
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

		apiServlet.deleteCourse(course1OfCoord.id);

		// ensure the course and related entities are deleted
		verifyAbsentInDatastore(course1OfCoord);
		assertEquals(0, apiServlet.getStudentListForCourse(course1OfCoord.id)
				.size());
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
		assertEquals(3, studentList.size());
		for (StudentData s : studentList) {
			assertEquals(course1OfCoord1.id, s.course);
		}

		// course with 0 students
		CourseData course2OfCoord1 = dataBundle.courses.get("course2OfCoord1");
		studentList = apiServlet.getStudentListForCourse(course2OfCoord1.id);
		assertEquals(0, studentList.size());

		assertEquals(null, apiServlet.getStudentListForCourse(null));

		// TODO: test for non-existent course
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
		
		
		//TODO: check for duplicate students in enroll lines
		
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
	public void testGetTeamsForCourse() throws Exception{
		printTestCaseHeader();
		refreshDataInDatastore();
		
		//testing for typical course
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		apiServlet.createStudent(new StudentData("|s1|s1@e|", course.id));
		apiServlet.createStudent(new StudentData("|s2|s2@e|", course.id));
		CourseData courseAsTeams = apiServlet.getTeamsForCourse(course.id);
		assertEquals(2, courseAsTeams.teams.size());
		
		String team1Id = "Team 1.1";
		assertEquals(team1Id, courseAsTeams.teams.get(0).name);
		assertEquals(team1Id, courseAsTeams.teams.get(0).profile.team);
		assertEquals(2, courseAsTeams.teams.get(0).students.size());
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
		
		//try again without the loners
		refreshDataInDatastore();
		courseAsTeams = apiServlet.getTeamsForCourse(course.id);
		assertEquals(2, courseAsTeams.teams.get(0).students.size());
		assertEquals(0, courseAsTeams.loners.size());
		
		assertEquals(null, apiServlet.getTeamsForCourse(null));
		
		//course without teams
		apiServlet.createCourse("coord1", "course1", "Course 1");
		assertEquals(0, apiServlet.getTeamsForCourse("course1").teams.size());
		
		//non-existent course
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
	public void testGetStudentWithId(){
		//TODO: implement this
	}

	@Test
	public void testEditStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

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
		
		//non-existent student
		student1InCourse1.course = "new-course";
		verifyAbsentInDatastore(student1InCourse1);
		try {
			apiServlet.editStudent(originalEmail, student1InCourse1);
			fail();
		} catch (EntityDoesNotExistException e) {
			Common.assertContains("new-course", e.getMessage());
		}

		// ensure a team profile is created when moving to a new one
		StudentData student2 = dataBundle.students.get("student3InCourse1");
		TeamProfileData teamProfileOfStudent2 = dataBundle.teamProfiles
				.get("profileOfTeam2.1");
		verifyPresentInDatastore(teamProfileOfStudent2);
		student2.team = "newTeam";
		TeamProfileData profileOfNewTeam = new TeamProfileData(student2.course, 
				"newTeam", new Text(""));
		verifyAbsentInDatastore(profileOfNewTeam);
		apiServlet.editStudent(student2.email, student2);
		verifyPresentInDatastore(profileOfNewTeam);

		// TODO: make sure team profiles are deleted if this is the last student
		// in that team
		// TODO: check for KeepExistingPolicy

	}

	@Test
	public void testDeleteStudent() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		SubmissionData submissionFromS1C1ToS2C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");
		verifyPresentInDatastore(submissionFromS1C1ToS2C1);
		SubmissionData submissionFromS2C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");
		verifyPresentInDatastore(submissionFromS2C1ToS1C1);
		SubmissionData submissionFromS1C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);

		StudentData student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		verifyPresentInDatastore(student2InCourse1);

		// verify that the student-to-be-deleted has some log entries
		verifyPresenceOfTfsLogsForStudent(student2InCourse1.course,
				student2InCourse1.email);

		apiServlet.deleteStudent(student2InCourse1.course,
				student2InCourse1.email);
		verifyAbsentInDatastore(student2InCourse1);

		// verify that other students in the course are intact
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);

		// try to delete the student again. should succeed.
		apiServlet.deleteStudent(student2InCourse1.course,
				student2InCourse1.email);

		verifyAbsentInDatastore(submissionFromS1C1ToS2C1);
		verifyAbsentInDatastore(submissionFromS2C1ToS1C1);
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);

		// verify that log entries belonging to the student was deleted
		verifyAbsenceOfTfsLogsForStudent(student2InCourse1.course,
				student2InCourse1.email);
		// verify that log entries belonging to another student remain intact
		verifyPresenceOfTfsLogsForStudent(student1InCourse1.course,
				student1InCourse1.email);

		// TODO: more testing, e.g. test for cascade delete of profiles,
		// submissions etc.
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

		StudentData student1 = new StudentData("t|n|e@g|c", courseId);

		// check if the course is empty
		assertEquals(0, apiServlet.getStudentListForCourse(courseId).size());

		// add a new student and verify it is added and treated as a new student
		StudentData enrollmentResult = apiServlet.enrollStudent(student1);
		assertEquals(1, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.NEW);
		verifyPresentInDatastore(student1);
		
		// add the same student. Verify it was not added
		enrollmentResult = apiServlet.enrollStudent(student1);
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.UNMODIFIED);

		// modify info of same student and verify it was treated as modified
		StudentData student2 = dataBundle.students.get("student1InCourse1");
		student2.name = student2.name+"y";
		StudentData studentToEnroll = new StudentData(student2.email,student2.name, student2.comments, student2.course, student2.team);
		enrollmentResult = apiServlet.enrollStudent(studentToEnroll);
		verifyEnrollmentResultForStudent(studentToEnroll, enrollmentResult,
				StudentData.UpdateStatus.MODIFIED);
		//check if the student is actually modified in datastore and existing 
		// values not specified in enroll action (e.g, id) prevail 
		verifyPresentInDatastore(student2);

		// add a new student to non-empty course
		StudentData student3 = new StudentData("t3|n3|e3@g|c3", courseId);
		enrollmentResult = apiServlet.enrollStudent(student3);
		assertEquals(2, apiServlet.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student3, enrollmentResult,
				StudentData.UpdateStatus.NEW);
		
		// student without team
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
		StudentData student1 = dataBundle.students.get("student1InCourse1");
		apiServlet.sendRegistrationInviteToStudent(student1.course,
				student1.email);

		assertEquals(1, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student1);
		// TODO: more testing
	}
	
	@Test
	public void testRegisterForCourse(){
		//TODO: implement this
		//input: key, course, email, googleId
		//output: status
		//throws: InvalidParametersException, EntityDoesNotExist
	}
	
	@Test
	public void testGetCourseListForStudent(){
		//TODO: implement this
		//input: googleId
		//output: List<CourseData>, including List<EvaluationData>
		//throws: InvalidParametersException, EntityDoesNotExist (if student does not exist)
	}
	
	@Test
	public void testGetEvauationResultForStudent(){
		//TODO: implement this
		//input: course, email,evalName
		//output: EvaluationResult
		//throws: InvalidParametersException, EntityDoesNotExist (if eval or student does not exist)
	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods_______________________________() {
	}

	@Test
	public void testCreateEvaluation() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation);
		apiServlet.deleteEvaluation(evaluation.course, evaluation.name);
		verifyAbsentInDatastore(evaluation);
		apiServlet.createEvalution(evaluation);
		verifyPresentInDatastore(evaluation);
		// TODO: more testing

	}

	@Test
	public void testGetEvaluation() {
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
		
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		EvaluationData result = apiServlet.getEvaluationResult(course.id, evaluation.name);
		
		assertEquals(2, result.teams.size());
		TeamData team1_1 = result.teams.get(0);
		assertEquals(2, team1_1.students.size());
		StudentData student2InCourse1 = team1_1.students.get(0);
		assertEquals("student2InCourse1", student2InCourse1.id);
		StudentData student1InCourse1 = team1_1.students.get(1);
		assertEquals("student1InCourse1", student1InCourse1.id);
		assertTrue(student1InCourse1.result.own != null);
		assertEquals(student1InCourse1.name, student1InCourse1.result.own.revieweeName);
		assertEquals(student1InCourse1.name, student1InCourse1.result.own.reviewerName);
		
		assertEquals(1, student1InCourse1.result.incoming.size());
		assertEquals(1, student1InCourse1.result.outgoing.size());
		assertTrue(student1InCourse1.result.claimedActual != Common.UNINITIALIZED_INT);
		assertTrue(student1InCourse1.result.claimedToCoord != Common.UNINITIALIZED_INT);
		assertTrue(student1InCourse1.result.claimedToStudent != Common.UNINITIALIZED_INT);
		assertTrue(student1InCourse1.result.perceivedToCoord != Common.UNINITIALIZED_INT);
		assertTrue(student1InCourse1.result.perceivedToStudent != Common.UNINITIALIZED_INT);
		
		assertTrue(student1InCourse1.result.incoming.get(0).normalized != Common.UNINITIALIZED_INT);
		assertEquals(student1InCourse1.name, student1InCourse1.result.incoming.get(0).revieweeName);
		assertEquals(student2InCourse1.name, student1InCourse1.result.incoming.get(0).reviewerName);
		
		assertTrue(student1InCourse1.result.outgoing.get(0).normalized != Common.UNINITIALIZED_INT);
		assertEquals(student2InCourse1.name, student1InCourse1.result.outgoing.get(0).revieweeName);
		assertEquals(student1InCourse1.name, student1InCourse1.result.outgoing.get(0).reviewerName);
		// TODO: more testing
	}
	
	@Test
	public void testGetSubmissoinsForEvaluation(){
		//TODO: test this
	}
	
	@Test
	public void testGetSubmissionsFromStudent(){
		//TODO: test this
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
		
		SubmissionData submissionData = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionData);
		// TODO: more testing
	}

	@Test
	public void testEditSubmission() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		ArrayList<SubmissionData> submissionContainer = new ArrayList<SubmissionData>();

		// try without empty list. Nothing should happen
		apiServlet.editSubmission(submissionContainer);

		SubmissionData sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		SubmissionData sub2 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");

		// checking editing of one of the submissions
		alterSubmission(sub1);

		submissionContainer.add(sub1);
		apiServlet.editSubmission(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		// check editing both submissions
		alterSubmission(sub1);
		alterSubmission(sub2);

		submissionContainer = new ArrayList<SubmissionData>();
		submissionContainer.add(sub1);
		submissionContainer.add(sub2);
		apiServlet.editSubmission(submissionContainer);

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
	}

	@Test
	public void testEditTfs() throws Exception {
		printTestCaseHeader();
		refreshDataInDatastore();

		TfsData tfs1 = dataBundle.teamFormingSessions
				.get("tfsInCourse1");
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
		teamProfile1.profile = new Text(teamProfile1.profile
				.getValue() + "x");
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

	private void verifyEvauationInfoExistsInList(EvaluationData evaluation,
			ArrayList<EvaluationData> evalInfoList) {

		for (EvaluationData ed : evalInfoList) {
			if (ed.name.equals(evaluation.name))
				return;
		}
		fail("Did not find " + evaluation.name
				+ " in the evaluation info list");
	}

	// ------------------------------------------------------------------------

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
			String newTeamName) {
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

	private void verifyTfsListForCoord(String coordId, int noOfTfs) {
		List<TfsData> tfsList = apiServlet
				.getTfsListForCoord(coordId);
		assertEquals(noOfTfs, tfsList.size());
		for (TfsData tfs : tfsList) {
			assertEquals(coordId,
					apiServlet.getCourse(tfs.course).coord);
		}
	}

	private void alterSubmission(SubmissionData submission) {
		submission.points = submission.points + 10;
		submission.p2pFeedback = new Text(submission
				.p2pFeedback.getValue() + "x");
		submission.justification = new Text(submission.justification
				.getValue() + "y");
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

	private void verifyAbsentInDatastore(SubmissionData submission) {
		assertEquals(
				null,
				apiServlet.getSubmission(submission.course,
						submission.evaluation,
						submission.reviewer, submission.reviewee));
	}

	private void verifyAbsentInDatastore(CoordData expectedCoord) {
		assertEquals(null, apiServlet.getCoord(expectedCoord.id));
	}

	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals(null, apiServlet.getCourse(course.id));
	}

	private void verifyAbsentInDatastore(StudentData student) {
		assertEquals(null,
				apiServlet.getStudent(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationData evaluation) {
		assertEquals(null,
				apiServlet.getEvaluation(evaluation.course, evaluation.name));
	}

	private void verifyAbsentInDatastore(TfsData tfs) {
		assertEquals(null, apiServlet.getTfs(tfs.course));
	}

	private void verifyAbsentInDatastore(TeamProfileData profile) {
		assertEquals(
				null,
				apiServlet.getTeamProfile(profile.course,
						profile.team));
	}

	private void verifyAbsenceOfTfsLogsForStudent(String courseId,
			String studentEmail) {
		List<StudentActionData> teamFormingLogs = apiServlet
				.getStudentActions(courseId);
		for (StudentActionData tfl : teamFormingLogs) {
			String actualEmail = tfl.email;
			assertTrue("unexpected email:" + actualEmail,
					!actualEmail.equals(studentEmail));
		}

	}

	private void verifyPresenceOfTfsLogsForStudent(String courseId,
			String studentEmail) {
		List<StudentActionData> teamFormingLogs = apiServlet
				.getStudentActions(courseId);
		for (StudentActionData tfl : teamFormingLogs) {
			if (tfl.email.equals(studentEmail))
				return;
		}
		fail("No log messages found for " + studentEmail + " in "
				+ courseId);
	}

	private void verifyPresentInDatastore(StudentData expectedStudent) {
		StudentData actualStudent = apiServlet.getStudent(
				expectedStudent.course, expectedStudent.email);
		expectedStudent.updateStatus = UpdateStatus.UNKNOWN;
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
		assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
	}

	private void verifyPresentInDatastore(SubmissionData expected) {
		SubmissionData actual = apiServlet.getSubmission(expected.course,
				expected.evaluation, expected.reviewer,
				expected.reviewee);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	private void verifyPresentInDatastore(StudentActionData expected) {
		List<StudentActionData> actualList = apiServlet.getStudentActions(expected
				.course);
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

	private void refreshDataInDatastore() throws Exception {
		dataBundle = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			apiServlet.deleteCoord(coord.id);
		}
		DataBundle data = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		apiServlet.persistNewDataBundle(data);
	}

	private boolean isLogEntryInList(StudentActionData teamFormingLogEntry,
			List<StudentActionData> teamFormingLogEntryList) {
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

	@AfterClass()
	public static void classTearDown(){
		printTestClassFooter("CoordCourseAddApiTest");
	}
	
	@After
	public void caseTearDown() {
		apiServlet.destroy();
		helper.tearDown();
	}

}
