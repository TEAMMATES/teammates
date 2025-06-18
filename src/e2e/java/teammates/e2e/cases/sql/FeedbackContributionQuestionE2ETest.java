package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for Contribution questions.
 */
public class FeedbackContributionQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/FeedbackContributionQuestionE2ETestSql.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FContrQn.CS2104");
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
        FeedbackContributionQuestionDetails questionDetails =
                (FeedbackContributionQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyContributionQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addContributionQuestion(loadedQuestion);

        feedbackEditPage.verifyContributionQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackContributionQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.getFeedbackSession().setCourse(course);
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyContributionQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackContributionQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setNotSureAllowed(false);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editContributionQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyContributionQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Student receiver = testData.students.get("benny.tmms@FContrQn.CS2104");
        Student receiver2 = testData.students.get("charlie.tmms@FContrQn.CS2104");
        feedbackSubmitPage.verifyContributionQuestion(1,
                (FeedbackContributionQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, student, 170);
        FeedbackResponse response2 = getResponse(question, receiver, 180);
        FeedbackResponse response3 = getResponse(question, receiver2, 60);
        List<FeedbackResponse> responses = Arrays.asList(response, response2, response3);
        feedbackSubmitPage.fillContributionResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        verifyPresentInDatabase(response);
        verifyPresentInDatabase(response2);
        verifyPresentInDatabase(response3);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyContributionResponse(1, responses);

        ______TS("edit response");
        response = getResponse(question, student, 50);
        response2 = getResponse(question, receiver, Const.POINTS_EQUAL_SHARE);
        response3 = getResponse(question, receiver2, Const.POINTS_NOT_SURE);
        responses = Arrays.asList(response, response2, response3);
        feedbackSubmitPage.fillContributionResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyContributionResponse(1, responses);
        verifyPresentInDatabase(response);
        verifyPresentInDatabase(response2);
        verifyPresentInDatabase(response3);
    }

    private FeedbackResponse getResponse(FeedbackQuestion question, Student receiver, int answer) {
        FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
        details.setAnswer(answer);
        return FeedbackResponse.makeResponse(question, student.getEmail(),
                student.getSection(), receiver.getEmail(), receiver.getSection(), details);
    }
}
