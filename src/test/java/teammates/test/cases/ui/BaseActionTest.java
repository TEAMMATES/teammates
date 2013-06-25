package teammates.test.cases.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.test.cases.BaseComponentTest;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionFactory;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;

/**
 * Parent class for *ActionTest classes.
 */
public class BaseActionTest extends BaseComponentTest {
	
	private DataBundle data = getTypicalDataBundle();

	/** 
	 * @param parameters Parameters that appear in a HttpServletRequest 
	 * received by the app.
	 * @return an {@link Action} object that matches the parameters given.
	 */
	protected Action getActionObject(String... parameters)
			throws IOException,
			MalformedURLException {
		WebRequest request = new PostMethodWebRequest(
				"http://localhost:8888" + URI);
		for (int i = 0; i < parameters.length; i = i + 2) {
			request.setParameter(parameters[i], parameters[i + 1]);
		}

		InvocationContext ic = sc.newInvocation(request);
		HttpServletRequest req = ic.getRequest();
		return ActionFactory.getAction(req);
	}

	protected ShowPageResult getShowPageResult(Action a)
			throws EntityDoesNotExistException, InvalidParametersException {
		return (ShowPageResult) a.executeAndPostProcess();
	}

	/**
	 * @return The {@code params} array with the {@code userId} inserted at the beginning.
	 */
	protected String[] addUserIdToParams(String userId, String[] params) {
		List<String> list = new ArrayList<String>();
		list.add(Common.PARAM_USER_ID);
		list.add(userId);
		for (String s : params) {
			list.add(s);
		}
		return list.toArray(new String[list.size()]);
	}

	protected String[] createParamsForTypicalEval(String courseId, String evalName) {
		
		return new String[]{
				Common.PARAM_COURSE_ID, courseId,
				Common.PARAM_EVALUATION_NAME, evalName,
				Common.PARAM_EVALUATION_COMMENTSENABLED, "true",
				Common.PARAM_EVALUATION_START, "01/01/2015",
				Common.PARAM_EVALUATION_STARTTIME, "0",
				Common.PARAM_EVALUATION_DEADLINE, "01/01/2015",
				Common.PARAM_EVALUATION_DEADLINETIME, "0",
				Common.PARAM_EVALUATION_TIMEZONE, "0",
				Common.PARAM_EVALUATION_GRACEPERIOD, "0",
				Common.PARAM_EVALUATION_INSTRUCTIONS, "ins"
		};
	}
	
