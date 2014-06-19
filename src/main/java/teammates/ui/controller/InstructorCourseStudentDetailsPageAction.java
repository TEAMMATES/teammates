package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseStudentDetailsPageAction extends InstructorCoursesPageAction {
    
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        // although "None" means "no section", yet in the intructorPrivilege sectionLevel, "None" will also never be a key
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        
        InstructorCourseStudentDetailsPageData data = new InstructorCourseStudentDetailsPageData(account);
        
        data.student = student;
        data.regKey = logic.getEncryptedKeyForStudent(courseId, studentEmail);
        data.hasSection = logic.hasIndicatedSections(courseId);
        
        statusToAdmin = "instructorCourseStudentDetails Page Load<br>" + 
                "Viewing details for Student <span class=\"bold\">" + studentEmail + 
                "</span> in Course <span class=\"bold\">[" + courseId + "]</span>"; 
        

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

    }


}
