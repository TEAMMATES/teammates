package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.pageobjects.AdminEmailPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminEmailPageUiTest extends BaseUiTestCase {
    
    private static final int ADMIN_EMAIL_TABLE_NUM_COLUMNS = 5;

    private static Browser browser;
    private static AdminEmailPage emailPage;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void allTests() throws Exception {
        testCompose();
        testSent();
        testDraft();
        testTrash();
    }
    
    private void testCompose() throws Exception {
        ______TS("email compose page");
        
        emailPage = loginAdminToPage(
                        browser, createUrl(Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE), AdminEmailPage.class);
        assertTrue(isEmailComposeElementsPresent());
        
        ______TS("send email - no recipient");
        
        emailPage.clickSendButton();
        emailPage.verifyStatus("Error : No reciver address or file given");
        
        ______TS("send email - recipient email format error");
        
        emailPage.inputRecipient("recipient");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email Content");
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageRecipientEmailFormatError("recipient"));
        
        ______TS("send email - no subject");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.clearSubjectBox();
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageNoSubject());
        
        ______TS("send email - success");
        
        emailPage.inputSubject("Email Subject");
        emailPage.clickSendButton();
        assertFalse(hasErrorMessage());
        assertTrue(isEmailComposeElementsPresent());
        
        ______TS("save email - success");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email to save");
        emailPage.clickSaveButton();
        emailPage.verifyStatus("Email draft has been saved");
    }

    private void testSent() {
        emailPage.clickSentTab();
        assertTrue(isEmailSentDataDisplayCorrect());
    }

    private void testDraft() {
        emailPage.clickDraftTab();
        assertTrue(isEmailDraftDataDisplayCorrect());
    }

    private void testTrash() {
        emailPage.clickTrashTab();
        assertTrue(isEmailTrashDataDisplayCorrect());
    }
    
    private boolean isEmailComposeElementsPresent() {
        return emailPage.isElementPresent(By.id("addressReceiverEmails"))
            && emailPage.isElementPresent(By.name("emailsubject"))
            && emailPage.isElementPresent(By.className("mce-tinymce"))
            && emailPage.isElementPresent(By.id("adminEmailBox"))
            && emailPage.isElementPresent(By.id("composeSubmitButton"))
            && emailPage.isElementPresent(By.id("composeSaveButton"));
    }
    
    private boolean hasStatusMessageRecipientEmailFormatError(String recipientName) throws Exception {
        return emailPage.getStatus().contains(
                getPopulatedErrorMessage(
                    FieldValidator.EMAIL_ERROR_MESSAGE, recipientName,
                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.EMAIL_MAX_LENGTH));
    }
    
    private boolean hasStatusMessageNoSubject() throws Exception {
        return emailPage.getStatus().equals(
                getPopulatedErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, "",
                    FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_EMPTY,
                    FieldValidator.EMAIL_SUBJECT_MAX_LENGTH));
    }
    
    private boolean hasErrorMessage() {
        return emailPage.isElementPresent(By.className("alert-danger"));
    }
    
    /**
     * This method only checks if the email sent data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailSentDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }
    
    /**
     * This method only checks if the email draft data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailDraftDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }
    
    /**
     * This method only checks if the email trash data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailTrashDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table"))
            && isEmptyTrashButtonPresent()
            && isEmailTableHeaderCorrect();
    }

    private boolean isEmailTableHeaderCorrect() {
        int numColumns = emailPage.getNumberOfColumnsFromDataTable(0); // 1 table
        
        if (numColumns != ADMIN_EMAIL_TABLE_NUM_COLUMNS) {
            return false;
        }
        
        List<String> expectedSessionTableHeaders = Arrays.asList("Action",
                                                                 "Address Receiver",
                                                                 "Group Receiver",
                                                                 "Subject",
                                                                 "Date");
        List<String> actualSessionTableHeaders = new ArrayList<String>();
        
        for (int i = 0; i < numColumns; i++) {
            actualSessionTableHeaders.add(emailPage.getHeaderValueFromDataTable(0, 0, i));
        }
        
        return actualSessionTableHeaders.equals(expectedSessionTableHeaders);
    }
    
    private boolean isEmptyTrashButtonPresent() {
        if (!emailPage.isElementPresent(By.className("btn-danger"))) {
            return false;
        }
        
        WebElement trashButton = browser.driver.findElement(By.className("btn-danger"));
        
        return trashButton.getText().contains("Empty Trash");
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
