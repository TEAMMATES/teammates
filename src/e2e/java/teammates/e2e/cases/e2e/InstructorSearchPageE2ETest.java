package teammates.e2e.cases.e2e;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorSearchPage;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    public void allTests() {

        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        AppUrl searchPageUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE).withUserId(instructorId);

        InstructorSearchPage searchPage = loginAdminToPage(searchPageUrl, InstructorSearchPage.class);

        ______TS("cannot click search button if no checkbox is selected");

        searchPage.clickStudentsCheckbox();
        searchPage.verifyCannotClickSearchButton();

        ______TS("search with no result");

        searchPage.inputSearchContent("thiswillnothitanything");
        searchPage.clickStudentsCheckbox();
        searchPage.clickCommentsCheckbox();
        searchPage.clickSearchButton();
        searchPage.verifyStatusMessage("No results found.");

        ______TS("search for students");

        searchPage.clickCommentsCheckbox();
        searchPage.inputSearchContent("student2");
        searchPage.clickSearchButton();

        CourseAttributes course1 = testData.courses.get("typicalCourse1");
        CourseAttributes course2 = testData.courses.get("typicalCourse2");
        String course1Header = createHeaderText(course1);
        String course2Header = createHeaderText(course2);

        StudentAttributes[] studentsInCourse1 = {
                testData.students.get("student2.2InCourse1"),
                testData.students.get("student2InCourse1"),
        };
        StudentAttributes[] studentsInCourse2 = {
                testData.students.get("student2.2InCourse2"),
                testData.students.get("student2InCourse2"),
        };

        Map<String, StudentAttributes[]> courseHeaderToStudents = new HashMap<>();
        courseHeaderToStudents.put(course1Header, studentsInCourse1);
        courseHeaderToStudents.put(course2Header, studentsInCourse2);

        searchPage.verifyStudentDetails(courseHeaderToStudents);

        ______TS("link: view student details page");

        StudentAttributes studentToView = testData.students.get("student2.2InCourse1");
        String studentEmail = studentToView.getEmail();

        InstructorCourseStudentDetailsViewPage studentDetailsViewPage =
                searchPage.clickViewStudent(course1Header, studentEmail);
        studentDetailsViewPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPage studentDetailsEditPage =
                searchPage.clickEditStudent(course1Header, studentEmail);
        studentDetailsEditPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPage studentRecordsPage =
                searchPage.clickViewAllRecords(course1Header, studentEmail);
        studentRecordsPage.verifyIsCorrectPage(course1.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("action: delete student");

        StudentAttributes studentToDelete = testData.students.get("student2InCourse1");

        searchPage.deleteStudent(course1Header, studentToDelete.getEmail());

        StudentAttributes[] studentsAfterDelete = {
                testData.students.get("student2.2InCourse1"),
        };

        searchPage.verifyStudentDetails(course1Header, studentsAfterDelete);
        verifyAbsentInDatastore(studentToDelete);

        // TODO add tests for search response comments

    }

    private String createHeaderText(CourseAttributes course) {
        return "[" + course.getId() + "]";
    }

}
