package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCoursesPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCoursesPageE2ETest.json");
        testData = removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPageSql coursesPage = loginToPage(url, InstructorCoursesPageSql.class,
                testData.accounts.get("ICs.instructor").getGoogleId());

        Results results = getAxeBuilder().analyze(coursesPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
