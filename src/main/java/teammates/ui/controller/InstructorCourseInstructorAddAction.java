package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
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
        
        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER)) {
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
        }
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToAdd);
        
        instructorToAdd.privileges.validatePrivileges();
        
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        instructorToAdd.isArchived = false;
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
    
    private void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructorToAdd){
        List<String> sectionNames = null;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch(EntityDoesNotExistException e) {
            return ;
        }
        HashMap<String, Boolean> sectionNamesTable = new HashMap<String, Boolean>();
        for (String sectionName : sectionNames) {
            sectionNamesTable.put(sectionName, false);
        }
        List<String> evalNames = new ArrayList<String>();
        List<String> feedbackNames = new ArrayList<String>();
        List<EvaluationAttributes> evaluations = logic.getEvaluationsForCourse(courseId);
        for (EvaluationAttributes eval : evaluations) {
            evalNames.add(eval.name);
        }
        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.feedbackSessionName);
        }
        HashMap<String, String> sectionNamesMap = new HashMap<String, String>();
        for (int i=0;i<sectionNames.size();i++) {
            String setSectionStr = getRequestParamValue("is" + Const.ParamsNames.INSTRUCTOR_SECTION + i + "set");
            boolean isSectionSpecial = setSectionStr != null && setSectionStr.equals("true");
            String valueForSectionName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SECTION + i);
            if (isSectionSpecial && valueForSectionName != null && sectionNamesTable.containsKey(valueForSectionName)) {
                sectionNamesMap.put(Const.ParamsNames.INSTRUCTOR_SECTION + i, valueForSectionName);
                sectionNamesTable.put(valueForSectionName, true);
            }
        }
        for (Entry<String, String> entry : sectionNamesMap.entrySet()) {
            updateInstructorPrivilegesForSectionInSectionLevel(entry.getKey(), entry.getValue(), instructorToAdd);
            String setSessionsStr = getRequestParamValue("is" + entry.getKey() + "sessionsset");
            boolean isSessionsSpecial = setSessionsStr != null && setSessionsStr.equals("true");
            if (isSessionsSpecial) {
                updateInstructorPrivilegesForSectionInSessionLevel(entry.getKey(), entry.getValue(), evalNames, feedbackNames, instructorToAdd);
            } else {
                instructorToAdd.privileges.removeSessionsPrivilegesForSection(entry.getValue());
            }
        }
        for (Entry<String, Boolean> entry : sectionNamesTable.entrySet()) {
            if (!entry.getValue().booleanValue()) {
                instructorToAdd.privileges.removeSectionLevelPrivileges(entry.getKey());
            }
        }
    }
    
    private void updateInstructorPrivilegesForSectionInSectionLevel(String sectionParam, String sectionName, InstructorAttributes instructorToAdd) {
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + sectionParam) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
    }

    private void updateInstructorPrivilegesForSectionInSessionLevel(String sectionParam,
            String sectionName, List<String> evalNames, List<String> feedbackNames, InstructorAttributes instructorToAdd) {
        for (String evalName : evalNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            
            instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
    }

}
