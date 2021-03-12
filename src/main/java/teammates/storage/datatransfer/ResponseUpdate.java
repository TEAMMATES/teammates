package teammates.storage.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

/**
 * A data class to contain old and new {@link FeedbackResponseAttributes} in an update.
 */
public class ResponseUpdate {

    private FeedbackResponseAttributes before;
    private FeedbackResponseAttributes after;

    public FeedbackResponseAttributes getAfter() {
        return after;
    }

    public FeedbackResponseAttributes getBefore() {
        return before;
    }

    public void setAfter(FeedbackResponseAttributes after) {
        this.after = after;
    }

    public void setBefore(FeedbackResponseAttributes before) {
        this.before = before;
    }

}
