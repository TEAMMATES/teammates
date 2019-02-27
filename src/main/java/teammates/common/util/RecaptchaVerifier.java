package teammates.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Used to handle the verification of the user's reCAPTCHA response.
 *
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">reCAPTCHA user response verification API</a>
 * @see <a href="https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha-what-should-i-do">Automated testing with reCAPTCHA v2</a>
 */
public final class RecaptchaVerifier {

    /** The Google reCAPTCHA API URL to verify the response token. */
    public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    /** The shared secret key between the TEAMMATES site and reCAPTCHA. */
    public static final String SECRET_KEY = Config.CAPTCHA_SECRET_KEY;

    private static final Logger log = Logger.getLogger();

    private RecaptchaVerifier() {
        // utility class
    }

    /**
     * Returns true if the {@code captchaResponse} token is verified successfully.
     * @param captchaResponse The user's captcha response from the client side
     * @return true if the response is verified successfully, and false if an exception occurs or if the request fails
     */
    public static boolean isVerificationSuccessful(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }

        try {
            URIBuilder urlb = new URIBuilder(VERIFY_URL);
            urlb.setParameter("secret", SECRET_KEY);
            urlb.setParameter("response", captchaResponse);

            HttpPost post = new HttpPost(urlb.build());

            // Execute and get the response.
            try (CloseableHttpClient client = HttpClients.createDefault();
                    CloseableHttpResponse resp = client.execute(post);
                    BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()))) {
                String response = br.lines().collect(Collectors.joining(System.lineSeparator()));

                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    log.info("reCAPTCHA verification request successful:" + response);
                    JsonObject responseInJson = JsonUtils.parse(response).getAsJsonObject();

                    if (responseInJson.has("error-codes")) {
                        JsonArray errorCodes = responseInJson.get("error-codes").getAsJsonArray();
                        log.warning("Error codes during reCAPTCHA verification: " + errorCodes.toString());
                    }

                    return Boolean.parseBoolean(responseInJson.get("success").toString());
                } else {
                    log.severe("reCAPTCHA verification request failure:" + response);
                    return false;
                }
            } catch (IOException | UnsupportedOperationException e) {
                log.severe("reCAPTCHA verification request failure: " + e.getMessage());
                return false;
            }
        } catch (URISyntaxException e) {
            log.severe("reCAPTCHA verification request failure: " + e.getMessage());
            return false;
        }
    }

}
