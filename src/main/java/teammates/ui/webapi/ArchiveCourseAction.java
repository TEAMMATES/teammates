package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.ui.output.CourseArchiveData;
import teammates.ui.request.CourseArchiveRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Changes the archive status of a course.
 */
public class ArchiveCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(idOfCourseToArchive, userInfo.id),
                sqlLogic.getCourse(idOfCourseToArchive));
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String idOfCourseToArchive = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseArchiveRequest courseArchiveRequest = getAndValidateRequestBody(CourseArchiveRequest.class);

        boolean isArchive = courseArchiveRequest.getArchiveStatus();

        // TODO: Either implement archived functionality or remove this whole action

        return new JsonResult(new CourseArchiveData(idOfCourseToArchive, isArchive));
    }
}
