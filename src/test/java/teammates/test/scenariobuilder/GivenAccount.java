package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.common.datatransfer.Provider;
import teammates.storage.entity.Account;

/**
 * Builder for Account entities used in test scenarios.
 */
public final class GivenAccount {
    private Account account;

    public GivenAccount(UUID accountId) {
        this.account = defaultAccount(accountId);
    }

    public Account build() {
        return account;
    }

    /**
     * Sets the email for the account.
     */
    public GivenAccount email(String email) {
        account.setEmail(email);
        return this;
    }

    /**
     * Sets the googleId for the account.
     */
    public GivenAccount googleId(String googleId) {
        account.setGoogleId(googleId);
        return this;
    }

    /**
     * Sets the provider, subject, and tenantId.
     */
    public GivenAccount authIdentity(Provider provider, String subject, String tenantId) {
        account.setProvider(provider);
        account.setSubject(subject);
        account.setTenantId(tenantId);
        return this;
    }

    void ensureConsistent() {
        // No mandatory relationships
        return;
    }

    /**
     * Generates a default alias for a section based on the course alias.
     */
    public static String getDefaultAlias(String courseAlias) {
        return "default:" + courseAlias;
    }

    private Account defaultAccount(UUID accountId) {
        String googleId = accountId.toString();
        Provider provider = Provider.TEAMMATES_DEV;
        String subject = "sub:" + accountId.toString();
        String tenantId = null;
        String name = "name:" + accountId.toString();
        String email = accountId.toString() + "@teammates.tmt";
        Account a = new Account(googleId, provider, subject, tenantId, name, email);
        a.setId(accountId);
        return a;
    }
}
