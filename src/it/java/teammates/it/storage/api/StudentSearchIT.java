package teammates.it.storage.api;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.UsersDb;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.AssertHelper;

/**
 * SUT: {@link UsersDb}.
 */
public class StudentSearchIT extends BaseTestCaseWithDatabaseAccess {

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
    public void testSearchStudentsInWholeSystem_typicalCase_success() throws Exception {
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

        List<Student> results = usersDb.searchStudentsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for students in whole system; empty query string does not match anyone");

        results = usersDb.searchStudentsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for students in whole system; query string matches some students");

        results = usersDb.searchStudentsInWholeSystem("student1");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; query string should be case-insensitive");

        results = usersDb.searchStudentsInWholeSystem("sTuDeNt1");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; students should be searchable by course id");

        results = usersDb.searchStudentsInWholeSystem("course-1");
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1,
                unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by course name");

        results = usersDb.searchStudentsInWholeSystem("Typical Course 1");
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1,
                unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their name");

        results = usersDb.searchStudentsInWholeSystem("student3 In Course1");
        verifySearchResults(results, stu3InCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their email");

        results = usersDb.searchStudentsInWholeSystem("student1@teammates.tmt");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");

        List<Student> studentList = usersDb.searchStudents("student1", Arrays.asList(ins1InCourse1));
        verifySearchResults(studentList, stu1InCourse1);

        studentList = usersDb.searchStudents("student1", Arrays.asList(ins1InCourse4));
        verifySearchResults(studentList, stu1InCourse4);

        ______TS("success: search for students in whole system; deleted students no longer searchable");

        usersDb.deleteUser(stu1InCourse1);
        results = usersDb.searchStudentsInWholeSystem("student1");
        verifySearchResults(results, stu1InCourse2, stu1InCourse3, stu1InCourse4);
    }

    @Test
    public void testSearchStudentsInWholeSystem_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        Student stu1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student stu1InCourse2 = typicalBundle.students.get("student1InCourse2");
        Student stu1InCourse3 = typicalBundle.students.get("student1InCourse3");
        Student stu1InCourse4 = typicalBundle.students.get("student1InCourse4");

        List<Student> studentList = usersDb.searchStudentsInWholeSystem("student1");
        verifySearchResults(studentList, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        usersDb.deleteUser(stu1InCourse1);
        studentList = usersDb.searchStudentsInWholeSystem("student1");
        verifySearchResults(studentList, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        usersDb.deleteUser(stu1InCourse2);
        studentList = usersDb.searchStudentsInWholeSystem("student1");
        verifySearchResults(studentList, stu1InCourse3, stu1InCourse4);
    }

    @Test
    public void testSearchStudentsInWholeSystem_wildcardCharacters_shouldBeTreatedLiterally() throws Exception {
        List<Student> results = usersDb.searchStudentsInWholeSystem("_");
        verifySearchResults(results);

        results = usersDb.searchStudentsInWholeSystem("%");
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
