package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {
    private final String id;
    private final String email;
    private final String name;
    private final String institute;
    private final String registrationKey;
    private final AccountRequestStatus status;
    @Nullable
    private final String comments;
    @Nullable
    private final Long registeredAt;
    private final long createdAt;

    public AccountRequestData(AccountRequestAttributes accountRequestInfo) {
        this.id = accountRequestInfo.getId();
        this.name = accountRequestInfo.getName();
        this.email = accountRequestInfo.getEmail();
        this.institute = accountRequestInfo.getInstitute();
        this.registrationKey = accountRequestInfo.getRegistrationKey();
        this.comments = null;
        this.createdAt = accountRequestInfo.getCreatedAt().toEpochMilli();

        if (accountRequestInfo.getRegisteredAt() == null) {
            this.status = AccountRequestStatus.APPROVED;
            this.registeredAt = null;
        } else {
            this.status = AccountRequestStatus.REGISTERED;
            this.registeredAt = accountRequestInfo.getRegisteredAt().toEpochMilli();
        }
    }

    public AccountRequestData(AccountRequest accountRequest) {
        this.id = accountRequest.getId().toString();
        this.name = accountRequest.getName();
        this.email = accountRequest.getEmail();
        this.institute = accountRequest.getInstitute();
        this.registrationKey = accountRequest.getRegistrationKey();
        this.status = accountRequest.getStatus();
        this.comments = accountRequest.getComments();
        this.createdAt = accountRequest.getCreatedAt().toEpochMilli();

        if (accountRequest.getRegisteredAt() == null) {
            this.registeredAt = null;
        } else {
            this.registeredAt = accountRequest.getRegisteredAt().toEpochMilli();
        }
    }

    public String getId() {
        return id;
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

    public AccountRequestStatus getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }

    public Long getRegisteredAt() {
        return registeredAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
