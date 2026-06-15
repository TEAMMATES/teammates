package teammates.test.scenariobuilder;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;

/**
 * Builder for AccountVerificationRequest entities used in test scenarios.
 */
public final class GivenAccountVerificationRequest extends GivenBase<AccountVerificationRequest> {
    public GivenAccountVerificationRequest(GivenData given, UUID accountVerificationRequestId) {
        super(given);
        this.entity = defaultAccountVerificationRequest(accountVerificationRequestId);
    }

    /**
     * Sets the email for the account verification request.
     */
    public GivenAccountVerificationRequest email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the name for the account verification request.
     */
    public GivenAccountVerificationRequest name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the institute for the account verification request, referenced by its alias.
     */
    public GivenAccountVerificationRequest institute(String instituteAlias) {
        Institute institute = given.getOrCreate(instituteAlias, given.dataBundle.institutes, given::institute);
        institute.addAccountVerificationRequest(entity);
        return this;
    }

    /**
     * Sets the account for the account verification request.
     */
    public GivenAccountVerificationRequest account(String accountAlias) {
        assert entity.getAccount() == null : "Account has already been set for this account verification request";
        Account account = given.getOrCreate(accountAlias, given.dataBundle.accounts, given::account);
        account.addAccountVerificationRequest(entity);
        return this;
    }

    /**
     * Sets the status for the account verification request.
     */
    public GivenAccountVerificationRequest status(AccountVerificationRequestStatus status) {
        entity.setStatus(status);
        return this;
    }

    /**
     * Marks the account verification request as pending.
     */
    public GivenAccountVerificationRequest pending() {
        return status(AccountVerificationRequestStatus.PENDING);
    }

    /**
     * Marks the account verification request as approved.
     */
    public GivenAccountVerificationRequest approved() {
        return status(AccountVerificationRequestStatus.APPROVED);
    }

    /**
     * Marks the account verification request as rejected.
     */
    public GivenAccountVerificationRequest rejected() {
        return status(AccountVerificationRequestStatus.REJECTED);
    }

    /**
     * Sets the comments for the account verification request.
     */
    public GivenAccountVerificationRequest comments(String comments) {
        entity.setComments(comments);
        return this;
    }

    /**
     * Sets the time when the demo course was created for the account verification request.
     */
    public GivenAccountVerificationRequest createdDemoCourseAt(Instant createdDemoCourseAt) {
        entity.setCreatedDemoCourseAt(createdDemoCourseAt);
        return this;
    }

    /**
     * Sets the created time for the account verification request.
     */
    public GivenAccountVerificationRequest createdAt(Instant createdAt) {
        entity.setCreatedAt(createdAt);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getInstituteId() == null) {
            this.institute("default");
        }
        if (entity.getAccountId() == null) {
            this.account("default");
        }
    }

    private AccountVerificationRequest defaultAccountVerificationRequest(UUID accountVerificationRequestId) {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest(
                accountVerificationRequestId + "@teammates.tmt",
                "name:" + accountVerificationRequestId,
                AccountVerificationRequestStatus.PENDING,
                "");
        accountVerificationRequest.setId(accountVerificationRequestId);
        return accountVerificationRequest;
    }
}
