package teammates.ui.output;

import java.util.List;

/**
 * The API output format of a list of accounts.
 */
public class AccountsData extends ApiOutput {

    private List<AccountData> accounts;

    public AccountsData(List<AccountData> accounts) {
        this.accounts = accounts;
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }

}
