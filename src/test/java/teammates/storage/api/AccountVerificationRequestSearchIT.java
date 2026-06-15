package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link AccountVerificationRequestsDb}.
 */
public class AccountVerificationRequestSearchIT extends BaseTestCaseWithDatabaseAccess {

    private final AccountVerificationRequestsDb accountVerificationRequestsDb = AccountVerificationRequestsDb.inst();

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testSearchAccountVerificationRequestsInWholeSystem_typicalCase_success() {
        AccountVerificationRequest ins1General = typicalBundle.accountVerificationRequests.get("instructor1");
        AccountVerificationRequest ins2General = typicalBundle.accountVerificationRequests.get("instructor2");
        AccountVerificationRequest ins1InCourse1 = typicalBundle.accountVerificationRequests.get("instructor1OfCourse1");
        AccountVerificationRequest ins2InCourse1 = typicalBundle.accountVerificationRequests.get("instructor2OfCourse1");
        AccountVerificationRequest ins1InCourse2 = typicalBundle.accountVerificationRequests.get("instructor1OfCourse2");
        AccountVerificationRequest ins2InCourse2 = typicalBundle.accountVerificationRequests.get("instructor2OfCourse2");
        AccountVerificationRequest ins1InCourse3 = typicalBundle.accountVerificationRequests.get("instructor1OfCourse3");
        AccountVerificationRequest ins2InCourse3 = typicalBundle.accountVerificationRequests.get("instructor2OfCourse3");
        AccountVerificationRequest insInUnregCourse = typicalBundle.accountVerificationRequests.get("instructor3");
        AccountVerificationRequest unregisteredInstructor1 = typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        AccountVerificationRequest unregisteredInstructor2 = typicalBundle.accountVerificationRequests.get("unregisteredInstructor2");

        ______TS("success: search for account requests; query string does not match anyone");

        List<AccountVerificationRequest> results =
                inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("non-existent"));
        verifySearchResults(results);

        ______TS("success: search for account requests; empty query string does not match anyone");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem(""));
        verifySearchResults(results);

        ______TS("success: search for account requests; query string should be case-insensitive");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("InStRuCtOr 2"));
        verifySearchResults(results, ins2General, ins2InCourse1, ins2InCourse2, ins2InCourse3, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their name");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem(
                "Instructor 3 of CourseNoRegister"));
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for account requests; account requests should be searchable by their email");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("instr2@course2.tmt"));
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for account requests; account requests should be searchable by their institute");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem(
                "TEAMMATES Test Institute 2"));
        verifySearchResults(results, unregisteredInstructor2);

        ______TS("success: search for account requests; account requests should be searchable by their comments");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem(
                "Comments for account request from instructor2"));
        verifySearchResults(results, ins2General);

        ______TS("success: search for account requests; unregistered account requests should be searchable");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem(
                "unregisteredinstructor1@gmail.tmt"));
        verifySearchResults(results, unregisteredInstructor1);

        ______TS("success: search for account requests; deleted account requests no longer searchable");

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse1, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountVerificationRequestsDb.removeAccountVerificationRequest(ins1InCourse1));
        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse2, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountVerificationRequestsDb.removeAccountVerificationRequest(ins1InCourse2));
        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, ins1InCourse3, unregisteredInstructor1);

        inTransaction(() -> accountVerificationRequestsDb.removeAccountVerificationRequest(ins1InCourse3));
        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("Instructor 1"));
        verifySearchResults(results, ins1General, unregisteredInstructor1);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testSearchAccountVerificationRequestsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() {
        List<AccountVerificationRequest> results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("_"));
        verifySearchResults(results);

        results = inTransaction(() -> accountVerificationRequestsDb.searchAccountVerificationRequestsInWholeSystem("%"));
        verifySearchResults(results);
    }

    /**
     * Verifies that search results match expected output.
     */
    private static void verifySearchResults(List<AccountVerificationRequest> actual, AccountVerificationRequest... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
