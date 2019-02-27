package teammates.e2e.pageobjects;

public abstract class LoginPageNew extends AppPageNew {

    public LoginPageNew(Browser browser) {
        super(browser);
    }

    public abstract StudentHomePageNew loginAsStudent(String username, String password);

    public abstract <T extends AppPageNew> T loginAsStudent(String username, String password, Class<T> typeOfPage);

    public abstract void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername);
}
