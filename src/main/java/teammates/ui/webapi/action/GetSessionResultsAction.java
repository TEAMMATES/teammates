package teammates.ui.webapi.action;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.SessionResultsData;
import teammates.ui.webapi.request.Intent;

/**
 * Gets feedback session results including statistics where necessary.
 */
public class GetSessionResultsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes fs = logic.getFeedbackSession(feedbackSessionName, courseId);

        if (fs == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback session is not found"));
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor = getInstructor(courseId);
            gateKeeper.verifyAccessible(instructor, fs);
            break;
        case STUDENT_RESULT:
            StudentAttributes student = getStudent(courseId);

            gateKeeper.verifyAccessible(student, fs);

            if (!fs.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.");
            }
            break;
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    private StudentAttributes getStudent(String courseId) {
        if (userInfo == null) {
            String regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
            if (StringHelper.isEmpty(regkey)) {
                throw new UnauthorizedAccessException("Student account is required to access this resource.");
            } else {
                return logic.getStudentForRegistrationKey(regkey);
            }
        }
        return logic.getStudentForGoogleId(courseId, userInfo.id);
    }

    private InstructorAttributes getInstructor(String courseId) {
        if (userInfo == null) {
            throw new UnauthorizedAccessException("Instructor account is required to access this resource");
        } else {
            return logic.getInstructorForGoogleId(courseId, userInfo.id);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        // Allow additional filter by question ID (equivalent to question number) and section name
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);

        FeedbackSessionResultsBundle bundle;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

            try {
                // TODO optimize the logic layer to get rid of functions that are no longer necessary
                if (questionId == null && selectedSection != null) {
                    bundle = logic.getFeedbackSessionResultsForInstructorInSection(
                            feedbackSessionName, courseId, instructor.email, selectedSection, SectionDetail.EITHER);
                } else if (selectedSection == null && questionId != null) {
                    bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId,
                            instructor.email, questionId);
                } else {
                    throw new InvalidHttpParameterException("Should provide either questionId or selectedSection");
                }
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }

            return new JsonResult(new SessionResultsData(bundle, instructor));
        case STUDENT_RESULT:
            // Question number and section name filters are not applied here
            StudentAttributes student = getStudent(courseId);

            try {
                bundle = logic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, student.email);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }

            if (bundle.isStudentHasSomethingNewToSee(student)) {
                // TODO do something
            } else {
                // TODO do something else
            }

            return new JsonResult(new SessionResultsData(bundle, student));
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}
