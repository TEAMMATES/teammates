package teammates.test.cases.search;

import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.InstructorsDb;
import teammates.storage.search.InstructorSearchDocument;
import teammates.storage.search.InstructorSearchQuery;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link InstructorsDb}, {@link InstructorSearchDocument},
 * {@link InstructorSearchQuery}.
 */
public class InstructorSearchTest extends BaseSearchTest {
    @Test
    public void allTests() throws InvalidParametersException {
        InstructorsDb instructorsDb = new InstructorsDb();

        InstructorAttributes ins1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes ins2InCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins2InCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
        InstructorAttributes ins3InCourse2 = dataBundle.instructors.get("instructor3OfCourse2");
        InstructorAttributes insInArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        InstructorAttributes insInUnregCourse = dataBundle.instructors.get("instructor5");

        ______TS("success: search for instructors in whole system; query string does not match anyone");

        InstructorSearchResultBundle results =
                instructorsDb.searchInstructorsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; empty query string does not match anyone");
        
        results = instructorsDb.searchInstructorsInWholeSystem("");
        verifySearchResults(results);
        
        ______TS("success: search for instructors in whole system; query string matches some instructors");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse1, ins1InCourse2);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = instructorsDb.searchInstructorsInWholeSystem("InStRuCtOr2");
        verifySearchResults(results, ins2InCourse1, ins2InCourse2);

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
        
        results = instructorsDb.searchInstructorsInWholeSystem("Manager");
        verifySearchResults(results, ins2InCourse1);
        
        ______TS("success: search for instructors in whole system; deleted instructors no longer searchable");
        
        instructorsDb.deleteInstructor(ins1InCourse1.courseId, ins1InCourse1.email);
        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse2);
        
        ______TS("success: search for instructors in whole system; instructors created without searchability unsearchable");
        
        instructorsDb.createInstructorsWithoutSearchability(Arrays.asList(ins1InCourse1));
        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse2);
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
                actual.instructorList.toArray(new InstructorAttributes[actual.instructorList.size()]));
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
