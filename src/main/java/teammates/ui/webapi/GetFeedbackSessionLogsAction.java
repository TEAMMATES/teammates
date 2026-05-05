package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
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
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course is not found");
        }

        UUID feedbackSessionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        if (feedbackSessionId != null) {
            // feedback session must be in the course specified by courseId
            FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
            if (feedbackSession == null || !Objects.equals(feedbackSession.getCourseId(), courseId)) {
                throw new EntityNotFoundException("Feedback session is not found in the specified course");
            }
        }

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() {
        String[] fslTypes = getNonNullRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        List<FeedbackSessionLogType> convertedFslTypes = new ArrayList<>();

        for (String fslType : fslTypes) {
            try {
                FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOf(fslType);
                convertedFslTypes.add(convertedFslType);
            } catch (IllegalArgumentException e) {
                throw new InvalidHttpParameterException("Invalid log type: " + fslType, e);
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

        if (endTime < startTime) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID studentId = getNullableUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        UUID feedbackSessionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        if (logic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("Course not found");
        }

        if (studentId != null && logic.getStudent(studentId) == null) {
            throw new EntityNotFoundException("Student not found");
        }

        if (feedbackSessionId != null && logic.getFeedbackSession(feedbackSessionId) == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        List<FeedbackSessionLog> fsLogEntries = logic.getOrderedFeedbackSessionLogs(courseId, studentId,
                feedbackSessionId, Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime));
        Map<String, Student> studentsMap = new HashMap<>();
        List<Student> students = logic.getStudentsForCourse(courseId);
        students.forEach(student -> studentsMap.put(student.getEmail(), student));

        Map<String, FeedbackSession> sessionsMap = new HashMap<>();
        List<FeedbackSession> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        feedbackSessions.forEach(fs -> sessionsMap.put(fs.getName(), fs));

        fsLogEntries = fsLogEntries.stream().filter(logEntry -> {
            FeedbackSessionLogType logType = logEntry.getFeedbackSessionLogType();
            // log entry may reference a soft-deleted feedback session which is not in sessionsMap
            String sessionName = logEntry.getFeedbackSession().getName();

            return convertedFslTypes.contains(logType) && sessionsMap.containsKey(sessionName);
        }).toList();

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
