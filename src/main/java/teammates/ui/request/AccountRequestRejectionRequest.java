package teammates.ui.request;

import javax.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
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
        // No validation
    }

    public String getReasonTitle() {
        return this.reasonTitle;
    }

    public String getReasonBody() {
        return this.reasonBody;
    }

}
