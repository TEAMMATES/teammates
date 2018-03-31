package teammates.common.util;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cryptographic helper functions.
 */
public final class CryptoHelper {
    private CryptoHelper() {
        // utility class
    }

    /**
     * Computes session token from session ID using the HMAC-MD5 algorithm.
     * Uses {@link Config#ENCRYPTION_KEY} as the secret key for the HMAC-MD5.
     */
    public static String computeSessionToken(String sessionId) {
        SecretKeySpec sks = new SecretKeySpec(StringHelper.hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacMD5");
            mac.init(sks);
        } catch (NoSuchAlgorithmException e) {
            Assumption.fail("Algorithm specified does not exist.");
        } catch (InvalidKeyException e) {
            Assumption.fail("Invalid encryption key encountered. Check your build.properties file.");
        }
        Charset charset = Charset.forName("UTF-8");
        byte[] encryptedSessionId = mac.doFinal(sessionId.getBytes(charset));
        return StringHelper.byteArrayToHexString(encryptedSessionId);
    }
}
