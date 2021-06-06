package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.constants.LogType;
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
    protected void testExecute() throws Exception {
        JsonResult actionOutput;

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        String courseId = course.getId();
        FeedbackSessionAttributes fsa1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes fsa2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2 = typicalBundle.students.get("student2InCourse1");
        String student1Email = student1.getEmail();
        String student2Email = student2.getEmail();
        long endTime = Instant.now().toEpochMilli();
        long startTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() - 1) * 24 * 60 * 60 * 1000;
        long invalidStartTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() + 1) * 24 * 60 * 60 * 1000;

        mockLogsProcessor.insertFeedbackSessionLog(student1, fsa1,
                Const.FeedbackSessionLogTypes.ACCESS, startTime);
        mockLogsProcessor.insertFeedbackSessionLog(student1, fsa2,
                Const.FeedbackSessionLogTypes.ACCESS, startTime + 1000);
        mockLogsProcessor.insertFeedbackSessionLog(student1, fsa2,
                Const.FeedbackSessionLogTypes.SUBMISSION, startTime + 2000);
        mockLogsProcessor.insertFeedbackSessionLog(student2, fsa1,
                Const.FeedbackSessionLogTypes.ACCESS, startTime + 3000);
        mockLogsProcessor.insertFeedbackSessionLog(student2, fsa1,
                Const.FeedbackSessionLogTypes.SUBMISSION, startTime + 4000);

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
        actionOutput = getJsonResult(getAction(paramsInvalid1));
        assertEquals(HttpStatus.SC_NOT_FOUND, actionOutput.getStatusCode());

        ______TS("Failure case: invalid student email");
        String[] paramsInvalid2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, "fake-student-email@gmail.com",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsInvalid2));
        assertEquals(HttpStatus.SC_NOT_FOUND, actionOutput.getStatusCode());

        ______TS("Failure case: invalid start or end times");
        String[] paramsInvalid3 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, "abc",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsInvalid3));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        String[] paramsInvalid4 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, " ",
        };
        actionOutput = getJsonResult(getAction(paramsInvalid4));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

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
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        // The filtering by the logs processor cannot be tested directly, assume that it filters correctly
        // Here, it simply returns all log entries
        FeedbackSessionLogsData fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        List<FeedbackSessionLogData> fsLogs = fslData.getFeedbackSessionLogs();

        // Course has 6 feedback sessions, first 4 of which have no log entries
        assertEquals(fsLogs.size(), 6);
        assertEquals(fsLogs.get(0).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(1).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(2).getFeedbackSessionLogEntries().size(), 0);
        assertEquals(fsLogs.get(3).getFeedbackSessionLogEntries().size(), 0);

        List<FeedbackSessionLogEntryData> fsLogEntries1 = fsLogs.get(4).getFeedbackSessionLogEntries();
        List<FeedbackSessionLogEntryData> fsLogEntries2 = fsLogs.get(5).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 3);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), LogType.FEEDBACK_SESSION_ACCESS);
        assertEquals(fsLogEntries1.get(1).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(1).getFeedbackSessionLogType(), LogType.FEEDBACK_SESSION_ACCESS);
        assertEquals(fsLogEntries1.get(2).getStudentData().getEmail(), student2Email);
        assertEquals(fsLogEntries1.get(2).getFeedbackSessionLogType(), LogType.FEEDBACK_SESSION_SUBMISSION);

        assertEquals(fsLogEntries2.size(), 2);
        assertEquals(fsLogEntries2.get(0).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(0).getFeedbackSessionLogType(), LogType.FEEDBACK_SESSION_ACCESS);
        assertEquals(fsLogEntries2.get(1).getStudentData().getEmail(), student1Email);
        assertEquals(fsLogEntries2.get(1).getFeedbackSessionLogType(), LogType.FEEDBACK_SESSION_SUBMISSION);

        ______TS("Success case: should accept optional email");
        String[] paramsSuccessful2 = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful2));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
        // No need to check output again here, it will be exactly the same as the previous case

        // TODO: if we restrict the range from start to end time, it should be tested here as well
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
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

        loginAsInstructor(helper.googleId);
        verifyCannotAccess(submissionParams);

        loginAsInstructor(instructor.googleId);
        verifyCanAccess(submissionParams);
    }

}
