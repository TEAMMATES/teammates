package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseStudentDetailsPageAction extends InstructorCoursesPageAction {
    
    private InstructorCourseStudentDetailsPageData data;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        
        if (student == null) {
            statusToUser.add(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_COURSE_DETAILS);
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        
        String commentRecipient = getRequestParamValue(Const.ParamsNames.SHOW_COMMENT_BOX);
        
        data = new InstructorCourseStudentDetailsPageData(account);
        
        data.currentInstructor = instructor;
        data.student = student;        
        
        data.regKey = logic.getEncryptedKeyForStudent(courseId, studentEmail);
        data.hasSection = logic.hasIndicatedSections(courseId);
        data.commentRecipient = commentRecipient;
        
        loadStudentProfile();
        
        statusToAdmin = "instructorCourseStudentDetails Page Load<br>" + 
                "Viewing details for Student <span class=\"bold\">" + studentEmail + 
                "</span> in Course <span class=\"bold\">[" + courseId + "]</span>"; 
        

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

    }
    
    private void loadStudentProfile() {
        // this means that the user is returning to the page and is not the first time
        boolean hasExistingStatus = !statusToUser.isEmpty() || 
                session.getAttribute(Const.ParamsNames.STATUS_MESSAGE) != null;
        
        if (data.student.googleId.isEmpty()) {
            if (!hasExistingStatus) {
                statusToUser.add(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS);
            }
        } else if(!data.currentInstructor.isAllowedForPrivilege(data.student.section, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
            if (!hasExistingStatus) {
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR);
            }
        } else {
            data.studentProfile = logic.getStudentProfile(data.student.googleId);
            Assumption.assertNotNull(data.studentProfile);
        }
    }
}
