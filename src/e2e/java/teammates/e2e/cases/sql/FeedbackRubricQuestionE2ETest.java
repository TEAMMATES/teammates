package teammates.e2e.cases.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for Rubric questions.
 */
public class FeedbackRubricQuestionE2ETest extends BaseFeedbackQuestionE2ETest {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/FeedbackRubricQuestionE2ESqlTest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FRubricQn.CS2104");
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
        FeedbackRubricQuestionDetails questionDetails =
                (FeedbackRubricQuestionDetails) loadedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.verifyRubricQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        FeedbackRubricQuestion newQuestion = new FeedbackRubricQuestion(
                loadedQuestion.getFeedbackSession(), 2, loadedQuestion.getDescription(),
                loadedQuestion.getGiverType(), loadedQuestion.getRecipientType(),
                loadedQuestion.getNumOfEntitiesToGiveFeedbackTo(),
                loadedQuestion.getShowResponsesTo(), loadedQuestion.getShowGiverNameTo(),
                loadedQuestion.getShowRecipientNameTo(), questionDetails);
        feedbackEditPage.addRubricQuestion(newQuestion);

        feedbackEditPage.verifyRubricQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(newQuestion);

        ______TS("copy question");
        FeedbackQuestion copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackRubricQuestionDetails) copiedQuestion.getQuestionDetailsCopy();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetailsCopy().getQuestionText());
        copiedQuestion.getCourse().setId(course.getId());
        copiedQuestion.getFeedbackSession().setName(feedbackSession.getName());
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyRubricQuestionDetails(3, questionDetails);
        verifyPresentInDatabase(copiedQuestion);

        ______TS("edit question");
        // add a new choice
        questionDetails = (FeedbackRubricQuestionDetails) newQuestion.getQuestionDetailsCopy();
        List<String> choices = questionDetails.getRubricChoices();
        choices.add("Edited choice.");
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();
        descriptions.get(0).add("Edit description.");
        descriptions.get(1).add("Edit description 1.");
        // edit existing descriptions
        descriptions.get(0).set(1, "Edit description 2");
        descriptions.get(1).set(0, "");
        // edit existing subquestion
        List<String> subQns = questionDetails.getRubricSubQuestions();
        subQns.set(0, "Edited subquestion.");
        // add a new subquestion
        subQns.add("Added subquestion.");
        descriptions.add(Arrays.asList("", "test", ""));
        // remove assigned weights
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        newQuestion.setQuestionDetails(questionDetails);
        feedbackEditPage.editRubricQuestion(2, questionDetails);
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.verifyRubricQuestionDetails(2, questionDetails);
        verifyPresentInDatabase(newQuestion);
    }

    @Override
    protected void testSubmitPage() {
        FeedbackSubmitPageSql feedbackSubmitPage = loginToFeedbackSubmitPage();
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        question.setQuestionNumber(1);
        ______TS("verify loaded question");
        Student receiver = testData.students.get("benny.tmms@FRubricQn.CS2104");
        feedbackSubmitPage.verifyRubricQuestion(1, receiver.getName(),
                (FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy());

        ______TS("submit response");
        // Student giver = testData.students.get("
        FeedbackResponse response = getResponse(question, receiver, Arrays.asList(1, 1));
        feedbackSubmitPage.fillRubricResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        verifyPresentInDatabase(response);

        ______TS("check previous response");
        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRubricResponse(1, receiver.getName(), response);

        ______TS("edit response");
        response = getResponse(question, receiver, Arrays.asList(0, 0));
        feedbackSubmitPage.fillRubricResponse(1, receiver.getName(), response);
        feedbackSubmitPage.clickSubmitQuestionButton(1);

        feedbackSubmitPage = getFeedbackSubmitPage();
        feedbackSubmitPage.verifyRubricResponse(1, receiver.getName(), response);
        verifyPresentInDatabase(response);
    }

    private FeedbackResponse getResponse(FeedbackQuestion question, Student receiver, List<Integer> answers) {
        FeedbackRubricResponseDetails details = new FeedbackRubricResponseDetails();
        details.setAnswer(answers);

        return FeedbackResponse.makeResponse(question, student.getEmail(), student.getSection(),
                receiver.getEmail(), receiver.getSection(), details);
    }
}
