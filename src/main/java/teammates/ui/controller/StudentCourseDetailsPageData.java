package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;

public class StudentCourseDetailsPageData extends PageData {
    public CourseDetailsBundle courseDetails;
    public TeamDetailsBundle team;
    public List<InstructorAttributes> instructors;
    
    public StudentCourseDetailsPageData(AccountAttributes account) {
        super(account);
    }
    
}
