package teammates.it.ui.webapi;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.CreateFeedbackSessionLogAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionIT extends BaseActionIT<CreateFeedbackSessionLogAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Course course1 = typicalBundle.courses.get("course1");
        String courseId1 = course1.getId();
        FeedbackSession fs1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession fs2 = typicalBundle.feedbackSessions.get("session2InTypicalCourse");
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student2 = typicalBundle.students.get("student2InCourse1");

        loginAsStudent(student1.getAccount().getGoogleId());

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString());
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name());

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
        };
        verifyHttpParameterFailure(paramsInvalid);

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulAccess));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        List<FeedbackSessionLog> persistedAccessLogs = logic.getOrderedFeedbackSessionLogs(courseId1,
                student1.getId(),
                fs1.getId(), Instant.now().minusSeconds(60), Instant.now().plusSeconds(60));
        assertEquals(1, persistedAccessLogs.size());
        assertEquals(fs1.getId(), persistedAccessLogs.get(0).getFeedbackSession().getId());
        assertEquals(student1.getId(), persistedAccessLogs.get(0).getStudent().getId());
        assertEquals(FeedbackSessionLogType.ACCESS, persistedAccessLogs.get(0).getFeedbackSessionLogType());

        ______TS("Success case: typical submission");
        loginAsStudent(student2.getAccount().getGoogleId());

        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
        };
        response = getJsonResult(getAction(paramsSuccessfulSubmission));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        List<FeedbackSessionLog> persistedSubmissionLogs = logic.getOrderedFeedbackSessionLogs(courseId1,
                student2.getId(), fs2.getId(), Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(60));
        assertEquals(1, persistedSubmissionLogs.size());
        assertEquals(fs2.getId(), persistedSubmissionLogs.get(0).getFeedbackSession().getId());
        assertEquals(student2.getId(), persistedSubmissionLogs.get(0).getStudent().getId());
        assertEquals(FeedbackSessionLogType.SUBMISSION,
                persistedSubmissionLogs.get(0).getFeedbackSessionLogType());

        ______TS("Failure case: should fail for missing feedback session");
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, UUID.randomUUID().toString(),
        };
        verifyHttpParameterFailure(paramsNonExistentFsName);
        assertEquals(2, logic
                .getOrderedFeedbackSessionLogs(courseId1, null, null, Instant.now().minusSeconds(60),
                        Instant.now().plusSeconds(60))
                .size());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        FeedbackSession fs1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession fs2 = typicalBundle.feedbackSessions.get("ongoingSession1InCourse3");
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
        };

        loginAsStudent(student1.getAccount().getGoogleId());
        verifyCanAccess(params);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
        };
        verifyCannotAccess(params);

        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForAdmin(params);
        verifyInaccessibleForInstructors(student1.getCourse(), params);
    }
}
