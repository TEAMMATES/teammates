package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminSearchPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/AdminSearchPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        AdminSearchPage searchPage = loginAdminToPage(url, AdminSearchPage.class);

        Course course = testData.courses.get("typicalCourse1");
        Student student = testData.students.get("student1InCourse1");
        Instructor instructor = testData.instructors.get("instructor1OfCourse1");

        ______TS("Typical case: Search student email");
        String searchContent = student.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String studentDetails = getExpectedStudentDetails(student);
        String studentManageAccountLink = getExpectedStudentManageAccountLink(student);
        searchPage.verifyStudentRowContent(student, course, studentDetails, studentManageAccountLink);

        ______TS("Typical case: Regenerate key for a course student");
        searchPage.regenerateStudentKey(student);
        searchPage.verifyRegenerateStudentKey();
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for instructor email");
        searchPage.clearSearchBox();
        searchContent = instructor.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String instructorManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        searchPage.verifyInstructorRowContent(instructor, course, instructorManageAccountLink);

        ______TS("Typical case: Regenerate key for an instructor");
        searchPage.regenerateInstructorKey(instructor);
        searchPage.verifyRegenerateInstructorKey();
    }

    private String getExpectedStudentDetails(Student student) {
        return String.format("%s [%s] (%s)", student.getCourseId(),
                student.getSectionName(),
                student.getTeamName());
    }

    private String getExpectedStudentManageAccountLink(Student student) {
        return student.isRegistered() ? createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withAccountId(student.getAccountId())
                .toAbsoluteString()
                : "";
    }

    private String getExpectedInstructorManageAccountLink(Instructor instructor) {
        return createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withAccountId(instructor.getAccountId())
                .toAbsoluteString();
    }
}
