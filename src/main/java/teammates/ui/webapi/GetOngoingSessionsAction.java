package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.OngoingSession;
import teammates.ui.output.OngoingSessionsData;

/**
 * Gets the list of all ongoing sessions.
 */
public class GetOngoingSessionsAction extends AdminOnlyAction {

    private static final String INVALID_START_TIME = "Invalid start time.";
    private static final String INVALID_END_TIME = "Invalid end time.";
    private static final String INVALID_RANGE = "The filter range is not valid. End time should be after start time.";

    @Override
    public JsonResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME);
        long startTime = parseTimeStringIfValid(startTimeString, INVALID_START_TIME);
        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME);
        long endTime = parseTimeStringIfValid(endTimeString, INVALID_END_TIME);
        validateTimeParameters(startTime, endTime);
        Instant rangeStart = Instant.ofEpochMilli(startTime);
        Instant rangeEnd = Instant.ofEpochMilli(endTime);
        List<FeedbackSession> ongoingSqlSessions = sqlLogic.getOngoingSessions(rangeStart, rangeEnd);
        Map<String, List<FeedbackSession>> courseIdToFeedbackSessionsSqlMap =
                createCourseIdToFeedbackSessionsSqlMap(ongoingSqlSessions);
        List<FeedbackSessionAttributes> allOngoingSessions = logic.getAllOngoingSessions(rangeStart, rangeEnd);
        Map<String, List<FeedbackSessionAttributes>> courseIdToFeedbackSessionsMap =
                createCourseIdToFeedbackSessionsMap(allOngoingSessions, courseIdToFeedbackSessionsSqlMap);
        Map<String, List<OngoingSession>> instituteToFeedbackSessionsSqlMap =
                createInstituteToFeedbackSessionsSqlMap(courseIdToFeedbackSessionsSqlMap);
        Map<String, List<OngoingSession>> instituteToFeedbackSessionsMap =
                createInstituteToFeedbackSessionsMap(courseIdToFeedbackSessionsMap);
        for (var sqlInstituteFeedbackSessionList : instituteToFeedbackSessionsSqlMap.entrySet()) {
            String sqlInstitute = sqlInstituteFeedbackSessionList.getKey();
            List<OngoingSession> sqlFeedbackSessions = sqlInstituteFeedbackSessionList.getValue();
            instituteToFeedbackSessionsMap.computeIfAbsent(sqlInstitute, k -> new ArrayList<>())
                    .addAll(sqlFeedbackSessions);
        }
        OngoingSessionsData output = createOutput(courseIdToFeedbackSessionsSqlMap, courseIdToFeedbackSessionsMap,
                instituteToFeedbackSessionsMap);
        return new JsonResult(output);
    }

    private long parseTimeStringIfValid(String timeString, String exceptionMessageIfInvalid) {
        long time;
        try {
            time = Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException(exceptionMessageIfInvalid, e);
        }
        return time;
    }

    private void validateTimeParameters(long startTime, long endTime) {
        try {
            // test for bounds
            Instant.ofEpochMilli(startTime).minus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW).toEpochMilli();
        } catch (ArithmeticException e) {
            throw new InvalidHttpParameterException(INVALID_START_TIME, e);
        }
        try {
            // test for bounds
            Instant.ofEpochMilli(endTime).plus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW).toEpochMilli();
        } catch (ArithmeticException e) {
            throw new InvalidHttpParameterException(INVALID_END_TIME, e);
        }
        if (startTime > endTime) {
            throw new InvalidHttpParameterException(INVALID_RANGE);
        }
    }

    private Map<String, List<FeedbackSession>> createCourseIdToFeedbackSessionsSqlMap(
            List<FeedbackSession> ongoingSqlSessions) {
        Map<String, List<FeedbackSession>> courseIdToFeedbackSessionsSqlMap = new HashMap<>();
        for (FeedbackSession fs : ongoingSqlSessions) {
            String courseId = fs.getCourse().getId();
            if (!isCourseMigrated(courseId)) {
                continue;
            }
            courseIdToFeedbackSessionsSqlMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(fs);
        }
        return courseIdToFeedbackSessionsSqlMap;
    }

    private Map<String, List<FeedbackSessionAttributes>> createCourseIdToFeedbackSessionsMap(
            List<FeedbackSessionAttributes> allOngoingSessions,
            Map<String, List<FeedbackSession>> courseIdToFeedbackSessionsSqlMap) {
        Map<String, List<FeedbackSessionAttributes>> courseIdToFeedbackSessionsMap = new HashMap<>();
        for (FeedbackSessionAttributes fs : allOngoingSessions) {
            String courseId = fs.getCourseId();
            if (courseIdToFeedbackSessionsSqlMap.containsKey(courseId)) {
                continue;
            }
            courseIdToFeedbackSessionsMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(fs);
        }
        return courseIdToFeedbackSessionsMap;
    }

    private Map<String, List<OngoingSession>> createInstituteToFeedbackSessionsSqlMap(
            Map<String, List<FeedbackSession>> courseIdToFeedbackSessionsSqlMap) {
        Map<String, List<OngoingSession>> instituteToFeedbackSessionsSqlMap = new HashMap<>();
        for (var courseIdFeedbackSessionList : courseIdToFeedbackSessionsSqlMap.entrySet()) {
            String courseId = courseIdFeedbackSessionList.getKey();
            List<FeedbackSession> feedbackSessions = courseIdFeedbackSessionList.getValue();
            List<Instructor> instructors = sqlLogic.getInstructorsByCourse(courseId);
            String googleId = getRegisteredInstructorGoogleIdFromSqlInstructors(instructors);
            String institute = sqlLogic.getCourse(courseId).getInstitute();
            List<OngoingSession> sessions = feedbackSessions.stream()
                    .map(session -> new OngoingSession(session, googleId))
                    .collect(Collectors.toList());
            instituteToFeedbackSessionsSqlMap.computeIfAbsent(institute, k -> new ArrayList<>()).addAll(sessions);
        }
        return instituteToFeedbackSessionsSqlMap;
    }

    private Map<String, List<OngoingSession>> createInstituteToFeedbackSessionsMap(
            Map<String, List<FeedbackSessionAttributes>> courseIdToFeedbackSessionsMap) {
        Map<String, List<OngoingSession>> instituteToFeedbackSessionsMap = new HashMap<>();
        for (var courseIdFeedbackSessionList : courseIdToFeedbackSessionsMap.entrySet()) {
            String courseId = courseIdFeedbackSessionList.getKey();
            List<FeedbackSessionAttributes> feedbackSessions = courseIdFeedbackSessionList.getValue();
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
            String googleId = getRegisteredInstructorGoogleIdFromInstructors(instructors);

            String institute = logic.getCourseInstitute(courseId);
            List<OngoingSession> sessions = feedbackSessions.stream()
                    .map(session -> new OngoingSession(session, googleId))
                    .collect(Collectors.toList());

            instituteToFeedbackSessionsMap.computeIfAbsent(institute, k -> new ArrayList<>()).addAll(sessions);
        }
        return instituteToFeedbackSessionsMap;
    }

    private OngoingSessionsData createOutput(Map<String, List<FeedbackSession>> courseIdToFeedbackSessionsSqlMap,
            Map<String, List<FeedbackSessionAttributes>> courseIdToFeedbackSessionsMap,
            Map<String, List<OngoingSession>> instituteToFeedbackSessionsMap) {
        int totalOngoingSessions = 0;
        int totalOpenSessions = 0;
        int totalClosedSessions = 0;
        int totalAwaitingSessions = 0;
        for (List<FeedbackSession> feedbackSessions : courseIdToFeedbackSessionsSqlMap.values()) {
            totalOngoingSessions += feedbackSessions.size();
            for (FeedbackSession fs : feedbackSessions) {
                if (fs.isOpened()) {
                    totalOpenSessions++;
                }
                if (fs.isClosed()) {
                    totalClosedSessions++;
                }
                if (fs.isWaitingToOpen()) {
                    totalAwaitingSessions++;
                }
            }
        }
        for (List<FeedbackSessionAttributes> feedbackSessions : courseIdToFeedbackSessionsMap.values()) {
            totalOngoingSessions += feedbackSessions.size();
            for (FeedbackSessionAttributes fs : feedbackSessions) {
                if (fs.isOpened()) {
                    totalOpenSessions++;
                }
                if (fs.isClosed()) {
                    totalClosedSessions++;
                }
                if (fs.isWaitingToOpen()) {
                    totalAwaitingSessions++;
                }
            }
        }
        long totalInstitutes = instituteToFeedbackSessionsMap.keySet().stream()
                .filter(key -> !Const.UNKNOWN_INSTITUTION.equals(key))
                .count();
        OngoingSessionsData output = new OngoingSessionsData();
        output.setTotalOngoingSessions(totalOngoingSessions);
        output.setTotalOpenSessions(totalOpenSessions);
        output.setTotalClosedSessions(totalClosedSessions);
        output.setTotalAwaitingSessions(totalAwaitingSessions);
        output.setTotalInstitutes(totalInstitutes);
        output.setSessions(instituteToFeedbackSessionsMap);
        return output;
    }

    private String getRegisteredInstructorGoogleIdFromSqlInstructors(List<Instructor> sqlInstructors) {
        for (Instructor sqlInstructor : sqlInstructors) {
            if (sqlInstructor.isRegistered()) {
                return sqlInstructor.getGoogleId();
            }
        }
        // There may be an instructor who was actually registered, but their account has not been migrated yet.
        // Thus, we must check the instructor entities of the course on datastore, if any.
        assert !sqlInstructors.isEmpty();
        String courseId = sqlInstructors.get(0).getCourseId();
        // If the course only exists in SQL, then the instructors should only be in SQL as well, so we can just return.
        if (logic.getCourse(courseId) == null) {
            return null;
        }
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isRegistered()) {
                return instructor.getGoogleId();
            }
        }
        return null;
    }

    private String getRegisteredInstructorGoogleIdFromInstructors(List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isRegistered()) {
                return instructor.getGoogleId();
            }
        }
        return null;
    }
}
