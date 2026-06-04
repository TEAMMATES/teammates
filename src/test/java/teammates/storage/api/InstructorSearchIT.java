package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Instructor;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.TestGroups;

/**
 * SUT: {@link UsersDb}.
 */
public class InstructorSearchIT extends BaseTestCaseWithDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testSearchInstructorsInWholeSystem_typicalCase_success() {
        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        Instructor ins1InCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        Instructor ins1InCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");
        Instructor ins2InCourse4 = typicalBundle.instructors.get("instructor2YetToJoinCourse4");
        Instructor ins3InCourse4 = typicalBundle.instructors.get("instructor3YetToJoinCourse4");
        Instructor insInUnregCourse = typicalBundle.instructors.get("instructorOfUnregisteredCourse");
        Instructor insUniqueDisplayName = typicalBundle.instructors.get("instructorOfCourse2WithUniqueDisplayName");
        Instructor unregisteredInsInCourse1 = typicalBundle.instructors.get("unregisteredInstructorOfCourse1");

        ______TS("success: search for instructors in whole system; empty query string does not match anyone");

        List<Instructor> results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem(""));
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("InStRuCtOr 2"));
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course id");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("course-1"));
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course name");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("Typical Course 1"));
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their name");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("Instructor Of Unregistered Course"));
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their email");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("instr2@teammates.tmt"));
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their role");

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("coowner"));
        verifySearchResults(results, ins1InCourse1,
                insInUnregCourse, insUniqueDisplayName,
                ins1InCourse3, ins1InCourse4,
                ins2InCourse4, ins3InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by displayed name");

        String displayName = insUniqueDisplayName.getDisplayName();
        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem(displayName));
        verifySearchResults(results, insUniqueDisplayName);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testSearchInstructorsInWholeSystem_deleteAfterSearch_shouldNotBeSearchable() {
        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        Instructor unregisteredInsInCourse1 = typicalBundle.instructors.get("unregisteredInstructorOfCourse1");

        List<Instructor> results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("course-1"));
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        inTransaction(() -> usersDb.deleteUser(ins1InCourse1));
        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("course-1"));
        verifySearchResults(results, ins2InCourse1, unregisteredInsInCourse1);

        inTransaction(() -> usersDb.deleteUser(ins2InCourse1));
        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("course-1"));
        verifySearchResults(results, unregisteredInsInCourse1);

        inTransaction(() -> usersDb.deleteUser(unregisteredInsInCourse1));
        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("course-1"));
        verifySearchResults(results);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testSearchInstructorsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() {
        List<Instructor> results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("_"));

        List<Instructor> expectedUnderscoreMatches = inTransaction(() -> usersDb
                .searchInstructorsInWholeSystem("instructor_permission_role_"));
        assertFalse(results.isEmpty());
        AssertHelper.assertSameContentIgnoreOrder(expectedUnderscoreMatches, results);

        results = inTransaction(() -> usersDb.searchInstructorsInWholeSystem("%"));
        verifySearchResults(results);
    }

    /**
     * Verifies that search results match with expected output.
     *
     * @param actual   the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<Instructor> actual, Instructor... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
