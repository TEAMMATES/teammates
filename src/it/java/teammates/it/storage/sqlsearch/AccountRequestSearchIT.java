package teammates.it.storage.sqlsearch;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link AccountRequestsDb},
 *      {@link teammates.storage.search.AccountRequestSearchDocument}.
 */
public class AccountRequestSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final SqlDataBundle typicalBundle = getTypicalSqlDataBundle();
    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        putDocuments(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Test
    public void allTests() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequest ins1General = typicalBundle.accountRequests.get("instructor1");
        AccountRequest ins2General = typicalBundle.accountRequests.get("instructor2");
        AccountRequest ins1InCourse1 = typicalBundle.accountRequests.get("instructor1OfCourse1");
        AccountRequest ins2InCourse1 = typicalBundle.accountRequests.get("instructor2OfCourse1");
        AccountRequest ins1InCourse2 = typicalBundle.accountRequests.get("instructor1OfCourse2");
        AccountRequest ins2InCourse2 = typicalBundle.accountRequests.get("instructor2OfCourse2");
        AccountRequest ins1InCourse3 = typicalBundle.accountRequests.get("instructor1OfCourse3");
        AccountRequest ins2InCourse3 = typicalBundle.accountRequests.get("instructor2OfCourse3");
        AccountRequest insInUnregCourse = typicalBundle.accountRequests.get("instructor3");
        AccountRequest unregisteredInstructor1 =
                typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest unregisteredInstructor2 =
                typicalBundle.accountRequests.get("unregisteredInstructor2");

        ______TS("success: search for account requests; query string does not match anyone");

        List<AccountRequest> results =
                accountRequestsDb.searchAccountRequestsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for account requests; empty query string does not match anyone");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for account requests; query string matches some account requests");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"Instructor 1\"");
        verifySearchResults(results, ins1InCourse1, ins1InCourse2, ins1InCourse3, unregisteredInstructor1, ins1General);

        ______TS("success: search for account requests; query string should be case-insensitive");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"InStRuCtOr 2\"");
        verifySearchResults(results, ins2InCourse1, ins2InCourse2, ins2InCourse3, unregisteredInstructor2, ins2General);

        ______TS("success: search for account requests; account requests should be searchable by their name");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"Instructor 3 of CourseNoRegister\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for account requests; account requests should be searchable by their email");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("instr2@course2.tmt");
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for account requests; account requests should be searchable by their institute");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"TEAMMATES Test Institute 2\"");
        verifySearchResults(results, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their comments");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("Comments for account request from instructor2");
        verifySearchResults(results, ins2General);

        ______TS("success: search for account requests; account requests should be searchable by their status");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("registered");
        verifySearchResults(results, ins2General);

        ______TS("success: search for account requests; unregistered account requests should be searchable");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"unregisteredinstructor1@gmail.tmt\"");
        verifySearchResults(results, unregisteredInstructor1);

        ______TS("success: search for account requests; deleted account requests no longer searchable");

        accountRequestsDb.deleteAccountRequest(ins1InCourse1);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"instructor 1\"");
        verifySearchResults(results, ins1InCourse2, ins1InCourse3, unregisteredInstructor1, ins1General);

        ______TS("success: search for account requests; account requests created without searchability unsearchable");

        accountRequestsDb.createAccountRequest(ins1InCourse1);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"instructor 1\"");
        verifySearchResults(results, ins1InCourse2, ins1InCourse3, unregisteredInstructor1, ins1General);

        ______TS("success: search for account requests; deleting account request without deleting document:"
                + "document deleted during search, account request unsearchable");

        accountRequestsDb.deleteAccountRequest(ins2InCourse1);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"instructor 2\"");
        verifySearchResults(results, ins2InCourse2, ins2InCourse3, unregisteredInstructor2, ins2General);
    }

    @Test
    public void testSearchAccountRequest_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequest ins1InCourse2 = typicalBundle.accountRequests.get("instructor1OfCourse2");
        AccountRequest ins2InCourse2 = typicalBundle.accountRequests.get("instructor2OfCourse2");

        // there is search result before deletion
        List<AccountRequest> results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"of Course 2\"");
        verifySearchResults(results, ins1InCourse2, ins2InCourse2);

        // delete an account request
        accountRequestsDb.deleteAccountRequest(ins1InCourse2);

        // the search result will change
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"of Course 2\"");
        verifySearchResults(results, ins2InCourse2);

        // delete all account requests
        accountRequestsDb.deleteAccountRequest(ins2InCourse2);

        // there should be no search result
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"of Course 2\"");
        verifySearchResults(results);
    }

    @Test
    public void testSearchAccountRequest_noSearchService_shouldThrowException() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        assertThrows(SearchServiceException.class,
                () -> accountRequestsDb.searchAccountRequestsInWholeSystem("anything"));
    }

    @Test
    public void testSqlInjectionSearchAccountRequestsInWholeSystem() throws Exception {
        ______TS("SQL Injection test in searchAccountRequestsInWholeSystem");

        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestsDb.createAccountRequest(accountRequest);

        String searchInjection = "institute'; DROP TABLE account_requests; --";
        List<AccountRequest> actualInjection = accountRequestsDb.searchAccountRequestsInWholeSystem(searchInjection);
        assertEquals(typicalBundle.accountRequests.size(), actualInjection.size());

        AccountRequest actual = accountRequestsDb.getAccountRequest(accountRequest.getId());
        assertEquals(accountRequest, actual);
    }

    /**
     * Verifies that search results match with expected output.
     *
     * @param actual the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<AccountRequest> actual,
            AccountRequest... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }

}
