package teammates.e2e.cases.e2e;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSearchPage;

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

    }

    private String createHeaderText(CourseAttributes course) {
        return "[" + course.getId() + "]";
    }

}
