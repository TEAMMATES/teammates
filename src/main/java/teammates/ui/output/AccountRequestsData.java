package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * The API output format of a list of account requests.
 */
public class AccountRequestsData extends ApiOutput {

    private final List<AccountRequestData> accountRequests;

    public AccountRequestsData(List<AccountRequestAttributes> accountRequestAttributes) {
        this.accountRequests = accountRequestAttributes.stream().map(AccountRequestData::new).collect(Collectors.toList());
    }

    public List<AccountRequestData> getAccountRequests() {
        return accountRequests;
    }

}
