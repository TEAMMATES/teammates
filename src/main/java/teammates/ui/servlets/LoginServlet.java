package teammates.ui.servlets;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.ResponseMode;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Servlet that handles login.
 */
public class LoginServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();
    private static final Set<String> MICROSOFT_SCOPES = Set.of("openid", "email");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nextUrl = req.getParameter("nextUrl");
        String provider = req.getParameter("provider");
        if (provider == null) {
            log.warning("No auth provider specified in login request");
            resp.sendError(HttpStatus.SC_BAD_REQUEST, "Missing auth provider");
            resp.sendRedirect("/");
            return;
        }

        if (nextUrl == null) {
            nextUrl = "/";
        }

        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        if (Config.isDevServerLoginEnabled()) {
            resp.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", "/devServerLogin?nextUrl=" + nextUrl.replace("&", "%26"));
            log.request(req, HttpStatus.SC_MOVED_PERMANENTLY, "Redirect to dev server login page");
            return;
        }

        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        boolean isLoginNeeded = uic == null || !uic.isValid();
        if (!isLoginNeeded) {
            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to next URL");
            resp.sendRedirect(nextUrl);
            return;
        }

        // Determine which auth provider to use based on the request parameter
        switch (provider.toLowerCase()) {
        case Const.AuthProviderTypes.FIREBASE:
            log.request(req, HttpStatus.SC_MOVED_PERMANENTLY, "Redirect to web login page");

            // nextUrl query param is encoded to retain its full value as the nextUrl may contain query params
            resp.sendRedirect("/web/login?nextUrl="
                    + nextUrl.replace("?", "%3f").replace("&", "%26"));
            break;
        case Const.AuthProviderTypes.MICROSOFT_ENTRA:
            AuthState entraState = new AuthState(nextUrl, req.getSession().getId(), Const.AuthProviderTypes.MICROSOFT_ENTRA);
            String entraEncryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(entraState));
            AuthorizationRequestUrlParameters params = AuthorizationRequestUrlParameters
                    .builder(getMicrosoftRedirectUri(req), MICROSOFT_SCOPES)
                    .state(entraEncryptedState)
                    .responseMode(ResponseMode.FORM_POST)
                    .build();
            URL authorizationUrl = getMicrosoftClient().getAuthorizationRequestUrl(params);

            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Microsoft sign-in page");

            resp.sendRedirect(authorizationUrl.toString());
            break;
        case Const.AuthProviderTypes.GOOGLE:
            AuthState googleState = new AuthState(nextUrl, req.getSession().getId(), Const.AuthProviderTypes.GOOGLE);
            String googleEncryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(googleState));
            AuthorizationCodeRequestUrl googleAuthorizationUrl = getGoogleAuthorizationFlow().newAuthorizationUrl();
            googleAuthorizationUrl.setRedirectUri(getRedirectUri(req));
            googleAuthorizationUrl.setState(googleEncryptedState);

            log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Google sign-in page");

            resp.sendRedirect(googleAuthorizationUrl.build());
            break;
        default:
            log.warning("Unknown auth provider: " + provider);
            resp.sendError(HttpStatus.SC_BAD_REQUEST, "Unknown auth provider: " + provider);
            break;
        }
    }

}
