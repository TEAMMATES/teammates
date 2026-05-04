package teammates.it.storage.sqlsearch;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.logic.entity.Instructor;
import teammates.storage.sqlapi.UsersDb;
import teammates.test.AssertHelper;

/**
 * SUT: {@link UsersDb}.
 */
public class InstructorSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();

    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Test
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

        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = usersDb.searchInstructorsInWholeSystem("InStRuCtOr 2");
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course id");

        results = usersDb.searchInstructorsInWholeSystem("course-1");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course name");

        results = usersDb.searchInstructorsInWholeSystem("Typical Course 1");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their name");

        results = usersDb.searchInstructorsInWholeSystem("Instructor Of Unregistered Course");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their email");

        results = usersDb.searchInstructorsInWholeSystem("instr2@teammates.tmt");
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their role");

        results = usersDb.searchInstructorsInWholeSystem("coowner");
        verifySearchResults(results, ins1InCourse1,
                insInUnregCourse, insUniqueDisplayName,
                ins1InCourse3, ins1InCourse4,
                ins2InCourse4, ins3InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by displayed name");

        String displayName = insUniqueDisplayName.getDisplayName();
        results = usersDb.searchInstructorsInWholeSystem(displayName);
        verifySearchResults(results, insUniqueDisplayName);
    }

    @Test
    public void testSearchInstructorsInWholeSystem_deleteAfterSearch_shouldNotBeSearchable() {
        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        Instructor unregisteredInsInCourse1 = typicalBundle.instructors.get("unregisteredInstructorOfCourse1");

        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("course-1");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        usersDb.deleteUser(ins1InCourse1);
        results = usersDb.searchInstructorsInWholeSystem("course-1");
        verifySearchResults(results, ins2InCourse1, unregisteredInsInCourse1);

        usersDb.deleteUser(ins2InCourse1);
        results = usersDb.searchInstructorsInWholeSystem("course-1");
        verifySearchResults(results, unregisteredInsInCourse1);

        usersDb.deleteUser(unregisteredInsInCourse1);
        results = usersDb.searchInstructorsInWholeSystem("course-1");
        verifySearchResults(results);
    }

    @Test
    public void testSearchInstructorsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() {
        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("_");

        List<Instructor> expectedUnderscoreMatches = usersDb
                .searchInstructorsInWholeSystem("instructor_permission_role_");
        assertFalse(results.isEmpty());
        AssertHelper.assertSameContentIgnoreOrder(expectedUnderscoreMatches, results);

        results = usersDb.searchInstructorsInWholeSystem("%");
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
