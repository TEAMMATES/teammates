package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for NumScale questions.
 */
public class FeedbackNumScaleQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackNumScaleQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FNumScaleQn.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        testEditPage();
        testSubmitPage();
    }

    @Override
    protected void testEditPage() {
        InstructorFeedbackEditPage feedbackEditPage = loginToFeedbackEditPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession").getCopy();
        FeedbackNumericalScaleQuestionDetails questionDetails =
                (FeedbackNumericalScaleQuestionDetails) loadedQuestion.getQuestionDetails();
        feedbackEditPage.verifyNumScaleQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addNumScaleQuestion(loadedQuestion);

        feedbackEditPage.verifyNumScaleQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackNumericalScaleQuestionDetails) copiedQuestion.getQuestionDetails();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetails().getQuestionText());
        copiedQuestion.courseId = course.getId();
        copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyNumScaleQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackNumericalScaleQuestionDetails) loadedQuestion.getQuestionDetails();
        questionDetails.setMinScale(0);
        questionDetails.setStep(1);
        questionDetails.setMaxScale(100);
        loadedQuestion.questionDetails = questionDetails;
        feedbackEditPage.editNumScaleQuestion(2, questionDetails);

        feedbackEditPage.verifyNumScaleQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        StudentAttributes receiver = testData.students.get("benny.tmms@FNumScaleQn.CS2104");
        feedbackSubmitPage.verifyNumScaleQuestion(1, receiver.getTeam(),
                (FeedbackNumericalScaleQuestionDetails) question.getQuestionDetails());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, 5.4);
        feedbackSubmitPage.submitNumScaleResponse(1, receiver.getTeam(), response);

        verifyPresentInDatastore(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyNumScaleResponse(1, receiver.getTeam(), response);

        ______TS("edit response");
        response = getResponse(questionId, receiver, 10.0);
        feedbackSubmitPage.submitNumScaleResponse(1, receiver.getTeam(), response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyNumScaleResponse(1, receiver.getTeam(), response);
        verifyPresentInDatastore(response);
    }

    private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, Double answer) {
        FeedbackNumericalScaleResponseDetails details = new FeedbackNumericalScaleResponseDetails();
        details.setAnswer(answer);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getTeam())
                .withResponseDetails(details)
                .build();
    }
}
