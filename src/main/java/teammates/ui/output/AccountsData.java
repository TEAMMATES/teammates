package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.Account;

/**
 * The API output format of a list of accounts.
 */
public class AccountsData extends ApiOutput {

    private List<AccountData> accounts;

    public AccountsData(List<Account> accounts) {
        this.accounts = accounts.stream().map(AccountData::new).collect(Collectors.toList());
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }

}
