package teammates.ui.output;

import javax.annotation.Nullable;

/**
 * The API output format for account request create request.
 */
public class AccountRequestCreateResponseData extends ApiOutput {
    private final String message;
    @Nullable
    private String joinLink;

    public AccountRequestCreateResponseData(String message) {
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
