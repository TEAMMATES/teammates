package teammates.e2e.cases.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 * specifically for MCQ questions.
 */
public class FeedbackMcqQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackMcqQuestionE2ESqlTest.json"));

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
        InstructorFeedbackEditPageSql feedbackEditPage = loginToFeedbackEditPage();

        ______TS("verify loaded question");
        FeedbackQuestion loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession");
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion
                .getQuestionDetailsCopy();
        feedbackEditPage.verifyMcqQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addMcqQuestion(loadedQuestion);

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackMcqQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyMcqQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setMcqWeights(new ArrayList<>());
        questionDetails.setOtherEnabled(false);
        questionDetails.setQuestionDropdownEnabled(false);
        questionDetails.setMcqOtherWeight(0);
        List<String> choices = questionDetails.getMcqChoices();
        choices.add("Edited choice");
        questionDetails.setMcqChoices(choices);
        loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession").makeDeepCopy(feedbackSession);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editMcqQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        feedbackSubmitPage.verifyMcqQuestion(1, "",
                (FeedbackMcqQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("verify question with generated options");
        feedbackSubmitPage.verifyGeneratedMcqQuestion(3, "", getGeneratedStudentOptions());

        ______TS("submit response");
        FeedbackResponse response = getResponse(question, false, "UI");
        feedbackSubmitPage.fillMcqResponse(1, "", response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        // verifyPresentInDatabase(response);

        // ______TS("check previous response");
        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyMcqResponse(1, "", response);

        // ______TS("edit response");
        // response = getResponse(questionId, true, "This is the edited response.");
        // feedbackSubmitPage.fillMcqResponse(1, "", response);
        // feedbackSubmitPage.clickSubmitQuestionButton(1);

        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyMcqResponse(1, "", response);
        // verifyPresentInDatabase(response);
    }

    private List<String> getGeneratedStudentOptions() {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(student.getCourse()))
                .map(s -> s.getName() + " (" + s.getTeam().getName() + ")")
                .collect(Collectors.toList());
    }

    private FeedbackResponse getResponse(FeedbackQuestion feedbackQuestion, boolean isOther, String answer) {
        FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
        if (isOther) {
            details.setOther(true);
            details.setOtherFieldContent(answer);
        } else {
            details.setAnswer(answer);
        }
        return FeedbackResponse.makeResponse(feedbackQuestion, student.getEmail(), null, instructor.getEmail(), null,
                details);
    }
}
