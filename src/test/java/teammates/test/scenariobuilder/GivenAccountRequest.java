package teammates.test.scenariobuilder;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Institute;

/**
 * Builder for AccountRequest entities used in test scenarios.
 */
public final class GivenAccountRequest extends GivenBase<AccountRequest> {
    public GivenAccountRequest(GivenData given, UUID accountRequestId) {
        super(given);
        this.entity = defaultAccountRequest(accountRequestId);
    }

    /**
     * Sets the email for the account request.
     */
    public GivenAccountRequest email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the name for the account request.
     */
    public GivenAccountRequest name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the institute for the account request, referenced by its alias.
     */
    public GivenAccountRequest institute(String instituteAlias) {
        Institute institute = given.getOrCreate(instituteAlias, given.dataBundle.institutes, given::institute);
        institute.addAccountRequest(entity);
        return this;
    }

    /**
     * Sets the account for the account request.
     */
    public GivenAccountRequest account(String accountAlias) {
        assert entity.getAccount() == null : "Account has already been set for this account request";
        Account account = given.getOrCreate(accountAlias, given.dataBundle.accounts, given::account);
        account.addAccountRequest(entity);
        return this;
    }

    /**
     * Sets the status for the account request.
     */
    public GivenAccountRequest status(AccountRequestStatus status) {
        entity.setStatus(status);
        return this;
    }

    /**
     * Marks the account request as pending.
     */
    public GivenAccountRequest pending() {
        return status(AccountRequestStatus.PENDING);
    }

    /**
     * Marks the account request as approved.
     */
    public GivenAccountRequest approved() {
        return status(AccountRequestStatus.APPROVED);
    }

    /**
     * Marks the account request as rejected.
     */
    public GivenAccountRequest rejected() {
        return status(AccountRequestStatus.REJECTED);
    }

    /**
     * Sets the comments for the account request.
     */
    public GivenAccountRequest comments(String comments) {
        entity.setComments(comments);
        return this;
    }

    /**
     * Sets the time when the demo course was created for the account request.
     */
    public GivenAccountRequest createdDemoCourseAt(Instant createdDemoCourseAt) {
        entity.setCreatedDemoCourseAt(createdDemoCourseAt);
        return this;
    }

    /**
     * Sets the created time for the account request.
     */
    public GivenAccountRequest createdAt(Instant createdAt) {
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

    private AccountRequest defaultAccountRequest(UUID accountRequestId) {
        AccountRequest accountRequest = new AccountRequest(
                accountRequestId + "@teammates.tmt",
                "name:" + accountRequestId,
                AccountRequestStatus.PENDING,
                "");
        accountRequest.setId(accountRequestId);
        return accountRequest;
    }
}
