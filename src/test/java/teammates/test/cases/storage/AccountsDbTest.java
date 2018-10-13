package teammates.test.cases.storage;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseComponentTestCase {

    private AccountsDb accountsDb = new AccountsDb();

    @Test
    public void testGetAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("typical success case without");
        AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);

        ______TS("typical success with student profile");
        retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);

        ______TS("failure: null parameter");
        AssertionError ae = assertThrows(AssertionError.class, () -> accountsDb.getAccount(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        // delete created account
        accountsDb.deleteAccount(a.googleId);
    }

    @Test
    public void testGetInstructorAccounts() throws Exception {
        int numOfInstructors = 3;

        // a non-instructor account
        createNewAccount();

        List<AccountAttributes> instructorAccountsExpected = createInstructorAccounts(numOfInstructors);
        List<AccountAttributes> instructorAccountsActual = accountsDb.getInstructorAccounts();

        assertEquals(numOfInstructors, instructorAccountsActual.size());

        for (int i = 0; i < numOfInstructors; i++) {
            // remove the created/modified dates due to their unpredictable nature
            instructorAccountsExpected.get(i).createdAt = null;
            instructorAccountsActual.get(i).createdAt = null;
        }

        AssertHelper.assertSameContentIgnoreOrder(instructorAccountsExpected, instructorAccountsActual);

        deleteInstructorAccounts(numOfInstructors);
    }

    private List<AccountAttributes> createInstructorAccounts(
            int numOfInstructors) throws Exception {
        AccountAttributes a;
        List<AccountAttributes> result = new ArrayList<>();
        for (int i = 0; i < numOfInstructors; i++) {
            a = getNewAccountAttributes();
            a.googleId = "id." + i;
            a.isInstructor = true;
            accountsDb.createAccount(a);
            result.add(a);
        }
        return result;
    }

    private void deleteInstructorAccounts(int numOfInstructors) {
        String googleId;
        for (int i = 0; i < numOfInstructors; i++) {
            googleId = "id." + i;
            accountsDb.deleteAccount(googleId);
        }
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case (legacy data)");
        AccountAttributes a = AccountAttributes.builder()
                .withGoogleId("test.account")
                .withName("Test account Name")
                .withIsInstructor(false)
                .withEmail("fresh-account@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountsDb.createAccount(a);
        accountsDb.deleteAccount(a.googleId);

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.email = "invalid email";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsDb.createAccount(a));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        AssertionError ae = assertThrows(AssertionError.class, () -> accountsDb.createAccount(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    @Test
    public void testEditAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("typical edit success case");
        assertFalse(a.isInstructor);
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.googleId)
                        .withIsInstructor(true)
                        .build()
        );

        AccountAttributes actualAccount = accountsDb.getAccount(a.googleId);

        assertTrue(actualAccount.isInstructor);
        assertTrue(updatedAccount.isInstructor);

        ______TS("non-existent account");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder("non.existent")
                                .withIsInstructor(true)
                                .build()
                ));
        AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, ednee.getMessage());

        ______TS("failure: null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> accountsDb.updateAccount(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        accountsDb.deleteAccount(a.googleId);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("typical success case");
        AccountAttributes newAccount = accountsDb.getAccount(a.googleId);
        assertNotNull(newAccount);

        accountsDb.deleteAccount(a.googleId);

        AccountAttributes newAccountDeleted = accountsDb.getAccount(a.googleId);
        assertNull(newAccountDeleted);

        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.googleId);

        ______TS("failure null paramter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> accountsDb.deleteAccount(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = getNewAccountAttributes();
        accountsDb.createAccount(a);
        return a;
    }

    private AccountAttributes getNewAccountAttributes() {
        return AccountAttributes.builder()
                .withGoogleId("valid.googleId")
                .withName("Valid Fresh Account")
                .withIsInstructor(false)
                .withEmail("valid@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();
    }
}
