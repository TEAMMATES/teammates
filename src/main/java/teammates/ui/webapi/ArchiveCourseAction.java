package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.ui.output.CourseArchiveData;
import teammates.ui.request.CourseArchiveRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Changes the archive status of a course.
 */
public class ArchiveCourseAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToArchive, userInfo.id),
                logic.getCourse(idOfCourseToArchive));
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseArchiveRequest courseArchiveRequest = getAndValidateRequestBody(CourseArchiveRequest.class);

        boolean isArchive = courseArchiveRequest.getArchiveStatus();
        try {
            // Set the archive status and status shown to user and admin
            logic.setArchiveStatusOfInstructor(userInfo.id, idOfCourseToArchive, isArchive);
        } catch (InvalidParametersException e) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new CourseArchiveData(idOfCourseToArchive, isArchive));
    }
}
