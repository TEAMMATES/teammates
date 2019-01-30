package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: searches for accounts.
 */
public class SearchAccountsAction extends Action {

    private static final String OPEN_CLOSE_DATES_SESSION_TEMPLATE = "[%s - %s]";

    private Set<String> courseIds = new HashSet<>();
    private Map<String, String> courseIdToCourseNameMap = new HashMap<>();
    private Map<String, String> courseIdToInstituteMap = new HashMap<>();
    private Map<String, String> courseIdToInstructorGoogleIdMap = new HashMap<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToOpenFeedbackSessionsMap = new HashMap<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToNotOpenFeedbackSessionsMap = new HashMap<>();
    private Map<String, List<FeedbackSessionAttributes>> courseIdToPublishedFeedbackSessionsMap = new HashMap<>();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can get accounts directly
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);

        List<StudentAttributes> students = logic.searchStudentsInWholeSystem(searchKey).studentList;
        List<InstructorAttributes> instructors = logic.searchInstructorsInWholeSystem(searchKey).instructorList;

        populateCourseIds(students, instructors);
        populateCourseIdToCourseNameMap();
        populateCourseIdToInstituteMap();
        populateCourseIdToFeedbackSessionsMap();

        List<StudentBundle> studentsBundle = getStudentsBundle(students);
        List<InstructorBundle> instructorsBundle = getInstructorsBundle(instructors);

        AdminAccountSearchResult result = new AdminAccountSearchResult(studentsBundle, instructorsBundle);
        return new JsonResult(result);
    }

    private void populateCourseIds(List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        for (StudentAttributes student : students) {
            if (student.course != null) {
                courseIds.add(student.course);
            }
        }
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId != null) {
                courseIds.add(instructor.courseId);
            }
        }
    }

    private void populateCourseIdToCourseNameMap() {
        for (String courseId : courseIds) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course != null) {
                courseIdToCourseNameMap.put(courseId, course.getName());
            }
        }
    }

    private void populateCourseIdToInstituteMap() {
        for (String courseId : courseIds) {
            String instructorForCourseGoogleId = findAvailableInstructorGoogleIdForCourse(courseId);
            AccountAttributes account = logic.getAccount(instructorForCourseGoogleId);
            if (account == null) {
                continue;
            }

            String institute = StringHelper.isEmpty(account.institute) ? "None" : account.institute;
            courseIdToInstituteMap.put(courseId, institute);
        }
    }

    /**
     * Finds the googleId of a registered instructor with co-owner privileges.
     * If there is no such instructor, finds the googleId of a registered
     * instructor with the privilege to modify instructors.
     *
     * @param courseId
     *            the ID of the course
     * @return the googleId of a suitable instructor if found, otherwise an
     *         empty string
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId) {
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

        for (InstructorAttributes instructor : instructorList) {
            if (instructor.isRegistered()
                    && (instructor.hasCoownerPrivileges()
                    || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR))) {
                courseIdToInstructorGoogleIdMap.put(courseId, instructor.googleId);
                return instructor.googleId;
            }

        }

        return "";
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

    private List<StudentBundle> getStudentsBundle(List<StudentAttributes> students) {
        List<StudentBundle> studentsBundle = new ArrayList<>();
        for (StudentAttributes student : students) {
            StudentBundle sb = new StudentBundle();
            sb.name = student.name;
            sb.email = student.email;
            if (student.googleId != null) {
                sb.googleId = student.googleId;
                sb.manageAccountLink = Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                        .withInstructorId(student.googleId)
                        .toString();
            }
            if (student.course != null) {
                sb.courseId = student.course;
                sb.courseName = courseIdToCourseNameMap.get(student.course);
                sb.institute = courseIdToInstituteMap.get(student.course);
            }
            sb.section = student.section;
            sb.team = student.team;
            sb.comments = student.comments;

            if (sb.googleId != null) {
                sb.homePageLink = Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                        .withUserId(sb.googleId)
                        .toString();
            }
            sb.courseJoinLink = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

            if (student.email != null && student.course != null
                    && !StringHelper.isEmpty(courseIdToInstructorGoogleIdMap.get(student.course))) {
                sb.recordsPageLink = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                        .withCourseId(student.course)
                        .withStudentEmail(student.email)
                        .withUserId(courseIdToInstructorGoogleIdMap.get(student.course))
                        .toAbsoluteString();
            }

            if (student.email != null && student.course != null) {
                for (FeedbackSessionAttributes openFs : courseIdToOpenFeedbackSessionsMap.get(student.course)) {
                    sb.openSessions.put(generateNameFragment(openFs),
                            generateSubmitUrl(student, openFs.getFeedbackSessionName()));
                }
                for (FeedbackSessionAttributes notOpenFs : courseIdToNotOpenFeedbackSessionsMap.get(student.course)) {
                    sb.notOpenSessions.put(generateNameFragment(notOpenFs) + " (Currently Not Open)",
                            generateSubmitUrl(student, notOpenFs.getFeedbackSessionName()));
                }
                for (FeedbackSessionAttributes publishedFs : courseIdToPublishedFeedbackSessionsMap.get(student.course)) {
                    sb.publishedSessions.put(generateNameFragment(publishedFs) + " (Published)",
                            generateResultUrl(student, publishedFs.getFeedbackSessionName()));
                }
            }

            studentsBundle.add(sb);
        }

        return studentsBundle;
    }

    private String generateNameFragment(FeedbackSessionAttributes feedbackSession) {
        String openCloseDateFragment = String.format(OPEN_CLOSE_DATES_SESSION_TEMPLATE,
                feedbackSession.getStartTimeString(), feedbackSession.getEndTimeString());
        return feedbackSession.getFeedbackSessionName() + " " + openCloseDateFragment;
    }

    private String generateSubmitUrl(StudentAttributes student, String fsName) {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(student.course)
                .withSessionName(fsName)
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();
    }

    private String generateResultUrl(StudentAttributes student, String fsName) {
        return Config.getFrontEndAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withCourseId(student.course)
                .withSessionName(fsName)
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();
    }

    private List<InstructorBundle> getInstructorsBundle(List<InstructorAttributes> instructors) {
        List<InstructorBundle> instructorsBundle = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            InstructorBundle ib = new InstructorBundle();
            ib.name = instructor.name;
            ib.email = instructor.email;
            if (instructor.googleId != null) {
                ib.googleId = instructor.googleId;
                ib.manageAccountLink = Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                        .withInstructorId(instructor.googleId)
                        .toString();
            }
            if (instructor.courseId != null) {
                ib.courseId = instructor.courseId;
                ib.courseName = courseIdToCourseNameMap.get(instructor.courseId);
                ib.institute = courseIdToInstituteMap.get(instructor.courseId);
            }

            if (ib.googleId != null) {
                ib.homePageLink = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                        .withUserId(instructor.googleId)
                        .toAbsoluteString();
            }
            ib.courseJoinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(StringHelper.encrypt(instructor.key))
                    .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                    .toAbsoluteString();

            instructorsBundle.add(ib);
        }

        return instructorsBundle;
    }

    private static class StudentBundle extends CommonBundle {
        private String section;
        private String team;
        private String comments;

        private String recordsPageLink;

        private Map<String, String> openSessions = new HashMap<>();
        private Map<String, String> notOpenSessions = new HashMap<>();
        private Map<String, String> publishedSessions = new HashMap<>();

        public String getSection() {
            return section;
        }

        public String getTeam() {
            return team;
        }

        public String getComments() {
            return comments;
        }

        public String getRecordsPageLink() {
            return recordsPageLink;
        }

        public Map<String, String> getOpenSessions() {
            return openSessions;
        }

        public Map<String, String> getNotOpenSessions() {
            return notOpenSessions;
        }

        public Map<String, String> getPublishedSessions() {
            return publishedSessions;
        }
    }

    private static class InstructorBundle extends CommonBundle {}

    private static class CommonBundle {
        protected String name;
        protected String email;
        protected String googleId;
        protected String courseId;
        protected String courseName;
        protected String institute;

        protected String courseJoinLink;
        protected String homePageLink;
        protected String manageAccountLink;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getGoogleId() {
            return googleId;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getInstitute() {
            return institute;
        }

        public String getCourseJoinLink() {
            return courseJoinLink;
        }

        public String getHomePageLink() {
            return homePageLink;
        }

        public String getManageAccountLink() {
            return manageAccountLink;
        }
    }

    /**
     * Output format for {@link SearchAccountsAction}.
     */
    public static class AdminAccountSearchResult extends ApiOutput {

        private final List<StudentBundle> students;
        private final List<InstructorBundle> instructors;

        public AdminAccountSearchResult(List<StudentBundle> students, List<InstructorBundle> instructors) {
            this.students = students;
            this.instructors = instructors;
        }

        public List<StudentBundle> getStudents() {
            return students;
        }

        public List<InstructorBundle> getInstructors() {
            return instructors;
        }

    }

}
