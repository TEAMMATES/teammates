package teammates.ui.webapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.Part;

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
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.JsonUtils;
import teammates.test.BaseComponentTestCase;
import teammates.test.MockPart;
import teammates.ui.request.BasicRequest;

/**
 * Base class for all action tests.
 *
 * @param <T> The action class being tested.
 */
public abstract class BaseActionTest<T extends Action> extends BaseComponentTestCase {

    protected static final String GET = HttpGet.METHOD_NAME;
    protected static final String POST = HttpPost.METHOD_NAME;
    protected static final String PUT = HttpPut.METHOD_NAME;
    protected static final String DELETE = HttpDelete.METHOD_NAME;

    protected DataBundle typicalBundle = getTypicalDataBundle();

    protected abstract String getActionUri();

    protected abstract String getRequestMethod();

    /**
     * Gets an action with empty request body and empty multipart config.
     */
    protected T getAction(String... params) {
        return getAction(null, null, null, params);
    }

    /**
     * Gets an action with request body.
     */
    protected T getAction(Object requestBody, String... params) {
        return getAction(JsonUtils.toJson(requestBody), params);
    }

    /**
     * Gets an action with request body.
     */
    protected T getAction(String body, String... params) {
        return getAction(body, null, null, params);
    }

    /**
     * Gets an action with request body and multipart config.
     */
    @SuppressWarnings("unchecked")
    protected T getAction(String body, Map<String, Part> parts, List<Cookie> cookies, String... params) {
        return (T) gaeSimulation.getActionObject(getActionUri(), getRequestMethod(), body, parts, cookies, params);
    }

    /**
     * Gets an action with request multipart config.
     */
    protected T getActionWithParts(String key, String filePath, String... params) throws IOException {
        Map<String, Part> parts = new HashMap<>();
        parts.put(key, new MockPart(filePath));

        return getAction(null, parts, null, params);
    }

    /**
     * Gets an action with list of cookies.
     */
    protected T getActionWithCookie(List<Cookie> cookies, String... params) {
        return getAction(null, null, cookies, params);
    }

