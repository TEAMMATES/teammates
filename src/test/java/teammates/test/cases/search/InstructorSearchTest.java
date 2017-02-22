package teammates.test.cases.search;

import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link InstructorsDb},
 *      {@link InstructorSearchDocument},
 *      {@link InstructorSearchQuery}.
 */
public class InstructorSearchTest extends BaseSearchTest {
    
    @Test
    public void allTests() {
        InstructorsDb instructorsDb = new InstructorsDb();
        
        InstructorAttributes ins1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes ins2InCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes ins3InCourse1 = dataBundle.instructors.get("instructor3OfCourse1");
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins1NotJoinInCourse1 = dataBundle.instructors.get("instructorNotYetJoinCourse1");
        InstructorAttributes ins1NotJoinInCourse = dataBundle.instructors.get("instructorNotYetJoinCourse");
        InstructorAttributes helperInCourse1 = dataBundle.instructors.get("helperOfCourse1");
        InstructorAttributes ins1InArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        
        ______TS("success: search for instructors; query string does not match any instructor");
        
        InstructorSearchResultBundle bundle = instructorsDb.searchInstructorsInWholeSystem("non-existent");
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.instructorList.isEmpty());
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("");
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.instructorList.isEmpty());
        
        ______TS("success: search for instructors; query string matches some instructors");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("Course1");
        
        assertEquals(4, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(ins1InCourse1, ins2InCourse1, ins3InCourse1, helperInCourse1), bundle.instructorList);
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("Helper");
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(helperInCourse1), bundle.instructorList);
        
        ______TS("success: search for instructors; query string should be case-insensitive");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("InStRUctOr4");
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(instructor4), bundle.instructorList);
        
        ______TS("success: search for instructors; instructor not join yet");
                
        bundle = instructorsDb.searchInstructorsInWholeSystem("Not Yet Joined");
        
        assertEquals(2, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(ins1NotJoinInCourse1, ins1NotJoinInCourse), bundle.instructorList);
        
        ______TS("success: search for instructors; instructor in archived course");
                
        bundle = instructorsDb.searchInstructorsInWholeSystem("InstructorOfArchiveCourse");
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(ins1InArchivedCourse), bundle.instructorList);
        
        ______TS("success: search for instructors; deleted instructor no longer searchable");
        
        instructorsDb.deleteInstructor(ins1InCourse2.courseId, ins1InCourse2.email);
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("Instructor1");
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(ins1InCourse1), bundle.instructorList);
    }
    
}
