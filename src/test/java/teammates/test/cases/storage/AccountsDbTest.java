package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
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
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.shortName = "test acc na";
        spa.email = "test@personal.com";
        spa.gender = Const.GenderTypes.MALE;
        spa.country = "test.country";
        spa.institute = "institute";
        spa.moreInfo = "this is more info";
        
        a.googleId = "test.account";
        a.name = "Test account Name";
        a.isInstructor = false;
        a.email = "fresh-account@email.com";
        a.institute = "National University of Singapore";
        a.studentProfile = spa;
        
        accountsDb.createAccount(a);
            
        ______TS("success case: duplicate account");
        accountsDb.createAccount(a);
        
        ______TS("test persistence of latest entry");
        AccountAttributes accountDataTest = accountsDb.getAccount(a.googleId);
        
        assertEquals(spa.shortName, accountDataTest.studentProfile.shortName);
        // ensure that the institutes are different for now...
        assertEquals(spa.institute, accountDataTest.studentProfile.institute);
        assertEquals(a.institute, accountDataTest.institute);
        assertEquals(spa.email, accountDataTest.studentProfile.email);
        
        assertFalse(accountDataTest.isInstructor);
        // Change a field
        accountDataTest.isInstructor = true;
        accountDataTest.studentProfile.gender = Const.GenderTypes.FEMALE;
        accountsDb.updateAccount(accountDataTest);
        // Re-retrieve
        accountDataTest = accountsDb.getAccount(a.googleId);
        assertTrue(accountDataTest.isInstructor);
        assertEquals(Const.GenderTypes.FEMALE, accountDataTest.studentProfile.gender);
        
        ______TS("success: modified date does not change if profile is not changed");
        
        accountDataTest = accountsDb.getAccount(a.googleId);
        accountDataTest.institute = "new institute";
        Date expectedModifiedDate = accountDataTest.studentProfile.modifiedDate;
        
        accountsDb.updateAccount(accountDataTest);
        a = accountsDb.getAccount(a.googleId);
        
        // ensure update was successful
        assertEquals(accountDataTest.institute, a.institute);
        // ensure profile was not updated
        assertEquals(expectedModifiedDate, a.studentProfile.modifiedDate);
        
        
        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.email = "invalid email";
        try {
            accountsDb.createAccount(a);
            signalFailureToDetectException(" - InvalidParametersException");
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
            signalFailureToDetectException(" - AssertionError");
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
            signalFailureToDetectException(" - EntityDoesNotExistException");
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
            signalFailureToDetectException(" - InvalidParametersException");
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(a.getInvalidityInfo()), ipe.getMessage());
        }
        
        // Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
        ______TS("failure: null parameter");
        try {
            accountsDb.updateAccount(null);
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    @Test 
    public void testGetStudentProfile() throws Exception{
        
        ______TS("success case");
        AccountAttributes a = createNewAccount();
        a = accountsDb.getAccount(a.googleId);
        StudentProfileAttributes spa = accountsDb.getStudentProfile(a.googleId);
        
        assertEquals(a.studentProfile.toString(), spa.toString());
        
        ______TS("non-existent account");
        
        assertNull(accountsDb.getStudentProfile("non-eXisTent"));
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
            signalFailureToDetectException(" - AssertionError");
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
        a.studentProfile = new StudentProfileAttributes();
        a.studentProfile.institute = "National University of Singapore";
        
        accountsDb.createAccount(a);
        return a;
    }
}
