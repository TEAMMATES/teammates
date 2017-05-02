package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public HashMap<String, List<String>> studentOpenFeedbackSessionLinksMap = new HashMap<String, List<String>>();
    public HashMap<String, List<String>> studentUnOpenedFeedbackSessionLinksMap = new HashMap<String, List<String>>();
    public HashMap<String, List<String>> studentPublishedFeedbackSessionLinksMap = new HashMap<String, List<String>>();
    public HashMap<String, String> feedbackSeesionLinkToNameMap = new HashMap<String, String>();
    public HashMap<String, String> studentIdToHomePageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> studentRecordsPageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> studentInstituteMap = new HashMap<String, String>();

    /*
     * Data related to searched instructors
     */
    public InstructorSearchResultBundle instructorResultBundle = new InstructorSearchResultBundle();
    public HashMap<String, String> instructorInstituteMap = new HashMap<String, String>();
    public HashMap<String, String> instructorHomaPageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> instructorCourseJoinLinkMap = new HashMap<String, String>();

    /*
     * Data related to both instructors and students
     */
    public HashMap<String, String> courseIdToCourseNameMap = new HashMap<String, String>();

    /*
     * Search result tables
     */
    private AdminSearchInstructorTable instructorTable;
    private AdminSearchStudentTable studentTable;

    public AdminSearchPageData(AccountAttributes account) {
        super(account);
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
        List<AdminSearchInstructorRow> rows = new ArrayList<AdminSearchInstructorRow>();

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
        String googleIdLink = instructorHomaPageLinkMap.get(instructor.googleId);
        String institute = instructorInstituteMap.get(instructor.getIdentificationString());
        String viewRecentActionsId = createViewRecentActionsId(instructor);
        String email = instructor.email;
        String courseJoinLink = instructorCourseJoinLinkMap.get(instructor.getIdentificationString());

        return new AdminSearchInstructorRow(id, name, courseName, courseId, googleId, googleIdLink,
                                            institute, viewRecentActionsId, email, courseJoinLink);
    }

    private String createId(InstructorAttributes instructor) {
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
        List<AdminSearchStudentRow> rows = new ArrayList<AdminSearchStudentRow>();

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

    private String createId(StudentAttributes student) {
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

        List<AdminSearchStudentFeedbackSession> sessions = new ArrayList<AdminSearchStudentFeedbackSession>();
        List<String> links = new ArrayList<String>();

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
                                                feedbackSeesionLinkToNameMap.get(link), link));
            }
        }

        return sessions;
    }
}
