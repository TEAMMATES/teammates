package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import jakarta.servlet.http.Cookie;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.AuthProxy;
import teammates.logic.api.MockEmailSender;
import teammates.logic.api.MockLogsProcessor;
import teammates.logic.api.MockTaskQueuer;
import teammates.sqllogic.api.Logic;
import teammates.sqllogic.api.MockUserProvision;
import teammates.sqllogic.api.SqlEmailGenerator;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.request.BasicRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.ActionMappingException;
import teammates.ui.webapi.ActionResult;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UnauthorizedAccessException;

/**
 * Base class for all action tests.
 *
 * <p>
 * On top of having a local database, these tests require proxy services to be
 * running (to be more precise, mocked).
 *
 * @param <T> The action class being tested.
 */
public abstract class BaseActionTest<T extends Action> extends BaseTestCase {

    static final String GET = HttpGet.METHOD_NAME;
    static final String POST = HttpPost.METHOD_NAME;
    static final String PUT = HttpPut.METHOD_NAME;
    static final String DELETE = HttpDelete.METHOD_NAME;

    Logic mockLogic = mock(Logic.class);
    teammates.logic.api.Logic mockDatastoreLogic = mock(teammates.logic.api.Logic.class);
    MockTaskQueuer mockTaskQueuer = new MockTaskQueuer();
    MockEmailSender mockEmailSender = new MockEmailSender();
    MockLogsProcessor mockLogsProcessor = new MockLogsProcessor();
    MockUserProvision mockUserProvision = new MockUserProvision();
    teammates.logic.api.RecaptchaVerifier mockRecaptchaVerifier = mock(teammates.logic.api.RecaptchaVerifier.class);
    SqlEmailGenerator mockSqlEmailGenerator = mock(SqlEmailGenerator.class);
    teammates.logic.api.EmailGenerator mockEmailGenerator = mock(teammates.logic.api.EmailGenerator.class);
    AuthProxy mockAuthProxy = mock(AuthProxy.class);

    abstract String getActionUri();

    abstract String getRequestMethod();

    /**
     * Gets an action with empty request body.
     */
    protected T getAction(String... params) {
        return getAction(null, null, params);
    }

    /**
     * Gets an action with request body.
     */
    protected T getAction(BasicRequest requestBody, String... params) {
        return getAction(JsonUtils.toCompactJson(requestBody), null, params);
    }

