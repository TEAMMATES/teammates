package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionLogData;
import teammates.ui.output.FeedbackSessionLogEntryData;
import teammates.ui.output.FeedbackSessionLogsData;

/**
 * SUT: {@link GetFeedbackSessionLogsAction}.
 */
public class GetFeedbackSessionLogsActionTest extends BaseActionTest<GetFeedbackSessionLogsAction> {
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

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        String courseId = course.getId();
        FeedbackSessionAttributes fsa1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes fsa2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        String fsa1Name = fsa1.getFeedbackSessionName();
        String fsa2Name = fsa2.getFeedbackSessionName();
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2 = typicalBundle.students.get("student2InCourse1");
        String student1Email = student1.getEmail();
        String student2Email = student2.getEmail();
        long endTime = Instant.now().toEpochMilli();
        long startTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() - 1) * 24 * 60 * 60 * 1000;
        long invalidStartTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() + 1) * 24 * 60 * 60 * 1000;

        mockLogsProcessor.insertFeedbackSessionLog(courseId, student1Email, fsa1Name,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime);
        mockLogsProcessor.insertFeedbackSessionLog(courseId, student1Email, fsa2Name,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime + 1000);
        mockLogsProcessor.insertFeedbackSessionLog(courseId, student1Email, fsa2Name,
                FeedbackSessionLogType.SUBMISSION.getLabel(), startTime + 2000);
        mockLogsProcessor.insertFeedbackSessionLog(courseId, student2Email, fsa1Name,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime + 3000);
        mockLogsProcessor.insertFeedbackSessionLog(courseId, student2Email, fsa1Name,
                FeedbackSessionLogType.SUBMISSION.getLabel(), startTime + 4000);

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
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyEntityNotFound(paramsInvalid1);

        ______TS("Failure case: invalid student email");
        String[] paramsInvalid2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, "fake-student-email@gmail.com",
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

        ______TS("Failure case: start time is before earliest search time");
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(invalidStartTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime)
        );

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

        ______TS("Success case: should accept optional email");
        String[] paramsSuccessful2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        getJsonResult(getAction(paramsSuccessful2));
        // No need to check output again here, it will be exactly the same as the previous case

        // TODO: if we restrict the range from start to end time, it should be tested here as well
    }

    @Test
    @Override
    protected void testAccessControl() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes helper = typicalBundle.instructors.get("helperOfCourse1");
        String courseId = instructor.getCourseId();

        ______TS("Only instructors of the same course can access");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);

        ______TS("Only instructors with modify student, session and instructor privilege can access");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyCannotAccess(submissionParams);

        loginAsInstructor(helper.getGoogleId());
        verifyCannotAccess(submissionParams);

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

}
