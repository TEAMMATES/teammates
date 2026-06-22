package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.storage.entity.AccountVerificationRequest;

/**
 * Output format of account verification request data.
 */
public class AccountVerificationRequestData implements ApiOutput {
    private final UUID accountVerificationRequestId;
    private final String email;
    private final String name;
    private final String institute;
    private final String country;
    private final AccountVerificationRequestStatus status;
    @Nullable
    private final String comments;
    @Nullable
    private final Long createdDemoCourseAt;
    private final long createdAt;

    @JsonCreator
    private AccountVerificationRequestData(UUID accountVerificationRequestId, String email, String name,
                                String institute, String country,
                                AccountVerificationRequestStatus status, String comments,
                                Long createdDemoCourseAt, long createdAt) {
        this.accountVerificationRequestId = accountVerificationRequestId;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.country = country;
        this.status = status;
        this.comments = comments;
        this.createdDemoCourseAt = createdDemoCourseAt;
        this.createdAt = createdAt;
    }

    public AccountVerificationRequestData(AccountVerificationRequest accountVerificationRequest) {
        this.accountVerificationRequestId = accountVerificationRequest.getId();
        this.name = accountVerificationRequest.getName();
        this.email = accountVerificationRequest.getEmail();
        this.institute = accountVerificationRequest.getInstitute().getName();
        this.country = accountVerificationRequest.getInstitute().getCountry();
        this.status = accountVerificationRequest.getStatus();
        this.comments = accountVerificationRequest.getComments();
        this.createdAt = accountVerificationRequest.getCreatedAt().toEpochMilli();

        if (accountVerificationRequest.getCreatedDemoCourseAt() == null) {
            this.createdDemoCourseAt = null;
        } else {
            this.createdDemoCourseAt = accountVerificationRequest.getCreatedDemoCourseAt().toEpochMilli();
        }
    }

    public UUID getAccountVerificationRequestId() {
        return accountVerificationRequestId;
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

    public AccountVerificationRequestStatus getStatus() {
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
