package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
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
        
        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_COURSE_DETAILS, StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        
        String commentRecipient = getRequestParamValue(Const.ParamsNames.SHOW_COMMENT_BOX);
        
        boolean isAbleToAddComment = instructor.isAllowedForPrivilege(
                student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        boolean hasSection = logic.hasIndicatedSections(courseId);
        
        StudentProfileAttributes studentProfile = loadStudentProfile(student, instructor);
        
        InstructorCourseStudentDetailsPageData data =
                new InstructorCourseStudentDetailsPageData(account, student, studentProfile,
                                                           isAbleToAddComment, hasSection, commentRecipient);
        
        statusToAdmin = "instructorCourseStudentDetails Page Load<br>"
                        + "Viewing details for Student <span class=\"bold\">" + studentEmail
                        + "</span> in Course <span class=\"bold\">[" + courseId + "]</span>"; 
        

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

    }
    
    private StudentProfileAttributes loadStudentProfile(StudentAttributes student, InstructorAttributes currentInstructor) {
        StudentProfileAttributes studentProfile = null;

        // this means that the user is returning to the page and is not the first time
        boolean hasExistingStatus = !statusToUser.isEmpty()
                                    || session.getAttribute(Const.ParamsNames.STATUS_MESSAGE) != null;
        
        if (student.googleId.isEmpty()) {
            if (!hasExistingStatus) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS, StatusMessageColor.WARNING));
            }
        } else if (!currentInstructor.isAllowedForPrivilege(student.section, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
            if (!hasExistingStatus) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR, StatusMessageColor.WARNING));
            }
        } else {
            studentProfile = logic.getStudentProfile(student.googleId);
            Assumption.assertNotNull(studentProfile);
        }
        
        return studentProfile;
    }
}
