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
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.OngoingSession;
import teammates.ui.output.OngoingSessionsData;

/**
 * Gets the list of all ongoing sessions.
 */
class GetOngoingSessionsAction extends AdminOnlyAction {

    private static final String UNKNOWN_INSTITUTION = "Unknown Institution";

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    JsonResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME);
        long startTime;
        try {
            startTime = Long.parseLong(startTimeString);
            //test for bounds
            Instant.ofEpochMilli(startTime).minus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW).toEpochMilli();
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidHttpParameterException("Invalid startTime parameter");
        }

        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME);
        long endTime;
        try {
            endTime = Long.parseLong(endTimeString);
            //test for bounds
            Instant.ofEpochMilli(endTime).plus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW).toEpochMilli();
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidHttpParameterException("Invalid endTime parameter");
        }

        if (startTime > endTime) {
            throw new InvalidHttpParameterException("The filter range is not valid. End time should be after start time.");
        }

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

            String institute = account == null ? UNKNOWN_INSTITUTION : account.institute;
            List<OngoingSession> sessions = courseIdToFeedbackSessionsMap.get(courseId).stream()
                    .map(session -> new OngoingSession(session, account))
                    .collect(Collectors.toList());

            instituteToFeedbackSessionsMap.computeIfAbsent(institute, k -> new ArrayList<>()).addAll(sessions);
        }

        long totalInstitutes = instituteToFeedbackSessionsMap.keySet().stream()
                .filter(key -> !key.equals(UNKNOWN_INSTITUTION))
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
                return logic.getAccount(instructor.googleId);
            }
        }
        return null;
    }
}
