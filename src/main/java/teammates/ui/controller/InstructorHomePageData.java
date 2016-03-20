package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.ui.template.CourseTable;

public class InstructorHomePageData extends PageData {
    
    private boolean isSortingDisabled;
    private List<CourseTable> courseTables;
    private String sortCriteria;
    private String remindParticularStudentActionLink;
    private String editCopyActionLink;
    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
        remindParticularStudentActionLink = getInstructorFeedbackRemindParticularStudentActionLink(true);
        editCopyActionLink = getInstructorFeedbackEditCopyActionLink(true);
    }
    
    public void init(List<CourseSummaryBundle> courseList, String sortCriteria) {
        this.sortCriteria = sortCriteria;
        this.isSortingDisabled = courseList.size() < 2;
        setCourseTables(courseList);
    }
    
    public String getSortCriteria() {
        return sortCriteria;
    }
    
    public boolean isSortingDisabled() {
        return isSortingDisabled;
    }
    
    public List<CourseTable> getCourseTables() {
        return courseTables;
    }
    
    public String getRemindParticularStudentActionLink() {
        return remindParticularStudentActionLink;
    }
    
    public String getEditCopyActionLink() {
        return editCopyActionLink;
    }
    
    private void setCourseTables(List<CourseSummaryBundle> courses) {
        courseTables = new ArrayList<CourseTable>(); 
        for (CourseSummaryBundle courseDetails : courses) {
            courseTables.add(new CourseTable(courseDetails.course, null, null));
        }
    }
}
