package teammates.logic.external;

/**
 * An interface to verify the user's reCAPTCHA response.
 */
public interface RecaptchaService {

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully.
     */
    boolean isVerificationSuccessful(String captchaResponse);

}
