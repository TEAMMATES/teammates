package teammates.logic.api;

import teammates.common.util.Config;
import teammates.logic.external.EmptyRecaptchaService;
import teammates.logic.external.GoogleRecaptchaService;
import teammates.logic.external.RecaptchaService;

/**
 * Used to handle the verification of the user's reCAPTCHA response.
 */
public class RecaptchaVerifier {

    private static final RecaptchaVerifier instance = new RecaptchaVerifier();
    private final RecaptchaService service;

    RecaptchaVerifier() {
        if (Config.IS_DEV_SERVER) {
            service = new EmptyRecaptchaService();
        } else {
            service = new GoogleRecaptchaService(Config.CAPTCHA_SECRET_KEY);
        }
    }

    public static RecaptchaVerifier inst() {
        return instance;
    }

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully.
     */
    public boolean isVerificationSuccessful(String captchaResponse) {
        return service.isVerificationSuccessful(captchaResponse);
    }

}
