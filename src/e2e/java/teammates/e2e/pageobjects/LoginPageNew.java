package teammates.e2e.pageobjects;

public abstract class LoginPageNew extends AppPageNew {

    public LoginPageNew(Browser browser) {
        super(browser);
    }

//    public abstract InstructorHomePage loginAsInstructor(String username, String password);

//    public abstract <T extends AppPageNew> T loginAsInstructor(String username, String password, Class<T> typeOfPage);

//    public abstract AppPageNew loginAsInstructorUnsuccessfully(String userName, String password);

    public abstract StudentHomePageNew loginAsStudent(String username, String password);

    public abstract <T extends AppPageNew> T loginAsStudent(String username, String password, Class<T> typeOfPage);

//    public abstract StudentCourseJoinConfirmationPageNew loginAsJoiningStudent(String username, String password);

//    public abstract InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(String username, String password);

//    public abstract InstructorHomePage loginAsJoiningInstructorByPassConfirmation(String username, String password);

    public abstract void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername);
}
