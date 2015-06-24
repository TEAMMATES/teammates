package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;


public class InstructorCourseStudentDetailsEditPageData extends  InstructorCourseStudentDetailsPageData{
    
    public boolean hasSection;
    public String newEmail;

    public InstructorCourseStudentDetailsEditPageData(AccountAttributes account) {
        super(account);
    }

    public void init(StudentAttributes student, String newEmail, boolean hasSection) {
        this.hasSection = hasSection;
        this.newEmail = newEmail;
        super.init(student, null, false, hasSection, null);
    }

}
