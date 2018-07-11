package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackTemplateQuestionAddAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(instructorDetailForCourse,
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        List<FeedbackQuestionAttributes> templateQuestions = logic.populateFeedbackSessionTemplateQuestions("TEAMEVALUATION",
                courseId, feedbackSessionName, account.getEmail());
        try {
            int index = 0;
            String feedbackQuestionNum = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER + "-" + index);

            while (feedbackQuestionNum != null) {
                FeedbackQuestionAttributes feedbackQuestion = templateQuestions.get(
                        Integer.parseInt(feedbackQuestionNum) - 1);
                feedbackQuestion.questionNumber = -1;
                logic.createFeedbackQuestion(feedbackQuestion);

                index++;

                feedbackQuestionNum = getRequestParamValue(
                        Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER + "-" + index);
            }

            if (index > 0) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_ADDED,
                        StatusMessageColor.SUCCESS));
            } else {
                statusToUser.add(new StatusMessage("No template questions are indicated to be added",
                        StatusMessageColor.DANGER));
                isError = true;
            }
        } catch (InvalidParametersException e) {
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
            statusToAdmin = e.getMessage();
            isError = true;
        }

        return createRedirectResult(new PageData(account, sessionToken)
                .getInstructorFeedbackEditLink(courseId, feedbackSessionName));
    }
}