    @BeforeMethod
    public void beforeTestMethodSetup() {
        prepareTestData();
    }

    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
    }

    protected abstract void testExecute() throws Exception;

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
     * Logs in the user to the GAE simulation environment as an admin.
     */
    protected void loginAsAdmin() {
        UserInfo user = gaeSimulation.loginAsAdmin("admin.user");
        assertTrue(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as an unregistered user
     * (without any right).
     */
    protected void loginAsUnregistered(String userId) {
        UserInfo user = gaeSimulation.loginUser(userId);
        assertFalse(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as an instructor
     * (without admin rights or student rights).
     */
    protected void loginAsInstructor(String userId) {
        UserInfo user = gaeSimulation.loginUser(userId);
        assertFalse(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as a student
     * (without admin rights or instructor rights).
     */
    protected void loginAsStudent(String userId) {
        UserInfo user = gaeSimulation.loginUser(userId);
        assertTrue(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as a student-instructor
     * (without admin rights).
     */
    protected void loginAsStudentInstructor(String userId) {
        UserInfo user = gaeSimulation.loginUser(userId);
        assertTrue(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    protected void grantInstructorWithSectionPrivilege(
            InstructorAttributes instructor, String privilege, String[] sections)
            throws InvalidParametersException, EntityDoesNotExistException {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        for (String section : sections) {
            instructorPrivileges.updatePrivilege(section, privilege, true);
        }

        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(instructor.getCourseId(), instructor.email)
                .withPrivileges(instructorPrivileges)
                .build());
    }

    // The next few methods are for testing access control

    // 'High-level' access-control tests: here it tests access control of an action for the full range of user types.

    protected void verifyAnyUserCanAccess(String... params) {
        verifyAccessibleWithoutLogin(params);
        verifyAccessibleForUnregisteredUsers(params);
        verifyAccessibleForAdmin(params);
    }

    protected void verifyAnyLoggedInUserCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyAccessibleForUnregisteredUsers(params);
        verifyAccessibleForAdmin(params);
    }

    protected void verifyOnlyAdminCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(params);
        verifyInaccessibleForInstructors(params);
        verifyAccessibleForAdmin(params);
    }

    protected void verifyOnlyInstructorsCanAccess(String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(params);
        verifyAccessibleForInstructorsOfTheSameCourse(params);
        verifyAccessibleForInstructorsOfOtherCourse(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(params);
    }

    protected void verifyOnlyInstructorsOfTheSameCourseCanAccess(String[] submissionParams) {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    protected void verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
            String privilege, String[] submissionParams)
            throws InvalidParametersException, EntityDoesNotExistException {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleWithoutCorrectCoursePrivilege(privilege, submissionParams);
    }

    // 'Mid-level' access control tests: here it tests access control of an action for one user type.

    protected void verifyAccessibleWithoutLogin(String... params) {

        ______TS("Non-logged-in users can access");

        gaeSimulation.logoutUser();
        verifyCanAccess(params);

    }

    protected void verifyInaccessibleWithoutLogin(String... params) {

        ______TS("Non-logged-in users cannot access");

        gaeSimulation.logoutUser();
        verifyCannotAccess(params);

    }

    protected void verifyAccessibleForUnregisteredUsers(String... params) {

        ______TS("Non-registered users can access");

        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCanAccess(params);

    }

    protected void verifyInaccessibleForUnregisteredUsers(String... params) {

        ______TS("Non-registered users cannot access");

        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCannotAccess(params);

    }

    protected void verifyAccessibleForAdmin(String... params) {

        ______TS("Admin can access");

        loginAsAdmin();
        verifyCanAccess(params);

    }

    protected void verifyInaccessibleForAdmin(String... params) {

        ______TS("Admin cannot access");

        loginAsAdmin();
        verifyCannotAccess(params);

    }

    protected void verifyInaccessibleForStudents(String... params) {

        ______TS("Students cannot access");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(params);

    }

    protected void verifyInaccessibleForInstructors(String... params) {

        ______TS("Instructors cannot access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCannotAccess(params);

    }

    protected void verifyAccessibleForAdminToMasqueradeAsInstructor(
            InstructorAttributes instructor, String[] submissionParams) {

        ______TS("admin can access");

        loginAsAdmin();
        //not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor.googleId, submissionParams);
    }

    protected void verifyAccessibleForAdminToMasqueradeAsInstructor(String[] submissionParams) {

        ______TS("admin can access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsAdmin();
        //not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor1OfCourse1.googleId, submissionParams);
    }

    protected void verifyInaccessibleWithoutModifySessionPrivilege(String[] submissionParams) {

        ______TS("without Modify-Session privilege cannot access");

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }

    protected void verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(String[] submissionParams) {

        ______TS("without Submit-Session-In-Sections privilege cannot access");

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }

    protected void verifyInaccessibleWithoutCorrectCoursePrivilege(
            String privilege, String[] submissionParams)
            throws InvalidParametersException, EntityDoesNotExistException {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        ______TS("without correct course privilege cannot access");

        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("only instructor with correct course privilege should pass");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        instructorPrivileges.updatePrivilege(privilege, true);
        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(course.getId(), helperOfCourse1.email)
                .withPrivileges(instructorPrivileges)
                .build());

        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, submissionParams);

        logic.updateInstructor(InstructorAttributes
                .updateOptionsWithEmailBuilder(course.getId(), helperOfCourse1.email)
                .withPrivileges(new InstructorPrivileges())
                .build());
    }

    protected void verifyAccessibleForInstructorsOfTheSameCourse(String[] submissionParams) {

        ______TS("course instructor can access");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(student1InCourse1.googleId, submissionParams);
        verifyCannotMasquerade(otherInstructor.googleId, submissionParams);

    }

    protected void verifyAccessibleForInstructorsOfOtherCourse(String[] submissionParams) {

        ______TS("other course's instructor can access");

        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse2.googleId);
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(student1InCourse1.googleId, submissionParams);
        verifyCannotMasquerade(otherInstructor.googleId, submissionParams);
    }

    protected void verifyAccessibleForStudentsOfTheSameCourse(String[] submissionParams) {

        ______TS("course students can access");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);
        verifyCanAccess(submissionParams);
    }

    protected void verifyInaccessibleForStudentsOfOtherCourse(String[] submissionParams) {

        ______TS("other course student cannot access");

        StudentAttributes otherStudent = typicalBundle.students.get("student1InCourse2");

        loginAsStudent(otherStudent.googleId);
        verifyCannotAccess(submissionParams);
    }

    protected void verifyInaccessibleForInstructorsOfOtherCourses(String[] submissionParams) {

        ______TS("other course instructor cannot access");

        InstructorAttributes otherInstructor = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsInstructor(otherInstructor.googleId);
        verifyCannotAccess(submissionParams);
    }

    // 'Low-level' access control tests: here it tests an action once with the given parameters.
    // These methods are not aware of the user type.

    /**
     * Verifies that the {@link Action} matching the {@code params} is accessible to the logged in user.
     */
    protected void verifyCanAccess(String... params) {
        Action c = getAction(params);
        c.checkAccessControl();
    }

    protected void verifyCanAccess(BasicRequest request, String... params) {
        Action c = getAction(request, params);
        c.checkAccessControl();
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not accessible to the user.
     */
    protected void verifyCannotAccess(String... params) {
        Action c = getAction(params);
        assertThrows(UnauthorizedAccessException.class, c::checkAccessControl);
    }

    protected void verifyCannotAccess(BasicRequest request, String... params) {
        Action c = getAction(request, params);
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
        assertThrows(UnauthorizedAccessException.class, () -> getAction(addUserIdToParams(userId, params)));
    }

    // The next few methods are for parsing results

    /**
     * Executes the action and returns the result.
     *
     * <p>Assumption: The action returns a {@link JsonResult}.
     */
    protected JsonResult getJsonResult(Action a) {
        return (JsonResult) a.execute();
    }

    /**
     * Executes the action and returns the result.
     *
     * <p>Assumption: The action returns a {@link ImageResult}.
     */
    protected ImageResult getImageResult(Action a) {
        return (ImageResult) a.execute();
    }

    // The next few methods are for verifying action results

    /**
     * Verifies that the {@code parameters} violates an assumption of the
     * matching {@link Action}. e.g., missing a compulsory parameter.
     */
    protected void verifyHttpParameterFailure(String... params) {
        Action c = getAction(params);
        assertThrows(InvalidHttpParameterException.class, c::execute);
    }

    protected void verifyNoTasksAdded(Action action) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }

    protected void verifySpecifiedTasksAdded(Action action, String taskName, int taskCount) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(taskCount, tasksAdded.get(taskName).intValue());
    }

    protected void verifyNoEmailsSent(Action action) {
        assertTrue(getEmailsSent(action).isEmpty());
    }

    protected List<EmailWrapper> getEmailsSent(Action action) {
        return action.getEmailSender().getEmailsSent();
    }

    protected void verifyNumberOfEmailsSent(Action action, int emailCount) {
        assertEquals(emailCount, action.getEmailSender().getEmailsSent().size());
    }

    protected void verifyEntityNotFound(String... params) {
        Action c = getAction(params);
        assertThrows(EntityNotFoundException.class, c::checkAccessControl);
    }
}
