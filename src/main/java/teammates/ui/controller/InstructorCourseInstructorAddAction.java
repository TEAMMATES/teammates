package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.api.GateKeeper;

/**
 * Action: add another instructor to a course that already exists
 */
public class InstructorCourseInstructorAddAction extends InstructorCourseInstructorAbstractAction {

    @Override
    protected ActionResult execute() {

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
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(courseId, instructorEmail);

            statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                                                             instructorName, instructorEmail),
                                               StatusMessageColor.SUCCESS));
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
     * @param courseId        Id of the course the instructor is being added to.
     * @param instructorName  Name of the instructor.
     * @param instructorEmail Email of the instructor.
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
        
        InstructorAttributes instructorToAdd = createInstructorWithBasicAttributes(courseId, instructorName,
                instructorEmail, instructorRole, isDisplayedToStudents, displayedName);
        
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
    private InstructorAttributes createInstructorWithBasicAttributes(String courseId, String instructorName,
            String instructorEmail, String instructorRole,
            boolean isDisplayedToStudents, String displayedName) {
        String instrName = Sanitizer.sanitizeName(instructorName);
        String instrEmail = Sanitizer.sanitizeEmail(instructorEmail);
        String instrRole = Sanitizer.sanitizeName(instructorRole);
        String instrDisplayedName = Sanitizer.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);
        
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, instrName, instrEmail,
                instrRole, isDisplayedToStudents, instrDisplayedName, privileges);
        
        return instructorToAdd;
    }
}
