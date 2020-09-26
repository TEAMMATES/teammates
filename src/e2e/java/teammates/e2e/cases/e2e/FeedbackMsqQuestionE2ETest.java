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
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE} specifically for MSQ
 * questions.
 */
public class FeedbackMsqQuestionE2ETest extends BaseE2ETestCase {
	InstructorAttributes instructor;
	CourseAttributes course;
	FeedbackSessionAttributes feedbackSession;
	StudentAttributes student;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.FEEDBACK_MSQ_QUESTION_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		instructor = testData.instructors.get(Const.TestCase.INSTRUCTOR_CONTENT);
		course = testData.courses.get(Const.TestCase.COURSE_CONTENT);
		feedbackSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SESSION);
		student = testData.students.get(Const.TestCase.ALICE_TMMS_F_MSQ_QUESTION_E2E_T_CS2104);
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
		FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion.getQuestionDetails();
		feedbackEditPage.verifyMsqQuestionDetails(1, questionDetails);

		______TS(Const.TestCase.ADD_NEW_QUESTION);
		// add new question exactly like loaded question
		loadedQuestion.setQuestionNumber(2);
		feedbackEditPage.addMsqQuestion(loadedQuestion);

		feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
		verifyPresentInDatastore(loadedQuestion);

		______TS(Const.TestCase.COPY_QUESTION);
		FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions
				.get(Const.TestCase.QN1_FOR_SECOND_SESSION);
		questionDetails = (FeedbackMsqQuestionDetails) copiedQuestion.getQuestionDetails();
		feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
				copiedQuestion.getQuestionDetails().getQuestionText());
		copiedQuestion.courseId = course.getId();
		copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
		copiedQuestion.setQuestionNumber(3);

		feedbackEditPage.verifyMsqQuestionDetails(3, questionDetails);
		verifyPresentInDatastore(copiedQuestion);

		______TS(Const.TestCase.EDIT_QUESTION);
		questionDetails = (FeedbackMsqQuestionDetails) loadedQuestion.getQuestionDetails();
		questionDetails.setHasAssignedWeights(false);
		questionDetails.setMsqWeights(new ArrayList<>());
		questionDetails.setOtherEnabled(false);
		questionDetails.setMsqOtherWeight(0);
		questionDetails.setMaxSelectableChoices(Integer.MIN_VALUE);
		List<String> choices = questionDetails.getMsqChoices();
		choices.add(Const.TestCase.EDITED_CHOICE);
		questionDetails.setMsqChoices(choices);
		loadedQuestion.questionDetails = questionDetails;
		feedbackEditPage.editMsqQuestion(2, questionDetails);

		feedbackEditPage.verifyMsqQuestionDetails(2, questionDetails);
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
		StudentAttributes receiver = testData.students.get(Const.TestCase.BENNY_TMMS_F_MSQ_QUESTION_E2E_T_CS2104);
		feedbackSubmitPage.verifyMsqQuestion(1, receiver.getName(),
				(FeedbackMsqQuestionDetails) question.getQuestionDetails());

		______TS(Const.TestCase.VERIFY_LOADED_QUESTION_WITH_GENERATED_OPTIONS);
		FeedbackQuestionAttributes generatedQn = testData.feedbackQuestions.get(Const.TestCase.QN1_FOR_SECOND_SESSION);
		feedbackSubmitPage.verifyGeneratedMsqQuestion(3, Const.TestCase.EMPTY_STRING,
				(FeedbackMsqQuestionDetails) generatedQn.getQuestionDetails(), getGeneratedTeams());

		______TS(Const.TestCase.SUBMIT_RESPONSE);
		String questionId = getFeedbackQuestion(question).getId();
		List<String> answers = Arrays.asList(Const.TestCase.LEADERSHIP, Const.TestCase.THIS_IS_THE_OTHER_RESPONSE);
		FeedbackResponseAttributes response = getResponse(questionId, receiver, answers.get(answers.size() - 1),
				answers);
		feedbackSubmitPage.submitMsqResponse(1, receiver.getName(), response);

		verifyPresentInDatastore(response);

		______TS(Const.TestCase.CHECK_PREVIOUS_RESPONSE);
		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);

		______TS(Const.TestCase.EDIT_RESPONSE);
		answers = Arrays.asList(Const.TestCase.EMPTY_STRING);
		response = getResponse(questionId, receiver, Const.TestCase.EMPTY_STRING, answers);
		feedbackSubmitPage.submitMsqResponse(1, receiver.getName(), response);

		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyMsqResponse(1, receiver.getName(), response);
		verifyPresentInDatastore(response);
	}

	private List<String> getGeneratedTeams() {
		return testData.students.values().stream().filter(s -> s.getCourse().equals(student.course))
				.map(s -> s.getTeam()).distinct().collect(Collectors.toList());
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
				.withResponseDetails(details).build();
	}
}
