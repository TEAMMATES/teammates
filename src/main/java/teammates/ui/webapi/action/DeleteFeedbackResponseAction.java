package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.request.Intent;

/**
 * Delete a feedback response.
 */
public class DeleteFeedbackResponseAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("The feedback response does not exist."));
        }
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(feedbackResponse.feedbackQuestionId);
        FeedbackSessionAttributes feedbackSession =
                logic.getFeedbackSession(feedbackResponse.feedbackSessionName, feedbackResponse.courseId);

        verifyInstructorCanSeeQuestionIfInModeration(feedbackQuestion);
        verifySessionOpenExceptForModeration(feedbackSession);
        verifyNotPreview();

        // verify user is the giver of the response
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            gateKeeper.verifyAnswerableForStudent(feedbackQuestion);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (feedbackQuestion.getGiverType() == FeedbackParticipantType.TEAMS) {
                if (!studentAttributes.getTeam().equals(feedbackResponse.giver)) {
                    List<StudentAttributes> teamMember =
                            logic.getStudentsForTeam(studentAttributes.getTeam(), studentAttributes.getCourse());
                    boolean isTeamMemberGiven =
                            teamMember.stream().anyMatch(student -> student.getEmail().equals(feedbackResponse.giver));
                    if (!isTeamMemberGiven) {
                        throw new UnauthorizedAccessException("You are not the authenticated team member of the response");
                    }
                }
            } else {
                if (!studentAttributes.getEmail().equals(feedbackResponse.giver)) {
                    throw new UnauthorizedAccessException("You are not the authenticated giver of the response");
                }
            }
            break;
        case INSTRUCTOR_SUBMISSION:
            gateKeeper.verifyAnswerableForInstructor(feedbackQuestion);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackQuestion.getCourseId());
            if (!instructorAttributes.getEmail().equals(feedbackResponse.giver)) {
                throw new UnauthorizedAccessException("You are not the authenticated giver of the response");
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        String feedbackResponseId;
        try {
            feedbackResponseId = StringHelper.decrypt(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe.getMessage(), ipe);
        }
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);

        logic.deleteFeedbackResponseCascade(feedbackResponse.getId());

        return new JsonResult("Feedback response deleted");
    }

}
