package teammates.it.ui.webapi;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.sqllogic.core.FeedbackSessionLogsLogic;
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

    private final FeedbackSessionLogsLogic fslLogic = FeedbackSessionLogsLogic.inst();

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
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
        Student student3 = typicalBundle.students.get("student1InCourse3");

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId1);
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_SQL_ID, student2.getId().toString()
        );

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        verifyHttpParameterFailure(paramsInvalid);

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulAccess));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        List<FeedbackSessionLog> persistedAccessLogs = logic.getOrderedFeedbackSessionLogs(courseId1, student1.getId(),
                fs1.getId(), Instant.now().minusSeconds(60), Instant.now().plusSeconds(60));
        assertEquals(persistedAccessLogs.size(), 1);
        assertEquals(persistedAccessLogs.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);

        ______TS("Success case: typical submission");
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs2.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student2.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student2.getId().toString(),
        };
        response = getJsonResult(getAction(paramsSuccessfulSubmission));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        List<FeedbackSessionLog> persistedSubmissionLogs = logic.getOrderedFeedbackSessionLogs(courseId1,
                student2.getId(), fs2.getId(), Instant.now().minusSeconds(60), Instant.now().plusSeconds(60));
        assertEquals(persistedSubmissionLogs.size(), 1);
        assertEquals(persistedSubmissionLogs.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should create even for invalid parameters");
        String[] paramsNonExistentCourseId = {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        response = getJsonResult(getAction(paramsNonExistentCourseId));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
        assertEquals(logic.getOrderedFeedbackSessionLogs(courseId1, null, null, Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(60)).size(), 3);

        ______TS("Failure case: should fail for missing feedback session");
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, UUID.randomUUID().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        verifyHttpParameterFailure(paramsNonExistentFsName);
        assertEquals(logic.getOrderedFeedbackSessionLogs(courseId1, null, null, Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(60)).size(), 3);

        ______TS("Failure case: should fail for missing student");
        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@email.com",
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, UUID.randomUUID().toString(),
        };
        verifyHttpParameterFailure(paramsNonExistentStudentEmail);
        assertEquals(logic.getOrderedFeedbackSessionLogs(courseId1, null, null, Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(60)).size(), 3);

        ______TS("Failure case: should fail when student does not belong to feedback session course");
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student3.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student3.getId().toString(),
        };
        verifyHttpParameterFailure(paramsWithoutAccess);
        assertEquals(logic.getOrderedFeedbackSessionLogs(courseId1, student3.getId(), fs1.getId(),
                Instant.now().minusSeconds(60), Instant.now().plusSeconds(60)).size(), 0);

        ______TS("Success case: duplicate log should still be persisted");
        FeedbackSessionLog latestAccessLog = fslLogic.getLatestFeedbackSessionLog(student1.getId(), fs1.getId(),
                FeedbackSessionLogType.ACCESS);
        Instant fixedNow = latestAccessLog.getTimestamp().plusMillis(1);
        CreateFeedbackSessionLogAction duplicateAction = getAction(paramsSuccessfulAccess);
        Field clockField = CreateFeedbackSessionLogAction.class.getDeclaredField("clock");
        clockField.setAccessible(true);
        clockField.set(duplicateAction, Clock.fixed(fixedNow, ZoneOffset.UTC));
        response = getJsonResult(duplicateAction);
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
        assertEquals(logic.getOrderedFeedbackSessionLogs(courseId1, student1.getId(), fs1.getId(),
                Instant.now().minusSeconds(60), Instant.now().plusSeconds(60)).size(), 3);

        ______TS("Success case: different log type should still be persisted");
        String[] paramsViewResult = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.VIEW_RESULT.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        response = getJsonResult(getAction(paramsViewResult));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        List<FeedbackSessionLog> allStudent1Session1Logs = logic.getOrderedFeedbackSessionLogs(courseId1,
                student1.getId(), fs1.getId(), Instant.now().minusSeconds(60), Instant.now().plusSeconds(60));
        assertEquals(allStudent1Session1Logs.size(), 4);
        assertTrue(allStudent1Session1Logs.stream()
                .anyMatch(logEntry -> logEntry.getFeedbackSessionLogType() == FeedbackSessionLogType.VIEW_RESULT));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student2 = typicalBundle.students.get("student2InCourse1");
        FeedbackSession fs1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = {
                Const.ParamsNames.COURSE_ID, student1.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };

        loginAsStudent(student1.getAccount().getGoogleId());
        verifyCanAccess(params);

        loginAsStudent(student2.getAccount().getGoogleId());
        verifyCannotAccess(params);

        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForAdmin(params);
        verifyInaccessibleForInstructors(student1.getCourse(), params);
    }
}
