package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;

public abstract class FeedbackSubmissionEditPageAction extends Action {
    protected String courseId;
    protected String feedbackSessionName;
    protected FeedbackSubmissionEditPageData data;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        if (!isSpecificUserJoinedCourse()) {
            return createPleaseJoinCourseResponse(courseId);
        }

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        if (feedbackSession == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_DELETED_NO_ACCESS,
                                               StatusMessageColor.WARNING));

            return createSpecificRedirectResult();
        }

        verifyAccessibleForSpecificUser(feedbackSession);

        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        String userEmailForCourse = getUserEmailForCourse();
        data = new FeedbackSubmissionEditPageData(account, student, sessionToken);
        data.bundle = getDataBundle(userEmailForCourse);

        data.setSessionOpenForSubmission(isSessionOpenForSpecificUser(data.bundle.feedbackSession));

        setStatusToAdmin();

        addFeedbackSubmissionStatusMessageIfNotRedirected();
        data.init(regKey, email, courseId);

        return createSpecificShowPageResult();
    }

    private void addFeedbackSubmissionStatusMessageIfNotRedirected() {
        // Add status messages only if accessing the page directly, otherwise use only status messages from original page
        if (isRedirectedFromFeedbackSubmissionEditSave()) {
            return;
        }

        if (data.isSessionOpenForSubmission()) {
            // Side effect of only adding status message when accessing the page directly means that the status message
            // following is only shown the first time and not when submitting
            StatusMessage statusMessage =
                    new StatusMessage(Const.StatusMessages.FEEDBACK_SUBMISSIONS_CAN_SUBMIT_PARTIAL_ANSWER,
                            StatusMessageColor.INFO);
            statusToUser.add(statusMessage);
        } else {
            // Side effect of only adding status message when accessing the page directly means that the status message
            // following will not show double submission statuses, such as the following (non-exhaustive):
            // - FEEDBACK_SUBMISSIONS_NOT_OPEN from FeedbackSubmissionEditSaveAction followed by
            // FEEDBACK_SUBMISSIONS_NOT_OPEN from FeedbackSubmissionEditPageAction
            // - FEEDBACK_RESPONSES_SAVED from FeedbackSubmissionEditSaveAction followed by
            // FEEDBACK_SUBMISSIONS_NOT_OPEN from FeedbackSubmissionEditPageAction
            StatusMessage statusMessage = new StatusMessage(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN,
                    StatusMessageColor.WARNING);
            statusToUser.add(statusMessage);
        }
    }

    /**
     * Returns if the page is redirected from other pages that are mapped to subclasses of
     * {@link FeedbackSubmissionEditSaveAction}.
     *
     * <p><b>Note:</b> This is a leaky abstraction; does not detect if the page is redirected but depends on the way
     * status messages to user are passed through the session.
     *
     * <p>The way status messages to user are implemented right now are as follows:<br>
     * 1. Every {@code *Action} adds the status messages ({@link Action#statusToUser}) to be shown the user.<br>
     * 2. The {@code *Action} is executed.<br>
     * 3. The base {@link Action} class will add the status messages to the session status messages
     * ({@link Action#putStatusMessageToSession}).<br>
     * 4. If the execution returns an {@link AjaxResult}, the status messages in the session may be cleared after setting it
     * in the {@link PageData} ({@link AjaxResult#clearStatusMessageForRequest(HttpServletRequest)}).<br>
     * 4. If the execution returns a {@link FileDownloadResult} or {@link ImageResult}, they do not do anything with status
     * messages but the session status messages will <strong>continue to persist.</strong><br>
     * 4. If the execution returns a {@link RedirectResult}, the status messages in the session will be passed along to the
     * next page.<br>
     * 4. If the execution returns a {@link ShowPageResult}, the status messages in the session is cleared after setting it
     * in the {@link PageData} ({@link ShowPageResult#addStatusMessagesToPageData(HttpServletRequest)}.<br>
     *
     * <p>Therefore if the session unexpectedly contains another status message, this returns the wrong result.
     * For example, one reason this could happen is that when the user is concurrently accessing another page and the status
     * message is currently being inserted into the session.
     */
    private boolean isRedirectedFromFeedbackSubmissionEditSave() {
        return session.getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST) != null;
    }

    protected abstract boolean isSpecificUserJoinedCourse();

    protected abstract void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa);

    protected abstract String getUserEmailForCourse();

    protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session);

    protected abstract void setStatusToAdmin();

    protected abstract ShowPageResult createSpecificShowPageResult();

    protected abstract RedirectResult createSpecificRedirectResult() throws EntityDoesNotExistException;
}