	/**
	 * Verifies that the {@code parameters} violates an assumption of the 
	 * matching {@link Action}. e.g., missing a compulsory parameter.
	 */
	protected void verifyAssumptionFailure(String... parameters) throws Exception {
		try {
			Action c = getActionObject(parameters);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
	}

	/*
	 * 'high-level' here means it tests access control of an action for the 
	 * full range of user types.
	 */
	@SuppressWarnings("unused")
	private void __________high_level_access_controll_checks(){};
	
	protected void verifyOnlyAdminsCanAccess(String[] submissionParams) throws Exception {
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyUnaccessibleForInstructors(submissionParams);
		//we omit checking for admin access because these are covered by UI tests
	}
	
	protected void verifyOnlyLoggedInUsersCanAccess(String[] submissionParams) throws Exception{
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyAccessibleForUnregisteredUsers(submissionParams);
		verifyAccessibleForStudents(submissionParams);
		verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
		//No need to check for instructors of the same course since even other instructors can access.
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
	}
	
	protected void verifyOnlyInstructorsCanAccess(String[] submissionParams) throws Exception{
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
	}
	
	protected void verifyOnlyInstructorsOfTheSameCourseCanAccess(String[] submissionParams)
			throws Exception {
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudents(submissionParams);
		verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
		verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
	}
	
	protected void verifyOnlyStudentsOfTheSameCourseCanAccess(String[] submissionParams)
			throws Exception {
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		verifyUnaccessibleForInstructors(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
	}
	
	/*
	 * 'mid-level' here means it tests access control of an action for 
	 * one user types.
	 */
	@SuppressWarnings("unused")
	private void __________mid_level_access_controll_checks(){};
	
	protected void verifyAccessibleForUnregisteredUsers(String[] submissionParams) throws Exception {
		
		______TS("non-registered users can access");
		
		String	unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginUser(unregUserId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}

	protected void verifyAccessibleForStudentsOfTheSameCourse(String[] submissionParams) throws Exception {
		
		______TS("students of the same course can access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginAsStudent(student1InCourse1.googleId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}

	protected void verifyAccessibleForStudents(String[] submissionParams) throws Exception {
		
		______TS("students can access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		StudentAttributes otherStudent = data.students.get("student1InCourse2");
		
		loginAsStudent(student1InCourse1.googleId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		verifyCannotMasquerade(addUserIdToParams(otherStudent.googleId,submissionParams));
		
	}

	protected void verifyAccessibleForInstructorsOfTheSameCourse(String[] submissionParams) throws Exception {
		
		______TS("course instructor can access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
		
		loginAsInstructor(instructor1OfCourse1.googleId);
		verifyCanAccess(submissionParams);
		
		verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		verifyCannotMasquerade(addUserIdToParams(otherInstructor.googleId,submissionParams));
		
	}

	protected void verifyAccessibleForInstructorsOfOtherCourses(String[] submissionParams) throws Exception {
		
		______TS("other course instructor can access");
	
		InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
		
		loginAsInstructor(otherInstructor.googleId);
		verifyCanAccess(submissionParams);
	}

	protected void verifyAccessibleForAdminToMasqueradeAsInstructor(String[] submissionParams) throws Exception {
		
		______TS("admin can access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		
		loginAsAdmin("admin.user");
		//not checking for non-masquerade mode because admin may not be an instructor
		verifyCanMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}

	protected void verifyAccessibleForAdminToMasqueradeAsStudent(String[] submissionParams) throws Exception {
		
		______TS("admin can access");
		
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginAsAdmin("admin.user");
		//not checking for non-masquerade mode because admin may not be a student
		verifyCanMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		
	}

	protected void verifyUnaccessibleWithoutLogin(String[] submissionParams) throws Exception {
		
		______TS("not-logged-in users cannot access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}
	
	protected void verifyUnaccessibleForUnregisteredUsers(String[] submissionParams) throws Exception {
		
		______TS("non-registered users cannot access");
		
		String	unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginUser(unregUserId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}
	
	protected void verifyUnaccessibleForStudents(String[] submissionParams) throws Exception {
		
		______TS("students cannot access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginAsStudent(student1InCourse1.googleId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
	}
	
	protected void verifyUnaccessibleForStudentsOfOtherCourses(String[] submissionParams) throws Exception {
		
		______TS("students of other courses cannot access");
	
		StudentAttributes studentInOtherCourse = data.students.get("student1InCourse2");
		
		loginAsStudent(studentInOtherCourse.googleId);
		verifyCannotAccess(submissionParams);
	}

	protected void verifyUnaccessibleForDifferentStudentOfTheSameCourses(String[] submissionParams) throws Exception {
		
		______TS("other students of the same course cannot access");
	
		StudentAttributes differentStudentInSameCourse = data.students.get("student2InCourse1");
		
		loginAsStudent(differentStudentInSameCourse.googleId);
		verifyCannotAccess(submissionParams);
	}

	protected void verifyUnaccessibleForInstructors(String[] submissionParams) throws Exception {
		
		______TS("instructors cannot access");
		
		InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
		
		loginAsInstructor(instructor1OfCourse1.googleId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId,submissionParams));
		
	}
	
	protected void verifyUnaccessibleForInstructorsOfOtherCourses(String[] submissionParams) throws Exception {
		
		______TS("other course instructor cannot access");
	
		InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
		
		loginAsInstructor(otherInstructor.googleId);
		verifyCannotAccess(submissionParams);
	}
	
	/*
	 * 'low-level' here means it tests an action once with the given parameters.
	 * These methods are not aware of the user type.
	 */
	@SuppressWarnings("unused")
	private void __________low_level_access_controll_checks(){};
	
	/**
	 * Verifies that the {@link Action} matching the {@code params} is 
	 * accessible to the logged in user. 
	 */
	protected void verifyCanAccess(String... params) throws Exception {
		Action c = getActionObject(params);
		c.executeAndPostProcess();
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is
	 * accessible to the logged in user masquerading as another user. 
	 */
	protected void verifyCanMasquerade(String... params) throws Exception {
		Action c = getActionObject(params);
		c.executeAndPostProcess();
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is not
	 * accessible to the logged in user. 
	 */
	protected void verifyCannotAccess(String... params) throws Exception {
		try {
			Action c = getActionObject(params);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (UnauthorizedAccessException e) {
			ignoreExpectedException();
		}
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is not
	 * accessible to the logged in user masquerading as another user. 
	 */
	protected void verifyCannotMasquerade(String... params) throws Exception {
		try {
			Action c = getActionObject(params);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (UnauthorizedAccessException e) {
			ignoreExpectedException();
		}
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is 
	 * redirected to {@code expectedRedirectUrl}. Note that only the base 
	 * URI is matched and parameters are ignored. E.g. "/page/studentHome" 
	 * matches "/page/studentHome?user=abc". 
	 */
	protected void verifyRedirectTo(String expectedRedirectUrl,	String... params) throws Exception {
		Action c = getActionObject(params);
		RedirectResult r = (RedirectResult) c.executeAndPostProcess();
		assertContains(expectedRedirectUrl, r.destination);
	}

}
