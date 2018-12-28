package teammates.test.cases.search;

import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link InstructorsDb},
 *      {@link teammates.storage.search.InstructorSearchDocument},
 *      {@link teammates.storage.search.InstructorSearchQuery}.
 */
public class InstructorSearchTest extends BaseSearchTest {
    @Test
    public void allTests() throws Exception {
        InstructorsDb instructorsDb = new InstructorsDb();

        InstructorAttributes ins1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes ins2InCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes helperInCourse1 = dataBundle.instructors.get("helperOfCourse1");
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins2InCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
        InstructorAttributes ins3InCourse2 = dataBundle.instructors.get("instructor3OfCourse2");
        InstructorAttributes ins1InCourse3 = dataBundle.instructors.get("instructor1OfCourse3");
        InstructorAttributes ins2InCourse3 = dataBundle.instructors.get("instructor2OfCourse3");
        InstructorAttributes ins1InCourse4 = dataBundle.instructors.get("instructor1OfCourse4");
        InstructorAttributes insInArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        InstructorAttributes insInUnregCourse = dataBundle.instructors.get("instructor5");
        InstructorAttributes ins1InTestingSanitizationCourse =
                dataBundle.instructors.get("instructor1OfTestingSanitizationCourse");
        ins1InTestingSanitizationCourse.sanitizeForSaving();

        ______TS("success: search for instructors in whole system; query string does not match anyone");

        InstructorSearchResultBundle results =
                instructorsDb.searchInstructorsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; empty query string does not match anyone");

        results = instructorsDb.searchInstructorsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; query string matches some instructors");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse1, ins1InCourse2, ins1InCourse3, ins1InCourse4,
                ins1InTestingSanitizationCourse);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = instructorsDb.searchInstructorsInWholeSystem("InStRuCtOr2");
        verifySearchResults(results, ins2InCourse1, ins2InCourse2, ins2InCourse3);

        ______TS("success: search for instructors in whole system; instructors in archived courses should be included");

        results = instructorsDb.searchInstructorsInWholeSystem("archived");
        verifySearchResults(results, insInArchivedCourse);

        ______TS("success: search for instructors in whole system; instructors in unregistered course should be included");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor5");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course id");

        results = instructorsDb.searchInstructorsInWholeSystem("idOfUnregisteredCourse");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by course name");

        results = instructorsDb.searchInstructorsInWholeSystem("idOfTypicalCourse2");
        verifySearchResults(results, ins1InCourse2, ins2InCourse2, ins3InCourse2);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their name");

        results = instructorsDb.searchInstructorsInWholeSystem("\"Instructor 5 of CourseNoRegister\"");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their email");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor2@course2.tmt");
        verifySearchResults(results, ins2InCourse2);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their google id");

        results = instructorsDb.searchInstructorsInWholeSystem("idOfInstructor5");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; instructors should be searchable by their role");

        results = instructorsDb.searchInstructorsInWholeSystem("Custom");
        verifySearchResults(results, helperInCourse1, ins2InCourse3, ins1InCourse4);

        ______TS("success: search for instructors in whole system; instructors should be searchable by displayed name");

        // create a new instructor with unique displayed name to test that field
        // current displayed names in data bundle are either helper or instructor, which matches on many other fields
        InstructorAttributes assistantProf = helperInCourse1.getCopy();
        String displayedName = "Assistant Prof Smith";
        assistantProf.displayedName = displayedName;
        instructorsDb.updateInstructorByEmail(assistantProf);
        results = instructorsDb.searchInstructorsInWholeSystem(displayedName);
        verifySearchResults(results, assistantProf);

        ______TS("success: search for instructors in whole system; deleted instructors no longer searchable");

        instructorsDb.deleteInstructor(ins1InCourse1.courseId, ins1InCourse1.email);
        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse2, ins1InCourse3, ins1InCourse4, ins1InTestingSanitizationCourse);

        ______TS("success: search for instructors in whole system; instructors created without searchability unsearchable");

        instructorsDb.createEntitiesWithoutExistenceCheck(Arrays.asList(ins1InCourse1));
        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse2, ins1InCourse3, ins1InCourse4, ins1InTestingSanitizationCourse);

        ______TS("success: search for instructors in whole system; deleting instructor without deleting document:"
                + "document deleted during search, instructor unsearchable");

        instructorsDb.deleteEntity(ins2InCourse1);
        results = instructorsDb.searchInstructorsInWholeSystem("instructor2");
        verifySearchResults(results, ins2InCourse2, ins2InCourse3);
    }

    /*
     * Verifies that search results match with expected output.
     * Parameters are modified to standardize {@link InstructorAttributes} for comparison.
     *
     * @param actual the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(InstructorSearchResultBundle actual,
            InstructorAttributes... expected) {
        assertEquals(expected.length, actual.numberOfResults);
        assertEquals(expected.length, actual.instructorList.size());
        standardizeInstructorsForComparison(expected);
        standardizeInstructorsForComparison(
                actual.instructorList.toArray(new InstructorAttributes[0]));
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual.instructorList);
    }

    /*
     * Standardizes instructors for comparison by setting key fields to null
     *
     * @param instructors the instructors to standardize.
     */
    private static void standardizeInstructorsForComparison(InstructorAttributes... instructors) {
        for (InstructorAttributes instructor : instructors) {
            instructor.key = null;
        }
    }
}
