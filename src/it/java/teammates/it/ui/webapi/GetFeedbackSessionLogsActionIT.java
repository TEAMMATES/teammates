package teammates.it.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionLogData;
import teammates.ui.output.FeedbackSessionLogEntryData;
import teammates.ui.output.FeedbackSessionLogsData;
import teammates.ui.webapi.GetFeedbackSessionLogsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionLogsAction}.
 */
public class GetFeedbackSessionLogsActionIT extends BaseActionIT<GetFeedbackSessionLogsAction> {
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
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        JsonResult actionOutput;

        Course course = typicalBundle.courses.get("course1");
        String courseId = course.getId();
        FeedbackSession fsa1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student2 = typicalBundle.students.get("student2InCourse1");
        String student1Email = student1.getEmail();
        String student2Email = student2.getEmail();
        long endTime = Instant.parse("2012-01-02T12:00:00Z").toEpochMilli();
        long startTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() - 1) * 24 * 60 * 60 * 1000;

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime)
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime)
        );

        ______TS("Failure case: invalid course id");
        String[] paramsInvalid1 = {
                Const.ParamsNames.COURSE_ID, "fake-course-id",
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyEntityNotFound(paramsInvalid1);

        ______TS("Failure case: invalid student id");
        String[] paramsInvalid2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_SQL_ID, "00000000-0000-0000-0000-000000000000",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyEntityNotFound(paramsInvalid2);

        ______TS("Failure case: invalid start or end times");
        String[] paramsInvalid3 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, "abc",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyHttpParameterFailure(paramsInvalid3);

        String[] paramsInvalid4 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, " ",
        };
        verifyHttpParameterFailure(paramsInvalid4);

        ______TS("Success case: should group by feedback session");
        String[] paramsSuccessful1 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful1));

        // The filtering by the logs processor cannot be tested directly, assume that it filters correctly
        // Here, it simply returns all log entries
        FeedbackSessionLogsData fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        List<FeedbackSessionLogData> fsLogs = fslData.getFeedbackSessionLogs();

        // Course has 6 feedback sessions, last 4 of which have no log entries
        assertEquals(fsLogs.size(), 6);
        assertEquals(fsLogs.get(2).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(3).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(4).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(5).getFeedbackSessionLogEntries().size(), 0);

        List<FeedbackSessionLogEntryData> fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();
        List<FeedbackSessionLogEntryData> fsLogEntries2 = fsLogs.get(1).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 3);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(1).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(2).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(2).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        assertEquals(fsLogEntries2.size(), 2);
        assertEquals(fsLogEntries2.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries2.get(1).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should accept optional student Id");
        String[] paramsSuccessful2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful2));
        fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        fsLogs = fslData.getFeedbackSessionLogs();

        assertEquals(fsLogs.size(), 6);
        assertEquals(fsLogs.get(2).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(3).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(4).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(5).getFeedbackSessionLogEntries().size(), 0);

        fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();
        fsLogEntries2 = fsLogs.get(1).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 1);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);

        assertEquals(fsLogEntries2.size(), 2);
        assertEquals(fsLogEntries2.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries2.get(1).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should accept optional feedback session");
        String[] paramsSuccessful3 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful3));
        fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        fsLogs = fslData.getFeedbackSessionLogs();

        assertEquals(fsLogs.size(), 6);
        assertEquals(fsLogs.get(1).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(2).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(3).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(4).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(5).getFeedbackSessionLogEntries().size(), 0);

        fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 3);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(1).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(2).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(2).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        // TODO: if we restrict the range from start to end time, it should be tested here as well
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Course course = typicalBundle.courses.get("course1");
        String courseId = course.getId();
        Instructor helper = typicalBundle.instructors.get("instructor2OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        ______TS("Only instructors with modify student, session and instructor privilege can access");
        verifyCannotAccess(submissionParams);

        loginAsInstructor(helper.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Only instructors of the same course can access");
        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

}
