package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

import teammates.storage.entity.AccountVerificationRequest;

/**
 * The API output format of a list of account verification requests.
 */
public class AccountVerificationRequestsData implements ApiOutput {

    private List<AccountVerificationRequestData> accountVerificationRequests;

    public AccountVerificationRequestsData() {
        this.accountVerificationRequests = new ArrayList<>();
    }

    public AccountVerificationRequestsData(List<AccountVerificationRequest> accountVerificationRequests) {
        this.accountVerificationRequests = accountVerificationRequests.stream()
                .map(AccountVerificationRequestData::new)
                .toList();
    }

    public List<AccountVerificationRequestData> getAccountVerificationRequests() {
        return accountVerificationRequests;
    }

    public void setAccountVerificationRequests(List<AccountVerificationRequestData> accountVerificationRequests) {
        this.accountVerificationRequests = accountVerificationRequests;
    }
}
