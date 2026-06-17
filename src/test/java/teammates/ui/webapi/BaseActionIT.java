package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.Cookie;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.Logic;
import teammates.logic.api.MockRecaptchaVerifier;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.api.MockUserProvision;
import teammates.logic.core.CoursesLogic;
import teammates.logic.email.EmailQueueService;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.ActionMappingException;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.BasicRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Base class for all action tests.
 *
 * <p>On top of having a local database, these tests require proxy services to be
 * running (to be more precise, mocked).
 *
 * @param <T> The action class being tested.
 */
public abstract class BaseActionIT<T extends Action> extends BaseTestCaseWithDatabaseAccess {

    static final String GET = HttpGet.METHOD_NAME;
    static final String POST = HttpPost.METHOD_NAME;
    static final String PUT = HttpPut.METHOD_NAME;
    static final String DELETE = HttpDelete.METHOD_NAME;

    Logic logic = Logic.inst();
    CoursesLogic coursesLogic = CoursesLogic.inst();
    MockTaskQueuer mockTaskQueuer = new MockTaskQueuer();
    MockUserProvision mockUserProvision = new MockUserProvision();
    MockRecaptchaVerifier mockRecaptchaVerifier = new MockRecaptchaVerifier();

    Course testCourseOther;

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
            action.setEmailQueueService(EmailQueueService.withTaskQueuer(mockTaskQueuer));
            mockUserProvision.setLogic(logic);
            action.setUserProvision(mockUserProvision);
            action.setRecaptchaVerifier(mockRecaptchaVerifier);
            inTransaction(() -> action.init(req));
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
     * Tests the {@link Action#execute()} method.
     *
     * <p>Some actions, particularly those with large number of different outcomes,
     * can alternatively separate each test case to different test blocks.
     */
    protected abstract void testExecute() throws Exception;

    /**
     * Tests the {@link Action#checkAccessControl()} method.
     *
     * <p>Some actions, particularly those with large number of different access
     * control settings,
     * can alternatively separate each test case to different test blocks.
     */
    protected abstract void testAccessControl() throws Exception;

    /**
     * Returns The {@code params} array with the {@code accountId}
     * (together with the parameter name) inserted at the beginning.
     */
    protected String[] addMasqueradeAccountToParams(UUID accountId, String[] params) {
        List<String> list = new ArrayList<>();
        list.add(Const.ParamsNames.MASQUERADE_ACCOUNT_ID);
        list.add(accountId == null ? "" : accountId.toString());
        list.addAll(Arrays.asList(params));
        return list.toArray(new String[0]);
    }

    // The next few methods are for logging in as various user

    /**
     * Logs in the user to the test environment as an admin.
     */
    protected void loginAsAdmin() {
        inTransaction(() -> {
            Account account = ensureAccountExists(Config.APP_ADMINS.get(0));
            mockUserProvision.loginAsAdmin(account);
            mockUserProvision.setLogic(logic);
        });
    }

    /**
     * Logs in the user to the test environment as an unregistered user
     * (without any right).
     */
    protected void loginAsUnregistered(String userId) {
        inTransaction(() -> {
            Account account = ensureAccountExists(userId);
            mockUserProvision.loginUser(account);
            mockUserProvision.setLogic(logic);
        });
    }

    /**
     * Logs in the user to the test environment as an instructor
     * (without admin rights or student rights).
     */
    protected void loginAsInstructor(String userId) {
        inTransaction(() -> {
            Account account = ensureAccountExists(userId);
            mockUserProvision.loginUser(account);
            mockUserProvision.setLogic(logic);
        });
    }

    /**
     * Logs in the user to the test environment as a student
     * (without admin rights or instructor rights).
     */
    protected void loginAsStudent(String userId) {
        inTransaction(() -> {
            Account account = ensureAccountExists(userId);
            mockUserProvision.loginUser(account);
            mockUserProvision.setLogic(logic);
        });
    }

    /**
     * Logs in the user to the test environment as a student-instructor (without
     * admin rights).
     */
    protected void loginAsStudentInstructor(String userId) {
        inTransaction(() -> {
            Account account = ensureAccountExists(userId);
            mockUserProvision.loginUser(account);
            mockUserProvision.setLogic(logic);
        });
    }

    /**
     * Logs in the user to the test environment as a maintainer.
     */
    protected void loginAsMaintainer() {
        inTransaction(() -> {
            Account account = ensureAccountExists(Config.APP_MAINTAINERS.get(0));
            mockUserProvision.loginAsMaintainer(account);
            mockUserProvision.setLogic(logic);
        });
    }

    private Account ensureAccountExists(String userId) {
        String email = userId.contains("@") ? userId : userId + "@example.com";
        String subject = userId;
        return logic.createOrGetAccount(Provider.TEAMMATES_DEV, subject, null, email);
    }

    /**
     * Logs the current user out of the test environment.
     */
    protected void logoutUser() {
        mockUserProvision.logoutUser();
    }

    // The next few methods are for testing access control

    // 'High-level' access-control tests: here it tests access control of an action
    // for the full range of user types.

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

    void verifyOnlyAdminCanAccess(Course course, String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(course, params);
        verifyInaccessibleForInstructors(course, params);
        verifyAccessibleForAdmin(params);
    }

    void verifyOnlyInstructorsCanAccess(Course course, String... params) {
        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(course, params);
        verifyAccessibleForInstructorsOfTheSameCourse(course, params);
        verifyAccessibleForInstructorsOfOtherCourse(course, params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(course, params);
    }

    void verifyOnlyInstructorsOfTheSameCourseCanAccess(Course course, String[] submissionParams) {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(course, submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(course, submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(course, submissionParams);
    }

    void verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
            Course course, String privilege, String[] submissionParams) {
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(course, submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);
        verifyInaccessibleWithoutCorrectCoursePrivilege(course, privilege, submissionParams);
    }

    // 'Mid-level' access control tests: here it tests access control of an action
    // for one user type.

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

    void verifyInaccessibleForStudents(Course course, String... params) {
        ______TS("Students cannot access");
        Student student = createTypicalStudent(course, "inaccessibleforstudents@teammates.tmt");

        loginAsStudent(student.getAccount().getGoogleId());
        verifyCannotAccess(params);

    }

    void verifyInaccessibleForInstructors(Course course, String... params) {
        ______TS("Instructors cannot access");
        Instructor instructor = createTypicalInstructor(course, "inaccessibleforinstructors@teammates.tmt");

        loginAsInstructor(instructor.getAccount().getGoogleId());
        verifyCannotAccess(params);

    }

    void verifyAccessibleForAdminToMasqueradeAsInstructor(
            Instructor instructor, String[] submissionParams) {
        ______TS("admin can access");

        loginAsAdmin();

        // not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor.getAccountId(), submissionParams);
    }

    void verifyAccessibleForAdminToMasqueradeAsInstructor(Course course, String[] submissionParams) {
        ______TS("admin can access");
        Instructor instructor = createTypicalInstructor(course,
                "accessibleforadmintomasqueradeasinstructor@teammates.tmt");
        loginAsAdmin();
        // not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(instructor.getAccountId(), submissionParams);
    }

    void verifyInaccessibleWithoutModifySessionPrivilege(Course course, String[] submissionParams) {
        ______TS("without Modify-Session privilege cannot access");

        Instructor instructor = createTypicalInstructor(course,
                "inaccessiblewithoutmodifysessionprivilege@teammates.tmt");

        loginAsInstructor(instructor.getAccount().getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(Course course, String[] submissionParams) {
        ______TS("without Submit-Session-In-Sections privilege cannot access");

        Instructor instructor = createTypicalInstructor(course,
                "inaccessiblewithoutsubmitsessioninsectionsprivilege@teammates.tmt");

        loginAsInstructor(instructor.getAccount().getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleWithoutCorrectCoursePrivilege(Course course, String privilege, String[] submissionParams) {
        Instructor instructor = createTypicalInstructor(course,
                "inaccessiblewithoutcorrectcourseprivilege@teammates.tmt");

        ______TS("without correct course privilege cannot access");

        loginAsInstructor(instructor.getAccount().getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("only instructor with correct course privilege should pass");
        InstructorPrivileges runtimePrivileges = new InstructorPrivileges(instructor.getId());
        runtimePrivileges.updatePrivilege(privilege, true);

        inTransaction(() -> {
            Instructor dbInstructor = logic.getInstructor(instructor.getId());
            logic.saveInstructorPrivileges(dbInstructor, runtimePrivileges);
            dbInstructor.setRole(InstructorPermissionRole.CUSTOM);
        });

        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor, submissionParams);
    }

    void verifyAccessibleForInstructorsOfTheSameCourse(Course course, String[] submissionParams) {
        ______TS("course instructor can access");
        Course courseOther = createTestCourseOther();
        assert !course.getId().equals(courseOther.getId());

        Instructor instructorSameCourse = createTypicalInstructor(course,
                "accessibleforinstructorsofthesamecourse-instructor@teammates.tmt");
        Student studentSameCourse = createTypicalStudent(course,
                "accessibleforinstructorsofthesamecourse-student@teammates.tmt");
        Instructor instructorOtherCourse = createTypicalInstructor(courseOther,
                "accessibleforinstructorsofthesamecourse-otherinstructor@teammates.tmt");

        loginAsInstructor(instructorSameCourse.getAccount().getGoogleId());
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(studentSameCourse.getAccountId(), submissionParams);
        verifyCannotMasquerade(instructorOtherCourse.getAccountId(), submissionParams);

    }

    void verifyAccessibleForInstructorsOfOtherCourse(Course course, String[] submissionParams) {
        ______TS("other course's instructor can access");
        Course courseOther = createTestCourseOther();
        assert !course.getId().equals(courseOther.getId());

        Instructor instructorSameCourse = createTypicalInstructor(course,
                "accessibleforinstructorsofothercourse-instructor@teammates.tmt");
        Student studentSameCourse = createTypicalStudent(course,
                "accessibleforinstructorsofothercourse-student@teammates.tmt");
        Instructor instructorOtherCourse = createTypicalInstructor(courseOther,
                "accessibleforinstructorsofothercourse-otherinstructor@teammates.tmt");

        loginAsInstructor(instructorOtherCourse.getAccount().getGoogleId());
        verifyCanAccess(submissionParams);

        verifyCannotMasquerade(studentSameCourse.getAccountId(), submissionParams);
        verifyCannotMasquerade(instructorSameCourse.getAccountId(), submissionParams);
    }

    void verifyAccessibleForStudentsOfTheSameCourse(Course course, String[] submissionParams) {
        ______TS("course students can access");
        Student student = createTypicalStudent(course, "accessibleforstudentsofthesamecourse@teammates.tmt");
        loginAsStudent(student.getAccount().getGoogleId());
        verifyCanAccess(submissionParams);
    }

    void verifyInaccessibleForStudentsOfOtherCourse(Course course, String[] submissionParams) {
        ______TS("other course student cannot access");
        Course courseOther = createTestCourseOther();
        Student otherStudent = createTypicalStudent(courseOther,
                "inaccessibleforstudentsofothercourse-other@teammates.tmt");
        assert !course.getId().equals(courseOther.getId());

        loginAsStudent(otherStudent.getAccount().getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyInaccessibleForInstructorsOfOtherCourses(Course course, String[] submissionParams) {
        ______TS("other course instructor cannot access");
        Course courseOther = createTestCourseOther();
        Instructor otherInstructor = createTypicalInstructor(courseOther,
                "inaccessibleforinstructorsofothercourses@teammates.tmt");
        assert !course.getId().equals(courseOther.getId());

        loginAsInstructor(otherInstructor.getAccount().getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    void verifyAccessibleForMaintainers(String... params) {
        ______TS("Maintainer can access");

        loginAsMaintainer();
        verifyCanAccess(params);
    }

    // 'Low-level' access control tests: here it tests an action once with the given
    // parameters.
    // These methods are not aware of the user type.

    /**
     * Verifies that the {@link Action} matching the {@code params} is accessible to
     * the logged in user.
     */
    protected void verifyCanAccess(String... params) {
        Action c = getAction(params);
        inTransaction(c::checkAccessControl);
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the user.
     */
    protected void verifyCannotAccess(String... params) {
        Action c = getAction(params);
        assertThrowsInTransaction(UnauthorizedAccessException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * accessible to the logged in user masquerading as another user with
     * {@code accountId}.
     */
    protected void verifyCanMasquerade(UUID accountId, String... params) {
        verifyCanAccess(addMasqueradeAccountToParams(accountId, params));
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the logged in user masquerading as another user with
     * {@code accountId}.
     */
    protected void verifyCannotMasquerade(UUID accountId, String... params) {
        Action action;
        try {
            action = getAction(addMasqueradeAccountToParams(accountId, params));
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof UnauthorizedAccessException);
            return;
        }
        assertThrowsInTransaction(UnauthorizedAccessException.class, action::checkAccessControl);
    }

    // The next few methods are for parsing results

    /**
     * Executes the action, verifies the status code as 200 OK, and returns the
     * result.
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
        ActionResult r = inTransaction(a::execute);
        assertEquals(statusCode, r.getStatusCode());
        return (JsonResult) r;
    }

    // The next few methods are for verifying action results

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(String... params) {
        Action c = getAction(params);
        return assertThrowsInTransaction(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpParameterException} being thrown.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrowsInTransaction(InvalidHttpParameterException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link InvalidHttpParameterException}
     * being thrown
     * when checking for access control.
     */
    protected InvalidHttpParameterException verifyHttpParameterFailureAcl(String... params) {
        Action c = getAction(params);
        return assertThrowsInTransaction(InvalidHttpParameterException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidHttpRequestBodyException} being thrown.
     */
    protected InvalidHttpRequestBodyException verifyHttpRequestBodyFailure(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrowsInTransaction(InvalidHttpRequestBodyException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException}
     * being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(String... params) {
        Action c = getAction(params);
        return assertThrowsInTransaction(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in {@link EntityNotFoundException}
     * being thrown.
     */
    protected EntityNotFoundException verifyEntityNotFound(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrowsInTransaction(EntityNotFoundException.class, c::execute);
    }

    /**
     * Verifies that the action results in {@link EntityNotFoundException} being
     * thrown when checking for access control.
     */
    protected EntityNotFoundException verifyEntityNotFoundAcl(String... params) {
        Action c = getAction(params);
        return assertThrowsInTransaction(EntityNotFoundException.class, c::checkAccessControl);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(String... params) {
        Action c = getAction(params);
        return assertThrowsInTransaction(InvalidOperationException.class, c::execute);
    }

    /**
     * Verifies that the executed action results in
     * {@link InvalidOperationException} being thrown.
     */
    protected InvalidOperationException verifyInvalidOperation(BasicRequest requestBody, String... params) {
        Action c = getAction(requestBody, params);
        return assertThrowsInTransaction(InvalidOperationException.class, c::execute);
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
     * Verifies that the executed action does not result in any email being queued.
     */
    protected void verifyNoEmailsQueued() {
        assertTrue(getQueuedEmails().isEmpty());
    }

    /**
     * Returns the list of emails sent as part of the executed action.
     */
    protected List<EmailWrapper> getEmailsSent() {
        return List.of();
    }

    /**
     * Returns the list of tasks added as part of the executed action.
     */
    protected List<TaskWrapper> getTasksAdded() {
        return mockTaskQueuer.getTasksAdded();
    }

    /**
     * Returns the list of emails queued as part of the executed action.
     */
    protected List<EmailWrapper> getQueuedEmails() {
        List<EmailWrapper> queuedEmails = new ArrayList<>();
        for (TaskWrapper task : getTasksAdded()) {
            SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
            queuedEmails.add(request.getEmail());
        }
        return queuedEmails;
    }

    /**
     * Verifies that the executed action results in the specified number of emails
     * being sent.
     */
    protected void verifyNumberOfEmailsSent(int emailCount) {
        assertEquals(emailCount, getEmailsSent().size());
    }

    /**
     * Verifies that the executed action results in the specified number of emails being queued.
     */
    protected void verifyNumberOfEmailsQueued(int emailCount) {
        assertEquals(emailCount, getQueuedEmails().size());
    }

    // TODO: createXX methods should be deprecated and replaced with proper test data builders.
    private Course createTestCourseOther() {
        if (testCourseOther == null) {
            testCourseOther = inTransaction(() -> {
                Institute institute = new Institute("test-institute", "SG");
                HibernateUtil.persist(institute);
                return coursesLogic.createCourse("test-course-other-id", "test course other",
                        Const.DEFAULT_TIME_ZONE, institute);
            });
        }
        return testCourseOther;
    }

    private Instructor createTypicalInstructor(Course course, String email) {
        Instructor instructor = inTransaction(() -> logic.getInstructorForEmail(course.getId(), email));
        if (instructor == null) {
            instructor = inTransaction(() -> {
                String googleId = email;
                String subject = email;
                Account account = logic.createAccount(Provider.TEAMMATES_DEV, subject, null, email, googleId);
                return logic.createInstructor(course, "instructor-name", email, true, "display-name",
                        InstructorPermissionRole.CUSTOM, account);
            });
        }
        return instructor;
    }

    private Student createTypicalStudent(Course course, String email) {
        Student student = inTransaction(() -> logic.getStudentForEmail(course.getId(), email));
        if (student == null) {
            student = inTransaction(() -> {
                Section section = logic.getSection(course.getId(), "section name");
                if (section == null) {
                    section = logic.createSection(course, "section name");
                }

                Team team = section.getTeams().stream()
                        .filter(t -> "team name".equals(t.getName()))
                        .findFirst()
                        .orElse(null);
                if (team == null) {
                    team = logic.createTeam(section, "team name");
                }

                Student createdStudent = logic.createStudent(course, team, "student-name", email, "");

                String googleId = email;
                String subject = email;
                Account account = logic.createAccount(Provider.TEAMMATES_DEV, subject, null, email, googleId);
                createdStudent.setAccount(account);
                return createdStudent;
            });
        }
        return student;
    }
}
