package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for NumScale questions.
 */
public class FeedbackNumScaleQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackNumScaleQuestionE2ESqlTest.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FNumScaleQn.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        testEditPage();
        logout();
        testSubmitPage();
    }

    @Override
    protected void testEditPage() {
        InstructorFeedbackEditPageSql feedbackEditPage = loginToFeedbackEditPage();

        ______TS("verify loaded question");
        FeedbackQuestion loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession");
        FeedbackNumericalScaleQuestionDetails questionDetails =
                (FeedbackNumericalScaleQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyNumScaleQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addNumScaleQuestion(loadedQuestion);
        feedbackEditPage.waitUntilAnimationFinish();

        feedbackEditPage.verifyNumScaleQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackNumericalScaleQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setQuestionNumber(3);
        copiedQuestion.setFeedbackSession(feedbackSession);

        feedbackEditPage.verifyNumScaleQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackNumericalScaleQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        FeedbackNumericalScaleQuestionDetails newQuestionDetails =
                (FeedbackNumericalScaleQuestionDetails) questionDetails.getDeepCopy();
        newQuestionDetails.setMinScale(0);
        newQuestionDetails.setStep(1);
        newQuestionDetails.setMaxScale(100);
        loadedQuestion.setQuestionDetails(newQuestionDetails);
        feedbackEditPage.editNumScaleQuestion(2, newQuestionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyNumScaleQuestionDetails(2, newQuestionDetails);
        verifyPresentInDatabase(loadedQuestion);

        // reset question details to original
        loadedQuestion.setQuestionDetails(questionDetails);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Student receiver = testData.students.get("benny.tmms@FNumScaleQn.CS2104");
        feedbackSubmitPage.verifyNumScaleQuestion(1, receiver.getTeamName(),
                (FeedbackNumericalScaleQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, receiver, 5.4);
        feedbackSubmitPage.fillNumScaleResponse(1, receiver.getTeamName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        // TODO: uncomment when SubmitFeedbackResponse is working
        // verifyPresentInDatabase(response);

        // ______TS("check previous response");
        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyNumScaleResponse(1, receiver.getTeamName(), response);

        // ______TS("edit response");
        // response = getResponse(question, receiver, 10.0);
        // feedbackSubmitPage.fillNumScaleResponse(1, receiver.getTeamName(), response);
        // feedbackSubmitPage.clickSubmitQuestionButton(1);

        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyNumScaleResponse(1, receiver.getTeamName(), response);
        // verifyPresentInDatabase(response);
    }

    private FeedbackResponse getResponse(FeedbackQuestion feedbackQuestion, Student receiver, Double answer) {
        FeedbackNumericalScaleResponseDetails details = new FeedbackNumericalScaleResponseDetails();
        details.setAnswer(answer);
        return FeedbackResponse.makeResponse(
                feedbackQuestion, student.getEmail(), null, receiver.getTeamName(), null, details);
    }

}
