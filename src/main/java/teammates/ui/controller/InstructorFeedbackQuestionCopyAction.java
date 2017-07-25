package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackQuestionCopyAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(instructorDetailForCourse,
                                    logic.getFeedbackSession(feedbackSessionName, courseId),
                                    false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        String instructorEmail = instructorDetailForCourse.email;

        try {
            int index = 0;
            String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + index);
            statusToAdmin = "";

            while (feedbackQuestionId != null) {
                FeedbackQuestionAttributes feedbackQuestion =
                        logic.copyFeedbackQuestion(feedbackQuestionId, feedbackSessionName, courseId, instructorEmail);

                index++;

                feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + index);

                statusToAdmin += "Created Feedback Question for Feedback Session:<span class=\"bold\">("
                        + feedbackQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                        + feedbackQuestion.courseId + "]</span> created.<br>"
                        + "<span class=\"bold\">"
                        + feedbackQuestion.getQuestionDetails().getQuestionTypeDisplayName()
                        + ":</span> "
                        + SanitizationHelper.sanitizeForHtml(feedbackQuestion.getQuestionDetails().getQuestionText());
            }

            if (index > 0) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_ADDED,
                                                   StatusMessageColor.SUCCESS));
            } else {
                statusToUser.add(new StatusMessage("No questions are indicated to be copied", StatusMessageColor.DANGER));
                isError = true;
            }
        } catch (InvalidParametersException e) {
            // This part is not tested because GateKeeper handles if this happens, would be
            // extremely difficult to replicate a situation whereby it gets past GateKeeper
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
            statusToAdmin = e.getMessage();
            isError = true;
        }

        return createRedirectResult(new PageData(account, sessionToken)
                .getInstructorFeedbackEditLink(courseId, feedbackSessionName));
    }
}
