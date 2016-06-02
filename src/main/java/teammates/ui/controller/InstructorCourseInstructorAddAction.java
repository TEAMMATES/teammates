package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;

/**
 * Action: add another instructor to a course that already exists
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
            InstructorAttributes newInstructor = logic.createInstructor(instructorToAdd);
            logic.sendRegistrationInviteToInstructor(courseId, newInstructor);

            statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                                                               instructorName, instructorEmail), StatusMessageColor.SUCCESS));
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
    
    /**
     * Creates a new instructor with all information filled in, using request parameters.
     * This includes basic information as well as custom privileges (if applicable).
     * 
     * @param courseId         Id of the course the instructor is being added to.
     * @param instructorName   Name of the instructor.
     * @param instructorEmail  Email of the instructor.
     * @return An instructor with all relevant info filled in.
     */
    private InstructorAttributes extractCompleteInstructor(String courseId, String instructorName, String instructorEmail) {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        if (displayedName == null || displayedName.isEmpty()) {
            displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        }
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        displayedName = Sanitizer.sanitizeName(displayedName);
        
        InstructorAttributes instructorToAdd = updateBasicInstructorAttributes(courseId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, displayedName);
        
        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToAdd);
        }
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToAdd);
        
        instructorToAdd.privileges.validatePrivileges();
        
        return instructorToAdd;
    }
    
    /**
     * Creates a new instructor with basic information.
     * This consists of everything apart from custom privileges.
     * 
     * @param courseId              Id of the course the instructor is being added to.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to students.
     * @param displayedName         Name to be visible to students.
     *                                  Should not be {@code null} even if {@code isDisplayedToStudents} is false.
     * @return An instructor with basic info, excluding custom privileges
     */
    private InstructorAttributes updateBasicInstructorAttributes(String courseId, String instructorName, String instructorEmail,
            String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        String instrName = Sanitizer.sanitizeName(instructorName);
        String instrEmail = Sanitizer.sanitizeEmail(instructorEmail);
        String instrRole = Sanitizer.sanitizeName(instructorRole);
        String instrDisplayedName = Sanitizer.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);
        
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, instrName, instrEmail,
                instrRole, isDisplayedToStudents, instrDisplayedName, privileges);
        
        return instructorToAdd;
    }
    
    /**
     * Updates course level privileges for the instructor by retrieving request parameters.
     * 
     * @param instructorToAdd Instructor that will be added.
     *                            This will be modified within the method.
     */
    private void updateInstructorCourseLevelPrivileges(
            InstructorAttributes instructorToAdd) {
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
    
    /**
     * Updates section and session level privileges for the instructor.
     * 
     * @param courseId        Course that the instructor is being added to.
     * @param instructorToAdd Instructor that will be added.
     *                            This will be modified within the method.
     */
    private void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructorToAdd) {
        List<String> sectionNames = null;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch (EntityDoesNotExistException e) {
            return;
        }
        HashMap<String, Boolean> isSectionSpecialMappings = new HashMap<String, Boolean>();
        for (String sectionName : sectionNames) {
            isSectionSpecialMappings.put(sectionName, false);
        }

        List<String> feedbackNames = new ArrayList<String>();

        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.feedbackSessionName);
        }
        HashMap<String, List<String>> sectionNamesMap = getSectionsWithSpecialPrivilegesFromParameters(
                                                                instructorToAdd, sectionNames,
                                                                isSectionSpecialMappings);
        for (Entry<String, List<String>> entry : sectionNamesMap.entrySet()) {
            String sectionGroupName = entry.getKey();
            List<String> specialSectionsInSectionGroup = entry.getValue();
            
            updateInstructorPrivilegesForSectionInSectionLevel(sectionGroupName,
                    specialSectionsInSectionGroup, instructorToAdd);
            
            //check if session-specific permissions are to be used
            String setSessionsStr = getRequestParamValue("is" + sectionGroupName + "sessionsset");
            boolean isSessionsForSectionGroupSpecial = "true".equals(setSessionsStr);
            if (isSessionsForSectionGroupSpecial) {
                updateInstructorPrivilegesForSectionInSessionLevel(sectionGroupName,
                        specialSectionsInSectionGroup, feedbackNames, instructorToAdd);
            } else {
                removeSessionLevelPrivileges(instructorToAdd, specialSectionsInSectionGroup);
            }
        }
        for (Entry<String, Boolean> entry : isSectionSpecialMappings.entrySet()) {
            String sectionNameToBeChecked = entry.getKey();
            boolean isSectionSpecial = entry.getValue().booleanValue();
            if (!isSectionSpecial) {
                instructorToAdd.privileges.removeSectionLevelPrivileges(sectionNameToBeChecked);
            }
        }
    }
    
    /**
     * Removes session level privileges for the instructor under the given sections.
     * 
     * @param instructorToAdd Instructor that will be added.
     *                            This will be modified within the method.
     * @param sectionNames    List of section names to be removed.
     */
    private void removeSessionLevelPrivileges(InstructorAttributes instructorToAdd, List<String> sectionNames) {
        for (String sectionName : sectionNames) {
            instructorToAdd.privileges.removeSessionsPrivilegesForSection(sectionName);
        }
    }
    
    /**
     * Gets the sections that are special for the instructor to be added.
     * 
     * @param instructorToAdd          Instructor that will be added.
     * @param sectionNames             List of section names in the course.
     * @param isSectionSpecialMappings Mapping of names of sections to boolean values indicating if they are special.
     *                                     This will be modified within the method.
     * @return List of section group names with their associated special sections.
     */
    private HashMap<String, List<String>> getSectionsWithSpecialPrivilegesFromParameters(
            InstructorAttributes instructorToAdd, List<String> sectionNames,
            HashMap<String, Boolean> isSectionSpecialMappings) {
        HashMap<String, List<String>> specialSectionsInSectionGroups = new HashMap<String, List<String>>();
        if (instructorToAdd.role.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            getSectionsWithSpecialPrivilegesForCustomInstructor(sectionNames, isSectionSpecialMappings,
                                                                specialSectionsInSectionGroups);
        }
        return specialSectionsInSectionGroups;
    }
    
    /**
     * Gets the sections that are special for the custom instructor to be added.
     * Prereq: the added instructor must be given a custom role.
     * 
     * @param sectionNames                   List of section names in the course.
     * @param isSectionSpecialMappings       Mapping of names of sections to boolean values indicating if they are special.
     *                                           This will be modified within the method.
     * @param specialSectionsInSectionGroups Mapping of section group names to the special sections that they contain.
     *                                           This will be modified within the method.
     */
    private void getSectionsWithSpecialPrivilegesForCustomInstructor(List<String> sectionNames,
            HashMap<String, Boolean> isSectionSpecialMappings,
            HashMap<String, List<String>> specialSectionsInSectionGroups) {
        for (int i = 0; i < sectionNames.size(); i++) {
            String sectionGroupIsSetStr = getRequestParamValue("is"
                                                  + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + "set");
            boolean isSectionGroupSpecial = "true".equals(sectionGroupIsSetStr);
            
            for (int j = 0; j < sectionNames.size(); j++) {
                String sectionNameFromParam = getRequestParamValue(
                                                     Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i
                                                     + Const.ParamsNames.INSTRUCTOR_SECTION + j);
                boolean isSectionParamValid = sectionNameFromParam != null
                                              && isSectionSpecialMappings.containsKey(sectionNameFromParam);
                if (isSectionGroupSpecial && isSectionParamValid) {
                    markSectionAsSpecial(isSectionSpecialMappings, specialSectionsInSectionGroups, i, sectionNameFromParam);
                }
            }
        }
    }
    
    /**
     * Marks {@code sectionToMark} as special in the associated mappings.
     * 
     * @param isSectionSpecialMappings       Mapping of names of sections to boolean values indicating if they are special.
     *                                           This will be modified within the method.
     * @param specialSectionsInSectionGroups Mapping of section group names to the special sections that they contain.
     *                                           This will be modified within the method.
     * @param sectionGroupIndex              Index of the section group to be updated.
     * @param sectionToMark                  Section that will be marked as special.
     */
    private void markSectionAsSpecial(HashMap<String, Boolean> isSectionSpecialMappings,
            HashMap<String, List<String>> specialSectionsInSectionGroups, int sectionGroupIndex,
            String sectionToMark) {
        // indicate that section group covers the section
        // and mark that this section is special
        if (specialSectionsInSectionGroups.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionGroupIndex) == null) {
            specialSectionsInSectionGroups.put(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionGroupIndex,
                                new ArrayList<String>());
        }
        specialSectionsInSectionGroups.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionGroupIndex).add(sectionToMark);
        isSectionSpecialMappings.put(sectionToMark, true);
    }
    
    /**
     * Updates instructor privileges at section level by retrieving request parameters.
     * The parameters that are retrieved are based off {@code sectionGroupName}.
     * 
     * @param sectionGroupName              Name of the section group.
     * @param specialSectionsInSectionGroup Sections marked as special under the section group.
     * @param instructorToAdd               Instructor that will be added.
     *                                          This will be modified within the method.
     */
    private void updateInstructorPrivilegesForSectionInSectionLevel(String sectionGroupName,
            List<String> specialSectionsInSectionGroup, InstructorAttributes instructorToAdd) {
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + sectionGroupName) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + sectionGroupName) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + sectionGroupName) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + sectionGroupName) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + sectionGroupName) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + sectionGroupName) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + sectionGroupName) != null;
        
        for (String sectionName : specialSectionsInSectionGroup) {
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
    }
    
    /**
     * Updates instructor privileges at session level by retrieving request parameters.
     * The parameters that are retrieved are based off {@code sectionGroupName} and {@code feedbackNames}.
     * 
     * @param sectionGroupName              Name of the section group.
     * @param specialSectionsInSectionGroup Sections marked as special under the section group.
     * @param feedbackNames                 List of feedback names under the course.
     * @param instructorToAdd               Instructor that will be added.
     *                                          This will be modified within the method.
     */
    private void updateInstructorPrivilegesForSectionInSessionLevel(String sectionGroupName,
            List<String> specialSectionsInSectionGroup, List<String> feedbackNames,
            InstructorAttributes instructorToAdd) {
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                    + sectionGroupName + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionGroupName + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionGroupName + "feedback" + feedbackName) != null;
            
            for (String sectionName : specialSectionsInSectionGroup) {
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
            }
        }
    }
}
