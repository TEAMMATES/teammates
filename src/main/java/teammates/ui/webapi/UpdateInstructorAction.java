package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Edits an instructor in a course.
 */
public class UpdateInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(
                instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);

        Instructor updatedInstructor;
        try {
            updatedInstructor = sqlLogic.updateInstructorCascade(courseId, instructorRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (InstructorUpdateException e) {
            throw new InvalidOperationException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        sqlLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, updatedInstructor);

        InstructorData newInstructorData = new InstructorData(updatedInstructor);
        newInstructorData.setGoogleId(updatedInstructor.getGoogleId());

        taskQueuer.scheduleInstructorForSearchIndexing(updatedInstructor.getCourseId(), updatedInstructor.getEmail());

        return new JsonResult(newInstructorData);
    }

}