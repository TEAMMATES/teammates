package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.entity.AccountRequest;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestSearchIT extends BaseTestCaseWithDatabaseAccess {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_typicalCase_success() {
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

        List<AccountRequest> results =
                inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("non-existent"));
        verifySearchResults(results);

        ______TS("success: search for account requests; empty query string does not match anyone");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem(""));
        verifySearchResults(results);

        ______TS("success: search for account requests; query string should be case-insensitive");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("InStRuCtOr 2"));
        verifySearchResults(results, ins2General, ins2InCourse1, ins2InCourse2, ins2InCourse3, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their name");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem(
                "Instructor 3 of CourseNoRegister"));
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for account requests; account requests should be searchable by their email");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("instr2@course2.tmt"));
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for account requests; account requests should be searchable by their institute");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem(
                "TEAMMATES Test Institute 2"));
        verifySearchResults(results, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their comments");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem(
                "Comments for account request from instructor2"));
        verifySearchResults(results, ins2General);

        ______TS("success: search for account requests; account requests should be searchable by their status");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("registered"));
        verifySearchResults(results, ins2General, unregisteredInstructor1, unregisteredInstructor2);

        ______TS("success: search for account requests; unregistered account requests should be searchable");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem(
                "unregisteredinstructor1@gmail.tmt"));
        verifySearchResults(results, unregisteredInstructor1);

        ______TS("success: search for account requests; deleted account requests no longer searchable");

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse1, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountRequestsDb.deleteAccountRequest(ins1InCourse1));
        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountRequestsDb.deleteAccountRequest(ins1InCourse2));
        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountRequestsDb.deleteAccountRequest(ins1InCourse3));
        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, unregisteredInstructor1);
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() {
        List<AccountRequest> results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("_"));
        verifySearchResults(results);

        results = inTransaction(() -> accountRequestsDb.searchAccountRequestsInWholeSystem("%"));
        verifySearchResults(results);
    }

    /**
     * Verifies that search results match expected output.
     */
    private static void verifySearchResults(List<AccountRequest> actual, AccountRequest... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
