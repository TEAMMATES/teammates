package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.template.StudentListSectionData;

public class InstructorStudentListAjaxPageData extends PageData {

    private String courseId;
    private int courseIndex;
    private boolean hasSection;
    private List<StudentListSectionData> sections;

    public InstructorStudentListAjaxPageData(AccountAttributes account, String courseId, int courseIndex,
                                             boolean hasSection, List<SectionDetailsBundle> sections,
                                             Map<String, Map<String, Boolean>> sectionPrivileges,
                                             Map<String, String> emailPhotoUrlMapping) {
        super(account);
        this.courseId = courseId;
        this.courseIndex = courseIndex;
        this.hasSection = hasSection;
        List<StudentListSectionData> sectionsDetails =
                                        new ArrayList<StudentListSectionData>();
        for (SectionDetailsBundle section : sections) {
            boolean isAllowedToViewStudentInSection = sectionPrivileges.get(section.name)
                                            .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
            boolean isAllowedToModifyStudent = sectionPrivileges.get(section.name)
                                            .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            boolean isAllowedToGiveCommentInSection = sectionPrivileges.get(section.name)
                                            .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            sectionsDetails.add(new StudentListSectionData(section, isAllowedToViewStudentInSection,
                                                           isAllowedToModifyStudent, isAllowedToGiveCommentInSection,
                                                           emailPhotoUrlMapping, account.googleId));
        }
        this.sections = sectionsDetails;
    }

    public String getCourseId() {
        return courseId;
    }

    public int getCourseIndex() {
        return courseIndex;
    }

    public boolean isHasSection() {
        return hasSection;
    }

    public List<StudentListSectionData> getSections() {
        return sections;
    }

}
