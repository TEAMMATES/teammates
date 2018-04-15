package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorCourseInstructorEditSaveAction extends InstructorCourseInstructorAbstractAction {

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
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        InstructorAttributes instructorToEdit =
                extractUpdatedInstructor(courseId, instructorId, instructorName, instructorEmail);
        updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);

        try {
            if (instructorId == null) {
                logic.updateInstructorByEmail(instructorEmail, instructorToEdit);
            } else {
                logic.updateInstructorByGoogleId(instructorId, instructorToEdit);
            }

            statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, instructorName),
                                               StatusMessageColor.SUCCESS));
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

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                             This may be modified within the method.
     */
    private void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrWithModifyInstructorPrivilege = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                                                   && instrWithModifyInstructorPrivilege != null
                                                   && (!instrWithModifyInstructorPrivilege.isRegistered()
                                                           || instrWithModifyInstructorPrivilege.googleId
                                                                     .equals(instructorToEdit.googleId));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        }
    }

    /**
     * Creates a new instructor representing the updated instructor with all information filled in,
     * using request parameters.
     * This includes basic information as well as custom privileges (if applicable).
     *
     * @param courseId        Id of the course the instructor is being added to.
     * @param instructorId    Id of the instructor.
     * @param instructorName  Name of the instructor.
     * @param instructorEmail Email of the instructor.
     * @return The updated instructor with all relevant info filled in.
     */
    private InstructorAttributes extractUpdatedInstructor(String courseId, String instructorId,
                                                          String instructorName, String instructorEmail) {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.INSTRUCTOR_ROLE_NAME, instructorRole);
        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        if (displayedName == null || displayedName.isEmpty()) {
            displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        }
        instructorRole = SanitizationHelper.sanitizeName(instructorRole);
        displayedName = SanitizationHelper.sanitizeName(displayedName);

        InstructorAttributes instructorToEdit =
                updateBasicInstructorAttributes(courseId, instructorId, instructorName, instructorEmail,
                                                instructorRole, isDisplayedToStudents, displayedName);

        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToEdit);
        }

        updateInstructorWithSectionLevelPrivileges(courseId, instructorToEdit);

        instructorToEdit.privileges.validatePrivileges();

        return instructorToEdit;
    }

    /**
     * Edits an existing instructor's basic information.
     * This consists of everything apart from custom privileges.
     *
     * @param courseId              Id of the course the instructor is being added to.
     * @param instructorId          Id of the instructor.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to students.
     * @param displayedName         Name to be visible to students.
     *                                  Should not be {@code null} even if {@code isDisplayedToStudents} is false.
     * @return The edited instructor with updated basic info, and its old custom privileges (if applicable)
     */
    private InstructorAttributes updateBasicInstructorAttributes(String courseId,
            String instructorId, String instructorName, String instructorEmail,
            String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        InstructorAttributes instructorToEdit = null;
        if (instructorId == null) {
            instructorToEdit = logic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        }
        instructorToEdit.name = SanitizationHelper.sanitizeName(instructorName);
        instructorToEdit.email = SanitizationHelper.sanitizeEmail(instructorEmail);
        instructorToEdit.role = SanitizationHelper.sanitizeName(instructorRole);
        instructorToEdit.displayedName = SanitizationHelper.sanitizeName(displayedName);
        instructorToEdit.isDisplayedToStudents = isDisplayedToStudents;
        instructorToEdit.privileges = new InstructorPrivileges(instructorToEdit.role);

        return instructorToEdit;
    }
}
