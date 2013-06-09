package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Constructor;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import teammates.common.Common;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;
import teammates.test.driver.Url;

/**
 * An abstract class that represents a browser-loaded page of the app and
 * provides ways to interact with it. Also contains methods to validate some
 * aspects of the page. .e.g, html page source. <br>
 * 
 * Note: We are using the PageObjects pattern here. 
 * https://code.google.com/p/selenium/wiki/PageObjects
 * 
 */
public abstract class AppPage {

	/**Home page of the application, as per test.properties file*/
	protected static final String HOMEPAGE = TestProperties.inst().TEAMMATES_URL;
	/** Browser instance the page is loaded into */
	protected Browser browser;
	
	@SuppressWarnings("unused")
	private void ____Common_page_elements___________________________________() {
	}
	@FindBy(id = "statusMessage")
	protected WebElement statusMessage;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[1]/a")
	protected WebElement homeTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[2]/a")
	protected WebElement coursesTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[3]/a")
	protected WebElement evaluationsTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[4]/a")
	protected WebElement helpTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[5]/a")
	protected WebElement logoutLink;
	
	@SuppressWarnings("unused")
	private void ____creation_and_navigation_______________________________() {
	}
	
	/**
	 * Used by subclasses to create a {@code AppPage} object to wrap around the
	 * given {@code browser} object. Fails if the page content does not match
	 * the page type, as defined by the sub-class.
	 */
	public AppPage(Browser browser) {
		this.browser = browser;
		if(!containsExpectedPageContents()){
			System.out.println("######### Not in the correct page! ##########");
			throw new IllegalStateException("Not in the correct page!");
		}
	}
	

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code typeOfPage}.
	 */
	public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Url url, Class<T> typeOfPage){
		currentBrowser.driver.get(url.toString());
		return createNewPage(currentBrowser, typeOfPage);
	}

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code typeOfPage}.
	 */
	public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Class<T> typeOfPage){
		return createNewPage(currentBrowser, typeOfPage);
	}

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code typeOfPage}.
	 */
	public static <T extends AppPage> T getNewPageInstance(String url, Class<T> typeOfPage){
		Browser b = new Browser();
		b.driver.get(url);
		return createNewPage(b, typeOfPage);
	}

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code typeOfDestinationPage}.
	 */
	public <T extends AppPage> T navigateTo(Url url, Class<T> typeOfDestinationPage){
		return getNewPageInstance(browser, url, typeOfDestinationPage);
	}

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code newPageType}.
	 */
	protected <T extends AppPage> T changePageType(Class<T> newPageType) {
		return createNewPage(browser, newPageType);
	}

	/**
	 * Waits until the page is fully loaded. Times out after 15 seconds.
	 */
	protected void waitForPageToLoad() {
		browser.selenium.waitForPageToLoad("15000");
	}

	public void reloadCurrentUrl() {
		browser.driver.get(browser.driver.getCurrentUrl());
	}

	/** Equivalent to pressing the 'back' button of the browser. <br>
	 * Fails if the page content does not match content expected in a page of
	 * the type indicated by the parameter {@code typeOfPreviousPage}.
	 */
	public <T extends AppPage> T goToPreviousPage(Class<T> typeOfPreviousPage) {
		browser.driver.navigate().back();
		waitForPageToLoad();
		return changePageType(typeOfPreviousPage);
	}

	/**
	 * Equivalent to clicking the 'Courses' tab on the top menu of the page.
	 * @return the loaded page.
	 */
	public AppPage loadCoursesTab() {
		coursesTab.click();
		waitForPageToLoad();
		return this;
	}

	/**
	 * Equivalent to clicking the 'Evaluations' tab on the top menu of the page.
	 * @return the loaded page.
	 */
	public AppPage loadEvaluationsTab() {
		evaluationsTab.click();
		waitForPageToLoad();
		return this;
	}

	/**
	 * Equivalent to clicking the 'logout' link in the top menu of the page.
	 */
	public void logout(){
		logout(browser);
	}
	
	/**
	 * Equivalent to clicking the 'logout' link in the top menu of the page.
	 */
	public static void logout(Browser currentBrowser){
		currentBrowser.driver.get(TestProperties.inst().TEAMMATES_URL + Common.JSP_LOGOUT);
		currentBrowser.isAdminLoggedIn = false;
	}
	
	@SuppressWarnings("unused")
	private void ____accessing_elements___________________________________() {
	}
	
	/**
	 * @return the HTML source of the currently loaded page.
	 */
	public String getPageSource() {
		return browser.driver.getPageSource();
	}

	
	public void click(By by) {
		WebElement element = browser.driver.findElement(by);
		element.click();
	}
	
	protected void fillTextBox(WebElement textBoxElement, String value) {
		textBoxElement.clear();
		textBoxElement.sendKeys(value);
	}

	protected String getTextBoxValue(WebElement textBox) {
		return textBox.getAttribute("value");
	}

	/**
	 * @return the status message in the page. Returns "" if there is no 
	 * status message in the page.
	 */
	public String getStatus() {
		return statusMessage == null? "" : statusMessage.getText();
	}

	/** 
	 * @return the value of the cell located at {@code (row,column)} 
	 * from a table (which is of type {@code class=dataTable}) in the page.
	 */
	public String getCellValueFromDataTable(int row, int column) {
		return browser.selenium.getTable("class=dataTable." + row + "." + column);
	}

	/**
	 * Clicks the element and clicks 'Yes' in the follow up dialog box. 
	 * Fails if there is no dialog box.
	 * @return the resulting page.
	 */
	public AppPage clickAndConfirm(WebElement elementToClick) {
		respondToAlert(elementToClick, true);
		return this;
	}
	
	/**
	 * Clicks the element and clicks 'No' in the follow up dialog box. 
	 * Fails if there is no dialog box.
	 * @return the resulting page.
	 */
	public void clickAndCancel(WebElement element){
		respondToAlert(element, false);
		waitForPageToLoad();
	}
	
	@SuppressWarnings("unused")
	private void ____verification_methods___________________________________() {
	}

	/** @return True if the page contains some basic elements expected in a page of the
	 * specific type. e.g., the top heading. 
	 */
	protected abstract boolean containsExpectedPageContents() ;

	/**
	 * @return True if there is a corresponding element for the given locator.
	 */
	public boolean isElementPresent(By by) {
		return browser.driver.findElements(by).size() != 0;
	}

	public void verifyUnclickable(WebElement element){
		try {
			respondToAlert(element, false);
			Assert.fail("This should not give an alert when clicked");
		} catch (NoAlertException e) {
			return;
		}
	}

	/**
	 * Compares selected column's rows with patternString to check the order of rows.
	 * This can be useful in checking if the table is sorted in a particular order.
	 * Separate rows using {*}
	 * e.g., {@code "{*}value 1{*}value 2{*}value 3" } <br>
	 * If you include "{*}" at the beginning of the pattern, it will not check the header row.
	 */
	public void verifyTablePattern(int column,String patternString){
		//TODO: This method API can be improved
		//patternString is split with {*} to separate the rows
		String[] splitString = patternString.split(java.util.regex.Pattern.quote("{*}"));
		for(int row=1;row<splitString.length;row++){
			//if a row is empty, it will not be asserted with
			//row starts from 1 to skip the header row, this requires patternString to start with {*}
			if(splitString[row].length()>0){
				assertEquals(this.getCellValueFromDataTable(row,column),splitString[row]);
			}
		}
	}
	
	/**
	 * Verifies that the currently loaded page has the same HTML content as 
	 * the content given in the file at {@code filePath}. <br>
	 * The HTML is checked for logical equivalence, not text equivalence. 
	 * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
	 * folder is assumed to be {@link Common.TEST_PAGES_FOLDER}. 
	 * @return The page (for chaining method calls).
	 */
	public AppPage verifyHtml(String filePath) {
		if(filePath.startsWith("/")){
			filePath = Common.TEST_PAGES_FOLDER + filePath;
		}
		try {
			String actual = getPageSource();
			String expected = Common.readFile(filePath).replace("{version}",
					TestProperties.inst().TEAMMATES_VERSION);
			HtmlHelper.assertSameHtml(actual, expected);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/**
	 * Verifies the status message in the page is same as the one specified.
	 * @return The page (for chaining method calls).
	 */
	public AppPage verifyStatus(String expectedStatus){
		assertEquals(expectedStatus, this.getStatus());
		return this;
	}
	@SuppressWarnings("unused")
	private void ____private_utility_methods________________________________() {
	}

	private static <T extends AppPage> T createNewPage(Browser currentBrowser,	Class<T> typeOfPage) {
		Constructor<T> constructor;
		try {
			constructor = typeOfPage.getConstructor(Browser.class);
			T page = constructor.newInstance(currentBrowser);
			PageFactory.initElements(currentBrowser.driver, page);
			return page;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void respondToAlert(WebElement elementToClick, boolean isConfirm) {
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		js.executeScript("window.confirm = function(msg){ delete(window.confirm); return "+isConfirm+";};");
		elementToClick.click();
	
		if ((Boolean) js
				.executeScript("return eval(window.confirm).toString()==" +
						"eval(function(msg){" +
							"delete(window.confirm); return "+isConfirm+";" +
						"}).toString()")) {
			// This means the click does not generate alert box
			js.executeScript("delete(window.confirm)");
			throw new NoAlertException(elementToClick.toString());
		}
	}
	

}
