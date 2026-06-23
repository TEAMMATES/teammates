package teammates.ui.request;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountVerificationRequestRejectionType;
import teammates.common.util.FieldValidator;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request body for rejecting an account verification request.
 */
public class AccountVerificationRequestRejectionRequest extends BasicRequest {
    private AccountVerificationRequestRejectionType rejectionType;

    @Nullable
    private String additionalComments;

    private AccountVerificationRequestRejectionRequest() {
        // for Jackson deserialization
    }

    public AccountVerificationRequestRejectionRequest(
            AccountVerificationRequestRejectionType rejectionType, String additionalComments) {
        this.rejectionType = rejectionType;
        this.additionalComments = additionalComments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(rejectionType != null, "Rejection type cannot be null");
        if (additionalComments != null) {
            validateTrue(FieldValidator.getInvalidityInfoForRejectionAdditionalComments(additionalComments).isEmpty(),
                    "Additional comments " + FieldValidator.REASON_TOO_LONG
                            + " (max " + FieldValidator.REJECTION_ADDITIONAL_COMMENTS_MAX_LENGTH + " characters)");
        }
    }

    public AccountVerificationRequestRejectionType getRejectionType() {
        return this.rejectionType;
    }

    @Nullable
    public String getAdditionalComments() {
        return this.additionalComments;
    }
}
