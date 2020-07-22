package teammates.e2e.pageobjects;

/**
 * Page Object Model for the official Google Accounts login page.
 */
public class GoogleLoginPage extends LoginPage {

    private static final String EXPECTED_SNIPPET_SIGN_IN = "Sign in – Google accounts";
    private static final String ERROR_CANNOT_LOGIN = "Automated login not allowed on Google login page.";

    // Google blocks log in by automation.
    // This AppPage is for testing correct navigation and not for actual login.
    public GoogleLoginPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains(EXPECTED_SNIPPET_SIGN_IN);
    }

    @Override
    public void loginAsAdmin(String adminUsername, String adminPassword) {
        throw new RuntimeException(ERROR_CANNOT_LOGIN);
    }

    @Override
    public StudentHomePage loginAsStudent(String username, String password) {
        throw new RuntimeException(ERROR_CANNOT_LOGIN);
    }

    @Override
    public <T extends AppPage> T loginAsStudent(String username, String password, Class<T> typeOfPage) {
        throw new RuntimeException(ERROR_CANNOT_LOGIN);
    }
}
