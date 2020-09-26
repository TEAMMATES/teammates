package teammates.e2e.cases.e2e;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE} specifically for
 * Contribution questions.
 */
public class FeedbackContributionQuestionE2ETest extends BaseE2ETestCase {
	InstructorAttributes instructor;
	CourseAttributes course;
	FeedbackSessionAttributes feedbackSession;
	StudentAttributes student;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.FEEDBACK_CONTRIBUTION_QUESTION_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		instructor = testData.instructors.get(Const.TestCase.INSTRUCTOR_CONTENT);
		course = testData.courses.get(Const.TestCase.COURSE_CONTENT);
		feedbackSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SESSION);
		student = testData.students.get(Const.TestCase.ALICE_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104);
	}

	@Test
	public void testAll() {
		testEditPage();
		testSubmitPage();
	}

	private void testEditPage() {
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE).withUserId(instructor.googleId)
				.withCourseId(course.getId()).withSessionName(feedbackSession.getFeedbackSessionName());
		InstructorFeedbackEditPage feedbackEditPage = loginAdminToPage(url, InstructorFeedbackEditPage.class);
		feedbackEditPage.waitForPageToLoad();

		______TS(Const.TestCase.VERIFY_LOADED_QUESTION);
		FeedbackQuestionAttributes loadedQuestion = testData.feedbackQuestions.get(Const.TestCase.QN1_FOR_FIRST_SESSION)
				.getCopy();
		FeedbackContributionQuestionDetails questionDetails = (FeedbackContributionQuestionDetails) loadedQuestion
				.getQuestionDetails();
		feedbackEditPage.verifyContributionQuestionDetails(1, questionDetails);

		______TS(Const.TestCase.ADD_NEW_QUESTION);
		// add new question exactly like loaded question
		loadedQuestion.setQuestionNumber(2);
		feedbackEditPage.addContributionQuestion(loadedQuestion);

		feedbackEditPage.verifyContributionQuestionDetails(2, questionDetails);
		verifyPresentInDatastore(loadedQuestion);

		______TS(Const.TestCase.COPY_QUESTION);
		FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions
				.get(Const.TestCase.QN1_FOR_SECOND_SESSION);
		questionDetails = (FeedbackContributionQuestionDetails) copiedQuestion.getQuestionDetails();
		feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
				copiedQuestion.getQuestionDetails().getQuestionText());
		copiedQuestion.courseId = course.getId();
		copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
		copiedQuestion.setQuestionNumber(3);

		feedbackEditPage.verifyContributionQuestionDetails(3, questionDetails);
		verifyPresentInDatastore(copiedQuestion);

		______TS(Const.TestCase.EDIT_QUESTION);
		questionDetails = (FeedbackContributionQuestionDetails) loadedQuestion.getQuestionDetails();
		questionDetails.setNotSureAllowed(false);
		loadedQuestion.questionDetails = questionDetails;
		feedbackEditPage.editContributionQuestion(2, questionDetails);

		feedbackEditPage.verifyContributionQuestionDetails(2, questionDetails);
		verifyPresentInDatastore(loadedQuestion);
	}

	private void testSubmitPage() {
		AppUrl url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE).withUserId(student.googleId)
				.withCourseId(student.course).withSessionName(feedbackSession.getFeedbackSessionName())
				.withRegistrationKey(getKeyForStudent(student));
		FeedbackSubmitPage feedbackSubmitPage = loginAdminToPage(url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();

		______TS(Const.TestCase.VERIFY_LOADED_QUESTION);
		FeedbackQuestionAttributes question = testData.feedbackQuestions.get(Const.TestCase.QN1_FOR_FIRST_SESSION);
		StudentAttributes receiver = testData.students
				.get(Const.TestCase.BENNY_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104);
		StudentAttributes receiver2 = testData.students
				.get(Const.TestCase.CHARLIE_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104);
		feedbackSubmitPage.verifyContributionQuestion(1,
				(FeedbackContributionQuestionDetails) question.getQuestionDetails());

		______TS(Const.TestCase.SUBMIT_RESPONSE);
		String questionId = getFeedbackQuestion(question).getId();
		FeedbackResponseAttributes response = getResponse(questionId, student, 170);
		FeedbackResponseAttributes response2 = getResponse(questionId, receiver, 180);
		FeedbackResponseAttributes response3 = getResponse(questionId, receiver2, 60);
		List responses = Arrays.asList(response, response2, response3);
		feedbackSubmitPage.submitContributionResponse(1, responses);

		verifyPresentInDatastore(response);
		verifyPresentInDatastore(response2);
		verifyPresentInDatastore(response3);

		______TS(Const.TestCase.CHECK_PREVIOUS_RESPONSE);
		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyContributionResponse(1, responses);

		______TS(Const.TestCase.EDIT_RESPONSE);
		response = getResponse(questionId, student, 50);
		response2 = getResponse(questionId, receiver, Const.POINTS_EQUAL_SHARE);
		response3 = getResponse(questionId, receiver2, Const.POINTS_NOT_SURE);
		responses = Arrays.asList(response, response2, response3);
		feedbackSubmitPage.submitContributionResponse(1, responses);

		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyContributionResponse(1, responses);
		verifyPresentInDatastore(response);
		verifyPresentInDatastore(response2);
		verifyPresentInDatastore(response3);
	}

	private FeedbackResponseAttributes getResponse(String questionId, StudentAttributes receiver, int answer) {
		FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
		details.setAnswer(answer);
		return FeedbackResponseAttributes.builder(questionId, student.getEmail(), receiver.getEmail())
				.withResponseDetails(details).build();
	}
}
