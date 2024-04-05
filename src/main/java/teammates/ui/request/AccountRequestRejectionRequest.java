package teammates.ui.request;

import javax.annotation.Nullable;

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
        if (reasonBody != null) {
            assertTrue(reasonTitle != null, "If reason body is not null, reason title cannot be null");
        }
        if (reasonTitle != null) {
            assertTrue(reasonBody != null, "If reason title is not null, reason body cannot be null");
        }
    }

    public String getReasonTitle() {
        return this.reasonTitle;
    }

    public String getReasonBody() {
        return this.reasonBody;
    }

}
