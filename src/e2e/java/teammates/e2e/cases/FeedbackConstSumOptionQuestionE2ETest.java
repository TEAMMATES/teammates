package teammates.e2e.cases;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for ConstSumOption questions.
 */
public class FeedbackConstSumOptionQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackConstSumOptionQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FCSumOptQn.CS2104");
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
        FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession").getCopy();
        FeedbackConstantSumQuestionDetails questionDetails =
                (FeedbackConstantSumQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyConstSumQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addConstSumOptionQuestion(loadedQuestion);

        feedbackEditPage.verifyConstSumQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackConstantSumQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setCourseId(course.getId());
        copiedQuestion.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyConstSumQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackConstantSumQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        List<String> options = questionDetails.getConstSumOptions();
        options.add("Edited option.");
        questionDetails.setNumOfConstSumOptions(questionDetails.getNumOfConstSumOptions() + 1);
        questionDetails.setConstSumOptions(options);
        questionDetails.setPointsPerOption(true);
        questionDetails.setPoints(1000);
        questionDetails.setDistributePointsFor("At least some options");
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editConstSumQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyConstSumQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        feedbackSubmitPage.verifyConstSumQuestion(1, "",
                (FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, Arrays.asList(50, 20, 30));
        feedbackSubmitPage.submitConstSumOptionResponse(1, "", response);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyConstSumOptionResponse(1, "", response);

        ______TS("edit response");
        response = getResponse(questionId, Arrays.asList(23, 47, 30));
        feedbackSubmitPage.submitConstSumOptionResponse(1, "", response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyConstSumOptionResponse(1, "", response);
        verifyPresentInDatabase(response);
    }

    private FeedbackResponseAttributes getResponse(String questionId, List<Integer> answers) {
        FeedbackConstantSumResponseDetails details = new FeedbackConstantSumResponseDetails();
        details.setAnswers(answers);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), student.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
