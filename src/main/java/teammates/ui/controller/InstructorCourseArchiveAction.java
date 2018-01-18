package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorCourseArchiveAction extends Action {

    @Override
    protected ActionResult execute() {

        String idOfCourseToArchive = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, idOfCourseToArchive);
        String archiveStatus = getRequestParamValue(Const.ParamsNames.COURSE_ARCHIVE_STATUS);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ARCHIVE_STATUS, archiveStatus);
        boolean isArchive = Boolean.parseBoolean(archiveStatus);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToArchive, account.googleId),
                                    logic.getCourse(idOfCourseToArchive));

        try {

            // Set the archive status and status shown to user and admin
            logic.setArchiveStatusOfInstructor(account.googleId, idOfCourseToArchive, isArchive);
            if (isArchive) {
                if (isRedirectedToHomePage()) {
                    statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE,
                                                                       idOfCourseToArchive), StatusMessageColor.SUCCESS));
                } else {
                    statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_ARCHIVED,
                                                                       idOfCourseToArchive), StatusMessageColor.SUCCESS));
                }
                statusToAdmin = "Course archived: " + idOfCourseToArchive;
            } else {
                statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_UNARCHIVED,
                                                                       idOfCourseToArchive), StatusMessageColor.SUCCESS));
                statusToAdmin = "Course unarchived: " + idOfCourseToArchive;
            }
        } catch (Exception e) {
            setStatusForException(e);
        }

        if (isRedirectedToHomePage()) {
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
    }

    /**
     * Checks if the action is executed in homepage or 'Courses' pages based on its redirection.
     */
    private boolean isRedirectedToHomePage() {
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        return nextUrl != null && nextUrl.equals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }
}
