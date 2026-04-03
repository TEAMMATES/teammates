package teammates.ui.servlets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.auth.GoogleOidcTokenVerifier;
import teammates.sqllogic.core.AccountsLogic;
import teammates.storage.sqlentity.Account;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link OAuth2CallbackServlet}.
 */
public class OAuth2CallbackServletTest extends BaseTestCase {

    @Test
    public void testDoGet_googleCallback_resolvesAccountWithinTransaction() throws Exception {
        AuthorizationCodeFlow authorizationFlow = mock(AuthorizationCodeFlow.class);
        AuthorizationCodeTokenRequest tokenRequest = mock(AuthorizationCodeTokenRequest.class);
        GoogleTokenResponse tokenResponse = mock(GoogleTokenResponse.class);
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);
        AccountsLogic accountsLogic = mock(AccountsLogic.class);
        Account account = new Account("Test User", "user@example.com");
        AtomicBoolean transactionActive = new AtomicBoolean(false);

        when(authorizationFlow.newTokenRequest("test-code")).thenReturn(tokenRequest);
        when(tokenRequest.setRedirectUri(anyString())).thenReturn(tokenRequest);
        when(tokenRequest.execute()).thenReturn(tokenResponse);
        when(tokenResponse.getIdToken()).thenReturn("test-id-token");
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getIssuer()).thenReturn("https://accounts.google.com");
        when(payload.getSubject()).thenReturn("subject-123");
        when(payload.getEmail()).thenReturn("user@example.com");
        when(payload.get("name")).thenReturn("Test User");
        when(accountsLogic.resolveOrCreateAccountFromOidc(
                "https://accounts.google.com", "subject-123", "user@example.com",
                "Test User", Const.LoginProviders.GOOGLE))
                .thenAnswer(invocation -> {
                    assertTrue("Account resolution should occur inside an active transaction", transactionActive.get());
                    return account;
                });

        try (MockedStatic<Config> config = mockStatic(Config.class, Mockito.withSettings()
                .defaultAnswer(Answers.CALLS_REAL_METHODS));
                MockedStatic<GoogleOidcTokenVerifier> oidcVerifier = mockStatic(GoogleOidcTokenVerifier.class);
                MockedStatic<AccountsLogic> accountsLogicStatic = mockStatic(AccountsLogic.class);
                MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class)) {
            config.when(Config::isUsingFirebase).thenReturn(false);
            oidcVerifier.when(() -> GoogleOidcTokenVerifier.verify("test-id-token")).thenReturn(idToken);
            accountsLogicStatic.when(AccountsLogic::inst).thenReturn(accountsLogic);
            hibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> {
                transactionActive.set(true);
                return null;
            });
            hibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> {
                transactionActive.set(false);
                return null;
            });
            hibernateUtil.when(HibernateUtil::rollbackTransaction).thenAnswer(invocation -> {
                transactionActive.set(false);
                return null;
            });

            OAuth2CallbackServlet servlet = new StubOAuth2CallbackServlet(authorizationFlow);
            MockHttpServletRequest request = new QueryStringMockHttpServletRequest(
                    "GET", "https://localhost/oauth2callback", buildGoogleQueryString("/next"));
            MockHttpServletResponse response = new RedirectFriendlyMockHttpServletResponse();

            servlet.doGet(request, response);

            assertEquals("/next", response.getRedirectUrl());
            assertEquals(1, response.getCookies().size());
            assertFalse(response.getCookies().get(0).getValue().isEmpty());
            hibernateUtil.verify(HibernateUtil::beginTransaction, times(1));
            hibernateUtil.verify(HibernateUtil::commitTransaction, times(1));
            hibernateUtil.verify(HibernateUtil::rollbackTransaction, never());
        }
    }

    @Test
    public void testDoGet_firebaseCallback_resolvesAccountWithinTransaction() throws Exception {
        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        FirebaseApp firebaseApp = mock(FirebaseApp.class);
        FirebaseOptions firebaseOptions = mock(FirebaseOptions.class);
        AccountsLogic accountsLogic = mock(AccountsLogic.class);
        Account account = new Account("user@example.com", "user@example.com");
        AtomicBoolean transactionActive = new AtomicBoolean(false);

        when(firebaseAuth.verifyIdToken("firebase-token")).thenReturn(firebaseToken);
        when(firebaseToken.getEmail()).thenReturn("user@example.com");
        when(firebaseToken.getUid()).thenReturn("firebase-uid");
        when(firebaseApp.getOptions()).thenReturn(firebaseOptions);
        when(firebaseOptions.getProjectId()).thenReturn("test-project");
        when(accountsLogic.resolveOrCreateAccountFromOidc(
                "https://securetoken.google.com/test-project", "firebase-uid", "user@example.com",
                "user@example.com", Const.LoginProviders.GOOGLE))
                .thenAnswer(invocation -> {
                    assertTrue("Account resolution should occur inside an active transaction", transactionActive.get());
                    return account;
                });

        try (MockedStatic<Config> config = mockStatic(Config.class, Mockito.withSettings()
                .defaultAnswer(Answers.CALLS_REAL_METHODS));
                MockedStatic<FirebaseAuth> firebaseAuthStatic = mockStatic(FirebaseAuth.class);
                MockedStatic<FirebaseApp> firebaseAppStatic = mockStatic(FirebaseApp.class);
                MockedStatic<AccountsLogic> accountsLogicStatic = mockStatic(AccountsLogic.class);
                MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class)) {
            config.when(Config::isUsingFirebase).thenReturn(true);
            firebaseAuthStatic.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            firebaseAppStatic.when(FirebaseApp::getInstance).thenReturn(firebaseApp);
            accountsLogicStatic.when(AccountsLogic::inst).thenReturn(accountsLogic);
            hibernateUtil.when(HibernateUtil::beginTransaction).thenAnswer(invocation -> {
                transactionActive.set(true);
                return null;
            });
            hibernateUtil.when(HibernateUtil::commitTransaction).thenAnswer(invocation -> {
                transactionActive.set(false);
                return null;
            });
            hibernateUtil.when(HibernateUtil::rollbackTransaction).thenAnswer(invocation -> {
                transactionActive.set(false);
                return null;
            });

            OAuth2CallbackServlet servlet = new StubOAuth2CallbackServlet(null);
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "https://localhost/oauth2callback");
            request.addParam("idToken", "firebase-token");
            request.addParam("nextUrl", "/firebase-next");
            MockHttpServletResponse response = new RedirectFriendlyMockHttpServletResponse();

            servlet.doGet(request, response);

            assertEquals("/firebase-next", response.getRedirectUrl());
            assertEquals(1, response.getCookies().size());
            assertFalse(response.getCookies().get(0).getValue().isEmpty());
            hibernateUtil.verify(HibernateUtil::beginTransaction, times(1));
            hibernateUtil.verify(HibernateUtil::commitTransaction, times(1));
            hibernateUtil.verify(HibernateUtil::rollbackTransaction, never());
        }
    }

    private static String buildGoogleQueryString(String nextUrl) throws IOException {
        String state = StringHelper.encrypt(JsonUtils.toCompactJson(new AuthServlet.AuthState(nextUrl, "1234")));
        return "code=test-code&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
    }

    private static final class StubOAuth2CallbackServlet extends OAuth2CallbackServlet {
        private final AuthorizationCodeFlow authorizationFlow;

        private StubOAuth2CallbackServlet(AuthorizationCodeFlow authorizationFlow) {
            this.authorizationFlow = authorizationFlow;
        }

        @Override
        AuthorizationCodeFlow getAuthorizationFlow() {
            return authorizationFlow;
        }
    }

    private static final class QueryStringMockHttpServletRequest extends MockHttpServletRequest {
        private final String queryString;

        private QueryStringMockHttpServletRequest(String method, String requestUrl, String queryString) {
            super(method, requestUrl);
            this.queryString = queryString;
        }

        @Override
        public String getQueryString() {
            return queryString;
        }
    }

    private static final class RedirectFriendlyMockHttpServletResponse extends MockHttpServletResponse {
        @Override
        public String encodeRedirectURL(String url) {
            return url;
        }
    }
}
