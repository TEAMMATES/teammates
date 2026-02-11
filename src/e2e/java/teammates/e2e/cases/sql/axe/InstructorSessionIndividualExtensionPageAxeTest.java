package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorSessionIndividualExtensionPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE)
                .withCourseId(testData.courses.get("course").getId())
                .withSessionName(testData.feedbackSessions.get("firstSession").getName());

        InstructorSessionIndividualExtensionPageSql individualExtensionPage =
                loginToPage(url, InstructorSessionIndividualExtensionPageSql.class,
                testData.instructors.get("ISesIe.instructor1").getGoogleId());

        Results results = getAxeBuilder().analyze(individualExtensionPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
