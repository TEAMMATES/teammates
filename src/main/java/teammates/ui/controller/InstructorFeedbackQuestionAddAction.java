package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);

        new GateKeeper().verifyAccessible(instructorDetailForCourse,
                                          logic.getFeedbackSession(feedbackSessionName, courseId),
                                          false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData(requestParameters,
                                                                                  instructorDetailForCourse.email);
        List<String> questionDetailsErrors = feedbackQuestion.getQuestionDetails().validateQuestionDetails();
        
        List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<StatusMessage>();
        
        for (String error : questionDetailsErrors) {
            questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
        }

        // if error is not empty not tested as extractFeedbackQuestionData method above uses Assumptions to cover it
        if (!questionDetailsErrors.isEmpty()) {
            statusToUser.addAll(questionDetailsErrorsMessages);
            isError = true;
        } else {
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
                                + ":</span> " + feedbackQuestion.getQuestionDetails().questionText;
            } catch (InvalidParametersException e) {
                statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
                statusToAdmin = e.getMessage();
                isError = true;
            }
        }
        return createRedirectResult(new PageData(account).getInstructorFeedbackEditLink(courseId, feedbackSessionName));
    }

    private String validateQuestionGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestion) {
        return InstructorFeedbackQuestionEditAction.validateQuestionGiverRecipientVisibility(feedbackQuestion);
    }

    private static FeedbackQuestionAttributes extractFeedbackQuestionData(Map<String, String[]> requestParameters,
                                                                          String creatorEmail) {
        FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();

        newQuestion.creatorEmail = creatorEmail;

        newQuestion.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("Null course id", newQuestion.courseId);

        newQuestion.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(
                                            requestParameters, Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("Null feedback session name", newQuestion.feedbackSessionName);

        String feedbackQuestionGiverType = HttpRequestHelper.getValueFromParamMap(
                                            requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
        Assumption.assertNotNull("Null giver type", feedbackQuestionGiverType);
        newQuestion.giverType = FeedbackParticipantType.valueOf(feedbackQuestionGiverType);

        String feedbackQuestionRecipientType = HttpRequestHelper.getValueFromParamMap(
                                                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
        Assumption.assertNotNull("Null recipient type", feedbackQuestionRecipientType);
        newQuestion.recipientType = FeedbackParticipantType.valueOf(feedbackQuestionRecipientType);

        String feedbackQuestionNumber = HttpRequestHelper.getValueFromParamMap(
                                            requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        Assumption.assertNotNull("Null question number", feedbackQuestionNumber);
        newQuestion.questionNumber = Integer.parseInt(feedbackQuestionNumber);
        Assumption.assertTrue("Invalid question number", newQuestion.questionNumber >= 1);

        String numberOfEntityTypes = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
        Assumption.assertNotNull("Null number of entity types", numberOfEntityTypes);

        if (numberOfEntityTypes.equals("custom")
            && (newQuestion.recipientType == FeedbackParticipantType.STUDENTS
                || newQuestion.recipientType == FeedbackParticipantType.TEAMS)) {
            String numberOfEntities = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
            Assumption.assertNotNull("Null number of entities for custom entity number", numberOfEntities);

            newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(numberOfEntities);
        } else {
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        }

        newQuestion.showResponsesTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));
        newQuestion.showGiverNameTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));
        newQuestion.showRecipientNameTo = getParticipantListFromParams(
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));

        String questionType = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        Assumption.assertNotNull("Null question type", questionType);
        questionType = FeedbackQuestionType.standardizeIfConstSum(questionType);
        newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);

        FeedbackQuestionDetails questionDetails = FeedbackQuestionDetails.createQuestionDetails(
                requestParameters, newQuestion.questionType);
        newQuestion.setQuestionDetails(questionDetails);

        return newQuestion;
    }

    private static List<FeedbackParticipantType> getParticipantListFromParams(String participantListParam) {
        List<FeedbackParticipantType> participantList = new ArrayList<FeedbackParticipantType>();

        if (participantListParam == null || participantListParam.isEmpty()) {
            return participantList;
        }

        String[] splitString = participantListParam.split(",");

        for (String str : splitString) {
            participantList.add(FeedbackParticipantType.valueOf(str));
        }

        return participantList;
    }
}
