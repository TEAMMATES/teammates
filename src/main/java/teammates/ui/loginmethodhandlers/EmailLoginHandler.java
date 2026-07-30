package teammates.ui.loginmethodhandlers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.Provider;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.logic.core.MagicLinksLogic;
import teammates.storage.entity.MagicLink;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

/**
 * Login handler for email magic-link login.
 */
public class EmailLoginHandler implements LoginMethodHandler {

    private static final Logger log = Logger.getLogger();

    private final MagicLinksLogic magicLinksLogic;

    public EmailLoginHandler() {
        this(MagicLinksLogic.inst());
    }

    EmailLoginHandler(MagicLinksLogic magicLinksLogic) {
        this.magicLinksLogic = magicLinksLogic;
    }

    @Override
    public String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, AuthException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.EMAIL);
        String encryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(state));
        String redirectUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.EMAIL_LOGIN_PAGE)
                .withParam(Const.ParamsNames.AUTH_STATE, encryptedState)
                .toAbsoluteString();

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to email login page");

        return redirectUrl;
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, AuthException {
        String token = req.getParameter(Const.ParamsNames.TOKEN);
        if (token == null) {
            throw new AuthException("Missing token parameter in email login callback");
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            String message = String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId());
            throw new AuthException(message);
        }

        MagicLink magicLink;
        try {
            magicLink = magicLinksLogic.consumeMagicLink(token);
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            throw new AuthException("Invalid or expired magic link", e);
        }

        return new AuthResult(Provider.EMAIL, magicLink.getEmail(), null, magicLink.getEmail());
    }
}
