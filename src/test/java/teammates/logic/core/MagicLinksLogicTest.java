package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.MagicLinksDb;
import teammates.storage.entity.MagicLink;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link MagicLinksLogic}.
 */
public class MagicLinksLogicTest extends BaseTestCase {

    private MagicLinksLogic magicLinksLogic = MagicLinksLogic.inst();

    private MagicLinksDb magicLinksDb;

    @BeforeMethod
    public void setUpMethod() {
        magicLinksDb = mock(MagicLinksDb.class);
        magicLinksLogic.initLogicDependencies(magicLinksDb);
    }

    @Test
    public void createMagicLink_validEmail_generatesTokenAndStoresHash() throws InvalidParametersException {
        when(magicLinksDb.upsertMagicLink(any(MagicLink.class))).thenAnswer(inv -> inv.getArgument(0));

        String token = magicLinksLogic.createMagicLink("user@example.com");

        ArgumentCaptor<MagicLink> captor = ArgumentCaptor.forClass(MagicLink.class);
        verify(magicLinksDb, times(1)).upsertMagicLink(captor.capture());
        MagicLink persistedMagicLink = captor.getValue();

        assertNotNull(token);
        assertEquals(43, token.length());
        assertEquals("user@example.com", persistedMagicLink.getEmail());
        assertNotEquals(token, persistedMagicLink.getTokenHash());
        assertEquals(MagicLinksLogic.hashToken(token), persistedMagicLink.getTokenHash());
    }

    @Test
    public void createMagicLink_invalidEmail_throwsInvalidParametersException() {
        assertThrows(InvalidParametersException.class, () -> magicLinksLogic.createMagicLink("invalid-email"));

        verify(magicLinksDb, never()).upsertMagicLink(any(MagicLink.class));
    }

    @Test
    public void getMagicLinkByToken_tokenExists_returnsMagicLink() {
        String token = "raw-token";
        MagicLink magicLink = new MagicLink("user@example.com", MagicLinksLogic.hashToken(token), Instant.now());
        when(magicLinksDb.getMagicLinkByTokenHash(MagicLinksLogic.hashToken(token))).thenReturn(magicLink);

        MagicLink actual = magicLinksLogic.getMagicLinkByToken(token);

        assertEquals(magicLink, actual);
    }

    @Test
    public void consumeMagicLink_magicLinkIsUsable_deletesAndReturnsMagicLink()
            throws EntityDoesNotExistException, InvalidParametersException {
        String token = "raw-token";
        MagicLink magicLink = new MagicLink("user@example.com", MagicLinksLogic.hashToken(token), Instant.now());
        when(magicLinksDb.getMagicLinkByTokenHash(MagicLinksLogic.hashToken(token))).thenReturn(magicLink);

        MagicLink actual = magicLinksLogic.consumeMagicLink(token);

        assertEquals(magicLink, actual);
        verify(magicLinksDb, times(1)).deleteMagicLink(magicLink);
    }

    @Test
    public void consumeMagicLink_magicLinkIsExpired_throwsInvalidParametersExceptionWithoutDeleting() {
        String token = "raw-token";
        MagicLink magicLink = new MagicLink("user@example.com", MagicLinksLogic.hashToken(token), Instant.now());
        magicLink.setExpiresAt(Instant.now().minusSeconds(1));
        when(magicLinksDb.getMagicLinkByTokenHash(MagicLinksLogic.hashToken(token))).thenReturn(magicLink);

        InvalidParametersException ex = assertThrows(
                InvalidParametersException.class, () -> magicLinksLogic.consumeMagicLink(token));

        assertEquals("Invalid or expired magic link.", ex.getMessage());
        verify(magicLinksDb, never()).deleteMagicLink(any(MagicLink.class));
    }

    @Test
    public void consumeMagicLink_magicLinkDoesNotExist_throwsEntityDoesNotExistException() {
        EntityDoesNotExistException ex = assertThrows(
                EntityDoesNotExistException.class, () -> magicLinksLogic.consumeMagicLink("raw-token"));

        assertEquals("Magic link does not exist for the given token.", ex.getMessage());
        verify(magicLinksDb, never()).deleteMagicLink(any(MagicLink.class));
    }
}
