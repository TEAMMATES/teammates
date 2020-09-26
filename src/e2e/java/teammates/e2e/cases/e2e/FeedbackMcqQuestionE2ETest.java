package teammates.e2e.cases.e2e;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE},
 * {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE} specifically for MCQ
 * questions.
 */
public class FeedbackMcqQuestionE2ETest extends BaseE2ETestCase {
	InstructorAttributes instructor;
	CourseAttributes course;
	FeedbackSessionAttributes feedbackSession;
	StudentAttributes student;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.FEEDBACK_MCQ_QUESTION_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		instructor = testData.instructors.get(Const.TestCase.INSTRUCTOR_CONTENT);
		course = testData.courses.get(Const.TestCase.COURSE_CONTENT);
		feedbackSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SESSION);
		student = testData.students.get(Const.TestCase.ALICE_TMMS_F_MCQ_QUESTION_E2E_T_CS2104);
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
		FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetails();
		feedbackEditPage.verifyMcqQuestionDetails(1, questionDetails);

		______TS(Const.TestCase.ADD_NEW_QUESTION);
		// add new question exactly like loaded question
		loadedQuestion.setQuestionNumber(2);
		feedbackEditPage.addMcqQuestion(loadedQuestion);

		feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
		verifyPresentInDatastore(loadedQuestion);

		______TS(Const.TestCase.COPY_QUESTION);
		FeedbackQuestionAttributes copiedQuestion = testData.feedbackQuestions
				.get(Const.TestCase.QN1_FOR_SECOND_SESSION);
		questionDetails = (FeedbackMcqQuestionDetails) copiedQuestion.getQuestionDetails();
		feedbackEditPage.copyQuestion(copiedQuestion.getCourseId(),
				copiedQuestion.getQuestionDetails().getQuestionText());
		copiedQuestion.courseId = course.getId();
		copiedQuestion.feedbackSessionName = feedbackSession.getFeedbackSessionName();
		copiedQuestion.setQuestionNumber(3);

		feedbackEditPage.verifyMcqQuestionDetails(3, questionDetails);
		verifyPresentInDatastore(copiedQuestion);

		______TS(Const.TestCase.EDIT_QUESTION);
		questionDetails = (FeedbackMcqQuestionDetails) loadedQuestion.getQuestionDetails();
		questionDetails.setHasAssignedWeights(false);
		questionDetails.setMcqWeights(new ArrayList<>());
		questionDetails.setOtherEnabled(false);
		questionDetails.setMcqOtherWeight(0);
		questionDetails.setNumOfMcqChoices(4);
		List<String> choices = questionDetails.getMcqChoices();
		choices.add(Const.TestCase.EDITED_CHOICE);
		questionDetails.setMcqChoices(choices);
		loadedQuestion.questionDetails = questionDetails;
		feedbackEditPage.editMcqQuestion(2, questionDetails);

		feedbackEditPage.verifyMcqQuestionDetails(2, questionDetails);
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
		feedbackSubmitPage.verifyMcqQuestion(1, Const.TestCase.EMPTY_STRING,
				(FeedbackMcqQuestionDetails) question.getQuestionDetails());

		______TS(Const.TestCase.VERIFY_QUESTION_WITH_GENERATED_OPTIONS);
		feedbackSubmitPage.verifyGeneratedMcqQuestion(3, Const.TestCase.EMPTY_STRING, getGeneratedStudentOptions());

		______TS(Const.TestCase.SUBMIT_RESPONSE);
		String questionId = getFeedbackQuestion(question).getId();
		FeedbackResponseAttributes response = getResponse(questionId, false, Const.TestCase.UI);
		feedbackSubmitPage.submitMcqResponse(1, Const.TestCase.EMPTY_STRING, response);

		verifyPresentInDatastore(response);

		______TS(Const.TestCase.CHECK_PREVIOUS_RESPONSE);
		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyMcqResponse(1, Const.TestCase.EMPTY_STRING, response);

		______TS(Const.TestCase.EDIT_RESPONSE);
		response = getResponse(questionId, true, Const.TestCase.THIS_IS_THE_EDITED_RESPONSE);
		feedbackSubmitPage.submitMcqResponse(1, Const.TestCase.EMPTY_STRING, response);

		feedbackSubmitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
		feedbackSubmitPage.waitForPageToLoad();
		feedbackSubmitPage.verifyMcqResponse(1, Const.TestCase.EMPTY_STRING, response);
		verifyPresentInDatastore(response);
	}

	private List<String> getGeneratedStudentOptions() {
		return testData.students.values().stream().filter(s -> s.getCourse().equals(student.course))
				.map(s -> s.getName() + Const.TestCase.SPACE_OPEN_BRACE + s.getTeam() + Const.TestCase.CLOSE_BRACE)
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
		return FeedbackResponseAttributes.builder(questionId, student.getEmail(), Const.GENERAL_QUESTION)
				.withResponseDetails(details).build();
	}
}
