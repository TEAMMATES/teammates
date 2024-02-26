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
import teammates.storage.sqlentity.Student;
import teammates.test.AssertHelper;
import teammates.test.TestProperties;

/**
 * SUT: {@link UsersDb},
 * {@link teammates.storage.sqlsearch.InstructorSearchDocument}.
 */
public class StudentSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

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

        Student stu1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student stu2InCourse1 = typicalBundle.students.get("student2InCourse1");
        Student stu3InCourse1 = typicalBundle.students.get("student3InCourse1");
        Student stu4InCourse1 = typicalBundle.students.get("student4InCourse1");
        Student stu1InCourse2 = typicalBundle.students.get("student1InCourse2");
        Student unregisteredStuInCourse1 = typicalBundle.students.get("unregisteredStudentInCourse1");
        Student stu1InCourse3 = typicalBundle.students.get("student1InCourse3");
        Student stu1InCourse4 = typicalBundle.students.get("student1InCourse4");
        Student stuOfArchivedCourse = typicalBundle.students.get("studentOfArchivedCourse");

        Instructor ins1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor ins1InCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");

        ______TS("success: search for students in whole system; query string does not match anyone");

        List<Student> results = usersDb.searchStudentsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for students in whole system; empty query string does not match anyone");

        results = usersDb.searchStudentsInWholeSystem("");
        verifySearchResults(results);

        ______TS("success: search for students in whole system; query string matches some students");

        results = usersDb.searchStudentsInWholeSystem("\"student1\"");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; query string should be case-insensitive");

        results = usersDb.searchStudentsInWholeSystem("\"sTuDeNt1\"");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students in whole system; students in archived courses should be included");

        results = usersDb.searchStudentsInWholeSystem("\"Student In Archived Course\"");
        verifySearchResults(results, stuOfArchivedCourse);

        ______TS("success: search for students in whole system; students should be searchable by course id");

        results = usersDb.searchStudentsInWholeSystem("\"course-1\"");
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1, unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by course name");

        results = usersDb.searchStudentsInWholeSystem("\"Typical Course 1\"");
        verifySearchResults(results, stu1InCourse1, stu2InCourse1, stu3InCourse1, stu4InCourse1, unregisteredStuInCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their name");

        results = usersDb.searchStudentsInWholeSystem("\"student3 In Course1\"");
        verifySearchResults(results, stu3InCourse1);

        ______TS("success: search for students in whole system; students should be searchable by their email");

        results = usersDb.searchStudentsInWholeSystem("student1@teammates.tmt");
        verifySearchResults(results, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        ______TS("success: search for students; query string matches some students; results restricted "
                 + "based on instructor's privilege");

        List<Instructor> ins1OfCourse1 = Arrays.asList(
                new Instructor[] { ins1InCourse1 });
        List<Instructor> ins1OfCourse4 = Arrays.asList(
                new Instructor[] { ins1InCourse4 });
        List<Student> studentList = usersDb.searchStudents("student1", ins1OfCourse1);

        verifySearchResults(studentList, stu1InCourse1);

        studentList = usersDb.searchStudents("student1", ins1OfCourse4);
        verifySearchResults(studentList, stu1InCourse4);

        ______TS("success: search for students in whole system; deleted students no longer searchable");

        usersDb.deleteUser(stu1InCourse1);
        results = usersDb.searchStudentsInWholeSystem("\"student1\"");
        verifySearchResults(results, stu1InCourse2, stu1InCourse3, stu1InCourse4);

    }

    @Test
    public void testSearchStudent_deleteAfterSearch_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        Student stu1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student stu1InCourse2 = typicalBundle.students.get("student1InCourse2");
        Student stu1InCourse3 = typicalBundle.students.get("student1InCourse3");
        Student stu1InCourse4 = typicalBundle.students.get("student1InCourse4");

        List<Student> studentList = usersDb.searchStudentsInWholeSystem("student1");

        // there is search result before deletion
        verifySearchResults(studentList, stu1InCourse1, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        // delete a student
        usersDb.deleteUser(stu1InCourse1);

        // the search result will change
        studentList = usersDb.searchStudentsInWholeSystem("student1");

        verifySearchResults(studentList, stu1InCourse2, stu1InCourse3, stu1InCourse4);

        // delete all students in course 2
        usersDb.deleteUser(stu1InCourse2);

        // the search result will change
        studentList = usersDb.searchStudentsInWholeSystem("student1");

        verifySearchResults(studentList, stu1InCourse3, stu1InCourse4);
    }

    @Test
    public void testSearchStudent_noSearchService_shouldThrowException() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        assertThrows(SearchServiceException.class,
                () -> usersDb.searchStudentsInWholeSystem("anything"));
    }

    /**
     * Verifies that search results match with expected output.
     *
     * @param actual   the results from the search query.
     * @param expected the expected results for the search query.
     */
    private static void verifySearchResults(List<Student> actual,
            Student... expected) {
        assertEquals(expected.length, actual.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual);
    }
}
