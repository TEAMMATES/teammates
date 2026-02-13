package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorFeedbackSessionsPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPageSql feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPageSql.class,
                testData.instructors.get("IFSessionPage.instr1").getGoogleId());

        Results results = getAxeBuilder().analyze(feedbackSessionsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
