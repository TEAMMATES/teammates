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

    @Test
    public void testIsVerificationSuccessful() {
        ______TS("null or empty captcha response");
        // Does not require Stub since these should not even attempt to make the API request
        assertFalse(new RecaptchaVerifier().isVerificationSuccessful(null));
        assertFalse(new RecaptchaVerifier().isVerificationSuccessful(""));

        ______TS("Successful verification");
        assertTrue(new RecaptchaVerifierStub().isVerificationSuccessful("success"));

        ______TS("reCAPTCHA error codes that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic error codes
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("missing recaptcha params"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha secret key"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha response"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid recaptcha request"));

        ______TS("Exceptions that can occur during the API request execution");
        // Use RecaptchaVerifierStub to mimic runtime exceptions
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("null response"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("invalid uri"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("http protocol error"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("i/o exception"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("timeout exception"));
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("non 2xx http response"));
    }

    /**
     * A subclass to mock successful response and the possible errors and exceptions that could occur in
     * RecaptchaVerifier#isVerificationSuccessful().
     * Success responses are mocked to decouple from the Google server for testing purposes. This way, tests will not be
     * affected by potential issues in the Google server (e.g. server down).
     *
     * @see <a href="https://developers.google.com/recaptcha/docs/verify#error-code-reference">reCAPTCHA API error codes</a>
     */
    static class RecaptchaVerifierStub extends RecaptchaVerifier {

        @Override
        @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
        protected String getApiResponse(String captchaResponse) throws URISyntaxException, IOException {
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
                throw new URISyntaxException("Invalid URI", "testing exception handling");

            case "http protocol error":
                throw new ClientProtocolException();

            case "i/o exception":
                throw new IOException();

            case "timeout exception":
                throw new ConnectTimeoutException();

            case "non 2xx http response":
                throw new HttpResponseException(101, "testing http failure status code");

            default:
                return "{ success: true }";
            }
        }
    }
}
