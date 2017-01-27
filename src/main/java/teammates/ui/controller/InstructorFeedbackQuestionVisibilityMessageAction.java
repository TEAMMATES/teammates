package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackQuestionVisibilityMessagePageData;

public class InstructorFeedbackQuestionVisibilityMessageAction extends Action {
    @Override
    protected ActionResult execute() {
        FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData(account.email);

        List<String> message = feedbackQuestion.getVisibilityMessage();

        InstructorFeedbackQuestionVisibilityMessagePageData data =
                new InstructorFeedbackQuestionVisibilityMessagePageData(account);
        data.visibilityMessage = message;

        return createAjaxResult(data);
    }

    private FeedbackQuestionAttributes extractFeedbackQuestionData(String creatorEmail) {
        FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();

        newQuestion.creatorEmail = creatorEmail;

        String feedbackQuestionGiverType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);

        Assumption.assertNotNull("Null giver type", feedbackQuestionGiverType);

        newQuestion.giverType = FeedbackParticipantType.valueOf(feedbackQuestionGiverType);

        String feedbackQuestionRecipientType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);

        Assumption.assertNotNull("Null recipient type", feedbackQuestionRecipientType);

        newQuestion.recipientType = FeedbackParticipantType.valueOf(feedbackQuestionRecipientType);

        String numberOfEntityTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);

        Assumption.assertNotNull("Null number of entity types", numberOfEntityTypes);

        if ("custom".equals(numberOfEntityTypes)
                && (newQuestion.recipientType == FeedbackParticipantType.STUDENTS
                        || newQuestion.recipientType == FeedbackParticipantType.TEAMS)) {
            String numberOfEntities = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);

            Assumption.assertNotNull("Null number of entities for custom entity number", numberOfEntities);

            newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(numberOfEntities);
        } else {
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        }

        newQuestion.showResponsesTo = getParticipantListFromParams(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));
        newQuestion.showGiverNameTo = getParticipantListFromParams(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));
        newQuestion.showRecipientNameTo = getParticipantListFromParams(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));

        String questionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        Assumption.assertNotNull("Null question type", questionType);
        questionType = FeedbackQuestionType.standardizeIfConstSum(questionType);

        newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);
        newQuestion.removeIrrelevantVisibilityOptions();

        return newQuestion;
    }

    private static List<FeedbackParticipantType> getParticipantListFromParams(String participantListParam) {
        List<FeedbackParticipantType> participantList = new ArrayList<FeedbackParticipantType>();

        if (participantListParam == null || participantListParam.isEmpty()) {
            // null not covered, even when set to null, action receives it as an empty string
            return participantList;
        }

        String[] splitString = participantListParam.split(",");

        for (String str : splitString) {
            participantList.add(FeedbackParticipantType.valueOf(str));
        }

        return participantList;
    }
}
