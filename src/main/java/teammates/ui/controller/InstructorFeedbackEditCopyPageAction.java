package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;


public class InstructorFeedbackEditCopyPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        List<InstructorAttributes> instructor = logic.getInstructorsForGoogleId(account.googleId);
        Assumption.assertNotNull(instructor);
        
        InstructorFeedbackEditCopyData data = new InstructorFeedbackEditCopyData(account);
        data.courses = logic.getCoursesForInstructor(account.googleId);
               
        return createAjaxResult("", data);
    }

}
