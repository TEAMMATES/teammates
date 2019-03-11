package teammates.test.cases.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.testng.annotations.Test;

import teammates.common.util.RecaptchaVerifier;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link RecaptchaVerifier}.
 */
public class RecaptchaVerifierTest extends BaseTestCase {

    /**
     * Tests the overloaded {@link RecaptchaVerifier#isVerificationSuccessful(String, String)} method.
     */
    @Test
    public void testIsVerificationSuccessful() {
        ______TS("null or empty CAPTCHA response");
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful(null, "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("", "testKey"));

        ______TS("empty CAPTCHA secret key");
        assertTrue(new RecaptchaVerifierStub().isVerificationSuccessful("empty secret key", null));

        ______TS("Successful verification");
        // Use RecaptchaVerifierStub to mimic success response
        assertTrue(new RecaptchaVerifierStub().isVerificationSuccessful("success", "testKey"));

        ______TS("reCAPTCHA error codes that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic error codes
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("missing recaptcha params", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha secret key", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha response", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha request", "testKey"));

        ______TS("Exceptions that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic runtime exceptions
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("null response", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid uri", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("http protocol error", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("i/o exception", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("timeout exception", "testKey"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("non 2xx http response", "testKey"));
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
                throw new HttpResponseException(101, "testing with http failure status code");

            default:
                return "{ success: true }";
            }
        }
    }
}
