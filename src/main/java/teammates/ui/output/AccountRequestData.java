package teammates.ui.output;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

import javax.annotation.Nullable;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {

    ApiOutput apiOutput = new ApiOutput();

    private final String email;
    private final String institute;
    private final String registrationKey;
    @Nullable
    private final Long registeredAt;
    private final long createdAt;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.apiOutput.name = accountRequestInfo.getName();
        this.email = accountRequestInfo.getEmail();
        this.institute = accountRequestInfo.getInstitute();
        this.registrationKey = accountRequestInfo.getRegistrationKey();
        this.createdAt = accountRequestInfo.getCreatedAt().toEpochMilli();
        if (accountRequestInfo.getRegisteredAt() == null) {
            this.registeredAt = null;
        } else {
            this.registeredAt = accountRequestInfo.getRegisteredAt().toEpochMilli();
        }
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return apiOutput.name;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public Long getRegisteredAt() {
        return registeredAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
