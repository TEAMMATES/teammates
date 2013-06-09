package teammates.test.pageobjects;

import org.openqa.selenium.By;


public class InstructorCourseEnrollPage extends AppPage {

	public InstructorCourseEnrollPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Enroll Students for");
	}

	public String getCourseId() {
		return browser.driver.findElement(By.id("courseid")).getText();
	}
	
	public InstructorCourseEnrollPage verifyContents(String courseId){
		getPageSource().contains("Enroll Students for "+courseId);
		return this;
	}

}
