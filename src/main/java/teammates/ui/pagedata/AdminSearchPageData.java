package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.ui.template.AdminSearchInstructorRow;
import teammates.ui.template.AdminSearchInstructorTable;
import teammates.ui.template.AdminSearchStudentFeedbackSession;
import teammates.ui.template.AdminSearchStudentLinks;
import teammates.ui.template.AdminSearchStudentRow;
import teammates.ui.template.AdminSearchStudentTable;

public class AdminSearchPageData extends PageData {

    private enum FeedbackSessionState {
        OPEN, CLOSED, PUBLISHED, AWAITING;
    }

    public String searchKey = "";

    /*
     * Data related to searched students
     */
    public StudentSearchResultBundle studentResultBundle = new StudentSearchResultBundle();
    public Map<String, List<String>> studentOpenFeedbackSessionLinksMap = new HashMap<>();
    public Map<String, List<String>> studentUnOpenedFeedbackSessionLinksMap = new HashMap<>();
    public Map<String, List<String>> studentPublishedFeedbackSessionLinksMap = new HashMap<>();
    public Map<String, String> feedbackSessionLinkToNameMap = new HashMap<>();
    public Map<String, String> studentIdToHomePageLinkMap = new HashMap<>();
    public Map<String, String> studentRecordsPageLinkMap = new HashMap<>();
    public Map<String, String> studentInstituteMap = new HashMap<>();

    /*
     * Data related to searched instructors
     */
    public InstructorSearchResultBundle instructorResultBundle = new InstructorSearchResultBundle();
    public Map<String, String> instructorInstituteMap = new HashMap<>();
    public Map<String, String> instructorHomePageLinkMap = new HashMap<>();
    public Map<String, String> instructorCourseJoinLinkMap = new HashMap<>();

    /*
     * Data related to both instructors and students
     */
    public Map<String, String> courseIdToCourseNameMap = new HashMap<>();

    /*
     * Search result tables
     */
    private AdminSearchInstructorTable instructorTable;
    private AdminSearchStudentTable studentTable;

    public AdminSearchPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init() {
        instructorTable = createInstructorTable();
        studentTable = createStudentTable();
    }

    public String getSearchKey() {
        return searchKey;
    }

    public AdminSearchInstructorTable getInstructorTable() {
        return instructorTable;
    }

    public AdminSearchStudentTable getStudentTable() {
        return studentTable;
    }

    public List<InstructorAttributes> getInstructorResultList() {
        return instructorResultBundle.instructorList;
    }

    public List<StudentAttributes> getStudentResultList() {
        return studentResultBundle.studentList;
    }

    private AdminSearchInstructorTable createInstructorTable() {
        List<AdminSearchInstructorRow> rows = new ArrayList<>();

        for (InstructorAttributes instructor : instructorResultBundle.instructorList) {
            rows.add(createInstructorRow(instructor));
        }

        return new AdminSearchInstructorTable(rows);
    }

    private AdminSearchInstructorRow createInstructorRow(InstructorAttributes instructor) {
        String id = createId(instructor);
        String name = instructor.name;
        String courseName = courseIdToCourseNameMap.get(instructor.courseId);
        String courseId = instructor.courseId;
        String googleId = instructor.googleId;
        String googleIdLink = instructorHomePageLinkMap.get(instructor.googleId);
        String institute = instructorInstituteMap.get(instructor.getIdentificationString());
        String viewRecentActionsId = createViewRecentActionsId(instructor);
        String email = instructor.email;
        String courseJoinLink = instructorCourseJoinLinkMap.get(instructor.getIdentificationString());

        return new AdminSearchInstructorRow(id, name, courseName, courseId, googleId, googleIdLink,
                                            institute, viewRecentActionsId, email, courseJoinLink);
    }

    /**
     * Generates the id of the row for the {@code instructor}.
     * Made public for testing purposes.
     */
    public static String createId(InstructorAttributes instructor) {
        String id = SanitizationHelper.sanitizeForSearch(instructor.getIdentificationString());
        id = StringHelper.removeExtraSpace(id);
        id = id.replace(" ", "").replace("@", "");

        return "instructor_" + id;
    }

