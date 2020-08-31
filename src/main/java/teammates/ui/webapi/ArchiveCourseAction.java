package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.CourseArchiveData;
import teammates.ui.request.CourseArchiveRequest;

/**
 * Changes the archive status of a course.
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

        CourseArchiveRequest courseArchiveRequest = getAndValidateRequestBody(CourseArchiveRequest.class);

        boolean isArchive = courseArchiveRequest.getArchiveStatus();
        try {
            // Set the archive status and status shown to user and admin
            logic.setArchiveStatusOfInstructor(userInfo.id, idOfCourseToArchive, isArchive);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new CourseArchiveData(idOfCourseToArchive, isArchive));
    }
}
