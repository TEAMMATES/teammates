
package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackSessionsAction;
import teammates.ui.webapi.output.FeedbackSessionsData;

/**
 * SUT: {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(6, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForCourse() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(0, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSoftDeletedSessionsForCourse() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(6, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asInstructorNotEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_asStudentWithCourseId_shouldReturnAllSessionsForCourse() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(6, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        GetFeedbackSessionsAction a = getAction();
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(6, fsData.getFeedbackSessions().size());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        loginAsStudent(student1InCourse1.googleId);
        ______TS("student can access");
        verifyCanAccess();

        ______TS("student of the same course can access");
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyCanAccess(submissionParam);

        ______TS("Student of another course cannot access");
        loginAsStudent(student1InCourse2.googleId);
        verifyCannotAccess(submissionParam);

        ______TS("instructor can access");
        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess();

        ______TS("instructor of the same course can access");
        verifyCanAccess(submissionParam);

        ______TS("instructor of another course cannot access");
        loginAsInstructor(instructor1OfCourse2.googleId);
        verifyCannotAccess(submissionParam);

        verifyInaccessibleForAdmin();
        verifyInaccessibleForUnregisteredUsers();
        verifyInaccessibleWithoutLogin();
    }

}
