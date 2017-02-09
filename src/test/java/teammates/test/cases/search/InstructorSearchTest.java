package teammates.test.cases.search;

import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.InstructorsDb;
import teammates.storage.search.InstructorSearchDocument;
import teammates.storage.search.InstructorSearchQuery;
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
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins2InCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
        InstructorAttributes insInArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        InstructorAttributes insInUnregCourse = dataBundle.instructors.get("instructor5");
        InstructorAttributes insInNoCourse = dataBundle.instructors.get("instructorWithoutCourses");
        
        ______TS("success: search for instructors in whole system; query string does not match anyone");
        
        InstructorSearchResultBundle bundle =
                instructorsDb.searchInstructorsInWholeSystem("non-existent");
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.instructorList.isEmpty());
        
        ______TS("success: search for instructors in whole system; query string matches some instructors");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        assertEquals(2, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(ins1InCourse1, ins1InCourse2),
                     bundle.instructorList);
        
        ______TS("success: search for instructors in whole system; query string should be case-insensitive");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("InStRuCtOr2");
        assertEquals(2, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(ins2InCourse1, ins2InCourse2),
                     bundle.instructorList);
        
        ______TS("success: search for instructors in whole system; instructors in archived courses should be included");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("instructor5");
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(insInArchivedCourse),
                     bundle.instructorList);
        
        ______TS("success: search for instructors in whole system; instructors without courses should be included");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("instructorWithoutCourses");
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(insInNoCourse),
                     bundle.instructorList);
        
        ______TS("success: search for instructors in whole system; instructors in unregistered course should be included");
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("instructor5");
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(insInUnregCourse),
                     bundle.instructorList);
        
        /* 
        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");
        
        List<InstructorAttributes> ins1OfCourse1 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse1") });
        List<InstructorAttributes> ins1OfCourse2 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse2") });
        
        bundle = instructorsDb.search("student1", ins1OfCourse1);
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse1), bundle.studentList);
        
        bundle = instructorsDb.search("student1", ins1OfCourse2);
        
        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse2), bundle.studentList);
        
        ______TS("success: search for students; deleted student no longer searchable");
        
        instructorsDb.deleteStudent(stu1InCourse1.course, stu1InCourse1.email);
        
        bundle = instructorsDb.search("student1", ins1OfCourse1);
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.studentList.isEmpty());
        
        ______TS("success: search for students; deleted student without deleted document: the document "
                 + "will be deleted during the search");
        
        instructorsDb.deleteStudentWithoutDocument(stu1InCourse2.course, stu1InCourse2.email);
        
        bundle = instructorsDb.search("student1", ins1OfCourse2);
        
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.studentList.isEmpty());
        
        instructorsDb.deleteStudentWithoutDocument(stu2InCourse1.course, stu2InCourse1.email);
        
        bundle = instructorsDb.searchInstructorsInWholeSystem("instructor2");
        
        assertEquals(2, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(stu2InCourse2, stu2InUnregCourse),
                     bundle.studentList);
        */
    }
}