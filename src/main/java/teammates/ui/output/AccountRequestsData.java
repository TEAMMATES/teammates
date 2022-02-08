package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * The API output format of a list of account requests.
 */
public class AccountRequestsData extends ApiOutput {

    private List<AccountRequestData> accountRequests;

    public AccountRequestsData() {
        this.accountRequests = new ArrayList<>();
    }

    public AccountRequestsData(List<AccountRequestAttributes> accountRequestAttributesList) {
        this.accountRequests = accountRequestAttributesList.stream()
                .map(AccountRequestData::new).collect(Collectors.toList());
    }

    public List<AccountRequestData> getAccountRequests() {
        return accountRequests;
    }

    public void setAccountRequests(List<AccountRequestData> accountRequests) {
        this.accountRequests = accountRequests;
    }
}
