package teammates.common.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Used to handle the verification of the user's reCAPTCHA response.
 *
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">reCAPTCHA user response verification API</a>
 */
public class RecaptchaVerifier {

    /** The Google reCAPTCHA API URL to verify the response token. */
    public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    /** The shared secret key between the TEAMMATES site and reCAPTCHA. */
    public static final String SECRET_KEY = Config.CAPTCHA_SECRET_KEY;

    private static final Logger log = Logger.getLogger();

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully.
     * @param captchaResponse The user's captcha response from the client side
     * @return true if the {@code captchaResponse} is verified successfully, and false if an exception occurs or if the
     *         API request fails
     */
    public boolean isVerificationSuccessful(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }

        try {
            String response = getApiResponse(captchaResponse);

            log.info("reCAPTCHA API response: " + response);
            JsonObject responseInJson = JsonUtils.parse(response).getAsJsonObject();

            if (responseInJson.has("error-codes")) {
                JsonArray errorCodes = responseInJson.get("error-codes").getAsJsonArray();
                log.warning("Error codes during reCAPTCHA verification: " + errorCodes.toString());
            }

            return Boolean.parseBoolean(responseInJson.get("success").toString());
        } catch (IOException e) {
            log.severe("reCAPTCHA request failure: " + e.getMessage());
            return false;
        } catch (Exception e) {
            log.severe("reCAPTCHA request failure: " + e.getStackTrace());
            return false;
        }
    }

    protected String getApiResponse(String captchaResponse) throws URISyntaxException, IOException {
        URIBuilder urlb = new URIBuilder(VERIFY_URL);
        urlb.setParameter("secret", SECRET_KEY);
        urlb.setParameter("response", captchaResponse);

        return ApiRequest.execute(urlb.build());
    }

}
