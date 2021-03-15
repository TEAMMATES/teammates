package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.LogsServiceException;
import teammates.common.util.Const;
import teammates.ui.constants.LogType;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionLogData;
import teammates.ui.output.FeedbackSessionLogEntryData;
import teammates.ui.output.FeedbackSessionLogsData;
import teammates.ui.output.StudentData;

/**
 * Action: gets the feedback session logs of feedback sessions of a course.
 */
public class GetFeedbackSessionLogsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseAttributes courseAttributes = logic.getCourse(courseId);

        if (courseAttributes == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Course is not found"));
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, courseAttributes);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (logic.getCourse(courseId) == null) {
            return new JsonResult("Course not found", HttpStatus.SC_NOT_FOUND);
        }
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        if (email != null && logic.getStudentForEmail(courseId, email) == null) {
            return new JsonResult("Student not found", HttpStatus.SC_NOT_FOUND);
        }
        String startTimeStr = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME);
        String endTimeStr = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME);
        Instant startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
        Instant endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
        // TODO: we might want to impose limits on the time range from startTime to endTime

        try {
            List<FeedbackSessionLogEntry> fsLogEntries = logic.getFeedbackSessionLogs(courseId, email, startTime, endTime);
            FeedbackSessionLogsData fslData = createFeedbackSessionLogsData(courseId, fsLogEntries);
            return new JsonResult(fslData);
        } catch (LogsServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private FeedbackSessionLogsData createFeedbackSessionLogsData(String courseId,
            List<FeedbackSessionLogEntry> fsLogEntries) {
        Map<FeedbackSessionAttributes, List<FeedbackSessionLogEntry>> groupedEntries =
                groupFeedbackSessionLogEntries(courseId, fsLogEntries);
        List<FeedbackSessionLogData> fsLogDatas = groupedEntries.entrySet().stream()
                .map(entry -> {
                    FeedbackSessionAttributes feedbackSession = entry.getKey();
                    List<FeedbackSessionLogEntry> logEntries = entry.getValue();
                    return createFeedbackSessionLogData(feedbackSession, logEntries);
                })
                .collect(Collectors.toList());
        return new FeedbackSessionLogsData(fsLogDatas);
    }

    private Map<FeedbackSessionAttributes, List<FeedbackSessionLogEntry>> groupFeedbackSessionLogEntries(
            String courseId, List<FeedbackSessionLogEntry> fsLogEntries) {
        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        Map<FeedbackSessionAttributes, List<FeedbackSessionLogEntry>> groupedEntries = new HashMap<>();
        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            groupedEntries.put(feedbackSession, new ArrayList<>());
        }
        for (FeedbackSessionLogEntry fsLogEntry : fsLogEntries) {
            FeedbackSessionAttributes fs = fsLogEntry.getFeedbackSession();
            if (groupedEntries.get(fs) != null) {
                groupedEntries.get(fs).add(fsLogEntry);
            }
        }
        return groupedEntries;
    }

    private FeedbackSessionLogData createFeedbackSessionLogData(FeedbackSessionAttributes feedbackSession,
            List<FeedbackSessionLogEntry> logEntries) {
        FeedbackSessionData fsData = new FeedbackSessionData(feedbackSession);
        List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                .map(logEntry -> createFeedbackSessionLogEntryData(logEntry))
                .collect(Collectors.toList());
        return new FeedbackSessionLogData(fsData, fsLogEntryDatas);
    }

    private FeedbackSessionLogEntryData createFeedbackSessionLogEntryData(FeedbackSessionLogEntry logEntry) {
        StudentAttributes student = logEntry.getStudent();
        StudentData studentData = new StudentData(student);
        LogType logType = LogType.valueOfLabel(logEntry.getFeedbackSessionLogType());
        long timestamp = logEntry.getTimestamp();
        return new FeedbackSessionLogEntryData(studentData, logType, timestamp);
    }
}
