package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCoursesPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCoursesPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/InstructorCoursesPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPage coursesPage = loginToPage(url, InstructorCoursesPage.class,
                sqlTestData.accounts.get("instructor").getGoogleId());

        Results results = getAxeBuilder().analyze(coursesPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
