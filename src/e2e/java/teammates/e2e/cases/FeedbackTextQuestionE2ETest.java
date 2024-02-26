package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for text questions.
 */
public class FeedbackTextQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackTextQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/FeedbackTextQuestionE2ETest_SqlEntities.json"));

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
        InstructorFeedbackEditPage feedbackEditPage = loginToFeedbackEditPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession");
        FeedbackTextQuestionDetails questionDetails = (FeedbackTextQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyTextQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addTextQuestion(loadedQuestion);

        feedbackEditPage.verifyTextQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackTextQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setCourseId(course.getId());
        copiedQuestion.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        copiedQuestion.setQuestionNumber(3);

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
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        InstructorAttributes receiver = testData.instructors.get("instructor");
        question.setQuestionNumber(1);
        feedbackSubmitPage.verifyTextQuestion(1, (FeedbackTextQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, "<p>This is the response for qn 1</p>");
        feedbackSubmitPage.fillTextResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);

        ______TS("edit response");
        String editedResponse = "<p><strong>Edited response</strong></p>";
        FeedbackTextResponseDetails editedDetails = new FeedbackTextResponseDetails(editedResponse);
        response.setResponseDetails(editedDetails);
        feedbackSubmitPage.fillTextResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);
        verifyPresentInDatabase(response);
    }

    private FeedbackResponseAttributes getResponse(String questionId, InstructorAttributes instructor, String answer) {
        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails(answer);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), instructor.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
