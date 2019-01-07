package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackResultsDownloadAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        boolean isMissingResponsesShown = getRequestParamAsBoolean(
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES);
        boolean isStatsShown = getRequestParamAsBoolean(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String questionNumber = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        // Parameter is used to throw the exceeding range exception in test
        String simulateExcessDataForTesting = getRequestParamValue("simulateExcessDataForTesting");

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;

        gateKeeper.verifyAccessible(instructor, session, !isCreatorOnly);

        String fileContent;
        String fileName;

        try {
            if ("true".equals(simulateExcessDataForTesting)) {
                throw new ExceedingRangeException("This session has more responses than that can be downloaded at one go.");
            }

            String questionName = "";
            if (questionNumber != null) {
                questionName = "_question" + questionNumber;
            }

            if (section == null || "All".equals(section)) {
                fileContent = logic.getFeedbackSessionResultSummaryAsCsv(
                        courseId, feedbackSessionName, instructor.email,
                        isMissingResponsesShown, isStatsShown, questionId);

                fileName = courseId + "_" + feedbackSessionName + questionName;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName
                              + " in Course " + courseId + " was downloaded";
            } else {
                fileContent = logic.getFeedbackSessionResultSummaryInSectionAsCsv(
                        courseId, feedbackSessionName, instructor.email, section,
                        questionId, isMissingResponsesShown, isStatsShown);
                fileName = courseId + "_" + feedbackSessionName + "_" + section + questionName;
                statusToAdmin = "Summary data for Feedback Session " + feedbackSessionName
                              + " in Course " + courseId + " within " + section + " was downloaded";
            }
        } catch (ExceedingRangeException e) {
            // not tested as the test file is not large enough to reach this catch block
            statusToUser.add(new StatusMessage("This session has more responses than that can be downloaded in one go. "
                        + "Please download responses for "
                        + (questionNumber == null ? "one question at a time instead. "
                        + "To download responses for a specific question, click on the corresponding question number."
                                                 : "section instead."),
                                                 StatusMessageColor.DANGER));
            isError = true;
            RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE);
            result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
            result.addResponseParam(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            return result;
        }

        return createFileDownloadResult(fileName, fileContent);
    }

}
