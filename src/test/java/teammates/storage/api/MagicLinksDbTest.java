package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.storage.entity.MagicLink;
import teammates.test.GroupNames;

/**
 * Tests for {@link MagicLinksDb}.
 */
public class MagicLinksDbTest extends BaseDbTestcase {
    private final MagicLinksDb magicLinksDb = MagicLinksDb.inst();

    @Test(groups = GroupNames.DB)
    public void upsertMagicLink_magicLinkDoesNotExist_magicLinkIsInserted() {
        UUID magicLinkId = given.uuid("magic-link");
        MagicLink magicLink = buildDefaultMagicLink(magicLinkId, "insert@example.com", "insert-token-hash");

        MagicLink actual = inTransaction(() -> magicLinksDb.upsertMagicLink(magicLink));

        assertEquals(magicLinkId, actual.getId());
        assertEquals("insert@example.com", actual.getEmail());
        assertEquals("insert-token-hash", actual.getTokenHash());
        verifyPresentInDatabase(MagicLink.class, magicLinkId);
    }

    @Test(groups = GroupNames.DB)
    public void upsertMagicLink_emailExists_updatesExistingMagicLink() {
        MagicLink existingMagicLink = inTransaction(() -> magicLinksDb.upsertMagicLink(
                buildDefaultMagicLink(given.uuid("existing-magic-link"), "upsert@example.com", "old-upsert-token-hash")));
        MagicLink updatedMagicLink = buildDefaultMagicLink(
                given.uuid("updated-magic-link"), "upsert@example.com", "new-upsert-token-hash");

        MagicLink actual = inTransaction(() -> magicLinksDb.upsertMagicLink(updatedMagicLink));

        assertEquals(existingMagicLink.getId(), actual.getId());
        assertEquals("upsert@example.com", actual.getEmail());
        assertEquals("new-upsert-token-hash", actual.getTokenHash());
        assertNull(inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("old-upsert-token-hash")));
    }

    @Test(groups = GroupNames.DB)
    public void getMagicLinkByTokenHash_magicLinkExists_returnsMagicLink() {
        MagicLink magicLink = inTransaction(() -> magicLinksDb.upsertMagicLink(
                buildDefaultMagicLink(given.uuid("magic-link"), "lookup@example.com", "lookup-token-hash")));

        MagicLink actual = inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("lookup-token-hash"));

        assertNotNull(actual);
        assertEquals(magicLink.getId(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getMagicLinkByTokenHash_magicLinkDoesNotExist_returnsNull() {
        inTransaction(() -> magicLinksDb.upsertMagicLink(
                buildDefaultMagicLink(given.uuid("magic-link"), "missing-lookup@example.com", "existing-token-hash")));

        MagicLink actual = inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("non-existent-token-hash"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void deleteMagicLink_magicLinkExists_magicLinkIsRemoved() {
        MagicLink magicLink = inTransaction(() -> magicLinksDb.upsertMagicLink(
                buildDefaultMagicLink(given.uuid("magic-link"), "delete@example.com", "delete-token-hash")));

        inTransaction(() -> magicLinksDb.deleteMagicLink(magicLinksDb.getMagicLinkByTokenHash("delete-token-hash")));

        verifyAbsentInDatabase(MagicLink.class, magicLink.getId());
    }

    private static MagicLink buildDefaultMagicLink(UUID magicLinkId, String email, String tokenHash) {
        MagicLink magicLink = new MagicLink(email, tokenHash, Instant.now());
        magicLink.setId(magicLinkId);
        return magicLink;
    }
}
