package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AdminEmailPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminEmailPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminEmailPage emailPage;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test 
    public void allTests() throws Exception{    
        testCompose();
        testSent();
        testDraft();
        testTrash();
    }
    
    private void testCompose() {
        ______TS("email compose page");
        
        emailPage = loginAdminToPage(
                        browser, createUrl(Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE), AdminEmailPage.class);
        emailPage.verifyHtml("/adminEmailCompose.html");
        
        ______TS("send email - no recipient");
        
        emailPage.clickSendButton();
        emailPage.verifyHtmlMainContent("/adminEmailNoRecipient.html");
        
        ______TS("send email - recipient email format error");
        
        emailPage.inputRecipient("recipient");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email Content");
        emailPage.clickSendButton();
        emailPage.verifyHtmlMainContent("/adminEmailRecipientEmailFormatError.html");
        
        ______TS("send email - no subject");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.clearSubjectBox();
        emailPage.clickSendButton();
        emailPage.verifyHtmlMainContent("/adminEmailNoSubject.html");
        
        ______TS("send email - success");
        
        emailPage.inputSubject("Email Subject");
        emailPage.clickSendButton();
        emailPage.verifyHtmlMainContent("/adminEmailSendSuccess.html");
        
        ______TS("save email - success");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email to save");
        emailPage.clickSaveButton();
        emailPage.verifyHtmlMainContent("/adminEmailSaveSuccess.html");
    }

    private void testSent() {
        emailPage.clickSentTab();
        emailPage.verifyHtmlMainContent("/adminEmailSentPage.html");
    }

    private void testDraft() {
        emailPage.clickDraftTab();
        emailPage.verifyHtmlMainContent("/adminEmailDraftPage.html");
    }

    private void testTrash() {
        emailPage.clickTrashTab();
        emailPage.verifyHtmlMainContent("/adminEmailTrashPage.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
