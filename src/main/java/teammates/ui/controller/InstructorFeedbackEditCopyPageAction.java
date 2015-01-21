package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.Logic;


public class InstructorFeedbackEditCopyPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId);
        Assumption.assertNotNull(instructors);
        
        InstructorFeedbackEditCopyPageData data = new InstructorFeedbackEditCopyPageData(account);
        data.courses = new ArrayList<CourseAttributes>();
                
        
        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        
        // Only add courses to data if the course is not archived and instructor has sufficient permissions
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.id, account.googleId);
            
            boolean isAllowedToMakeSession = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            boolean isArchived = Logic.isCourseArchived(course.id, account.googleId);

            if (!isArchived && isAllowedToMakeSession) { 
                data.courses.add(course);
            }
        }
        
        CourseAttributes.sortByCreatedDate(data.courses);
               
        return createAjaxResult("", data);
    }

}
