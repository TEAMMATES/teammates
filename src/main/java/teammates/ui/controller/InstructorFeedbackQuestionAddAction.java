package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackQuestionAddAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(instructorDetailForCourse,
                                    logic.getFeedbackSession(feedbackSessionName, courseId),
                                    false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData(instructorDetailForCourse.email);
        List<String> questionDetailsErrors = feedbackQuestion.getQuestionDetails().validateQuestionDetails(courseId);

        List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<>();

        for (String error : questionDetailsErrors) {
            questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
        }

        RedirectResult redirectResult = createRedirectResult(new PageData(account, sessionToken)
                .getInstructorFeedbackEditLink(courseId, feedbackSessionName));

        if (!questionDetailsErrors.isEmpty()) {
            statusToUser.addAll(questionDetailsErrorsMessages);
            isError = true;

            return redirectResult;
        }

        String err = validateQuestionGiverRecipientVisibility(feedbackQuestion);

        if (!err.isEmpty()) {
            statusToUser.add(new StatusMessage(err, StatusMessageColor.DANGER));
            isError = true;
        }

        try {
            logic.createFeedbackQuestion(feedbackQuestion);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Created Feedback Question for Feedback Session:<span class=\"bold\">("
                          + feedbackQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                          + feedbackQuestion.courseId + "]</span> created.<br>"
                          + "<span class=\"bold\">"
                          + feedbackQuestion.getQuestionDetails().getQuestionTypeDisplayName()
                          + ":</span> "
                          + SanitizationHelper.sanitizeForHtml(feedbackQuestion.getQuestionDetails().getQuestionText());
        } catch (InvalidParametersException e) {
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
            statusToAdmin = e.getMessage();
            isError = true;
        }
        return redirectResult;
    }

    private String validateQuestionGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestion) {
        return InstructorFeedbackQuestionEditAction.validateQuestionGiverRecipientVisibility(feedbackQuestion);
    }

    private FeedbackQuestionAttributes extractFeedbackQuestionData(String creatorEmail) {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        String feedbackQuestionGiverType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
        FeedbackParticipantType giverType = FeedbackParticipantType.valueOf(feedbackQuestionGiverType);

        String feedbackQuestionRecipientType =
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
        FeedbackParticipantType recipientType = FeedbackParticipantType.valueOf(feedbackQuestionRecipientType);

        String feedbackQuestionNumber = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        int questionNumber = Integer.parseInt(feedbackQuestionNumber);
        Assumption.assertTrue("Invalid question number", questionNumber >= 1);

        String numberOfEntityTypes = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);

        int numberOfEntitiesToGiveFeedbackTo;
        if ("custom".equals(numberOfEntityTypes)
                && (recipientType == FeedbackParticipantType.STUDENTS
                        || recipientType == FeedbackParticipantType.TEAMS)) {
            String numberOfEntities = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
            numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(numberOfEntities);
        } else {
            numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        }

        List<FeedbackParticipantType> showResponsesTo = FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));
        List<FeedbackParticipantType> showGiverNameTo = FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));
        List<FeedbackParticipantType> showRecipientNameTo =
                FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));

        String questionTypeInString = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        questionTypeInString = FeedbackQuestionType.standardizeIfConstSum(questionTypeInString);
        FeedbackQuestionType questionType = FeedbackQuestionType.valueOf(questionTypeInString);

        FeedbackQuestionDetails questionDetails = FeedbackQuestionDetails.createQuestionDetails(
                requestParameters, questionType);

        String questionDescription = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION);

        return FeedbackQuestionAttributes.builder()
                .withCreatorEmail(creatorEmail)
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withGiverType(giverType)
                .withRecipientType(recipientType)
                .withQuestionNumber(questionNumber)
                .withNumOfEntitiesToGiveFeedbackTo(numberOfEntitiesToGiveFeedbackTo)
                .withShowResponseTo(showResponsesTo)
                .withShowGiverNameTo(showGiverNameTo)
                .withShowRecipientNameTo(showRecipientNameTo)
                .withQuestionType(questionType)
                .withQuestionMetaData(questionDetails)
                .withQuestionDescription(new Text(questionDescription))
                .build();
    }

}