    private String createViewRecentActionsId(InstructorAttributes instructor) {
        String availableIdString = "";

        boolean isSearchingUsingGoogleId = instructor.googleId != null && !instructor.googleId.trim().isEmpty();
        boolean isSearchingUsingName = instructor.name != null && !instructor.name.trim().isEmpty();
        boolean isSearchingUsingEmail = instructor.email != null && !instructor.email.trim().isEmpty();
        if (isSearchingUsingGoogleId) {
            availableIdString = "person:" + instructor.googleId;
        } else if (isSearchingUsingName) {
            availableIdString = "person:" + instructor.name;
        } else if (isSearchingUsingEmail) {
            availableIdString = "person:" + instructor.email;
        }

        return availableIdString;
    }

    private AdminSearchStudentTable createStudentTable() {
        List<AdminSearchStudentRow> rows = new ArrayList<>();

        for (StudentAttributes student : studentResultBundle.studentList) {
            rows.add(createStudentRow(student));
        }

        return new AdminSearchStudentTable(rows);
    }

    private AdminSearchStudentRow createStudentRow(StudentAttributes student) {
        String id = createId(student);
        String name = student.name;
        String institute = studentInstituteMap.get(student.getIdentificationString());
        String courseName = courseIdToCourseNameMap.get(student.course);
        String courseId = student.course;
        String section = student.section;
        String team = student.team;
        String googleId = student.googleId;
        String email = student.email;
        String comments = student.comments;
        String viewRecentActionsId = createViewRecentActionsId(student);

        AdminSearchStudentLinks links = createStudentLinks(student);

        List<AdminSearchStudentFeedbackSession> openFeedbackSessions =
                                        createFeedbackSessionsList(student, FeedbackSessionState.OPEN);
        List<AdminSearchStudentFeedbackSession> closedFeedbackSessions =
                                        createFeedbackSessionsList(student, FeedbackSessionState.CLOSED);
        List<AdminSearchStudentFeedbackSession> publishedFeedbackSessions =
                                        createFeedbackSessionsList(student, FeedbackSessionState.PUBLISHED);

        return new AdminSearchStudentRow(id, name, institute, courseName, courseId, section,
                                         team, googleId, email, comments, viewRecentActionsId,
                                         links, openFeedbackSessions, closedFeedbackSessions,
                                         publishedFeedbackSessions);
    }

    /**
     * Generates the id of the row for the {@code student}.
     * Made public for testing purposes.
     */
    public static String createId(StudentAttributes student) {
        String id = SanitizationHelper.sanitizeForSearch(student.getIdentificationString());
        id = id.replace(" ", "").replace("@", "");
        return "student_" + id;
    }

    private String createViewRecentActionsId(StudentAttributes student) {
        String availableIdString = "";

        boolean isSearchingUsingGoogleId = student.googleId != null && !student.googleId.trim().isEmpty();
        boolean isSearchingUsingName = student.name != null && !student.name.trim().isEmpty();
        boolean isSearchingUsingEmail = student.email != null && !student.email.trim().isEmpty();
        if (isSearchingUsingGoogleId) {
            availableIdString = "person:" + student.googleId;
        } else if (isSearchingUsingName) {
            availableIdString = "person:" + student.name;
        } else if (isSearchingUsingEmail) {
            availableIdString = "person:" + student.email;
        }

        return availableIdString;
    }

    private AdminSearchStudentLinks createStudentLinks(StudentAttributes student) {
        String detailsPageLink = studentRecordsPageLinkMap.get(student.getIdentificationString());
        String homePageLink = studentIdToHomePageLinkMap.get(student.googleId);
        String courseJoinLink = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        return new AdminSearchStudentLinks(detailsPageLink, homePageLink, courseJoinLink);
    }

    private List<AdminSearchStudentFeedbackSession> createFeedbackSessionsList(
                                    StudentAttributes student, FeedbackSessionState fsState) {

        List<AdminSearchStudentFeedbackSession> sessions = new ArrayList<>();
        List<String> links = new ArrayList<>();

        switch (fsState) {
        case OPEN:
            links = studentOpenFeedbackSessionLinksMap.get(student.getIdentificationString());
            break;
        case CLOSED:
            links = studentUnOpenedFeedbackSessionLinksMap.get(student.getIdentificationString());
            break;
        case PUBLISHED:
            links = studentPublishedFeedbackSessionLinksMap.get(student.getIdentificationString());
            break;
        default:
            Assumption.fail();
            break;
        }

        if (links != null) {
            for (String link : links) {
                sessions.add(new AdminSearchStudentFeedbackSession(
                                                feedbackSessionLinkToNameMap.get(link), link));
            }
        }

        return sessions;
    }
}
