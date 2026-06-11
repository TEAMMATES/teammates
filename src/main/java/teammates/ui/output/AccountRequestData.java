package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;

/**
 * Output format of account request data.
 */
public class AccountRequestData implements ApiOutput {
    private final UUID accountRequestId;
    private final String email;
    private final String name;
    private final String institute;
    private final String country;
    private final String registrationKey;
    private final AccountRequestStatus status;
    @Nullable
    private final String comments;
    @Nullable
    private final Long registeredAt;
    private final long createdAt;

    @JsonCreator
    private AccountRequestData(UUID accountRequestId, String email, String name, String institute, String country,
                                String registrationKey, AccountRequestStatus status, String comments,
                                Long registeredAt, long createdAt) {
        this.accountRequestId = accountRequestId;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.country = country;
        this.registrationKey = registrationKey;
        this.status = status;
        this.comments = comments;
        this.registeredAt = registeredAt;
        this.createdAt = createdAt;
    }

    public AccountRequestData(AccountRequest accountRequest) {
        this.accountRequestId = accountRequest.getId();
        this.name = accountRequest.getName();
        this.email = accountRequest.getEmail();
        this.institute = accountRequest.getInstitute().getName();
        this.country = accountRequest.getInstitute().getCountry();
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

    public UUID getAccountRequestId() {
        return accountRequestId;
    }

    public String getInstitute() {
        return institute;
    }

    public String getCountry() {
        return country;
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
