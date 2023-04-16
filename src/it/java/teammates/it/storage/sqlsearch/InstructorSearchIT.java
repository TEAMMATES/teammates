package teammates.it.storage.sqlsearch;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Instructor;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link UsersDb},
 * {@link teammates.storage.sqlsearch.InstructorSearchDocument}.
 */
public class InstructorSearchIT extends BaseSearchIT {

    private final UsersDb usersDb = UsersDb.inst();

    @Test
    public void allTests() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins2InCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        // Instructor helperInCourse1 =
        // typicalBundle.instructors.get("helperOfCourse1");
        Instructor insInArchivedCourse = typicalBundle.instructors.get("instructorOfArchivedCourse");
        Instructor insInUnregCourse = typicalBundle.instructors.get("instructorOfUnregisteredCourse");
        // Instructor ins1InTestingSanitizationCourse = typicalBundle.instructors
        // .get("instructor1OfTestingSanitizationCourse");
        // ins1InTestingSanitizationCourse.sanitizeForSaving();

        ______TS("success: search for instructors in whole system; query string does not match anyone");

        List<Instructor> results = usersDb.searchInstructorsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; empty query string does not match anyone");

        results = usersDb.searchInstructorsInWholeSystem("");
        verifySearchResults(results);

        // ______TS("success: search for instructors in whole system; query string
        // matches some instructors");

        // results = usersDb.searchInstructorsInWholeSystem("instructor1");
        // verifySearchResults(results, ins1InCourse1, ins1InCourse2, ins1InCourse3,
        // ins1InCourse4,
        // ins1InTestingSanitizationCourse);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = usersDb.searchInstructorsInWholeSystem("\"InStRuCtOr 2\"");
        verifySearchResults(results, ins2InCourse1);

        ______TS("success: search for instructors in whole system; instructors in archived courses should be included");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Archived Course\"");
        verifySearchResults(results, insInArchivedCourse);

        ______TS(
                "success: search for instructors in whole system; instructors in unregistered course should be included");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Unregistered Course\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course id");

        results = usersDb.searchInstructorsInWholeSystem("\"course-1\"");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course name");

        results = usersDb.searchInstructorsInWholeSystem("\"Typical Course 1\"");
        verifySearchResults(results, ins1InCourse1, ins2InCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their name");

        results = usersDb.searchInstructorsInWholeSystem("\"Instructor Of Unregistered Course\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their email");

        results = usersDb.searchInstructorsInWholeSystem("instr2@teammates.tmt");
        verifySearchResults(results, ins2InCourse1);

        ______TS(
                "success: search for instructors in whole system; instructors should be searchable by their google id");

        results = usersDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse1);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their role");

        results = usersDb.searchInstructorsInWholeSystem("\"Co-owner\"");
        verifySearchResults(results, ins1InCourse1, insInArchivedCourse, insInUnregCourse);

        // TODO: After UpdateInstructorAction migrated
        //
        // ______TS("success: search for instructors in whole system; instructors should
        // be searchable by displayed name");
        // // create a new instructor with unique displayed name to test that field
        // // current displayed names in data bundle are either helper or instructor,
        // which
        // // matches on many other fields
        // Instructor assistantProf = helperInCourse1.getCopy();
        // String name = "Assistant Prof Smith";
        // assistantProf.setName(name);
        // Instructor updatedInstructor = usersDb.updateInstructorByEmail(
        // Instructor
        // .updateOptionsWithEmailBuilder(assistantProf.getCourseId(),
        // assistantProf.getEmail())
        // .withDisplayedName(assistantProf.getDisplayedName())
        // .build());
        // usersDb.putDocument(updatedInstructor);
        // results = usersDb.searchInstructorsInWholeSystem(displayedName);
        // verifySearchResults(results, assistantProf);

        // ______TS("success: search for instructors in whole system; deleted
        // instructors no longer searchable");

        // usersDb.deleteInstructor(ins1InCourse1.getCourseId(),
        // ins1InCourse1.getEmail());
        // results = usersDb.searchInstructorsInWholeSystem("instructor1");
        // verifySearchResults(results, ins1InCourse2, ins1InCourse3, ins1InCourse4,
        // ins1InTestingSanitizationCourse);

        // ______TS(
        // "success: search for instructors in whole system; instructors created without
        // searchability unsearchable");

        // usersDb.putEntity(ins1InCourse1);
        // results = usersDb.searchInstructorsInWholeSystem("instructor1");
        // verifySearchResults(results, ins1InCourse2, ins1InCourse3, ins1InCourse4,
        // ins1InTestingSanitizationCourse);

        // ______TS("success: search for instructors in whole system; deleting
        // instructor without deleting document:"
        // + "document deleted during search, instructor unsearchable");

        // usersDb.deleteInstructor(ins2InCourse1.getCourseId(),
        // ins2InCourse1.getEmail());
        // results = usersDb.searchInstructorsInWholeSystem("instructor2");
        // verifySearchResults(results, ins2InCourse2, ins2InCourse3);
    }

    // TODO: After DeleteInstructorAction migrated
    //
    // @Test
    // public void testSearchInstructor_deleteAfterSearch_shouldNotBeSearchable()
    // throws Exception {
    // if (!TestProperties.isSearchServiceActive()) {
    // return;
    // }

    // Instructor ins1InCourse2 =
    // typicalBundle.instructors.get("instructor1OfCourse2");
    // Instructor ins2InCourse2 =
    // typicalBundle.instructors.get("instructor2OfCourse2");
    // Instructor ins3InCourse2 =
    // typicalBundle.instructors.get("instructor3OfCourse2");

    // // there is search result before deletion
    // List<Instructor> results =
    // usersDb.searchInstructorsInWholeSystem("idOfTypicalCourse2");
    // verifySearchResults(results, ins1InCourse2, ins2InCourse2, ins3InCourse2);

    // // delete a student
    // usersDb.deleteInstructor(ins1InCourse2.getCourseId(),
    // ins1InCourse2.getEmail());

    // // the search result will change
    // results = usersDb.searchInstructorsInWholeSystem("idOfTypicalCourse2");
    // verifySearchResults(results, ins2InCourse2, ins3InCourse2);

    // // delete all instructors in course 2
    // usersDb.deleteInstructors(
    // AttributesDeletionQuery.builder()
    // .withCourseId(ins2InCourse2.getCourseId())
    // .build());

    // // there should be no search result
    // results = usersDb.searchInstructorsInWholeSystem("idOfTypicalCourse2");
    // verifySearchResults(results);
    // }

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
     * Parameters are modified to standardize {@link Instructor} for
     * comparison.
     *
     * @param actual   the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<Instructor> actual,
            Instructor... expected) {
        assertEquals(expected.length, actual.size());
        // standardizeInstructorsForComparison(expected);
        // standardizeInstructorsForComparison(
        // actual.toArray(new Instructor[0]));
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }

    // /**
    // * Standardizes instructors for comparison by setting key fields to null.
    // *
    // * @param instructors the instructors to standardize.
    // */
    // private static void standardizeInstructorsForComparison(Instructor...
    // instructors) {
    // for (Instructor instructor : instructors) {
    // instructor.setRegKey(null);
    // }
    // }
}
