package teammates.storage.search;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.api.StudentsDb;
import teammates.test.AssertHelper;

/**
 * SUT: {@link StudentsDb},
 *      {@link teammates.storage.search.StudentSearchDocument},
 *      {@link teammates.storage.search.StudentSearchQuery}.
 */
public class StudentSearchTest extends BaseSearchTest {

    private StudentsDb studentsDb = new StudentsDb();

    @Test
    public void allTests() {
        StudentAttributes stu1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes stu2InCourse1 = dataBundle.students.get("student2InCourse1");
        StudentAttributes stu1InCourse2 = dataBundle.students.get("student1InCourse2");
        StudentAttributes stu2InCourse2 = dataBundle.students.get("student2InCourse2");
        StudentAttributes stu1InCourse3 = dataBundle.students.get("student1InCourse3");
        StudentAttributes stu1InUnregCourse = dataBundle.students.get("student1InUnregisteredCourse");
        StudentAttributes stu2InUnregCourse = dataBundle.students.get("student2InUnregisteredCourse");
        StudentAttributes stu1InArchCourse = dataBundle.students.get("student1InArchivedCourse");

        ______TS("success: search for students in whole system; query string does not match any student");

        StudentSearchResultBundle bundle =
                studentsDb.searchStudentsInWholeSystem("non-existent");

        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.studentList.isEmpty());

        ______TS("success: search for students in whole system; query string matches some students");

        bundle = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(5, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                     bundle.studentList);

        ______TS("success: search for students in whole system; query string should be case-insensitive");

        bundle = studentsDb.searchStudentsInWholeSystem("stUdeNt2");

        assertEquals(3, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(stu2InCourse1, stu2InCourse2, stu2InUnregCourse),
                     bundle.studentList);

        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");

        List<InstructorAttributes> ins1OfCourse1 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse1") });
        List<InstructorAttributes> ins1OfCourse2 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse2") });

        bundle = studentsDb.search("student1", ins1OfCourse1);

        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse1), bundle.studentList);

        bundle = studentsDb.search("student1", ins1OfCourse2);

        assertEquals(1, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse2), bundle.studentList);

        ______TS("success: search for students; deleted student no longer searchable");

        studentsDb.deleteStudent(stu1InCourse1.course, stu1InCourse1.email);

        bundle = studentsDb.search("student1", ins1OfCourse1);

        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.studentList.isEmpty());

    }

    @Test
    public void testSearchStudent_createNewStudent_studentShouldBeSearchable() throws Exception {
        CourseAttributes courseAttributes = dataBundle.courses.get("typicalCourse1");

        StudentSearchResultBundle bundle =
                studentsDb.searchStudentsInWholeSystem("studentABCDE");

        assertEquals(0, bundle.numberOfResults);

        // create a new student
        studentsDb.createEntity(
                StudentAttributes.builder(courseAttributes.getId(), "studentABCDE@email.com")
                        .withName("studentABCDE")
                        .withTeamName("TEAM-ABCDE")
                        .withComment("")
                        .build());

        // the newly created student is searchable
        bundle = studentsDb.searchStudentsInWholeSystem("studentABCDE");
        assertEquals(1, bundle.numberOfResults);
        assertEquals("studentABCDE", bundle.studentList.get(0).getName());

    }

    @Test
    public void testSearchStudent_deleteAfterSearch_shouldNotBeSearchable() {
        StudentAttributes stu1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes stu1InCourse2 = dataBundle.students.get("student1InCourse2");
        StudentAttributes stu1InCourse3 = dataBundle.students.get("student1InCourse3");
        StudentAttributes stu1InUnregCourse = dataBundle.students.get("student1InUnregisteredCourse");
        StudentAttributes stu1InArchCourse = dataBundle.students.get("student1InArchivedCourse");

        StudentSearchResultBundle bundle = studentsDb.searchStudentsInWholeSystem("student1");

        // there is search result before deletion
        assertEquals(5, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                bundle.studentList);

        // delete a student
        studentsDb.deleteStudent(stu1InCourse1.getCourse(), stu1InCourse1.getEmail());

        // the search result will change
        bundle = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(4, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                bundle.studentList);

        // delete all students in course 2
        studentsDb.deleteStudents(AttributesDeletionQuery.builder().withCourseId(
                stu1InCourse2.getCourse())
                .build());

        // the search result will change
        bundle = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(3, bundle.numberOfResults);
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                bundle.studentList);
    }

}
