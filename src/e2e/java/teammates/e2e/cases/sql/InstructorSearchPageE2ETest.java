package teammates.e2e.cases.sql;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPageSql;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPageSql;
import teammates.e2e.pageobjects.InstructorSearchPageSql;
import teammates.e2e.pageobjects.InstructorStudentRecordsPageSql;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }
        testData = doRemoveAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorSearchPageE2ETestSql.json"));
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        String instructorId = testData.accounts.get("instructor1OfCourse1").getGoogleId();
        AppUrl searchPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE);

        InstructorSearchPageSql searchPage = loginToPage(searchPageUrl, InstructorSearchPageSql.class, instructorId);

        ______TS("cannot click search button if no search term is entered");

        searchPage.search("");

        ______TS("search with no result");

        searchPage.search("thiswillnothitanything");
        searchPage.verifyStatusMessage("No results found.");

        ______TS("search for students");

        searchPage.search("student2");

        Course course1 = testData.courses.get("typicalCourse1");
        Course course2 = testData.courses.get("typicalCourse2");

        Student[] studentsInCourse1 = {
        testData.students.get("student2InCourse1"),
        testData.students.get("student2.2InCourse1"),
        };
        Student[] studentsInCourse2 = {
                testData.students.get("student2InCourse2"),
                testData.students.get("student2.2InCourse2"),
        };

        Map<String, Student[]> courseIdToStudents = new HashMap<>();
        courseIdToStudents.put(course1.getId(), studentsInCourse1);
        courseIdToStudents.put(course2.getId(), studentsInCourse2);

        Map<String, Course> courseIdToCourse = new HashMap<>();
        courseIdToCourse.put(course1.getId(), course1);
        courseIdToCourse.put(course2.getId(), course2);

        searchPage.verifyStudentDetails(courseIdToCourse, courseIdToStudents);

        ______TS("link: view student details page");

        Student studentToView = testData.students.get("student2.2InCourse1");
        String studentEmail = studentToView.getEmail();

        InstructorCourseStudentDetailsViewPageSql studentDetailsViewPage =
                searchPage.clickViewStudent(course1, studentEmail);
        studentDetailsViewPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPageSql studentDetailsEditPage =
                searchPage.clickEditStudent(course1, studentEmail);
        studentDetailsEditPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPageSql studentRecordsPage =
                searchPage.clickViewAllRecords(course1, studentEmail);
        studentRecordsPage.verifyIsCorrectPage(course1.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("action: delete student");

        Student studentToDelete = testData.students.get("student2InCourse2");

        searchPage.deleteStudent(course2, studentToDelete.getEmail());

        Student[] studentsAfterDelete = {
                testData.students.get("student2.2InCourse2"),
        };

        searchPage.verifyStudentDetails(course2, studentsAfterDelete);
        verifyAbsentInDatabase(studentToDelete);

    }

}
