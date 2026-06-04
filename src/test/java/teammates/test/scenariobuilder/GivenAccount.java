package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.common.datatransfer.Provider;
import teammates.storage.entity.Account;

/**
 * Builder for Account entities used in test scenarios.
 */
public final class GivenAccount extends GivenBase<Account> {
    public GivenAccount(GivenData given, UUID accountId) {
        super(given);
        this.entity = defaultAccount(accountId);
    }

    /**
     * Sets the email for the account.
     */
    public GivenAccount email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the googleId for the account.
     */
    public GivenAccount googleId(String googleId) {
        entity.setGoogleId(googleId);
        return this;
    }

    /**
     * Sets the provider, subject, and tenantId.
     */
    public GivenAccount authIdentity(Provider provider, String subject, String tenantId) {
        entity.setProvider(provider);
        entity.setSubject(subject);
        entity.setTenantId(tenantId);
        return this;
    }

    @Override
    void ensureConsistent() {
        // No mandatory relationships
    }

    /**
     * Generates a default alias for an account based on the account ID.
     */
    public static String getDefaultAlias(String accountAlias) {
        return "default:" + accountAlias;
    }

    private Account defaultAccount(UUID accountId) {
        String googleId = accountId.toString();
        Provider provider = Provider.TEAMMATES_DEV;
        String subject = "sub:" + accountId.toString();
        String tenantId = "";
        String name = "name:" + accountId.toString();
        String email = accountId.toString() + "@teammates.tmt";
        Account a = new Account(googleId, provider, subject, tenantId, name, email);
        a.setId(accountId);
        return a;
    }
}
