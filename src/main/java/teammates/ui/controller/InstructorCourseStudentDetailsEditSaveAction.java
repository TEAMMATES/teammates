package teammates.ui.controller;

import java.util.Arrays;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorCourseStudentDetailsEditSaveAction extends InstructorCoursesPageAction {
    
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        
        boolean hasSection = logic.hasIndicatedSections(courseId);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        
        if (student == null) {
            return redirectWithError(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT,
                                     "Student <span class=\"bold\">" + studentEmail + "</span> in "
                                     + "Course <span class=\"bold\">[" + courseId + "]</span> not found.",
                                     courseId);
        }
        
        student.name = getRequestParamValue(Const.ParamsNames.STUDENT_NAME);
        student.email = getRequestParamValue(Const.ParamsNames.NEW_STUDENT_EMAIL);
        student.team = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        student.section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        student.comments = getRequestParamValue(Const.ParamsNames.COMMENTS);    
        
        student.name = Sanitizer.sanitizeName(student.name);
        student.email = Sanitizer.sanitizeEmail(student.email);
        student.team = Sanitizer.sanitizeName(student.team);
        student.section = Sanitizer.sanitizeName(student.section);
        student.comments = Sanitizer.sanitizeTextField(student.comments);
        
        try {
            student.updateWithExistingRecord(logic.getStudentForEmail(courseId, studentEmail));
            logic.validateSections(Arrays.asList(student), courseId);
            logic.updateStudent(studentEmail, student);
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Student <span class=\"bold\">" + studentEmail + "'s</span> details in "
                            + "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                            + "New Email: " + student.email + "<br>New Team: " + student.team + "<br>"
                            + "Comments: " + student.comments;
            
            RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
            return result;
            
        } catch (InvalidParametersException | EnrollException e) {
            setStatusForException(e);
            String newEmail = student.email;
            student.email = studentEmail;
            InstructorCourseStudentDetailsEditPageData data =
                    new InstructorCourseStudentDetailsEditPageData(account, student, newEmail, hasSection);
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT, data);
        }
        
    }

    private RedirectResult redirectWithError(String errorToUser, String errorToAdmin, String courseId) {
        statusToUser.add(new StatusMessage(errorToUser, StatusMessageColor.DANGER));
        statusToAdmin = errorToAdmin;
        isError = true;
        
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }

}
