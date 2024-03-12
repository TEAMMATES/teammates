package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorStudentListPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_LIST_PAGE}.
 */
public class InstructorStudentListPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentListPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorStudentListPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl listPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_LIST_PAGE);
        InstructorStudentListPage listPage = loginToPage(listPageUrl, InstructorStudentListPage.class,
                testData.instructors.get("instructorOfCourse1").getGoogleId());

        Results results = getAxeBuilder().analyze(listPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
