package teammates.ui.output;

import javax.annotation.Nullable;

/**
 * The API output format for account request status update request.
 */
public class AccountRequestStatusUpdateResponseData extends ApiOutput {

    private AccountRequestData accountRequest;
    @Nullable
    private String joinLink;

    public void setAccountRequest(AccountRequestData accountRequest) {
        this.accountRequest = accountRequest;
    }

    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }

    public AccountRequestData getAccountRequest() {
        return accountRequest;
    }

    public String getJoinLink() {
        return joinLink;
    }

}
