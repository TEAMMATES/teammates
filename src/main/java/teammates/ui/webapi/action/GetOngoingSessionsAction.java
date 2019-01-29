package teammates.ui.webapi.action;

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
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets the list of all ongoing sessions.
 */
public class GetOngoingSessionsAction extends Action {

    private static final String UNKNOWN_INSTITUTION = "Unknown Institution";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can get the list of all ongoing sessions
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public ActionResult execute() {
        String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME);
        long startTime;
        try {
            startTime = Long.parseLong(startTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid startTime parameter");
        }

        String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME);
        long endTime;
        try {
            endTime = Long.parseLong(endTimeString);
        } catch (NumberFormatException e) {
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
        output.totalOngoingSessions = totalOngoingSessions;
        output.totalOpenSessions = totalOpenSessions;
        output.totalClosedSessions = totalClosedSessions;
        output.totalAwaitingSessions = totalAwaitingSessions;
        output.totalInstitutes = totalInstitutes;
        output.sessions = instituteToFeedbackSessionsMap;

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

    private static class OngoingSession {

        private final String sessionStatus;
        private final String instructorHomePageLink;
        private final long startTime;
        private final long endTime;
        private final String creatorEmail;
        private final String courseId;
        private final String feedbackSessionName;

        OngoingSession(FeedbackSessionAttributes fs, AccountAttributes account) {
            this.sessionStatus = getSessionStatusForShow(fs);

            String instructorHomePageLink = "";
            if (account != null) {
                instructorHomePageLink = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                        .withUserId(account.googleId)
                        .toString();
            }
            this.instructorHomePageLink = instructorHomePageLink;

            this.startTime = fs.getStartTime().toEpochMilli();
            this.endTime = fs.getEndTime().toEpochMilli();
            this.creatorEmail = fs.getCreatorEmail();
            this.courseId = fs.getCourseId();
            this.feedbackSessionName = fs.getFeedbackSessionName();
        }

        public String getSessionStatusForShow(FeedbackSessionAttributes fs) {
            List<String> status = new ArrayList<>();

            if (fs.isClosed()) {
                status.add("[Closed]");
            }
            if (fs.isOpened()) {
                status.add("[Opened]");
            }
            if (fs.isWaitingToOpen()) {
                status.add("[Waiting To Open]");
            }
            if (fs.isPublished()) {
                status.add("[Published]");
            }
            if (fs.isInGracePeriod()) {
                status.add("[Grace Period]");
            }

            return status.isEmpty() ? "No Status" : String.join(" ", status);
        }

        public String getSessionStatus() {
            return sessionStatus;
        }

        public String getInstructorHomePageLink() {
            return instructorHomePageLink;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public String getCreatorEmail() {
            return creatorEmail;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getFeedbackSessionName() {
            return feedbackSessionName;
        }

    }

    /**
     * Output format for {@link GetOngoingSessionsAction}.
     */
    public static class OngoingSessionsData extends ApiOutput {

        private int totalOngoingSessions;
        private int totalOpenSessions;
        private int totalClosedSessions;
        private int totalAwaitingSessions;
        private long totalInstitutes;
        private Map<String, List<OngoingSession>> sessions;

        public int getTotalOngoingSessions() {
            return totalOngoingSessions;
        }

        public int getTotalOpenSessions() {
            return totalOpenSessions;
        }

        public int getTotalClosedSessions() {
            return totalClosedSessions;
        }

        public int getTotalAwaitingSessions() {
            return totalAwaitingSessions;
        }

        public long getTotalInstitutes() {
            return totalInstitutes;
        }

        public Map<String, List<OngoingSession>> getSessions() {
            return sessions;
        }

    }

}
