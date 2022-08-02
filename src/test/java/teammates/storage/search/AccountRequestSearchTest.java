package teammates.storage.search;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.storage.api.AccountRequestsDb;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link AccountRequestsDb},
 *      {@link teammates.storage.search.AccountRequestSearchDocument}.
 */
public class AccountRequestSearchTest extends BaseSearchTest {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    @Test
    public void allTests() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequestAttributes ins1InCourse1 = dataBundle.accountRequests.get("instructor1OfCourse1");
        AccountRequestAttributes ins1InCourse2 = dataBundle.accountRequests.get("instructor1OfCourse2");
        AccountRequestAttributes ins2InCourse2 = dataBundle.accountRequests.get("instructor2OfCourse2");
        AccountRequestAttributes ins2InCourse3 = dataBundle.accountRequests.get("instructor2OfCourse3");
        AccountRequestAttributes insInUnregCourse = dataBundle.accountRequests.get("instructor5");
        AccountRequestAttributes insOfArchivedCourse = dataBundle.accountRequests.get("instructorOfArchivedCourse");
        AccountRequestAttributes approvedInstructor1 = dataBundle.accountRequests.get("approvedUnregisteredRequest1");
        AccountRequestAttributes approvedInstructor2 = dataBundle.accountRequests.get("approvedUnregisteredRequest2");
        AccountRequestAttributes submittedInstructor1 = dataBundle.accountRequests.get("submittedRequest1");
        AccountRequestAttributes submittedInstructor2 = dataBundle.accountRequests.get("submittedRequest2");
        AccountRequestAttributes rejectedInstructor1 = dataBundle.accountRequests.get("rejectedRequest1");

        ______TS("success: search for account requests; query string does not match anyone");

        List<AccountRequestAttributes> results =
                accountRequestsDb.searchAccountRequestsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for account requests; empty query string does not match anyone");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for account requests; query string matches some account requests");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"Instructor 1\"");
        verifySearchResults(results, ins1InCourse1, ins1InCourse2,
                approvedInstructor1, submittedInstructor1, rejectedInstructor1);

        ______TS("success: search for account requests; query string should be case-insensitive");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"InStRuCtOr 2\"");
        verifySearchResults(results, ins2InCourse2, ins2InCourse3, approvedInstructor2, submittedInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their name");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"Instructor 5 of CourseNoRegister\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for account requests; account requests should be searchable by their email");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("instr2@course2.tmt");
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for account requests; account requests should be searchable by their institute");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"TEAMMATES Test Institute 5\"");
        verifySearchResults(results, insOfArchivedCourse);

        ______TS("success: search for account requests; unregistered account requests should be searchable");

        results = accountRequestsDb.searchAccountRequestsInWholeSystem("approvedUnregisteredInstructor1@tmt.tmt");
        verifySearchResults(results, approvedInstructor1);

        ______TS("success: search for account requests; deleted account requests no longer searchable");

        accountRequestsDb.deleteAccountRequest(ins1InCourse1.getEmail(), ins1InCourse1.getInstitute());
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"instructor 1\"");
        verifySearchResults(results, ins1InCourse2, approvedInstructor1, submittedInstructor1, rejectedInstructor1);

        ______TS("success: search for account requests; account requests created without searchability unsearchable");

        accountRequestsDb.putEntity(ins1InCourse1);
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"instructor 1\"");
        verifySearchResults(results, ins1InCourse2, approvedInstructor1, submittedInstructor1, rejectedInstructor1);
    }

    @Test
    public void testSearchAccountRequest_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequestAttributes ins1InCourse2 = dataBundle.accountRequests.get("instructor1OfCourse2");
        AccountRequestAttributes ins2InCourse2 = dataBundle.accountRequests.get("instructor2OfCourse2");

        // there is search result before deletion
        List<AccountRequestAttributes> results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"of Course 2\"");
        verifySearchResults(results, ins1InCourse2, ins2InCourse2);

        // delete an account request
        accountRequestsDb.deleteAccountRequest(ins1InCourse2.getEmail(), ins1InCourse2.getInstitute());

        // the search result will change
        results = accountRequestsDb.searchAccountRequestsInWholeSystem("\"of Course 2\"");
        verifySearchResults(results, ins2InCourse2);

        // delete all account requests
        accountRequestsDb.deleteAccountRequest(ins2InCourse2.getEmail(), ins2InCourse2.getInstitute());

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

    /**
     * Verifies that search results match with expected output.
     *
     * @param actual the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<AccountRequestAttributes> actual,
            AccountRequestAttributes... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }

}
