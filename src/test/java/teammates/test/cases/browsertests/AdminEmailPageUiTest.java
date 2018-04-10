package teammates.test.cases.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AdminEmailPage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_EMAIL_COMPOSE_PAGE},
 *      {@link Const.ActionURIs#ADMIN_EMAIL_SENT_PAGE},
 *      {@link Const.ActionURIs#ADMIN_EMAIL_DRAFT_PAGE},
 *      {@link Const.ActionURIs#ADMIN_EMAIL_TRASH_PAGE}.
 */
public class AdminEmailPageUiTest extends BaseUiTestCase {

    private static final int ADMIN_EMAIL_TABLE_NUM_COLUMNS = 5;

    private AdminEmailPage emailPage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
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
                        createUrl(Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE), AdminEmailPage.class);
        assertTrue(isEmailComposeElementsPresent());

        ______TS("send email - no recipient");

        emailPage.clickSendButton();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals("Error : No receiver address or file given");

        ______TS("send email - recipient email format error");

        emailPage.inputRecipient("recipient");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email Content");
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageRecipientEmailFormatError("recipient"));

        ______TS("send email - no subject");

        emailPage.clearRecipientBox();
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.clearSubjectBox();
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageNoSubject());

        ______TS("send email - success");

        emailPage.inputSubject("Email Subject");
        emailPage.clickSendButton();
        assertFalse(hasErrorMessage());
        assertTrue(isEmailComposeElementsPresent());
        emailPage.waitForTextsForAllStatusMessagesToUserEquals("Email will be sent within an hour to recipient@email.tmt");

        ______TS("send email to group - invalid file type");

        emailPage.clearRecipientBox();
        emailPage.clearSubjectBox();
        emailPage.inputGroupRecipient("invalidGroupList.xlsx");
        emailPage.waitForTextsForAllStatusMessagesToUserEquals("Group receiver list upload failed. Please try again.");

        ______TS("send email to group - no subject");

        emailPage.clearRecipientBox();
        emailPage.clearSubjectBox();
        emailPage.inputGroupRecipient("validGroupList.txt");
        emailPage.inputEmailContent("Email Content");
        String groupListFileKey = emailPage.getGroupListFileKey();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Group receiver list successfully uploaded to Google Cloud Storage");
        verifyGroupListFileKey(groupListFileKey);
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageNoSubject());
        deleteGroupListFile(groupListFileKey);

        ______TS("send email to group - success");

        emailPage.clearRecipientBox();
        emailPage.clearSubjectBox();
        emailPage.inputGroupRecipient("validGroupList.txt");
        groupListFileKey = emailPage.getGroupListFileKey();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Group receiver list successfully uploaded to Google Cloud Storage");
        verifyGroupListFileKey(groupListFileKey);
        emailPage.inputSubject("Email Subject");
        emailPage.inputEmailContent("Email Content");
        emailPage.clickSendButton();
        assertFalse(hasErrorMessage());
        assertTrue(isEmailComposeElementsPresent());
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Email will be sent within an hour to uploaded group receiver's list.");
        deleteGroupListFile(groupListFileKey);

        ______TS("send email to groupmode and addressmode - no subject");

        emailPage.clearRecipientBox();
        emailPage.clearSubjectBox();
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputEmailContent("Email Content");
        emailPage.inputGroupRecipient("validGroupList.txt");
        groupListFileKey = emailPage.getGroupListFileKey();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Group receiver list successfully uploaded to Google Cloud Storage");
        verifyGroupListFileKey(groupListFileKey);
        emailPage.clearSubjectBox();
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageNoSubject());
        deleteGroupListFile(groupListFileKey);

        ______TS("send email to groupmode and addressmode - success");

        emailPage.clearRecipientBox();
        emailPage.clearSubjectBox();
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputSubject("Email Subject");
        emailPage.inputEmailContent("Email Content");
        emailPage.inputGroupRecipient("validGroupList.txt");
        groupListFileKey = emailPage.getGroupListFileKey();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Group receiver list successfully uploaded to Google Cloud Storage");
        verifyGroupListFileKey(groupListFileKey);
        emailPage.clickSendButton();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Email will be sent within an hour to uploaded group receiver's list.",
                "Email will be sent within an hour to recipient@email.tmt");
        deleteGroupListFile(groupListFileKey);

        ______TS("save email - success");

        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email to save");
        emailPage.clickSaveButton();
        emailPage.waitForTextsForAllStatusMessagesToUserEquals("Email draft has been saved");
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
        return emailPage.getTextsForAllStatusMessagesToUser().contains(
                getPopulatedErrorMessage(
                    FieldValidator.EMAIL_ERROR_MESSAGE, recipientName,
                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.EMAIL_MAX_LENGTH));
    }

    private boolean hasStatusMessageNoSubject() throws Exception {
        return emailPage.getTextsForAllStatusMessagesToUser().contains(
                getPopulatedEmptyStringErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                    FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.EMAIL_SUBJECT_MAX_LENGTH));
    }

    private boolean hasErrorMessage() {
        return emailPage.isElementPresent(By.className("alert-danger"));
    }

    /**
     * This method only checks if the email sent data table is displayed correctly
     * i.e, table headers are correct.
     * It does not test for the table content.
     */
    private boolean isEmailSentDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }

    /**
     * This method only checks if the email draft data table is displayed correctly
     * i.e, table headers are correct.
     * It does not test for the table content.
     */
    private boolean isEmailDraftDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }

    /**
     * This method only checks if the email trash data table is displayed correctly
     * i.e, table headers are correct.
     * It does not test for the table content.
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
        List<String> actualSessionTableHeaders = new ArrayList<>();

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

    private void verifyGroupListFileKey(String key) {
        assertTrue(BackDoor.isGroupListFileKeyPresentInGcs(key));
    }

    private void deleteGroupListFile(String key) {
        BackDoor.deleteGroupListFile(key);
    }

}
