package teammates.ui.controller;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing the 'Edit' page for a course of an instructor
 */
public class InstructorCourseEditPageAction extends Action {
 
    @Override
    public ActionResult execute() throws EntityDoesNotExistException { 
                
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes courseToEdit = logic.getCourse(courseId);
         
        new GateKeeper().verifyAccessible(instructor, courseToEdit);
        
        /* Setup page data for 'Edit' page of a course for an instructor */
        InstructorCourseEditPageData data = new InstructorCourseEditPageData(account);
        data.course = courseToEdit;
        data.instructorPermission = this.getInstructorPermissionForEmail(instructor);
        data.instructorList = logic.getInstructorsForCourse(courseId);
        data.instructorPermissions = logic.getInstructorPermissionsForCourse(courseId);
        
        statusToAdmin = "instructorCourseEdit Page Load<br>"
                + "Editing information for Course <span class=\"bold\">["
                + courseId + "]</span>";
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, data);
        return response;
    }
    
    private InstructorPermissionAttributes getInstructorPermissionForEmail(InstructorAttributes instr) {
        InstructorPermissionAttributes instructorPermission = logic.getInstructorPermissionForEmail(instr.courseId, instr.email);
        if (instructorPermission != null) {
            return instructorPermission;
        } else {
            return new InstructorPermissionAttributes(instr.email, instr.courseId, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                    new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        }
    }
}
