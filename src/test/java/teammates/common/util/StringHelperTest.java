package teammates.common.util;

import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StringHelper}.
 */
public class StringHelperTest extends BaseTestCase {

    @Test
    public void testIsEmpty() {
        Assertions.assertTrue(StringHelper.isEmpty(null));
        Assertions.assertTrue(StringHelper.isEmpty(""));
        Assertions.assertFalse(StringHelper.isEmpty("test"));
        Assertions.assertFalse(StringHelper.isEmpty("     "));
    }

    @Test
    public void testGenerateStringOfLength() {

        Assertions.assertEquals("sssss", StringHelper.generateStringOfLength(5, 's'));
        Assertions.assertEquals("", StringHelper.generateStringOfLength(0, 's'));
    }

    @Test
    public void testIsMatching() {
        Assertions.assertTrue(StringHelper.isMatching("\u00E0", "à"));
        Assertions.assertTrue(StringHelper.isMatching("\u0061\u0300", "à"));
        Assertions.assertFalse(StringHelper.isMatching("Héllo", "Hello"));
    }

    @Test
    public void testToString() {
        List<String> strings = new ArrayList<>();
        Assertions.assertEquals("", StringHelper.toString(strings, ""));
        Assertions.assertEquals("", StringHelper.toString(strings, "<br>"));

        strings.add("aaa");
        Assertions.assertEquals("aaa", StringHelper.toString(strings, ""));
        Assertions.assertEquals("aaa", StringHelper.toString(strings, "\n"));
        Assertions.assertEquals("aaa", StringHelper.toString(strings, "<br>"));

        strings.add("bbb");
        Assertions.assertEquals("aaabbb", StringHelper.toString(strings, ""));
        Assertions.assertEquals("aaa\nbbb", StringHelper.toString(strings, "\n"));
        Assertions.assertEquals("aaa<br>bbb", StringHelper.toString(strings, "<br>"));

        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(44);
        Assertions.assertEquals("1\n44", StringHelper.toString(ints, "\n"));
    }

    @Test
    public void testKeyEncryption() throws Exception {
        String msg = "Test decryption";
        String decrptedMsg;

        decrptedMsg = StringHelper.decrypt(StringHelper.encrypt(msg));
        Assertions.assertEquals(msg, decrptedMsg);
    }

    @Test
    public void testAesGcmEncryptionUsesRandomIv() throws Exception {
        String plaintext = StringHelper.generateStringOfLength(64, 'A');
        String ciphertext1 = StringHelper.encrypt(plaintext);
        String ciphertext2 = StringHelper.encrypt(plaintext);

        Assertions.assertNotEquals(ciphertext1, ciphertext2);
        Assertions.assertEquals(plaintext, StringHelper.decrypt(ciphertext1));
        Assertions.assertEquals(plaintext, StringHelper.decrypt(ciphertext2));
    }

    @Test
    public void testGenerateSha256Hmac_isDeterministic() {
        String data = "sample-data";
        String hmac1 = StringHelper.generateSha256Hmac(data);
        String hmac2 = StringHelper.generateSha256Hmac(data);
        String hmac3 = StringHelper.generateSha256Hmac("another-data");

        Assertions.assertEquals(hmac1, hmac2);
        Assertions.assertNotEquals(hmac1, hmac3);
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
            Assertions.assertThrows(InvalidParametersException.class, () -> StringHelper.decrypt(invalidCiphertext));
        }
    }

    @Test
    public void testRemoveExtraSpace() {

        Assertions.assertNull(StringHelper.removeExtraSpace((String) null));

        String str = "";
        Assertions.assertEquals("", StringHelper.removeExtraSpace(str));

        str = "a    a";
        Assertions.assertEquals("a a", StringHelper.removeExtraSpace(str));

        str = " \u00A0 a    a   ";
        Assertions.assertEquals("a a", StringHelper.removeExtraSpace(str));

        str = "    ";
        Assertions.assertEquals("", StringHelper.removeExtraSpace(str));

        str = " a      b       c       d      ";
        Assertions.assertEquals("a b c d", StringHelper.removeExtraSpace(str));
    }

    @Test
    public void testReplaceIllegalChars() {
        String regex = "[a-zA-Z0-9_.$-]+";

        Assertions.assertNull(StringHelper.replaceIllegalChars(null, regex, '_'));

        String str = "";
        Assertions.assertEquals("", StringHelper.replaceIllegalChars(str, regex, '_'));

        str = "abc";
        Assertions.assertEquals("abc", StringHelper.replaceIllegalChars(str, regex, '_'));

        str = "illegal!?Chars+1";
        Assertions.assertEquals("illegal__Chars_1", StringHelper.replaceIllegalChars(str, regex, '_'));
        Assertions.assertEquals("illegal..Chars.1", StringHelper.replaceIllegalChars(str, regex, '.'));
    }

    @Test
    public void testConvertToEmptyStringIfNull() {
        String empty = "";
        String whitespace = " ";
        String nonEmpty = "non-empty";
        Assertions.assertEquals("", StringHelper.convertToEmptyStringIfNull(null));
        Assertions.assertEquals("non-empty", StringHelper.convertToEmptyStringIfNull(nonEmpty));
        Assertions.assertEquals("", StringHelper.convertToEmptyStringIfNull(empty));
        Assertions.assertEquals(" ", StringHelper.convertToEmptyStringIfNull(whitespace));
    }

    @Test
    public void testTruncateHead() {
        Assertions.assertEquals("1234567890", StringHelper.truncateHead("xxxx1234567890", 10));
        Assertions.assertEquals("1234567890", StringHelper.truncateHead("1234567890", 10));
        Assertions.assertEquals("123456789", StringHelper.truncateHead("123456789", 10));
        Assertions.assertEquals("567890", StringHelper.truncateHead("1234567890", 6));
    }

}
