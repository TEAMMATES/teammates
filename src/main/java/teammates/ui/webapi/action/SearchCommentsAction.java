package teammates.ui.webapi.action;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: Showing the InstructorSearchPage for an instructor.
 */
public class SearchCommentsAction extends Action {
    // TODO: Write tests

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only instructor can search
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        boolean isSearchForStudents = getBooleanRequestParamValue(Const.ParamsNames.SEARCH_STUDENTS);
        boolean isSearchFeedbackSessionData = getBooleanRequestParamValue(Const.ParamsNames.SEARCH_FEEDBACK_SESSION_DATA);

        SearchResult output = new SearchResult();

        //Start searching
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
        if (isSearchFeedbackSessionData) {
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults =
                    logic.searchFeedbackResponseComments(searchKey, instructors);
            setSearchFeedbackSessionDataTables(output, frCommentSearchResults);
        }
        if (isSearchForStudents) {
            StudentSearchResultBundle studentSearchResults = logic.searchStudents(searchKey, instructors);
            setSearchStudentsTables(output, studentSearchResults);
        }

        return new JsonResult(output);
    }

    private void setSearchStudentsTables(SearchResult output, StudentSearchResultBundle studentSearchResultBundle) {
        Stream<String> distinctCourseIds =
                studentSearchResultBundle.studentList.stream().map(student -> student.course).distinct();
        output.searchStudentsTables = distinctCourseIds
            .map(courseId -> new SearchStudentsTable(
                courseId, createStudentRows(courseId, studentSearchResultBundle)))
            .collect(Collectors.toList());
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void setSearchFeedbackSessionDataTables(SearchResult output,
            FeedbackResponseCommentSearchResultBundle resultBundle) {
        /*
        this.feedbackSessionDataResults = resultBundle.questions.keySet().stream()
            .map(fsName -> new FeedbackSessionDataResultRow(fsName,
                resultBundle.sessions.get(fsName).getCourseId(),
                createQuestions(fsName, resultBundle)))
            .collect(Collectors.toList());
            */
    }

    private List<StudentListSectionData> createStudentRows(String courseId,
            StudentSearchResultBundle studentSearchResultBundle) {
        List<StudentAttributes> studentsInCourse = studentSearchResultBundle.studentList.stream()
                .filter(student -> student.course.equals(courseId)).collect(Collectors.toList());
        Stream<String> distinctSectionName = studentsInCourse.stream().map(student -> student.section).distinct();
        InstructorAttributes instructor = studentSearchResultBundle.courseIdInstructorMap.get(courseId);
        return distinctSectionName
            .map(sectionName -> new StudentListSectionData(
                sectionName,
                instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS),
                instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                createStudentDataInSection(sectionName, studentsInCourse)))
            .collect(Collectors.toList());
    }

    private List<StudentListStudentData> createStudentDataInSection(String sectionName,
            List<StudentAttributes> studentsInCourse) {
        return studentsInCourse.stream().filter(student -> student.section.equals(sectionName))
            .map(student -> new StudentListStudentData(
                student.name, student.email, student.getStudentStatus(), student.team))
            .collect(Collectors.toList());
    }

    /**
     * Output format for {@link SearchCommentsAction}.
     */
    private static class SearchResult extends ApiOutput {
        /* Tables containing search results */
        public List<SearchFeedbackSessionDataTable> searchFeedbackSessionDataTables;
        public List<SearchStudentsTable> searchStudentsTables;

    }

    private static class SearchFeedbackSessionDataTable {

    }

    private static class SearchStudentsTable {
        public String courseId;
        public List<StudentListSectionData> sections;

        SearchStudentsTable(String courseId, List<StudentListSectionData> sections) {
            this.courseId = courseId;
            this.sections = sections;
        }
    }

    private static class StudentListSectionData {
        public String sectionName;
        public boolean isAllowedToViewStudentInSection;
        public boolean isAllowedToModifyStudent;
        public List<StudentListStudentData> students;

        StudentListSectionData(String sectionName, boolean isAllowedToViewStudentInSection,
                                    boolean isAllowedToModifyStudent, List<StudentListStudentData> students) {
            this.sectionName = sectionName;
            this.isAllowedToViewStudentInSection = isAllowedToViewStudentInSection;
            this.isAllowedToModifyStudent = isAllowedToModifyStudent;
            this.students = students;
        }
    }

    private static class StudentListStudentData {

        public String name;
        public String email;
        public String status;
        public String team;

        StudentListStudentData(String studentName, String studentEmail, String studentStatus, String teamName) {
            this.name = studentName;
            this.email = studentEmail;
            this.status = studentStatus;
            this.team = teamName;
        }
    }
}
