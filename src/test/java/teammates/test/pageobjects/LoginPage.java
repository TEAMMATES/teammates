package teammates.test.pageobjects;



public abstract class LoginPage extends AppPage {
	
	public LoginPage(Browser browser){
		super(browser);
	}

	public abstract InstructorHomePage loginAsInstructor(String username, String password);
	public abstract AppPage loginAsInstructorUnsuccessfully(String userName, String password);
	
	public abstract StudentHomePage loginAsStudent(String username, String password);

	public abstract StudentCourseJoinConfirmationPage loginAsJoiningStudent(String username, String password);
	
	public abstract void loginAdminAsInstructor(
			String adminUsername, String adminPassword, String instructorUsername);



}
