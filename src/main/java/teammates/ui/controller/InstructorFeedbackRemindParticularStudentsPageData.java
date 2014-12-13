package teammates.ui.controller;

import java.util.Vector;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.AccountAttributes;

public class InstructorFeedbackRemindParticularStudentsPageData extends PageData {
    public Vector<StudentAttributes> studentsToRemind;
    
    public InstructorFeedbackRemindParticularStudentsPageData(AccountAttributes account) {
        super(account);
    }
}
