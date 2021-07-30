package teammates.logic.api;

/**
 * Allows mocking of the {@link RecaptchaVerifier} used in production.
 */
public class MockRecaptchaVerifier extends RecaptchaVerifier {

    @Override
    public boolean isVerificationSuccessful(String captchaResponse) {
        return true;
    }

}
