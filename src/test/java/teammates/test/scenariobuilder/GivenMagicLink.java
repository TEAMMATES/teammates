package teammates.test.scenariobuilder;

import java.time.Instant;
import java.util.UUID;

import teammates.storage.entity.MagicLink;

/**
 * Builder for MagicLink entities used in test scenarios.
 */
public final class GivenMagicLink extends GivenBase<MagicLink> {
    public GivenMagicLink(GivenData given, UUID magicLinkId) {
        super(given);
        this.entity = defaultMagicLink(magicLinkId);
    }

    /**
     * Sets the email for the magic link.
     */
    public GivenMagicLink email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the token hash for the magic link.
     */
    public GivenMagicLink tokenHash(String tokenHash) {
        entity.setTokenHash(tokenHash);
        return this;
    }

    /**
     * Sets the expiry time for the magic link.
     */
    public GivenMagicLink expiresAt(Instant expiresAt) {
        entity.setExpiresAt(expiresAt);
        return this;
    }

    @Override
    void ensureConsistent() {
        // No mandatory relationships
    }

    private MagicLink defaultMagicLink(UUID magicLinkId) {
        MagicLink magicLink = new MagicLink(
                magicLinkId.toString() + "@teammates.tmt",
                "token-hash:" + magicLinkId.toString(),
                Instant.now());
        magicLink.setId(magicLinkId);
        return magicLink;
    }
}
