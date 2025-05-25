package teammates.e2e.cases.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 * specifically for msq questions.
 */
public class FeedbackMsqQuestionE2ETest extends BaseFeedbackQuestionE2ETest {
    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackMsqQuestionE2ESqlTest.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FMsqQn.CS2104");
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
        FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion
                .getQuestionDetailsCopy();
        feedbackEditPage.verifyMsqQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addMsqQuestion(loadedQuestion);

        feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackMsqQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.setFeedbackSession(feedbackSession);
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyMsqQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setMsqWeights(new ArrayList<>());
        questionDetails.setOtherEnabled(false);
        questionDetails.setMsqOtherWeight(0);
        questionDetails.setMaxSelectableChoices(Const.POINTS_NO_VALUE);
        List<String> choices = questionDetails.getMsqChoices();
        choices.add("Edited choice");
        questionDetails.setMsqChoices(choices);
        loadedQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editMsqQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(loadedQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPage feedbackSubmitPage = loginToFeedbackSubmitPage();

        ______TS("verify loaded question");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        Student receiver = testData.students.get("benny.tmms@FMsqQn.CS2104");
        feedbackSubmitPage.verifyMsqQuestion(1, receiver.getName(),
                (FeedbackMsqQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("verify loaded question with generated options");
        FeedbackQuestion generatedQn = testData.feedbackQuestions.get("qn1ForSecondSession");
        feedbackSubmitPage.verifyGeneratedMsqQuestion(3, "",
                (FeedbackMsqQuestionDetails) generatedQn.getQuestionDetailsCopy(), getGeneratedTeams());

        ______TS("submit response");
        List<String> answers = Arrays.asList("Leadership", "This is the other response.");
        FeedbackResponse response = getResponse(question, receiver, answers.get(answers.size() - 1), answers);
        feedbackSubmitPage.fillMsqResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        // TODO: uncomment when SubmitFeedbackResponse is working
        // verifyPresentInDatabase(response);

        // ______TS("check previous response");
        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);

        // ______TS("edit response");
        // answers = Arrays.asList("");
        // response = getResponse(question, receiver, "", answers);
        // feedbackSubmitPage.fillMsqResponse(1, receiver.getName(), response);
        // feedbackSubmitPage.clickSubmitQuestionButton(1);

        // feedbackSubmitPage = getFeedbackSubmitPage();
        // feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);
        // verifyPresentInDatabase(response);
    }

    private List<String> getGeneratedTeams() {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(student.getCourse()))
                .map(s -> s.getTeam().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private FeedbackResponse getResponse(FeedbackQuestion feedbackQuestion, Student receiver, String other,
            List<String> answers) {
        FeedbackMsqResponseDetails details = new FeedbackMsqResponseDetails();
        if (!other.isEmpty()) {
            details.setOther(true);
            details.setOtherFieldContent(other);
        }
        details.setAnswers(answers);

        return FeedbackResponse.makeResponse(feedbackQuestion, student.getEmail(), student.getSection(),
                receiver.getEmail(), receiver.getSection(), details);
    }
}
