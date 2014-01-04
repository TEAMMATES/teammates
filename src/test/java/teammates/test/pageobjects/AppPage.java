package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;

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
	
	/** These are elements common to most pages in our app */
	@SuppressWarnings("unused")
	private void ____Common_page_elements___________________________________() {
	}
	@FindBy(id = "statusMessage")
	protected WebElement statusMessage;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[1]/a")
	protected WebElement instructorHomeTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[2]/a")
	protected WebElement instructorCoursesTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[3]/a")
	protected WebElement instructorEvaluationsTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[4]/a")
	protected WebElement instructorStudentsTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[5]/a")
	protected WebElement instructorHelpTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[6]/a")
	protected WebElement instructorLogoutLink;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[1]/a")
	protected WebElement studentHomeTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[2]/a")
	protected WebElement studentHelpTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[3]/a")
	protected WebElement studentLogoutLink;
	
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
		boolean isCorrectPageType = containsExpectedPageContents();
		
		if (isCorrectPageType) { return; }
		
		// To minimize test failures due to eventual consistency, we try to
		//  reload the page and compare once more.
		System.out.println("#### Incorrect page type: going to try reloading the page.");
		
		ThreadHelper.waitFor(2000);
		
		this.reloadPage();
		isCorrectPageType = containsExpectedPageContents();
		
		if (isCorrectPageType) { return; }
		
		System.out.println("######### Not in the correct page! ##########");
		throw new IllegalStateException("Not in the correct page!");
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
	 * Gives an AppPage instance based on the given Browser.
	 */
	public static AppPage getNewPageInstance(Browser currentBrowser){
		return getNewPageInstance(currentBrowser, GenericAppPage.class);
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
	 * Simply loads the given URL. 
	 */
	public AppPage navigateTo(Url url){
		browser.driver.get(url.toString());
		return this;
	}

	/**
	 * Fails if the new page content does not match content expected in a page of
	 * the type indicated by the parameter {@code newPageType}.
	 */
	public <T extends AppPage> T changePageType(Class<T> newPageType) {
		return createNewPage(browser, newPageType);
	}

	/**
	 * Waits until the page is fully loaded. Times out after 15 seconds.
	 */
	protected void waitForPageToLoad() {
		browser.selenium.waitForPageToLoad("15000");
	}

	/**
	 * Switches to the new browser window just opened.
	 */
	protected void switchToNewWindow() {
		String curWin = browser.driver.getWindowHandle();
		for (String handle : browser.driver.getWindowHandles()) {
			if (handle.equals(curWin))
				continue;
			browser.selenium.selectWindow(handle);
			browser.selenium.windowFocus();
		}
	}
	
	public void closeCurrentWindowAndSwitchToParentWindow() {
		browser.selenium.close();
		switchToParentWindow();
	}
	
	public void switchToParentWindow() {
		browser.selenium.selectWindow("null");
		browser.selenium.windowFocus();
	}

	public void reloadPage() {
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
		instructorCoursesTab.click();
		waitForPageToLoad();
		return this;
	}

	/**
	 * Equivalent to clicking the 'Evaluations' tab on the top menu of the page.
	 * @return the loaded page.
	 */
	public AppPage loadEvaluationsTab() {
		instructorEvaluationsTab.click();
		waitForPageToLoad();
		return this;
	}

	/**
	 * Equivalent to clicking the 'logout' link in the top menu of the page.
	 * @return 
	 */
	public AppPage logout(){
		logout(browser);
		return this;
	}
	
	/**
	 * Equivalent to clicking the 'logout' link in the top menu of the page.
	 */
	public static void logout(Browser currentBrowser){
		currentBrowser.driver.get(TestProperties.inst().TEAMMATES_URL + Const.ViewURIs.LOGOUT);
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

	
	/**
	 * This can be used to save pages which can later be used as the 'expected'
	 * in UI test cases. After saving the file, remember to edit it manually and
	 *  replace the version number in the page footer with the string 
	 * "{$version}". so that the test can insert the correct version number 
	 * before comparing the 'expected' with the 'actual.
	 *  e.g., replace "V4.55" in the page footer by "V{$version}".
	 *  @param filePath If the full path is not given, it will be saved in the
	 *  {@code Common.TEST_PAGES_FOLDER} folder. In that case, the parameter
	 *  value should start with "/". e.g., "/instructorHomePage.html".
	 */
	public void saveCurrentPage(String filePath) {
		if(filePath.startsWith("/")){
			filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
		}
		try {
		String pageSource = getPageSource();
		FileWriter output = new FileWriter(new File(filePath));
		output.write(pageSource);
			output.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void click(By by) {
		WebElement element = browser.driver.findElement(by);
		element.click();
	}
	
	public String getElementAttribute(By locator, String attrName) {
		return browser.driver.findElement(locator).getAttribute(attrName);
	}
	
	protected void fillTextBox(WebElement textBoxElement, String value) {
		textBoxElement.clear();
		textBoxElement.sendKeys(value);
	}

	protected String getTextBoxValue(WebElement textBox) {
		return textBox.getAttribute("value");
	}

	/** 'check' the check box, if it is not already 'checked'.
	 * No action taken if it is already 'checked'.
	 */
	protected void markCheckBoxAsChecked(WebElement checkBox) {
		if(!checkBox.isSelected()){
			checkBox.click();
		}
	}

	/** 'uncheck' the check box, if it is already 'checked'.
	 * No action taken if it is not already 'checked'.
	 */
	protected void markCheckBoxAsUnchecked(WebElement checkBox) {
		if(checkBox.isSelected()){
			checkBox.click();
		}
	}

	/** 
	 * Selection is based on the value shown to the user. 
	 * Since selecting an option by clicking on the option doesn't work sometimes
	 * in Firefox, we simulate a user typing the value to select the option
	 * instead (i.e., we use the {@code sendKeys()} method). <br>
	 * <br>
	 * The method will fail with an AssertionError if the selected value is
	 * not the one we wanted to select.
	 */
	public void selectDropdownByVisibleValue(WebElement element, String value) {
		Select select = new Select(element);
		element.sendKeys(value);
		String selectedVisibleValue = select.getFirstSelectedOption().getText();
		assertEquals(value, selectedVisibleValue);
		element.sendKeys(Keys.RETURN);
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
		respondToAlertWithRetry(elementToClick, true);
		waitForPageToLoad();
		return this;
	}
	
	/**
	 * Clicks the element and clicks 'No' in the follow up dialog box. 
	 * Fails if there is no dialog box.
	 * @return the resulting page.
	 */
	public void clickAndCancel(WebElement elementToClick){
		respondToAlertWithRetry(elementToClick, false);
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
	
	/**
	 * @return True if there is a corresponding element for the given id.
	 */
	public boolean isElementPresent(String elementId) {
		try{
			browser.driver.findElement(By.id(elementId));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void verifyUnclickable(WebElement element){
		try {
			respondToAlertWithRetry(element, false);
			Assert.fail("This should not give an alert when clicked");
		} catch (NoAlertPresentException e) {
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
				assertEquals(splitString[row],this.getCellValueFromDataTable(row,column));
			}
		}
	}
	
	/**
	 * Verifies that the currently loaded page has the same HTML content as 
	 * the content given in the file at {@code filePath}. <br>
	 * The HTML is checked for logical equivalence, not text equivalence. 
	 * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
	 * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
	 * @return The page (for chaining method calls).
	 */
	public AppPage verifyHtml(String filePath) {
		if(filePath.startsWith("/")){
			filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
		}
		try {
			String actual = getPageSource();
			String expected = FileHelper.readFile(filePath);
			HtmlHelper.assertSameHtml(actual, expected);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/**
	 * Verifies that the currently loaded page has the same HTML content as 
	 * the content given in the file at {@code filePath}. <br>
	 * Since the verification is done after making an Ajax Request, the HTML is checked
	 * after "waitDuration", for "maxRetryCount" number of times.
	 * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
	 * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
	 * @return The page (for chaining method calls).
	 */
	public AppPage verifyHtmlAjax(String filePath) {
		int maxRetryCount = 5;
		int waitDuration = 1000;
		
		if(filePath.startsWith("/")){
			filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
		}
		
		String expectedString = "";
		
		try {
			expectedString = FileHelper.readFile(filePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		for(int i =0; i < maxRetryCount; i++) {
			ThreadHelper.waitFor(waitDuration);	
			try {
				String actual = getPageSource();
				if(HtmlHelper.areSameHtml(actual, expectedString)) {
					break;
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		
		return verifyHtml(filePath);
	}
	
	/**
	 * Also supports the expression "{*}" which will match any text.
	 * e.g. "team 1{*}team 2" will match "team 1 xyz team 2"
	 */
	public AppPage verifyContains(String searchString) {
		AssertHelper.assertContainsRegex(searchString, getPageSource());
		return this;
	}
		
	/**
	 * Verifies the status message in the page is same as the one specified.
	 * @return The page (for chaining method calls).
	 */
	public AppPage verifyStatus(String expectedStatus){
		boolean isSameStatus = expectedStatus.equals(this.getStatus());
		if(!isSameStatus){
			//try one more time (to account for delays in displaying the status message).
			ThreadHelper.waitFor(2000);
			assertEquals(expectedStatus, this.getStatus());
		}
		return this;
	}

	/**
	 * As of now, this simply verifies that the link is not broken. It does
	 * not verify whether the file content is as expected. To be improved.
	 */
	public void verifyDownloadLink(Url url) {
		//TODO: implement a better way to download a file and check content 
		// (may be using HtmlUnit as the Webdriver?)
		String beforeReportDownloadUrl = browser.driver.getCurrentUrl();
		browser.driver.get(url.toString());
		String afterReportDownloadUrl = browser.driver.getCurrentUrl();
		assertEquals(beforeReportDownloadUrl, afterReportDownloadUrl);
	}
	
	/**
	 * Verify if a file is downloadable based on the given url. If its downloadable,
	 * download the file and get the SHA-1 hex of it and verify the hex with the given 
	 * expected hash.
	 * 
	 * Compute the expected hash of a file from http://onlinemd5.com/ (SHA-1)
	 */
	public void verifyDownloadableFile(String url,String expectedHash) throws Exception {
		
		if (!url.startsWith("http") ){
			url = HOMEPAGE + url;
		}
		
		URL fileToDownload = new URL(url);
	    
        String localDownloadPath = System.getProperty("java.io.tmpdir");
        File downloadedFile = new File(localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
        
        if (downloadedFile.exists()){ 
        	downloadedFile.delete();
        }
        if (downloadedFile.canWrite() == false){ 
        	downloadedFile.setWritable(true);
        }
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet httpget = new HttpGet(fileToDownload.toURI());
        HttpParams httpRequestParameters = httpget.getParams();
        httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        httpget.setParams(httpRequestParameters);
 
        HttpResponse response = client.execute(httpget);
        FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
        response.getEntity().getContent().close();
 
        String downloadedFileAbsolutePath = downloadedFile.getAbsolutePath();
		assertEquals(new File(downloadedFileAbsolutePath).exists(), true);
		
		String actualHash = DigestUtils.shaHex(new FileInputStream(downloadedFile));
    	assertEquals(actualHash,expectedHash.toLowerCase());
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


	private void respondToAlertWithRetry(WebElement elementToClick, boolean isConfirm) {
		elementToClick.click();	
		//This method might fail at times due to a Selenium bug
		//  See https://code.google.com/p/selenium/issues/detail?id=3544
		//  The delay below is a temporary workaround to minimize the failure rate.
		ThreadHelper.waitFor(250);
		Alert alert = browser.driver.switchTo().alert();
		if(isConfirm){
			alert.accept();
		}else {
			alert.dismiss();
		}
	}

}
