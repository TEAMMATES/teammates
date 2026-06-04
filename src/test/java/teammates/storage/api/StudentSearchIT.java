package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link UsersDb}.
 */
public class StudentSearchIT extends BaseTestCaseWithDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();

    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test
    public void testSearchStudentsInWholeSystem_typicalCase_success() {
        Student stu1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student stu2InCourse1 = typicalBundle.students.get("student2InCourse1");
        Student stu3InCourse1 = typicalBundle.students.get("student3InCourse1");
        Student stu4InCourse1 = typicalBundle.students.get("student4InCourse1");
        Student stu1InCourse2 = typicalBundle.students.get("student1InCourse2");
        Student unregisteredStuInCourse1 = typicalBundle.students.get("unregisteredStudentInCourse1");
        Student stu1InCourse3 = typicalBundle.students.get("student1InCourse3");
        Student stu1InCourse4 = typicalBundle.students.get("student1InCourse4");

        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins1InCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");

        ______TS("success: search for students in whole system; query string does not match anyone");

        List<Student> results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("non-existent"));
        verifySearchResults(results);

        ______TS("success: search for students in whole system; empty query string does not match anyone");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem(""));
        verifySearchResults(results);

        ______TS("success: search for students in whole system; query string matches some students");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1"));
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; query string should be case-insensitive");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("sTuDeNt1"));
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; students should be searchable by course id");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("course-1"));
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1,
                unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by course name");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("Typical Course 1"));
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1,
                unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their name");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student3 In Course1"));
        verifySearchResults(results, stu3InCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their email");

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1@teammates.tmt"));
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");

        List<Student> studentList =
                inTransaction(() -> usersDb.searchStudents("student1", Arrays.asList(ins1InCourse1)));
        verifySearchResults(studentList, stu1InCourse1);

        studentList = inTransaction(() -> usersDb.searchStudents("student1", Arrays.asList(ins1InCourse4)));
        verifySearchResults(studentList, stu1InCourse4);

        ______TS("success: search for students in whole system; deleted students no longer searchable");

        inTransaction(() -> usersDb.removeUser(stu1InCourse1));
        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1"));
        verifySearchResults(results, stu1InCourse2, stu1InCourse3, stu1InCourse4);
    }

    @Test
    public void testSearchStudentsInWholeSystem_deleteAfterSearch_shouldNotBeSearchable() {
        Student stu1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student stu1InCourse2 = typicalBundle.students.get("student1InCourse2");
        Student stu1InCourse3 = typicalBundle.students.get("student1InCourse3");
        Student stu1InCourse4 = typicalBundle.students.get("student1InCourse4");

        List<Student> studentList = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1"));
        verifySearchResults(studentList, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        inTransaction(() -> usersDb.removeUser(stu1InCourse1));
        studentList = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1"));
        verifySearchResults(studentList, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        inTransaction(() -> usersDb.removeUser(stu1InCourse2));
        studentList = inTransaction(() -> usersDb.searchStudentsInWholeSystem("student1"));
        verifySearchResults(studentList, stu1InCourse3, stu1InCourse4);
    }

    @Test
    public void testSearchStudentsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() {
        List<Student> results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("_"));
        verifySearchResults(results);

        results = inTransaction(() -> usersDb.searchStudentsInWholeSystem("%"));
        verifySearchResults(results);
    }

    /**
     * Verifies that search results match expected output.
     */
    private static void verifySearchResults(List<Student> actual, Student... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
