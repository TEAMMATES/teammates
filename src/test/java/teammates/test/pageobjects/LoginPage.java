package teammates.test.pageobjects;

public abstract class LoginPage extends AppPage {

    public LoginPage(Browser browser) {
        super(browser);
    }

    public abstract InstructorHomePage loginAsInstructor(String username, String password);

    public abstract <T extends AppPage> T loginAsInstructor(String username, String password, Class<T> typeOfPage);

    public abstract AppPage loginAsInstructorUnsuccessfully(String userName, String password);

    public abstract StudentHomePage loginAsStudent(String username, String password);

    public abstract <T extends AppPage> T loginAsStudent(String username, String password, Class<T> typeOfPage);

    public abstract StudentCourseJoinConfirmationPage loginAsJoiningStudent(String username, String password);

    public abstract InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(String username, String password);

    public abstract InstructorHomePage loginAsJoiningInstructorByPassConfirmation(String username, String password);

    public abstract void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername);

}