    /**
     * Gets an action with request body and cookie.
     */
    protected T getAction(String body, List<Cookie> cookies, String... params) {
        mockTaskQueuer.clearTasks();
        mockEmailSender.clearEmails();
        MockHttpServletRequest req = new MockHttpServletRequest(getRequestMethod(), getActionUri());
        for (int i = 0; i < params.length; i = i + 2) {
            req.addParam(params[i], params[i + 1]);
        }
        if (body != null) {
            req.setBody(body);
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                req.addCookie(cookie);
            }
        }
        try {
            @SuppressWarnings("unchecked")
            T action = (T) ActionFactory.getAction(req, getRequestMethod());
            action.setLogic(mockLogic);
            action.setLogic(mockDatastoreLogic);
            action.setTaskQueuer(mockTaskQueuer);
            action.setEmailSender(mockEmailSender);
            action.setLogsProcessor(mockLogsProcessor);
            action.setUserProvision(mockUserProvision);
            action.setRecaptchaVerifier(mockRecaptchaVerifier);
            action.setSqlEmailGenerator(mockSqlEmailGenerator);
            action.setEmailGenerator(mockEmailGenerator);
            action.setAuthProxy(mockAuthProxy);
            action.init(req);
            return action;
        } catch (ActionMappingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an action with list of cookies.
     */
    protected T getActionWithCookie(List<Cookie> cookies, String... params) {
        return getAction(null, cookies, params);
    }

    /**
     * Returns The {@code params} array with the {@code userId}
     * (together with the parameter name) inserted at the beginning.
     */
    protected String[] addUserIdToParams(String userId, String[] params) {
        List<String> list = new ArrayList<>();
        list.add(Const.ParamsNames.USER_ID);
        list.add(userId);
        list.addAll(Arrays.asList(params));
        return list.toArray(new String[0]);
    }

    // The next few methods are for logging in as various user

    /**
     * Logs in the user to the test environment as an admin.
     */
    protected void loginAsAdmin() {
        UserInfo user = mockUserProvision.loginAsAdmin(Config.APP_ADMINS.get(0));
        assertTrue(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as an unregistered user
     * (without any right).
     */
    protected void loginAsUnregistered(String userId) {
        UserInfo user = mockUserProvision.loginUser(userId);
        assertFalse(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as an instructor
     * (without admin rights or student rights).
     */
    protected void loginAsInstructor(String userId) {
        UserInfo user = mockUserProvision.loginAsInstructor(userId);
        assertFalse(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a student
     * (without admin rights or instructor rights).
     */
    protected void loginAsStudent(String userId) {
        UserInfo user = mockUserProvision.loginAsStudent(userId);
        assertTrue(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a student-instructor
     * (without admin rights).
     */
    protected void loginAsStudentInstructor(String userId) {
        UserInfo user = mockUserProvision.loginAsStudentInstructor(userId);
        assertTrue(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a maintainer.
     */
    protected void loginAsMaintainer() {
        UserInfo user = mockUserProvision.loginAsMaintainer(Config.APP_MAINTAINERS.get(0));
        assertTrue(user.isMaintainer);
    }

    /**
     * Logs the current user out of the test environment.
     */
    protected void logoutUser() {
        mockUserProvision.logoutUser();
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is accessible to
     * the logged in user.
     */
    protected void verifyCanAccess(String... params) {
        Action c = getAction(params);
        try {
            c.checkAccessControl();
        } catch (UnauthorizedAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the user.
     */
    protected void verifyCannotAccess(String... params) {
        Action c = getAction(params);
        assertThrows(UnauthorizedAccessException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * accessible to the logged in user masquerading as another user with
     * {@code userId}.
     */
    protected void verifyCanMasquerade(String userId, String... params) {
        verifyCanAccess(addUserIdToParams(userId, params));
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the logged in user masquerading as another user with
     * {@code userId}.
     */
    protected void verifyCannotMasquerade(String userId, String... params) {
        assertThrows(UnauthorizedAccessException.class,
                () -> getAction(addUserIdToParams(userId, params)).checkAccessControl());
    }

    // The next few methods are for parsing results

    /**
     * Executes the action, verifies the status code as 200 OK, and returns the
     * result.
     *
     * <p>
     * Assumption: The action returns a {@link JsonResult}.
     */
    protected JsonResult getJsonResult(Action a) {
        return getJsonResult(a, HttpStatus.SC_OK);
    }

    /**
     * Executes the action, verifies the status code, and returns the result.
     *
     * <p>
     * Assumption: The action returns a {@link JsonResult}.
     */
    protected JsonResult getJsonResult(Action a, int statusCode) {
        try {
            ActionResult r = a.execute();
            assertEquals(statusCode, r.getStatusCode());
            return (JsonResult) r;
        } catch (InvalidOperationException | InvalidHttpRequestBodyException e) {
            throw new RuntimeException(e);
        }
    }

    // The next few methods are for verifying action results

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link InvalidHttpParameterException}
     * being thrown
     * when checking for access control.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailureAcl(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidHttpParameterException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpRequestBodyException} being thrown.
     */
    protected InvalidHttpRequestBodyException verifyHttpRequestBodyFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidHttpRequestBodyException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException}
     * being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(String... params) {
        Action c = getAction(params);
        return assertThrows(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException}
     * being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link EntityNotFoundException} being
     * thrown when checking for access control.
     */
    protected EntityNotFoundException verifyEntityNotFoundAcl(String... params) {
        Action c = getAction(params);
        return assertThrows(EntityNotFoundException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidOperationException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidOperationException.class, c::execute);
    }

    /**
     * Verifies that the executed action does not result in any background task
     * being added.
     */
    protected void verifyNoTasksAdded() {
        Map<String, Integer> tasksAdded = mockTaskQueuer.getNumberOfTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }

    /**
     * Verifies that the executed action results in the specified background tasks
     * being added.
     */
    protected void verifySpecifiedTasksAdded(String taskName, int taskCount) {
        Map<String, Integer> tasksAdded = mockTaskQueuer.getNumberOfTasksAdded();
        assertEquals(taskCount, tasksAdded.get(taskName).intValue());
    }

    /**
     * Verifies that the executed action does not result in any email being sent.
     */
    protected void verifyNoEmailsSent() {
        assertTrue(getEmailsSent().isEmpty());
    }

    /**
     * Returns the list of emails sent as part of the executed action.
     */
    protected List<EmailWrapper> getEmailsSent() {
        return mockEmailSender.getEmailsSent();
    }

    /**
     * Verifies that the executed action results in the specified number of emails
     * being sent.
     */
    protected void verifyNumberOfEmailsSent(int emailCount) {
        assertEquals(emailCount, mockEmailSender.getEmailsSent().size());
    }

    /**
     * Access Control Helper Methods.
     */
    private void loginAsStudentOfTheSameCourse(Course thisCourse) {
        Student sameCourseStudent = getTypicalStudent();
        sameCourseStudent.setCourse(thisCourse);

        logoutUser();
        loginAsStudent(sameCourseStudent.getId().toString());
    }

    private void loginAsInstructorOfOtherCourse() {
        Instructor otherCourseInstructor = getTypicalInstructor();
        Course otherCourse = new Course("other-course-id", "other-course-name", Const.DEFAULT_TIME_ZONE, "teammates");
        otherCourseInstructor.setCourse(otherCourse);

        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(otherCourseInstructor);

        logoutUser();
        loginAsInstructor(otherCourseInstructor.getId().toString());
    }

    private void verifySameCourseAccessibility(
            Course thisCourse, String privilege, boolean canAccess, String... params) {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(privilege, canAccess);

        verifySameCourseAccessibility(thisCourse, instructorPrivileges, canAccess, params);
    }

    private void verifySameCourseAccessibility(
            Course thisCourse, InstructorPrivileges instructorPrivileges, boolean canAccess, String... params) {
        Instructor instructor = getTypicalInstructor();
        instructor.setAccount(new Account("instructor-googleId", instructor.getName(), instructor.getEmail()));

        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructor);
        when(mockLogic.getCourse(thisCourse.getId())).thenReturn(thisCourse);

        instructor.setCourse(thisCourse);

        logoutUser();
        loginAsInstructor(instructor.getId().toString());
        verifyCanAccess(params);

        instructor.setPrivileges(instructorPrivileges);

        if (canAccess) {
            verifyCanAccess(params);
            verifyAccessibleForAdminsToMasqueradeAsInstructor(instructor, params);
        } else {
            verifyCannotAccess(params);
            verifyCannotMasquerade(instructor.getId().toString(), params);
        }
    }

    private void verifyDifferentCourseAccessibility(
            Course thisCourse, String privilege, boolean canAccess, String... params) {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(privilege, canAccess);

        verifyDifferentCourseAccessibility(thisCourse, instructorPrivileges, canAccess, params);
    }

    private void verifyDifferentCourseAccessibility(
            Course thisCourse, InstructorPrivileges instructorPrivileges, boolean canAccess, String... params) {
        Instructor instructor = getTypicalInstructor();

        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructor);
        when(mockLogic.getCourse(thisCourse.getId())).thenReturn(thisCourse);

        instructor.setCourse(thisCourse);

        logoutUser();
        loginAsInstructor(instructor.getId().toString());
        verifyCanAccess(params);

        instructor.setPrivileges(instructorPrivileges);

        if (canAccess) {
            verifyCanAccess(params);
            verifyAccessibleForAdminsToMasqueradeAsInstructor(instructor, params);
        } else {
            verifyCannotAccess(params);
            verifyCannotMasquerade(instructor.getId().toString(), params);
        }
    }

    private void instructorsOfTheSameCourseCanAccess(Course thisCourse, String... params) {
        Instructor sameCourseInstructor = getTypicalInstructor();
        sameCourseInstructor.setCourse(thisCourse);

        Instructor otherCourseInstructor = getTypicalInstructor();
        Course otherCourse = new Course("other-course-id", "other-course-name", Const.DEFAULT_TIME_ZONE, "teammates");
        otherCourseInstructor.setCourse(otherCourse);

        Student sameCourseStudent = getTypicalStudent();
        sameCourseStudent.setCourse(thisCourse);

        verifyCannotMasquerade(sameCourseStudent.getId().toString(), params);
        verifyCannotMasquerade(otherCourseInstructor.getId().toString(), params);

        logoutUser();
        loginAsInstructor(sameCourseInstructor.getId().toString());
        verifyCanAccess(params);
    }

    /**
     * 'Mid-level' Access Control Test Methods.
     * Here it tests access control of an action for one user type.
     * Both Mid and High Level follows the order Admin -> Maintainer -> Instructor
     * -> Student -> Unregistered -> No login.
     */
    void verifyAdminsCanAccess(String... params) {
        logoutUser();
        loginAsAdmin();
        verifyCanAccess(params);
        logoutUser();
    }

    void verifyAdminsCannotAccess(String... params) {
        logoutUser();
        loginAsAdmin();
        verifyCannotAccess(params);
        logoutUser();
    }

    void verifyMaintainersCanAccess(String... params) {
        loginAsMaintainer();
        verifyCanAccess(params);
        logoutUser();
    }

    void verifyMaintainersCannotAccess(String... params) {
        loginAsMaintainer();
        verifyCannotAccess(params);
        logoutUser();
    }

    void verifyInstructorsCanAccess(Course thisCourse, String... params) {
        logoutUser();
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(thisCourse);

        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructor);
        when(mockLogic.getCourse(thisCourse.getId())).thenReturn(thisCourse);

        loginAsInstructor(instructor.getId().toString());
        verifyCanAccess(params);
        logoutUser();
    }

    void verifyInstructorsCannotAccess(String... params) {
        logoutUser();
        loginAsInstructor("instructor-googleId");
        verifyCannotAccess(params);
        logoutUser();
    }

    void verifyStudentsCanAccess(String... params) {
        logoutUser();
        loginAsStudent("student-googleId");
        verifyCanAccess(params);
        logoutUser();
    }

    void verifyStudentsCannotAccess(String... params) {
        logoutUser();
        loginAsStudent("student-googleId");
        verifyCannotAccess(params);
        logoutUser();
    }

    void verifyUnregisteredCanAccess(String... params) {
        logoutUser();
        loginAsUnregistered("unregistered-googleId");
        verifyCanAccess(params);
        logoutUser();
    }

    void verifyUnregisteredCannotAccess(String... params) {
        logoutUser();
        loginAsUnregistered("unregistered-googleId");
        verifyCannotAccess(params);
        logoutUser();
    }

    void verifyWithoutLoginCanAccess(String... params) {
        logoutUser();
        verifyCanAccess(params);
    }

    void verifyWithoutLoginCannotAccess(String... params) {
        logoutUser();
        verifyCannotAccess(params);
    }

    /**
     * 'High-level' Access Control Test Methods.
     * Here it tests access control of an action for the full range of user types.
     */
    // Admins
    void verifyAccessibleForAdminsToMasqueradeAsInstructor(Instructor instructor, String... params) {
        loginAsAdmin();

        mockUserProvision.setAdmin(false);
        mockUserProvision.setInstructor(true);
        mockUserProvision.setStudent(false);
        mockUserProvision.setMaintainer(false);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructor);

        verifyCanMasquerade(instructor.getGoogleId(), params);
        mockUserProvision.setInstructor(false);
    }

    void verifyInaccessibleForAdminsToMasqueradeAsInstructor(Instructor instructor, String... params) {
        loginAsAdmin();

        mockUserProvision.setAdmin(false);
        mockUserProvision.setInstructor(true);
        mockUserProvision.setStudent(false);
        mockUserProvision.setMaintainer(false);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructor);

        verifyCannotMasquerade(instructor.getGoogleId(), params);
        mockUserProvision.setInstructor(false);
    }

    void verifyOnlyAdminsCanAccess(String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCannotAccess(params);
        verifyStudentsCannotAccess(params);
        verifyInstructorsCannotAccess(params);
        verifyAdminsCanAccess(params);
    }

    // Instructors
    void verifyAccessibleWithModifySessionPrivilege(Course thisCourse, String... params) {
        verifyAccessibleWithCorrectSameCoursePrivilege(
                thisCourse, Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

    void verifyAccessibleWithCorrectSameCoursePrivilege(
            Course thisCourse, String privilege, String... params) {
        verifySameCourseAccessibility(thisCourse, privilege, true, params);
        verifyDifferentCourseAccessibility(thisCourse, privilege, false, params);
    }

    void verifyAccessibleWithCorrectSameCoursePrivilege(
            Course thisCourse, InstructorPrivileges privilege, String... params) {
        verifySameCourseAccessibility(thisCourse, privilege, true, params);
        verifyDifferentCourseAccessibility(thisCourse, privilege, false, params);
    }

    void verifyInaccessibleWithoutModifySessionPrivilege(Course thisCourse, String... params) {
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(
                thisCourse, Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

    void verifyInaccessibleWithoutCorrectSameCoursePrivilege(
            Course thisCourse, String privilege, String... params) {
        verifySameCourseAccessibility(thisCourse, privilege, false, params);
        verifyDifferentCourseAccessibility(thisCourse, privilege, false, params);
    }

    void verifyInaccessibleWithoutCorrectSameCoursePrivilege(
            Course thisCourse, InstructorPrivileges privilege, String... params) {
        verifySameCourseAccessibility(thisCourse, privilege, false, params);
        verifyDifferentCourseAccessibility(thisCourse, privilege, false, params);
    }

    void verifyAccessibleWithSubmitSessionInSectionsPrivilege(Course thisCourse, String... params) {
        verifyAccessibleWithCorrectSameCoursePrivilege(
                thisCourse, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, params);
    }

    void verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(Course thisCourse, String... params) {
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(
                thisCourse, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, params);
    }

    void verifyInstructorsOfOtherCoursesCanAccess(String... params) {
        loginAsInstructorOfOtherCourse();
        verifyCanAccess(params);
    }

    void verifyInstructorsOfOtherCoursesCannotAccess(String... params) {
        loginAsInstructorOfOtherCourse();
        verifyCannotAccess(params);
    }

    void verifyAnyInstructorCanAccess(Course currentCourse, String... params) {
        Instructor testInstructor = getTypicalInstructor();
        testInstructor.setCourse(currentCourse);

        verifyInstructorsCanAccess(currentCourse, params);
        instructorsOfTheSameCourseCanAccess(currentCourse, params);
        verifyInstructorsOfOtherCoursesCanAccess(params);
        verifyAccessibleForAdminsToMasqueradeAsInstructor(testInstructor, params);
    }

    void verifyInstructorsCanAccessNoMasquerade(Course currentCourse, String... params) {
        Instructor testInstructor = getTypicalInstructor();
        testInstructor.setCourse(currentCourse);

        verifyInstructorsCanAccess(currentCourse, params);
        instructorsOfTheSameCourseCanAccess(currentCourse, params);
        verifyInstructorsOfOtherCoursesCanAccess(params);
        verifyInaccessibleForAdminsToMasqueradeAsInstructor(testInstructor, params);
    }

    void verifyInstructorsOfTheSameCourseCanAccess(Course currentCourse, String... params) {
        verifyInstructorsCanAccess(currentCourse, params);
        instructorsOfTheSameCourseCanAccess(currentCourse, params);
        verifyInstructorsOfOtherCoursesCannotAccess(params);
    }

    void verifyOnlyInstructorsCanAccess(Course currentCourse, String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCannotAccess(params);
        verifyStudentsCannotAccess(params);
        verifyAnyInstructorCanAccess(currentCourse, params);
    }

    void verifyOnlyInstructorsOfTheSameCourseCanAccess(Course currentCourse, String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCannotAccess(params);
        verifyStudentsCannotAccess(params);
        verifyInstructorsOfTheSameCourseCanAccess(currentCourse, params);
    }

    void verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
            Course thisCourse, String privilege, String... submissionParams) {
        verifyWithoutLoginCannotAccess(submissionParams);
        verifyUnregisteredCannotAccess(submissionParams);
        verifyStudentsCannotAccess(submissionParams);
        verifyAccessibleWithCorrectSameCoursePrivilege(thisCourse, privilege, submissionParams);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(thisCourse, privilege, submissionParams);
    }

    void verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
            Course thisCourse, InstructorPrivileges privilege, String... submissionParams) {
        verifyWithoutLoginCannotAccess(submissionParams);
        verifyUnregisteredCannotAccess(submissionParams);
        verifyStudentsCannotAccess(submissionParams);
        verifyAccessibleWithCorrectSameCoursePrivilege(thisCourse, privilege, submissionParams);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(thisCourse, privilege, submissionParams);
    }

    // Students
    void verifyOnlyStudentsCanAccess(String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCannotAccess(params);
        verifyStudentsCanAccess(params);
    }

    void verifyStudentsOfTheSameCourseCanAccess(Course thisCourse, String... params) {
        loginAsStudentOfTheSameCourse(thisCourse);
        verifyCanAccess(params);
    }

    void verifyStudentsOfOtherCoursesCannotAccess(Course thisCourse, String... params) {
        loginAsStudentOfTheSameCourse(thisCourse);
        verifyCannotAccess(params);
    }

    // Registered
    void verifyAnyLoggedInUserCanAccess(String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCanAccess(params);
        verifyAdminsCanAccess(params);
        verifyInstructorsCanAccess(getTypicalCourse(), params);
        verifyStudentsCanAccess(params);
    }

    // No Login
    void verifyAnyUserCanAccess(String... params) {
        verifyWithoutLoginCanAccess(params);
        verifyUnregisteredCanAccess(params);
        verifyAdminsCanAccess(params);
        verifyInstructorsCanAccess(getTypicalCourse(), params);
        verifyStudentsCanAccess(params);
        verifyMaintainersCanAccess(params);
    }

    void verifyNoUsersCanAccess(String... params) {
        verifyWithoutLoginCannotAccess(params);
        verifyUnregisteredCannotAccess(params);
        verifyStudentsCannotAccess(params);
        verifyInstructorsCannotAccess(params);
        verifyAdminsCannotAccess(params);
    }
}
