package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.logic.entity.AccountRequest;

/**
 * Output format of account request data.
 */
public class AccountRequestData extends ApiOutput {
    // TODO: rename to accountRequestId for consistency.
    private final UUID id;
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

    @JsonCreator
    private AccountRequestData(UUID id, String email, String name, String institute, String registrationKey,
                                AccountRequestStatus status, String comments, Long registeredAt, long createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.registrationKey = registrationKey;
        this.status = status;
        this.comments = comments;
        this.registeredAt = registeredAt;
        this.createdAt = createdAt;
    }

    public AccountRequestData(AccountRequest accountRequest) {
        this.id = accountRequest.getId();
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

    public UUID getId() {
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
