package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.storage.api.AccountsDb;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.Priority;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentHomePageAction;
import teammates.ui.pagedata.StudentHomePageData;

/**
 * SUT: {@link StudentHomePageAction}.
 */
// Priority added due to conflict between InstructorStudentListPageActionTest,
// and StudentHomePageActionTest.
@Priority(-2)
public class StudentHomePageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_HOME_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String unregUserId = "unreg.user";
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        String adminUserId = "admin.user";

        String[] submissionParams = new String[] {};

        ______TS("unregistered student");

        gaeSimulation.loginUser(unregUserId);
        StudentHomePageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        AssertHelper.assertContainsRegex(
                getPageResultDestination(Const.ViewURIs.STUDENT_HOME, false, "unreg.user"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        AssertHelper.assertContainsRegex(
                "Ooops! Your Google account is not known to TEAMMATES{*}use the new Gmail address.",
                r.getStatusMessage());

        StudentHomePageData data = (StudentHomePageData) r.data;
        assertEquals(0, data.getCourseTables().size());

        String expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage"
                                    + "|||true|||Unregistered|||Unknown|||unreg.user|||Unknown"
                                    + "|||Servlet Action Failure :Student with Google ID "
                                    + "unreg.user does not exist|||/page/studentHomePage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("registered student with no courses");

        // Note: this can happen only if the course was deleted after the student joined it.
        // The 'welcome stranger' response is not really appropriate for this situation, but
        // we keep it because the situation is rare and not worth extra coding.

        // Create a student account without courses
        AccountAttributes studentWithoutCourses = AccountAttributes.builder()
                .withGoogleId("googleId.without.courses")
                .withName("Student Without Courses")
                .withEmail("googleId.without.courses@email.tmt")
                .withInstitute("TEAMMATES Test Institute 5")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes("googleId.without.courses")
                .build();

        AccountsDb accountsDb = new AccountsDb();
        accountsDb.createAccount(studentWithoutCourses);
        assertNotNull(accountsDb.getAccount(studentWithoutCourses.googleId));

        gaeSimulation.loginUser(studentWithoutCourses.googleId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        AssertHelper.assertContainsRegex(
                getPageResultDestination(Const.ViewURIs.STUDENT_HOME, false, studentWithoutCourses.googleId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        AssertHelper.assertContainsRegex(
                "Ooops! Your Google account is not known to TEAMMATES{*}use the new Gmail address.",
                r.getStatusMessage());

        data = (StudentHomePageData) r.data;
        assertEquals(0, data.getCourseTables().size());

        expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true"
                             + "|||Unregistered|||Student Without Courses|||googleId.without.courses"
                             + "|||googleId.without.courses@email.tmt|||Servlet Action Failure "
                             + ":Student with Google ID googleId.without.courses does not exist"
                             + "|||/page/studentHomePage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("typical user, masquerade mode");

        gaeSimulation.loginAsAdmin(adminUserId);
        studentId = typicalBundle.students.get("student2InCourse2").googleId;

        // Access page in masquerade mode
        a = getAction(addUserIdToParams(studentId, submissionParams));
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.STUDENT_HOME, false, studentId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        data = (StudentHomePageData) r.data;
        assertEquals(2, data.getCourseTables().size());

        expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true"
                             + "|||Student(M)|||Student in two courses|||student2InCourse1"
                             + "|||student2InCourse1@gmail.tmt"
                             + "|||studentHome Page Load<br>Total courses: 2"
                             + "|||/page/studentHomePage";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);

        ______TS("New student with no existing course, course join affected by eventual consistency");
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1"
        };
        studentId = "newStudent";
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData) r.data;
        assertEquals(1, data.getCourseTables().size());
        assertEquals("idOfTypicalCourse1", data.getCourseTables().get(0).getCourseId());

        ______TS("Registered student with existing courses, course join affected by eventual consistency");
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse2"
        };
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        studentId = student1InCourse1.googleId;
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData) r.data;
        assertEquals(2, data.getCourseTables().size());
        assertEquals("idOfTypicalCourse2", data.getCourseTables().get(1).getCourseId());

        ______TS("Just joined course, course join not affected by eventual consistency and appears in list");
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1"
        };
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        studentId = student1InCourse1.googleId;
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData) r.data;
        assertEquals(1, data.getCourseTables().size());

        // Delete additional sessions that were created
        CoursesLogic.inst().deleteCourseCascade("typicalCourse2");
    }

    @Override
    protected StudentHomePageAction getAction(String... params) {
        return (StudentHomePageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyLoggedInUsersCanAccess(submissionParams);

        // check for persistence issue
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "random_course"
        };

        verifyAccessibleForUnregisteredUsers(submissionParams);
    }

}
