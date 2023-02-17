package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;
import teammates.logic.api.MockEmailSender;
import teammates.logic.api.MockLogsProcessor;
import teammates.logic.api.MockRecaptchaVerifier;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.api.MockUserProvision;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;
import teammates.test.MockHttpServletRequest;
import teammates.ui.request.BasicRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Base class for all action tests.
 *
 * <p>On top of having a local database, these tests require proxy services to be running (to be more precise, mocked).
 *
 * @param <T> The action class being tested.
 */
public abstract class BaseActionTest<T extends Action> extends BaseTestCaseWithLocalDatabaseAccess {

    static final String GET = HttpGet.METHOD_NAME;
    static final String POST = HttpPost.METHOD_NAME;
    static final String PUT = HttpPut.METHOD_NAME;
    static final String DELETE = HttpDelete.METHOD_NAME;

    DataBundle typicalBundle = getTypicalDataBundle();
    LogicExtension logic = new LogicExtension();
    MockTaskQueuer mockTaskQueuer = new MockTaskQueuer();
    MockEmailSender mockEmailSender = new MockEmailSender();
    MockLogsProcessor mockLogsProcessor = new MockLogsProcessor();
    MockUserProvision mockUserProvision = new MockUserProvision();
    MockRecaptchaVerifier mockRecaptchaVerifier = new MockRecaptchaVerifier();

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
            action.setTaskQueuer(mockTaskQueuer);
            action.setEmailSender(mockEmailSender);
            action.setLogsProcessor(mockLogsProcessor);
            action.setUserProvision(mockUserProvision);
            action.setRecaptchaVerifier(mockRecaptchaVerifier);
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

    @BeforeMethod
    public void beforeTestMethodSetup() {
        prepareTestData();
    }

