package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;

public class InstructorStudentListAjaxPageData extends PageData {

    public InstructorStudentListAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public List<SectionDetailsBundle> courseSectionDetails;
    public CourseAttributes course;
    public boolean hasSection;
    public Map<String, String> emailPhotoUrlMapping;
    public Map<String, Map<String, Boolean>> sectionPrivileges;

}
