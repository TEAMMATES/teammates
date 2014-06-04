package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class AccountsDbTest extends BaseComponentTestCase {

    private AccountsDb accountsDb = new AccountsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsDb.class);
    }
    
    @Test
    public void testGetAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        
       ______TS("typical success case");
        AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);
        
        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);
        
        ______TS("failure: null parameter");
        try {
            accountsDb.getAccount(null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    private void testGetInstructorAccounts() throws Exception {
        
        restoreTypicalDataInDatastore();
        DataBundle dataBundle = getTypicalDataBundle();
        
        List<AccountAttributes> instructorAccountsActual = accountsDb.getInstructorAccounts();
        
        List<AccountAttributes> typicalAccounts = new ArrayList<AccountAttributes>(dataBundle.accounts.values());
        List<AccountAttributes> instructorAccountsExpected = new ArrayList<AccountAttributes>();
        for (AccountAttributes acc : typicalAccounts) {
            if (acc.isInstructor) {
                instructorAccountsExpected.add(acc);
            }
        }
        assertEquals(10, instructorAccountsActual.size());
        TestHelper.isSameContentIgnoreOrder(instructorAccountsExpected, instructorAccountsActual);
    }
    
    @Test
    public void testCreateAccount() throws Exception {

        testGetInstructorAccounts();
        
        ______TS("typical success case");
        AccountAttributes a = new AccountAttributes();
        a.googleId = "test.account";
        a.name = "Test account Name";
        a.isInstructor = false;
        a.email = "fresh-account@email.com";
        a.institute = "National University of Singapore";
        accountsDb.createAccount(a);
            
        ______TS("success case: duplicate account");
        accountsDb.createAccount(a);
        
        ______TS("test persistence of latest entry");
        AccountAttributes accountDataTest = accountsDb.getAccount(a.googleId);
        assertFalse(accountDataTest.isInstructor);
        // Change a field
        accountDataTest.isInstructor = true;
        accountsDb.updateAccount(accountDataTest);
        // Re-retrieve
        accountDataTest = accountsDb.getAccount(a.googleId);
        assertTrue(accountDataTest.isInstructor);
        
        accountsDb.deleteAccount(a.googleId);

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.email = "invalid email";
        try {
            accountsDb.createAccount(a);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(FieldValidator.EMAIL_ERROR_MESSAGE,
                    "invalid email",
                    FieldValidator.REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        
        ______TS("failure: null parameter");
        try {
            accountsDb.createAccount(null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testEditAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        
        ______TS("typical success case");
        a.name = "Edited name";
        accountsDb.updateAccount(a);
        
        ______TS("non-existent account");
        
        try {
            a.googleId = "non.existent";
            accountsDb.updateAccount(a);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, edne.getMessage());
        }
        
        ______TS("failure: invalid parameters");
        
        a.googleId = "";
        a.email = "test-no-at-funny.com";
        a.name = "%asdf";
        a.institute = StringHelper.generateStringOfLength(65);
        
        try {
            accountsDb.updateAccount(a);
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(a.getInvalidityInfo()), ipe.getMessage());
        }
        
        // Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
        ______TS("failure: null parameter");
        try {
            accountsDb.updateAccount(null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        
        ______TS("typical success case");
        accountsDb.deleteAccount(a.googleId);
        
        AccountAttributes deleted = accountsDb.getAccount(a.googleId);
        assertNull(deleted);
        
        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.googleId);
        
        ______TS("failure null paramter");
        
        try {
            accountsDb.deleteAccount(null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }


    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = new AccountAttributes();
        a.googleId = "valid.googleId";
        a.name = "Valid Fresh Account";
        a.isInstructor = false;
        a.email = "valid@email.com";
        a.institute = "National University of Singapore";
        
        accountsDb.createAccount(a);
        return a;
    }
}
