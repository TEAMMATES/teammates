package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static teammates.TeamEvalResult.NA;
import static teammates.TeamEvalResult.NSB;
import static teammates.TeamEvalResult.NSU;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Datastore;
import teammates.TeamEvalResult;
import teammates.api.Common;
import teammates.api.EnrollException;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.api.JoinCourseException;
import teammates.api.Logic;
import teammates.api.UnauthorizedAccessException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvalResultData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.EvaluationData.EvalStatus;
import teammates.datatransfer.StudentActionData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.StudentData.UpdateStatus;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamData;
import teammates.datatransfer.TeamProfileData;
import teammates.datatransfer.TfsData;
import teammates.datatransfer.UserData;
import teammates.persistent.Student;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;

public class LogicTest extends BaseTestCase {

	final static Logic logic = new Logic();
	private static final int USER_TYPE_NOT_LOGGED_IN = -1;
	private static final int USER_TYPE_UNREGISTERED = 0;
	private static final int USER_TYPE_STUDENT = 1;
	private static final int USER_TYPE_COORD = 2;

	private static Gson gson = Common.getTeammatesGson();
	static String jsonString;

	private static DataBundle dataBundle = getTypicalDataBundle();

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Logic.class);
		Datastore.initialize();
	}

	@Before
	public void caseSetUp() throws ServletException {
		LocalTaskQueueTestConfig ltqtc = new LocalTaskQueueTestConfig();
		setEmailQueuePath(ltqtc);
		dataBundle = getTypicalDataBundle();
		LocalUserServiceTestConfig localUserServiceTestConfig = new LocalUserServiceTestConfig();
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig(),
				new LocalMailServiceTestConfig(), localUserServiceTestConfig,
				ltqtc);
		setHelperTimeZone(helper);
		helper.setUp();
	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods___________________________________() {
	}

	@Test
	public void testCoordGetLoginUrl() {
		printTestCaseHeader();
		assertEquals("/_ah/login?continue=www.abc.com",
				Logic.getLoginUrl("www.abc.com"));
	}

	@Test
	public void testCoordGetLogoutUrl() {
		printTestCaseHeader();
		assertEquals("/_ah/logout?continue=www.def.com",
				Logic.getLogoutUrl("www.def.com"));
	}

	@Test
	public void testGetLoggedInUser() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// also make this user a student
		StudentData coordAsStudent = new StudentData(
				"|Coord As Student|coordasstudent@yahoo.com|", "some-course");
		coordAsStudent.id = coord.id;
		logic.createStudent(coordAsStudent);

		helper.setEnvIsLoggedIn(true);
		helper.setEnvIsAdmin(true);

		helper.setEnvEmail(coord.id);
		helper.setEnvAuthDomain("gmail.com");
		UserData user = logic.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(true, user.isStudent);

		// this user is no longer a student
		logic.deleteStudent(coordAsStudent.course, coordAsStudent.email);
		// this user is no longer an admin
		helper.setEnvIsAdmin(false);

		user = logic.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(false, user.isStudent);

		// check for unregistered student
		helper.setEnvEmail("unknown");
		helper.setEnvAuthDomain("gmail.com");
		user = logic.getLoggedInUser();
		assertEquals("unknown", user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(false, user.isStudent);

		// check for user who is only a student
		StudentData student = dataBundle.students.get("student1InCourse1");
		helper.setEnvEmail(student.id);
		helper.setEnvAuthDomain("gmail.com");
		user = logic.getLoggedInUser();
		assertEquals(student.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(true, user.isStudent);

		// check for user not logged in
		helper.setEnvIsLoggedIn(false);
		assertEquals(null, logic.getLoggedInUser());
		assertEquals(null, logic.getLoggedInUser());
		assertEquals(null, logic.getLoggedInUser());
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods____________________________________() {
	}

	@Test
	public void testCreateCoord() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("unauthorized access");

		String methodName = "createCoord";
		Class<?>[] paramTypes = new Class[] { String.class, String.class,
				String.class };
		Object[] params = new Object[] { "id", "name", "email@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, null,
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName,
				"student1InCourse1", paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("success case");

		loginAsAdmin("admin.user");
		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// delete, to avoid clashes with existing data
		logic.deleteCoord(coord.id);
		verifyAbsentInDatastore(coord);
		// create
		logic.createCoord(coord.id, coord.name, coord.email);
		// read existing coord
		verifyPresentInDatastore(coord);
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		// create a course to check cascade delete later
		logic.createCourse(coord.id, course.id, course.name);
		verifyPresentInDatastore(course);
		// delete existing
		logic.deleteCoord(coord.id);
		// read non-existent coord
		verifyAbsentInDatastore(coord);
		// check for cascade delete
		verifyAbsentInDatastore(course);
		// delete non-existent (fails silently)
		logic.deleteCoord(coord.id);

		______TS("invalid parameters");

		// we check one invalid value for each parameter.

		try {
			logic.createCoord("valid-id", "", "valid@email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_EMPTY_STRING, e.errorCode);
			BaseTestCase.assertContains("Coordinator name", e.getMessage());
		}

		try {
			logic.createCoord("valid-id", "valid name", "invalid email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_EMAIL, e.errorCode);
			BaseTestCase.assertContains("Email address", e.getMessage());
		}

		try {
			logic.createCoord("invalid id", "valid name", "valid@email.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			BaseTestCase.assertContains("Google ID", e.getMessage());
		}
	}

	@Test
	public void testGetCoord() throws Exception {
		// mostly tested in testCreateCoord
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("unauthorized: not logged in");

		Class<?>[] paramTypes = new Class[] { String.class };
		Object[] params = new Object[] { "id" };
		String methodName = "getCoord";

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, null,
				paramTypes, params);

		______TS("authorized: logged in");

		verifyCanAccess(USER_TYPE_UNREGISTERED, methodName,
				"student1InCourse1", paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

	}

	@Test
	public void testEditCoord() {
		// method not implemented
	}

	@Test
	public void testDeleteCoord() throws Exception {
		printTestCaseHeader();
		// mostly tested in testCreateCoord

		______TS("unauthorized");

		restoreTypicalDataInDatastore();

		Class<?>[] paramTypes = new Class[] { String.class };
		Object[] params = new Object[] { "id" };
		String methodName = "deleteCoord";

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, null,
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName,
				"student1InCourse1", paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);
	}

	@Test
	public void testGetCourseListForCoord() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("authentication");

		String methodName = "getCourseListForCoord";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfTypicalCoord1" };
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// coord does not own the given coordId
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "diff-id" });

		// coord owns the given id
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("coord with 2 courses");

		loginAsAdmin("admin.user");

		CoordData coord = dataBundle.coords.get("typicalCoord1");
		HashMap<String, CourseData> courseList = logic
				.getCourseListForCoord(coord.id);
		assertEquals(2, courseList.size());
		for (CourseData item : courseList.values()) {
			// check if course belongs to this coord
			assertEquals(coord.id, logic.getCourse(item.id).coord);
		}

		______TS("coord with 0 courses");

		coord = dataBundle.coords.get("typicalCoord3");
		courseList = logic.getCourseListForCoord(coord.id);
		assertEquals(0, courseList.size());

		______TS("null parameters");

		assertEquals(null, logic.getCourseListForCoord(null));

		______TS("non-existent coord");

		try {
			logic.getCourseListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testGetCourseDetailsListForCoord() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		
		______TS("authentication");

		String methodName = "getCourseDetailsListForCoord";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfTypicalCoord1" };
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// coord does not own the given coordId
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "diff-id" });

		// coord owns the given id
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);
		
		______TS("typical case");

		loginAsAdmin("admin.user");
		
		HashMap<String, CourseData> courseListForCoord = logic
				.getCourseDetailsListForCoord("idOfTypicalCoord1");
		assertEquals(2, courseListForCoord.size());
		String course1Id = "idOfCourse1OfCoord1";

		// course with 2 evaluations
		ArrayList<EvaluationData> course1Evals = courseListForCoord
				.get(course1Id).evaluations;
		String course1EvalDetails = "";
		for (EvaluationData ed : course1Evals) {
			course1EvalDetails = course1EvalDetails
					+ Common.getTeammatesGson().toJson(ed) + Common.EOL;
		}
		// TODO: this line fails at times. actual <3> expected <2>
		// there is some hidden data dependency with other tests
		int numberOfEvalsInCourse1 = course1Evals.size();
		assertEquals(course1EvalDetails, 2, numberOfEvalsInCourse1);
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

		______TS("coord has a course with 0 evaluations");
		
		courseListForCoord = logic
				.getCourseDetailsListForCoord("idOfTypicalCoord2");
		assertEquals(2, courseListForCoord.size());
		assertEquals(0,
				courseListForCoord.get("idOfCourse2OfCoord2").evaluations
						.size());

		______TS("coord with 0 courses");
		
		loginAsAdmin("admin.user");
		logic.createCoord("coordWith0course", "Coord with 0 courses",
				"coordWith0course@gmail.com");
		courseListForCoord = logic
				.getCourseDetailsListForCoord("coordWith0course");
		assertEquals(0, courseListForCoord.size());

		______TS("null parameters");
		
		assertEquals(null, logic.getCourseDetailsListForCoord(null));

		______TS("non-existent coord");
		
		try {
			logic.getCourseDetailsListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

	}

	@Test
	public void testGetEvalListForCoord() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		
		______TS("authentication");

		String methodName = "getEvaluationsListForCoord";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfTypicalCoord1" };
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// coord does not own the given coordId
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "diff-id" });

		// coord owns the given id
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);
		
		______TS("typical case, coord has 3 evaluations");

		CoordData coord1 = dataBundle.coords.get("typicalCoord1");
		ArrayList<EvaluationData> evalList = logic
				.getEvaluationsListForCoord(coord1.id);
		assertEquals(3, evalList.size());
		for (EvaluationData ed : evalList) {
			assertTrue(ed.course.contains("Coord1"));
		}

		______TS("coord has 1 evaluation");
		
		loginAsAdmin("admin.user");
		
		CoordData coord2 = dataBundle.coords.get("typicalCoord2");
		evalList = logic.getEvaluationsListForCoord(coord2.id);
		assertEquals(1, evalList.size());
		for (EvaluationData ed : evalList) {
			assertTrue(ed.course.contains("Coord2"));
		}

		______TS("coord has 0 evaluations");
		
		CoordData coord3 = dataBundle.coords.get("typicalCoord3");
		evalList = logic.getEvaluationsListForCoord(coord3.id);
		assertEquals(0, evalList.size());

		______TS("null parameters");
		
		assertEquals(null, logic.getEvaluationsListForCoord(null));

		______TS("non-existent coord");
		
		try {
			logic.getEvaluationsListForCoord("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods___________________________________() {
	}

	@Test
	public void testCreateCourse() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		
		______TS("authentication");

		String methodName = "createCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class, String.class };
		Object[] params = new Object[] { "idOfTypicalCoord1", "new-course", "New Course" };
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// coord does not own the given coordId
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "diff-id", "new-course", "New Course" });

		// coord owns the given id
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);
		
		______TS("typical case");

		CoordData coord = dataBundle.coords.get("typicalCoord1");
		// delete, to avoid clashes with existing data
		loginAsAdmin("admin.user");
		logic.deleteCoord(coord.id);

		CourseData course = dataBundle.courses.get("course1OfCoord1");

		verifyAbsentInDatastore(course);

		logic.createCourse(course.coord, course.id, course.name);
		verifyPresentInDatastore(course);

		______TS("duplicate course id");
		
		try {
			logic.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		______TS("invalid parameters");
		
		course.coord = "invalid id";
		try {
			logic.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			BaseTestCase.assertContains("Google ID", e.getMessage());
		}

		// create with invalid course ID
		course.coord = "typicalCoord1";
		course.id = "invalid id";
		try {
			logic.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_INVALID_CHARS, e.errorCode);
			BaseTestCase.assertContains("Course ID", e.getMessage());
		}

		// create with invalid course ID
		course.id = "valid-course-id";
		course.name = "";
		try {
			logic.createCourse(course.coord, course.id, course.name);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_EMPTY_STRING, e.errorCode);
			BaseTestCase.assertContains("Course name", e.getMessage());
		}

		// other combinations of invalid input should be checked against
		// CourseDataTest.validate()
	}

	@Test
	public void testGetCourse() throws Exception {
		// mostly tested in testCreateCourse
		______TS("authentication");
		
		restoreTypicalDataInDatastore();

		String methodName = "getCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		
		______TS("null parameters");
		
		assertEquals(null, logic.getCourse(null));
	}

	@Test
	public void testGetCourseDetails() throws Exception {
		printTestCaseHeader();
		
		______TS("authentication");
		
		restoreTypicalDataInDatastore();
		
		String methodName = "getCourseDetails";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		//course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2"});
		
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");
		
		loginAsAdmin("admin.user");

		CourseData course = dataBundle.courses.get("course1OfCoord1");
		CourseData courseDetials = logic.getCourseDetails(course.id);
		assertEquals(course.id, courseDetials.id);
		assertEquals(course.name, courseDetials.name);
		assertEquals(2, courseDetials.teamsTotal);
		assertEquals(5, courseDetials.studentsTotal);
		assertEquals(0, courseDetials.unregisteredTotal);

		______TS("course without students");

		logic.createCourse("coord1", "course1", "course 1");
		courseDetials = logic.getCourseDetails("course1");
		assertEquals("course1", courseDetials.id);
		assertEquals("course 1", courseDetials.name);
		assertEquals(0, courseDetials.teamsTotal);
		assertEquals(0, courseDetials.studentsTotal);
		assertEquals(0, courseDetials.unregisteredTotal);

		______TS("non-existent");

		try {
			logic.getCourseDetails("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

		// TODO: handle null parameters

		// TODO: more testing e.g, course without students etc.

	}

	@Test
	public void testEditCourse() {
		// method not implemented
	}

	@Test
	public void testDeleteCourse() throws Exception {
		printTestCaseHeader();

		______TS("authentication");
		
		restoreTypicalDataInDatastore();
		
		String methodName = "deleteCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		//course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2"});
		
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");
		
		CourseData course1OfCoord = dataBundle.courses.get("course1OfCoord1");

		// ensure there are entities in the datastore under this course
		assertTrue(logic.getStudentListForCourse(course1OfCoord.id).size() != 0);
		verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyPresentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1"));

		StudentData studentInCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(course1OfCoord.id, studentInCourse.course);
		verifyPresentInDatastore(studentInCourse);

		logic.deleteCourse(course1OfCoord.id);

		// ensure the course and related entities are deleted
		verifyAbsentInDatastore(course1OfCoord);
		verifyAbsentInDatastore(studentInCourse);
		verifyAbsentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyAbsentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1"));

		______TS("non-existent");
		
		// try to delete again. Should fail silently.
		logic.deleteCourse(course1OfCoord.id);

		______TS("null parameter");
		
		// try null parameter. Should fail silently.
		logic.deleteCourse(null);
	}

	@Test
	public void testGetStudentListForCourse() throws Exception {
		printTestCaseHeader();
		
		______TS("authentication");
		
		restoreTypicalDataInDatastore();
		
		String methodName = "getStudentListForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		//course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2"});
		
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("course with multiple students");
		
		restoreTypicalDataInDatastore();
		
		loginAsAdmin("admin.user");

		CourseData course1OfCoord1 = dataBundle.courses.get("course1OfCoord1");
		List<StudentData> studentList = logic
				.getStudentListForCourse(course1OfCoord1.id);
		assertEquals(5, studentList.size());
		for (StudentData s : studentList) {
			assertEquals(course1OfCoord1.id, s.course);
		}

		______TS("course with 0 students");
		
		CourseData course2OfCoord1 = dataBundle.courses.get("course2OfCoord1");
		studentList = logic.getStudentListForCourse(course2OfCoord1.id);
		assertEquals(0, studentList.size());

		______TS("null parameter");
		
		assertEquals(null, logic.getStudentListForCourse(null));

		______TS("non-existent course");
		
		try {
			logic.getStudentListForCourse("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testEnrollStudents() throws Exception {
		printTestCaseHeader();

		______TS("authentication");
		
		restoreTypicalDataInDatastore();
		
		String methodName = "enrollStudents";
		Class<?>[] paramTypes = new Class<?>[] { String.class , String.class};
		Object[] params = new Object[] {"t|n|e@c|c", "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		//course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "t|n|e@c|c", "idOfCourse1OfCoord2"});
		
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);
		
		______TS("all valid students, but contains blank lines");
		
		restoreTypicalDataInDatastore();

		String coordId = "coordForEnrollTesting";
		loginAsAdmin("admin.user");
		logic.createCoord(coordId, "Coord for Enroll Testing",
				"coordForEnrollTestin@gmail.com");
		String courseId = "courseForEnrollTest";
		logic.createCourse(coordId, courseId, "Course for Enroll Testing");
		String EOL = Common.EOL;

		String line0 = "t1|n1|e1@g|c1";
		String line1 = " t2|  n2|  e2@g|  c2";
		String line2 = "t3|n3|e3@g|c3  ";
		String line3 = "t4|n4|  e4@g|c4";
		String line4 = "t5|n5|e5@g  |c5";
		String lines = line0 + EOL + line1 + EOL + line2 + EOL
				+ "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
				+ EOL + "    " + EOL + EOL;
		List<StudentData> enrollResults = logic.enrollStudents(lines, courseId);

		assertEquals(5, enrollResults.size());
		assertEquals(5, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(new StudentData(line0, courseId),
				enrollResults.get(0), StudentData.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentData(line1, courseId),
				enrollResults.get(1), StudentData.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentData(line4, courseId),
				enrollResults.get(4), StudentData.UpdateStatus.NEW);

		______TS("includes a mix of unmodified, modified, and new");
		
		String line0_1 = "t3|modified name|e3@g|c3";
		String line5 = "t6|n6|e6@g|c6";
		lines = line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
		enrollResults = logic.enrollStudents(lines, courseId);
		assertEquals(6, enrollResults.size());
		assertEquals(6, logic.getStudentListForCourse(courseId).size());
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

		______TS("includes an incorrect line");
		
		//no changes should be done to the database
		String incorrectLine = "incorrectly formatted line";
		lines = "t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
				+ line3;
		try {
			enrollResults = logic.enrollStudents(lines, courseId);
			fail("Did not throw exception for incorrectly formatted line");
		} catch (EnrollException e) {
			assertTrue(e.getMessage().contains(incorrectLine));
		}
		assertEquals(6, logic.getStudentListForCourse(courseId).size());

		______TS("null parameters");
		
		try {
			logic.enrollStudents(null, courseId);
			fail();
		} catch (EnrollException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("Enroll text", e.getMessage());
		}

		try {
			logic.enrollStudents("any text", null);
			fail();
		} catch (EnrollException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("Course ID", e.getMessage());
		}

		______TS("same student added, modified and unmodified in one shot");
		
		logic.createCourse("tes.coord", "tes.course", "TES Course");
		lines = "t8|n8|e8@g|c1" + EOL + "t8|n8a|e8@g|c1" + EOL
				+ "t8|n8a|e8@g|c1";
		enrollResults = logic.enrollStudents(lines, "tes.course");

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
		
		______TS("authentication");
		
		restoreTypicalDataInDatastore();
		
		String methodName = "sendRegistrationInviteForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1"};
		
		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		//course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2"});
		
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("all students already registered");
		
		restoreTypicalDataInDatastore();
		CourseData course1 = dataBundle.courses.get("course1OfCoord1");

		// send registration key to a class in which all are registered
		logic.sendRegistrationInviteForCourse(course1.id);
		assertEquals(0, getNumberOfEmailTasksInQueue());

		______TS("some students not registered");
		
		// modify two students to make them 'unregistered' and send again
		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		student1InCourse1.id = "";
		logic.editStudent(student1InCourse1.email, student1InCourse1);
		StudentData student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		student2InCourse1.id = "";
		logic.editStudent(student2InCourse1.email, student2InCourse1);
		logic.sendRegistrationInviteForCourse(course1.id);
		assertEquals(2, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student1InCourse1);
		verifyRegistrationEmailToStudent(student2InCourse1);

		______TS("send again to the same class");
		
		logic.sendRegistrationInviteForCourse(course1.id);
		assertEquals(4, getNumberOfEmailTasksInQueue());

		______TS("null parameters");
		
		try {
			logic.sendRegistrationInviteForCourse(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}
	}

	@Test
	public void testGetTeamsForCourse() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		
		loginAsAdmin("admin.user");

		// testing for typical course
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		logic.createStudent(new StudentData("|s1|s1@e|", course.id));
		logic.createStudent(new StudentData("|s2|s2@e|", course.id));
		CourseData courseAsTeams = logic.getTeamsForCourse(course.id);
		assertEquals(2, courseAsTeams.teams.size());

		String team1Id = "Team 1.1";
		assertEquals(team1Id, courseAsTeams.teams.get(0).name);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(0).team);
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(1).team);

		String team2Id = "Team 1.2";
		assertEquals(team2Id, courseAsTeams.teams.get(1).name);
		assertEquals(1, courseAsTeams.teams.get(1).students.size());
		assertEquals(team2Id, courseAsTeams.teams.get(1).students.get(0).team);

		assertEquals(2, courseAsTeams.loners.size());
		assertEquals("s1@e", courseAsTeams.loners.get(0).email);
		assertEquals("s2@e", courseAsTeams.loners.get(1).email);

		// try again without the loners
		restoreTypicalDataInDatastore();
		courseAsTeams = logic.getTeamsForCourse(course.id);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(0, courseAsTeams.loners.size());

		assertEquals(null, logic.getTeamsForCourse(null));

		// course without teams
		logic.createCourse("coord1", "course1", "Course 1");
		assertEquals(0, logic.getTeamsForCourse("course1").teams.size());

		// non-existent course
		try {
			logic.getTeamsForCourse("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
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

		logic.createStudent(newStudent);
		verifyPresentInDatastore(newStudent);

		// try to create the same student
		try {
			logic.createStudent(newStudent);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		try {
			logic.createStudent(null);
			fail();
		} catch (InvalidParametersException e) {
		}

		// TODO: test for invalid parameters in StudentData

	}

	@Test
	public void testGetStudent() {
		// mostly tested in testCreateStudent
		assertEquals(null, logic.getStudent(null, "email@email.com"));
		assertEquals(null, logic.getStudent("course-id", null));
	}

	@Test
	public void testGetStudentWithId() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("student in one course");
		StudentData studentInOneCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(1, logic.getStudentsWithId(studentInOneCourse.id).size());
		assertEquals(studentInOneCourse.email,
				logic.getStudentsWithId(studentInOneCourse.id).get(0).email);
		assertEquals(studentInOneCourse.name,
				logic.getStudentsWithId(studentInOneCourse.id).get(0).name);
		assertEquals(studentInOneCourse.course,
				logic.getStudentsWithId(studentInOneCourse.id).get(0).course);

		______TS("student in two courses");
		// this student is in two courses, course1 and course 2.

		// get list using student data from course 1
		StudentData studentInTwoCoursesInCourse1 = dataBundle.students
				.get("student2InCourse1");
		ArrayList<StudentData> listReceivedUsingStudentInCourse1 = logic
				.getStudentsWithId(studentInTwoCoursesInCourse1.id);
		assertEquals(2, listReceivedUsingStudentInCourse1.size());

		// get list using student data from course 2
		StudentData studentInTwoCoursesInCourse2 = dataBundle.students
				.get("student2InCourse2");
		ArrayList<StudentData> listReceivedUsingStudentInCourse2 = logic
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
		assertEquals(null, logic.getStudentsWithId("non-existent"));
	}

	@Test
	public void testEditStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

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
		logic.editStudent(originalEmail, student1InCourse1);
		verifyPresentInDatastore(student1InCourse1);

		______TS("check for KeepExistingPolicy");
		// try changing email only
		StudentData copyOfStudent1 = new StudentData();
		copyOfStudent1.course = student1InCourse1.course;
		originalEmail = student1InCourse1.email;

		student1InCourse1.email = student1InCourse1.email + "y";
		copyOfStudent1.email = student1InCourse1.email;

		logic.editStudent(originalEmail, copyOfStudent1);
		verifyPresentInDatastore(student1InCourse1);

		______TS("non-existent student");
		student1InCourse1.course = "new-course";
		verifyAbsentInDatastore(student1InCourse1);
		try {
			logic.editStudent(originalEmail, student1InCourse1);
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("new-course", e.getMessage());
		}

		// no need to check for cascade delete/creates due to LazyCreationPolicy
		// and TolerateOrphansPolicy.

		// TODO: test for invalid parameters in StudentData
	}

	@Test
	public void testDeleteStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

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

		logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);
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

		______TS("delete non-existent student");
		// should fail silently.
		logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);

		______TS("null parameters");
		// should fail silently.
		logic.deleteStudent(null, student1InCourse1.email);
		logic.deleteStudent(student1InCourse1.course, null);

		// No need to test for cascade delete of TeamProfiles because we follow
		// tolerateOrphansPolicy for TeamProfiles
	}

	@Test
	public void testEnrollStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		String coordId = "coordForEnrollTesting";
		loginAsAdmin("admin.user");
		logic.deleteCoord(coordId);
		logic.createCoord(coordId, "Coord for Enroll Testing",
				"coordForEnrollTestin@gmail.com");
		String courseId = "courseForEnrollTest";
		logic.createCourse(coordId, courseId, "Course for Enroll Testing");

		______TS("add student into empty course");
		StudentData student1 = new StudentData("t|n|e@g|c", courseId);

		// check if the course is empty
		assertEquals(0, logic.getStudentListForCourse(courseId).size());

		// add a new student and verify it is added and treated as a new student
		StudentData enrollmentResult = logic.enrollStudent(student1);
		assertEquals(1, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.NEW);
		verifyPresentInDatastore(student1);

		______TS("add existing student");
		// Verify it was not added
		enrollmentResult = logic.enrollStudent(student1);
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.UNMODIFIED);

		______TS("modify info of existing student");
		// verify it was treated as modified
		StudentData student2 = dataBundle.students.get("student1InCourse1");
		student2.name = student2.name + "y";
		StudentData studentToEnroll = new StudentData(student2.email,
				student2.name, student2.comments, student2.course,
				student2.team);
		enrollmentResult = logic.enrollStudent(studentToEnroll);
		verifyEnrollmentResultForStudent(studentToEnroll, enrollmentResult,
				StudentData.UpdateStatus.MODIFIED);
		// check if the student is actually modified in datastore and existing
		// values not specified in enroll action (e.g, id) prevail
		verifyPresentInDatastore(student2);

		______TS("add student into non-empty course");
		StudentData student3 = new StudentData("t3|n3|e3@g|c3", courseId);
		enrollmentResult = logic.enrollStudent(student3);
		assertEquals(2, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student3, enrollmentResult,
				StudentData.UpdateStatus.NEW);

		______TS("add student without team");
		StudentData student4 = new StudentData("|n4|e4@g", courseId);
		enrollmentResult = logic.enrollStudent(student4);
		assertEquals(3, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student4, enrollmentResult,
				StudentData.UpdateStatus.NEW);
	}

	@Test
	public void testSendRegistrationInviteToStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("send to existing student");
		StudentData student1 = dataBundle.students.get("student1InCourse1");
		logic.sendRegistrationInviteToStudent(student1.course, student1.email);

		assertEquals(1, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student1);

		// send to another student
		StudentData student2 = dataBundle.students.get("student2InCourse1");
		logic.sendRegistrationInviteToStudent(student2.course, student2.email);

		assertEquals(2, getNumberOfEmailTasksInQueue());
		verifyRegistrationEmailToStudent(student2);

		______TS("send to non-existing student");
		try {
			logic.sendRegistrationInviteToStudent(student1.course,
					"non@existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non@existent", e.getMessage());
			BaseTestCase.assertContains(student1.course, e.getMessage());
		}
		assertEquals(2, getNumberOfEmailTasksInQueue());

		______TS("try with null parameters");
		try {
			logic.sendRegistrationInviteToStudent(student1.course, null);
			fail();
		} catch (InvalidParametersException e) {
		}
		try {
			logic.sendRegistrationInviteToStudent(null, student1.email);
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
		restoreTypicalDataInDatastore();

		______TS("register an unregistered student");

		// make a student 'unregistered'
		StudentData student = dataBundle.students.get("student1InCourse1");
		String googleId = "student1InCourse1";
		String key = logic.getKeyForStudent(student.course, student.email);
		student.id = "";
		logic.editStudent(student.email, student);
		assertEquals("", logic.getStudent(student.course, student.email).id);

		helper.setEnvIsLoggedIn(true);
		helper.setEnvEmail(googleId);
		helper.setEnvAuthDomain("gmail.com");

		logic.joinCourse(googleId, key);
		assertEquals(googleId,
				logic.getStudent(student.course, student.email).id);

		______TS("try to register again with a valid key");

		try {
			logic.joinCourse(googleId, key);
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_ALREADY_JOINED, e.errorCode);
		}
		assertEquals(googleId,
				logic.getStudent(student.course, student.email).id);

		______TS("use a valid key belonging to a different user");

		helper.setEnvEmail("student2InCourse1");
		helper.setEnvAuthDomain("gmail.com");
		try {
			logic.joinCourse("student2InCourse1", key);
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER,
					e.errorCode);
		}
		assertEquals(googleId,
				logic.getStudent(student.course, student.email).id);

		______TS("try to register with invalid key");

		// make a student 'unregistered'
		student.id = "";
		logic.editStudent(student.email, student);

		try {
			logic.joinCourse(googleId, "invalidkey");
			fail();
		} catch (JoinCourseException e) {
			assertEquals(Common.ERRORCODE_INVALID_KEY, e.errorCode);
		}

		assertEquals("", logic.getStudent(student.course, student.email).id);

		______TS("null parameters");

		try {
			logic.joinCourse(googleId, null);
			fail();
		} catch (InvalidParametersException e) {
		}
		try {
			logic.joinCourse(null, null);
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
		assertEquals(null, logic.getKeyForStudent(student.course, null));
		assertEquals(null, logic.getKeyForStudent(null, student.email));
		assertEquals(null, logic.getKeyForStudent(null, null));

		______TS("non-existent student");
		assertEquals(null,
				logic.getKeyForStudent(student.course, "non@existent"));
	}

	@Test
	public void testGetCourseListForStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("student having two courses");
		StudentData studentInTwoCourses = dataBundle.students
				.get("student2InCourse1");
		List<CourseData> courseList = logic
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
		courseList = logic.getCourseListForStudent(studentInOneCourse.id);
		assertEquals(1, courseList.size());
		course1 = dataBundle.courses.get("course1OfCoord1");
		assertEquals(course1.id, courseList.get(0).id);
		assertEquals(course1.name, courseList.get(0).name);

		// student having zero courses is not applicable

		______TS("non-existent student");
		try {
			logic.getCourseListForStudent("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

		______TS("null parameter");
		try {
			logic.getCourseListForStudent(null);
			fail();
		} catch (InvalidParametersException e) {
			BaseTestCase.assertContains(Common.ERRORCODE_NULL_PARAMETER,
					e.errorCode);
		}
	}

	@Test
	public void testGetCourseDetailsListForStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

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
		logic.editEvaluation(expectedEval1InCourse1);

		expectedEval2InCourse1.startTime = Common
				.getDateOffsetToCurrentTime(-1);
		expectedEval2InCourse1.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, expectedEval2InCourse1.getStatus());
		logic.editEvaluation(expectedEval2InCourse1);

		// Make sure all evaluations in course2 are still AWAITING
		expectedEval1InCourse2.startTime = Common.getDateOffsetToCurrentTime(1);
		expectedEval1InCourse2.endTime = Common.getDateOffsetToCurrentTime(2);
		expectedEval1InCourse2.activated = false;
		assertEquals(EvalStatus.AWAITING, expectedEval1InCourse2.getStatus());
		logic.editEvaluation(expectedEval1InCourse2);

		// Get course details for student
		List<CourseData> courseList = logic
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
		courseList = logic
				.getCourseDetailsListForStudent(studentWithNoEvaluations.id);
		assertEquals(1, courseList.size());
		assertEquals(0, courseList.get(0).evaluations.size());

		// student with 0 courses not applicable

		______TS("non-existent student");

		try {
			logic.getCourseDetailsListForStudent("non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

		______TS("null parameter");

		try {
			logic.getCourseDetailsListForStudent(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("google id", e.getMessage()
					.toLowerCase());
		}

	}

	@Test
	public void testHasStudentSubmittedEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		StudentData student = dataBundle.students.get("student1InCourse1");

		______TS("student has submitted");

		assertEquals(true, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, student.email));

		______TS("student has not submitted");

		// create a new evaluation reusing data from previous one
		evaluation.name = "New evaluation";
		logic.createEvaluation(evaluation);
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, student.email));

		______TS("null parameters");

		try {
			logic.hasStudentSubmittedEvaluation(null, evaluation.name,
					student.email);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("course id", e.getMessage()
					.toLowerCase());
		}
		try {
			logic.hasStudentSubmittedEvaluation(evaluation.course, null,
					student.email);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("evaluation name", e.getMessage()
					.toLowerCase());
		}

		try {
			logic.hasStudentSubmittedEvaluation(evaluation.course,
					evaluation.name, null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("student email", e.getMessage()
					.toLowerCase());
		}

		______TS("non-existent course/evaluation/student");

		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				"non-existent-course", evaluation.name, student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, "non-existent-eval", student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, "non-existent@student"));

	}

	@Test
	public void testGetStudentInCourseForGoogleId() throws Exception {

		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		StudentData studentInTwoCoursesInCourse1 = dataBundle.students
				.get("student2InCourse1");

		String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.id;
		assertEquals(studentInTwoCoursesInCourse1.email,
				logic.getStudentInCourseForGoogleId(
						studentInTwoCoursesInCourse1.course,
						googleIdOfstudentInTwoCourses).email);

		StudentData studentInTwoCoursesInCourse2 = dataBundle.students
				.get("student2InCourse2");
		assertEquals(studentInTwoCoursesInCourse2.email,
				logic.getStudentInCourseForGoogleId(
						studentInTwoCoursesInCourse2.course,
						googleIdOfstudentInTwoCourses).email);

		// TODO: more testing
	}

	@Test
	public void testGetEvauationResultForStudent() throws Exception {

		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");
		
		loginAsAdmin("admin.user");

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

		EvalResultData result = logic.getEvaluationResultForStudent(course.id,
				evaluation.name, student1email);

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

		// check calculated values
		assertEquals(student1email, result.getOwnerEmail());
		assertEquals(100, result.claimedFromStudent);
		assertEquals(100, result.claimedToCoord);
		assertEquals(90, result.perceivedToCoord);
		assertEquals(90, result.perceivedToStudent);
		int teamSize = 4;

		// check size of submission lists
		assertEquals(teamSize, result.outgoing.size());
		assertEquals(teamSize, result.incoming.size());
		assertEquals(teamSize, result.selfEvaluations.size());

		// check reviewee of incoming
		assertEquals("student1InCourse1@gmail.com",
				result.outgoing.get(0).reviewee);
		assertEquals("student2InCourse1@gmail.com",
				result.outgoing.get(1).reviewee);
		assertEquals("student3InCourse1@gmail.com",
				result.outgoing.get(2).reviewee);
		assertEquals("student4InCourse1@gmail.com",
				result.outgoing.get(3).reviewee);

		// check sorting of 'incoming' (should be sorted feedback)
		String feedback1 = result.incoming.get(0).p2pFeedback.getValue();
		String feedback2 = result.incoming.get(1).p2pFeedback.getValue();
		String feedback3 = result.incoming.get(2).p2pFeedback.getValue();
		String feedback4 = result.incoming.get(3).p2pFeedback.getValue();
		assertTrue(0 > feedback1.compareTo(feedback2));
		assertTrue(0 > feedback2.compareTo(feedback3));
		assertTrue(0 > feedback3.compareTo(feedback4));

		// check reviewer of outgoing
		assertEquals("student3InCourse1@gmail.com",
				result.incoming.get(0).reviewer);
		assertEquals("student2InCourse1@gmail.com",
				result.incoming.get(1).reviewer);
		assertEquals("student4InCourse1@gmail.com",
				result.incoming.get(2).reviewer);
		assertEquals("student1InCourse1@gmail.com",
				result.incoming.get(3).reviewer);

		// check some random values from submission lists
		assertEquals(100, result.outgoing.get(1).points); // reviewee=student2
		assertEquals(NSB, result.incoming.get(0).points); // reviewer=student3
		assertEquals(113, result.incoming.get(0).normalized); // reviewer=student3
		assertEquals(
				"justification of student1InCourse1 rating to student1InCourse1",
				result.selfEvaluations.get(0).justification.getValue()); // student2

		______TS("null parameter");

		try {
			logic.getEvaluationResultForStudent(null, "eval name",
					"e@gmail.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("course id", e.getMessage()
					.toLowerCase());
		}

		try {
			logic.getEvaluationResultForStudent("course-id", null,
					"e@gmail.com");
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("evaluation name", e.getMessage()
					.toLowerCase());
		}

		try {
			logic.getEvaluationResultForStudent("course-id", "eval name", null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("student email", e.getMessage()
					.toLowerCase());
		}

		______TS("non-existent course");

		try {
			logic.getEvaluationResultForStudent("non-existent-course",
					evaluation.name, student1email);
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent-course", e.getMessage()
					.toLowerCase());
		}

		______TS("non-existent evaluation");

		try {
			logic.getEvaluationResultForStudent(course.id, "non existent eval",
					student1email);
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non existent eval", e.getMessage()
					.toLowerCase());
		}

		______TS("non-existent student");

		try {
			logic.getEvaluationResultForStudent(course.id, evaluation.name,
					"non-existent@email.com");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent@email.com", e
					.getMessage().toLowerCase());
		}

		______TS("student added after evaluation");

		// TODO: test this after implementing lazy creation of submissions

	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods_______________________________() {

	}

	@Test
	public void testCreateEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(evaluation);
		logic.deleteEvaluation(evaluation.course, evaluation.name);
		verifyAbsentInDatastore(evaluation);
		logic.createEvaluation(evaluation);
		verifyPresentInDatastore(evaluation);

		______TS("Duplicate evaluation name");

		try {
			logic.createEvaluation(evaluation);
			fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(evaluation.name, e.getMessage());
		}

		______TS("invalid parameters");
		try {
			logic.createEvaluation(null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		evaluation.name = evaluation.name + "new";
		evaluation.course = null;
		try {
			logic.createEvaluation(evaluation);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("course id", e.getMessage()
					.toLowerCase());
		}
		// invalid values to other parameters should be checked against
		// EvaluationData.validate();

	}

	@Test
	public void testGetEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");

		EvaluationData expected = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		EvaluationData actual = logic.getEvaluation(expected.course,
				expected.name);
		verifySameEvaluationData(expected, actual);

		______TS("null parameters");

		assertEquals(null, logic.getEvaluation(null, expected.name));
		assertEquals(null, logic.getEvaluation(expected.course, null));

		______TS("non-existent");

		assertEquals(null, logic.getEvaluation("non-existent", expected.name));
		assertEquals(null, logic.getEvaluation(expected.course, "non-existent"));

	}

	@Test
	public void testEditEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");

		EvaluationData eval = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		eval.gracePeriod = eval.gracePeriod + 1;
		eval.instructions = eval.instructions + "x";
		eval.p2pEnabled = (!eval.p2pEnabled);
		eval.startTime = Common.getDateOffsetToCurrentTime(-1);
		eval.endTime = Common.getDateOffsetToCurrentTime(2);
		logic.editEvaluation(eval);
		verifyPresentInDatastore(eval);

		______TS("null parameters");

		try {
			logic.editEvaluation(null);
			fail();
		} catch (InvalidParametersException e) {
			verifyNullParameterDetectedCorrectly(e, "evaluation");
		}

		______TS("invalid parameters");

		// make the evaluation invalid;
		eval.course = null;
		try {
			logic.editEvaluation(eval);
			fail();
		} catch (InvalidParametersException e) {
			verifyNullParameterDetectedCorrectly(e, "course id");
		}

		// Checking for other type of invalid parameter situations
		// is done in EvaluationDataTest

	}

	@Test
	public void testDeleteEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical delete");
		EvaluationData eval = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		verifyPresentInDatastore(eval);
		// verify there are submissions under this evaluation
		SubmissionData submission = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submission);

		logic.deleteEvaluation(eval.course, eval.name);
		verifyAbsentInDatastore(eval);
		// verify submissions are deleted too
		verifyAbsentInDatastore(submission);

		______TS("null parameters");
		// should fail silently
		logic.deleteEvaluation(null, eval.name);
		logic.deleteEvaluation(eval.course, null);

		______TS("non-existent");
		// should fail silently
		logic.deleteEvaluation("non-existent", eval.name);
		logic.deleteEvaluation(eval.course, "non-existent");

	}

	@Test
	public void testPublishAndUnpublishEvaluation() throws Exception {

		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		EvaluationData eval1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		assertEquals(false,
				logic.getEvaluation(eval1.course, eval1.name).published);
		logic.publishEvaluation(eval1.course, eval1.name);
		assertEquals(true,
				logic.getEvaluation(eval1.course, eval1.name).published);
		logic.unpublishEvaluation(eval1.course, eval1.name);
		assertEquals(false,
				logic.getEvaluation(eval1.course, eval1.name).published);
		// TODO: more testing
	}

	@Test
	public void testGetEvaluationResult() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();
		
		loginAsAdmin("admin.user");

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

		EvaluationData result = logic.getEvaluationResult(course.id,
				evaluation.name);
		print(result.toString());

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
		assertEquals(90, s1.result.perceivedToStudent);
		assertEquals(90, s1.result.perceivedToCoord);
		// check some more individual values
		assertEquals(110, s2.result.claimedFromStudent);
		assertEquals(NSB, s3.result.claimedToCoord);
		assertEquals(95, s4.result.perceivedToStudent);
		assertEquals(96, s2.result.perceivedToCoord);

		// check outgoing submissions (s1 more intensely than others)

		assertEquals(4, s1.result.outgoing.size());

		SubmissionData s1_s1 = s1.result.outgoing.get(S1_POS);
		assertEquals(100, s1_s1.normalizedToCoord);
		String expected = "justification of student1InCourse1 rating to student1InCourse1";
		assertEquals(expected, s1_s1.justification.getValue());
		expected = "student1InCourse1 view of team dynamics";
		assertEquals(expected, s1_s1.p2pFeedback.getValue());

		SubmissionData s1_s2 = s1.result.outgoing.get(S2_POS);
		assertEquals(100, s1_s2.normalizedToCoord);
		expected = "justification of student1InCourse1 rating to student2InCourse1";
		assertEquals(expected, s1_s2.justification.getValue());
		expected = "comments from student1InCourse1 to student2InCourse1";
		assertEquals(expected, s1_s2.p2pFeedback.getValue());

		assertEquals(100, s1.result.outgoing.get(S3_POS).normalizedToCoord);
		assertEquals(100, s1.result.outgoing.get(S4_POS).normalizedToCoord);

		assertEquals(NSU, s2.result.outgoing.get(S3_POS).normalizedToCoord);
		assertEquals(100, s2.result.outgoing.get(S4_POS).normalizedToCoord);
		assertEquals(NSB, s3.result.outgoing.get(S2_POS).normalizedToCoord);
		assertEquals(84, s4.result.outgoing.get(S2_POS).normalizedToCoord);

		// check incoming submissions (s2 more intensely than others)

		assertEquals(4, s1.result.incoming.size());
		assertEquals(90, s1.result.incoming.get(S1_POS).normalized);
		assertEquals(100, s1.result.incoming.get(S4_POS).normalized);

		SubmissionData s2_s1 = s1.result.incoming.get(S2_POS);
		assertEquals(96, s2_s1.normalized);
		expected = "justification of student2InCourse1 rating to student1InCourse1";
		assertEquals(expected, s2_s1.justification.getValue());
		expected = "comments from student2InCourse1 to student1InCourse1";
		assertEquals(expected, s2_s1.p2pFeedback.getValue());
		assertEquals(115, s2.result.incoming.get(S4_POS).normalized);

		SubmissionData s3_s1 = s1.result.incoming.get(S3_POS);
		assertEquals(113, s3_s1.normalized);
		assertEquals("", s3_s1.justification.getValue());
		assertEquals("", s3_s1.p2pFeedback.getValue());
		assertEquals(113, s3.result.incoming.get(S3_POS).normalized);

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
		assertEquals(null, logic.getEvaluationResult(null, evaluation.name));
		assertEquals(null, logic.getEvaluationResult(course.id, null));

		// try for non-existent courses/evaluations
		try {
			logic.getEvaluationResult(course.id, "non existent evaluation");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non existent evaluation",
					e.getMessage());
		}

		try {
			logic.getEvaluationResult("non-existent-course", "any name");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent-course", e.getMessage());
		}

		// TODO: reduce rounding off error during
		// "self rating removed and normalized"

		//@formatter:off
				
				createNewEvaluationWithSubmissions("courseForTestingER", "Eval 1", new int[][] { 
						{ 110, 100, 110 },
						{ 90, 110, NSU }, 
						{ 90, 100, 110 } });
				//@formatter:on

		result = logic.getEvaluationResult("courseForTestingER", "Eval 1");
		print(result.toString());

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
		//@formatter:off
		int[][] expected = { 
				{ 110, 120, 130 }, 
				{ 210, 220, 230 },
				{ 310, 320, 330 } };
		assertEquals(TeamEvalResult.pointsToString(expected),
				TeamEvalResult.pointsToString(teamResult.claimedToStudents));

		
		// expected result
		// claimedToCoord 		[ 92, 100, 108]
		//                		[ 95, 100, 105]
		// 				  		[ 97, 100, 103]
		// ===============
		// unbiased   			[ NA,  96, 104]
		//                		[ 95,  NA, 105]
		// 				  		[ 98, 102,  NA]
		// ===============
		// perceivedToCoord 	[ 97, 99, 105]
		// ===============
		// perceivedToStudents 	[116, 118, 126]
		// 						[213, 217, 230]
		// 						[309, 316, 335]
		//@formatter:on

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
		assertEquals(92, s1.result.outgoing.get(S1_POS).normalizedToCoord);
		assertEquals(100, s1.result.outgoing.get(S2_POS).normalizedToCoord);
		assertEquals(108, s1.result.outgoing.get(S3_POS).normalizedToCoord);
		assertEquals(s1.name, s1.result.incoming.get(S1_POS).revieweeName);
		assertEquals(s1.name, s1.result.incoming.get(S1_POS).reviewerName);
		assertEquals(116, s1.result.incoming.get(S1_POS).normalized);
		assertEquals(119, s1.result.incoming.get(S2_POS).normalized);
		assertEquals(125, s1.result.incoming.get(S3_POS).normalized);
		assertEquals(NA, s1.result.incoming.get(S1_POS).normalizedToCoord);
		assertEquals(95, s1.result.incoming.get(S2_POS).normalizedToCoord);
		assertEquals(98, s1.result.incoming.get(S3_POS).normalizedToCoord);

		s2 = team.students.get(S2_POS);
		assertEquals(220, s2.result.claimedFromStudent);
		assertEquals(100, s2.result.claimedToCoord);
		assertEquals(217, s2.result.perceivedToStudent);
		assertEquals(99, s2.result.perceivedToCoord);
		assertEquals(95, s2.result.outgoing.get(S1_POS).normalizedToCoord);
		assertEquals(100, s2.result.outgoing.get(S2_POS).normalizedToCoord);
		assertEquals(105, s2.result.outgoing.get(S3_POS).normalizedToCoord);
		assertEquals(213, s2.result.incoming.get(S1_POS).normalized);
		assertEquals(217, s2.result.incoming.get(S2_POS).normalized);
		assertEquals(229, s2.result.incoming.get(S3_POS).normalized);
		assertEquals(96, s2.result.incoming.get(S1_POS).normalizedToCoord);
		assertEquals(NA, s2.result.incoming.get(S2_POS).normalizedToCoord);
		assertEquals(102, s2.result.incoming.get(S3_POS).normalizedToCoord);

		s3 = team.students.get(S3_POS);
		assertEquals(330, s3.result.claimedFromStudent);
		assertEquals(103, s3.result.claimedToCoord);
		assertEquals(334, s3.result.perceivedToStudent);
		assertEquals(104, s3.result.perceivedToCoord);
		assertEquals(97, s3.result.outgoing.get(S1_POS).normalizedToCoord);
		assertEquals(100, s3.result.outgoing.get(S2_POS).normalizedToCoord);
		assertEquals(103, s3.result.outgoing.get(S3_POS).normalizedToCoord);
		assertEquals(310, s3.result.incoming.get(S1_POS).normalized);
		assertEquals(316, s3.result.incoming.get(S2_POS).normalized);
		assertEquals(334, s3.result.incoming.get(S3_POS).normalized);
		assertEquals(104, s3.result.incoming.get(S1_POS).normalizedToCoord);
		assertEquals(105, s3.result.incoming.get(S2_POS).normalizedToCoord);
		assertEquals(NA, s3.result.incoming.get(S3_POS).normalizedToCoord);

	}

	@Test
	public void testPopulateTeamResults() throws Exception {
		// tested in testCalculateTeamResult()
	}

	@Test
	public void testGetSubmissoinsForEvaluation() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");
		
		loginAsAdmin("admin.user");

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		// reuse this evaluation data to create a new one
		evaluation.name = "new evaluation";
		logic.createEvaluation(evaluation);

		HashMap<String, SubmissionData> submissions = invokeGetSubmissionsForEvaluation(
				evaluation.course, evaluation.name);
		// team 1.1 has 4 students, team 1.2 has only 1 student.
		// there should be 4*4+1=17 submissions.
		assertEquals(17, submissions.keySet().size());
		// verify they all belong to this evaluation
		for (String key : submissions.keySet()) {
			assertEquals(evaluation.course, submissions.get(key).course);
			assertEquals(evaluation.name, submissions.get(key).evaluation);
		}

		______TS("evaluation in empty class");

		logic.createCourse("coord1", "course1", "Course 1");
		evaluation.course = "course1";
		logic.createEvaluation(evaluation);

		submissions = invokeGetSubmissionsForEvaluation(evaluation.course,
				evaluation.name);
		assertEquals(0, submissions.keySet().size());

		______TS("non-existent course/evaluation");

		try {
			invokeGetSubmissionsForEvaluation(evaluation.course, "non-existent");
		} catch (Exception e) {
			BaseTestCase.assertContains("non-existent", e.getCause()
					.getMessage());
		}

		try {
			invokeGetSubmissionsForEvaluation("non-existent", evaluation.name);
		} catch (Exception e) {
			BaseTestCase.assertContains("non-existent", e.getCause()
					.getMessage());
		}

		// no need to check for invalid parameters as it is a private method
		// TODO: verify orphan submissions are not returned
	}

	@Test
	public void testGetSubmissionsFromStudent() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("typical case");

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		// reuse this evaluation data to create a new one
		evaluation.name = "new evaluation";
		logic.createEvaluation(evaluation);
		// this is the student we are going to check
		StudentData student = dataBundle.students.get("student1InCourse1");

		List<SubmissionData> submissions = logic.getSubmissionsFromStudent(
				evaluation.course, evaluation.name, student.email);
		// there should be 4 submissions as this student is in a 4-person team
		assertEquals(4, submissions.size());
		// verify they all belong to this student
		for (SubmissionData s : submissions) {
			assertEquals(evaluation.course, s.course);
			assertEquals(evaluation.name, s.evaluation);
			assertEquals(student.email, s.reviewer);
			assertEquals(student.name, s.reviewerName);
			assertEquals(logic.getStudent(evaluation.course, s.reviewee).name,
					s.revieweeName);
		}

		______TS("orphan submissions");

		// TODO: test this after implementing lazy creation

		______TS("null parameters");

		try {
			logic.getSubmissionsFromStudent(null, evaluation.name,
					student.email);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("course id", e.getMessage()
					.toLowerCase());
		}

		try {
			logic.getSubmissionsFromStudent(evaluation.course, null,
					student.email);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("evaluation name", e.getMessage()
					.toLowerCase());
		}

		try {
			logic.getSubmissionsFromStudent(evaluation.course, evaluation.name,
					null);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
			BaseTestCase.assertContains("student email", e.getMessage()
					.toLowerCase());
		}

		______TS("course/evaluation/student does not exist");

		try {
			logic.getSubmissionsFromStudent("non-existent", evaluation.name,
					student.email);
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

		try {
			logic.getSubmissionsFromStudent(evaluation.course, "non-existent",
					student.email);
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}

		try {
			logic.getSubmissionsFromStudent(evaluation.course, evaluation.name,
					"non-existent");
			fail();
		} catch (EntityDoesNotExistException e) {
			BaseTestCase.assertContains("non-existent", e.getMessage());
		}
	}

	@Test
	public void testSendReminderForEvaluation_1() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("empty class");

		loginAsAdmin("admin.user");
		logic.createCourse("coord1", "course1", "course 1");
		EvaluationData newEval = new EvaluationData();
		newEval.course = "course1";
		newEval.name = "new eval";
		newEval.startTime = Common.getDateOffsetToCurrentTime(1);
		newEval.endTime = Common.getDateOffsetToCurrentTime(2);
		logic.createEvaluation(newEval);
		// reuse exisitng evaluation to create a new one
		logic.sendReminderForEvaluation("course1", "new eval");
		assertEquals(0, getNumberOfEmailTasksInQueue());

		______TS("no one has submitted fully");

		EvaluationData eval = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		logic.sendReminderForEvaluation(eval.course, eval.name);

		assertEquals(5, getNumberOfEmailTasksInQueue());
		List<StudentData> studentList = logic
				.getStudentListForCourse(eval.course);

		for (StudentData sd : studentList) {
			verifyRegistrationEmailToStudent(sd);
		}

		// This test continues in the next test case. We had to break it into
		// mulitiple cases to reset email queue before sending reminder
		// emails again.
	}

	@Test
	public void testSendReminderForEvaluation_2() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		______TS("some have submitted fully");
		
		loginAsAdmin("admin.user");

		EvaluationData eval = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		// This student is the only member in Team 1.2. If he submits his
		// self-evaluation, he sill be considered 'fully submitted'. Only
		// student in Team 1.2 should receive emails.
		StudentData singleStudnetInTeam1_2 = dataBundle.students
				.get("student5InCourse1");
		SubmissionData sub = new SubmissionData();
		sub.course = singleStudnetInTeam1_2.course;
		sub.evaluation = eval.name;
		sub.team = singleStudnetInTeam1_2.team;
		sub.reviewer = singleStudnetInTeam1_2.email;
		sub.reviewee = singleStudnetInTeam1_2.email;
		sub.points = 100;
		sub.justification = new Text("j");
		sub.p2pFeedback = new Text("y");
		ArrayList<SubmissionData> submissions = new ArrayList<SubmissionData>();
		submissions.add(sub);
		logic.editSubmissions(submissions);
		logic.sendReminderForEvaluation(eval.course, eval.name);
		// 4 more tasks should be added to the queue (for 4 students in Team1.1)
		assertEquals(4, getNumberOfEmailTasksInQueue());

		List<StudentData> studentList = logic
				.getStudentListForCourse(eval.course);

		// verify all students in Team 1.1 received emails.
		for (StudentData sd : studentList) {
			if (sd.team.equals("Team 1.1")) {
				verifyRegistrationEmailToStudent(sd);
			}
		}
		// TODO: complete this

		______TS("null parameter");

		______TS("non-existent course");

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
		restoreTypicalDataInDatastore();

		______TS("typical case");
		SubmissionData expected = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(expected);

		______TS("null parameters");
		// no need to check for null as this is a private method

		______TS("non-existent");

		assertEquals(
				null,
				invokeGetSubmission("non-existent", expected.evaluation,
						expected.reviewer, expected.reviewee));
		assertEquals(
				null,
				invokeGetSubmission(expected.course, "non-existent",
						expected.reviewer, expected.reviewee));
		assertEquals(
				null,
				invokeGetSubmission(expected.course, expected.evaluation,
						"non-existent", expected.reviewee));
		assertEquals(
				null,
				invokeGetSubmission(expected.course, expected.evaluation,
						expected.reviewer, "non-existent"));

		______TS("lazy creation");
		// This need not be tested here because this method does not have
		// enough information to activate lazy creation.
	}

	@Test
	public void testEditSubmissions() throws Exception {
		printTestCaseHeader();
		restoreTypicalDataInDatastore();

		ArrayList<SubmissionData> submissionContainer = new ArrayList<SubmissionData>();

		// try without empty list. Nothing should happen
		logic.editSubmissions(submissionContainer);

		SubmissionData sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		SubmissionData sub2 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");

		// checking editing of one of the submissions
		alterSubmission(sub1);

		submissionContainer.add(sub1);
		logic.editSubmissions(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		// check editing both submissions
		alterSubmission(sub1);
		alterSubmission(sub2);

		submissionContainer = new ArrayList<SubmissionData>();
		submissionContainer.add(sub1);
		submissionContainer.add(sub2);
		logic.editSubmissions(submissionContainer);

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
	private void ____helper_methods_________________________________________() {
	}

	private void verifyNullParameterDetectedCorrectly(
			InvalidParametersException e, String nameOfNullParameter) {
		assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		BaseTestCase.assertContains(nameOfNullParameter, e.getMessage()
				.toLowerCase());
	}

	private void createNewEvaluationWithSubmissions(String courseId,
			String evaluationName, int[][] input)
			throws EntityAlreadyExistsException, InvalidParametersException,
			EntityDoesNotExistException {
		// create course
		loginAsAdmin("admin.user");
		logic.createCourse("coordForTestingER", courseId,
				"Course For Testing Evaluation Results");
		// create students
		int teamSize = input.length;
		String teamName = "team1";
		for (int i = 0; i < teamSize; i++) {
			StudentData student = new StudentData();
			int studentNumber = i + 1;
			student.email = "s" + studentNumber + "@gmail.com";
			student.name = "Student " + studentNumber;
			student.team = teamName;
			student.course = courseId;
			logic.createStudent(student);
		}
		// create evaluation
		EvaluationData e = new EvaluationData();
		e.course = courseId;
		e.name = evaluationName;
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.gracePeriod = 0;
		e.instructions = "instructions for " + e.name;
		logic.createEvaluation(e);
		// create submissions
		ArrayList<SubmissionData> submissions = new ArrayList<SubmissionData>();
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				SubmissionData sub = new SubmissionData();
				sub.course = courseId;
				sub.evaluation = e.name;
				sub.team = teamName;
				int reviewerNumber = i + 1;
				sub.reviewer = "s" + reviewerNumber + "@gmail.com";
				int revieweeNumber = j + 1;
				sub.reviewee = "s" + revieweeNumber + "@gmail.com";
				sub.points = input[i][j];
				sub.justification = new Text("jus[s" + reviewerNumber + "->s"
						+ revieweeNumber + "]");
				sub.p2pFeedback = new Text("p2p[s" + reviewerNumber + "->s"
						+ revieweeNumber + "]");
				submissions.add(sub);
			}
		}
		logic.editSubmissions(submissions);
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
		List<TaskStateInfo> taskInfoList = getTasksInQueue("email-queue");
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

	private void alterSubmission(SubmissionData submission) {
		submission.points = submission.points + 10;
		submission.p2pFeedback = new Text(submission.p2pFeedback.getValue()
				+ "x");
		submission.justification = new Text(submission.justification.getValue()
				+ "y");
	}

	private void verifyAbsentInDatastore(SubmissionData submission)
			throws Exception {
		assertEquals(
				null,
				invokeGetSubmission(submission.course, submission.evaluation,
						submission.reviewer, submission.reviewee));
	}

	private void verifyAbsentInDatastore(CoordData expectedCoord) {
		assertEquals(null, logic.getCoord(expectedCoord.id));
	}

	private void verifyAbsentInDatastore(CourseData course) {
		assertEquals(null, logic.getCourse(course.id));
	}

	private void verifyAbsentInDatastore(StudentData student) {
		assertEquals(null, logic.getStudent(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationData evaluation) {
		assertEquals(null,
				logic.getEvaluation(evaluation.course, evaluation.name));
	}

	public static void verifyPresentInDatastore(StudentData expectedStudent) {
		StudentData actualStudent = logic.getStudent(expectedStudent.course,
				expectedStudent.email);
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

	public static void verifyPresentInDatastore(SubmissionData expected)
			throws Exception {
		SubmissionData actual = invokeGetSubmission(expected.course,
				expected.evaluation, expected.reviewer, expected.reviewee);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(EvaluationData expected) {
		EvaluationData actual = logic.getEvaluation(expected.course,
				expected.name);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(CourseData expected) {
		CourseData actual = logic.getCourse(expected.id);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(CoordData expected) {
		CoordData actual = logic.getCoord(expected.id);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifySameEvaluationData(EvaluationData expected,
			EvaluationData actual) {
		assertEquals(expected.course, actual.course);
		assertEquals(expected.name, actual.name);
		assertSameDates(expected.startTime, actual.startTime);
		assertSameDates(expected.endTime, actual.endTime);
		assertEquals(expected.timeZone, actual.timeZone, 0.1);
		assertEquals(expected.instructions, actual.instructions);
		assertEquals(expected.p2pEnabled, actual.p2pEnabled);
		assertEquals(expected.published, actual.published);
		assertEquals(expected.activated, actual.activated);
	}

	public static boolean isLogEntryInList(
			StudentActionData teamFormingLogEntry,
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
		Method privateMethod = Logic.class.getDeclaredMethod(
				"calculateTeamResult", new Class[] { TeamData.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { team };
		return (TeamEvalResult) privateMethod.invoke(logic, params);
	}

	// TODO: try to generalize invoke*() methods and push to parent class
	private void invokePopulateTeamResult(TeamData team,
			TeamEvalResult teamResult) throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod(
				"populateTeamResult", new Class[] { TeamData.class,
						TeamEvalResult.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { team, teamResult };
		privateMethod.invoke(logic, params);
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, SubmissionData> invokeGetSubmissionsForEvaluation(
			String courseId, String evaluationName) throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod(
				"getSubmissionsForEvaluation", new Class[] { String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { courseId, evaluationName };
		return (HashMap<String, SubmissionData>) privateMethod.invoke(logic,
				params);
	}

	private static SubmissionData invokeGetSubmission(String course,
			String evaluation, String reviewer, String reviewee)
			throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod("getSubmission",
				new Class[] { String.class, String.class, String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { course, evaluation, reviewer, reviewee };
		return (SubmissionData) privateMethod.invoke(logic, params);
	}

	private void verifyCannotAccess(int userType, String methodName,
			String userId, Class<?>[] paramTypes, Object[] params)
			throws Exception {
		verifyAccessLevel(false, userType, methodName, userId, paramTypes,
				params);
	}

	private void verifyCanAccess(int userType, String methodName,
			String userId, Class<?>[] paramTypes, Object[] params)
			throws Exception {
		verifyAccessLevel(true, userType, methodName, userId, paramTypes,
				params);
	}

	private void verifyAccessLevel(boolean allowed, int userType,
			String methodName, String userId, Class<?>[] paramTypes,
			Object[] params) throws Exception {
		Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);
		switch (userType) {
		case USER_TYPE_NOT_LOGGED_IN:
			break;
		case USER_TYPE_UNREGISTERED:
			loginUser(userId);
			break;
		case USER_TYPE_STUDENT:
			loginAsStudent(userId);
			break;
		case USER_TYPE_COORD:
			loginAsCoord(userId);
			break;
		}

		try {
			method.invoke(logic, params);
			if (!allowed) {
				fail();
			}
		} catch (Exception e) {
			if (!allowed) {
				assertEquals(UnauthorizedAccessException.class, e.getCause()
						.getClass());
			}
		}
	}

	private static SubmissionData createSubmission(int from, int to) {
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

	private void setPointsForSubmissions(int[][] points) throws Exception {
		int teamSize = points.length;
		ArrayList<SubmissionData> submissions = new ArrayList<SubmissionData>();
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				SubmissionData s = invokeGetSubmission("idOfCourse1OfCoord1",
						"evaluation1 In Course1", "student" + (i + 1)
								+ "InCourse1@gmail.com", "student" + (j + 1)
								+ "InCourse1@gmail.com");
				s.points = points[i][j];
				submissions.add(s);
			}
		}
		logic.editSubmissions(submissions);
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(Logic.class);
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

}
