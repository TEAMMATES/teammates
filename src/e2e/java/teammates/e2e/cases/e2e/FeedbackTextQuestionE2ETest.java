package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}
 *      specifically for text questions.
 */
public class FeedbackTextQuestionE2ETest extends BaseE2ETestCase {
    InstructorAttributes instructor;
    CourseAttributes course;
    FeedbackSessionAttributes feedbackSession;
    StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackTextQuestionE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("openSession");
        student = testData.students.get("alice.tmms@FTextQuestionE2eT.CS2104");
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
        FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get("qn1ForFirstSession");
        FeedbackTextQuestionDetails questionDetails = (FeedbackTextQuestionDetails) loadedQuestion.getQuestionDetails();
        feedbackEditPage.verifyTextQuestionDetails(1, questionDetails);

        ______TS("add new question");
        // add new question exactly like loaded question
        loadedQuestion.setQuestionNumber(2);
        feedbackEditPage.addTextQuestion(loadedQuestion);

        feedbackEditPage.verifyTextQuestionDetails(2, questionDetails);
        verifyPresentInDatastore(loadedQuestion);

        ______TS("copy question");
        FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions.get("qn1ForSecondSession");
        questionDetails = (FeedbackTextQuestionDetails) copiedQuestion.getQuestionDetails();
        feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
                copiedQuestion.getQuestionDetails().getQuestionText());
        copiedQuestion.courseId = course.getId();
        copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        copiedQuestion.setQuestionNumber(3);

        feedbackEditPage.verifyTextQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);

        ______TS("edit question");
        questionDetails.setRecommendedLength(200);
        copiedQuestion.questionDetails = questionDetails;
        feedbackEditPage.editTextQuestion(3, questionDetails);

        feedbackEditPage.verifyTextQuestionDetails(3, questionDetails);
        verifyPresentInDatastore(copiedQuestion);
    }

    private void testSubmitPage() {
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withUserId(student.googleId)
                .withCourseId(student.course)
                .withSessionName(feedbackSession.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(getStudent(student).key));
        FeedbackSubmitPage feedbackSubmitPage = loginAdminToPage(url, FeedbackSubmitPage.class);
        feedbackSubmitPage.waitForPageToLoad();

        ______TS("verify loaded text question");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1ForFirstSession");
        question.setQuestionNumber(1);
        feedbackSubmitPage.verifyTextQuestion(1, (FeedbackTextQuestionDetails) question.getQuestionDetails());

        ______TS("submit response");
        StudentAttributes receiver = testData.students.get("benny.tmms@FTextQuestionE2eT.CS2104");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackResponseAttributes response = getResponse(questionId, receiver, "<p>This is the response for qn 1</p>");
        feedbackSubmitPage.submitTextResponse(1, receiver.getName(), response);

        verifyPresentInDatastore(response);

        ______TS("check previous response");
        feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
        feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);

        ______TS("edit response");
        String editedResponse = "<p><strong>Edited response</strong></p>";
        FeedbackTextResponseDetails editedDetails = new FeedbackTextResponseDetails(editedResponse);
        response.setResponseDetails(editedDetails);
        feedbackSubmitPage.submitTextResponse(1, receiver.getName(), response);

        feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
        feedbackSubmitPage.verifyTextResponse(1, receiver.getName(), response);
        verifyPresentInDatastore(response);
    }

    private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, String answer) {
        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails(answer);
        return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getEmail())
                .withResponseDetails(details)
                .build();
    }
}
