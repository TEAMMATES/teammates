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
    private final long createdAtTimeStamp;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.name = accountRequestInfo.getName();
        this.email = accountRequestInfo.getEmail();
        this.institute = accountRequestInfo.getInstitute();
        this.registrationKey = accountRequestInfo.getRegistrationKey();
        this.createdAtTimeStamp = accountRequestInfo.getCreatedAt().toEpochMilli();
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

}
