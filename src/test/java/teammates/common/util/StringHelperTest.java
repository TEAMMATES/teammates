package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.testng.annotations.Test;

import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StringHelper}.
 */
public class StringHelperTest extends BaseTestCase {

    @Test
    public void testIsEmpty() {
        assertTrue(StringHelper.isEmpty(null));
        assertTrue(StringHelper.isEmpty(""));
        assertFalse(StringHelper.isEmpty("test"));
        assertFalse(StringHelper.isEmpty("     "));
    }

    @Test
    public void testGenerateStringOfLength() {

        assertEquals("sssss", StringHelper.generateStringOfLength(5, 's'));
        assertEquals("", StringHelper.generateStringOfLength(0, 's'));
    }

    @Test
    public void testIsMatching() {
        assertTrue(StringHelper.isMatching("\u00E0", "à"));
        assertTrue(StringHelper.isMatching("\u0061\u0300", "à"));
        assertFalse(StringHelper.isMatching("Héllo", "Hello"));
    }

    @Test
    public void testToString() {
        List<String> strings = new ArrayList<>();
        assertEquals("", StringHelper.toString(strings, ""));
        assertEquals("", StringHelper.toString(strings, "<br>"));

        strings.add("aaa");
        assertEquals("aaa", StringHelper.toString(strings, ""));
        assertEquals("aaa", StringHelper.toString(strings, "\n"));
        assertEquals("aaa", StringHelper.toString(strings, "<br>"));

        strings.add("bbb");
        assertEquals("aaabbb", StringHelper.toString(strings, ""));
        assertEquals("aaa\nbbb", StringHelper.toString(strings, "\n"));
        assertEquals("aaa<br>bbb", StringHelper.toString(strings, "<br>"));

        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(44);
        assertEquals("1\n44", StringHelper.toString(ints, "\n"));
    }

    @Test
    public void testKeyEncryption() throws Exception {
        String msg = "Test decryption";
        String decrptedMsg;

        decrptedMsg = StringHelper.decrypt(StringHelper.encrypt(msg));
        assertEquals(msg, decrptedMsg);
    }

    @Test
    public void testAesGcmEncryptionUsesRandomIv() throws Exception {
        String plaintext = StringHelper.generateStringOfLength(64, 'A');
        String ciphertext1 = StringHelper.encrypt(plaintext);
        String ciphertext2 = StringHelper.encrypt(plaintext);

        assertNotEquals(ciphertext1, ciphertext2);
        assertEquals(plaintext, StringHelper.decrypt(ciphertext1));
        assertEquals(plaintext, StringHelper.decrypt(ciphertext2));
    }

    private static String generateSignature(String data) throws Exception {
        SecretKeySpec signingKey =
                new SecretKeySpec(StringHelper.hexStringToByteArray(Config.ENCRYPTION_KEY), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] value = mac.doFinal(data.getBytes(Const.ENCODING));
        return StringHelper.byteArrayToHexString(value);
    }

    @Test
    public void testDecryptingInvalidCiphertextThrowsException() {
        // The decrypt function converts a hex string into an array of bytes before decryption.
        // E.g AF is the byte 10101111
        // Hence, non-hex strings should fail to decrypt.
        String invalidHexString = "GHI";

        String tooShortCiphertext = "00112233445566778899AA";
        String validCiphertext = StringHelper.encrypt("valid plaintext");
        String tamperedCiphertext = validCiphertext.substring(0, validCiphertext.length() - 1)
                + (validCiphertext.endsWith("0") ? "1" : "0");

        String[] invalidCiphertexts = {invalidHexString, tooShortCiphertext, tamperedCiphertext};
        for (String invalidCiphertext : invalidCiphertexts) {
            assertThrows(InvalidParametersException.class, () -> StringHelper.decrypt(invalidCiphertext));
        }
    }

    @Test
    public void testRemoveExtraSpace() {

        assertNull(StringHelper.removeExtraSpace((String) null));

        String str = "";
        assertEquals("", StringHelper.removeExtraSpace(str));

        str = "a    a";
        assertEquals("a a", StringHelper.removeExtraSpace(str));

        str = " \u00A0 a    a   ";
        assertEquals("a a", StringHelper.removeExtraSpace(str));

        str = "    ";
        assertEquals("", StringHelper.removeExtraSpace(str));

        str = " a      b       c       d      ";
        assertEquals("a b c d", StringHelper.removeExtraSpace(str));
    }

    @Test
    public void testReplaceIllegalChars() {
        String regex = "[a-zA-Z0-9_.$-]+";

        assertNull(StringHelper.replaceIllegalChars(null, regex, '_'));

        String str = "";
        assertEquals("", StringHelper.replaceIllegalChars(str, regex, '_'));

        str = "abc";
        assertEquals("abc", StringHelper.replaceIllegalChars(str, regex, '_'));

        str = "illegal!?Chars+1";
        assertEquals("illegal__Chars_1", StringHelper.replaceIllegalChars(str, regex, '_'));
        assertEquals("illegal..Chars.1", StringHelper.replaceIllegalChars(str, regex, '.'));
    }

    @Test
    public void testConvertToEmptyStringIfNull() {
        String empty = "";
        String whitespace = " ";
        String nonEmpty = "non-empty";
        assertEquals("", StringHelper.convertToEmptyStringIfNull(null));
        assertEquals("non-empty", StringHelper.convertToEmptyStringIfNull(nonEmpty));
        assertEquals("", StringHelper.convertToEmptyStringIfNull(empty));
        assertEquals(" ", StringHelper.convertToEmptyStringIfNull(whitespace));
    }

    @Test
    public void testTruncateHead() {
        assertEquals("1234567890", StringHelper.truncateHead("xxxx1234567890", 10));
        assertEquals("1234567890", StringHelper.truncateHead("1234567890", 10));
        assertEquals("123456789", StringHelper.truncateHead("123456789", 10));
        assertEquals("567890", StringHelper.truncateHead("1234567890", 6));
    }

    @Test
    public void testSignatureGeneration() throws Exception {
        String data1 = "National University of Singapore";
        String data2 = "Nanyang Technological University";

        assertEquals(generateSignature(data1), StringHelper.generateSignature(data1));

        assertNotEquals(StringHelper.generateSignature(data1), StringHelper.generateSignature(data2));
    }

    @Test
    public void testSignatureVerification() {
        String valid = "National University of Singapore";
        String invalid = "Nanyang Technological University";
        String signature = StringHelper.generateSignature(valid);

        assertTrue(StringHelper.isCorrectSignature(valid, signature));

        assertFalse(StringHelper.isCorrectSignature(valid, invalid));
        assertFalse(StringHelper.isCorrectSignature(valid, null));
        assertFalse(StringHelper.isCorrectSignature(null, signature));
        assertFalse(StringHelper.isCorrectSignature(invalid, signature));
    }
}
