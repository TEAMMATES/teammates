package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseArchiveAction extends Action {

    @Override
    protected ActionResult execute() {

        String idOfCourseToArchive = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(idOfCourseToArchive);
        String setArchiveStatus = getRequestParamValue(Const.ParamsNames.COURSE_ARCHIVE_STATUS);
        Assumption.assertNotNull(setArchiveStatus);
        boolean setArchive = Boolean.parseBoolean(setArchiveStatus);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(idOfCourseToArchive, account.googleId),
                logic.getCourse(idOfCourseToArchive));
        
        try {
            // Set the archive status and status shown to user and admin
            logic.setArchiveStatusOfInstructor(account.googleId, idOfCourseToArchive, setArchive);
            if (setArchive) {
                if (isRedirectedToHomePage()) {
                    statusToUser.add(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, idOfCourseToArchive));
                } else {
                    statusToUser.add(String.format(Const.StatusMessages.COURSE_ARCHIVED, idOfCourseToArchive));
                }
                statusToAdmin = "Course archived: " + idOfCourseToArchive;
            } else {  
                statusToUser.add(String.format(Const.StatusMessages.COURSE_UNARCHIVED, idOfCourseToArchive));
                statusToAdmin = "Course unarchived: " + idOfCourseToArchive;
            }
        } catch (Exception e) {
            setStatusForException(e);
        }

        if (isRedirectedToHomePage()) {
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        } else {
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        }
    }
    
    /**
     * Checks if the action is executed in homepage or 'Courses' pages based on its redirection
     */
    private boolean isRedirectedToHomePage() {
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        boolean isHomePageUrl = (nextUrl != null && nextUrl.equals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
        
        return isHomePageUrl;
    }
}
