package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AccountRequestFormPage;

/**
 * SUT: {@link Const.WebPageURIs#ACCOUNT_REQUEST_FORM_PAGE}.
 */
public class AccountRequestFormPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AccountRequestFormPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ACCOUNT_REQUEST_FORM_PAGE);
        AccountRequestFormPage requestFormPage = getNewPageInstance(url, AccountRequestFormPage.class);

        ______TS("no error is shown upon initialization and submission is enabled");

        requestFormPage.verifyNoErrorMessagePresent();
        requestFormPage.verifySubmitButtonEnabled();

        ______TS("show error and disable submission when selecting student account type");

        requestFormPage.selectAccountType(AccountRequestFormPage.STUDENT_ACCOUNT_TYPE_NAME);

        requestFormPage.verifyFieldErrorMessagePresent(AccountRequestFormPage.ACCOUNT_TYPE_FIELD_NAME,
                "This form is for instructors to submit only.");
        requestFormPage.verifySubmitButtonDisabled();

        ______TS("error is cleared and submission is enabled when selecting back instructor account type");

        requestFormPage.selectAccountType(AccountRequestFormPage.INSTRUCTOR_ACCOUNT_TYPE_NAME);

        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.ACCOUNT_TYPE_FIELD_NAME);
        requestFormPage.verifySubmitButtonEnabled();

        ______TS("show error when submitting the empty form");

        requestFormPage.clickSubmitButton();

        requestFormPage.verifyStatusMessage("Ensure the form is valid before submission.");
        requestFormPage.waitForPageToLoad();
        requestFormPage.verifyAllEmptyFieldErrorMessagesPresent();
        requestFormPage.verifySubmitButtonEnabled(); // submit button is only disabled when selecting student account type

        ______TS("error is cleared when filling in form fields");

        requestFormPage.fillInField(AccountRequestFormPage.NAME_FIELD_NAME, "Invalid%Name");
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.NAME_FIELD_NAME);

        requestFormPage.fillInField(AccountRequestFormPage.INSTITUTE_FIELD_NAME, "Invalid Institute |");
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.INSTITUTE_FIELD_NAME);

        requestFormPage.fillInField(AccountRequestFormPage.COUNTRY_FIELD_NAME, "%Invalid Country");
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.COUNTRY_FIELD_NAME);

        requestFormPage.fillInField(AccountRequestFormPage.EMAIL_FIELD_NAME, "invalid_email@tmt");
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.EMAIL_FIELD_NAME);

        requestFormPage.fillInField(AccountRequestFormPage.HOME_PAGE_URL_FIELD_NAME, "https://www.google.com/");
        requestFormPage.fillInField(AccountRequestFormPage.COMMENTS_FIELD_NAME, "Is TEAMMATES free to use?");

        ______TS("submission fails when there are invalid fields");

        requestFormPage.clickSubmitButton();

        requestFormPage.verifyStatusMessage("Submission fails. See details at the bottom of the form.");
        requestFormPage.waitForPageToLoad();
        requestFormPage.verifyPageBottomErrorMessage(
                "Oops, some information is in incorrect format. Please fix them and submit again.");
        requestFormPage.verifyFieldErrorMessagePresent(AccountRequestFormPage.NAME_FIELD_NAME,
                "\"Invalid%Name\" is not acceptable to TEAMMATES as a/an person name");
        requestFormPage.verifyFieldErrorMessagePresent(AccountRequestFormPage.INSTITUTE_FIELD_NAME,
                "\"Invalid Institute |\" is not acceptable to TEAMMATES as a/an university/school/institute name");
        requestFormPage.verifyFieldErrorMessagePresent(AccountRequestFormPage.COUNTRY_FIELD_NAME,
                "\"%Invalid Country\" is not acceptable to TEAMMATES as a/an country name");
        requestFormPage.verifyFieldErrorMessagePresent(AccountRequestFormPage.EMAIL_FIELD_NAME,
                "\"invalid_email@tmt\" is not acceptable to TEAMMATES as a/an email");
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.HOME_PAGE_URL_FIELD_NAME);
        requestFormPage.verifyFieldErrorMessageNotPresent(AccountRequestFormPage.COMMENTS_FIELD_NAME);

        ______TS("submission fails when the provided information conflicts with an existing account request");

        requestFormPage.fillInField(AccountRequestFormPage.NAME_FIELD_NAME, "Valid Name");
        requestFormPage.fillInField(AccountRequestFormPage.INSTITUTE_FIELD_NAME, "TEAMMATES Test Institute");
        requestFormPage.fillInField(AccountRequestFormPage.COUNTRY_FIELD_NAME, "Singapore");
        requestFormPage.fillInField(AccountRequestFormPage.EMAIL_FIELD_NAME, "RequestForm.existing@gmail.tmt");
        requestFormPage.clickSubmitButton();

        requestFormPage.verifyStatusMessage("Submission fails. See details at the bottom of the form.");
        requestFormPage.waitForPageToLoad();
        requestFormPage.verifyPageBottomErrorMessage(
                "Oops, your submission is unsuccessful because an account request already exists."
                        + " Please check if you have entered your personal information correctly."
                        + " If you think this shouldn't happen, please contact us.");

        ______TS("successful submission");

        requestFormPage.fillInField(AccountRequestFormPage.NAME_FIELD_NAME, "Jerald Harmon");
        requestFormPage.fillInField(AccountRequestFormPage.INSTITUTE_FIELD_NAME, "TEAMMATES Test Institute");
        requestFormPage.fillInField(AccountRequestFormPage.COUNTRY_FIELD_NAME, "Singapore");
        requestFormPage.fillInField(AccountRequestFormPage.EMAIL_FIELD_NAME, "jerald_harmon@gmail.tmt");
        requestFormPage.clickSubmitButton();

        requestFormPage.verifyStatusMessage(
                "Your submission is successful and the request will be processed within 24 hours.");
        assertNotNull(getAccountRequest("jerald_harmon@gmail.tmt", "TEAMMATES Test Institute, Singapore"));
        // TODO: verify it has navigated to the static index page

        // clean up
        BACKDOOR.deleteAccountRequest("jerald_harmon@gmail.tmt", "TEAMMATES Test Institute, Singapore");
    }

}
