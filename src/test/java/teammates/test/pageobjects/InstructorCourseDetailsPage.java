package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;



public class InstructorCourseDetailsPage extends AppPage {

	public InstructorCourseDetailsPage(Browser browser){
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Course Details</h1>");
	}

	public String getCourseId() {
		return browser.driver.findElement(By.id("courseid")).getText();
	}

	public InstructorCourseDetailsPage verifyContents(String courseId) {
		assertEquals(courseId, this.getCourseId());
		return this;
	}

}
