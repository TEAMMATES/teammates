package teammates.common.util;

import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.LinkKey;
import teammates.common.datatransfer.LinkKeyType;
import teammates.common.exception.InvalidParametersException;

/**
 * Utility class for encrypting and decrypting student session keys.
 */
public final class LinkKeyUtil {

    private LinkKeyUtil() {
        // utility class
    }

    /**
     * Encrypts the session key payload for a student session link.
     */
    public static String encrypt(UUID userId, LinkKeyType type, String regKey, UUID feedbackSessionId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(regKey);
        Objects.requireNonNull(feedbackSessionId);

        return StringHelper.encrypt(JsonUtils.toCompactJson(new LinkKey(userId, type, regKey, feedbackSessionId)));
    }

    /**
     * Decrypts the supplied encrypted student session key.
     */
    public static LinkKey decrypt(String encryptedKey) throws InvalidParametersException {
        Objects.requireNonNull(encryptedKey);

        String decrypted = StringHelper.decrypt(encryptedKey);
        return JsonUtils.fromJson(decrypted, LinkKey.class);
    }
}
