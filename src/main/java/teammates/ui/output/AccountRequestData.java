package teammates.ui.output;

import java.time.Instant;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {

    private final String email;
    private final String name;
    private final String institute;
    private final String registrationKey;
    private final Instant registeredAt;
    private final Instant createdAt;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.name = accountRequestInfo.getName();
        this.email = accountRequestInfo.getEmail();
        this.institute = accountRequestInfo.getInstitute();
        this.registrationKey = accountRequestInfo.getRegistrationKey();
        this.registeredAt = accountRequestInfo.getRegisteredAt();
        this.createdAt = accountRequestInfo.getCreatedAt();
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

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
