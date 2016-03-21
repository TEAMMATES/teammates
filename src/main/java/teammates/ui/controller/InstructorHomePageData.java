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
    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
        remindParticularStudentActionLink = getInstructorFeedbackRemindParticularStudentActionLink(true);
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

    /**
     * Retrieves the form submit link of reminding particular student with 
     * link of page to return after completing.
     * @return form submit action link
     */
    public String getRemindParticularStudentActionLink() {
        return remindParticularStudentActionLink;
    }
    
    private void setCourseTables(List<CourseSummaryBundle> courses) {
        courseTables = new ArrayList<CourseTable>(); 
        for (CourseSummaryBundle courseDetails : courses) {
            courseTables.add(new CourseTable(courseDetails.course, null, null));
        }
    }
}
