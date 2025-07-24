package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for RankRecipient questions.
 */
public class FeedbackRankRecipientQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/FeedbackRankRecipientQuestionE2ETestSql.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRankRcptQn.CS2104");
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
        FeedbackRankRecipientsQuestionDetails questionDetails =
                (FeedbackRankRecipientsQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyRankQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addRankRecipientsQuestion(loadedQuestion);

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRankRecipientsQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.getFeedbackSession().setCourse(course);
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRankQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackRankRecipientsQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setAreDuplicatesAllowed(false);
        questionDetails.setMaxOptionsToBeRanked(3);
        questionDetails.setMinOptionsToBeRanked(Const.POINTS_NO_VALUE);
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
        Instructor receiver = testData.instructors.get("instructor");
        Instructor receiver2 = testData.instructors.get("instructor2");
        feedbackSubmitPage.verifyRankQuestion(1, receiver.getName(),
                (FeedbackRankQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, receiver, 1);
        FeedbackResponse response2 = getResponse(question, receiver2, 2);
        List<FeedbackResponse> responses = Arrays.asList(response, response2);
        feedbackSubmitPage.fillRankRecipientResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        verifyPresentInDatabase(response);
        verifyPresentInDatabase(response2);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankRecipientResponse(1, responses);

        ______TS("edit response");
        response = getResponse(question, receiver, Const.POINTS_NOT_SUBMITTED);
        response2 = getResponse(question, receiver2, 1);
        responses = Arrays.asList(response, response2);
        feedbackSubmitPage.fillRankRecipientResponse(1, responses);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankRecipientResponse(1, responses);
        verifyAbsentInDatabase(response);
        verifyPresentInDatabase(response2);
    }

    private FeedbackResponse getResponse(FeedbackQuestion question, Instructor receiver, int answer) {
        FeedbackRankRecipientsResponseDetails details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(answer);
        return FeedbackResponse.makeResponse(question, student.getEmail(),
                student.getSection(), receiver.getEmail(), receiver.getSection(), details);
    }
}
