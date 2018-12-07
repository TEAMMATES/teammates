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
        data.init(regKey, email, courseId);

        setStatusToAdmin();
        // TODO: implement this in another way so there is no dependence on status messages from another page
        addFeedbackSubmissionStatusMessageIfNotRedirected();

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
     * <p><b>Note:</b> This is a leaky abstraction; does not detect if the page is actually redirected from
     * {@link FeedbackSubmissionEditSaveAction} but detects if there are status messages in the session.
     *
     * <p>This abstraction is implemented this way due to how status messages are implemented as follows:`
     * <ol>
     * <li>Every {@code *Action} adds the status messages ({@link Action#statusToUser}) to be shown the user.
     * <li>The {@code *Action} is executed.
     * <li>The base {@link Action} class will add the status messages to the session status messages
     *     ({@link Action#putStatusMessageToSession}).
     * <li>
     *     The action will be executed and returns a {@code *Result}.
     *     <ul>
     *     <li>For {@link AjaxResult}, the session status messages may be cleared after setting it
     *         in the {@link PageData} ({@link AjaxResult#clearStatusMessageForRequest(HttpServletRequest)}).
 *         <li>For {@link FileDownloadResult} or {@link ImageResult}, they do not do anything with status messages but
     *         the session status messages will <strong>continue to persist.</strong>
     *     <li>For {@link RedirectResult}, the session status messages will be used in the next page.
     *     <li>For {@link ShowPageResult}, the session status messages in the session is cleared after setting it
     *         in the {@link PageData} ({@link ShowPageResult#addStatusMessagesToPageData(HttpServletRequest)}.
     *     </ul>
     * </ol>
     *
     * <p>Therefore if the session unexpectedly contains another status message, this returns the wrong result.
     * For example, one reason this could happen is that when the user is concurrently accessing another page and the status
     * message is currently being added to the session.
     */
    private boolean isRedirectedFromFeedbackSubmissionEditSave() {
        // If there are status messages in the session means there is a redirect, or there is a previous AJAX request, or
        // the user is concurrently accessing another page that has just added status messages into the session.
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
