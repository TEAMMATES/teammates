package teammates.ui.webapi.action;

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
import teammates.common.util.TimeHelper;
import teammates.ui.webapi.output.ApiOutput;

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

        String recentlyJoinedCourseId = getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);
        boolean hasEventualConsistencyMsg = false;

        List<CourseDetailsBundle> courses = new ArrayList<>();
        Map<String, SessionInfoMap> sessionsInfoMap = new HashMap<>();

        try {
            courses = logic.getCourseDetailsListForStudent(userInfo.id);
            sessionsInfoMap = generateFeedbackSessionsInfoMap(courses);

            CourseDetailsBundle.sortDetailedCoursesByCourseId(courses);

            boolean isDataConsistent = isCourseIncluded(recentlyJoinedCourseId, courses);
            if (!isDataConsistent) {
                hasEventualConsistencyMsg = addPlaceholderCourse(courses, recentlyJoinedCourseId,
                        sessionsInfoMap);
            }

            for (CourseDetailsBundle course : courses) {
                FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
            }

        } catch (EntityDoesNotExistException e) {
            if (recentlyJoinedCourseId == null) {
                return new JsonResult("Ooops! Your Google account is not known to TEAMMATES{*}use the new Gmail address.",
                        HttpStatus.SC_NOT_FOUND);
            } else {
                hasEventualConsistencyMsg = addPlaceholderCourse(courses, recentlyJoinedCourseId,
                        sessionsInfoMap);
            }
        }

        StudentCourses data = new StudentCourses(recentlyJoinedCourseId, hasEventualConsistencyMsg,
                courses, sessionsInfoMap);

        return new JsonResult(data);
    }

    private Map<String, SessionInfoMap> generateFeedbackSessionsInfoMap(
            List<CourseDetailsBundle> courses) {
        Map<String, SessionInfoMap> returnValue = new HashMap<>();

        for (CourseDetailsBundle c : courses) {
            for (FeedbackSessionDetailsBundle fsb : c.feedbackSessions) {
                FeedbackSessionAttributes f = fsb.feedbackSession;
                String fId = f.getCourseId() + '%' + f.getFeedbackSessionName();

                String endTime = TimeHelper.formatDateTimeForDisplay(f.getEndTime(), f.getTimeZone());
                boolean isOpened = f.isOpened();
                boolean isWaitingToOpen = f.isWaitingToOpen();
                boolean isPublished = f.isPublished();
                boolean isSubmitted = getStudentStatusForSession(f);

                returnValue.put(fId, new SessionInfoMap(endTime, isOpened, isWaitingToOpen, isPublished, isSubmitted));
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
                                         Map<String, SessionInfoMap> sessionsInfoMap) {
        try {
            CourseDetailsBundle course = logic.getCourseDetails(courseId);
            courses.add(course);

            addPlaceholderFeedbackSessions(course, sessionsInfoMap);
            FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);

            return false;

        } catch (EntityDoesNotExistException e) {
            return true;
        }
    }

    private void addPlaceholderFeedbackSessions(CourseDetailsBundle course,
                                                Map<String, SessionInfoMap> sessionsInfoMap) {
        for (FeedbackSessionDetailsBundle fsb : course.feedbackSessions) {
            FeedbackSessionAttributes fsbFeedbackSession = fsb.feedbackSession;
            String fsbId = fsbFeedbackSession.getCourseId() + '%' + fsbFeedbackSession.getFeedbackSessionName();

            String endTime = TimeHelper.formatDateTimeForDisplay(fsbFeedbackSession.getEndTime(),
                    fsbFeedbackSession.getTimeZone());
            boolean isOpened = fsbFeedbackSession.isOpened();
            boolean isWaitingToOpen = fsbFeedbackSession.isWaitingToOpen();
            boolean isPublished = fsbFeedbackSession.isPublished();
            boolean isSubmitted = true;
            SessionInfoMap map = new SessionInfoMap(endTime, isOpened, isWaitingToOpen, isPublished, isSubmitted);

            sessionsInfoMap.put(fsbId, map);
        }
    }

    /**
     * Output format for {@link GetStudentCoursesAction}.
     */
    public static class StudentCourses extends ApiOutput {

        private final String recentlyJoinedCourseId;
        private final boolean hasEventualConsistencyMsg;
        private final List<CourseDetailsBundle> courses;
        private final Map<String, SessionInfoMap> sessionsInfoMap;

        public StudentCourses(String recentlyJoinedCourseId,
                              boolean hasEventualConsistencyMsg,
                              List<CourseDetailsBundle> courses,
                              Map<String, SessionInfoMap> sessionsInfoMap) {
            this.recentlyJoinedCourseId = recentlyJoinedCourseId;
            this.hasEventualConsistencyMsg = hasEventualConsistencyMsg;
            this.courses = courses;
            this.sessionsInfoMap = sessionsInfoMap;
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

        public Map<String, SessionInfoMap> getSessionsInfoMap() {
            return sessionsInfoMap;
        }

    }

    private static class SessionInfoMap {
        private String endTime;

        private boolean isOpened;
        private boolean isWaitingToOpen;
        private boolean isPublished;
        private boolean isSubmitted;

        private SessionInfoMap(String endTime, boolean isOpened, boolean isWaitingToOpen,
                              boolean isPublished, boolean isSubmitted) {
            this.endTime = endTime;
            this.isOpened = isOpened;
            this.isWaitingToOpen = isWaitingToOpen;
            this.isPublished = isPublished;
            this.isSubmitted = isSubmitted;
        }

        public String getEndTime() {
            return this.endTime;
        }

        public boolean getIsOpened() {
            return this.isOpened;
        }

        public boolean getIsWaitingToOpen() {
            return this.isWaitingToOpen;
        }

        public boolean getIsPublished() {
            return this.isPublished;
        }

        public boolean getIsSubmitted() {
            return this.isSubmitted;
        }
    }

}
