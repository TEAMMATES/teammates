package teammates.test.scenariobuilder;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;

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
     * Sets the institute for the account request.
     */
    public GivenAccountRequest institute(String institute) {
        entity.setInstitute(institute);
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
     * Marks the account request as registered.
     */
    public GivenAccountRequest registered() {
        return status(AccountRequestStatus.REGISTERED);
    }

    /**
     * Sets the comments for the account request.
     */
    public GivenAccountRequest comments(String comments) {
        entity.setComments(comments);
        return this;
    }

    /**
     * Sets the registration key for the account request.
     */
    public GivenAccountRequest registrationKey(String registrationKey) {
        entity.setRegistrationKey(registrationKey);
        return this;
    }

    /**
     * Sets the time when the account request was registered.
     */
    public GivenAccountRequest registeredAt(Instant registeredAt) {
        entity.setRegisteredAt(registeredAt);
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
        // No mandatory relationships
    }

    private AccountRequest defaultAccountRequest(UUID accountRequestId) {
        AccountRequest accountRequest = new AccountRequest(
                accountRequestId + "@teammates.tmt",
                "name:" + accountRequestId,
                "Institute " + accountRequestId,
                AccountRequestStatus.PENDING,
                "");
        accountRequest.setId(accountRequestId);
        accountRequest.setRegistrationKey("registration-key:" + accountRequestId);
        return accountRequest;
    }
}
