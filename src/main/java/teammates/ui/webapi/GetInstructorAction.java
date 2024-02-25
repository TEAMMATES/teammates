package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;

/**
 * Get the information of an instructor inside a course.
 */
public class GetInstructorAction extends BasicFeedbackSubmissionAction {

    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            if (isCourseMigrated(courseId)) {
                Instructor instructor = getSqlInstructorOfCourseFromRequest(courseId);
                if (instructor == null) {
                    throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
                }
            } else {
                InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
                if (instructorAttributes == null) {
                    throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
                }
            }
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String intentString = getNonNullRequestParamValue(Const.ParamsNames.INTENT);
        Intent intent;
        try {
            intent = Intent.valueOf(intentString);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException("Invalid intent: " + intentString, e);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructorAttributes;

            switch (intent) {
            case INSTRUCTOR_SUBMISSION:
            case INSTRUCTOR_RESULT:
                instructorAttributes = getInstructorOfCourseFromRequest(courseId);
                break;
            case FULL_DETAIL:
                instructorAttributes = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                break;
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }

            if (instructorAttributes == null) {
                throw new EntityNotFoundException("Instructor could not be found for this course");
            }

            InstructorData instructorData = new InstructorData(instructorAttributes);
            instructorData.setInstitute(logic.getCourseInstitute(courseId));
            if (intent == Intent.FULL_DETAIL) {
                instructorData.setGoogleId(instructorAttributes.getGoogleId());
            }

            return new JsonResult(instructorData);
        }

        Instructor instructor;

        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            instructor = getSqlInstructorOfCourseFromRequest(courseId);
            break;
        case FULL_DETAIL:
            instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (instructor == null) {
            throw new EntityNotFoundException("Instructor could not be found for this course");
        }

        InstructorData instructorData = new InstructorData(instructor);
        if (intent == Intent.FULL_DETAIL) {
            instructorData.setGoogleId(instructor.getGoogleId());
        }

        return new JsonResult(instructorData);
    }

}
