package teammates.e2e.pageobjects;

/**
 * Abstract Page Object Model for the login page.
 */
public abstract class LoginPage extends AppPage {

    public LoginPage(Browser browser) {
        super(browser);
    }

    public abstract StudentHomePage loginAsStudent(String username, String password);

    public abstract <T extends AppPage> T loginAsStudent(String username, String password, Class<T> typeOfPage);

    public abstract void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername);
}
