package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link AppUrl}.
 */
public class AppUrlTest extends BaseTestCase {

    @Test
    public void constructor_validHttpUrl_createsAppUrl() {
        AppUrl url = new AppUrl("http://www.teammates.tmt");
        assertEquals("http://www.teammates.tmt", url.getBaseUrl());
        assertEquals("", url.toString());
    }

    @Test
    public void constructor_validHttpsUrl_createsAppUrl() {
        AppUrl url = new AppUrl("https://www.teammates.tmt");
        assertEquals("https://www.teammates.tmt", url.getBaseUrl());
    }

    @Test
    public void constructor_urlWithPath_preservesPath() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        assertEquals("/page", url.toString());
    }

    @Test
    public void constructor_urlWithInitialQuery_preservesInitialQuery() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
    }

    @Test
    public void constructor_noScheme_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppUrl("www.teammates.tmt/page"));
    }

    @Test
    public void constructor_unknownScheme_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppUrl("randomprotocol://www.teammates.tmt/page"));
    }

    @Test
    public void constructor_ftp_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppUrl("ftp://www.teammates.tmt/page"));
    }

    @Test
    public void constructor_noAuthority_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppUrl("http:///page"));
    }

    @Test
    public void constructor_malformedUrl_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppUrl("http://[invalid"));
    }

    @Test
    public void withParam_singleParameter_addsParameter() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithParam = url.withParam("key1", "value1");
        assertEquals("/page?key1=value1", urlWithParam.toString());
    }

    @Test
    public void withParam_multipleParameters_addsAllInOrder() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithParams = url.withParam("key1", "value1").withParam("key2", "value2");
        assertEquals("/page?key1=value1&key2=value2", urlWithParams.toString());
    }

    @Test
    public void withParam_duplicateKeys_allowsDuplicates() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithDuplicates = url.withParam("key", "value1").withParam("key", "value2");
        assertEquals("/page?key=value1&key=value2", urlWithDuplicates.toString());
    }

    @Test
    public void withParam_nullKey_returnsUnchanged() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl result = url.withParam(null, "value");
        assertEquals("/page", result.toString());
    }

    @Test
    public void withParam_emptyKey_returnsUnchanged() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl result = url.withParam("", "value");
        assertEquals("/page", result.toString());
    }

    @Test
    public void withParam_nullValue_returnsUnchanged() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl result = url.withParam("key", null);
        assertEquals("/page", result.toString());
    }

    @Test
    public void withParam_emptyValue_returnsUnchanged() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl result = url.withParam("key", "");
        assertEquals("/page", result.toString());
    }

    @Test
    public void withParam_specialCharacters_encodesCorrectly() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithParam = url.withParam("key", "#&?");
        String result = urlWithParam.toString();
        // Verify special characters are percent-encoded
        assertEquals(true, result.contains("%23")); // #
        assertEquals(true, result.contains("%26")); // &
        assertEquals(true, result.contains("%3F")); // ?
    }

    @Test
    public void withParam_isImmutable_returnsNewInstance() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithParam = url.withParam("key", "value");
        // Original should be unchanged
        assertEquals("/page", url.toString());
        // New instance should have the parameter
        assertEquals("/page?key=value", urlWithParam.toString());
    }

    @Test
    public void withParam_onUrlWithInitialQuery_appendsNewParam() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page?existing=param");
        AppUrl urlWithParam = url.withParam("key", "value");
        assertEquals("/page?existing=param&key=value", urlWithParam.toString());
    }

    @Test
    public void toString_withAddedParams_buildsQueryString() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page").withParam("key1", "value1");
        assertEquals("/page?key1=value1", url.toString());
    }

    @Test
    public void toAbsoluteString_returnsFullUrl() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        assertEquals("http://www.teammates.tmt/page", url.toAbsoluteString());
    }

    @Test
    public void toAbsoluteString_withParameters_includesAll() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page")
                .withParam("key1", "value1");
        assertEquals("http://www.teammates.tmt/page?key1=value1", url.toAbsoluteString());
    }

    @Test
    public void toAbsoluteString_urlWithoutPath_returnsBaseUrl() {
        AppUrl url = new AppUrl("http://www.teammates.tmt");
        assertEquals("http://www.teammates.tmt", url.toAbsoluteString());
    }

    @Test
    public void getBaseUrl_returnsBaseWithoutPath() {
        AppUrl url = new AppUrl("http://www.teammates.tmt:8080/page");
        assertEquals("http://www.teammates.tmt:8080", url.getBaseUrl());
    }

    @Test
    public void withAccountId_addsAccountIdParameter() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        UUID accountId = UUID.fromString("12345678-1234-5678-1234-567812345678");
        AppUrl urlWithId = url.withAccountId(accountId);
        assertEquals(
                "/page?accountid=12345678-1234-5678-1234-567812345678",
                urlWithId.toString());
    }

    @Test
    public void withCourseId_addsCourseIdParameter() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithCourseId = url.withCourseId("CS101");
        assertEquals("/page?courseid=CS101", urlWithCourseId.toString());
    }

    @Test
    public void withUserId_addsUserIdParameter() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        UUID userId = UUID.fromString("87654321-4321-8765-4321-876543218765");
        AppUrl urlWithUserId = url.withUserId(userId);
        assertEquals(
                "/page?userid=87654321-4321-8765-4321-876543218765",
                urlWithUserId.toString());
    }

    @Test
    public void withRegistrationKey_addsRegkeyParameter() {
        AppUrl url = new AppUrl("http://www.teammates.tmt/page");
        AppUrl urlWithKey = url.withRegistrationKey("somekey123");
        assertEquals("/page?key=somekey123", urlWithKey.toString());
    }

}
