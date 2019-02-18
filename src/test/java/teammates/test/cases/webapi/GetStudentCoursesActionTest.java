package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.storage.api.AccountsDb;
import teammates.ui.webapi.action.GetStudentCoursesAction;
import teammates.ui.webapi.action.GetStudentCoursesAction.StudentCourses;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetStudentCoursesAction}.
 */
public class GetStudentCoursesActionTest extends BaseActionTest<GetStudentCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        String unregUserId = "unreg.user";
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String studentId;

        String[] submissionParams = new String[] {};

        ______TS("unregistered student");

        loginAsUnregistered(unregUserId);
        GetStudentCoursesAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("Ooops! Your Google account is not known to TEAMMATES{*}use the new Gmail address.",
                msg.getMessage());

        ______TS("registered student with no courses");

        // Note: this can happen only if the course was deleted after the student joined it.
        // The 'welcome stranger' response is not really appropriate for this situation, but
        // we keep it because the situation is rare and not worth extra coding.

        // Create a student account without courses
        AccountAttributes studentWithoutCourses = AccountAttributes.builder("googleId.without.courses")
                .withName("Student Without Courses")
                .withEmail("googleId.without.courses@email.tmt")
                .withInstitute("TEAMMATES Test Institute 5")
                .withIsInstructor(false)
                .build();

        AccountsDb accountsDb = new AccountsDb();
        accountsDb.createEntity(studentWithoutCourses);
        assertNotNull(accountsDb.getAccount(studentWithoutCourses.googleId));

        loginAsUnregistered(studentWithoutCourses.googleId);
        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());
        msg = (MessageOutput) r.getOutput();
        assertEquals("Ooops! Your Google account is not known to TEAMMATES{*}use the new Gmail address.",
                msg.getMessage());

        ______TS("typical user, masquerade mode");

        loginAsAdmin();
        studentId = typicalBundle.students.get("student2InCourse2").googleId;

        // Access page in masquerade mode
        a = getAction(addUserIdToParams(studentId, submissionParams));
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        StudentCourses data = (StudentCourses) r.getOutput();
        assertEquals(2, data.getCourses().size());

        ______TS("New student with no existing course, course join affected by eventual consistency");

        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1",
        };

        studentId = "newStudent";
        loginAsUnregistered(studentId);

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        data = (StudentCourses) r.getOutput();
        assertEquals(1, data.getCourses().size());
        assertEquals("idOfTypicalCourse1", data.getCourses().get(0).getCourse().getId());

        ______TS("Registered student with existing courses, course join affected by eventual consistency");

        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse2",
        };

        studentId = student1InCourse1.googleId;
        loginAsStudent(studentId);

        a = getAction(submissionParams);
        r = getJsonResult(a);
        data = (StudentCourses) r.getOutput();

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        assertEquals(2, data.getCourses().size());
        assertEquals("idOfTypicalCourse2", data.getCourses().get(1).getCourse().getId());

        ______TS("Just joined course, course join not affected by eventual consistency and appears in list");

        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1",
        };

        studentId = student1InCourse1.googleId;
        loginAsStudent(studentId);

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        data = (StudentCourses) r.getOutput();
        assertEquals(1, data.getCourses().size());

        // Delete additional sessions that were created
        CoursesLogic.inst().deleteCourseCascade("typicalCourse2");
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyInaccessibleWithoutLogin(submissionParams);

        // check for persistence issue
        submissionParams = new String[] {
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "random_course",
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }
}
