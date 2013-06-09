package teammates.test.pageobjects;



public class InstructorHomePage extends AppPage {

	public InstructorHomePage(Browser browser){
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return containsExpectedPageContents(getPageSource());
	}
	
	public static boolean containsExpectedPageContents(String pageSource){
		return pageSource.contains("<h1>Instructor Home</h1>");
	}

}
