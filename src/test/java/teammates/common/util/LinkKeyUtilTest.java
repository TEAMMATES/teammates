package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.LinkKey;
import teammates.common.datatransfer.LinkKeyType;
import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCase;

/**
 * Tests for {@link LinkKeyUtil}.
 */
public class LinkKeyUtilTest extends BaseTestCase {

    @Test
    public void encryptAndDecrypt_validPayload_roundTrips() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-4000-8000-000000000001");
        UUID feedbackSessionId = UUID.fromString("00000000-0000-4000-8000-000000000002");
        String regKey = "sample-reg-key";

        String encryptedKey = LinkKeyUtil.encrypt(userId, LinkKeyType.SUBMISSION, regKey, feedbackSessionId);
        LinkKey linkKey = LinkKeyUtil.decrypt(encryptedKey);

        assertEquals(userId, linkKey.userId());
        assertEquals(LinkKeyType.SUBMISSION, linkKey.type());
        assertEquals(regKey, linkKey.regKey());
        assertEquals(feedbackSessionId, linkKey.feedbackSessionId());
    }

    @Test
    public void decrypt_invalidCiphertext_throwsInvalidParametersException() {
        assertThrows(InvalidParametersException.class, () -> LinkKeyUtil.decrypt("invalid"));
    }
}
