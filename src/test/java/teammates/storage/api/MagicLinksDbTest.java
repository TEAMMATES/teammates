package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        var magicLinkRef = given.magicLink("magic-link",
                ml -> ml.email("insert@example.com").tokenHash("insert-token-hash"));
        MagicLink magicLink = given.getDataBundle().magicLinks.get(magicLinkRef.alias());

        MagicLink actual = inTransaction(() -> magicLinksDb.upsertMagicLink(magicLink));

        assertEquals(magicLinkRef.id(), actual.getId());
        assertEquals("insert@example.com", actual.getEmail());
        assertEquals("insert-token-hash", actual.getTokenHash());
        verifyPresentInDatabase(MagicLink.class, magicLinkRef.id());
    }

    @Test(groups = GroupNames.DB)
    public void upsertMagicLink_emailExists_updatesExistingMagicLink() {
        var existingMagicLink = given.magicLink("existing-magic-link",
                ml -> ml.email("upsert@example.com").tokenHash("old-upsert-token-hash"));
        var updatedMagicLink = given.magicLink("updated-magic-link",
                ml -> ml.email("upsert@example.com").tokenHash("new-upsert-token-hash"));
        inTransaction(() -> magicLinksDb.upsertMagicLink(
                given.getDataBundle().magicLinks.get(existingMagicLink.alias())));

        MagicLink actual = inTransaction(
                () -> magicLinksDb.upsertMagicLink(given.getDataBundle().magicLinks.get(updatedMagicLink.alias())));

        assertEquals(existingMagicLink.id(), actual.getId());
        assertEquals("upsert@example.com", actual.getEmail());
        assertEquals("new-upsert-token-hash", actual.getTokenHash());
        assertNull(inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("old-upsert-token-hash")));
    }

    @Test(groups = GroupNames.DB)
    public void getMagicLinkByTokenHash_magicLinkExists_returnsMagicLink() {
        var magicLink = given.magicLink("magic-link",
                ml -> ml.email("lookup@example.com").tokenHash("lookup-token-hash"));
        persistGivenData(given);

        MagicLink actual = inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("lookup-token-hash"));

        assertNotNull(actual);
        assertEquals(magicLink.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getMagicLinkByTokenHash_magicLinkDoesNotExist_returnsNull() {
        given.magicLink("magic-link",
                ml -> ml.email("missing-lookup@example.com").tokenHash("existing-token-hash"));
        persistGivenData(given);

        MagicLink actual = inTransaction(() -> magicLinksDb.getMagicLinkByTokenHash("non-existent-token-hash"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void deleteMagicLink_magicLinkExists_magicLinkIsRemoved() {
        var magicLink = given.magicLink("magic-link",
                ml -> ml.email("delete@example.com").tokenHash("delete-token-hash"));
        persistGivenData(given);

        inTransaction(() -> magicLinksDb.deleteMagicLink(magicLinksDb.getMagicLinkByTokenHash("delete-token-hash")));

        verifyAbsentInDatabase(MagicLink.class, magicLink.id());
    }
}
