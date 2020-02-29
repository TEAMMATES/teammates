package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.SearchSessionsData;
import teammates.ui.webapi.output.StudentSessionsData;

/**
 * Searches for sessions.
 */
public class SearchSessionsAction extends Action {

    private static final String OPEN_CLOSE_DATES_SESSION_TEMPLATE = "[%s - %s]";

    private Set<String> courseIds = new HashSet<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToOpenFeedbackSessionsMap = new HashMap<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToNotOpenFeedbackSessionsMap = new HashMap<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToPublishedFeedbackSessionsMap = new HashMap<>();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can search for sessions
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        List<StudentAttributes> students = logic.searchStudentsInWholeSystem(searchKey).studentList;

        populateCourseIds(students);
        populateCourseIdToFeedbackSessionsMap();

        Map<String, StudentSessionsData> sessionsData = getSessionsData(students);
        SearchSessionsData result = new SearchSessionsData(sessionsData);
        return new JsonResult(result);
    }

    private void populateCourseIds(List<StudentAttributes> students) {
        for (StudentAttributes student : students) {
            if (student.course != null) {
                courseIds.add(student.course);
            }
        }
    }

    private void populateCourseIdToFeedbackSessionsMap() {
        for (String courseId : courseIds) {
            List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
            for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
                if (feedbackSession.isOpened()) {
                    courseIdToOpenFeedbackSessionsMap.computeIfAbsent(courseId, k -> new ArrayList<>())
                            .add(feedbackSession);
                } else {
                    courseIdToNotOpenFeedbackSessionsMap.computeIfAbsent(courseId, k -> new ArrayList<>())
                            .add(feedbackSession);
                }
                if (feedbackSession.isPublished()) {
                    courseIdToPublishedFeedbackSessionsMap.computeIfAbsent(courseId, k -> new ArrayList<>())
                            .add(feedbackSession);
                }
            }
        }
    }

    private Map<String, StudentSessionsData> getSessionsData(List<StudentAttributes> students) {
        Map<String, StudentSessionsData> sessionsData = new HashMap<>();

        for (StudentAttributes student : students) {
            StudentSessionsData studentSessionsData = new StudentSessionsData();

            if (student.email != null && student.course != null) {
                // Open sessions
                if (courseIdToOpenFeedbackSessionsMap.get(student.course) != null) {
                    for (FeedbackSessionAttributes openFs : courseIdToOpenFeedbackSessionsMap.get(student.course)) {
                        studentSessionsData.addOpenSession(generateNameFragment(openFs),
                                generateSubmitUrl(student, openFs.getFeedbackSessionName()));
                    }
                }

                // Closed sessions
                if (courseIdToNotOpenFeedbackSessionsMap.get(student.course) != null) {
                    for (FeedbackSessionAttributes notOpenFs : courseIdToNotOpenFeedbackSessionsMap
                            .get(student.course)) {
                        studentSessionsData.addClosedSession(generateNameFragment(notOpenFs) + " (Currently Not Open)",
                                generateSubmitUrl(student, notOpenFs.getFeedbackSessionName()));
                    }
                }

                // Published sessions
                if (courseIdToPublishedFeedbackSessionsMap.get(student.course) != null) {
                    for (FeedbackSessionAttributes publishedFs : courseIdToPublishedFeedbackSessionsMap
                            .get(student.course)) {
                        studentSessionsData.addPublishedSession(generateNameFragment(publishedFs) + " (Published)",
                                generateResultUrl(student, publishedFs.getFeedbackSessionName()));
                    }
                }
            }

            sessionsData.put(student.getEmail(), studentSessionsData);
        }

        return sessionsData;
    }

    private String generateNameFragment(FeedbackSessionAttributes feedbackSession) {
        String openCloseDateFragment = String.format(OPEN_CLOSE_DATES_SESSION_TEMPLATE,
                feedbackSession.getStartTimeString(), feedbackSession.getEndTimeString());
        return feedbackSession.getFeedbackSessionName() + " " + openCloseDateFragment;
    }

    private String generateSubmitUrl(StudentAttributes student, String fsName) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE).withCourseId(student.course)
                .withSessionName(fsName).withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email).toAbsoluteString();
    }

    private String generateResultUrl(StudentAttributes student, String fsName) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE).withCourseId(student.course)
                .withSessionName(fsName).withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email).toAbsoluteString();
    }

}
