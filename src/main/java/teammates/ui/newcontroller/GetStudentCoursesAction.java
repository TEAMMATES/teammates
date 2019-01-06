package teammates.ui.newcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: gets the courses and feedback sessions which a student is enrolled in.
 */
public class GetStudentCoursesAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {

        String recentlyJoinedCourseId = getNonNullRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);
        boolean hasEventualConsistencyMsg = false;

        List<CourseDetailsBundle> courses = new ArrayList<>();
        Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap = new HashMap<>();

        try {
            courses = logic.getCourseDetailsListForStudent(userInfo.id);
            sessionSubmissionStatusMap = generateFeedbackSessionSubmissionStatusMap(courses);

            CourseDetailsBundle.sortDetailedCoursesByCourseId(courses);

            boolean isDataConsistent = isCourseIncluded(recentlyJoinedCourseId, courses);
            if (!isDataConsistent) {
                hasEventualConsistencyMsg = addPlaceholderCourse(courses, recentlyJoinedCourseId,
                        sessionSubmissionStatusMap);
            }

            for (CourseDetailsBundle course : courses) {
                FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
            }

        } catch (EntityDoesNotExistException e) {
            if (recentlyJoinedCourseId == null) {
                return new JsonResult("Your Google account is not known to TEAMMATES", HttpStatus.SC_NOT_FOUND);
            } else {
                hasEventualConsistencyMsg = addPlaceholderCourse(courses, recentlyJoinedCourseId,
                        sessionSubmissionStatusMap);
            }
        }

        StudentCourses data = new StudentCourses(recentlyJoinedCourseId, hasEventualConsistencyMsg, courses,
                sessionSubmissionStatusMap);

        return new JsonResult(data);
    }

    /**
     * Output format for {@link GetStudentCoursesAction}.
     */
    public static class StudentCourses extends ActionResult.ActionOutput {

        private final String recentlyJoinedCourseId;
        private final boolean hasEventualConsistencyMsg;
        private final List<CourseDetailsBundle> courses;
        private final Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap;

        public StudentCourses(String recentlyJoinedCourseId,
                              boolean hasEventualConsistencyMsg,
                              List<CourseDetailsBundle> courses,
                              Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap) {
            this.recentlyJoinedCourseId = recentlyJoinedCourseId;
            this.hasEventualConsistencyMsg = hasEventualConsistencyMsg;
            this.courses = courses;
            this.sessionSubmissionStatusMap = sessionSubmissionStatusMap;
        }

        public String getRecentlyJoinedCourseId() {
            return recentlyJoinedCourseId;
        }

        public boolean getHasEventualConsistencyMsg() {
            return hasEventualConsistencyMsg;
        }

        public List<CourseDetailsBundle> getCourses() {
            return courses;
        }

        public Map<FeedbackSessionAttributes, Boolean> getSessionSubmissionStatusMap() {
            return sessionSubmissionStatusMap;
        }

    }

    private Map<FeedbackSessionAttributes, Boolean> generateFeedbackSessionSubmissionStatusMap(
            List<CourseDetailsBundle> courses) {
        Map<FeedbackSessionAttributes, Boolean> returnValue = new HashMap<>();

        for (CourseDetailsBundle c : courses) {
            for (FeedbackSessionDetailsBundle fsb : c.feedbackSessions) {
                FeedbackSessionAttributes f = fsb.feedbackSession;
                returnValue.put(f, getStudentStatusForSession(f));
            }
        }
        return returnValue;
    }

    private boolean getStudentStatusForSession(FeedbackSessionAttributes fs) {
        StudentAttributes student = logic.getStudentForGoogleId(fs.getCourseId(), userInfo.id);

        String studentEmail = student.email;

        return logic.hasStudentSubmittedFeedback(fs, studentEmail);
    }

    private boolean isCourseIncluded(String recentlyJoinedCourseId, List<CourseDetailsBundle> courses) {
        boolean isCourseIncluded = false;

        if (recentlyJoinedCourseId == null) {
            isCourseIncluded = true;
        } else {
            for (CourseDetailsBundle currentCourse : courses) {
                if (currentCourse.course.getId().equals(recentlyJoinedCourseId)) {
                    isCourseIncluded = true;
                }
            }
        }

        return isCourseIncluded;
    }

    /**
     * Adds a placeholder course for a recently joined course and indicates whether an eventual consistency
     * message needs to be shown to the user.
     */
    private boolean addPlaceholderCourse(List<CourseDetailsBundle> courses, String courseId,
                                      Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap) {
        try {
            CourseDetailsBundle course = logic.getCourseDetails(courseId);
            courses.add(course);

            addPlaceholderFeedbackSessions(course, sessionSubmissionStatusMap);
            FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);

            return false;

        } catch (EntityDoesNotExistException e) {
            return true;
        }
    }

    private void addPlaceholderFeedbackSessions(CourseDetailsBundle course,
                                                Map<FeedbackSessionAttributes, Boolean> sessionSubmissionStatusMap) {
        for (FeedbackSessionDetailsBundle fsb : course.feedbackSessions) {
            sessionSubmissionStatusMap.put(fsb.feedbackSession, true);
        }
    }

}
