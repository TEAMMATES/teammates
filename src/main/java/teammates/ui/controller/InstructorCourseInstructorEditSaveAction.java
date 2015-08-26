package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.INSTRUCTOR_NAME, instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        InstructorAttributes instructorToEdit = extractUpdatedInstructor(courseId, instructorId, instructorName, instructorEmail);
        updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
        
        try {
            if (instructorId != null) {
                logic.updateInstructorByGoogleId(instructorId, instructorToEdit);
            } else {
                logic.updateInstructorByEmail(instructorEmail, instructorToEdit);
            }
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, StatusMessageColor.SUCCESS));
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
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        displayedName = Sanitizer.sanitizeName(displayedName);
        
        InstructorAttributes instructorToEdit = updateBasicInstructorAttributes(courseId, instructorId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, displayedName);
        
        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToEdit);
        }
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToEdit);
        
        instructorToEdit.privileges.validatePrivileges();
        
        instructorToEdit.instructorPrivilegesAsText = instructorToEdit.getTextFromInstructorPrivileges();
        
        return instructorToEdit;
    }

    private void updateInstructorCourseLevelPrivileges(
            InstructorAttributes instructorToEdit) {
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
    }

    private InstructorAttributes updateBasicInstructorAttributes(String courseId,
            String instructorId, String instructorName, String instructorEmail,
            String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        InstructorAttributes instructorToEdit = null;
        if (instructorId != null) {
            instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        } else {
            instructorToEdit = logic.getInstructorForEmail(courseId, instructorEmail);
        }
        instructorToEdit.name = Sanitizer.sanitizeName(instructorName);
        instructorToEdit.email = Sanitizer.sanitizeEmail(instructorEmail);
        instructorToEdit.role = Sanitizer.sanitizeName(instructorRole);
        instructorToEdit.displayedName = Sanitizer.sanitizeName(displayedName);
        instructorToEdit.isDisplayedToStudents = isDisplayedToStudents;
        instructorToEdit.privileges = new InstructorPrivileges(instructorToEdit.role);
        instructorToEdit.instructorPrivilegesAsText = instructorToEdit.getTextFromInstructorPrivileges();
        
        return instructorToEdit;
    }
    
    private void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructorToEdit){
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
        
        List<String> feedbackNames = new ArrayList<String>();
        
        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.feedbackSessionName);
        }
        HashMap<String, List<String>> sectionNamesMap = extractSectionNames(instructorToEdit, sectionNames, sectionNamesTable);
        for (Entry<String, List<String>> entry : sectionNamesMap.entrySet()) {
            updateInstructorPrivilegesForSectionInSectionLevel(entry.getKey(), entry.getValue(), instructorToEdit);
            String setSessionsStr = getRequestParamValue("is" + entry.getKey() + "sessionsset");
            boolean isSessionsSpecial = setSessionsStr != null && setSessionsStr.equals("true");
            if (isSessionsSpecial) {
                updateInstructorPrivilegesForSectionInSessionLevel(entry.getKey(), entry.getValue(), feedbackNames, instructorToEdit);
            } else {
                removeSessionLevelPrivileges(instructorToEdit, entry.getValue());
            }
        }
        for (Entry<String, Boolean> entry : sectionNamesTable.entrySet()) {
            if (!entry.getValue().booleanValue()) {
                instructorToEdit.privileges.removeSectionLevelPrivileges(entry.getKey());
            }
        }
    }

    private void removeSessionLevelPrivileges(InstructorAttributes instructorToEdit, List<String> sectionNames) {
        for (String sectionName : sectionNames) {
            instructorToEdit.privileges.removeSessionsPrivilegesForSection(sectionName);
        }
    }

    private HashMap<String, List<String>> extractSectionNames(
            InstructorAttributes instructorToEdit, List<String> sectionNames, HashMap<String, Boolean> sectionNamesTable) {
        HashMap<String, List<String>> sectionNamesMap = new HashMap<String, List<String>>();
        if (instructorToEdit.role.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            for (int i=0; i<sectionNames.size(); i++) {
                String setSectionGroupStr = getRequestParamValue("is" + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + "set");
                boolean isSectionGroupSpecial = setSectionGroupStr != null && setSectionGroupStr.equals("true");
                for (int j=0; j<sectionNames.size(); j++) {
                    String valueForSectionName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + Const.ParamsNames.INSTRUCTOR_SECTION + j);
                    if (isSectionGroupSpecial && valueForSectionName != null && sectionNamesTable.containsKey(valueForSectionName)) {
                        if (sectionNamesMap.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i) == null) {
                            sectionNamesMap.put(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i, new ArrayList<String>());
                        }
                        sectionNamesMap.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i).add(valueForSectionName);
                        sectionNamesTable.put(valueForSectionName, true);
                    }
                }
            }
        }
        return sectionNamesMap;
    }

    private void updateInstructorPrivilegesForSectionInSectionLevel(String sectionParam, List<String> sectionNames, InstructorAttributes instructorToEdit) {
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + sectionParam) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        for (String sectionName : sectionNames) {
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToEdit.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
    }

    private void updateInstructorPrivilegesForSectionInSessionLevel(String sectionParam,
            List<String> sectionNames, List<String> feedbackNames, InstructorAttributes instructorToEdit) {
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            
            for (String sectionName : sectionNames) {
                instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
                instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
                instructorToEdit.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
            }
        }
    }
}
