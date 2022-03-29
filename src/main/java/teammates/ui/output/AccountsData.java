package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * The API output format of a list of accounts.
 */
public class AccountsData extends ApiOutput {

    private List<AccountData> accounts;

    public AccountsData(List<AccountAttributes> accountAttributes) {
        this.accounts = accountAttributes.stream().map(AccountData::new).collect(Collectors.toList());
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }

}
