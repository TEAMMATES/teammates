package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionLogsData;

/**
 * Action: gets the feedback session logs of feedback sessions of a course.
 */
public class GetFeedbackSessionLogsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = sqlLogic.getCourse(courseId);

        if (course == null) {
            throw new EntityNotFoundException("Course is not found");
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() {
        String fslTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        List<FeedbackSessionLogType> convertedFslTypes = new ArrayList<>();
        if (fslTypes != null) {
            // Multiple log types are separated by a comma e.g access,submission
            for (String fslType : fslTypes.split(",")) {
                FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);

                if (convertedFslType == null) {
                    throw new InvalidHttpParameterException("Invalid log type");
                }

                convertedFslTypes.add(convertedFslType);
            }
        }

        String startTimeStr = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME);
        String endTimeStr = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME);
        long startTime;
        long endTime;
        try {
            startTime = Long.parseLong(startTimeStr);
            endTime = Long.parseLong(endTimeStr);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid start or end time", e);
        }
        // TODO: we might want to impose limits on the time range from startTime to endTime

        if (endTime < startTime) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        UUID studentId = null;
        UUID feedbackSessionId = null;
        String studentIdString = getRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        String feedbackSessionIdString = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        if (studentIdString != null) {
            studentId = getUuidFromString(Const.ParamsNames.STUDENT_SQL_ID, studentIdString);
        }

        if (feedbackSessionIdString != null) {
            feedbackSessionId = getUuidFromString(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionIdString);
        }

        if (sqlLogic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("Course not found");
        }

        if (studentId != null && sqlLogic.getStudent(studentId) == null) {
            throw new EntityNotFoundException("Student not found");
        }

        if (feedbackSessionId != null && sqlLogic.getFeedbackSession(feedbackSessionId) == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        List<FeedbackSessionLog> fsLogEntries = sqlLogic.getOrderedFeedbackSessionLogs(courseId, studentId,
                feedbackSessionId, Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime));
        Map<String, Student> studentsMap = new HashMap<>();
        Map<String, FeedbackSession> sessionsMap = new HashMap<>();
        List<FeedbackSession> feedbackSessions = sqlLogic.getFeedbackSessionsForCourse(courseId);
        feedbackSessions.forEach(fs -> sessionsMap.put(fs.getName(), fs));

        fsLogEntries = fsLogEntries.stream().filter(logEntry -> {
            FeedbackSessionLogType logType = logEntry.getFeedbackSessionLogType();
            if (logType == null || fslTypes != null && !convertedFslTypes.contains(logType)) {
                // If the feedback session log type retrieved from the log is invalid
                // or not the type being queried, ignore the log
                return false;
            }

            if (!studentsMap.containsKey(logEntry.getStudent().getEmail())) {
                Student student = sqlLogic.getStudent(logEntry.getStudent().getId());
                if (student == null) {
                    // If the student email retrieved from the log is invalid, ignore the log
                    return false;
                }
                studentsMap.put(student.getEmail(), student);
            }
            // If the feedback session retrieved from the log is invalid, ignore the log
            return sessionsMap.containsKey(logEntry.getFeedbackSession().getName());
        }).collect(Collectors.toList());

        Map<String, List<FeedbackSessionLog>> groupedEntries = groupFeedbackSessionLogs(fsLogEntries);
        feedbackSessions.forEach(fs -> groupedEntries.putIfAbsent(fs.getName(), new ArrayList<>()));

        FeedbackSessionLogsData fslData = new FeedbackSessionLogsData(groupedEntries, studentsMap, sessionsMap);
        return new JsonResult(fslData);
    }

    private Map<String, List<FeedbackSessionLog>> groupFeedbackSessionLogs(
            List<FeedbackSessionLog> fsLogEntries) {
        Map<String, List<FeedbackSessionLog>> groupedEntries = new LinkedHashMap<>();
        for (FeedbackSessionLog fsLogEntry : fsLogEntries) {
            String fsName = fsLogEntry.getFeedbackSession().getName();
            groupedEntries.computeIfAbsent(fsName, k -> new ArrayList<>()).add(fsLogEntry);
        }
        return groupedEntries;
    }
}
