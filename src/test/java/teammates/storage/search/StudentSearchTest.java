package teammates.storage.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.storage.api.StudentsDb;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link StudentsDb},
 *      {@link teammates.storage.search.StudentSearchDocument}.
 */
public class StudentSearchTest extends BaseSearchTest {

    private final StudentsDb studentsDb = new StudentsDb();

    @Test
    public void allTests() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        StudentAttributes stu1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes stu2InCourse1 = dataBundle.students.get("student2InCourse1");
        StudentAttributes stu1InCourse2 = dataBundle.students.get("student1InCourse2");
        StudentAttributes stu2InCourse2 = dataBundle.students.get("student2InCourse2");
        StudentAttributes stu1InCourse3 = dataBundle.students.get("student1InCourse3");
        StudentAttributes stu1InUnregCourse = dataBundle.students.get("student1InUnregisteredCourse");
        StudentAttributes stu2InUnregCourse = dataBundle.students.get("student2InUnregisteredCourse");
        StudentAttributes stu1InArchCourse = dataBundle.students.get("student1InArchivedCourse");

        ______TS("success: search for students in whole system; query string does not match any student");

        List<StudentAttributes> studentList =
                studentsDb.searchStudentsInWholeSystem("non-existent");

        assertEquals(0, studentList.size());

        ______TS("success: search for students in whole system; query string matches some students");

        studentList = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(5, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                     studentList);

        ______TS("success: search for students in whole system; query string should be case-insensitive");

        studentList = studentsDb.searchStudentsInWholeSystem("stUdeNt2");

        assertEquals(3, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(
                     Arrays.asList(stu2InCourse1, stu2InCourse2, stu2InUnregCourse),
                     studentList);

        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");

        List<InstructorAttributes> ins1OfCourse1 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse1") });
        List<InstructorAttributes> ins1OfCourse2 = Arrays.asList(
                new InstructorAttributes[] { dataBundle.instructors.get("instructor1OfCourse2") });

        studentList = studentsDb.search("student1", ins1OfCourse1);

        assertEquals(1, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse1), studentList);

        studentList = studentsDb.search("student1", ins1OfCourse2);

        assertEquals(1, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(stu1InCourse2), studentList);

        ______TS("success: search for students; deleted student no longer searchable");

        studentsDb.deleteStudent(stu1InCourse1.course, stu1InCourse1.email);

        studentList = studentsDb.search("student1", ins1OfCourse1);

        assertEquals(0, studentList.size());

    }

    @Test
    public void testSearchStudent_createNewStudent_studentShouldBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        CourseAttributes courseAttributes = dataBundle.courses.get("typicalCourse1");

        List<StudentAttributes> studentList =
                studentsDb.searchStudentsInWholeSystem("studentABCDE");

        assertEquals(0, studentList.size());

        // create a new student
        studentsDb.createEntity(
                StudentAttributes.builder(courseAttributes.getId(), "studentABCDE@email.com")
                        .withName("studentABCDE")
                        .withTeamName("TEAM-ABCDE")
                        .withComment("")
                        .build());

        // the newly created student is searchable
        studentList = studentsDb.searchStudentsInWholeSystem("studentABCDE");
        assertEquals(1, studentList.size());
        assertEquals("studentABCDE", studentList.get(0).getName());

    }

    @Test
    public void testSearchStudent_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        StudentAttributes stu1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes stu1InCourse2 = dataBundle.students.get("student1InCourse2");
        StudentAttributes stu1InCourse3 = dataBundle.students.get("student1InCourse3");
        StudentAttributes stu1InUnregCourse = dataBundle.students.get("student1InUnregisteredCourse");
        StudentAttributes stu1InArchCourse = dataBundle.students.get("student1InArchivedCourse");

        List<StudentAttributes> studentList = studentsDb.searchStudentsInWholeSystem("student1");

        // there is search result before deletion
        assertEquals(5, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                studentList);

        // delete a student
        studentsDb.deleteStudent(stu1InCourse1.getCourse(), stu1InCourse1.getEmail());

        // the search result will change
        studentList = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(4, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse2, stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                studentList);

        // delete all students in course 2
        studentsDb.deleteStudents(AttributesDeletionQuery.builder().withCourseId(
                stu1InCourse2.getCourse())
                .build());

        // the search result will change
        studentList = studentsDb.searchStudentsInWholeSystem("student1");

        assertEquals(3, studentList.size());
        AssertHelper.assertSameContentIgnoreOrder(
                Arrays.asList(stu1InCourse3, stu1InUnregCourse, stu1InArchCourse),
                studentList);
    }

    @Test
    public void testSearchStudents_noSearchService_shouldThrowException() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        assertThrows(SearchNotImplementedException.class,
                () -> studentsDb.search("anything", new ArrayList<>()));
        assertThrows(SearchNotImplementedException.class,
                () -> studentsDb.searchStudentsInWholeSystem("anything"));
    }

}
