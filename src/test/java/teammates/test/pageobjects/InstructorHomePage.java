package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



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

	public InstructorHelpPage clickHelpLink() {
		instructorHelpTab.click();
		waitForPageToLoad();
		switchToNewWindow();
		return changePageType(InstructorHelpPage.class);
	}
	
	public void clickHomeTab() {
		instructorHomeTab.click();
		waitForPageToLoad();
	}

	public WebElement getRemindLink(String courseId, String evalName) {
		return getLinkInRow("t_eval_remind", getEvaluationRowId(courseId, evalName));
	}
	
	public WebElement getPublishLink(String courseId, String evalName){
		return getLinkInRow("t_eval_publish", getEvaluationRowId(courseId, evalName));
	}
	
	public WebElement getUnpublishLink(String courseId, String evalName){
		return getLinkInRow("t_eval_unpublish", getEvaluationRowId(courseId, evalName));
	}
	
	public WebElement getDeleteEvalLink(String courseId, String evalName){
		return getLinkInRow("t_eval_delete", getEvaluationRowId(courseId, evalName));
	}
	
	public WebElement getDeleteCourseLink(String courseId){
		return getLinkInRow("t_course_delete", getCourseRowId(courseId));
	}
	
	private WebElement getLinkInRow(String elementClassNamePrefix, int rowId){
		return browser.driver.findElement(By.className(elementClassNamePrefix + rowId));
	}

	private int getEvaluationRowId(String courseId, String evalName) {
		int courseRowID = getCourseRowId(courseId);
		if (courseRowID == -1)
			return -2;
		String template = "//div[@id='course%d']//table[@class='dataTable']//tr[@id='evaluation%d']";
		int max = (Integer) (browser.selenium)
				.getXpathCount("//div//table[@class='dataTable']//tr");
		for (int id = 0; id < max; id++) {
			if (getElementText(
					By.xpath(String.format(template + "//td[1]", courseRowID,
							id))).equals(evalName)) {
				return id;
			}
		}
		return -1;
	}
	
	private int getCourseRowId(String courseId) {
		int id = 0;
		while (isElementPresent(By.id("course" + id))) {
			if (getElementText(
					By.xpath("//div[@id='course" + id
							+ "']/div[@class='result_homeTitle']/h2"))
					.startsWith("[" + courseId + "]")) {
				return id;
			}
			id++;
		}
		return -1;
	}
	
	private String getElementText(By locator) {
		if (!isElementPresent(locator))
			return "";
		return browser.driver.findElement(locator).getText();
	}
	

}
