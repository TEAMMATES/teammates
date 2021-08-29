package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for MCQ questions.
 */
public class FeedbackMcqQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackMcqQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FMcqQn.CS2104");
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
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyMcqQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addMcqQuestion(loadedQuestion);

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackMcqQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setCourseId(course.getId());
        copiedQuestion.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyMcqQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setMcqWeights(new ArrayList<>());
        questionDetails.setOtherEnabled(false);
        questionDetails.setMcqOtherWeight(0);
        List<String> choices = questionDetails.getMcqChoices();
        choices.add("Edited choice");
        questionDetails.setMcqChoices(choices);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editMcqQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        feedbackSubmitPage.verifyMcqQuestion(1, "",
                (FeedbackMcqQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("verify question with generated options");
        feedbackSubmitPage.verifyGeneratedMcqQuestion(3, "", getGeneratedStudentOptions());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, false, "UI");
        feedbackSubmitPage.submitMcqResponse(1, "", response);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyMcqResponse(1, "", response);

        ______TS("edit response");
        response = getResponse(questionId, true, "This is the edited response.");
        feedbackSubmitPage.submitMcqResponse(1, "", response);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyMcqResponse(1, "", response);
        verifyPresentInDatabase(response);
    }

    private List<String> getGeneratedStudentOptions() {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(student.getCourse()))
                .map(s -> s.getName() + " (" + s.getTeam() + ")")
                .collect(Collectors.toList());
    }

    private FeedbackResponseAttributes getResponse(String questionId, boolean isOther, String answer) {
        FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
        if (isOther) {
            details.setOther(true);
            details.setOtherFieldContent(answer);
        } else {
            details.setAnswer(answer);
        }
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), "%GENERAL%")
                .withResponseDetails(details)
                .build();
    }
}
