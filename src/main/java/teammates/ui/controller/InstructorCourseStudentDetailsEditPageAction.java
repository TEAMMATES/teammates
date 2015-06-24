package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseStudentDetailsEditPageAction extends InstructorCoursesPageAction {
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        boolean hasSection = logic.hasIndicatedSections(courseId);
        
        InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
        data.init(student, student.email, hasSection);

        statusToAdmin = "instructorCourseStudentEdit Page Load<br>"
                        + "Editing Student <span class=\"bold\">" + studentEmail +"'s</span> details "
                        + "in Course <span class=\"bold\">[" + courseId + "]</span>"; 
        

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT, data);

    }


}
