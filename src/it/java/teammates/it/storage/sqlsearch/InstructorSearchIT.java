package teammates.it.storage.sqlsearch;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Instructor;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link UsersDb},
 * {@link teammates.storage.sqlsearch.InstructorSearchDocument}.
 */
public class InstructorSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final SqlDataBundle typicalBundle = getTypicalSqlDataBundle();
    private final UsersDb usersDb = UsersDb.inst();

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

        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        Instructor ins1InCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");
        Instructor ins2InCourse4 = typicalBundle.instructors.get("instructor2YetToJoinCourse4");
        Instructor ins3InCourse4 = typicalBundle.instructors.get("instructor3YetToJoinCourse4");
        Instructor insInArchivedCourse = typicalBundle.instructors.get("instructorOfArchivedCourse");
        Instructor insInUnregCourse = typicalBundle.instructors.get("instructorOfUnregisteredCourse");
        Instructor insUniqueDisplayName = typicalBundle.instructors.get("instructorOfCourse2WithUniqueDisplayName");
        Instructor ins1InCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        Instructor unregisteredInsInCourse1 = typicalBundle.instructors.get("unregisteredInstructorOfCourse1");

        ______TS("success: search for instructors in whole system; query string does not match anyone");

        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; empty query string does not match anyone");

        results = usersDb.searchInstructorsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; query string matches some instructors");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor of\"");
        verifySearchResults(results, insInArchivedCourse, insInUnregCourse, insUniqueDisplayName);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = usersDb.searchInstructorsInWholeSystem("\"InStRuCtOr 2\"");
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors in archived courses should be included");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Archived Course\"");
        verifySearchResults(results, insInArchivedCourse);

        ______TS(
                "success: search for instructors in whole system; instructors in unregistered course should be included");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Unregistered Course\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course id");

        results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course name");

        results = usersDb.searchInstructorsInWholeSystem("\"Typical Course 1\"");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their name");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Unregistered Course\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their email");

        results = usersDb.searchInstructorsInWholeSystem("instr2@teammates.tmt");
        verifySearchResults(results, ins2InCourse1, ins2InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their role");
        results = usersDb.searchInstructorsInWholeSystem("\"Co-owner\"");
        verifySearchResults(results, ins1InCourse1, insInArchivedCourse,
                insInUnregCourse, insUniqueDisplayName, ins1InCourse3,
                ins1InCourse4, ins2InCourse4, ins3InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by displayed name");

        String displayName = insUniqueDisplayName.getDisplayName();
        results = usersDb.searchInstructorsInWholeSystem(displayName);
        verifySearchResults(results, insUniqueDisplayName);

        ______TS("success: search for instructors in whole system; deleted instructors no longer searchable");

        usersDb.deleteUser(insUniqueDisplayName);
        results = usersDb.searchInstructorsInWholeSystem("\"Instructor of\"");
        verifySearchResults(results, insInArchivedCourse, insInUnregCourse);

        // This method used to use usersDb.putEntity, not sure if the .createInstructor method has the same functionality
        ______TS("success: search for instructors in whole system; instructors created without searchability unsearchable");
        usersDb.createInstructor(insUniqueDisplayName);
        results = usersDb.searchInstructorsInWholeSystem("\"Instructor of\"");
        verifySearchResults(results, insInArchivedCourse, insInUnregCourse, insUniqueDisplayName);

        ______TS("success: search for instructors in whole system; deleting instructor without deleting document:"
                + "document deleted during search, instructor unsearchable");

        usersDb.deleteUser(ins1InCourse3);
        results = usersDb.searchInstructorsInWholeSystem("\"Instructor 1\"");
        verifySearchResults(results, ins1InCourse1, ins1InCourse4);
    }

    @Test
    public void testSearchInstructor_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        Instructor unregisteredInsInCourse1 = typicalBundle.instructors.get("unregisteredInstructorOfCourse1");

        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1, unregisteredInsInCourse1);

        usersDb.deleteUser(ins1InCourse1);
        results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results, ins2InCourse1, unregisteredInsInCourse1);

        // This used to test .deleteInstructors, but we don't seem to have a similar method to delete all users in course
        usersDb.deleteUser(ins2InCourse1);
        results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results, unregisteredInsInCourse1);

        usersDb.deleteUser(unregisteredInsInCourse1);
        results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results);
    }

    @Test
    public void testSearchInstructor_noSearchService_shouldThrowException() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        assertThrows(SearchServiceException.class,
                () -> usersDb.searchInstructorsInWholeSystem("anything"));
    }

    /**
     * Verifies that search results match with expected output.
     *
     * @param actual   the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<Instructor> actual,
            Instructor... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
