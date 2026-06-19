package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;

/**
 * Shared utilities for building session results output.
 */
final class SessionResultsUtils {

    private SessionResultsUtils() {
        // utility class
    }

    /**
     * Gets giver name of a response from the bundle.
     *
     * <p>Name is anonymized if the giver is not visible to the user.
     */
    static String getGiverNameOfResponse(UUID responseId,
            ResponseGiver responseGiver, SessionResultsBundle bundle) {
        if (bundle.isResponseGiverVisible(responseId)) {
            return responseGiver.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonGiverName(responseGiver);
        }
    }

    /**
     * Gets recipient name of a response from the bundle.
     *
     * <p>Name is anonymized if the recipient is not visible to the user.
     */
    static String getRecipientNameOfResponse(UUID responseId,
            ResponseRecipient responseRecipient, SessionResultsBundle bundle) {
        if (bundle.isResponseRecipientVisible(responseId, responseRecipient.getRecipientType())) {
            return responseRecipient.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonRecipientName(responseRecipient);
        }
    }

    /**
     * Builds instructor comment output for a list of instructor comments.
     */
    static List<ResponseInstructorCommentData> buildInstructorComments(
            List<ResponseInstructorComment> responseInstructorComments, SessionResultsBundle bundle) {
        List<ResponseInstructorCommentData> outputs = new ArrayList<>();

        for (ResponseInstructorComment comment : responseInstructorComments) {
            outputs.add(new ResponseInstructorCommentData(comment, bundle.isCommentGiverVisible(comment)));
        }

        return outputs;
    }
}
