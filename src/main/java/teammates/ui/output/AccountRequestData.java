package teammates.ui.output;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {

    private final String email;
    private final String name;
    private final String institute;
    private final String registrationKey;
    @Nullable
    private final Long registeredAt;
    private final long createdAt;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.name = accountRequestInfo.getName();
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
        return name;
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
