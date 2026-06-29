package teammates.common.util;

import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.CourseJoinKey;
import teammates.common.datatransfer.SessionKey;
import teammates.common.datatransfer.SessionKeyType;
import teammates.common.exception.InvalidParametersException;

/**
 * Utility class for encrypting and decrypting keys.
 */
public final class KeyUtil {

    private KeyUtil() {
        // utility class
    }

    /**
     * Encrypts the session key payload for a student session link.
     */
    public static String encryptSessionKey(UUID userId, SessionKeyType type, int linkVersion, UUID feedbackSessionId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(feedbackSessionId);

        return StringHelper.encrypt(JsonUtils.toCompactJson(new SessionKey(userId, type, linkVersion, feedbackSessionId)));
    }

    /**
     * Decrypts the supplied encrypted student session key.
     */
    public static SessionKey decryptSessionKey(String encryptedKey) throws InvalidParametersException {
        Objects.requireNonNull(encryptedKey);

        String decrypted = StringHelper.decrypt(encryptedKey);
        return JsonUtils.fromJson(decrypted, SessionKey.class);
    }

    /**
     * Encrypts the course join key payload for a course join link.
     */
    public static String encryptCourseJoinKey(UUID userId, int linkVersion) {
        Objects.requireNonNull(userId);

        return StringHelper.encrypt(JsonUtils.toCompactJson(new CourseJoinKey(userId, linkVersion)));
    }

    /**
     * Decrypts the supplied encrypted course join key.
     */
    public static CourseJoinKey decryptCourseJoinKey(String encryptedKey) throws InvalidParametersException {
        Objects.requireNonNull(encryptedKey);

        String decrypted = StringHelper.decrypt(encryptedKey);
        return JsonUtils.fromJson(decrypted, CourseJoinKey.class);
    }
}
