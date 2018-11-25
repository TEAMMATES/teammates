package teammates.test.cases.newaction;

import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.EmailWrapper;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.newcontroller.Action;
import teammates.ui.newcontroller.JsonResult;

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

    protected T getAction(String... params) {
        return getAction(null, params);
    }

    @SuppressWarnings("unchecked")
    protected T getAction(String body, String... params) {
        return (T) gaeSimulation.getNewActionObject(getActionUri(), getRequestMethod(), body, params);
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

    // 'Low-level' access control tests: here it tests an action once with the given parameters.
    // These methods are not aware of the user type.

    /**
     * Verifies that the {@link Action} matching the {@code params} is accessible to the logged in user.
     */
    private void verifyCanAccess(String... params) {
        Action c = getAction(params);
        c.checkAccessControl();
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not accessible to the user.
     */
    private void verifyCannotAccess(String... params) {
        Action c = getAction(params);
        assertThrows(UnauthorizedAccessException.class, () -> c.checkAccessControl());
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

    // The next few methods are for verifying action results

    /**
     * Verifies that the {@code parameters} violates an assumption of the
     * matching {@link Action}. e.g., missing a compulsory parameter.
     */
    protected void verifyHttpParameterFailure(String... params) {
        Action c = getAction(params);
        assertThrows(InvalidHttpParameterException.class, () -> c.execute());
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

}
