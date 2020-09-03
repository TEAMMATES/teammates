package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * A single ongoing session.
 */
public class OngoingSession {

    private final String sessionStatus;
    private final String instructorHomePageLink;
    private final long startTime;
    private final long endTime;
    private final String creatorEmail;
    private final String courseId;
    private final String feedbackSessionName;

    public OngoingSession(FeedbackSessionAttributes fs, AccountAttributes account) {
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

    /**
     * Gets the status for a feedback session to be displayed to the user.
     */
    private String getSessionStatusForShow(FeedbackSessionAttributes fs) {
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
