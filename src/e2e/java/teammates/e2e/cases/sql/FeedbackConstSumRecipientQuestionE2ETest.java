package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for ConstSumRecipient questions.
 */
public class FeedbackConstSumRecipientQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/FeedbackConstSumRecipientQuestionE2ETestSql.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FCSumRcptQn.CS2104");
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
        FeedbackConstantSumQuestionDetails questionDetails =
                (FeedbackConstantSumQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyConstSumQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addConstSumRecipientQuestion(loadedQuestion);

        feedbackEditPage.verifyConstSumQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackConstantSumQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.getFeedbackSession().setCourse(course);
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyConstSumQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackConstantSumQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
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
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Student receiver = testData.students.get("benny.tmms@FCSumRcptQn.CS2104");
        Student receiver2 = testData.students.get("charlie.tmms@FCSumRcptQn.CS2104");
        feedbackSubmitPage.verifyConstSumQuestion(1, "",
                (FeedbackConstantSumQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, receiver, 49);
        FeedbackResponse response2 = getResponse(question, receiver2, 51);
        List<FeedbackResponse> responses = Arrays.asList(response, response2);
        feedbackSubmitPage.fillConstSumRecipientResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        verifyPresentInDatabase(response);
        verifyPresentInDatabase(response2);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyConstSumRecipientResponse(1, responses);

        ______TS("edit response");
        response = getResponse(question, receiver, 21);
        response2 = getResponse(question, receiver2, 79);
        responses = Arrays.asList(response, response2);
        feedbackSubmitPage.fillConstSumRecipientResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyConstSumRecipientResponse(1, responses);
        verifyPresentInDatabase(response);
        verifyPresentInDatabase(response2);
    }

    private FeedbackResponse getResponse(FeedbackQuestion question, Student receiver, Integer answer) {
        FeedbackConstantSumResponseDetails details = new FeedbackConstantSumResponseDetails();
        details.setAnswers(Arrays.asList(answer));
        return FeedbackResponse.makeResponse(question, student.getEmail(),
                student.getSection(), receiver.getTeamName(), receiver.getSection(), details);
    }
}
