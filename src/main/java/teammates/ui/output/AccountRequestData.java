package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;

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
    private boolean isDuplicateEmail;
    private boolean hasExistingInstructor;
    private int sameInstituteCount;

    @JsonCreator
    private AccountRequestData(UUID id, String email, String name, String institute, String registrationKey,
                                AccountRequestStatus status, String comments, Long registeredAt, long createdAt,
                                boolean isDuplicateEmail, boolean hasExistingInstructor, int sameInstituteCount) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.institute = institute;
        this.registrationKey = registrationKey;
        this.status = status;
        this.comments = comments;
        this.registeredAt = registeredAt;
        this.createdAt = createdAt;
        this.isDuplicateEmail = isDuplicateEmail;
        this.hasExistingInstructor = hasExistingInstructor;
        this.sameInstituteCount = sameInstituteCount;
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

    public void setIsDuplicateEmail(boolean isDuplicateEmail) {
        this.isDuplicateEmail = isDuplicateEmail;
    }

    public boolean getIsDuplicateEmail() {
        return isDuplicateEmail;
    }

    public void setHasExistingInstructor(boolean hasExistingInstructor) {
        this.hasExistingInstructor = hasExistingInstructor;
    }

    public boolean getHasExistingInstructor() {
        return hasExistingInstructor;
    }

    public void setSameInstituteCount(int sameInstituteCount) {
        this.sameInstituteCount = sameInstituteCount;
    }

    public int getSameInstituteCount() {
        return sameInstituteCount;
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
