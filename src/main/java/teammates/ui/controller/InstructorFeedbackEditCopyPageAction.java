package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.logic.api.Logic;


public class InstructorFeedbackEditCopyPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        List<InstructorAttributes> instructor = logic.getInstructorsForGoogleId(account.googleId);
        Assumption.assertNotNull(instructor);
        
        InstructorFeedbackEditCopyData data = new InstructorFeedbackEditCopyData(account);
        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        
        for (CourseAttributes course : courses) {
            if (!Logic.isCourseArchived(course.id, account.googleId)) { 
                data.courses.add(course);
            }
        }
               
        return createAjaxResult("", data);
    }

}
