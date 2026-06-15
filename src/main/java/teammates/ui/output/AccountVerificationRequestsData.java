package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

/**
 * The API output format of a list of account verification requests.
 */
public class AccountVerificationRequestsData implements ApiOutput {

    private List<AccountVerificationRequestData> accountVerificationRequests;

    public AccountVerificationRequestsData() {
        this.accountVerificationRequests = new ArrayList<>();
    }

    public List<AccountVerificationRequestData> getAccountVerificationRequests() {
        return accountVerificationRequests;
    }

    public void setAccountVerificationRequests(List<AccountVerificationRequestData> accountVerificationRequests) {
        this.accountVerificationRequests = accountVerificationRequests;
    }
}
