package teammates.ui.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackQuestionEditAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                                    logic.getFeedbackSession(feedbackSessionName, courseId),
                                    false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        String editType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);

        try {
            if ("edit".equals(editType)) {
                String questionText = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
                Assumption.assertNotEmpty("Empty question text", questionText);

                FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();
                editQuestion(updatedQuestion);
            } else if ("delete".equals(editType)) {
                // branch not tested because if it's not edit or delete, Assumption.fail will cause test failure
                deleteQuestion();
            } else if ("duplicate".equals(editType)) {
                duplicateQuestion();
            } else {
                // Assumption.fails are not tested
                Assumption.fail("Invalid editType");
            }
        } catch (InvalidParametersException e) {
            // This part is not tested because GateKeeper handles if this happens, would be
            // extremely difficult to replicate a situation whereby it gets past GateKeeper
            setStatusForException(e);
        }

        return createRedirectResult(new PageData(account, sessionToken)
                                            .getInstructorFeedbackEditLink(courseId, feedbackSessionName));
    }

    private void duplicateQuestion() throws InvalidParametersException {
        String questionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String questionNumber = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);

        String instructorEmail = logic.getInstructorForGoogleId(courseId, account.googleId).email;

        FeedbackQuestionAttributes feedbackQuestion =
                logic.copyFeedbackQuestion(questionId, feedbackSessionName, courseId, instructorEmail);
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_DUPLICATED, StatusMessageColor.SUCCESS));
        statusToAdmin = "Feedback Question " + questionNumber + " for session: <span class=\"bold\">("
                + feedbackSessionName + ")</span> for Course <span class\"bold\">["
                + courseId + "]</span> copied to Feedback Question " + feedbackQuestion.questionNumber + ". <br>";
    }

    private void deleteQuestion() {
        String questionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String questionNumber = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);

        logic.deleteFeedbackQuestion(questionId);
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, StatusMessageColor.SUCCESS));
        statusToAdmin = "Feedback Question " + questionNumber + " for session:<span class=\"bold\">("
                + feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                + courseId + "]</span> deleted.<br>";
    }

    private void editQuestion(FeedbackQuestionAttributes updatedQuestion) throws InvalidParametersException,
                                                                                 EntityDoesNotExistException {
        String err = validateQuestionGiverRecipientVisibility(updatedQuestion);

        if (!err.isEmpty()) {
            statusToUser.add(new StatusMessage(err, StatusMessageColor.DANGER));
            isError = true;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        FeedbackQuestionDetails updatedQuestionDetails = updatedQuestion.getQuestionDetails();
        List<String> questionDetailsErrors = updatedQuestionDetails.validateQuestionDetails(courseId);
        List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<>();

        for (String error : questionDetailsErrors) {
            questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
        }

        if (questionDetailsErrors.isEmpty()) {
            logic.updateFeedbackQuestionNumber(updatedQuestion);

            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Question " + updatedQuestion.questionNumber
                          + " for session:<span class=\"bold\">("
                          + updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                          + updatedQuestion.courseId + "]</span> edited.<br>"
                          + "<span class=\"bold\">"
                          + updatedQuestionDetails.getQuestionTypeDisplayName() + ":</span> "
                          + SanitizationHelper.sanitizeForHtml(updatedQuestionDetails.getQuestionText());
        } else {
            statusToUser.addAll(questionDetailsErrorsMessages);
            isError = true;
        }
    }

    /**
     * Validates that the giver and recipient for the given FeedbackQuestionAttributes is valid for its question type.
     * Validates that the visibility for the given FeedbackQuestionAttributes is valid for its question type.
     *
     * @return error message detailing the error, or an empty string if valid.
     */
    public static String validateQuestionGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";

        FeedbackQuestionDetails questionDetails = null;
        Class<? extends FeedbackQuestionDetails> questionDetailsClass = feedbackQuestionAttributes
                                                                            .questionType.getQuestionDetailsClass();
        Constructor<? extends FeedbackQuestionDetails> questionDetailsClassConstructor;

        try {
            questionDetailsClassConstructor = questionDetailsClass.getConstructor();
            questionDetails = questionDetailsClassConstructor.newInstance();
            Method m = questionDetailsClass.getMethod("validateGiverRecipientVisibility",
                                                      FeedbackQuestionAttributes.class);
            errorMsg = (String) m.invoke(questionDetails, feedbackQuestionAttributes);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | InstantiationException e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
            // Assumption.fails are not tested
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for "
                            + feedbackQuestionAttributes.questionType.toString() + " question type.");
        }

        return errorMsg;
    }

    private FeedbackQuestionAttributes extractFeedbackQuestionData() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        // TODO thoroughly investigate when and why these parameters can be null
        // and check all possibilities in the tests
        // should only be null when deleting. might be good to separate the delete action from this class

        // When editing, usually the following fields are not null. If they are null somehow(edit from browser),
        // Then the field will not update and take on its old value.
        // When deleting, the following fields are null.
        // numofrecipients
        // questiontext
        // numofrecipientstype
        // recipienttype
        // receiverLeaderCheckbox
        // givertype

        // Can be null
        FeedbackParticipantType giverType = null;
        String giverTypeInString = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
        if (giverTypeInString != null) {
            giverType = FeedbackParticipantType.valueOf(giverTypeInString);
        }

        // Can be null
        FeedbackParticipantType recipientType = null;
        String recipientTypeInString = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
        if (recipientTypeInString != null) {
            recipientType = FeedbackParticipantType.valueOf(recipientTypeInString);
        }

        String feedbackQuestionNumber = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        int questionNumber = Integer.parseInt(feedbackQuestionNumber);
        Assumption.assertTrue("Invalid question number", questionNumber >= 1);

        // Can be null
        String nEntityTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);

        int numberOfEntitiesToGiveFeedbackTo;
        if (numberOfEntitiesIsUserDefined(recipientType, nEntityTypes)) {
            String nEntities = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
            numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(nEntities);
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
        FeedbackQuestionType questionType = FeedbackQuestionType.valueOf(questionTypeInString);

        // Can be null
        String questionText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        FeedbackQuestionDetails questionDetails = null;
        if (questionText != null && !questionText.isEmpty()) {
            questionDetails = FeedbackQuestionDetails.createQuestionDetails(
                    requestParameters, questionType);
        }

        String questionDescription = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION);

        return FeedbackQuestionAttributes.builder()
                .withFeedbackQuestionId(feedbackQuestionId)
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

    private static boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType, String nEntityTypes) {
        if (recipientType != FeedbackParticipantType.STUDENTS
                && recipientType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        return "custom".equals(nEntityTypes);
    }

}
