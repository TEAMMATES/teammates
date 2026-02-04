package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseDetailsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_DETAILS_PAGE}.
 */
public class InstructorCourseDetailsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorCourseDetailsPageE2ESqlTest.json");
        testData = removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl detailsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICDet.CS2104").getId());
        InstructorCourseDetailsPage detailsPage = loginToPage(detailsPageUrl, InstructorCourseDetailsPage.class,
                testData.instructors.get("ICDet.instr").getGoogleId());

        Results results = getAxeBuilder().analyze(detailsPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
