package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_SESSION_SUBMISSION_PAGE}.
 */
public class FeedbackSubmitPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/FeedbackSubmitPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withFeedbackSessionId(testData.feedbackSessions.get("Open Session").getId());

        FeedbackSubmitPage feedbackSubmitPage = loginToPage(url, FeedbackSubmitPage.class,
                testData.students.get("alice.tmms@FSubmit.CS2104").getEmail());

        Results results = getAxeBuilder("aria-valuenow").analyze(feedbackSubmitPage.getBrowser().getDriver());
        formatViolations(results);
    }

}
