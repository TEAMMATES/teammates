package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;
import teammates.ui.template.HomeFeedbackSessionRow;
import teammates.ui.template.StudentFeedbackSessionActions;
import teammates.ui.template.StudentHomeFeedbackSessionRow;

public class StudentHomePageData extends PageData {

    private List<CourseTable> courseTables;

    public StudentHomePageData(AccountAttributes account, String sessionToken,
                               List<CourseDetailsBundle> courses,
                               Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap) {
        super(account, sessionToken);
        setCourseTables(courses, sessionSubmissionStatusMap);
    }

    public List<CourseTable> getCourseTables() {
        return courseTables;
    }

    private void setCourseTables(List<CourseDetailsBundle> courses,
                                 Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap) {
        courseTables = new ArrayList<>();
        int startingSessionIdx = 0; // incremented for each session row without resetting between courses
        for (CourseDetailsBundle courseDetails : courses) {
            CourseTable courseTable = new CourseTable(courseDetails.course,
                                                      createCourseTableLinks(courseDetails.course.getId()),
                                                      createSessionRows(courseDetails.feedbackSessions,
                                                                        sessionSubmissionStatusMap,
                                                                        startingSessionIdx));
            startingSessionIdx += courseDetails.feedbackSessions.size();
            courseTables.add(courseTable);
        }
    }

    private List<ElementTag> createCourseTableLinks(String courseId) {
        List<ElementTag> links = new ArrayList<>();
        links.add(new ElementTag("View Team",
                                 "href", getStudentCourseDetailsLink(courseId),
                                 "title", Const.Tooltips.STUDENT_COURSE_DETAILS));
        return links;
    }

    private List<HomeFeedbackSessionRow> createSessionRows(List<FeedbackSessionDetailsBundle> feedbackSessions,
            Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap, int startingSessionIdx) {
        List<HomeFeedbackSessionRow> rows = new ArrayList<>();

        int sessionIdx = startingSessionIdx;
        for (FeedbackSessionDetailsBundle session : feedbackSessions) {
            FeedbackSessionAttributes feedbackSession = session.feedbackSession;
            String sessionName = feedbackSession.getFeedbackSessionName();
            boolean hasSubmitted = sessionSubmissionStatusMap.get(feedbackSession);

            rows.add(new StudentHomeFeedbackSessionRow(
                    PageData.sanitizeForHtml(sessionName),
                    getStudentSubmissionsTooltipForSession(feedbackSession, hasSubmitted),
                    getStudentPublishedTooltipForSession(feedbackSession),
                    getStudentSubmissionStatusForSession(feedbackSession, hasSubmitted),
                    getStudentPublishedStatusForSession(feedbackSession),
                    TimeHelper.formatDateTimeForDisplay(feedbackSession.getEndTime(), feedbackSession.getTimeZone()),
                    feedbackSession.getEndTimeInIso8601UtcFormat(),
                    getStudentFeedbackSessionActions(feedbackSession, hasSubmitted),
                    sessionIdx));

            ++sessionIdx;
        }

        return rows;
    }

    /**
     * Returns the submission status of the student for a given feedback session as a String.
     *
     * @param session The feedback session in question.
     * @param hasSubmitted Whether the student had submitted the session or not.
     */
    private String getStudentSubmissionStatusForSession(FeedbackSessionAttributes session, boolean hasSubmitted) {
        if (session.isOpened()) {
            return hasSubmitted ? "Submitted" : "Pending";
        }

        if (session.isWaitingToOpen()) {
            return "Awaiting";
        }

        return "Closed";
    }

    private String getStudentPublishedStatusForSession(FeedbackSessionAttributes session) {
        if (session.isPublished()) {
            return "Published";
        }

        return "Not Published";
    }

    /**
     * Returns the hover message to explain feedback session submission status.
     *
     * @param session The feedback session in question.
     * @param hasSubmitted Whether the student had submitted the session or not.
     */
    private String getStudentSubmissionsTooltipForSession(FeedbackSessionAttributes session, boolean hasSubmitted) {
        StringBuilder msg = new StringBuilder();

        Boolean isAwaiting = session.isWaitingToOpen();

        if (isAwaiting) {
            msg.append(Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_AWAITING);
        } else if (hasSubmitted) {
            msg.append(Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_SUBMITTED);
        } else {
            msg.append(Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PENDING);
        }
        if (session.isClosed()) {
            msg.append(Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_CLOSED);
        }
        return msg.toString();
    }

    private String getStudentPublishedTooltipForSession(FeedbackSessionAttributes session) {
        if (session.isPublished()) {
            return Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_PUBLISHED;
        } else {
            return Const.Tooltips.STUDENT_FEEDBACK_SESSION_STATUS_NOT_PUBLISHED;
        }
    }

    /**
     * Returns the list of available actions for a specific feedback session.
     *
     * @param fs The feedback session in question.
     * @param hasSubmitted Whether the student had submitted the session or not.
     */
    private StudentFeedbackSessionActions getStudentFeedbackSessionActions(
            FeedbackSessionAttributes fs, boolean hasSubmitted) {
        String resultsLink = getStudentFeedbackResultsLink(fs.getCourseId(), fs.getFeedbackSessionName());
        String responseEditLink = getStudentFeedbackSubmissionEditLink(fs.getCourseId(), fs.getFeedbackSessionName());
        return new StudentFeedbackSessionActions(fs, resultsLink, responseEditLink, hasSubmitted);
    }
}
