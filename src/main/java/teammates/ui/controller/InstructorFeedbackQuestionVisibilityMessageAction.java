package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackQuestionVisibilityMessagePageData;

public class InstructorFeedbackQuestionVisibilityMessageAction extends Action {
    @Override
    protected ActionResult execute() {
        FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData(account.email);

        List<String> message = feedbackQuestion.getVisibilityMessage();

        InstructorFeedbackQuestionVisibilityMessagePageData data =
                new InstructorFeedbackQuestionVisibilityMessagePageData(account, sessionToken);
        data.visibilityMessage = message;

        return createAjaxResult(data);
    }

    private FeedbackQuestionAttributes extractFeedbackQuestionData(String creatorEmail) {
        String feedbackQuestionGiverType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);

        FeedbackParticipantType giverType = FeedbackParticipantType.valueOf(feedbackQuestionGiverType);

        String feedbackQuestionRecipientType =
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);

        FeedbackParticipantType recipientType = FeedbackParticipantType.valueOf(feedbackQuestionRecipientType);

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

        return FeedbackQuestionAttributes.builder()
                .withCreatorEmail(creatorEmail)
                .withGiverType(giverType)
                .withRecipientType(recipientType)
                .withNumOfEntitiesToGiveFeedbackTo(numberOfEntitiesToGiveFeedbackTo)
                .withShowResponseTo(showResponsesTo)
                .withShowGiverNameTo(showGiverNameTo)
                .withShowRecipientNameTo(showRecipientNameTo)
                .withQuestionType(questionType)
                .build();
    }

}
