package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;

/**
 * Action: Archives a course for an instructor.
 */
public class ArchiveCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToArchive, userInfo.id),
                logic.getCourse(idOfCourseToArchive));
    }

    @Override
    public ActionResult execute() {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String archiveStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ARCHIVE_STATUS);
        boolean isArchive = Boolean.parseBoolean(archiveStatus);
        try {
            // Set the archive status and status shown to user and admin
            logic.setArchiveStatusOfInstructor(userInfo.id, idOfCourseToArchive, isArchive);
            if (!isArchive) {
                return new JsonResult("The course has been unarchived.", HttpStatus.SC_OK);
            }
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult("The course has been archived. It will not appear in the home page any more. "
                + "You can access archived courses from the 'Courses' tab.\n"
                + "Go there to undo the archiving and bring the course back to the home page.", HttpStatus.SC_OK);
    }

}
