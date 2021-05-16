package teammates.e2e.cases;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for RankOption questions.
 */
public class FeedbackRankOptionQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRankOptionQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRankOptQn.CS2104");
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
        FeedbackRankOptionsQuestionDetails questionDetails =
                (FeedbackRankOptionsQuestionDetails) loadedQuestion.getQuestionDetails();
        feedbackEditPage.verifyRankQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addRankOptionsQuestion(loadedQuestion);

        feedbackEditPage.verifyRankQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRankOptionsQuestionDetails) copiedQuestion.getQuestionDetails();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetails().getQuestionText());
        copiedQuestion.courseId = course.getId();
        copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRankQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackRankOptionsQuestionDetails) loadedQuestion.getQuestionDetails();
        List<String> options = questionDetails.getOptions();
        options.remove(0);
        options.set(1, "Edited option.");
        questionDetails.setOptions(options);
        questionDetails.setAreDuplicatesAllowed(true);
        questionDetails.setMaxOptionsToBeRanked(Const.POINTS_NO_VALUE);
        questionDetails.setMinOptionsToBeRanked(1);
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
        StudentAttributes receiver = testData.students.get("benny.tmms@FRankOptQn.CS2104");
        feedbackSubmitPage.verifyRankQuestion(1, receiver.getName(),
                (FeedbackRankQuestionDetails) question.getQuestionDetails());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, Arrays.asList(2, 1, 3,
                Const.POINTS_NOT_SUBMITTED));
        feedbackSubmitPage.submitRankOptionResponse(1, receiver.getName(), response);

        verifyPresentInDatastore(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankOptionResponse(1, receiver.getName(), response);

        ______TS("edit response");
        response = getResponse(questionId, receiver, Arrays.asList(Const.POINTS_NOT_SUBMITTED, 1, 3, 2));
        feedbackSubmitPage.submitRankOptionResponse(1, receiver.getName(), response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRankOptionResponse(1, receiver.getName(), response);
        verifyPresentInDatastore(response);
    }

    private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, List<Integer> answers) {
        FeedbackRankOptionsResponseDetails details = new FeedbackRankOptionsResponseDetails();
        details.setAnswers(answers);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
