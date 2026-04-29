package teammates.it.storage.sqlsearch;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.AssertHelper;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_typicalCase_success() throws Exception {
        AccountRequest ins1General = typicalBundle.accountRequests.get("instructor1");
        AccountRequest ins2General = typicalBundle.accountRequests.get("instructor2");
        AccountRequest ins1InCourse1 = typicalBundle.accountRequests.get("instructor1OfCourse1");
        AccountRequest ins2InCourse1 = typicalBundle.accountRequests.get("instructor2OfCourse1");
        AccountRequest ins1InCourse2 = typicalBundle.accountRequests.get("instructor1OfCourse2");
        AccountRequest ins2InCourse2 = typicalBundle.accountRequests.get("instructor2OfCourse2");
        AccountRequest ins1InCourse3 = typicalBundle.accountRequests.get("instructor1OfCourse3");
        AccountRequest ins2InCourse3 = typicalBundle.accountRequests.get("instructor2OfCourse3");
        AccountRequest insInUnregCourse = typicalBundle.accountRequests.get("instructor3");
        AccountRequest unregisteredInstructor1 = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest unregisteredInstructor2 = typicalBundle.accountRequests.get("unregisteredInstructor2");

        ______TS("success: search for account requests; query string does not match anyone");

        List<AccountRequest> results = accountRequestsDb.searchAccountRequestsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for account requests; empty query string does not match anyone");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for account requests; query string should be case-insensitive");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("InStRuCtOr 2");
        verifySearchResults(results, ins2General, ins2InCourse1, ins2InCourse2, ins2InCourse3, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their name");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 3 of CourseNoRegister");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for account requests; account requests should be searchable by their email");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("instr2@course2.tmt");
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for account requests; account requests should be searchable by their institute");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("TEAMMATES Test Institute 2");
        verifySearchResults(results, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their comments");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Comments for account request from instructor2");
        verifySearchResults(results, ins2General);

        ______TS("success: search for account requests; account requests should be searchable by their status");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("registered");
        verifySearchResults(results, ins2General, unregisteredInstructor1, unregisteredInstructor2);

        ______TS("success: search for account requests; unregistered account requests should be searchable");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("unregisteredinstructor1@gmail.tmt");
        verifySearchResults(results, unregisteredInstructor1);

        ______TS("success: search for account requests; deleted account requests no longer searchable");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1");
        verifySearchResults(results, ins1General, ins1InCourse1, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        accountRequestsDb.deleteAccountRequest(ins1InCourse1);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1");
        verifySearchResults(results, ins1General, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        accountRequestsDb.deleteAccountRequest(ins1InCourse2);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1");
        verifySearchResults(results, ins1General, ins1InCourse3, unregisteredInstructor1);

        accountRequestsDb.deleteAccountRequest(ins1InCourse3);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1");
        verifySearchResults(results, ins1General, unregisteredInstructor1);
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() throws Exception {
        List<AccountRequest> results = accountRequestsDb.searchAccountRequestsInWholeSystem("_");
        verifySearchResults(results);

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("%");
        verifySearchResults(results);
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_sqlInjectionInput_shouldNotAffectData() throws Exception {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute",
                AccountRequestStatus.PENDING, "comments");
        accountRequestsDb.createAccountRequest(accountRequest);

        String searchInjection = "institute'; DROP TABLE account_requests; --";
        List<AccountRequest> results = accountRequestsDb.searchAccountRequestsInWholeSystem(searchInjection);
        verifySearchResults(results);

        AccountRequest actual = accountRequestsDb.getAccountRequest(accountRequest.getId());
        assertEquals(accountRequest, actual);
    }

    /**
     * Verifies that search results match expected output.
     */
    private static void verifySearchResults(List<AccountRequest> actual, AccountRequest... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