    /**
     * Prepares the test data used for the current test.
     */
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
    }

    /**
     * Tests the {@link Action#execute()} method.
     *
     * <p>Some actions, particularly those with large number of different outcomes,
     * can alternatively separate each test case to different test blocks.
     */
    protected abstract void testExecute() throws Exception;

    /**
     * Tests the {@link Action#checkAccessControl()} method.
     *
     * <p>Some actions, particularly those with large number of different access control settings,
     * can alternatively separate each test case to different test blocks.
     */
    protected abstract void testAccessControl() throws Exception;

    /**
     * Returns The {@code params} array with the {@code userId}
     *         (together with the parameter name) inserted at the beginning.
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
        UserInfo user = mockUserProvision.loginUser(userId);
        assertFalse(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a student
     * (without admin rights or instructor rights).
     */
    protected void loginAsStudent(String userId) {
        UserInfo user = mockUserProvision.loginUser(userId);
        assertTrue(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a student-instructor
     * (without admin rights).
     */
    protected void loginAsStudentInstructor(String userId) {
        UserInfo user = mockUserProvision.loginUser(userId);
        assertTrue(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the test environment as a maintainer.
     */
    protected void loginAsMaintainer() {
        UserInfo user = mockUserProvision.loginUser(Config.APP_MAINTAINERS.get(0));
        assertTrue(user.isMaintainer);
    }

    /**
     * Logs the current user out of the test environment.
     */
    protected void logoutUser() {
        mockUserProvision.logoutUser();
    }

    void grantInstructorWithSectionPrivilege(
            InstructorAttributes instructor, String privilege, String[] sections)
            throws Exception {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        for (String section : sections) {
            instructorPrivileges.updatePrivilege(section, privilege, true);
        }

        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(instructor.getCourseId(), instructor.getEmail())
                .withPrivileges(instructorPrivileges)
                .build());
    }

    // The next few methods are for testing access control

    // 'High-level' access-control tests: here it tests access control of an action for the full range of user types.

    void verifyAnyUserCanAccess(String... params) {
        verifyAccessibleWithoutLogin(params);
        verifyAccessibleForUnregisteredUsers(params);
        verifyAccessibleForAdmin(params);
    }

    void verifyAnyLoggedInUserCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyAccessibleForUnregisteredUsers(params);
        verifyAccessibleForAdmin(params);
    }

    void verifyOnlyAdminCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(params);
        verifyInaccessibleForInstructors(params);
        verifyAccessibleForAdmin(params);
    }

    void verifyOnlyInstructorsCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(params);
        verifyAccessibleForInstructorsOfTheSameCourse(params);
        verifyAccessibleForInstructorsOfOtherCourse(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(params);
    }

    void verifyOnlyInstructorsOfTheSameCourseCanAccess(String[] submissionParams) {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    void verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
            String privilege, String[] submissionParams)
            throws Exception {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleWithoutCorrectCoursePrivilege(privilege, submissionParams);
    }

    // 'Mid-level' access control tests: here it tests access control of an action for one user type.

    void verifyAccessibleWithoutLogin(String... params) {

        ______TS("Non-logged-in users can access");

        logoutUser();
        verifyCanAccess(params);

    }

    void verifyInaccessibleWithoutLogin(String... params) {

        ______TS("Non-logged-in users cannot access");

        logoutUser();
        verifyCannotAccess(params);

    }

    void verifyAccessibleForUnregisteredUsers(String... params) {

        ______TS("Non-registered users can access");

        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCanAccess(params);

    }

    void verifyInaccessibleForUnregisteredUsers(String... params) {

        ______TS("Non-registered users cannot access");

        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCannotAccess(params);

    }

    void verifyAccessibleForAdmin(String... params) {

        ______TS("Admin can access");

        loginAsAdmin();
        verifyCanAccess(params);

    }

    void verifyInaccessibleForAdmin(String... params) {

        ______TS("Admin cannot access");

        loginAsAdmin();
        verifyCannotAccess(params);

    }

    void verifyInaccessibleForStudents(String... params) {

        ______TS("Students cannot access");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(params);

    }

    void verifyInaccessibleForInstructors(String... params) {

        ______TS("Instructors cannot access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCannotAccess(params);

    }

    void verifyAccessibleForAdminToMasqueradeAsInstructor(
            InstructorAttributes instructor, String[] submissionParams) {

        ______TS("admin can access");

        loginAsAdmin();
        //not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor.getGoogleId(), submissionParams);
    }

    void verifyAccessibleForAdminToMasqueradeAsInstructor(String[] submissionParams) {

        ______TS("admin can access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsAdmin();
        //not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor1OfCourse1.getGoogleId(), submissionParams);
    }

    void verifyInaccessibleWithoutModifySessionPrivilege(String[] submissionParams) {

        ______TS("without Modify-Session privilege cannot access");

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(String[] submissionParams) {

        ______TS("without Submit-Session-In-Sections privilege cannot access");

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleWithoutCorrectCoursePrivilege(String privilege, String[] submissionParams) throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        ______TS("without correct course privilege cannot access");

        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("only instructor with correct course privilege should pass");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        instructorPrivileges.updatePrivilege(privilege, true);
        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(course.getId(), helperOfCourse1.getEmail())
                .withPrivileges(instructorPrivileges)
                .build());

        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, submissionParams);

        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(course.getId(), helperOfCourse1.getEmail())
                .withPrivileges(new InstructorPrivileges())
                .build());
    }

    void verifyAccessibleForInstructorsOfTheSameCourse(String[] submissionParams) {

        ______TS("course instructor can access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(student1InCourse1.getGoogleId(), submissionParams);
        verifyCannotMasquerade(otherInstructor.getGoogleId(), submissionParams);

    }

    void verifyAccessibleForInstructorsOfOtherCourse(String[] submissionParams) {

        ______TS("other course's instructor can access");

        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(student1InCourse1.getGoogleId(), submissionParams);
        verifyCannotMasquerade(otherInstructor.getGoogleId(), submissionParams);
    }

    void verifyAccessibleForStudentsOfTheSameCourse(String[] submissionParams) {

        ______TS("course students can access");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParams);
    }

    void verifyInaccessibleForStudentsOfOtherCourse(String[] submissionParams) {

        ______TS("other course student cannot access");

        StudentAttributes otherStudent = typicalBundle.students.get("student1InCourse2");

        loginAsStudent(otherStudent.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleForInstructorsOfOtherCourses(String[] submissionParams) {

        ______TS("other course instructor cannot access");

        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsInstructor(otherInstructor.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyAccessibleForMaintainers(String... params) {

        ______TS("Maintainer can access");

        loginAsMaintainer();
        verifyCanAccess(params);
    }

    // 'Low-level' access control tests: here it tests an action once with the given parameters.
    // These methods are not aware of the user type.

    /**
     * Verifies that the {@link Action} matching the {@code params} is accessible to the logged in user.
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
     * Verifies that the {@link Action} matching the {@code params} is not accessible to the user.
     */
    protected void verifyCannotAccess(String... params) {
        Action c = getAction(params);
        assertThrows(UnauthorizedAccessException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * accessible to the logged in user masquerading as another user with {@code userId}.
     */
    protected void verifyCanMasquerade(String userId, String... params) {
        verifyCanAccess(addUserIdToParams(userId, params));
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the logged in user masquerading as another user with {@code userId}.
     */
    protected void verifyCannotMasquerade(String userId, String... params) {
        assertThrows(UnauthorizedAccessException.class,
                () -> getAction(addUserIdToParams(userId, params)).checkAccessControl());
    }

    // The next few methods are for parsing results

    /**
     * Executes the action, verifies the status code as 200 OK, and returns the result.
     *
     * <p>Assumption: The action returns a {@link JsonResult}.
     */
    protected JsonResult getJsonResult(Action a) {
        return getJsonResult(a, HttpStatus.SC_OK);
    }

    /**
     * Executes the action, verifies the status code, and returns the result.
     *
     * <p>Assumption: The action returns a {@link JsonResult}.
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
     * Verifies that the executed action results in {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link InvalidHttpParameterException} being thrown
     * when checking for access control.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailureAcl(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidHttpParameterException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in {@link InvalidHttpRequestBodyException} being thrown.
     */
    protected InvalidHttpRequestBodyException verifyHttpRequestBodyFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidHttpRequestBodyException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException} being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(String... params) {
        Action c = getAction(params);
        return assertThrows(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException} being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link EntityNotFoundException} being thrown when checking for access control.
     */
    protected EntityNotFoundException verifyEntityNotFoundAcl(String... params) {
        Action c = getAction(params);
        return assertThrows(EntityNotFoundException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(String... params) {
        Action c = getAction(params);
        return assertThrows(InvalidOperationException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrows(InvalidOperationException.class, c::execute);
    }

    /**
     * Verifies that the executed action does not result in any background task being added.
     */
    protected void verifyNoTasksAdded() {
        Map<String, Integer> tasksAdded = mockTaskQueuer.getNumberOfTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }

    /**
     * Verifies that the executed action results in the specified background tasks being added.
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
     * Verifies that the executed action results in the specified number of emails being sent.
     */
    protected void verifyNumberOfEmailsSent(int emailCount) {
        assertEquals(emailCount, mockEmailSender.getEmailsSent().size());
    }

}
