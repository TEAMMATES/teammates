package teammates.ui.request;

import java.util.Objects;

import jakarta.annotation.Nullable;

import teammates.common.util.SanitizationHelper;

/**
 * The request reasonBody for rejecting an account request.
 */
public class AccountRequestRejectionRequest extends BasicRequest {
    @Nullable
    private String reasonTitle;

    @Nullable
    private String reasonBody;

    public AccountRequestRejectionRequest(String reasonTitle, String reasonBody) {
        this.reasonTitle = SanitizationHelper.sanitizeTitle(reasonTitle);
        this.reasonBody = SanitizationHelper.sanitizeForRichText(reasonBody);
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        if (reasonBody == null || reasonTitle == null) {
            assertTrue(Objects.equals(reasonBody, reasonTitle),
                    "Both reason body and title need to be null to reject silently");
        }
    }

    public String getReasonTitle() {
        return this.reasonTitle;
    }

    public String getReasonBody() {
        return this.reasonBody;
    }

    /**
     * Returns true if both reason body and title are non-null.
     */
    public boolean checkHasReason() {
        return this.reasonBody != null && this.reasonTitle != null;
    }
}
