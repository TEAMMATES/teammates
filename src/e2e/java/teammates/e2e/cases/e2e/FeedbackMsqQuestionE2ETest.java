package teammates.e2e.cases.e2e;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for MSQ questions.
 */
public class FeedbackMsqQuestionE2ETest extends BaseE2ETestCase {
    InstructorAttributes instructor;
    CourseAttributes course;
    FeedbackSessionAttributes feedbackSession;
    StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackMsqQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FMsqQuestionE2eT.CS2104");
    }

    @Test
    public void testAll() {
        testEditPage();
        testSubmitPage();
    }

    private void testEditPage() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withUserId(instructor.googleId)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        InstructorFeedbackEditPage feedbackEditPage = loginAdminToPage(url, InstructorFeedbackEditPage.class);
        feedbackEditPage.waitForPageToLoad();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession").getCopy();
        FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion.getQuestionDetails();
        feedbackEditPage.verifyMsqQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addMsqQuestion(loadedQuestion);

        feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackMsqQuestionDetails) copiedQuestion.getQuestionDetails();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetails().getQuestionText());
        copiedQuestion.courseId = course.getId();
        copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyMsqQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);

        ______TS("edit question");
        questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion.getQuestionDetails();
        questionDetails.setHasAssignedWeights(false);
        questionDetails.setMsqWeights(new ArrayList<>());
        questionDetails.setOtherEnabled(false);
        questionDetails.setMsqOtherWeight(0);
        questionDetails.setMaxSelectableChoices(Integer.MIN_VALUE);
        List<String> choices = questionDetails.getMsqChoices();
        choices.add("Edited choice");
        questionDetails.setMsqChoices(choices);
        loadedQuestion.questionDetails = questionDetails;
        feedbackEditPage.editMsqQuestion(2, questionDetails);

        feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);
    }

    private void testSubmitPage() {
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withUserId(student.googleId)
                .withCourseId(student.course)
                .withSessionName(feedbackSession.getFeedbackSessionName())
                .withRegistrationKey(getKeyForStudent(student));
        FeedbackSubmitPage feedbackSubmitPage = loginAdminToPage(url, FeedbackSubmitPage.class);
        feedbackSubmitPage.waitForPageToLoad();

        ______TS("verify loaded question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        StudentAttributes receiver = testData.students.get("benny.tmms@FMsqQuestionE2eT.CS2104");
        feedbackSubmitPage.verifyMsqQuestion(1, receiver.getName(),
                (FeedbackMsqQuestionDetails) question.getQuestionDetails());

        ______TS("verify loaded question with generated options");
        FeedbackQuestionAttributes generatedQn = testData.feedbackQuestions.get("qn1ForSecondSession");
        feedbackSubmitPage.verifyGeneratedMsqQuestion(3, "",
                (FeedbackMsqQuestionDetails) generatedQn.getQuestionDetails(), getGeneratedTeams());

        ______TS("submit response");
        String questionId = getFeedbackQuestion(question).getId();
        List<String> answers = Arrays.asList("Leadership", "This is the other response.");
        FeedbackResponseAttributes response = getResponse(questionId, receiver, answers.get(answers.size() - 1), answers);
        feedbackSubmitPage.submitMsqResponse(1, receiver.getName(), response);

        verifyPresentInDatastore(response);

        ______TS("check previous response");
        feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
        feedbackSubmitPage.waitForPageToLoad();
        feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);

        ______TS("edit response");
        answers = Arrays.asList("");
        response = getResponse(questionId, receiver, "", answers);
        feedbackSubmitPage.submitMsqResponse(1, receiver.getName(), response);

        feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
        feedbackSubmitPage.waitForPageToLoad();
        feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);
        verifyPresentInDatastore(response);
    }

    private List<String> getGeneratedTeams() {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(student.course))
                .map(s -> s.getTeam())
                .distinct()
                .collect(Collectors.toList());
    }

    private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, String other,
                                                   List<String> answers) {
        FeedbackMsqResponseDetails details = new FeedbackMsqResponseDetails();
        if (!other.isEmpty()) {
            details.setOther(true);
            details.setOtherFieldContent(other);
        }
        details.setAnswers(answers);

        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
