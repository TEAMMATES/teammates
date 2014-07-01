package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        //TODO: Allow editing of the instructors whom hasn't join the course yet
        // Need to change the corresponding UI with extra parameters in the request to have this feature
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        Assumption.assertNotNull(instructorId);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        InstructorAttributes instructorToEdit = extractUpdatedInstructor(courseId, instructorId, instructorName, instructorEmail);
        updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
        
        try {
            logic.updateInstructorByGoogleId(instructorId, instructorToEdit);
            
            statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED);
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorName + "</span>"
                    + " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                    + "New Name: " + instructorName + "<br>New Email: " + instructorEmail;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        /* Create redirection to 'Edit' page with corresponding course id */
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }
    
    private void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrCanModifyInstructor = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrCanModifyInstructor = instructor;
            }
        }
        boolean lastCanModifyInstructor = (numOfInstrCanModifyInstructor <= 1) && 
                ((instrCanModifyInstructor != null && instrCanModifyInstructor.googleId == null) ||
                (instrCanModifyInstructor != null && instrCanModifyInstructor.googleId != null &&
                instrCanModifyInstructor.googleId.equals(instructorToEdit.googleId)));
        if (lastCanModifyInstructor) {
            instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        }
        instructorToEdit.instructorPrivilegesAsText = instructorToEdit.getTextFromInstructorPrivileges();
    }
    
    private InstructorAttributes extractUpdatedInstructor(String courseId, String instructorId, String instructorName, String instructorEmail) {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        displayedName = (displayedName == null || displayedName.isEmpty()) ?
                InstructorAttributes.DEFAULT_DISPLAY_NAME : displayedName;
        
        InstructorAttributes instructorToEdit = updateBasicInstructorAttributes(courseId, instructorId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, displayedName);
        
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
            
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, isModifyCourseChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, isModifyInstructorChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, isModifySessionChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, isModifyStudentChecked);
        
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
        
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToEdit);
        
        instructorToEdit.privileges.validatePrivileges();
        
        instructorToEdit.instructorPrivilegesAsText = instructorToEdit.getTextFromInstructorPrivileges();
        
        return instructorToEdit;
    }

    private InstructorAttributes updateBasicInstructorAttributes(String courseId,
            String instructorId, String instructorName, String instructorEmail,
            String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        InstructorAttributes instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        instructorToEdit.name = Sanitizer.sanitizeName(instructorName);
        instructorToEdit.email = Sanitizer.sanitizeEmail(instructorEmail);
        instructorToEdit.role = Sanitizer.sanitizeName(instructorRole);
        instructorToEdit.displayedName = Sanitizer.sanitizeName(displayedName);
        instructorToEdit.isDisplayedToStudents = isDisplayedToStudents;
        
        return instructorToEdit;
    }
    
    private void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructorToEdit){
        // TODO: use set here is better
        List<String> sectionNames = null;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch(EntityDoesNotExistException e) {
            return ;
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
            String valueForSectionName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SECTION + i);
            if (valueForSectionName != null && sectionNames.contains(valueForSectionName)) {
                sectionNamesMap.put(Const.ParamsNames.INSTRUCTOR_SECTION + i, valueForSectionName);
            }
        }
        for (Entry<String, String> entry : sectionNamesMap.entrySet()) {
            updateInstructorPrivilegesForSectionInSectionLevel(entry.getKey(), entry.getValue(), instructorToEdit);
            updateInstructorPrivilegesForSectionInSessionLevel(entry.getKey(), entry.getValue(), evalNames, feedbackNames, instructorToEdit);
        }
    }

    private void updateInstructorPrivilegesForSectionInSectionLevel(String sectionParam, String sectionName, InstructorAttributes instructorToEdit) {
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + sectionParam) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
        instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
    }

    private void updateInstructorPrivilegesForSectionInSessionLevel(String sectionParam,
            String sectionName, List<String> evalNames, List<String> feedbackNames, InstructorAttributes instructorToEdit) {
        for (String evalName : evalNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName) != null;
            
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            
            instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
    }
}
