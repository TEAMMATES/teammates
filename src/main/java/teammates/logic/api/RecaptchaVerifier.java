package teammates.logic.api;

import teammates.common.util.Config;
import teammates.logic.core.EmptyRecaptchaService;
import teammates.logic.core.GoogleRecaptchaService;
import teammates.logic.core.RecaptchaService;

/**
 * Used to handle the verification of the user's reCAPTCHA response.
 */
public class RecaptchaVerifier {

    private static final RecaptchaVerifier instance = new RecaptchaVerifier();
    private final RecaptchaService service;

    RecaptchaVerifier() {
        if (Config.isDevServer()) {
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
