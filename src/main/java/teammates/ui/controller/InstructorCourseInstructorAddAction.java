package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

/**
 * Action: add another instructor for an existent course of an instructor
 */
public class InstructorCourseInstructorAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        
        InstructorAttributes instructorToAdd = extractCompleteInstructor(
                courseId, instructorName, instructorEmail);
        
        /* Process adding the instructor and setup status to be shown to user and admin */
        try {
            logic.createInstructor(instructorToAdd);
            logic.sendRegistrationInviteToInstructor(courseId, instructorEmail);
            
            statusToUser.add(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                    instructorName, instructorEmail));
            statusToAdmin = "New instructor (<span class=\"bold\"> " + instructorEmail + "</span>)"
                    + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        redirectResult.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return redirectResult;
    }
    
    private InstructorAttributes extractCompleteInstructor(String courseId, String instructorName, String instructorEmail) {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        displayedName = (displayedName == null || displayedName.isEmpty()) ?
                InstructorAttributes.DEFAULT_DISPLAY_NAME : displayedName;
        
        boolean isModifyCourseChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) != null;
        boolean isModifyInstructorChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR) != null;
        boolean isModifySessionChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) != null;
        boolean isModifyStudentChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT) != null;
        
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS) != null;
        
        InstructorAttributes instructorToAdd = constructorNewInstructor(courseId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, displayedName);
            
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, isModifyCourseChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, isModifyInstructorChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, isModifySessionChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, isModifyStudentChecked);
        
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
        
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        
        instructorToAdd.privileges.validatePrivileges();
        
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        return instructorToAdd;
    }

    private InstructorAttributes constructorNewInstructor(String courseId,
            String instructorName, String instructorEmail, String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        instructorName = Sanitizer.sanitizeName(instructorName);
        instructorEmail = Sanitizer.sanitizeEmail(instructorEmail);
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        displayedName = Sanitizer.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, instructorName, instructorEmail, instructorRole,
                isDisplayedToStudents, displayedName, privileges);
        return instructorToAdd;
    }

}
