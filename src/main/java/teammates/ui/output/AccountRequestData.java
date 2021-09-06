package teammates.ui.output;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {

    private final String email;
    private final String name;
    private final String institute;
    private final String registrationKey;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.name = accountRequestInfo.getName();
        this.email = accountRequestInfo.getEmail();
        this.institute = accountRequestInfo.getInstitute();
        this.registrationKey = accountRequestInfo.getRegistrationKey();
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

}
