package teammates.common.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link RecaptchaVerifier}.
 */
public class RecaptchaVerifierTest extends BaseTestCase {

    /**
     * Tests the overloaded {@link RecaptchaVerifier#isVerificationSuccessful(String)} method.
     */
    @Test
    public void testIsVerificationSuccessful() {
        ______TS("null or empty CAPTCHA response");
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful(null));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful(""));

        ______TS("empty CAPTCHA secret key");
        assertTrue(new RecaptchaVerifierStub(null).isVerificationSuccessful("empty secret key"));
        assertTrue(new RecaptchaVerifierStub("").isVerificationSuccessful("empty secret key"));

        ______TS("Successful verification");
        // Use RecaptchaVerifierStub to mimic success response
        assertTrue(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("success"));

        ______TS("reCAPTCHA error codes that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic error codes
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("missing recaptcha params"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("invalid recaptcha secret key"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("invalid recaptcha response"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("invalid recaptcha request"));

        ______TS("Exceptions that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic runtime exceptions
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("null response"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("invalid uri"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("http protocol error"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("i/o exception"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("timeout exception"));
        assertFalse(new RecaptchaVerifierStub("testKey").isVerificationSuccessful("non 2xx http response"));
    }

    /**
     * A subclass to mock responses and exceptions that could result in
     * {@link RecaptchaVerifier#getApiResponse(String, String)}.
     * Success response is also mocked to decouple from the Google server for testing purposes. This way, tests are not
     * affected by potential issues in the Google server (e.g. server down).
     *
     * @see <a href="https://developers.google.com/recaptcha/docs/verify#error-code-reference">reCAPTCHA API error codes</a>
     */
    private static class RecaptchaVerifierStub extends RecaptchaVerifier {

        private RecaptchaVerifierStub(String secretKey) {
            super(secretKey);
        }

        @Override
        @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
        protected String getApiResponse(String captchaResponse, String secretKey) throws URISyntaxException, IOException {
            switch (captchaResponse) {
            case "success":
                return "{ success: true }";

            case "missing recaptcha params":
                return "{ success: false, error-codes: [ 'missing-input-response', 'missing-input-secret' ] }";

            case "invalid recaptcha secret key":
                return "{ success: false, error-codes: [ 'invalid-input-secret' ] }";

            case "invalid recaptcha response":
                return "{ success: false, error-codes: [ 'invalid-input-response' ] }";

            case "invalid recaptcha request":
                return "{ success: false, error-codes: [ 'bad-request' ] }";

            case "null response":
                throw new NullPointerException();

            case "invalid uri":
                throw new URISyntaxException("Invalid URI", "testing with invalid uri exception");

            case "http protocol error":
                throw new ClientProtocolException();

            case "i/o exception":
                throw new IOException();

            case "timeout exception":
                throw new ConnectTimeoutException();

            case "non 2xx http response":
                throw new HttpResponseException(500, "testing with http failure status code");

            default:
                return "{ success: false }";
            }
        }
    }
}
