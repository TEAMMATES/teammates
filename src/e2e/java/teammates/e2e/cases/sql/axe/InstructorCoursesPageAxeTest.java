package teammates.e2e.cases.sql.axe;

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
        testData = loadSqlDataBundle("/InstructorCoursesPageE2ETestSql.json");
        testData = removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPage coursesPage = loginToPage(url, InstructorCoursesPage.class,
                testData.accounts.get("ICs.instructor").getGoogleId());

        Results results = getAxeBuilder().analyze(coursesPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
