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
    private final AccountRequestStatus status;
    @Nullable
    private final String comments;
    @Nullable
    private final Long createdDemoCourseAt;
    private final long createdAt;

    @JsonCreator
    private AccountRequestData(UUID accountRequestId, String email, String name, String institute, String country,
                                AccountRequestStatus status, String comments,
                                Long createdDemoCourseAt, long createdAt) {
        this.accountRequestId = accountRequestId;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.country = country;
        this.status = status;
        this.comments = comments;
        this.createdDemoCourseAt = createdDemoCourseAt;
        this.createdAt = createdAt;
    }

    public AccountRequestData(AccountRequest accountRequest) {
        this.accountRequestId = accountRequest.getId();
        this.name = accountRequest.getName();
        this.email = accountRequest.getEmail();
        this.institute = accountRequest.getInstitute().getName();
        this.country = accountRequest.getInstitute().getCountry();
        this.status = accountRequest.getStatus();
        this.comments = accountRequest.getComments();
        this.createdAt = accountRequest.getCreatedAt().toEpochMilli();

        if (accountRequest.getCreatedDemoCourseAt() == null) {
            this.createdDemoCourseAt = null;
        } else {
            this.createdDemoCourseAt = accountRequest.getCreatedDemoCourseAt().toEpochMilli();
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

    public AccountRequestStatus getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }

    public Long getCreatedDemoCourseAt() {
        return createdDemoCourseAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
