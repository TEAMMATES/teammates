package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * Action: copies instructors from other courses to an existent course of an instructor
 */
public class InstructorCourseInstructorCopyAction extends InstructorCourseInstructorAddAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorEmails = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAILS);
        Assumption.assertNotNull(instructorEmails);
        String instructorNames = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAMES);
        Assumption.assertNotNull(instructorNames);
        String instructorDisplayNames = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAMES);
        Assumption.assertNotNull(instructorDisplayNames);
        String instructorRoles = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAMES);
        Assumption.assertNotNull(instructorRoles);
        
        String areInstructorsAllowedToModifyCourse = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_COURSE);
        Assumption.assertNotNull(areInstructorsAllowedToModifyCourse);
        String areInstructorsAllowedToModifyInstructor = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_INSTRUCTOR);
        Assumption.assertNotNull(areInstructorsAllowedToModifyInstructor);
        String areInstructorsAllowedToModifySession = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_SESSION);
        Assumption.assertNotNull(areInstructorsAllowedToModifySession);
        String areInstructorsAllowedToModifyStudent = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_STUDENT);
        Assumption.assertNotNull(areInstructorsAllowedToModifyStudent);
        
        String areInstructorsAllowedToViewStudentInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_VIEW_STUDENT_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToViewStudentInSections);
        String areInstructorsAllowedToViewCommentInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_VIEW_COMMENT_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToViewCommentInSections);
        String areInstructorsAllowedToGiveCommentInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_GIVE_COMMENT_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToGiveCommentInSections);
        String areInstructorsAllowedToModifyCommentInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_COMMENT_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToModifyCommentInSections);
        
        String areInstructorsAllowedToViewSessionInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_VIEW_SESSION_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToViewSessionInSections);
        String areInstructorsAllowedToSubmitSessionInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_SUBMIT_SESSION_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToSubmitSessionInSections);
        String areInstructorsAllowedToModifySessionInSections = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSIONS_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        Assumption.assertNotNull(areInstructorsAllowedToModifySessionInSections);
        
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        
        HashSet<InstructorAttributes> instructorsDetails = extractInstructorsDetails(courseId, instructorEmails, instructorNames, instructorDisplayNames, instructorRoles
                , areInstructorsAllowedToModifyCourse, areInstructorsAllowedToModifyInstructor, areInstructorsAllowedToModifySession, areInstructorsAllowedToModifyStudent
                , areInstructorsAllowedToViewStudentInSections, areInstructorsAllowedToViewCommentInSections, areInstructorsAllowedToGiveCommentInSections, areInstructorsAllowedToModifyCommentInSections
                , areInstructorsAllowedToViewSessionInSections, areInstructorsAllowedToSubmitSessionInSections, areInstructorsAllowedToModifySessionInSections);
        
        for (InstructorAttributes instructorDetails : instructorsDetails) {
            String instructorEmail = instructorDetails.email;
            Assumption.assertNotNull(instructorEmail);
            String instructorName = instructorDetails.name;
            Assumption.assertNotNull(instructorName);
            String instructorDisplayedName = instructorDetails.displayedName;
            Assumption.assertNotNull(instructorDisplayedName);
            String instructorRole = instructorDetails.role;
            Assumption.assertNotNull(instructorRole);
            
            
            
            /* Process adding the instructor and setup status to be shown to user and admin */
            try {
                InstructorAttributes newInstructor = logic.createInstructor(instructorDetails);
                logic.sendRegistrationInviteToInstructor(courseId, newInstructor);
    
                statusToUser.add(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                        instructorName, instructorEmail));
                statusToAdmin = "New instructor (<span class=\"bold\"> " + instructorEmail + "</span>)"
                        + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
            } catch (EntityAlreadyExistsException e) {
                setStatusForException(e, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
            } catch (InvalidParametersException e) {
                setStatusForException(e);
            }
        
        }
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        redirectResult.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return redirectResult;
    }
    
    HashSet<InstructorAttributes> extractInstructorsDetails(String courseId, String instructorEmails, String instructorNames, String instructorDisplayNames, String instructorRoles
            , String areInstructorsAllowedToModifyCourse, String areInstructorsAllowedToModifyInstructor, String areInstructorsAllowedToModifySession, String areInstructorsAllowedToModifyStudent
            , String areInstructorsAllowedToViewStudentInSections, String areInstructorsAllowedToViewCommentInSections, String areInstructorsAllowedToGiveCommentInSections, String areInstructorsAllowedToModifyCommentInSections
            , String areInstructorsAllowedToViewSessionInSections, String areInstructorsAllowedToSubmitSessionInSections, String areInstructorsAllowedToModifySessionInSections) {
        HashSet<InstructorAttributes> instructorsToAdd = new HashSet<InstructorAttributes>();
        String[] instructorEmailsList = instructorEmails.split("\\|");
        String[] instructorNamesList = instructorNames.split("\\|");
        String[] instructorDisplayNamesList = instructorDisplayNames.split("\\|");
        String[] instructorRolesList = instructorRoles.split("\\|");
        
        String[] isAllowedToModifyCourseList = areInstructorsAllowedToModifyCourse.split("\\|");
        String[] isAllowedToModifyInstructorList = areInstructorsAllowedToModifyInstructor.split("\\|");
        String[] isAllowedToModifySessionList = areInstructorsAllowedToModifySession.split("\\|");
        String[] isAllowedToModifyStudentList = areInstructorsAllowedToModifyStudent.split("\\|");
        
        String[] isAllowedToViewStudentInSectionsList = areInstructorsAllowedToViewStudentInSections.split("\\|");
        String[] isAllowedToViewCommentInSectionsList = areInstructorsAllowedToViewCommentInSections.split("\\|");
        String[] isAllowedToGiveCommentInSectionsList = areInstructorsAllowedToGiveCommentInSections.split("\\|");
        String[] isAllowedToModifyCommentInSectionsList = areInstructorsAllowedToModifyCommentInSections.split("\\|");
        
        String[] isAllowedToViewSessionInSectionsList = areInstructorsAllowedToViewSessionInSections.split("\\|");
        String[] isAllowedToSubmitSessionInSectionsList = areInstructorsAllowedToSubmitSessionInSections.split("\\|");
        String[] isAllowedToModifySessionInSectionsList = areInstructorsAllowedToModifySessionInSections.split("\\|");
        
        for(int i = 0; i < instructorEmailsList.length; i++) {
            String[] privilege = new String[11];
            
            privilege[0] = isAllowedToModifyCourseList[i];
            privilege[1] = isAllowedToModifyInstructorList[i];
            privilege[2] = isAllowedToModifySessionList[i];
            privilege[3] = isAllowedToModifyStudentList[i];
            
            privilege[4] = isAllowedToViewStudentInSectionsList[i];
            privilege[5] = isAllowedToViewCommentInSectionsList[i];
            privilege[6] = isAllowedToGiveCommentInSectionsList[i];
            privilege[7] = isAllowedToModifyCommentInSectionsList[i];
           
            privilege[8] = isAllowedToViewSessionInSectionsList[i];
            privilege[9] = isAllowedToSubmitSessionInSectionsList[i];
            privilege[10] = isAllowedToModifySessionInSectionsList[i];  
            
            instructorsToAdd.add(extractCompleteInstructor(courseId, instructorEmailsList[i].trim(), instructorNamesList[i].trim(), instructorDisplayNamesList[i].trim(), instructorRolesList[i].trim(), privilege));
        }
        return instructorsToAdd;
    }
    
    private InstructorAttributes extractCompleteInstructor(String courseId, String instructorEmail, String instructorName, String instructorDisplayName, String instructorRole, String[] privilege) {
        boolean isDisplayedToStudents = (instructorDisplayName == null || instructorDisplayName.isEmpty()) ?
                false : true;
        instructorDisplayName = (instructorDisplayName == null || instructorDisplayName.isEmpty()) ?
                InstructorAttributes.DEFAULT_DISPLAY_NAME : instructorDisplayName;
        instructorName = Sanitizer.sanitizeName(instructorName);
        instructorDisplayName = Sanitizer.sanitizeName(instructorDisplayName);
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        
        InstructorAttributes instructorToAdd = updateBasicInstructorAttributes(courseId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, instructorDisplayName);
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToAdd);
        
        instructorToAdd.privileges.validatePrivileges();
        
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        return instructorToAdd;
    }
}
