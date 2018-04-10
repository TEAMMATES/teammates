package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

/**
 * The action to copy selected instructor in the copy instructor modal to the course.
 */
public class InstructorCourseInstructorCopyAction extends Action {

    @Override
    protected ActionResult execute() {
        String thisCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes thisInstructor = logic.getInstructorForGoogleId(thisCourseId, account.googleId);

        gateKeeper.verifyAccessible(thisInstructor, logic.getCourse(thisCourseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        try {
            int index = 0;
            String instructorToBeCopiedEmail =
                    getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL + "-" + index);
            String instructorToBeCopiedFromCourse =
                    getRequestParamValue(Const.ParamsNames.COURSE_ID + "-" + index);
            statusToAdmin = "";

            while (instructorToBeCopiedEmail != null) {
                InstructorAttributes instructorCopied =
                        logic.copyInstructor(instructorToBeCopiedEmail, instructorToBeCopiedFromCourse, thisCourseId);
                index++;

                taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                        loggedInUser.googleId, instructorToBeCopiedEmail, thisCourseId);

                statusToAdmin += "Added Instructor for Course: <span class=\"bold\">["
                        + instructorCopied.getCourseId() + "]</span>.<br>"
                        + "<span class=\"bold\">"
                        + instructorCopied.getEmail()
                        + ":</span> "
                        + instructorCopied.getName();

                statusToUser.add(new StatusMessage(
                                    String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                                            instructorCopied.getName(),
                                            instructorToBeCopiedEmail),
                                    StatusMessageColor.SUCCESS));

                instructorToBeCopiedEmail =
                        getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL + "-" + index);
                instructorToBeCopiedFromCourse =
                        getRequestParamValue(Const.ParamsNames.COURSE_ID + "-" + index);
            }

            if (index == 0) {
                statusToUser.add(
                        new StatusMessage("No instructors are indicated to be copied", StatusMessageColor.DANGER));
                isError = true;
            }
        } catch (InvalidParametersException ipe) {
            setStatusForException(ipe);
        } catch (EntityAlreadyExistsException eaee) {
            setStatusForException(eaee, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        }

        return createRedirectResult(new PageData(account, sessionToken).getInstructorCourseEditLink(thisCourseId));
    }
}
