package teammates.ui.output;

import javax.annotation.Nullable;

/**
 * The API output format for account request create request.
 */
public class AccountRequestCreateResponseData extends ApiOutput {
    private String message;
    @Nullable
    private String joinLink;

    public AccountRequestCreateResponseData() {}

    public void setMessage(String message) {
        this.message = message;
    }

    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }

    public String getMessage() {
        return message;
    }

    public String getJoinLink() {
        return joinLink;
    }
}
