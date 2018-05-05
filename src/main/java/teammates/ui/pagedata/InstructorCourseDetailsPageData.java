package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.ElementTag;
import teammates.ui.template.StudentListSectionData;

/**
 * PageData: data used for the "Course Details" page.
 */
public class InstructorCourseDetailsPageData extends PageData {
    private InstructorAttributes currentInstructor;
    private CourseDetailsBundle courseDetails;
    private List<InstructorAttributes> instructors;
    private String studentListHtmlTableAsString;
    private ElementTag courseRemindButton;
    private ElementTag courseDeleteAllButton;
    private List<StudentListSectionData> sections;
    private boolean hasSection;

    public InstructorCourseDetailsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(InstructorAttributes currentInstructor, CourseDetailsBundle courseDetails,
                     List<InstructorAttributes> instructors) {
        this.currentInstructor = currentInstructor;
        this.courseDetails = courseDetails;
        this.instructors = instructors;

        boolean isDisabled =
                !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        String courseId = sanitizeForJs(courseDetails.course.getId());
        String href = sanitizeForJs(getInstructorCourseRemindLink(courseDetails.course.getId()));
        courseRemindButton = createButton(null, "btn btn-primary", "button_remind", href,
                                          Const.Tooltips.COURSE_REMIND, "tooltip", courseId, isDisabled);

        String hrefDeleteStudents = sanitizeForJs(getInstructorCourseStudentDeleteAllLink(courseId));
        courseDeleteAllButton = createButton(null, "btn btn-danger course-student-delete-all-link", "button-delete-all",
                hrefDeleteStudents, null, null, courseId, isDisabled);

        this.sections = new ArrayList<>();
        for (SectionDetailsBundle section : courseDetails.sections) {
            Map<String, String> emailPhotoUrlMapping = new HashMap<>();
            for (TeamDetailsBundle teamDetails : section.teams) {
                for (StudentAttributes student : teamDetails.students) {
                    String studentPhotoUrl = student.getPublicProfilePictureUrl();
                    studentPhotoUrl = Url.addParamToUrl(studentPhotoUrl,
                                                    Const.ParamsNames.USER_ID, account.googleId);
                    emailPhotoUrlMapping.put(student.email, studentPhotoUrl);
                }
            }
            boolean isAllowedToViewStudentInSection = currentInstructor.isAllowedForPrivilege(section.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
            boolean isAllowedToModifyStudent = currentInstructor.isAllowedForPrivilege(section.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            this.sections.add(new StudentListSectionData(section, isAllowedToViewStudentInSection,
                    isAllowedToModifyStudent, emailPhotoUrlMapping, account.googleId, getSessionToken(),
                    Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE));
        }
        if (sections.size() == 1) {
            StudentListSectionData section = sections.get(0);
            this.hasSection = !"None".equals(section.getSectionName());
        } else {
            this.hasSection = true;
        }
    }

    public InstructorAttributes getCurrentInstructor() {
        return currentInstructor;
    }

    public CourseDetailsBundle getCourseDetails() {
        return courseDetails;
    }

    public List<InstructorAttributes> getInstructors() {
        return instructors;
    }

    public ElementTag getCourseRemindButton() {
        return courseRemindButton;
    }

    public ElementTag getCourseDeleteAllButton() {
        return courseDeleteAllButton;
    }

    public void setStudentListHtmlTableAsString(String studentListHtmlTableAsString) {
        this.studentListHtmlTableAsString = studentListHtmlTableAsString;
    }

    public String getStudentListHtmlTableAsString() {
        return studentListHtmlTableAsString;
    }

    public List<StudentListSectionData> getSections() {
        return sections;
    }

    public boolean isHasSection() {
        return hasSection;
    }

    private ElementTag createButton(String content, String buttonClass, String id, String href,
            String title, String dataToggle, String dataCourseId, boolean isDisabled) {
        ElementTag button = new ElementTag(content);

        if (buttonClass != null) {
            button.setAttribute("class", buttonClass);
        }

        if (id != null) {
            button.setAttribute("id", id);
        }

        if (href != null) {
            button.setAttribute("href", href);
        }

        if (title != null) {
            button.setAttribute("title", title);
            button.setAttribute("data-placement", "top");
        }

        if (dataToggle != null) {
            button.setAttribute("data-toggle", dataToggle);
        }

        if (dataCourseId != null) {
            button.setAttribute("data-course-id", dataCourseId);
        }

        if (isDisabled) {
            button.setAttribute("disabled", null);
        }
        return button;
    }
}
