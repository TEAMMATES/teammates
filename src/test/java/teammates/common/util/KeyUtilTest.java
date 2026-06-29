package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionKey;
import teammates.common.datatransfer.SessionKeyType;
import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCase;

/**
 * Tests for {@link KeyUtil}.
 */
public class KeyUtilTest extends BaseTestCase {

    @Test
    public void encryptAndDecrypt_validPayload_roundTrips() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-4000-8000-000000000001");
        UUID feedbackSessionId = UUID.fromString("00000000-0000-4000-8000-000000000002");
        int linkVersion = 3;

        String encryptedKey = KeyUtil.encryptSessionKey(userId, SessionKeyType.SUBMISSION, linkVersion, feedbackSessionId);
        SessionKey sessionKey = KeyUtil.decryptSessionKey(encryptedKey);

        assertEquals(userId, sessionKey.userId());
        assertEquals(SessionKeyType.SUBMISSION, sessionKey.type());
        assertEquals(linkVersion, sessionKey.linkVersion());
        assertEquals(feedbackSessionId, sessionKey.feedbackSessionId());
    }

    @Test
    public void decrypt_invalidCiphertext_throwsInvalidParametersException() {
        assertThrows(InvalidParametersException.class, () -> KeyUtil.decryptSessionKey("invalid"));
    }
}
