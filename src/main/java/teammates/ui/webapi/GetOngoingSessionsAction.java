package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.OngoingSession;
import teammates.ui.output.OngoingSessionsData;

/**
 * Gets the list of all ongoing sessions.
 */
class GetOngoingSessionsAction extends AdminOnlyAction {

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

        List<FeedbackSessionAttributes> allOngoingSessions =
                logic.getAllOngoingSessions(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime));

        int totalOngoingSessions = allOngoingSessions.size();
        int totalOpenSessions = 0;
        int totalClosedSessions = 0;
        int totalAwaitingSessions = 0;

        Set<String> courseIds = new HashSet<>();
        Map<String, List<FeedbackSessionAttributes>> courseIdToFeedbackSessionsMap = new HashMap<>();
        for (FeedbackSessionAttributes fs : allOngoingSessions) {
            if (fs.isOpened()) {
                totalOpenSessions++;
            }
            if (fs.isClosed()) {
                totalClosedSessions++;
            }
            if (fs.isWaitingToOpen()) {
                totalAwaitingSessions++;
            }

            String courseId = fs.getCourseId();
            courseIds.add(courseId);
            courseIdToFeedbackSessionsMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(fs);
        }

        Map<String, List<OngoingSession>> instituteToFeedbackSessionsMap = new HashMap<>();
        for (String courseId : courseIds) {
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
            AccountAttributes account = getRegisteredInstructorAccountFromInstructors(instructors);

            String institute = logic.getCourseInstitute(courseId);
            List<OngoingSession> sessions = courseIdToFeedbackSessionsMap.get(courseId).stream()
                    .map(session -> new OngoingSession(session, account))
                    .collect(Collectors.toList());

            instituteToFeedbackSessionsMap.computeIfAbsent(institute, k -> new ArrayList<>()).addAll(sessions);
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

        return new JsonResult(output);
    }

    private AccountAttributes getRegisteredInstructorAccountFromInstructors(List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isRegistered()) {
                return logic.getAccount(instructor.getGoogleId());
            }
        }
        return null;
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
}
