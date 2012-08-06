package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static teammates.logic.TeamEvalResult.NA;
import static teammates.logic.TeamEvalResult.NSB;
import static teammates.logic.TeamEvalResult.NSU;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvalResultData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.EvaluationData.EvalStatus;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.StudentData.UpdateStatus;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.datatransfer.TeamData;
import teammates.common.datatransfer.UserData;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.Emails;
import teammates.logic.TeamEvalResult;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.api.EvaluationsStorage;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Student;
import teammates.storage.entity.Submission;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;

public class LogicTest extends BaseTestCase {

	final static Logic logic = new Logic();

	// these are used for access control checking for different types of users
	private static final int USER_TYPE_NOT_LOGGED_IN = -1;
	private static final int USER_TYPE_UNREGISTERED = 0;
	private static final int USER_TYPE_STUDENT = 1;
	private static final int USER_TYPE_COORD = 2;

	private static Gson gson = Common.getTeammatesGson();

	private static DataBundle dataBundle = getTypicalDataBundle();

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Logic.class);
		Datastore.initialize();
	}

	@Before
	public void caseSetUp() throws ServletException {
		dataBundle = getTypicalDataBundle();

		LocalTaskQueueTestConfig localTasks = new LocalTaskQueueTestConfig();
		LocalUserServiceTestConfig localUserServices = new LocalUserServiceTestConfig();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		LocalMailServiceTestConfig localMail = new LocalMailServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore, localMail,
				localUserServices, localTasks);
		setHelperTimeZone(helper);
		helper.setUp();
	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods___________________________________() {
	}

	@Test
	public void testCoordGetLoginUrl() {

		assertEquals("/_ah/login?continue=www.abc.com",
				Logic.getLoginUrl("www.abc.com"));
	}

	@Test
	public void testCoordGetLogoutUrl() {

		assertEquals("/_ah/logout?continue=www.def.com",
				Logic.getLogoutUrl("www.def.com"));
	}

	@Test
	public void testGetLoggedInUser() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("admin+coord+student");

		CoordData coord = dataBundle.coords.get("typicalCoord1");
		loginAsAdmin(coord.id);
		// also make this user a student
		StudentData coordAsStudent = new StudentData(
				"|Coord As Student|coordasstudent@yahoo.com|", "some-course");
		coordAsStudent.id = coord.id;
		logic.createStudent(coordAsStudent);

		UserData user = logic.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(true, user.isStudent);

		______TS("admin+coord only");

		// this user is no longer a student
		logic.deleteStudent(coordAsStudent.course, coordAsStudent.email);

		user = logic.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(false, user.isStudent);

		______TS("coord only");
		// this user is no longer an admin
		helper.setEnvIsAdmin(false);

		user = logic.getLoggedInUser();
		assertEquals(coord.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(true, user.isCoord);
		assertEquals(false, user.isStudent);

		______TS("unregistered");

		loginUser("unknown");

		user = logic.getLoggedInUser();
		assertEquals("unknown", user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(false, user.isStudent);

		______TS("student only");

		StudentData student = dataBundle.students.get("student1InCourse1");
		loginAsStudent(student.id);

		user = logic.getLoggedInUser();
		assertEquals(student.id, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(true, user.isStudent);

		______TS("admin only");

		loginAsAdmin("any.user");

		user = logic.getLoggedInUser();
		assertEquals("any.user", user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(false, user.isCoord);
		assertEquals(false, user.isStudent);

		______TS("not logged in");

		// check for user not logged in
		logoutUser();
		assertEquals(null, logic.getLoggedInUser());
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods____________________________________() {
	}

	@Test
	public void testCreateCoord() throws Exception {

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

		// We don't check admin access because if it doesn't affect normal users
		// i.e., admin is part of the development team.

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

		// We check one invalid value for each parameter. Other combinations
		// of invalid values should be checked at lower levels.

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

		restoreTypicalDataInDatastore();

		______TS("authentication");

		Class<?>[] paramTypes = new Class[] { String.class };
		Object[] params = new Object[] { "id" };
		String methodName = "getCoord";

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, null,
				paramTypes, params);

		verifyCanAccess(USER_TYPE_UNREGISTERED, methodName,
				"student1InCourse1", paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("null parameter");

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null });

	}

	@Test
	public void testEditCoord() {
		// method not implemented
	}

	@Test
	public void testDeleteCoord() throws Exception {

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

		______TS("null parameter");

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null });

	}

	@Test
	public void testGetCourseListForCoord() throws Exception {

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

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null });

		______TS("non-existent coord");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@Test
	public void testGetCourseDetailsListForCoord() throws Exception {

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

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null });

		______TS("non-existent coord");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@Test
	public void testGetEvalListForCoord() throws Exception {

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
		EvaluationData evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		for (EvaluationData ed : evalList) {
			assertTrue(ed.course.contains("Coord1"));
			if(ed.name.equals(evaluation.name)){
				//We have, 4 students in Team 1.1 and 1 student in Team 1.2
				//Only 3 have submitted.
				assertEquals(5,ed.expectedTotal);
				assertEquals(3,ed.submittedTotal);
			}
		}
		
		______TS("check immunity from orphaned submissions");
		
		//move a student from Team 1.1 to Team 1.2
		StudentData student = dataBundle.students.get("student4InCourse1");
		student.team = "Team 1.2";
		logic.editStudent(student.email, student);
		
		evalList = logic.getEvaluationsListForCoord(coord1.id);
		assertEquals(3, evalList.size());
		
		for (EvaluationData ed : evalList) {
			if(ed.name.equals(evaluation.name)){
				//Now we have, 3 students in Team 1.1 and 2 student in Team 1.2
				//Only 2 (1 less than before) have submitted 
				//   because we just moved a student to a new team and that
				//   student's previous submissions are now orphaned.
				assertEquals(5,ed.expectedTotal);
				assertEquals(2,ed.submittedTotal);
			}
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

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null });

		______TS("non-existent coord");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods___________________________________() {
	}

	@Test
	public void testCreateCourse() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("authentication");

		String methodName = "createCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class };
		Object[] params = new Object[] { "idOfTypicalCoord1", "new-course",
				"New Course" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// coord does not own the given coordId
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "diff-id", "new-course",
						"New Course" });

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
		// CourseData.validate()

		______TS("null parameters");

		verifyNullPointerException(methodName, "coordinator ID", paramTypes,
				new Object[] { null, "new-course", "New Course" });

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { "idOfTypicalCoord1", null, "New Course" });

		verifyNullPointerException(methodName, "course name", paramTypes,
				new Object[] { "idOfTypicalCoord1", "new-course", null });

	}

	@Test
	public void testGetCourse() throws Exception {
		// mostly tested in testCreateCourse
		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });

	}

	@Test
	public void testGetCourseDetails() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getCourseDetails";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// student in different course
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse2",
				paramTypes, params);

		// student in same course
		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2" });

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

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });

		______TS("null parameter");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });

	}

	@Test
	public void testEditCourse() {
		// method not implemented
	}

	@Test
	public void testDeleteCourse() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "deleteCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2" });

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

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testGetStudentListForCourse() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getStudentListForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2" });

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

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });

		______TS("non-existent course");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@Test
	public void testEnrollStudents() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "enrollStudents";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "t|n|e@c|c", "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "t|n|e@c|c", "idOfCourse1OfCoord2" });

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

		// no changes should be done to the database
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

		verifyNullPointerException(methodName, "enrollment text", paramTypes,
				new Object[] { null, courseId });

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { "any text", null });

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

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "sendRegistrationInviteForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2" });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("all students already registered");

		loginAsAdmin("admin.user");

		restoreTypicalDataInDatastore();
		CourseData course1 = dataBundle.courses.get("course1OfCoord1");

		// send registration key to a class in which all are registered
		List<MimeMessage> emailsSent = logic
				.sendRegistrationInviteForCourse(course1.id);
		assertEquals(0, emailsSent.size());

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
		emailsSent = logic.sendRegistrationInviteForCourse(course1.id);
		assertEquals(2, emailsSent.size());
		verifyJoinInviteToStudent(student2InCourse1, emailsSent.get(0));
		verifyJoinInviteToStudent(student1InCourse1, emailsSent.get(1));

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testGetTeamsForCourse() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getTeamsForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// student not in same course
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse2",
				paramTypes, params);

		// student in same course
		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2" });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

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

		______TS("without loners");

		// TODO: remove this if we don't allow loners

		restoreTypicalDataInDatastore();
		courseAsTeams = logic.getTeamsForCourse(course.id);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(0, courseAsTeams.loners.size());

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null });

		______TS("course without teams");

		logic.createCourse("coord1", "course1", "Course 1");
		assertEquals(0, logic.getTeamsForCourse("course1").teams.size());

		______TS("non-existent course");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	@Test
	public void testCreateStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "createStudent";
		Class<?>[] paramTypes = new Class<?>[] { StudentData.class };
		StudentData s = new StudentData("t|n|e@com|c", "idOfCourse1OfCoord1");
		Object[] params = new Object[] { s };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { new StudentData("t|n|e@com|c",
						"idOfCourse1OfCoord2") });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		loginAsAdmin("admin.user");
		
		restoreTypicalDataInDatastore();
		//reuse existing student to create a new student
		StudentData newStudent = dataBundle.students.get("student1InCourse1");
		newStudent.email = "new@student.com";
		verifyAbsentInDatastore(newStudent);
		
		List<Submission> submissionsBeforeAdding = EvaluationsStorage.inst().getSubmissionList(newStudent.course);
		
		logic.createStudent(newStudent);
		verifyPresentInDatastore(newStudent);
		
		List<Submission> submissionsAfterAdding = EvaluationsStorage.inst().getSubmissionList(newStudent.course);
		
		//expected increase in submissions = 2*(1+4+4)
		//2 is the number of evaluations in the course
		//4 is the number of existing members in the team
		//1 is the self evaluation
		//We simply check the increase in submissions. A deeper check is 
		//  unnecessary because adjusting existing submissions should be 
		//  checked elsewhere.
		assertEquals(submissionsBeforeAdding.size()+18, submissionsAfterAdding.size());

		______TS("duplicate student");

		// try to create the same student
		try {
			logic.createStudent(newStudent);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		______TS("null parameter");

		verifyNullPointerException(methodName, "student data", paramTypes,
				new Object[] { null });

		// other combination of invalid data should be tested against
		// StudentData

	}

	@Test
	public void testGetStudent() throws Exception {
		// mostly tested in testCreateStudent

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord2",
				"student1InCourse1@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2", "e@c.com" });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, "email@email.com" });

		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { "course-id", null });
	}

	@Test
	public void testEditStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "editStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class,
				StudentData.class };
		StudentData s = new StudentData("t|n|e@com|c", "idOfCourse1OfCoord1");
		Object[] params = new Object[] { "student1InCourse1@gmail.com", s };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "student1InCourse1@gmail.com",
						new StudentData("t|n|e@com|c", "idOfCourse1OfCoord2") });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical edit");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

		StudentData student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		verifyPresentInDatastore(student1InCourse1);
		String originalEmail = student1InCourse1.email;
		student1InCourse1.name = student1InCourse1.name + "x";
		student1InCourse1.id = student1InCourse1.id + "x";
		student1InCourse1.comments = student1InCourse1.comments + "x";
		String newEmail = student1InCourse1.email + "x";
		student1InCourse1.email = newEmail;
		student1InCourse1.team = "Team 1.2"; // move to a different team
		student1InCourse1.profile = new Text("new profile detail abc ");

		// take a snapshot of submissions before
		List<SubmissionData> submissionsBeforeEdit = EvaluationsStorage.inst()
				.getSubmissionsForCourse(student1InCourse1.course);

		//verify student details changed correctly
		logic.editStudent(originalEmail, student1InCourse1);
		verifyPresentInDatastore(student1InCourse1);

		// take a snapshot of submissions after the edit
		List<SubmissionData> submissionsAfterEdit = EvaluationsStorage.inst()
				.getSubmissionsForCourse(student1InCourse1.course);
		
		// We moved a student from a 4-person team to an existing 1-person team.
		// We have 2 evaluations in the course.
		// Therefore, submissions that will be deleted = 7*2 = 14
		//              submissions that will be added = 3*2
		assertEquals(submissionsBeforeEdit.size() - 14  + 6,
				submissionsAfterEdit.size()); 
		
		// verify new submissions were created to match new team structure
		verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
				student1InCourse1.course);

		______TS("check for KeepExistingPolicy");

		// try changing email only
		StudentData copyOfStudent1 = new StudentData();
		copyOfStudent1.course = student1InCourse1.course;
		originalEmail = student1InCourse1.email;

		newEmail = student1InCourse1.email + "y";
		student1InCourse1.email = newEmail;
		copyOfStudent1.email = newEmail;

		logic.editStudent(originalEmail, copyOfStudent1);
		verifyPresentInDatastore(student1InCourse1);

		______TS("non-existent student");

		student1InCourse1.course = "new-course";
		verifyAbsentInDatastore(student1InCourse1);

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				originalEmail, student1InCourse1 });

		______TS("null parameters");

		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { null,
						new StudentData("t|n|e@com|c", "idOfCourse1OfCoord2") });

		verifyNullPointerException(methodName, "student object", paramTypes,
				new Object[] { "student1InCourse1@gmail.com", null });

		// no need to check for cascade delete/creates due to LazyCreationPolicy
		// and TolerateOrphansPolicy.
	}

	@Test
	public void testDeleteStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "deleteStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"student1InCourse1@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2",
						"student1InCourse2@gmail.com" });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical delete");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

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

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, student1InCourse1.email });

		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { student1InCourse1.course, null });
	}

	@Test
	public void testSendRegistrationInviteToStudent() throws Exception {

		// Authentication testing is moved to the bottom of this method
		// to avoid interfering the email queue.

		______TS("send to existing student");

		loginAsAdmin("admin.user");

		restoreTypicalDataInDatastore();

		StudentData student1 = dataBundle.students.get("student1InCourse1");

		MimeMessage email = logic.sendRegistrationInviteToStudent(
				student1.course, student1.email);

		verifyJoinInviteToStudent(student1, email);

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "sendRegistrationInviteToStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"student1InCourse1@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "idOfCourse1OfCoord2",
						"student1InCourse2@gmail.com" });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("send to non-existing student");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				student1.course, "non@existent" });

		______TS("null parameters");

		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { student1.course, null });

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, student1.email });
	}

	@Test
	public void testGetStudentWithId() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getStudentsWithId";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "student1InCourse1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// different student
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		// same student
		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("student in one course");

		loginAsAdmin("admin.user");

		restoreTypicalDataInDatastore();
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

		assertEquals(0, logic.getStudentsWithId("non-existent").size());

		______TS("null parameters");

		verifyNullPointerException(methodName, "Google ID", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testEnrollStudent() throws Exception {

		// private method. no need to test authentication

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
		StudentData enrollmentResult = invokeEnrollStudent(student1);
		assertEquals(1, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.NEW);
		verifyPresentInDatastore(student1);

		______TS("add existing student");

		// Verify it was not added
		enrollmentResult = invokeEnrollStudent(student1);
		verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentData.UpdateStatus.UNMODIFIED);

		______TS("modify info of existing student");

		// verify it was treated as modified
		StudentData student2 = dataBundle.students.get("student1InCourse1");
		student2.name = student2.name + "y";
		StudentData studentToEnroll = new StudentData(student2.email,
				student2.name, student2.comments, student2.course,
				student2.team);
		enrollmentResult = invokeEnrollStudent(studentToEnroll);
		verifyEnrollmentResultForStudent(studentToEnroll, enrollmentResult,
				StudentData.UpdateStatus.MODIFIED);
		// check if the student is actually modified in datastore and existing
		// values not specified in enroll action (e.g, id) prevail
		verifyPresentInDatastore(student2);

		______TS("add student into non-empty course");

		StudentData student3 = new StudentData("t3|n3|e3@g|c3", courseId);
		enrollmentResult = invokeEnrollStudent(student3);
		assertEquals(2, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student3, enrollmentResult,
				StudentData.UpdateStatus.NEW);

		______TS("add student without team");

		StudentData student4 = new StudentData("|n4|e4@g", courseId);
		enrollmentResult = invokeEnrollStudent(student4);
		assertEquals(3, logic.getStudentListForCourse(courseId).size());
		verifyEnrollmentResultForStudent(student4, enrollmentResult,
				StudentData.UpdateStatus.NEW);
	}

	@Test
	public void testGetStudentInCourseForGoogleId() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getStudentInCourseForGoogleId";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"student1InCourse1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// different student
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		// same student
		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("student in two courses");

		restoreTypicalDataInDatastore();
		loginAsAdmin("admin.user");
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

		______TS("student in zero courses");

		assertEquals(null, logic.getStudentInCourseForGoogleId("non-existent",
				"random-google-id"));

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, "not-null" });
		verifyNullPointerException(methodName, "Google ID", paramTypes,
				new Object[] { "not-null", null });
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

		______TS("authentication");

		restoreTypicalDataInDatastore();

		// make a student 'unregistered'
		loginAsAdmin("admin.user");
		StudentData student = dataBundle.students.get("student1InCourse1");
		String googleId = "student1InCourse1";
		String key = logic.getKeyForStudent(student.course, student.email);
		student.id = "";
		logic.editStudent(student.email, student);
		assertEquals("", logic.getStudent(student.course, student.email).id);

		String methodName = "joinCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "some.user", key };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		// not the owner of googleId
		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "other.user",
				paramTypes, params);

		// owner of googleId
		verifyCanAccess(USER_TYPE_UNREGISTERED, methodName, "some.user",
				paramTypes, params);

		______TS("register an unregistered student");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

		// make a student 'unregistered'
		student = dataBundle.students.get("student1InCourse1");
		googleId = "student1InCourse1";
		key = logic.getKeyForStudent(student.course, student.email);
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

		verifyNullPointerException(methodName, "key", paramTypes, new Object[] {
				googleId, null });
		verifyNullPointerException(methodName, "Google ID", paramTypes,
				new Object[] { null, "fdsfsfsfsdfsf" });
	}

	@Test
	public void testGetKeyForStudent() throws Exception {
		// mostly tested in testJoinCourse()

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getKeyForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"student1InCourse1@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("null parameters");

		StudentData student = dataBundle.students.get("student1InCourse1");

		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { student.course, null });
		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, student.email });

		______TS("non-existent student");

		assertEquals(null,
				logic.getKeyForStudent(student.course, "non@existent"));
	}

	@Test
	public void testGetCourseListForStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getCourseListForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "student1InCourse1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// not the owner of the id
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		______TS("student having two courses");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

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

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });

		______TS("null parameter");

		verifyNullPointerException(methodName, "Google ID", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testHasStudentSubmittedEvaluation() throws Exception {

		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		StudentData student = dataBundle.students.get("student1InCourse1");

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "hasStudentSubmittedEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class };
		Object[] params = new Object[] { evaluation.course, evaluation.name,
				student.email };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		______TS("student has submitted");

		loginAsAdmin("admin.user");

		assertEquals(true, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, student.email));

		______TS("student has not submitted");

		// create a new evaluation reusing data from previous one
		evaluation.name = "New evaluation";
		logic.createEvaluation(evaluation);
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, student.email));

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, evaluation.name, student.email });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { evaluation.course, null, student.email });
		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { evaluation.course, evaluation.name, null });

		______TS("non-existent course/evaluation/student");

		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				"non-existent-course", evaluation.name, student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, "non-existent-eval", student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.course, evaluation.name, "non-existent@student"));

	}

	@Test
	public void testGetCourseDetailsListForStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getCourseDetailsListForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		Object[] params = new Object[] { "student1InCourse1" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// not the owner of the id
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		______TS("student having multiple evaluations in multiple courses");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

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
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(expectedEval1InCourse1);

		expectedEval2InCourse1.startTime = Common
				.getDateOffsetToCurrentTime(-1);
		expectedEval2InCourse1.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, expectedEval2InCourse1.getStatus());
		backDoorLogic.editEvaluation(expectedEval2InCourse1);

		// Make sure all evaluations in course2 are still AWAITING
		expectedEval1InCourse2.startTime = Common.getDateOffsetToCurrentTime(1);
		expectedEval1InCourse2.endTime = Common.getDateOffsetToCurrentTime(2);
		expectedEval1InCourse2.activated = false;
		assertEquals(EvalStatus.AWAITING, expectedEval1InCourse2.getStatus());
		backDoorLogic.editEvaluation(expectedEval1InCourse2);

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

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });

		______TS("null parameter");

		verifyNullPointerException(methodName, "Google ID", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testGetEvauationResultForStudent() throws Exception {

		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		String student1email = "student1InCourse1@gmail.com";

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getEvaluationResultForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class };
		Object[] params = new Object[] { course.id, evaluation.name,
				student1email };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// student cannot access because evaluation is not published
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, new Object[] { "course1OfCoord2", evaluation.name,
						student1email });

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		// publish the evaluation
		loginAsAdmin("admin.user");
		evaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		evaluation.published = true;
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(evaluation);
		logoutUser();

		// other students still cannot access this student's result
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		// but this student can now access his own result
		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		______TS("typical case");

		// reconfigure points of an existing evaluation in the datastore
		restoreTypicalDataInDatastore();
		course = dataBundle.courses.get("course1OfCoord1");
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		student1email = "student1InCourse1@gmail.com";

		loginAsAdmin("admin.user");

		// @formatter:off
		setPointsForSubmissions(new int[][] { { 100, 100, 100, 100 },
				{ 110, 110, NSU, 110 }, { NSB, NSB, NSB, NSB },
				{ 70, 80, 110, 120 } });
		// @formatter:on

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

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, "eval name", "e@gmail.com" });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { "course-id", null, "e@gmail.com" });
		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { "course-id", "eval name", null });

		______TS("non-existent course");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent-course", evaluation.name, student1email });

		______TS("non-existent evaluation");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				course.id, "non existent eval", student1email });

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

		// testCreateStudent verifies adding student mid-evaluation creates
		//   additional submissions correctly. No need to check here.

	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods_______________________________() {

	}

	@Test
	public void testCreateEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "createEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { EvaluationData.class };
		EvaluationData evaluation = new EvaluationData();
		evaluation.course = "idOfCourse1OfCoord1";
		evaluation.name = "new evaluation";
		Object[] params = new Object[] { evaluation };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		loginAsAdmin("admin.user");

		restoreTypicalDataInDatastore();

		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
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

		______TS("null parameters");

		verifyNullPointerException(methodName, "evaluation", paramTypes,
				new Object[] { null });

		______TS("invalid parameters");

		evaluation.name = evaluation.name + "new";
		evaluation.course = null;
		try {
			logic.createEvaluation(evaluation);
			fail();
		} catch (NullPointerException e) {
		}
		// invalid values to other parameters should be checked against
		// EvaluationData.validate();

	}

	@Test
	public void testGetEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord2", "eval name" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		EvaluationData expected = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		EvaluationData actual = logic.getEvaluation(expected.course,
				expected.name);
		verifySameEvaluationData(expected, actual);

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, expected.name });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { expected.course, null });

		______TS("non-existent");

		assertEquals(null, logic.getEvaluation("non-existent", expected.name));
		assertEquals(null, logic.getEvaluation(expected.course, "non-existent"));

	}

	@Test
	public void testEditEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "editEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class, Date.class, Date.class, Double.TYPE,
				Integer.TYPE, Boolean.TYPE };
		EvaluationData eval = new EvaluationData();
		eval.course = "idOfCourse1OfCoord1";
		eval.name = "new evaluation";
		eval.instructions = "inst";
		Date dummyTime = Calendar.getInstance().getTime();
		eval.startTime = dummyTime;
		eval.endTime = dummyTime;

		Object[] params = new Object[] { eval.course, eval.name,
				eval.instructions, eval.startTime, eval.endTime, eval.timeZone,
				eval.gracePeriod, eval.p2pEnabled };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

		eval = dataBundle.evaluations.get("evaluation1InCourse1OfCoord1");
		eval.gracePeriod = eval.gracePeriod + 1;
		eval.instructions = eval.instructions + "x";
		eval.p2pEnabled = (!eval.p2pEnabled);
		eval.startTime = Common.getDateOffsetToCurrentTime(-1);
		eval.endTime = Common.getDateOffsetToCurrentTime(2);
		invokeEditEvaluation(eval);

		// flip back these fields because it is not supposed to change
		verifyPresentInDatastore(eval);

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, "not null", "not null", dummyTime,
						dummyTime, 0, 0, false });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { "course-id", null, "not null", dummyTime,
						dummyTime, 0, 0, false });
		verifyNullPointerException(methodName, "starting time", paramTypes,
				new Object[] { "course-id", "not null", "not null", null,
						dummyTime, 0, 0, false });
		verifyNullPointerException(methodName, "deadline", paramTypes,
				new Object[] { "course-id", "not null", "not null", dummyTime,
						null, 0, 0, false });

		______TS("invalid parameters");

		// make the evaluation invalid (end time is before start time)
		eval.startTime = Common.getDateOffsetToCurrentTime(1);
		eval.endTime = Common.getDateOffsetToCurrentTime(0);
		try {
			invokeEditEvaluation(eval);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_END_BEFORE_START, e.errorCode);
		}

		// Checking for other type of invalid parameter situations
		// is done in EvaluationDataTest

	}

	@Test
	public void testDeleteEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "deleteEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"new evaluation" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical delete");

		restoreTypicalDataInDatastore();
		loginAsAdmin("admin.user");

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

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, eval.name });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { eval.course, null });

		______TS("non-existent");

		// should fail silently
		logic.deleteEvaluation("non-existent", eval.name);
		logic.deleteEvaluation(eval.course, "non-existent");

	}

	@Test
	public void testPublishAndUnpublishEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String[] methodNames = new String[] { "publishEvaluation",
				"unpublishEvaluation" };
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"new evaluation" };

		// check access control for both methods
		for (int i = 0; i < methodNames.length; i++) {
			verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodNames[i],
					"any.user", paramTypes, params);

			verifyCannotAccess(USER_TYPE_UNREGISTERED, methodNames[i],
					"any.user", paramTypes, params);

			verifyCannotAccess(USER_TYPE_STUDENT, methodNames[i],
					"student1InCourse1", paramTypes, params);

			// course belongs to a different coord
			verifyCannotAccess(USER_TYPE_COORD, methodNames[i],
					"idOfTypicalCoord2", paramTypes, params);

			verifyCanAccess(USER_TYPE_COORD, methodNames[i],
					"idOfTypicalCoord1", paramTypes, params);
		}

		______TS("typical cases");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

		EvaluationData eval1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		// ensure not published yet
		assertEquals(false,
				logic.getEvaluation(eval1.course, eval1.name).published);
		// ensure CLOSED
		eval1.endTime = Common.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, eval1.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(eval1);

		logic.publishEvaluation(eval1.course, eval1.name);
		assertEquals(true,
				logic.getEvaluation(eval1.course, eval1.name).published);

		logic.unpublishEvaluation(eval1.course, eval1.name);
		assertEquals(false,
				logic.getEvaluation(eval1.course, eval1.name).published);

		______TS("not ready for publishing");

		// make the evaluation OPEN
		eval1.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, eval1.getStatus());
		backDoorLogic.editEvaluation(eval1);

		try {
			logic.publishEvaluation(eval1.course, eval1.name);
			fail();
		} catch (InvalidParametersException e) {
			assertContains(Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING,
					e.errorCode);
		}

		// ensure evaluation stays in the same state
		assertEquals(EvalStatus.OPEN,
				logic.getEvaluation(eval1.course, eval1.name).getStatus());

		______TS("not ready for unpublishing");

		try {
			logic.unpublishEvaluation(eval1.course, eval1.name);
			fail();
		} catch (InvalidParametersException e) {
			assertContains(Common.ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING,
					e.errorCode);
		}

		// ensure evaluation stays in the same state
		assertEquals(EvalStatus.OPEN,
				logic.getEvaluation(eval1.course, eval1.name).getStatus());

		______TS("non-existent");

		for (int i = 0; i < params.length; i++) {
			verifyEntityDoesNotExistException(methodNames[i], paramTypes,
					new Object[] { "non-existent", "non-existent" });
		}
		______TS("null parameters");

		for (int j = 0; j < methodNames.length; j++) {
			verifyNullPointerException(methodNames[j], "course ID", paramTypes,
					new Object[] { null, "random" });
			verifyNullPointerException(methodNames[j], "evaluation name",
					paramTypes, new Object[] { "random", null });
		}

	}

	@Test
	public void testSendEvaluationPublishedEmails() throws Exception {
		// private method. no need to check for authentication.

		loginAsAdmin("admin.user");
		restoreTypicalDataInDatastore();
		dataBundle = getTypicalDataBundle();

		EvaluationData e = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");

		List<MimeMessage> emailsSent = invokeSendEvaluationPublishedEmails(
				e.course, e.name);
		assertEquals(5, emailsSent.size());

		List<StudentData> studentList = logic.getStudentListForCourse(e.course);

		for (StudentData s : studentList) {
			String errorMessage = "No email sent to " + s.email;
			MimeMessage emailToStudent = getEmailToStudent(s, emailsSent);
			assertTrue(errorMessage, emailToStudent != null);
			assertContains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED,
					emailToStudent.getSubject());
			assertContains(e.name, emailToStudent.getSubject());
		}
	}

	@Test
	public void testGetEvaluationResult() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getEvaluationResult";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"new evaluation" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

		// reconfigure points of an existing evaluation in the datastore
		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");

		// @formatter:off
		setPointsForSubmissions(new int[][] { 
				{ 100, 100, 100, 100 },
				{ 110, 110, NSU, 110 }, 
				{ NSB, NSB, NSB, NSB },
				{ 70, 80, 110, 120 } });
		// @formatter:on

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

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, evaluation.name });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { course.id, null });

		______TS("non-existent course");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				course.id, "non existent evaluation" });

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent-course", "any name" });

		______TS("data used in UI tests");

		// @formatter:off

		createNewEvaluationWithSubmissions("courseForTestingER", "Eval 1",
				new int[][] { 
				{ 110, 100, 110 }, 
				{  90, 110, NSU },
				{  90, 100, 110 } });
		// @formatter:on

		result = logic.getEvaluationResult("courseForTestingER", "Eval 1");
		print(result.toString());

	}

	@Test
	public void testCalculateTeamResult() throws Exception {

		try {
			invokeCalculateTeamResult(null);
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getCause().getClass());
		}

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

		TeamEvalResult teamResult = invokeCalculateTeamResult(team);
		// note the pattern in numbers. due to the way we generate submissions,
		// 110 means it is from s1 to s1 and
		// should appear in the 1,1 location in the matrix.
		// @formatter:off
		int[][] expected = { { 110, 120, 130 }, { 210, 220, 230 },
				{ 310, 320, 330 } };
		assertEquals(TeamEvalResult.pointsToString(expected),
				TeamEvalResult.pointsToString(teamResult.claimed));

		// expected result
		// claimedToCoord [ 92, 100, 108]
		// [ 95, 100, 105]
		// [ 97, 100, 103]
		// ===============
		// unbiased [ NA, 96, 104]
		// [ 95, NA, 105]
		// [ 98, 102, NA]
		// ===============
		// perceivedToCoord [ 97, 99, 105]
		// ===============
		// perceivedToStudents [116, 118, 126]
		// [213, 217, 230]
		// [309, 316, 335]
		// @formatter:on

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
		// Team 1.1 has 4 students, Team 1.2 has only 1 student.
		// There should be 4*4+1=17 submissions.
		assertEquals(17, submissions.keySet().size());
		// verify they all belong to this evaluation
		for (String key : submissions.keySet()) {
			assertEquals(evaluation.course, submissions.get(key).course);
			assertEquals(evaluation.name, submissions.get(key).evaluation);
		}
		
		______TS("orphan submissions");
		
		// move student from Team 1.1 to Team 1.2
		StudentData student = dataBundle.students.get("student1InCourse1");
		student.team = "Team 1.2";
		logic.editStudent(student.email, student);

		// Now, team 1.1 has 3 students, team 1.2 has 2 student.
		// There should be 3*3+2*2=13 submissions if no orphans are returned.
		submissions = invokeGetSubmissionsForEvaluation(evaluation.course,
				evaluation.name);
		assertEquals(13, submissions.keySet().size());
		
		// Check if the returned submissions match the current team structure
		List<StudentData> students = logic
				.getStudentListForCourse(evaluation.course);
		verifySubmissionsExistForCurrentTeamStructureInEvaluation(
				evaluation.name, students, new ArrayList<SubmissionData>(
						submissions.values()));

		______TS("evaluation in empty class");

		logic.createCourse("coord1", "course1", "Course 1");
		evaluation.course = "course1";
		logic.createEvaluation(evaluation);

		submissions = invokeGetSubmissionsForEvaluation(evaluation.course,
				evaluation.name);
		assertEquals(0, submissions.keySet().size());

		______TS("non-existent course/evaluation");

		String methodName = "getSubmissionsForEvaluation";
		Class<?>[] paramTypes = new Class[] { String.class, String.class };

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				evaluation.course, "non-existent" });
		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent", evaluation.name });

		// no need to check for invalid parameters as it is a private method
	}

	@Test
	public void testGetSubmissionsFromStudent() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "getSubmissionsFromStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"new evaluation", "student1InCourse1@gmail.com" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// not the reviewer
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");

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

		//Move student to a new team
		student.team = "Team 1.3";
		logic.editStudent(student.email, student);
		
		submissions = logic.getSubmissionsFromStudent(
				evaluation.course, evaluation.name, student.email);
		//There should be 1 submission as he is now in a 1-person team.
		//   Orphaned submissions from previous team should not be returned.
				assertEquals(1, submissions.size());
				
		// Move the student out and move in again
		student.team = "Team 1.4";
		logic.editStudent(student.email, student);
		student.team = "Team 1.3";
		logic.editStudent(student.email, student);
		submissions = logic.getSubmissionsFromStudent(evaluation.course,
				evaluation.name, student.email);
		assertEquals(1, submissions.size());

		______TS("null parameters");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, evaluation.name, student.email });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { evaluation.course, null, student.email });
		verifyNullPointerException(methodName, "student email", paramTypes,
				new Object[] { evaluation.course, evaluation.name, null });

		______TS("course/evaluation/student does not exist");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent", evaluation.name, student.email });

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				evaluation.course, "non-existent", student.email });

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				evaluation.course, evaluation.name, "non-existent" });
	}

	@Test
	public void testSendReminderForEvaluation() throws Exception {

		______TS("authentication");

		restoreTypicalDataInDatastore();

		String methodName = "sendReminderForEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfCourse1OfCoord1",
				"new evaluation" };

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("empty class");

		restoreTypicalDataInDatastore();

		loginAsAdmin("admin.user");
		logic.createCourse("coord1", "course1", "course 1");
		EvaluationData newEval = new EvaluationData();
		newEval.course = "course1";
		newEval.name = "new eval";
		newEval.startTime = Common.getDateOffsetToCurrentTime(1);
		newEval.endTime = Common.getDateOffsetToCurrentTime(2);
		logic.createEvaluation(newEval);

		List<MimeMessage> emailsSent = logic.sendReminderForEvaluation(
				"course1", "new eval");
		assertEquals(0, emailsSent.size());

		______TS("1 person submitted fully, 4 others have not");

		EvaluationData eval = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		emailsSent = logic.sendReminderForEvaluation(eval.course, eval.name);

		assertEquals(4, emailsSent.size());
		List<StudentData> studentList = logic
				.getStudentListForCourse(eval.course);

		//student 1 would not recieve email 
		for (StudentData s : studentList) {
			if(!s.name.equals("student1 In Course1")){
				String errorMessage = "No email sent to " + s.email;
				assertTrue(errorMessage, getEmailToStudent(s, emailsSent) != null);
			}
		}

		______TS("some have submitted fully");

		loginAsAdmin("admin.user");

		// This student is the only member in Team 1.2. If he submits his
		// self-evaluation, he sill be considered 'fully submitted'. Only
		// student in Team 1.1 should receive emails.
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
		emailsSent = logic.sendReminderForEvaluation(eval.course, eval.name);

		assertEquals(3, emailsSent.size());

		studentList = logic.getStudentListForCourse(eval.course);

		// verify 3 students in Team 1.1 received emails.
		for (StudentData s : studentList) {
			if (s.team.equals("Team 1.1") && !s.name.equals("student1 In Course1")) {
				String errorMessage = "No email sent to " + s.email;
				assertTrue(errorMessage,
						getEmailToStudent(s, emailsSent) != null);
			}
		}

		______TS("non-existent course/evaluation");

		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent-course", "non-existent-eval" });

		______TS("null parameter");

		verifyNullPointerException(methodName, "course ID", paramTypes,
				new Object[] { null, eval.name });
		verifyNullPointerException(methodName, "evaluation name", paramTypes,
				new Object[] { eval.course, null });
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

		______TS("typical cases");

		restoreTypicalDataInDatastore();
		loginAsAdmin("admin.user");

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

		______TS("non-existent evaluation");

		// already tested under testEditSubmission()

		______TS("authentication");

		// mostly done in testEditSubmission(), we test only one case here

		restoreTypicalDataInDatastore();

		String methodName = "editSubmissions";
		Class<?>[] paramTypes = new Class<?>[] { List.class };
		List<SubmissionData> submissions = new ArrayList<SubmissionData>();
		SubmissionData s = new SubmissionData();
		s.course = "idOfCourse1OfCoord1";
		s.evaluation = "evaluation1 In Course1";
		s.reviewer = "student1InCourse1@gmail.com";
		submissions.add(s);
		Object[] params = new Object[] { submissions };

		// ensure the evaluation is closed
		loginAsAdmin("admin.user");
		EvaluationData evaluation = logic.getEvaluation(s.course, s.evaluation);
		evaluation.startTime = Common.getDateOffsetToCurrentTime(1);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(2);
		evaluation.activated = false;
		assertEquals(EvalStatus.AWAITING, evaluation.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(evaluation);
		logoutUser();

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// not the reviewer
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("null parameter");

		verifyNullPointerException(methodName, "submissions", paramTypes,
				new Object[] { null });
	}

	@Test
	public void testEditSubmission() throws Exception {

		restoreTypicalDataInDatastore();

		String methodName = "editSubmission";
		Class<?>[] paramTypes = new Class<?>[] { SubmissionData.class };
		SubmissionData s = new SubmissionData();
		s.course = "idOfCourse1OfCoord1";
		s.evaluation = "evaluation1 In Course1";
		s.reviewer = "student1InCourse1@gmail.com";
		Object[] params = new Object[] { s };

		// ensure the evaluation is open
		loginAsAdmin("admin.user");
		EvaluationData evaluation = logic.getEvaluation(s.course, s.evaluation);
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());
		logoutUser();

		verifyCannotAccess(USER_TYPE_NOT_LOGGED_IN, methodName, "any.user",
				paramTypes, params);

		verifyCannotAccess(USER_TYPE_UNREGISTERED, methodName, "any.user",
				paramTypes, params);

		// not the reviewer
		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student2InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		// course belongs to a different coord
		verifyCannotAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord2",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		// close the evaluation
		loginAsAdmin("admin.user");
		evaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(evaluation);
		logoutUser();

		// verify reviewer cannot edit anymore but coord can

		verifyCannotAccess(USER_TYPE_STUDENT, methodName, "student1InCourse1",
				paramTypes, params);

		verifyCanAccess(USER_TYPE_COORD, methodName, "idOfTypicalCoord1",
				paramTypes, params);

		______TS("typical case");

		restoreTypicalDataInDatastore();

		SubmissionData sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		alterSubmission(sub1);

		invokeEditSubmission(sub1);

		verifyPresentInDatastore(sub1);

		______TS("null parameter");

		verifyNullPointerException(methodName, "submission", paramTypes,
				new Object[] { null });

		______TS("non-existent evaluation");

		sub1.evaluation = "non-existent";

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { sub1 });
	}

	@Test
	public void testDeleteSubmission() {
		// method not implemented
	}

	@SuppressWarnings("unused")
	private void ____HELPER_methods_________________________________________() {
	}
	
	/** 
	 * Verifies submissions required to support the current Team structure
	 *    exists in the database, for all evaluations under the give course. 
	 *    However, there could also be orphaned submissions in the database. 
	 *    This method does not care about those.
	 */
	private void verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(
			List<SubmissionData> submissionList, String courseId) throws EntityDoesNotExistException {
		CourseData course = logic.getCourseDetails(courseId);
		List<StudentData> students = logic.getStudentListForCourse(courseId);

		for(EvaluationData e: course.evaluations){
			verifySubmissionsExistForCurrentTeamStructureInEvaluation(e.name, students, submissionList);
		}
	}
	
	
	private void verifySubmissionsExistForCurrentTeamStructureInEvaluation(String evaluationName,
			List<StudentData> students, List<SubmissionData> submissions) {

		for (StudentData reviewer : students) {
			for (StudentData reviewee : students) {
				if (!reviewer.team.equals(reviewee.team)) {
					continue;
				}
				verifySubmissionExists(evaluationName, reviewer.email, reviewee.email,
						reviewer.team, submissions);
			}
		}

	}

	/**
	 * Verifies if there is a submission in the list for the 
	 *    given evaluation for the same reviewer and reviewee 
	 *    under the same team. Does not check other attributes.
	 */
	private void verifySubmissionExists(String evaluationName, String reviewer,
			String reviewee, String team, List<SubmissionData> submissions) {
		int count = 0;
		for (SubmissionData s : submissions) {
			if (s.evaluation.equals(evaluationName)
					&& s.reviewer.equals(reviewer)
					&& s.reviewee.equals(reviewee) 
					&& s.team.equals(team)) {
				count++;
			}
		}
		String errorMsg = "Count is not 1 for "+evaluationName+":"+team+":"+reviewer+"->"+reviewee;
		assertEquals(errorMsg, 1, count);
	}

	private MimeMessage getEmailToStudent(StudentData s,
			List<MimeMessage> emailsSent) throws MessagingException {
		for (MimeMessage m : emailsSent) {
			boolean emailSentToThisStudent = m.getAllRecipients()[0].toString()
					.equalsIgnoreCase(s.email);
			if (emailSentToThisStudent) {
				print("email sent to:" + s.email);
				return m;
			}
		}
		return null;
	}

	private void verifyJoinInviteToStudent(StudentData student,
			MimeMessage email) throws MessagingException {
		assertEquals(student.email, email.getAllRecipients()[0].toString());
		assertContains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN,
				email.getSubject());
		assertContains(student.course, email.getSubject());
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
		StudentData.equalizeIrrelevantData(expectedStudent, actualStudent);
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

	private void verifyCannotAccess(int userType, String methodName,
			String userId, Class<?>[] paramTypes, Object[] params)
			throws Exception {
		verifyAccessLevel(true, userType, methodName, userId, paramTypes,
				params);
	}

	private void verifyCanAccess(int userType, String methodName,
			String userId, Class<?>[] paramTypes, Object[] params)
			throws Exception {
		verifyAccessLevel(false, userType, methodName, userId, paramTypes,
				params);
	}

	private void verifyAccessLevel(boolean isUnauthExceptionExpected,
			int userType, String methodName, String userId,
			Class<?>[] paramTypes, Object[] params) throws Exception {
		Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

		switch (userType) {
		case USER_TYPE_NOT_LOGGED_IN:
			logoutUser();
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
			method.setAccessible(true); // in case it is a private method
			method.invoke(logic, params);
			if (isUnauthExceptionExpected) {
				fail();
			}
		} catch (Exception e) {
			String stack = Common.stackTraceToString(e);

			if (isUnauthExceptionExpected) {
				// ensure it was the UnauthorizedAccessException
				assertEquals(
						"UnauthorizedAccessException expected, but received this: "
								+ stack, UnauthorizedAccessException.class, e
								.getCause().getClass());
			} else {
				// ensure it was not the UnauthorizedAccessException
				assertTrue("UnauthorizedAccessException is not expected here: "
						+ stack, e.getCause() == null
						|| UnauthorizedAccessException.class != e.getCause()
								.getClass());
			}
		}
	}

	private void verifyNullPointerException(String methodName,
			String parameterName, Class<?>[] paramTypes, Object[] params)
			throws Exception {

		Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

		try {
			method.setAccessible(true); // in case it is a private method
			method.invoke(logic, params);
			fail();
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause.getClass() == NullPointerException.class) {
				verifyNullParameterDetectedCorrectly(
						(NullPointerException) cause, parameterName);
			} else {
				throw e;
			}
		}
	}

	private void verifyEntityDoesNotExistException(String methodName,
			Class<?>[] paramTypes, Object[] params) throws Exception {

		Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

		try {
			method.setAccessible(true); // in case it is a private method
			method.invoke(logic, params);
			fail();
		} catch (Exception e) {
			assertEquals(EntityDoesNotExistException.class, e.getCause()
					.getClass());
		}
	}

	@SuppressWarnings("unused")
	private void ____invoking_private_methods__() {
	}

	private void verifyNullParameterDetectedCorrectly(NullPointerException e,
			String nameOfNullParameter) {
		BaseTestCase.assertContains(nameOfNullParameter.toLowerCase(), e
				.getMessage().toLowerCase());
	}

	private void invokeEditEvaluation(EvaluationData e)
			throws InvalidParametersException, EntityDoesNotExistException {
		logic.editEvaluation(e.course, e.name, e.instructions, e.startTime,
				e.endTime, e.timeZone, e.gracePeriod, e.p2pEnabled);
	}

	private TeamEvalResult invokeCalculateTeamResult(TeamData team)
			throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod(
				"calculateTeamResult", new Class[] { TeamData.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { team };
		return (TeamEvalResult) privateMethod.invoke(logic, params);
	}

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

	private static StudentData invokeEnrollStudent(StudentData student)
			throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod("enrollStudent",
				new Class[] { StudentData.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { student };
		return (StudentData) privateMethod.invoke(logic, params);
	}

	private void invokeEditSubmission(SubmissionData s) throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod("editSubmission",
				new Class[] { SubmissionData.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { s };
		privateMethod.invoke(logic, params);
	}

	@SuppressWarnings("unchecked")
	private List<MimeMessage> invokeSendEvaluationPublishedEmails(
			String courseId, String evaluationName) throws Exception {
		Method privateMethod = Logic.class.getDeclaredMethod(
				"sendEvaluationPublishedEmails", new Class[] { String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { courseId, evaluationName };
		return (List<MimeMessage>) privateMethod.invoke(logic, params);
	}

	@SuppressWarnings("unused")
	private void ____test_object_manipulation_methods__() {
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

	private void alterSubmission(SubmissionData submission) {
		submission.points = submission.points + 10;
		submission.p2pFeedback = new Text(submission.p2pFeedback.getValue()
				+ "x");
		submission.justification = new Text(submission.justification.getValue()
				+ "y");
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
