package teammates.storage.transaction.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

/**
 * A data class to contain old and new {@link FeedbackResponseAttributes} in an update.
 */
public class ResponseUpdate {

    private FeedbackResponseAttributes oldResponse;
    private FeedbackResponseAttributes newResponse;

    public FeedbackResponseAttributes getNewResponse() {
        return newResponse;
    }

    public FeedbackResponseAttributes getOldResponse() {
        return oldResponse;
    }

    public void setNewResponse(FeedbackResponseAttributes newResponse) {
        this.newResponse = newResponse;
    }

    public void setOldResponse(FeedbackResponseAttributes oldResponse) {
        this.oldResponse = oldResponse;
    }

}
