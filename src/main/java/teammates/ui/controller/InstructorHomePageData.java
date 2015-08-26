package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.ui.template.CourseTable;

public class InstructorHomePageData extends PageData {
    
    private int unarchivedCoursesCount;
    private List<CourseTable> courseTables;
    private String sortCriteria;

    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(List<CourseSummaryBundle> courseList, String sortCriteria,
            Map<String,InstructorAttributes> instructors) {
        this.sortCriteria = sortCriteria;
        setUnarchivedCoursesCount(courseList, instructors);
        setCourseTables(courseList);
    }
    
    public String getSortCriteria() {
        return sortCriteria;
    }
    
    public int getUnarchivedCoursesCount() {
        return unarchivedCoursesCount;
    }
    
    public List<CourseTable> getCourseTables() {
        return courseTables;
    }
    
    private void setUnarchivedCoursesCount(List<CourseSummaryBundle> courses,
                                           Map<String, InstructorAttributes> instructors) {
        unarchivedCoursesCount = 0;
        for (CourseSummaryBundle courseDetails : courses) {
            InstructorAttributes instructor = instructors.get(courseDetails.course.id);
            boolean notArchived = instructor.isArchived == null || !instructor.isArchived;
            if (notArchived) {
                unarchivedCoursesCount++;
            }
        }
    }
    
    private void setCourseTables(List<CourseSummaryBundle> courses) {
        courseTables = new ArrayList<CourseTable>(); 
        for (CourseSummaryBundle courseDetails : courses) {
            courseTables.add(new CourseTable(courseDetails.course, null, null));
        }
    }
}
