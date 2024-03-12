package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSessionIndividualExtensionPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorSessionIndividualExtensionPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE)
                .withCourseId(testData.courses.get("course").getId())
                .withSessionName(testData.feedbackSessions.get("firstSession").getFeedbackSessionName());

        InstructorSessionIndividualExtensionPage individualExtensionPage =
                loginToPage(url, InstructorSessionIndividualExtensionPage.class,
                testData.instructors.get("ISesIe.instructor1").getGoogleId());

        Results results = getAxeBuilder().analyze(individualExtensionPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
