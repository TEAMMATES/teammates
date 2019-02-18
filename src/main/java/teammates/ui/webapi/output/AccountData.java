package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final AccountAttributes accountInfo;

    public AccountData(AccountAttributes accountInfo) {
        this.accountInfo = accountInfo;
    }

    public AccountAttributes getAccountInfo() {
        return accountInfo;
    }
}
