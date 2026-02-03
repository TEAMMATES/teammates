package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/InstructorCourseEditPageE2ETestSql.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withCourseId(testData.courses.get("ICEdit.CS2104").getId());
        InstructorCourseEditPage editPage = loginToPage(url, InstructorCourseEditPage.class,
                testData.instructors.get("ICEdit.coowner.CS2104").getGoogleId());

        Results results = getAxeBuilder().analyze(editPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
