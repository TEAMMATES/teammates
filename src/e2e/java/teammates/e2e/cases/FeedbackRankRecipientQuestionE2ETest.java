package teammates.e2e.cases;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for RankRecipient questions.
 */
public class FeedbackRankRecipientQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRankRecipientQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRankRcptQn.CS2104");
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
        FeedbackRankRecipientsQuestionDetails questionDetails =
                (FeedbackRankRecipientsQuestionDetails) loadedQuestion.getQuestionDetails();
        feedbackEditPage.verifyRankQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addRankRecipientsQuestion(loadedQuestion);

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRankRecipientsQuestionDetails) copiedQuestion.getQuestionDetails();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetails().getQuestionText());
        copiedQuestion.courseId = course.getId();
        copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRankQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackRankRecipientsQuestionDetails) loadedQuestion.getQuestionDetails();
        questionDetails.setAreDuplicatesAllowed(false);
        questionDetails.setMaxOptionsToBeRanked(3);
        questionDetails.setMinOptionsToBeRanked(Const.POINTS_NO_VALUE);
        loadedQuestion.questionDetails = questionDetails;
        feedbackEditPage.editRankQuestion(2, questionDetails);

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        InstructorAttributes receiver = testData.instructors.get("instructor");
        InstructorAttributes receiver2 = testData.instructors.get("instructor2");
        feedbackSubmitPage.verifyRankQuestion(1, receiver.getName(),
                (FeedbackRankQuestionDetails) question.getQuestionDetails());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, 1);
        FeedbackResponseAttributes response2 = getResponse(questionId, receiver2, 2);
        List responses = Arrays.asList(response, response2);
        feedbackSubmitPage.submitRankRecipientResponse(1, responses);

        verifyPresentInDatastore(response);
        verifyPresentInDatastore(response2);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankRecipientResponse(1, responses);

        ______TS("edit response");
        response = getResponse(questionId, receiver, Const.POINTS_NOT_SUBMITTED);
        response2 = getResponse(questionId, receiver2, 1);
        responses = Arrays.asList(response, response2);
        feedbackSubmitPage.submitRankRecipientResponse(1, responses);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankRecipientResponse(1, responses);
        verifyAbsentInDatastore(response);
        verifyPresentInDatastore(response2);
    }

    private FeedbackResponseAttributes getResponse(String questionId, InstructorAttributes receiver, Integer answer) {
        FeedbackRankRecipientsResponseDetails details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(answer);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
