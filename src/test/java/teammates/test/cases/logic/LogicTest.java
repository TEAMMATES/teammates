package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;
import static teammates.logic.core.TeamEvalResult.NA;
import static teammates.logic.core.TeamEvalResult.NSB;
import static teammates.logic.core.TeamEvalResult.NSU;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.SubmissionsLogic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class LogicTest extends BaseComponentTestCase {

	private static final Logic logic = new Logic();
	protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

	private static final CoursesDb coursesDb = new CoursesDb();
	private static final InstructorsDb instructorsDb = new InstructorsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final StudentsDb studentsDb = new StudentsDb();
	private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
	private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
	private static final CommentsDb commentsDb = new CommentsDb();

	private static Gson gson = Utils.getTeammatesGson();

	private static DataBundle dataBundle = getTypicalDataBundle();

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Logic.class);
	}

	@BeforeMethod
	public void caseSetUp() throws ServletException {
		dataBundle = getTypicalDataBundle();
	}

	@SuppressWarnings("unused")
	private void ____USER_level_methods___________________________________() {
	}

	@Test
	public void testGetLoginUrl() {
		gaeSimulation.logoutUser();
		assertEquals("/_ah/login?continue=www.abc.com",
				Logic.getLoginUrl("www.abc.com"));
	}

	@Test
	public void testGetLogoutUrl() {
		gaeSimulation.loginUser("any.user");
		assertEquals("/_ah/logout?continue=www.def.com",
				Logic.getLogoutUrl("www.def.com"));
	}
	
	//TODO: test isUserLoggedIn method

	@Test
	public void testGetCurrentUser() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("admin+instructor+student");

		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		gaeSimulation.loginAsAdmin(instructor.googleId);
		// also make this user a student
		StudentAttributes instructorAsStudent = new StudentAttributes(
				"Team 1|Instructor As Student|instructorasstudent@yahoo.com|", "some-course");
		instructorAsStudent.googleId = instructor.googleId;
		logic.createStudent(instructorAsStudent);

		UserType user = logic.getCurrentUser();
		assertEquals(instructor.googleId, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isInstructor);
		assertEquals(true, user.isStudent);

		______TS("admin+instructor only");

		// this user is no longer a student
		logic.deleteStudent(instructorAsStudent.course, instructorAsStudent.email);

		user = logic.getCurrentUser();
		assertEquals(instructor.googleId, user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(true, user.isInstructor);
		assertEquals(false, user.isStudent);

		______TS("instructor only");
		
		// this user is no longer an admin
		gaeSimulation.loginAsInstructor(instructor.googleId);
		
		user = logic.getCurrentUser();
		assertEquals(instructor.googleId, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(true, user.isInstructor);
		assertEquals(false, user.isStudent);

		______TS("unregistered");

		gaeSimulation.loginUser("unknown");

		user = logic.getCurrentUser();
		assertEquals("unknown", user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isInstructor);
		assertEquals(false, user.isStudent);

		______TS("student only");

		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		gaeSimulation.loginAsStudent(student.googleId);

		user = logic.getCurrentUser();
		assertEquals(student.googleId, user.id);
		assertEquals(false, user.isAdmin);
		assertEquals(false, user.isInstructor);
		assertEquals(true, user.isStudent);

		______TS("admin only");

		gaeSimulation.loginAsAdmin("any.user");

		user = logic.getCurrentUser();
		assertEquals("any.user", user.id);
		assertEquals(true, user.isAdmin);
		assertEquals(false, user.isInstructor);
		assertEquals(false, user.isStudent);

		______TS("not logged in");

		// check for user not logged in
		gaeSimulation.logoutUser();
		assertEquals(null, logic.getCurrentUser());
	}
	
	@SuppressWarnings("unused")
	private void ____ACCOUNT_level_methods____________________________________() {
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetInstructorAccounts() throws Exception{
		
		restoreTypicalDataInDatastore();

		______TS("success case");
		
		List<AccountAttributes> instructorAccounts = logic.getInstructorAccounts();
		int size = instructorAccounts.size();
		
		logic.createAccount("test.account", "Test Account", true, "test@account.com", "Foo University");
		instructorAccounts = logic.getInstructorAccounts();
		assertEquals(instructorAccounts.size(), size + 1);
		
		logic.deleteAccount("test.account");
		instructorAccounts = logic.getInstructorAccounts();
		assertEquals(instructorAccounts.size(), size);
	}
	
	//TODO: add unit tests for other ACCOUNT-level methods
	/*
		createAccount(String, String, boolean, String, String)
		getAccount(String)
		updateAccount(AccountAttributes)
		deleteAccount(String)
	 */

	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_level_methods____________________________________() {
	}

	@Test
	public void testCreateInstructorAccount() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("success case");

		// Delete any existing
		CourseAttributes cd = dataBundle.courses.get("typicalCourse1");
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse1");
		logic.deleteCourse(cd.id);
		verifyAbsentInDatastore(cd);
		verifyAbsentInDatastore(instructor);
		verifyAbsentInDatastore(instructor2);
		
		// Create fresh
		logic.createCourseAndInstructor(instructor.googleId, cd.id, cd.name);
		try {
			logic.createInstructorAccount(instructor.googleId, instructor.courseId, instructor.name, instructor.email, "National University of Singapore");
			signalFailureToDetectException();
		} catch (EntityAlreadyExistsException eaee) {
			// Course must be created with a creator. `instructor` here is our creator, so recreating it should give us EAEE
		}
		// Here we create another INSTRUCTOR for testing our createInstructor() method
		String googleIdWithGmailDomain = instructor2.googleId+"@GMAIL.COM"; //to check if "@GMAIL.COM" is stripped out correctly
		logic.createInstructorAccount(googleIdWithGmailDomain, instructor2.courseId, instructor2.name, instructor2.email, "National University of Singapore");
		
		// `instructor` here is created with NAME and EMAIL field obtain from his AccountData
		AccountAttributes creator = dataBundle.accounts.get("instructor1OfCourse1");
		instructor.name = creator.name;
		instructor.email = creator.email; 
		verifyPresentInDatastore(cd);
		verifyPresentInDatastore(instructor);
		verifyPresentInDatastore(instructor2);
		
		// Delete fresh
		logic.deleteCourse(cd.id);
		// read deleted course
		verifyAbsentInDatastore(cd);
		// check for cascade delete
		verifyAbsentInDatastore(instructor);
		verifyAbsentInDatastore(instructor2);
		
		// Delete non-existent (fails silently)
		logic.deleteCourse(cd.id);
		logic.deleteInstructor(instructor.courseId, instructor.googleId);
		logic.deleteInstructor(instructor2.courseId, instructor2.googleId);

		______TS("invalid parameters");

		String googleId = "valid-id";
		
		//ensure no account exist for this instructor
		assertNull(logic.getAccount(googleId));
		
		// Ensure the exception is thrown at logic level
		try {
			logic.createInstructorAccount(googleId, "invalid courseId", "Valid name", "valid@email.com", "National University of Singapore");
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(
					String.format(COURSE_ID_ERROR_MESSAGE, "invalid courseId" , REASON_INCORRECT_FORMAT),
					e.getMessage());
		}
		
		//ensure no account exist for this instructor because the operation above failed 
		assertNull(logic.getAccount(googleId));

		______TS("null parameters");
		
		try {
			logic.createInstructorAccount(null, "valid.courseId", "Valid Name", "valid@email.com", "National University of Singapore");
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
		try {
			logic.createInstructorAccount("valid.id", null, "Valid Name", "valid@email.com", "National University of Singapore");
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	@Test
	public void testGetInstructorForGoogleId() throws Exception {
		
		______TS("invalid case: null parameters");

		try {
			logic.getInstructorForGoogleId(null, "instructorId");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		try {
			logic.getInstructorForGoogleId("course-id", null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("success: get instructor with specific googleId");
		
		restoreTypicalDataInDatastore();

		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instr = logic.getInstructorForGoogleId(courseId, googleId);
		
		assertEquals(courseId, instr.courseId);
		assertEquals(googleId, instr.googleId);
		assertEquals("instructor1@course1.com", instr.email);
		assertEquals("Instructor1 Course1", instr.name);
	}
	
	@Test
	public void testGetInstructorsForGoogleId() throws Exception {
	
		______TS("invalid case: null parameters");

		try {
			logic.getInstructorsForGoogleId(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("success: get all instructors for a google id");
		
		restoreTypicalDataInDatastore();
		
		String googleId = "idOfInstructor3";
		
		List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(googleId);
		assertEquals(2, instructors.size());
		
	}
	
	@Test
	public void testIsNewInstructor() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("success: is new instructor");
		String instructorId = "idOfInstructorWithOnlyOneSampleCourse";
		assertEquals(true, logic.isNewInstructor(instructorId));
		
		______TS("failure: instructor is not new user");
		instructorId = "idOfInstructor1OfCourse1";
		assertEquals(false, logic.isNewInstructor(instructorId));
	}
	
	@Test
	public void testGetInstructorsForCourse() throws Exception {
		
		______TS("invalid case: null parameters");

		try {
			logic.getInstructorsForCourse(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("success: get all instructors for a course");

		restoreTypicalDataInDatastore();
		
		String courseId = "idOfTypicalCourse1";
		
		List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
		assertEquals(3, instructors.size());
	}
	
	@Test
	public void testDeleteInstructor() throws Exception {
		
		______TS("invalid case: null parameters");

		try {
			logic.deleteInstructor(null, "instructor-id");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		try {
			logic.deleteInstructor("course-id", null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("success: delete an instructor for specific course");
		
		restoreTypicalDataInDatastore();
		
		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instructorDeleted = logic.getInstructorForGoogleId(courseId, googleId);
		
		logic.deleteInstructor(courseId, googleId);
		
		LogicTest.verifyAbsentInDatastore(instructorDeleted);
		
	}

	@Test
	public void testUpdateInstructor() throws Exception {
		
		______TS("invalid case: course id is null");

		try {
			logic.updateInstructor(null, "idOfInstructor1OfCourse1", "Instructor Name", "instr@email.com");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("invalid case: instructor id is null");

		try {
			logic.updateInstructor("idOfTypicalCourse1", null, "Instructor Name", "instr@email.com");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("invalid case: instructor name is null");

		try {
			logic.updateInstructor("idOfTypicalCourse1", "idOfInstructor1OfCourse1", null, "instr@email.com");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("invalid case: instructor email is null");

		try {
			logic.updateInstructor("idOfTypicalCourse1", "idOfInstructor1OfCourse1", "Instructor Name", null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, e.getMessage());
		}
		
		______TS("success: update an instructor");
		
		restoreTypicalDataInDatastore();
		
		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instructorToBeUpdated = logic.getInstructorForGoogleId(courseId, googleId);
		instructorToBeUpdated.email = "new-email@course1.com";
		
		logic.updateInstructor(courseId, instructorToBeUpdated.googleId, instructorToBeUpdated.name, instructorToBeUpdated.email);
		
		InstructorAttributes instructorUpdated = logic.getInstructorForGoogleId(courseId, googleId);
		assertEquals(instructorToBeUpdated.email, instructorUpdated.email);
	}

	@Test
	public void testDowngradeInstructorToStudentCascade() throws Exception {

		// mostly tested in testCreateInstructor

		
		______TS("typical case");
		
		

		InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");

		// ensure that the instructor exists in datastore
		verifyPresentInDatastore(instructor1);
		
		logic.deleteInstructor(instructor1.courseId, instructor1.googleId);
		
		verifyAbsentInDatastore(instructor1);
		
		______TS("non-existent");
		
		// try to delete again. Should fail silently.
		logic.deleteInstructor(instructor1.courseId, instructor1.googleId);

		______TS("null parameter");

		try {
			logic.deleteInstructor(null, null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	//TODO: implement tests for these methods
	/*

		deleteInstructor(String, String)
	 */

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods___________________________________() {
	}

	@Test
	public void testCreateCourseAndInstructor() throws Exception {
		
		/* Explanation: The SUT has 5 paths. They are,
		 * path 1 - exit due to access control failure.
		 * path 2 - success.
		 * path 3,4,5 - exit due to a null parameter (there are three parameter).
		 * 
		 */

		restoreTypicalDataInDatastore();


		______TS("typical case");
		
		/* here we test path 2 */

		
		
		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		AccountAttributes creator = dataBundle.accounts.get("instructor1OfCourse1");
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		// Note the instructor created will get the name and email from the existing account
		instructor.name = creator.name;
		instructor.email = creator.email;

		// Delete to avoid clashes with existing data
		logic.deleteCourse(instructor.courseId);

		verifyAbsentInDatastore(course);
		verifyAbsentInDatastore(instructor);

		logic.createCourseAndInstructor(instructor.googleId , course.id, course.name);
		verifyPresentInDatastore(course);
		verifyPresentInDatastore(instructor);

		______TS("null parameters");
		
		/* Here we test paths 3, 4, 5 */
		
		try {
			logic.createCourseAndInstructor(null, course.id, course.name);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
		try {
			logic.createCourseAndInstructor(instructor.googleId, null, course.name);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
		try {
			logic.createCourseAndInstructor(instructor.googleId, course.id, null);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetCourse() throws Exception {
		// mostly tested in testCreateCourse

		restoreTypicalDataInDatastore();


		______TS("null parameters");

		try {
			logic.getCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetCourseDetails() throws Exception {


		restoreTypicalDataInDatastore();

		String methodName = "getCourseDetails";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
		
		______TS("typical case");

		

		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		CourseDetailsBundle courseDetials = logic.getCourseDetails(course.id);
		assertEquals(course.id, courseDetials.course.id);
		assertEquals(course.name, courseDetials.course.name);
		assertEquals(2, courseDetials.stats.teamsTotal);
		assertEquals(5, courseDetials.stats.studentsTotal);
		assertEquals(0, courseDetials.stats.unregisteredTotal);

		______TS("course without students");

		logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("instructor1", "course1", "course 1");
		courseDetials = logic.getCourseDetails("course1");
		assertEquals("course1", courseDetials.course.id);
		assertEquals("course 1", courseDetials.course.name);
		assertEquals(0, courseDetials.stats.teamsTotal);
		assertEquals(0, courseDetials.stats.studentsTotal);
		assertEquals(0, courseDetials.stats.unregisteredTotal);

		______TS("non-existent");

		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });

		______TS("null parameter");

		try {
			logic.getCourseDetails(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetCoursesForStudentAccount() throws Exception {
	
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getCoursesForStudentAccount";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
	
	
		______TS("student having two courses");
	
		restoreTypicalDataInDatastore();
	
		
	
		StudentAttributes studentInTwoCourses = dataBundle.students
				.get("student2InCourse1");
		List<CourseAttributes> courseList = logic
				.getCoursesForStudentAccount(studentInTwoCourses.googleId);
		assertEquals(2, courseList.size());
		// For some reason, index 0 is Course2 and index 1 is Course1
		// Anyway in DataStore which follows a HashMap structure,
		// there is no guarantee on the order of Entities' storage
		CourseAttributes course1 = dataBundle.courses.get("typicalCourse2");
		assertEquals(course1.id, courseList.get(0).id);
		assertEquals(course1.name, courseList.get(0).name);
	
		CourseAttributes course2 = dataBundle.courses.get("typicalCourse1");
		assertEquals(course2.id, courseList.get(1).id);
		assertEquals(course2.name, courseList.get(1).name);
	
		______TS("student having one course");
	
		StudentAttributes studentInOneCourse = dataBundle.students
				.get("student1InCourse1");
		courseList = logic.getCoursesForStudentAccount(studentInOneCourse.googleId);
		assertEquals(1, courseList.size());
		course1 = dataBundle.courses.get("typicalCourse1");
		assertEquals(course1.id, courseList.get(0).id);
		assertEquals(course1.name, courseList.get(0).name);
	
		// student having zero courses is not applicable
	
		______TS("non-existent student");
	
		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	
		______TS("null parameter");
	
		try {
			logic.getCoursesForStudentAccount(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	//TODO: Test getCourseSummariesWithoutStatsForInstructor() method
	
	@Test
	public void testGetCourseSummariesForInstructor() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getCourseSummariesForInstructor";
		Class<?>[] paramTypes = new Class<?>[] { String.class };

	
		______TS("instructor with 2 courses");
	
		
	
		// Instructor 3 is an instructor of 2 courses - Course 1 and Course 2. 
		// Retrieve from one course to get the googleId, then pull the courses for that googleId
		InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
		HashMap<String, CourseDetailsBundle> courseList = logic.getCourseSummariesForInstructor(instructor.googleId);
		assertEquals(2, courseList.size());
		for (CourseDetailsBundle cdd : courseList.values()) {
			// check if course belongs to this instructor
			assertTrue(logic.isInstructorOfCourse(instructor.googleId, cdd.course.id));
		}
	
		______TS("instructor with 0 courses");
	
		/*
		 * This is to be for Account entity
		instructor = dataBundle.instructors.get("typicalInstructor3");
		courseList = logic.getCourseListForInstructor(instructor.googleId);
		assertEquals(0, courseList.size());
		*/
	
		______TS("null parameters");
	
		try {
			logic.getCourseSummariesForInstructor(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent instructor");
	
		verifyEntityDoesNotExistException(
				methodName, paramTypes, new Object[] {"non-existent-course"});
	
	}

	@Test
	public void testGetCourseDetailsListForInstructor() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getCourseDetailsListForInstructor";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
	
	
		______TS("typical case");
	
		
	
		HashMap<String, CourseDetailsBundle> courseListForInstructor = logic
				.getCourseDetailsListForInstructor("idOfInstructor3");
		assertEquals(2, courseListForInstructor.size());
		String course1Id = "idOfTypicalCourse1";
	
		// course with 2 evaluations
		ArrayList<EvaluationDetailsBundle> course1Evals = courseListForInstructor
				.get(course1Id).evaluations;
		String course1EvalDetails = "";
		for (EvaluationDetailsBundle ed : course1Evals) {
			course1EvalDetails = course1EvalDetails
					+ Utils.getTeammatesGson().toJson(ed) + Const.EOL;
		}
		int numberOfEvalsInCourse1 = course1Evals.size();
		assertEquals(course1EvalDetails, 2, numberOfEvalsInCourse1);
		assertEquals(course1Id, course1Evals.get(0).evaluation.courseId);
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation1InCourse1"),
				course1Evals);
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation2InCourse1"),
				course1Evals);
	
		// course with 1 evaluation
		assertEquals(course1Id, course1Evals.get(1).evaluation.courseId);
		ArrayList<EvaluationDetailsBundle> course2Evals = courseListForInstructor
				.get("idOfTypicalCourse2").evaluations;
		assertEquals(1, course2Evals.size());
		verifyEvaluationInfoExistsInList(
				dataBundle.evaluations.get("evaluation1InCourse2"),
				course2Evals);
	
		______TS("instructor has a course with 0 evaluations");
	
		
		
		courseListForInstructor = logic
				.getCourseDetailsListForInstructor("idOfInstructor4");
		assertEquals(1, courseListForInstructor.size());
		assertEquals(0,
				courseListForInstructor.get("idOfCourseNoEvals").evaluations
						.size());
	
		/*
		 * Not allowed, this is for Account
		______TS("instructor with 0 courses");
	
		
		logic.createInstructor("instructorWith0course", "Instructor with 0 courses",
				"instructorWith0course@gmail.com");
		courseListForInstructor = logic
				.getCourseDetailsListForInstructor("instructorWith0course");
		assertEquals(0, courseListForInstructor.size());
		*/
	
		______TS("null parameters");
	
		try {
			logic.getCourseDetailsListForInstructor(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent instructor");
	
		
		
		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				"non-existent"});
		
	}

	@Test
	public void testGetCourseDetailsListForStudent() throws Exception {
	
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getCourseDetailsListForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
	
		______TS("student having multiple evaluations in multiple courses");
	
		restoreTypicalDataInDatastore();
	
		
	
		// Let's call this course 1. It has 2 evaluations.
		CourseAttributes expectedCourse1 = dataBundle.courses.get("typicalCourse1");
	
		EvaluationAttributes expectedEval1InCourse1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		EvaluationAttributes expectedEval2InCourse1 = dataBundle.evaluations
				.get("evaluation2InCourse1");
	
		// Let's call this course 2. I has only 1 evaluation.
		CourseAttributes expectedCourse2 = dataBundle.courses.get("typicalCourse2");
	
		EvaluationAttributes expectedEval1InCourse2 = dataBundle.evaluations
				.get("evaluation1InCourse2");
	
		// This student is in both course 1 and 2
		StudentAttributes studentInTwoCourses = dataBundle.students
				.get("student2InCourse1");
	
		// Make sure all evaluations in course1 are visible (i.e., not AWAITING)
		expectedEval1InCourse1.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		expectedEval1InCourse1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		expectedEval1InCourse1.published = false;
		assertEquals(EvalStatus.CLOSED, expectedEval1InCourse1.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.updateEvaluation(expectedEval1InCourse1);
	
		expectedEval2InCourse1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		expectedEval2InCourse1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, expectedEval2InCourse1.getStatus());
		backDoorLogic.updateEvaluation(expectedEval2InCourse1);
	
		// Make sure all evaluations in course2 are still AWAITING
		expectedEval1InCourse2.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		expectedEval1InCourse2.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		expectedEval1InCourse2.activated = false;
		assertEquals(EvalStatus.AWAITING, expectedEval1InCourse2.getStatus());
		backDoorLogic.updateEvaluation(expectedEval1InCourse2);
	
		// Get course details for student
		List<CourseDetailsBundle> courseList = logic
				.getCourseDetailsListForStudent(studentInTwoCourses.googleId);
	
		// verify number of courses received
		assertEquals(2, courseList.size());
	
		// verify details of course 1 (note: index of course 1 is not 0)
		CourseDetailsBundle actualCourse1 = courseList.get(1);
		assertEquals(expectedCourse1.id, actualCourse1.course.id);
		assertEquals(expectedCourse1.name, actualCourse1.course.name);
		assertEquals(2, actualCourse1.evaluations.size());
	
		// verify details of evaluation 1 in course 1
		EvaluationAttributes actualEval1InCourse1 = actualCourse1.evaluations.get(1).evaluation;
		verifySameEvaluationData(expectedEval1InCourse1, actualEval1InCourse1);
	
		// verify some details of evaluation 2 in course 1
		EvaluationAttributes actualEval2InCourse1 = actualCourse1.evaluations.get(0).evaluation;
		verifySameEvaluationData(expectedEval2InCourse1, actualEval2InCourse1);
	
		// for course 2, verify no evaluations returned (because the evaluation
		// in this course is still AWAITING.
		CourseDetailsBundle actualCourse2 = courseList.get(0);
		assertEquals(expectedCourse2.id, actualCourse2.course.id);
		assertEquals(expectedCourse2.name, actualCourse2.course.name);
		assertEquals(0, actualCourse2.evaluations.size());
	
		______TS("student in a course with no evaluations");
	
		StudentAttributes studentWithNoEvaluations = dataBundle.students
				.get("student1InCourse2");
		courseList = logic
				.getCourseDetailsListForStudent(studentWithNoEvaluations.googleId);
		assertEquals(1, courseList.size());
		assertEquals(0, courseList.get(0).evaluations.size());
	
		// student with 0 courses not applicable
	
		______TS("non-existent student");
	
		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	
		______TS("null parameter");
	
		try {
			logic.getCourseDetailsListForStudent(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testUpdateCourse() {
		// method not implemented
	}

	@Test
	public void testDeleteCourse() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("typical case");
	
		restoreTypicalDataInDatastore();
	
		
	
		CourseAttributes course1OfInstructor = dataBundle.courses.get("typicalCourse1");
	
		// ensure there are entities in the datastore under this course
		assertTrue(logic.getStudentsForCourse(course1OfInstructor.id).size() != 0);
		verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyPresentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1"));
	
		StudentAttributes studentInCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(course1OfInstructor.id, studentInCourse.course);
		verifyPresentInDatastore(studentInCourse);
	
		logic.deleteCourse(course1OfInstructor.id);
	
		// ensure the course and related entities are deleted
		verifyAbsentInDatastore(course1OfInstructor);
		verifyAbsentInDatastore(studentInCourse);
		verifyAbsentInDatastore(dataBundle.students.get("student1InCourse1"));
		verifyAbsentInDatastore(dataBundle.evaluations
				.get("evaluation1InCourse1"));
		ArrayList<SubmissionAttributes> submissionsOfCourse = new ArrayList<SubmissionAttributes>(dataBundle.submissions.values());
		for (SubmissionAttributes s : submissionsOfCourse) {
			if (s.course.equals(course1OfInstructor.id)) {
				verifyAbsentInDatastore(s);
			}
		}
	
		______TS("non-existent");
	
		// try to delete again. Should fail silently.
		logic.deleteCourse(course1OfInstructor.id);
	
		______TS("null parameter");
	
		try {
			logic.deleteCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	@Test
	public void testCreateStudent() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("typical case");

		// TODO: Move following to StudentsLogicTest (together with SUT -> StudentsLogic)
		
		
		
		restoreTypicalDataInDatastore();
		//reuse existing student to create a new student
		StudentAttributes newStudent = dataBundle.students.get("student1InCourse1");
		newStudent.email = "new@student.com";
		verifyAbsentInDatastore(newStudent);
		
		List<SubmissionAttributes> submissionsBeforeAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
		
		logic.createStudent(newStudent);
		verifyPresentInDatastore(newStudent);
		
		List<SubmissionAttributes> submissionsAfterAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
		
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
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
		}

		______TS("invalid parameter");

		// Only checking that exception is thrown at logic level
		newStudent.email = "invalid email";
		
		try {
			logic.createStudent(newStudent);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertEquals(
					String.format(EMAIL_ERROR_MESSAGE, "invalid email", REASON_INCORRECT_FORMAT),
					e.getMessage());
		}
		
		______TS("null parameters");
		
		try {
			logic.createStudent(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}

		// other combination of invalid data should be tested against
		// StudentAttributes

	}

	@Test
	public void testGetStudentForEmail() throws Exception {
		// mostly tested in testCreateStudent

		restoreTypicalDataInDatastore();


		______TS("null parameters");

		try {
			logic.getStudentForEmail(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetStudentsForGoogleId() throws Exception {
	
		______TS("access control");
	
		restoreTypicalDataInDatastore();
	
		______TS("student in one course");
	
		
	
		restoreTypicalDataInDatastore();
		StudentAttributes studentInOneCourse = dataBundle.students
				.get("student1InCourse1");
		assertEquals(1, logic.getStudentsForGoogleId(studentInOneCourse.googleId).size());
		assertEquals(studentInOneCourse.email,
				logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).email);
		assertEquals(studentInOneCourse.name,
				logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).name);
		assertEquals(studentInOneCourse.course,
				logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).course);
	
		______TS("student in two courses");
	
		// this student is in two courses, course1 and course 2.
	
		// get list using student data from course 1
		StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
				.get("student2InCourse1");
		List<StudentAttributes> listReceivedUsingStudentInCourse1 = logic
				.getStudentsForGoogleId(studentInTwoCoursesInCourse1.googleId);
		assertEquals(2, listReceivedUsingStudentInCourse1.size());
	
		// get list using student data from course 2
		StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
				.get("student2InCourse2");
		List<StudentAttributes> listReceivedUsingStudentInCourse2 = logic
				.getStudentsForGoogleId(studentInTwoCoursesInCourse2.googleId);
		assertEquals(2, listReceivedUsingStudentInCourse2.size());
	
		// check the content from first list (we assume the content of the
		// second list is similar.
	
		StudentAttributes firstStudentReceived = listReceivedUsingStudentInCourse1
				.get(0);
		// First student received turned out to be the one from course 2
		assertEquals(studentInTwoCoursesInCourse2.email,
				firstStudentReceived.email);
		assertEquals(studentInTwoCoursesInCourse2.name,
				firstStudentReceived.name);
		assertEquals(studentInTwoCoursesInCourse2.course,
				firstStudentReceived.course);
	
		// then the second student received must be from course 1
		StudentAttributes secondStudentReceived = listReceivedUsingStudentInCourse1
				.get(1);
		assertEquals(studentInTwoCoursesInCourse1.email,
				secondStudentReceived.email);
		assertEquals(studentInTwoCoursesInCourse1.name,
				secondStudentReceived.name);
		assertEquals(studentInTwoCoursesInCourse1.course,
				secondStudentReceived.course);
	
		______TS("non existent student");
	
		assertEquals(0, logic.getStudentsForGoogleId("non-existent").size());
	
		______TS("null parameters");
	
		try {
			logic.getStudentsForGoogleId(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetStudentForGoogleId() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("student in two courses");
	
		restoreTypicalDataInDatastore();
		
		StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
				.get("student2InCourse1");
	
		String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.googleId;
		assertEquals(studentInTwoCoursesInCourse1.email,
				logic.getStudentForGoogleId(
						studentInTwoCoursesInCourse1.course,
						googleIdOfstudentInTwoCourses).email);
	
		StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
				.get("student2InCourse2");
		assertEquals(studentInTwoCoursesInCourse2.email,
				logic.getStudentForGoogleId(
						studentInTwoCoursesInCourse2.course,
						googleIdOfstudentInTwoCourses).email);
	
		______TS("student in zero courses");
	
		assertEquals(null, logic.getStudentForGoogleId("non-existent",
				"random-google-id"));
	
		______TS("null parameters");
	
		try {
			logic.getStudentForGoogleId("valid.course", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testGetStudentsForCourse() throws Exception {
	
		restoreTypicalDataInDatastore();
		
		______TS("course with multiple students");
	
		restoreTypicalDataInDatastore();
	
		
	
		CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
		List<StudentAttributes> studentList = logic
				.getStudentsForCourse(course1OfInstructor1.id);
		assertEquals(5, studentList.size());
		for (StudentAttributes s : studentList) {
			assertEquals(course1OfInstructor1.id, s.course);
		}
	
		______TS("course with 0 students");
	
		CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
		studentList = logic.getStudentsForCourse(course2OfInstructor1.id);
		assertEquals(0, studentList.size());
	
		______TS("null parameter");
	
		try {
			logic.getStudentsForCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent course");
	
		studentList = logic.getStudentsForCourse("non-existent");
		assertEquals(0, studentList.size());
		
	}

	@Test
	public void testGetTeamsForCourse() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getTeamsForCourse";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
	
		______TS("typical case");
	
		restoreTypicalDataInDatastore();
	
		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		logic.createStudent(new StudentAttributes("t1|s1|s1@e|", course.id));
		CourseDetailsBundle courseAsTeams = logic.getTeamsForCourse(course.id);
		assertEquals(3, courseAsTeams.teams.size());
	
		String team1Id = "Team 1.1";
		assertEquals(team1Id, courseAsTeams.teams.get(0).name);
		assertEquals(4, courseAsTeams.teams.get(0).students.size());
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(0).team);
		assertEquals(team1Id, courseAsTeams.teams.get(0).students.get(1).team);
	
		String team2Id = "Team 1.2";
		assertEquals(team2Id, courseAsTeams.teams.get(1).name);
		assertEquals(1, courseAsTeams.teams.get(1).students.size());
		assertEquals(team2Id, courseAsTeams.teams.get(1).students.get(0).team);
	
		______TS("null parameters");
	
		try {
			logic.getTeamsForCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("course without teams");
	
		logic.deleteCourse("course1");
		logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("instructor1", "course1", "Course 1");
		assertEquals(0, logic.getTeamsForCourse("course1").teams.size());
	
		______TS("non-existent course");
	
		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	@Test
	public void testGetKeyForStudent() throws Exception {
		// mostly tested in testJoinCourse()
	
		restoreTypicalDataInDatastore();
	
		______TS("null parameters");
	
		try {
			logic.getKeyForStudent("valid.course.id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent student");
		
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		assertEquals(null,
				logic.getKeyForStudent(student.course, "non@existent"));
	}

	@Test
	public void testUpdateStudent() throws Exception {

		restoreTypicalDataInDatastore();

		

		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		String originalEmail = student1InCourse1.email;
		 		
		______TS("typical success case");
		student1InCourse1.name = student1InCourse1.name + "x";
		student1InCourse1.googleId = student1InCourse1.googleId + "x";
		student1InCourse1.comments = student1InCourse1.comments + "x";
		student1InCourse1.email = student1InCourse1.email + "x";
		student1InCourse1.team = "Team 1.2";
		logic.updateStudent(originalEmail, student1InCourse1);		
		verifyPresentInDatastore(student1InCourse1);
		
		// check for cascade
		List<SubmissionAttributes> submissionsAfterEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);		
		verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
				student1InCourse1.course);
		
		______TS("null parameters");

		try {
			logic.updateStudent(null, student1InCourse1);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}		
		try {
			logic.updateStudent("test@email.com", null);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
	}

	@Test
	public void testJoinCourse() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		// make a student 'unregistered'
		
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		String googleId = "student1InCourse1";
		String key = logic.getKeyForStudent(student.course, student.email);
		student.googleId = "";
		logic.updateStudent(student.email, student);
		assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
	
	
		______TS("register an unregistered student");
	
		restoreTypicalDataInDatastore();
	
		
	
		// make a student 'unregistered'
		student = dataBundle.students.get("student1InCourse1");
		googleId = "student1InCourse1";
		key = logic.getKeyForStudent(student.course, student.email);
		student.googleId = "";
		logic.updateStudent(student.email, student);
		assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
	
		// TODO: remove encrpytion - should fail test
		//Test if unencrypted key used
		logic.joinCourse(googleId, key);
		assertEquals(googleId,
				logic.getStudentForEmail(student.course, student.email).googleId);
		
		
		
		// make a student 'unregistered'
		student = dataBundle.students.get("student1InCourse1");
		googleId = "student1InCourse1";
		key = logic.getKeyForStudent(student.course, student.email);
		student.googleId = "";
		logic.updateStudent(student.email, student);
		assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
		logic.deleteAccount(googleId);	// for testing account creation
		AccountAttributes studentAccount = logic.getAccount(googleId); // this is because student accounts are not in typical data bundle
		assertNull(studentAccount);
	
		//Test for encrypted key used
		key = StringHelper.encrypt(key);
		logic.joinCourse(googleId, key);
		assertEquals(googleId,
				logic.getStudentForEmail(student.course, student.email).googleId);
		
		// Check that an account with the student's google ID was created
		studentAccount = logic.getAccount(googleId);
		verifyPresentInDatastore(studentAccount); 
		AccountAttributes accountOfInstructorOfCourse = dataBundle.accounts.get("instructor1OfCourse1");
		assertEquals(accountOfInstructorOfCourse.institute, studentAccount.institute);// Test that student account was appended with the correct Institute
				
		______TS("null parameters");
	
		try {
			logic.joinCourse("valid.user", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
		try {
			logic.joinCourse(null, key);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testEnrollStudents() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("all valid students, but contains blank lines");
	
		restoreTypicalDataInDatastore();
	
		String instructorId = "instructorForEnrollTesting";
		String courseId = "courseForEnrollTest";
		
		logic.createAccount("instructorForEnrollTesting", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor(instructorId, courseId, "Course for Enroll Testing");
		String EOL = Const.EOL;
	
		String line0 = "t1|n1|e1@g|c1";
		String line1 = " t2|  n2|  e2@g|  c2";
		String line2 = "t3|n3|e3@g|c3  ";
		String line3 = "t4|n4|  e4@g|c4";
		String line4 = "t5|n5|e5@g  |c5";
		String lines = line0 + EOL + line1 + EOL + line2 + EOL
				+ "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
				+ EOL + "    " + EOL + EOL;
		List<StudentAttributes> enrollResults = logic.enrollStudents(lines, courseId);
	
		assertEquals(5, enrollResults.size());
		assertEquals(5, logic.getStudentsForCourse(courseId).size());
		verifyEnrollmentResultForStudent(new StudentAttributes(line0, courseId),
				enrollResults.get(0), StudentAttributes.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentAttributes(line1, courseId),
				enrollResults.get(1), StudentAttributes.UpdateStatus.NEW);
		verifyEnrollmentResultForStudent(new StudentAttributes(line4, courseId),
				enrollResults.get(4), StudentAttributes.UpdateStatus.NEW);
		
		CourseDetailsBundle cd = logic.getCourseDetails(courseId);
		assertEquals(5, cd.stats.unregisteredTotal);
	
		______TS("includes a mix of unmodified, modified, and new");
	
		String line0_1 = "t3|modified name|e3@g|c3";
		String line5 = "t6|n6|e6@g|c6";
		lines = line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
		enrollResults = logic.enrollStudents(lines, courseId);
		assertEquals(6, enrollResults.size());
		assertEquals(6, logic.getStudentsForCourse(courseId).size());
		verifyEnrollmentResultForStudent(new StudentAttributes(line0, courseId),
				enrollResults.get(0), StudentAttributes.UpdateStatus.UNMODIFIED);
		verifyEnrollmentResultForStudent(new StudentAttributes(line0_1, courseId),
				enrollResults.get(1), StudentAttributes.UpdateStatus.MODIFIED);
		verifyEnrollmentResultForStudent(new StudentAttributes(line1, courseId),
				enrollResults.get(2), StudentAttributes.UpdateStatus.UNMODIFIED);
		verifyEnrollmentResultForStudent(new StudentAttributes(line5, courseId),
				enrollResults.get(3), StudentAttributes.UpdateStatus.NEW);
		assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
				enrollResults.get(4).updateStatus);
		assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
				enrollResults.get(5).updateStatus);
	
		______TS("includes an incorrect line");
	
		// no changes should be done to the database
		String incorrectLine = "incorrectly formatted line";
		lines = "t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
				+ line3;
		try {
			enrollResults = logic.enrollStudents(lines, courseId);
			Assert.fail("Did not throw exception for incorrectly formatted line");
		} catch (EnrollException e) {
			assertTrue(e.getMessage().contains(incorrectLine));
		}
		assertEquals(6, logic.getStudentsForCourse(courseId).size());
	
		______TS("null parameters");
	
		try {
			logic.enrollStudents("a|b|c|d", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("same student added, modified and unmodified in one shot");
	
		logic.createAccount("tes.instructor", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("tes.instructor", "tes.course", "TES Course");
		lines = "t8|n8|e8@g|c1" + EOL + "t8|n8a|e8@g|c1" + EOL
				+ "t8|n8a|e8@g|c1";
		enrollResults = logic.enrollStudents(lines, "tes.course");
	
		assertEquals(3, enrollResults.size());
		assertEquals(StudentAttributes.UpdateStatus.NEW,
				enrollResults.get(0).updateStatus);
		assertEquals(StudentAttributes.UpdateStatus.MODIFIED,
				enrollResults.get(1).updateStatus);
		assertEquals(StudentAttributes.UpdateStatus.UNMODIFIED,
				enrollResults.get(2).updateStatus);
	}

	@Test
	public void testSendRegistrationInviteForCourse() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("all students already registered");
	
		
	
		restoreTypicalDataInDatastore();
		CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
	
		// send registration key to a class in which all are registered
		List<MimeMessage> emailsSent = logic
				.sendRegistrationInviteForCourse(course1.id);
		assertEquals(0, emailsSent.size());
	
		______TS("some students not registered");
	
		// modify two students to make them 'unregistered' and send again
		StudentAttributes student1InCourse1 = dataBundle.students
				.get("student1InCourse1");
		student1InCourse1.googleId = "";
		logic.updateStudent(student1InCourse1.email, student1InCourse1);
		StudentAttributes student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		student2InCourse1.googleId = "";
		logic.updateStudent(student2InCourse1.email, student2InCourse1);
		emailsSent = logic.sendRegistrationInviteForCourse(course1.id);
		assertEquals(2, emailsSent.size());
		verifyJoinInviteToStudent(student2InCourse1, emailsSent.get(0));
		verifyJoinInviteToStudent(student1InCourse1, emailsSent.get(1));
	
		______TS("null parameters");
	
		try {
			logic.sendRegistrationInviteForCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testSendRegistrationInviteToStudent() throws Exception {
	
	
		______TS("send to existing student");
	
		
	
		restoreTypicalDataInDatastore();
	
		StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
	
		MimeMessage email = logic.sendRegistrationInviteToStudent(
				student1.course, student1.email);
	
		verifyJoinInviteToStudent(student1, email);
	
		______TS("send to non-existing student");
	
		restoreTypicalDataInDatastore();
	
		String methodName = "sendRegistrationInviteToStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
	
	
		verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
				student1.course, "non@existent" });
	
		______TS("null parameters");
	
		try {
			logic.sendRegistrationInviteToStudent(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testSendReminderForEvaluation() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "sendReminderForEvaluation";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
	
		______TS("empty class");
	
		restoreTypicalDataInDatastore();
	
		
		
		AccountsLogic.inst().deleteAccountCascade("instructor1");
		CoursesLogic.inst().deleteCourseCascade("course1");
		logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("instructor1", "course1", "course 1");
		EvaluationAttributes newEval = new EvaluationAttributes();
		newEval.courseId = "course1";
		newEval.name = "new eval";
		newEval.instructions = new Text("instructions");
		newEval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		newEval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		logic.createEvaluation(newEval);
	
		List<MimeMessage> emailsSent = logic.sendReminderForEvaluation(
				"course1", "new eval");
		
		int numOfInstructor = logic.getInstructorsForCourse(newEval.courseId).size();
		assertEquals(0+numOfInstructor, emailsSent.size());
	
		______TS("1 person submitted fully, 4 others have not");
	
		EvaluationAttributes eval = dataBundle.evaluations
				.get("evaluation1InCourse1");
		emailsSent = logic.sendReminderForEvaluation(eval.courseId, eval.name);
		
		numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
		assertEquals(4+numOfInstructor, emailsSent.size());
		List<StudentAttributes> studentList = logic
				.getStudentsForCourse(eval.courseId);
	
		//student 1 would not recieve email 
		for (StudentAttributes s : studentList) {
			if(!s.name.equals("student1 In Course1")){
				String errorMessage = "No email sent to " + s.email;
				assertTrue(errorMessage, getEmailToStudent(s, emailsSent) != null);
			}
		}
	
		______TS("some have submitted fully");
	
		
	
		// This student is the only member in Team 1.2. If he submits his
		// self-evaluation, he sill be considered 'fully submitted'. Only
		// student in Team 1.1 should receive emails.
		StudentAttributes singleStudnetInTeam1_2 = dataBundle.students
				.get("student5InCourse1");
		SubmissionAttributes sub = new SubmissionAttributes();
		sub.course = singleStudnetInTeam1_2.course;
		sub.evaluation = eval.name;
		sub.team = singleStudnetInTeam1_2.team;
		sub.reviewer = singleStudnetInTeam1_2.email;
		sub.reviewee = singleStudnetInTeam1_2.email;
		sub.points = 100;
		sub.justification = new Text("j");
		sub.p2pFeedback = new Text("y");
		ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
		submissions.add(sub);
		logic.updateSubmissions(submissions);
		emailsSent = logic.sendReminderForEvaluation(eval.courseId, eval.name);
	
		numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
		assertEquals(3+numOfInstructor, emailsSent.size());
	
		studentList = logic.getStudentsForCourse(eval.courseId);
	
		// verify 3 students in Team 1.1 received emails.
		for (StudentAttributes s : studentList) {
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
	
		try {
			logic.sendReminderForEvaluation("valid.course.id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}

	@Test
	public void testDeleteStudent() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("typical delete");

		restoreTypicalDataInDatastore();

		

		// this is the student to be deleted
		StudentAttributes student2InCourse1 = dataBundle.students
				.get("student2InCourse1");
		verifyPresentInDatastore(student2InCourse1);

		// ensure student-to-be-deleted has some submissions
		SubmissionAttributes submissionFromS1C1ToS2C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");
		verifyPresentInDatastore(submissionFromS1C1ToS2C1);

		SubmissionAttributes submissionFromS2C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");
		verifyPresentInDatastore(submissionFromS2C1ToS1C1);

		SubmissionAttributes submissionFromS1C1ToS1C1 = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submissionFromS1C1ToS1C1);

		logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);
		verifyAbsentInDatastore(student2InCourse1);

		// verify that other students in the course are intact
		StudentAttributes student1InCourse1 = dataBundle.students
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

		try {
			logic.deleteStudent(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}



	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods_______________________________() {

	}

	@Test
	public void testCreateEvaluation() throws Exception {

		restoreTypicalDataInDatastore();

		EvaluationAttributes evaluation = new EvaluationAttributes();
		evaluation.courseId = "idOfTypicalCourse1";
		evaluation.name = "new evaluation";

		______TS("typical case");

		

		restoreTypicalDataInDatastore();

		evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		verifyPresentInDatastore(evaluation);
		logic.deleteEvaluation(evaluation.courseId, evaluation.name);
		verifyAbsentInDatastore(evaluation);
		logic.createEvaluation(evaluation);
		verifyPresentInDatastore(evaluation);

		______TS("Duplicate evaluation name");

		try {
			logic.createEvaluation(evaluation);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			AssertHelper.assertContains(evaluation.name, e.getMessage());
		}

		______TS("null parameters");

		try {
			logic.createEvaluation(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("invalid parameters");

		// Only checking that exception is thrown at logic level
		evaluation.courseId = "invalid course";
		try {
			logic.createEvaluation(evaluation);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertEquals(
					String.format(COURSE_ID_ERROR_MESSAGE, evaluation.courseId, REASON_INCORRECT_FORMAT),
					e.getMessage());
		}
		// invalid values to other parameters should be checked against
		// EvaluationData.validate();

	}

	@Test
	public void testGetEvaluation() throws Exception {

		restoreTypicalDataInDatastore();

		______TS("typical case");

		restoreTypicalDataInDatastore();

		EvaluationAttributes expected = dataBundle.evaluations
				.get("evaluation1InCourse1");
		EvaluationAttributes actual = logic.getEvaluation(expected.courseId,
				expected.name);
		verifySameEvaluationData(expected, actual);

		______TS("null parameters");

		try {
			logic.getEvaluation("valid.course.id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}

		______TS("non-existent");

		assertNull(logic.getEvaluation("non-existent", expected.name));
		assertNull(logic.getEvaluation(expected.courseId, "non-existent"));

	}

	/* TODO: implement tests for the following: 
	 * 1. getEvaluationDetails()	
	 * 2. getEvaluationsListForInstructor()
	 * 3. getEvaluationDetailsForCourse()
	 */
	@Test
	public void testGetEvaluationsDetailsForInstructor() throws Exception {
	
		restoreTypicalDataInDatastore();
		String methodName = "getEvaluationsDetailsForInstructor";
		Class<?>[] paramTypes = new Class<?>[] { String.class };
	
		______TS("typical case, instructor has 3 evaluations");
	
		
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
		ArrayList<EvaluationDetailsBundle> evalList = logic
				.getEvaluationsDetailsForInstructor(instructor.googleId);
		// 2 Evals from Course 1, 1 Eval from Course  2
		assertEquals(3, evalList.size());
		EvaluationAttributes evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		for (EvaluationDetailsBundle edd : evalList) {
			if(edd.evaluation.name.equals(evaluation.name)){
				//We have, 4 students in Team 1.1 and 1 student in Team 1.2
				//Only 3 have submitted.
				assertEquals(5,edd.stats.expectedTotal);
				assertEquals(3,edd.stats.submittedTotal);
			}
		}
		
		______TS("check immunity from orphaned submissions");
		
		//move a student from Team 1.1 to Team 1.2
		StudentAttributes student = dataBundle.students.get("student4InCourse1");
		student.team = "Team 1.2";
		logic.updateStudent(student.email, student);
		
		evalList = logic.getEvaluationsDetailsForInstructor(instructor.googleId);
		assertEquals(3, evalList.size());
		
		for (EvaluationDetailsBundle edd : evalList) {
			if(edd.evaluation.name.equals(evaluation.name)){
				//Now we have, 3 students in Team 1.1 and 2 student in Team 1.2
				//Only 2 (1 less than before) have submitted 
				//   because we just moved a student to a new team and that
				//   student's previous submissions are now orphaned.
				assertEquals(5,edd.stats.expectedTotal);
				assertEquals(2,edd.stats.submittedTotal);
			}
		}
	
		______TS("instructor has 1 evaluation");
	
		
	
		InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse2");
		evalList = logic.getEvaluationsDetailsForInstructor(instructor2.googleId);
		assertEquals(1, evalList.size());
		for (EvaluationDetailsBundle edd : evalList) {
			assertTrue(logic.isInstructorOfCourse(instructor2.googleId, edd.evaluation.courseId));
		}
	
		______TS("instructor has 0 evaluations");
	
		
		
		InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
		evalList = logic.getEvaluationsDetailsForInstructor(instructor4.googleId);
		assertEquals(0, evalList.size());
	
		______TS("null parameters");
	
		try {
			logic.getEvaluationsDetailsForInstructor(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent instructor");
	
		
		
		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non-existent" });
	}

	
	
	@Test
	public void testGetEvaluationResult() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getEvaluationResult";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
	
		______TS("typical case");
	
		restoreTypicalDataInDatastore();
	
		
	
		// reconfigure points of an existing evaluation in the datastore
		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
	
		// @formatter:off
		setPointsForSubmissions(new int[][] { 
				{ 100, 100, 100, 100 },
				{ 110, 110, NSU, 110 }, 
				{ NSB, NSB, NSB, NSB },
				{ 70, 80, 110, 120 } });
		// @formatter:on
	
		EvaluationResultsBundle result = logic.getEvaluationResult(course.id,
				evaluation.name);
		print(result.toString());
	
		// no need to sort, the result should be sorted by default
	
		// check for evaluation details
		assertEquals(evaluation.courseId, result.evaluation.courseId);
		assertEquals(evaluation.name, result.evaluation.name);
		assertEquals(evaluation.startTime, result.evaluation.startTime);
		assertEquals(evaluation.endTime, result.evaluation.endTime);
		assertEquals(evaluation.gracePeriod, result.evaluation.gracePeriod);
		assertEquals(evaluation.instructions, result.evaluation.instructions);
		assertEquals(evaluation.timeZone, result.evaluation.timeZone, 0.1);
		assertEquals(evaluation.p2pEnabled, result.evaluation.p2pEnabled);
		assertEquals(evaluation.published, result.evaluation.published);
	
		// check number of teams
		assertEquals(2, result.teamResults.size());
	
		// check students in team 1.1
		TeamResultBundle team1_1 = result.teamResults.get("Team 1.1");
		assertEquals(4, team1_1.studentResults.size());
	
		int S1_POS = 0;
		int S2_POS = 1;
		int S3_POS = 2;
		int S4_POS = 3;
	
		
		StudentResultBundle srb1 = team1_1.studentResults.get(S1_POS);
		StudentResultBundle srb2 = team1_1.studentResults.get(S2_POS);
		StudentResultBundle srb3 = team1_1.studentResults.get(S3_POS);
		StudentResultBundle srb4 = team1_1.studentResults.get(S4_POS);
		
		StudentAttributes s1 = srb1.student;
		StudentAttributes s2 = srb2.student;
		StudentAttributes s3 = srb3.student;
		StudentAttributes s4 = srb4.student;
	
		assertEquals("student1InCourse1", s1.googleId);
		assertEquals("student2InCourse1", s2.googleId);
		assertEquals("student3InCourse1", s3.googleId);
		assertEquals("student4InCourse1", s4.googleId);
	
		// check self-evaluations of some students
		assertEquals(s1.name, srb1.getSelfEvaluation().details.revieweeName);
		assertEquals(s1.name, srb1.getSelfEvaluation().details.reviewerName);
		assertEquals(s3.name, srb3.getSelfEvaluation().details.revieweeName);
		assertEquals(s3.name, srb3.getSelfEvaluation().details.reviewerName);
	
		// check individual values for s1
		assertEquals(100, srb1.summary.claimedFromStudent);
		assertEquals(100, srb1.summary.claimedToInstructor);
		assertEquals(90, srb1.summary.perceivedToStudent);
		assertEquals(90, srb1.summary.perceivedToInstructor);
		// check some more individual values
		assertEquals(110, srb2.summary.claimedFromStudent);
		assertEquals(NSB, srb3.summary.claimedToInstructor);
		assertEquals(95, srb4.summary.perceivedToStudent);
		assertEquals(96, srb2.summary.perceivedToInstructor);
	
		// check outgoing submissions (s1 more intensely than others)
	
		assertEquals(4, srb1.outgoing.size());
	
		SubmissionAttributes s1_s1 = srb1.outgoing.get(S1_POS);
		assertEquals(100, s1_s1.details.normalizedToInstructor);
		String expected = "justification of student1InCourse1 rating to student1InCourse1";
		assertEquals(expected, s1_s1.justification.getValue());
		expected = "student1InCourse1 view of team dynamics";
		assertEquals(expected, s1_s1.p2pFeedback.getValue());
	
		SubmissionAttributes s1_s2 = srb1.outgoing.get(S2_POS);
		assertEquals(100, s1_s2.details.normalizedToInstructor);
		expected = "justification of student1InCourse1 rating to student2InCourse1";
		assertEquals(expected, s1_s2.justification.getValue());
		expected = "comments from student1InCourse1 to student2InCourse1";
		assertEquals(expected, s1_s2.p2pFeedback.getValue());
	
		assertEquals(100, srb1.outgoing.get(S3_POS).details.normalizedToInstructor);
		assertEquals(100, srb1.outgoing.get(S4_POS).details.normalizedToInstructor);
	
		assertEquals(NSU, srb2.outgoing.get(S3_POS).details.normalizedToInstructor);
		assertEquals(100, srb2.outgoing.get(S4_POS).details.normalizedToInstructor);
		assertEquals(NSB, srb3.outgoing.get(S2_POS).details.normalizedToInstructor);
		assertEquals(84, srb4.outgoing.get(S2_POS).details.normalizedToInstructor);
	
		// check incoming submissions (s2 more intensely than others)
	
		assertEquals(4, srb1.incoming.size());
		assertEquals(90, srb1.incoming.get(S1_POS).details.normalizedToStudent);
		assertEquals(100, srb1.incoming.get(S4_POS).details.normalizedToStudent);
	
		SubmissionAttributes s2_s1 = srb1.incoming.get(S2_POS);
		assertEquals(96, s2_s1.details.normalizedToStudent);
		expected = "justification of student2InCourse1 rating to student1InCourse1";
		assertEquals(expected, s2_s1.justification.getValue());
		expected = "comments from student2InCourse1 to student1InCourse1";
		assertEquals(expected, s2_s1.p2pFeedback.getValue());
		assertEquals(115, srb2.incoming.get(S4_POS).details.normalizedToStudent);
	
		SubmissionAttributes s3_s1 = srb1.incoming.get(S3_POS);
		assertEquals(113, s3_s1.details.normalizedToStudent);
		assertEquals("", s3_s1.justification.getValue());
		assertEquals("", s3_s1.p2pFeedback.getValue());
		assertEquals(113, srb3.incoming.get(S3_POS).details.normalizedToStudent);
	
		assertEquals(108, srb4.incoming.get(S3_POS).details.normalizedToStudent);
	
		// check team 1.2
		TeamResultBundle team1_2 = result.teamResults.get("Team 1.2");
		assertEquals(1, team1_2.studentResults.size());
		StudentResultBundle team1_2studentResult = team1_2.studentResults.get(0);
		assertEquals(NSB, team1_2studentResult.summary.claimedFromStudent);
		assertEquals(1, team1_2studentResult.outgoing.size());
		assertEquals(NSB, team1_2studentResult.summary.claimedToInstructor);
		assertEquals(NSB, team1_2studentResult.outgoing.get(0).points);
		assertEquals(NA, team1_2studentResult.incoming.get(0).details.normalizedToStudent);
	
		______TS("null parameters");
	
		try {
			logic.getEvaluationResult("valid.course.id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
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
	public void testGetEvaluationResultSummaryAsCsv() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		String methodName = "getEvaluationResultSummaryAsCsv";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
	
		______TS("typical case");
	
		restoreTypicalDataInDatastore();
	
		
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		
		String export = logic.getEvaluationResultSummaryAsCsv(eval.courseId, eval.name);
		
		// This is what export should look like:
		// ==================================
		//Course,,idOfTypicalCourse1
		//Evaluation Name,,evaluation1 In Course1
		//
		//Team,,Student,,Claimed,,Perceived,,Received
		//Team 1.1,,student1 In Course1,,100,,100,,100,100,-9999
		//Team 1.1,,student2 In Course1,,-999,,100,,100,-9999,-9999
		//Team 1.1,,student3 In Course1,,-999,,100,,100,-9999,-9999
		//Team 1.1,,student4 In Course1,,-999,,100,,100,-9999,-9999
		//Team 1.2,,student5 In Course1,,-999,,-9999,,
		
		String[] exportLines = export.split(Const.EOL);
		assertEquals("Course,," + eval.courseId, exportLines[0]);
		assertEquals("Evaluation Name,," + eval.name, exportLines[1]);
		assertEquals("", exportLines[2]);
		assertEquals("Team,,Student,,Claimed,,Perceived,,Received", exportLines[3]);
		assertEquals("Team 1.1,,student1 In Course1,,100,,100,,100,100,N/A", exportLines[4]);
		assertEquals("Team 1.1,,student2 In Course1,,Not Submitted,,100,,100,N/A,N/A", exportLines[5]);
		assertEquals("Team 1.1,,student3 In Course1,,Not Submitted,,100,,100,N/A,N/A", exportLines[6]);
		assertEquals("Team 1.1,,student4 In Course1,,Not Submitted,,100,,100,N/A,N/A", exportLines[7]);
		assertEquals("Team 1.2,,student5 In Course1,,Not Submitted,,N/A,,", exportLines[8]);
		
		______TS("Non-existent Course/Eval");
		
		verifyEntityDoesNotExistException(methodName, paramTypes,
				new Object[] { "non.existent", "Non Existent" });
		
		______TS("Null parameters");
		
		try {
			logic.getEvaluationResultSummaryAsCsv(null, eval.name);
			Assert.fail();
		} catch (AssertionError ae) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
		}
		
		try {
			logic.getEvaluationResultSummaryAsCsv(eval.courseId, null);
			Assert.fail();
		} catch (AssertionError ae) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
		}
	}

	@Test
	public void testGetEvauationResultForStudent() throws Exception {
	
		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
		String student1email = "student1InCourse1@gmail.com";
	
		restoreTypicalDataInDatastore();
		
		String methodName = "getEvaluationResultForStudent";
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class,
				String.class };
	
		______TS("typical case");
	
		// reconfigure points of an existing evaluation in the datastore
		restoreTypicalDataInDatastore();
		course = dataBundle.courses.get("typicalCourse1");
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		student1email = "student1InCourse1@gmail.com";
	
		
	
		// @formatter:off
		setPointsForSubmissions(new int[][] 
				{ { 100, 100, 100, 100 },
				  { 110, 110, NSU, 110 }, 
				  { NSB, NSB, NSB, NSB },
				  { 70, 80, 110, 120 } });
		// @formatter:on
	
		// "idOfCourse1OfInstructor1", "evaluation1 In Course1",
	
		StudentResultBundle result = logic.getEvaluationResultForStudent(course.id,
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
		assertEquals(100, result.summary.claimedFromStudent);
		assertEquals(100, result.summary.claimedToInstructor);
		assertEquals(90, result.summary.perceivedToInstructor);
		assertEquals(90, result.summary.perceivedToStudent);
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
		assertEquals(113, result.incoming.get(0).details.normalizedToStudent); // reviewer=student3
		assertEquals(
				"justification of student1InCourse1 rating to student1InCourse1",
				result.selfEvaluations.get(0).justification.getValue()); // student2
	
		______TS("null parameter");
	
		try {
			logic.getEvaluationResultForStudent("valid.course.id", "valid evaluation name", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
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
			Assert.fail();
		} catch (EntityDoesNotExistException e) {
			AssertHelper.assertContains("non-existent@email.com", e
					.getMessage().toLowerCase());
		}
	
		______TS("student added after evaluation");
	
		// testCreateStudent verifies adding student mid-evaluation creates
		//   additional submissions correctly. No need to check here.
	
	}

	@Test
	public void testUpdateEvaluation() throws Exception {

		restoreTypicalDataInDatastore();

		EvaluationAttributes eval = new EvaluationAttributes();
		eval.courseId = "idOfTypicalCourse1";
		eval.name = "new evaluation";
		eval.instructions = new Text("inst");
		Date dummyTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		eval.startTime = dummyTime;
		eval.endTime = dummyTime;

		______TS("typical case");

		restoreTypicalDataInDatastore();

		

		eval = dataBundle.evaluations.get("evaluation1InCourse1");
		eval.gracePeriod = eval.gracePeriod + 1;
		eval.instructions = new Text(eval.instructions.getValue() + "x");
		eval.p2pEnabled = (!eval.p2pEnabled);
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		//we don't modify derived attributes here because they cannot be updated this way.
		invokeEditEvaluation(eval);

		verifyPresentInDatastore(eval);

		______TS("null parameters");

		try {
			logic.updateEvaluation(null,
									"valid evaluation name",
									"valid instructions",
									new Date(),
									new Date(),
									1.00,
									1,
									true);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}

		______TS("invalid parameters");

		// make the evaluation invalid (end time is before start time)
		eval.timeZone = 0;
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		try {
			invokeEditEvaluation(eval);
			Assert.fail();
		} catch (InvalidParametersException e) {
			String errorMessage = String.format(TIME_FRAME_ERROR_MESSAGE,
					END_TIME_FIELD_NAME, EVALUATION_NAME, START_TIME_FIELD_NAME) ;
			assertEquals(errorMessage, e.getMessage());
		}

		// Checking for other type of invalid parameter situations
		// is done in EvaluationDataTest

	}

	@Test
	public void testPublishAndUnpublishEvaluation() throws Exception {

		restoreTypicalDataInDatastore();

		String[] methodNames = new String[] { "publishEvaluation",
				"unpublishEvaluation" };
		Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
		Object[] params = new Object[] { "idOfTypicalCourse1",
				"new evaluation" };


		______TS("typical cases");

		restoreTypicalDataInDatastore();

		

		EvaluationAttributes eval1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		// ensure not published yet
		assertEquals(false,
				logic.getEvaluation(eval1.courseId, eval1.name).published);
		// ensure CLOSED
		eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, eval1.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.updateEvaluation(eval1);

		logic.publishEvaluation(eval1.courseId, eval1.name);
		assertEquals(true,
				logic.getEvaluation(eval1.courseId, eval1.name).published);

		logic.unpublishEvaluation(eval1.courseId, eval1.name);
		assertEquals(false,
				logic.getEvaluation(eval1.courseId, eval1.name).published);

		______TS("not ready for publishing");

		// make the evaluation OPEN
		eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.OPEN, eval1.getStatus());
		backDoorLogic.updateEvaluation(eval1);

		try {
			logic.publishEvaluation(eval1.courseId, eval1.name);
			Assert.fail();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(Const.StatusCodes.PUBLISHED_BEFORE_CLOSING,
					e.errorCode);
		}

		// ensure evaluation stays in the same state
		assertEquals(EvalStatus.OPEN,
				logic.getEvaluation(eval1.courseId, eval1.name).getStatus());

		______TS("not ready for unpublishing");

		try {
			logic.unpublishEvaluation(eval1.courseId, eval1.name);
			Assert.fail();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(Const.StatusCodes.UNPUBLISHED_BEFORE_PUBLISHING,
					e.errorCode);
		}

		// ensure evaluation stays in the same state
		assertEquals(EvalStatus.OPEN,
				logic.getEvaluation(eval1.courseId, eval1.name).getStatus());

		______TS("non-existent");

		for (int i = 0; i < params.length; i++) {
			verifyEntityDoesNotExistException(methodNames[i], paramTypes,
					new Object[] { "non-existent", "non-existent" });
		}
		______TS("null parameters");
		
		// Same as entity does not exist
		try {
			logic.publishEvaluation(null, eval1.name);
			Assert.fail();
		} catch (AssertionError a) {
		}
		
		try {
			logic.unpublishEvaluation(eval1.courseId, null);
			Assert.fail();
		} catch (AssertionError a) {
		}

	}


	
	
	@Test
	public void testDeleteEvaluation() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("typical delete");
	
		restoreTypicalDataInDatastore();
		
	
		EvaluationAttributes eval = dataBundle.evaluations
				.get("evaluation1InCourse1");
		verifyPresentInDatastore(eval);
		// verify there are submissions under this evaluation
		SubmissionAttributes submission = dataBundle.submissions
				.get("submissionFromS1C1ToS1C1");
		verifyPresentInDatastore(submission);
	
		logic.deleteEvaluation(eval.courseId, eval.name);
		verifyAbsentInDatastore(eval);
		// verify submissions are deleted too
		ArrayList<SubmissionAttributes> submissionsOfEvaluation = new ArrayList<SubmissionAttributes>(dataBundle.submissions.values());
		for (SubmissionAttributes s : submissionsOfEvaluation) {
			if (s.evaluation.equals(eval.name)) {
				verifyAbsentInDatastore(s);
			}
		}
	
		______TS("null parameters");
	
		try {
			logic.deleteEvaluation("valid.course.id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent");
	
		// should fail silently
		logic.deleteEvaluation("non-existent", eval.name);
		logic.deleteEvaluation(eval.courseId, "non-existent");
	
	}

	
	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods_______________________________() {
	}

	@Test
	public void testCreateSubmission() {
		// method not implemented
	}


	@Test
	public void testGetSubmissionsForEvaluationFromStudent() throws Exception {
	
		restoreTypicalDataInDatastore();
	
		______TS("typical case");
	
		restoreTypicalDataInDatastore();
	
		
	
		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
		// reuse this evaluation data to create a new one
		evaluation.name = "new evaluation";
		logic.createEvaluationWithoutSubmissionQueue(evaluation);
		// this is the student we are going to check
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
	
		List<SubmissionAttributes> submissions = logic.getSubmissionsForEvaluationFromStudent(
				evaluation.courseId, evaluation.name, student.email);
		// there should be 4 submissions as this student is in a 4-person team
		assertEquals(4, submissions.size());
		// verify they all belong to this student
		for (SubmissionAttributes s : submissions) {
			assertEquals(evaluation.courseId, s.course);
			assertEquals(evaluation.name, s.evaluation);
			assertEquals(student.email, s.reviewer);
			assertEquals(student.name, s.details.reviewerName);
			assertEquals(logic.getStudentForEmail(evaluation.courseId, s.reviewee).name,
					s.details.revieweeName);
		}
	
		______TS("orphan submissions");
	
		//Move student to a new team
		student.team = "Team 1.3";
		logic.updateStudent(student.email, student);
		
		submissions = logic.getSubmissionsForEvaluationFromStudent(
				evaluation.courseId, evaluation.name, student.email);
		//There should be 1 submission as he is now in a 1-person team.
		//   Orphaned submissions from previous team should not be returned.
				assertEquals(1, submissions.size());
				
		// Move the student out and move in again
		student.team = "Team 1.4";
		logic.updateStudent(student.email, student);
		student.team = "Team 1.3";
		logic.updateStudent(student.email, student);
		submissions = logic.getSubmissionsForEvaluationFromStudent(evaluation.courseId,
				evaluation.name, student.email);
		assertEquals(1, submissions.size());
	
		______TS("null parameters");
		
		try {
			logic.getSubmissionsForEvaluationFromStudent("valid.course.id", "valid evaluation name", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("course/evaluation/student does not exist");
	
		
		assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
				"non-existent", evaluation.name, student.email ).size());
	
		assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
				evaluation.courseId, "non-existent", student.email ).size());
	
		assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
				evaluation.courseId, evaluation.name, "non-existent" ).size());
	}

	@Test
	public void testHasStudentSubmittedEvaluation() throws Exception {
	
		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
	
		restoreTypicalDataInDatastore();
	
		______TS("student has submitted");
	
		
	
		assertEquals(true, logic.hasStudentSubmittedEvaluation(
				evaluation.courseId, evaluation.name, student.email));
	
		______TS("student has not submitted");
	
		// create a new evaluation reusing data from previous one
		evaluation.name = "New evaluation";
		logic.createEvaluation(evaluation);
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.courseId, evaluation.name, student.email));
	
		______TS("null parameters");
	
		try {
			logic.hasStudentSubmittedEvaluation("valid.course.id", "valid evaluation name", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	
		______TS("non-existent course/evaluation/student");
	
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				"non-existent-course", evaluation.name, student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.courseId, "non-existent-eval", student.email));
		assertEquals(false, logic.hasStudentSubmittedEvaluation(
				evaluation.courseId, evaluation.name, "non-existent@student"));
	
	}

	@Test
	public void testUpdateSubmissions() throws Exception {

		______TS("typical cases");

		restoreTypicalDataInDatastore();
		

		ArrayList<SubmissionAttributes> submissionContainer = new ArrayList<SubmissionAttributes>();

		// try without empty list. Nothing should happen
		logic.updateSubmissions(submissionContainer);

		SubmissionAttributes sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		SubmissionAttributes sub2 = dataBundle.submissions
				.get("submissionFromS2C1ToS1C1");

		// checking editing of one of the submissions
		alterSubmission(sub1);

		submissionContainer.add(sub1);
		logic.updateSubmissions(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		// check editing both submissions
		alterSubmission(sub1);
		alterSubmission(sub2);

		submissionContainer = new ArrayList<SubmissionAttributes>();
		submissionContainer.add(sub1);
		submissionContainer.add(sub2);
		logic.updateSubmissions(submissionContainer);

		verifyPresentInDatastore(sub1);
		verifyPresentInDatastore(sub2);

		______TS("non-existent evaluation");

		// already tested under testUpdateSubmission()

		______TS("null parameter");

		try {
			logic.updateSubmissions(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}



	@Test
	public void testDeleteSubmission() {
		// method not implemented
	}
	
	/* TODO: implement tests for the following :
	 * 1. getFeedbackSessionDetails()
	 * 2. getFeedbackSessionsListForInstructor()
	 */
	
	@SuppressWarnings("unused")
	private void ____COMMENT_level_methods_____________________________() {
		//The tests here are only for null params check,
		//the rest are done in CommentsLogicTest
	}
	
	@Test
	public void testCreateComment() throws Exception{
		______TS("null parameters");
		
		try {
			logic.createComment(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		
	}
	
	@Test
	public void testUpdateComment() throws Exception{
		______TS("null parameters");
		
		try {
			logic.updateComment(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteComment() throws Exception{
		______TS("null parameters");
		
		try {
			logic.deleteComment(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	@Test
	public void testGetCommentsForGiver() throws Exception{
		______TS("null parameters");
		
		try {
			logic.getCommentsForGiver(null, "giver@mail.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		try {
			logic.getCommentsForGiver("course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	@Test
	public void testGetCommentsForReceiver() throws Exception{
		______TS("null parameters");
		
		try {
			logic.getCommentsForReceiver(null, "receiver@mail.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		try {
			logic.getCommentsForReceiver("course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	@Test
	public void testGetCommentsForGiverAndReceiver() throws Exception{
		______TS("null parameters");
		
		try {
			logic.getCommentsForGiverAndReceiver(null, "giver@mail.com", "receiver@mail.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		try {
			logic.getCommentsForGiverAndReceiver("course-id", null, "receiver@mail.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
		try {
			logic.getCommentsForGiverAndReceiver("course-id", "giver@mail.com", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
		}
	}
	
	
	@SuppressWarnings("unused")
	private void ____MISC_methods_________________________________________() {
	}
	
	@Test 
	public void testSendEmailErrorReport(){
		//tested elsewhere
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
	
	public static void verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(
			List<SubmissionAttributes> submissionList, String courseId) throws EntityDoesNotExistException {
		CourseDetailsBundle course = CoursesLogic.inst().getCourseDetails(courseId);
		List<StudentAttributes> students = studentsDb.getStudentsForCourse(courseId);

		for(EvaluationDetailsBundle e: course.evaluations){
			verifySubmissionsExistForCurrentTeamStructureInEvaluation(e.evaluation.name, students, submissionList);
		}
	}
	
	public static void verifySubmissionsExistForCurrentTeamStructureInEvaluation(String evaluationName,
			List<StudentAttributes> students, List<SubmissionAttributes> submissions) {

		for (StudentAttributes reviewer : students) {
			for (StudentAttributes reviewee : students) {
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
	public static void verifySubmissionExists(String evaluationName, String reviewer,
			String reviewee, String team, List<SubmissionAttributes> submissions) {
		int count = 0;
		for (SubmissionAttributes s : submissions) {
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

	public static MimeMessage getEmailToStudent(StudentAttributes s,
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

	private void verifyJoinInviteToStudent(StudentAttributes student,
			MimeMessage email) throws MessagingException {
		assertEquals(student.email, email.getAllRecipients()[0].toString());
		AssertHelper.assertContains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN,
				email.getSubject());
		AssertHelper.assertContains(student.course, email.getSubject());
	}

	private void verifyEvaluationInfoExistsInList(EvaluationAttributes evaluation,
			ArrayList<EvaluationDetailsBundle> evalInfoList) {

		for (EvaluationDetailsBundle edd : evalInfoList) {
			if (edd.evaluation.name.equals(evaluation.name))
				return;
		}
		Assert.fail("Did not find " + evaluation.name + " in the evaluation info list");
	}

	public static void verifyEnrollmentResultForStudent(StudentAttributes expectedStudent,
			StudentAttributes enrollmentResult, StudentAttributes.UpdateStatus status) {
		String errorMessage = "mismatch! \n expected:\n"
				+ Utils.getTeammatesGson().toJson(expectedStudent)
				+ "\n actual \n"
				+ Utils.getTeammatesGson().toJson(enrollmentResult);
		assertEquals(errorMessage, true,
				enrollmentResult.isEnrollInfoSameAs(expectedStudent) &&
				enrollmentResult.updateStatus == status);
	}

	public static void verifyAbsentInDatastore(AccountAttributes account)
			throws Exception {
		assertEquals(null, logic.getAccount(account.googleId));
	}

	
	public static void verifyAbsentInDatastore(SubmissionAttributes submission)
			throws Exception {
		assertEquals(
				null,
				invokeGetSubmission(submission.course, submission.evaluation,
						submission.reviewer, submission.reviewee));
	}

	public static void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
		assertEquals(null, instructorsDb.getInstructorForGoogleId(expectedInstructor.courseId, expectedInstructor.googleId));
	}

	public static void verifyAbsentInDatastore(CourseAttributes course) {
		assertEquals(null, coursesDb.getCourse(course.id));
	}

	public static void verifyAbsentInDatastore(StudentAttributes student) {
		assertEquals(null, logic.getStudentForEmail(student.course, student.email));
	}

	private void verifyAbsentInDatastore(EvaluationAttributes evaluation) {
		assertEquals(null,
				logic.getEvaluation(evaluation.courseId, evaluation.name));
	}
	
	public static void verifyAbsentInDatastore(FeedbackSessionAttributes fsa) {
		assertEquals(null,
				fsDb.getFeedbackSession(fsa.courseId, fsa.feedbackSessionName));	
	}
	
	public static void verifyAbsentInDatastore(FeedbackQuestionAttributes fqa) {
		assertEquals(null,
				fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber));	
	}
	
	public static void verifyAbsentInDatastore(CommentAttributes comment) {
		assertEquals(null, commentsDb.getComment(comment.courseId,
				comment.giverEmail, comment.receiverEmail, comment.commentText, comment.createdAt));
	}
	
	//TODO: move these verify methods to a utility class
	
	public static void verifyPresentInDatastore(AccountAttributes expectedAccount) {
		AccountAttributes actualAccount = logic.getAccount(expectedAccount.googleId);
		// Account when created by createInstructor may take up different values in NAME and EMAIL
		// from the typicalDataBundle. Hence we only check that the account exists in the DataStore
		assertTrue(actualAccount != null);
	}

	public static void verifyPresentInDatastore(StudentAttributes expectedStudent) {
		StudentAttributes actualStudent = studentsDb.getStudentForEmail(expectedStudent.course,
				expectedStudent.email);
		expectedStudent.updateStatus = UpdateStatus.UNKNOWN;
		equalizeIrrelevantData(expectedStudent, actualStudent);
		assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
	}

	public static void verifyPresentInDatastore(SubmissionAttributes expected)
			throws Exception {
		SubmissionAttributes actual = invokeGetSubmission(expected.course,
				expected.evaluation, expected.reviewer, expected.reviewee);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(EvaluationAttributes expected) {
		EvaluationAttributes actual = evaluationsDb.getEvaluation(expected.courseId,
				expected.name);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(CourseAttributes expected) {
		CourseAttributes actual = coursesDb.getCourse(expected.id);
		// Ignore time field as it is stamped at the time of creation in testing
		actual.createdAt = expected.createdAt;
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(InstructorAttributes expected) {
		InstructorAttributes actual = instructorsDb.getInstructorForGoogleId(
				expected.courseId, expected.googleId);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}
	
	public static void verifyPresentInDatastore(FeedbackSessionAttributes expected) {
		FeedbackSessionAttributes actual = fsDb.getFeedbackSession(expected.courseId, expected.feedbackSessionName);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}

	public static void verifyPresentInDatastore(FeedbackQuestionAttributes expected) {
		FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(
				expected.feedbackSessionName, expected.courseId, expected.questionNumber);
		assertEquals(gson.toJson(expected), gson.toJson(actual));
	}
	
	public static void verifyPresentInDatastore(CommentAttributes expected){
		CommentAttributes actual = commentsDb.getComment(expected.courseId, expected.giverEmail, expected.receiverEmail, expected.commentText, expected.createdAt);
		assertEquals(expected.courseId, actual.courseId);
		assertEquals(expected.giverEmail, actual.giverEmail);
		assertEquals(expected.receiverEmail, actual.receiverEmail);
		assertEquals(expected.commentText, actual.commentText);
	}
	
	public static void verifySameEvaluationData(EvaluationAttributes expected,
			EvaluationAttributes actual) {
		assertEquals(expected.courseId, actual.courseId);
		assertEquals(expected.name, actual.name);
		AssertHelper.assertSameDates(expected.startTime, actual.startTime);
		AssertHelper.assertSameDates(expected.endTime, actual.endTime);
		assertEquals(expected.timeZone, actual.timeZone, 0.1);
		assertEquals(expected.instructions, actual.instructions);
		assertEquals(expected.p2pEnabled, actual.p2pEnabled);
		assertEquals(expected.published, actual.published);
		assertEquals(expected.activated, actual.activated);
	}


	private void verifyEntityDoesNotExistException(String methodName,
			Class<?>[] paramTypes, Object[] params) throws Exception {

		Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

		try {
			method.setAccessible(true); // in case it is a private method
			method.invoke(logic, params);
			Assert.fail();
		} catch (Exception e) {
			assertEquals(EntityDoesNotExistException.class, e.getCause()
					.getClass());
		}
	}
	
	private static void equalizeIrrelevantData(
			StudentAttributes expectedStudent,
			StudentAttributes actualStudent) {
		
		// For these fields, we consider null and "" equivalent.
		if ((expectedStudent.googleId == null) && (actualStudent.googleId.equals(""))) {
			actualStudent.googleId = null;
		}
		if ((expectedStudent.team == null) && (actualStudent.team.equals(""))) {
			actualStudent.team = null;
		}
		if ((expectedStudent.comments == null)
				&& (actualStudent.comments.equals(""))) {
			actualStudent.comments = null;
		}

		// pretend keys match because the key is generated on the server side
		// and cannot be anticipated
		if ((actualStudent.key != null)) {
			expectedStudent.key = actualStudent.key;
		}
	}

	@SuppressWarnings("unused")
	private void ____invoking_private_methods__() {
	}


	private void invokeEditEvaluation(EvaluationAttributes e)
			throws InvalidParametersException, EntityDoesNotExistException {
		logic.updateEvaluation(e.courseId, e.name, e.instructions.getValue(), e.startTime,
				e.endTime, e.timeZone, e.gracePeriod, e.p2pEnabled);
	}


	public static SubmissionAttributes invokeGetSubmission(String course,
			String evaluation, String reviewer, String reviewee)
			throws Exception {
		Method privateMethod = BackDoorLogic.class.getDeclaredMethod("getSubmission",
				new Class[] { String.class, String.class, String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { course, evaluation, reviewer, reviewee };
		return (SubmissionAttributes) privateMethod.invoke(new BackDoorLogic(), params);
	}


	@SuppressWarnings("unused")
	private void ____test_object_manipulation_methods__() {
	}

	private void createNewEvaluationWithSubmissions(String courseId,
			String evaluationName, int[][] input)
			throws EntityAlreadyExistsException, InvalidParametersException,
			EntityDoesNotExistException {
		// create course
		
		logic.createAccount("instructorForTestingER", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("instructorForTestingER", courseId,
				"Course For Testing Evaluation Results");
		// create students
		int teamSize = input.length;
		String teamName = "team1";
		for (int i = 0; i < teamSize; i++) {
			StudentAttributes student = new StudentAttributes();
			int studentNumber = i + 1;
			student.email = "s" + studentNumber + "@gmail.com";
			student.name = "Student " + studentNumber;
			student.team = teamName;
			student.course = courseId;
			student.comments = "";
			student.googleId="";
			logic.createStudent(student);
		}
		// create evaluation
		EvaluationAttributes e = new EvaluationAttributes();
		e.courseId = courseId;
		e.name = evaluationName;
		e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
		e.gracePeriod = 0;
		e.instructions = new Text("instructions for " + e.name);
		logic.createEvaluationWithoutSubmissionQueue(e);
		// create submissions
		ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes sub = new SubmissionAttributes();
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
		logic.updateSubmissions(submissions);
	}

	public static void alterSubmission(SubmissionAttributes submission) {
		submission.points = submission.points + 10;
		submission.p2pFeedback = new Text(submission.p2pFeedback.getValue()
				+ "x");
		submission.justification = new Text(submission.justification.getValue()
				+ "y");
	}

	private void setPointsForSubmissions(int[][] points) throws Exception {
		int teamSize = points.length;
		ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes s = invokeGetSubmission("idOfTypicalCourse1",
						"evaluation1 In Course1", "student" + (i + 1)
								+ "InCourse1@gmail.com", "student" + (j + 1)
								+ "InCourse1@gmail.com");
				s.points = points[i][j];
				submissions.add(s);
			}
		}
		logic.updateSubmissions(submissions);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(Logic.class);
	}

}
