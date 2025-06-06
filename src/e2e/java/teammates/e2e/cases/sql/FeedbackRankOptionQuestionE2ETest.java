package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 * specifically for RankOption questions.
 */
public class FeedbackRankOptionQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackRankOptionQuestionE2ESqlTest.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRankOptQn.CS2104");
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
        FeedbackQuestion loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession")
                .makeDeepCopy(feedbackSession);
        FeedbackRankOptionsQuestionDetails questionDetails = (FeedbackRankOptionsQuestionDetails) loadedQuestion
                .getQuestionDetailsCopy();
        feedbackEditPage.verifyRankQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addRankOptionsQuestion(loadedQuestion);

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRankOptionsQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRankQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackRankOptionsQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        List<String> options = questionDetails.getOptions();
        options.remove(0);
        options.set(1, "Edited option.");
        questionDetails.setOptions(options);
        questionDetails.setAreDuplicatesAllowed(true);
        questionDetails.setMaxOptionsToBeRanked(Const.POINTS_NO_VALUE);
        questionDetails.setMinOptionsToBeRanked(1);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editRankQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Student receiver = testData.students.get("benny.tmms@FRankOptQn.CS2104");
        feedbackSubmitPage.verifyRankQuestion(1, receiver.getName(),
                (FeedbackRankQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, receiver, Arrays.asList(2, 1, 3,
                Const.POINTS_NOT_SUBMITTED));
        feedbackSubmitPage.fillRankOptionResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        // verifyPresentInDatabase(response);

        // ______TS("check previous response");
        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyRankOptionResponse(1, receiver.getName(), response);

        // ______TS("edit response");
        // response = getResponse(questionId, receiver,
        // Arrays.asList(Const.POINTS_NOT_SUBMITTED, 1, 3, 2));
        // feedbackSubmitPage.fillRankOptionResponse(1, receiver.getName(), response);
        // feedbackSubmitPage.clickSubmitQuestionButton(1);

        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyRankOptionResponse(1, receiver.getName(), response);
        // verifyPresentInDatabase(response);
    }

    private FeedbackResponse getResponse(FeedbackQuestion question, Student receiver, List<Integer> answers) {
        FeedbackRankOptionsResponseDetails details = new FeedbackRankOptionsResponseDetails();
        details.setAnswers(answers);
        return FeedbackResponse.makeResponse(question, student.getEmail(), null, receiver.getEmail(), null, details);
    }
}
