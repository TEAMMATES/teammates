package teammates.test.cases.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.testng.annotations.Test;

import teammates.common.util.RecaptchaVerifier;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link RecaptchaVerifier}.
 * @see <a href="https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha-what-should-i-do">Automated testing with reCAPTCHA v2</a>
 */
public class RecaptchaVerifierTest extends BaseTestCase {

    @Test
    public void testIsVerificationSuccessful() {
        ______TS("null or empty captcha response");
        assertFalse(new RecaptchaVerifier().isVerificationSuccessful(null));
        assertFalse(new RecaptchaVerifier().isVerificationSuccessful(""));

        ______TS("Any other key"); // success when using the official secret test key
        assertTrue(new RecaptchaVerifier().isVerificationSuccessful("testResponseKey"));
        assertTrue(new RecaptchaVerifier().isVerificationSuccessful(".#?"));

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
        assertFalse(new RecaptchaVerifierStub().isVerificationSuccessful("non 2xx http response"));
    }

    /**
     * A subclass to mock the possible errors and exceptions that could occur in
     * RecaptchaVerifier#isVerificationSuccessful().
     *
     * @see <a href="https://developers.google.com/recaptcha/docs/verify#error-code-reference">reCAPTCHA API error codes</a>
     */
    static class RecaptchaVerifierStub extends RecaptchaVerifier {

        @Override
        @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
        protected String getApiResponse(String captchaResponse) throws URISyntaxException, IOException {
            switch (captchaResponse) {
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

            case "non 2xx http response":
                throw new HttpResponseException(101, "testing http failure status code");

            default:
                return super.getApiResponse(captchaResponse);
            }
        }
    }
}
