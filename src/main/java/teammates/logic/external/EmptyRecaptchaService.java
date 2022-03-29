package teammates.logic.external;

/**
 * Service that bypasses reCAPTCHA verification, i.e. always returning successful verification.
 */
public class EmptyRecaptchaService implements RecaptchaService {

    @Override
    public boolean isVerificationSuccessful(String captchaResponse) {
        return true;
    }

}
