package teammates.logic.external;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import teammates.common.util.HttpRequest;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

import tools.jackson.databind.JsonNode;

/**
 * Google-based reCAPTCHA verifier service.
 *
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">reCAPTCHA user response verification API</a>
 */
public class GoogleRecaptchaService implements RecaptchaService {

    /** The Google reCAPTCHA API URL to verify the response token. */
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final Logger log = Logger.getLogger();

    private final String secretKey;

    public GoogleRecaptchaService(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean isVerificationSuccessful(String captchaResponse) {
        if (secretKey == null || secretKey.isEmpty()) {
            return false;
        }

        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }

        try {
            String response = getApiResponse(captchaResponse, secretKey);
            JsonNode responseInJson = JsonUtils.parse(response);

            if (responseInJson.has("error-codes")) {
                log.warning("Error codes during reCAPTCHA verification: " + responseInJson.get("error-codes"));
            }

            return responseInJson.path("success").asBoolean(false);
        } catch (Exception e) {
            log.severe("", e);
            return false;
        }
    }

    String getApiResponse(String captchaResponse, String secretKey) throws URISyntaxException, IOException {
        URIBuilder urlb = new URIBuilder(VERIFY_URL);
        urlb.setParameter("secret", secretKey);
        urlb.setParameter("response", captchaResponse);

        return HttpRequest.executeGetRequest(urlb.build());
    }

}
