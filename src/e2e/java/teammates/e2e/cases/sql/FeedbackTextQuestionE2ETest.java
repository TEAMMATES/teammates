package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for text questions.
 */
public class FeedbackTextQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackTextQuestionE2ESqlTest.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FTextQn.CS2104");
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
        FeedbackTextQuestionDetails questionDetails = (FeedbackTextQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyTextQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addTextQuestion(loadedQuestion);

        feedbackEditPage.verifyTextQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackTextQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setQuestionNumber(3);
        copiedQuestion.setFeedbackSession(feedbackSession);

        feedbackEditPage.verifyTextQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails.setRecommendedLength(200);
        copiedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editTextQuestion(3, questionDetails);

        feedbackEditPage.verifyTextQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Instructor receiver = testData.instructors.get("instructor");
        question.setQuestionNumber(1);
        feedbackSubmitPage.verifyTextQuestion(1, (FeedbackTextQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, receiver, "<p>This is the response for qn 1</p>");
        feedbackSubmitPage.fillTextResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        // TODO: uncomment when SubmitFeedbackResponse is working
        // verifyPresentInDatabase(response);

        // ______TS("check previous response");
        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);

        // ______TS("edit response");
        // FeedbackResponse editedResponse = getResponse(question, receiver, "<p><strong>Edited response</strong></p>");
        // feedbackSubmitPage.fillTextResponse(1, receiver.getName(), editedResponse);
        // feedbackSubmitPage.clickSubmitQuestionButton(1);

        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);
        // verifyPresentInDatabase(editedResponse);
    }

    private FeedbackResponse getResponse(FeedbackQuestion feedbackQuestion, Instructor instructor, String answer) {
        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails(answer);
        return FeedbackResponse.makeResponse(
            feedbackQuestion, student.getEmail(), null, instructor.getEmail(), null, details);
    }
}
