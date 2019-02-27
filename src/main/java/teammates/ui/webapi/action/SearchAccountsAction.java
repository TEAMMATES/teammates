package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.InstructorAccountSearchResult;
import teammates.common.datatransfer.StudentAccountSearchResult;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.AdminSearchResultData;


/**
 * Searches for accounts.
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

        List<StudentAccountSearchResult> studentsBundle = getStudentsBundle(students);
        List<InstructorAccountSearchResult> instructorsBundle = getInstructorsBundle(instructors);

        AdminSearchResultData result = new AdminSearchResultData(studentsBundle, instructorsBundle);
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

    private List<StudentAccountSearchResult> getStudentsBundle(List<StudentAttributes> students) {
        List<StudentAccountSearchResult> studentsBundle = new ArrayList<>();
        for (StudentAttributes student : students) {
            StudentAccountSearchResult sb = new StudentAccountSearchResult();
            sb.setName(student.name);
            sb.setEmail(student.email);
            if (student.googleId != null) {
                sb.setGoogleId(student.googleId);
                sb.setManageAccountLink(Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                        .withInstructorId(student.googleId)
                        .toString());
            }
            if (student.course != null) {
                sb.setCourseId(student.course);
                sb.setCourseName(courseIdToCourseNameMap.get(student.course));
                sb.setInstitute(courseIdToInstituteMap.get(student.course));
            }
            sb.setSection(student.section);
            sb.setTeam(student.team);
            sb.setComments(student.comments);

            if (sb.getGoogleId() != null) {
                sb.setHomePageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                        .withUserId(sb.getGoogleId())
                        .toString());
            }
            sb.setCourseJoinLink(Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString());

            if (student.email != null && student.course != null
                    && !StringHelper.isEmpty(courseIdToInstructorGoogleIdMap.get(student.course))) {
                sb.setRecordsPageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                        .withCourseId(student.course)
                        .withStudentEmail(student.email)
                        .withUserId(courseIdToInstructorGoogleIdMap.get(student.course))
                        .toAbsoluteString());
            }

            if (student.email != null && student.course != null) {

                if (courseIdToOpenFeedbackSessionsMap.get(student.course) != null) {
                    Map<String, String> openSessions = sb.getOpenSessions();
                    for (FeedbackSessionAttributes openFs : courseIdToOpenFeedbackSessionsMap.get(student.course)) {
                        openSessions.put(generateNameFragment(openFs),
                                generateSubmitUrl(student, openFs.getFeedbackSessionName()));
                    }
                    sb.setOpenSessions(openSessions);
                }

                if (courseIdToNotOpenFeedbackSessionsMap.get(student.course) != null) {
                    Map<String, String> notOpenSessions = sb.getNotOpenSessions();
                    for (FeedbackSessionAttributes notOpenFs : courseIdToNotOpenFeedbackSessionsMap.get(student.course)) {
                        notOpenSessions.put(generateNameFragment(notOpenFs) + " (Currently Not Open)",
                                generateSubmitUrl(student, notOpenFs.getFeedbackSessionName()));
                    }
                    sb.setNotOpenSessions(notOpenSessions);
                }

                if (courseIdToPublishedFeedbackSessionsMap.get(student.course) != null) {
                    Map<String, String> publishedSessions = sb.getPublishedSessions();
                    for (FeedbackSessionAttributes publishedFs
                            : courseIdToPublishedFeedbackSessionsMap.get(student.course)) {
                        publishedSessions.put(generateNameFragment(publishedFs) + " (Published)",
                                generateResultUrl(student, publishedFs.getFeedbackSessionName()));
                    }
                    sb.setPublishedSessions(publishedSessions);
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
        return Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(student.course)
                .withSessionName(fsName)
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();
    }

    private List<InstructorAccountSearchResult> getInstructorsBundle(List<InstructorAttributes> instructors) {
        List<InstructorAccountSearchResult> instructorsBundle = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            InstructorAccountSearchResult ib = new InstructorAccountSearchResult();
            ib.setName(instructor.name);
            ib.setEmail(instructor.email);

            if (instructor.googleId != null) {
                ib.setGoogleId(instructor.googleId);
                ib.setManageAccountLink(Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                        .withInstructorId(instructor.googleId)
                        .toString());
            }
            if (instructor.courseId != null) {
                ib.setCourseId(instructor.courseId);
                ib.setCourseName(courseIdToCourseNameMap.get(instructor.courseId));
                ib.setInstitute(courseIdToInstituteMap.get(instructor.courseId));
            }

            if (ib.getGoogleId() != null) {
                ib.setHomePageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                        .withUserId(instructor.googleId)
                        .toAbsoluteString());
            }
            ib.setCourseJoinLink(Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(StringHelper.encrypt(instructor.key))
                    .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                    .toAbsoluteString());

            instructorsBundle.add(ib);
        }

        return instructorsBundle;
    }

}
