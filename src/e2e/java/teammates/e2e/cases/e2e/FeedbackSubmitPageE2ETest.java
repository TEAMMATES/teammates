package teammates.e2e.cases.e2e;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 */
public class FeedbackSubmitPageE2ETest extends BaseE2ETestCase {
	private StudentAttributes student;
	private InstructorAttributes instructor;

	private FeedbackSessionAttributes openSession;
	private FeedbackSessionAttributes closedSession;
	private FeedbackSessionAttributes gracePeriodSession;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.FEEDBACK_SUBMIT_PAGE_E2E_TEST_JSON);
		testData.feedbackSessions.get(Const.TestCase.GRACE_PERIOD_SESSION).setEndTime(Instant.now());
		student = testData.students.get(Const.TestCase.ALICE);
		if (!TestProperties.isDevServer()) {
			student.email = TestProperties.TEST_STUDENT1_ACCOUNT;
		}
		removeAndRestoreDataBundle(testData);

		instructor = testData.instructors.get(Const.TestCase.SF_SUBMIT_E2E_T_INSTR);
		openSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SPACE_SESSION);
		closedSession = testData.feedbackSessions.get(Const.TestCase.CLOSED_SPACE_SESSION);
		gracePeriodSession = testData.feedbackSessions.get(Const.TestCase.GRACE_PERIOD_SESSION);
	}

	@Test
	public void testAll() {
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
				.withUserId(instructor.getGoogleId()).withCourseId(openSession.getCourseId())
				.withSessionName(openSession.getFeedbackSessionName());
		FeedbackSubmitPage submitPage = loginAdminToPage(url, FeedbackSubmitPage.class);

		______TS(Const.TestCase.VERIFY_LOADED_SESSION_DATA);
		submitPage.verifyFeedbackSessionDetails(openSession);

		______TS(Const.TestCase.QUESTIONS_WITH_GIVER_TYPE_INSTRUCTOR);
		submitPage.verifyNumQuestions(1);
		submitPage.verifyQuestionDetails(1, testData.feedbackQuestions.get(Const.TestCase.QN5_IN_SESSION1));

		______TS(Const.TestCase.QUESTIONS_WITH_GIVER_TYPE_STUDENTS);
		submitPage = loginAdminToPage(getStudentSubmitPageUrl(student, openSession), FeedbackSubmitPage.class);

		submitPage.verifyNumQuestions(4);
		submitPage.verifyQuestionDetails(1, testData.feedbackQuestions.get(Const.TestCase.QN1_IN_SESSION1));
		submitPage.verifyQuestionDetails(2, testData.feedbackQuestions.get(Const.TestCase.QN2_IN_SESSION1));
		submitPage.verifyQuestionDetails(3, testData.feedbackQuestions.get(Const.TestCase.QN3_IN_SESSION1));
		submitPage.verifyQuestionDetails(4, testData.feedbackQuestions.get(Const.TestCase.QN4_IN_SESSION1));

		______TS(Const.TestCase.VERIFY_RECIPIENTS_STUDENTS);
		submitPage.verifyLimitedRecipients(1, 3, getOtherStudents(student));

		______TS(Const.TestCase.VERIFY_RECIPIENTS_INSTRUCTORS);
		submitPage.verifyRecipients(2, getInstructors(), Const.TestCase.INIT_CAP_INSTRUCTOR);

		______TS(Const.TestCase.VERIFY_RECIPIENTS_TEAM_MATES);
		submitPage.verifyRecipients(3, getTeammates(student), Const.TestCase.INIT_CAP_STUDENT);

		______TS(Const.TestCase.VERIFY_RECIPIENTS_TEAMS);
		submitPage.verifyRecipients(4, getOtherTeams(student), Const.TestCase.INIT_CAP_TEAM);

		______TS(Const.TestCase.SUBMIT_PARTIAL_RESPONSE);
		int[] unansweredQuestions = { 1, 2, 3, 4 };
		submitPage.verifyWarningMessageForPartialResponse(unansweredQuestions);

		______TS(Const.TestCase.CANNOT_SUBMIT_IN_CLOSED_SESSION);
		AppUrl closedSessionUrl = getStudentSubmitPageUrl(student, closedSession);
		submitPage = AppPage.getNewPageInstance(browser, closedSessionUrl, FeedbackSubmitPage.class);
		submitPage.verifyCannotSubmit();

		______TS(Const.TestCase.CAN_SUBMIT_IN_GRACE_PERIOD);
		AppUrl gracePeriodSessionUrl = getStudentSubmitPageUrl(student, gracePeriodSession);
		submitPage = AppPage.getNewPageInstance(browser, gracePeriodSessionUrl, FeedbackSubmitPage.class);
		FeedbackQuestionAttributes question = testData.feedbackQuestions
				.get(Const.TestCase.QN1_IN_GRACE_PERIOD_SESSION);
		String questionId = getFeedbackQuestion(question).getId();
		String recipient = Const.TestCase.INIT_CAP_TEAM_2;
		FeedbackResponseAttributes response = getMcqResponse(questionId, recipient, false, Const.TestCase.UI);
		submitPage.submitMcqResponse(1, recipient, response);

		verifyPresentInDatastore(response);

		______TS(Const.TestCase.CONFIRMATION_EMAIL);
		submitPage.markWithConfirmationEmail();
		submitPage.submitMcqResponse(1, recipient, response);

		verifyEmailSent(student.getEmail(),
				Const.TestCase.TEAMMATES_FEEDBACK_RESPONSES_SUCCESSFULLY_RECORDED
						+ Const.TestCase.LEFT_SQUARE_PAREN_COURSE
						+ testData.courses.get(Const.TestCase.SF_SUBMIT_E2E_T_CS2104).getName()
						+ Const.TestCase.LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION
						+ gracePeriodSession.getFeedbackSessionName() + Const.TestCase.RIGHT_SQUARE_PAREN);

		______TS(Const.TestCase.ADD_COMMENT);
		String responseId = getFeedbackResponse(response).getId();
		int qnToComment = 1;
		String comment = Const.TestCase.P_NEW_COMMENT_P;
		submitPage.addComment(qnToComment, recipient, comment);

		submitPage.verifyComment(qnToComment, recipient, comment);
		verifyPresentInDatastore(getFeedbackResponseComment(responseId, comment));

		______TS(Const.TestCase.EDIT_COMMENT);
		comment = Const.TestCase.P_EDITED_COMMENT_P;
		submitPage.editComment(qnToComment, recipient, comment);

		submitPage.verifyComment(qnToComment, recipient, comment);
		verifyPresentInDatastore(getFeedbackResponseComment(responseId, comment));

		______TS(Const.TestCase.DELETE_COMMENT);
		submitPage.deleteComment(qnToComment, recipient);

		submitPage.verifyStatusMessage(Const.TestCase.YOUR_COMMENT_HAS_BEEN_DELETED);
		submitPage.verifyNoCommentPresent(qnToComment, recipient);
		verifyAbsentInDatastore(getFeedbackResponseComment(responseId, comment));

		______TS(Const.TestCase.PREVIEW_AS_STUDENT);
		url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE).withUserId(instructor.googleId)
				.withCourseId(openSession.getCourseId()).withSessionName(openSession.getFeedbackSessionName())
				.withParam(Const.TestCase.PREVIEWAS, student.getEmail());
		submitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);

		submitPage.verifyFeedbackSessionDetails(openSession);
		submitPage.verifyNumQuestions(4);
		submitPage.verifyQuestionDetails(1, testData.feedbackQuestions.get(Const.TestCase.QN1_IN_SESSION1));
		submitPage.verifyQuestionDetails(2, testData.feedbackQuestions.get(Const.TestCase.QN2_IN_SESSION1));
		submitPage.verifyQuestionDetails(3, testData.feedbackQuestions.get(Const.TestCase.QN3_IN_SESSION1));
		submitPage.verifyQuestionDetails(4, testData.feedbackQuestions.get(Const.TestCase.QN4_IN_SESSION1));
		submitPage.verifyCannotSubmit();

		______TS(Const.TestCase.PREVIEW_AS_INSTRUCTOR);
		url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE).withUserId(instructor.googleId)
				.withCourseId(openSession.getCourseId()).withSessionName(openSession.getFeedbackSessionName())
				.withParam(Const.TestCase.PREVIEWAS, instructor.getEmail());
		submitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);

		submitPage.verifyFeedbackSessionDetails(openSession);
		submitPage.verifyNumQuestions(1);
		submitPage.verifyQuestionDetails(1, testData.feedbackQuestions.get(Const.TestCase.QN5_IN_SESSION1));
		submitPage.verifyCannotSubmit();

		______TS(Const.TestCase.MODERATING_INSTRUCTOR_CANNOT_SEE_QUESTIONS_WITHOUT_INSTRUCTOR_VISIBILITY);
		url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE).withUserId(instructor.googleId)
				.withCourseId(gracePeriodSession.getCourseId())
				.withSessionName(gracePeriodSession.getFeedbackSessionName())
				.withParam(Const.TestCase.MODERATEDPERSON, student.getEmail())
				.withParam(Const.TestCase.MODERATEDQUESTION_ID, questionId);
		submitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);

		submitPage.verifyFeedbackSessionDetails(gracePeriodSession);
		// One out of two questions in grace period session should not be visible
		submitPage.verifyNumQuestions(1);
		submitPage.verifyQuestionDetails(1, question);

		______TS(Const.TestCase.SUBMIT_MODERATED_RESPONSE);
		response = getMcqResponse(questionId, recipient, false, Const.TestCase.ALGO);
		submitPage.submitMcqResponse(1, recipient, response);

		verifyPresentInDatastore(response);
	}

	private AppUrl getStudentSubmitPageUrl(StudentAttributes student, FeedbackSessionAttributes session) {
		return createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE).withUserId(student.googleId)
				.withCourseId(student.course).withSessionName(session.getFeedbackSessionName())
				.withRegistrationKey(getKeyForStudent(student));
	}

	private List<String> getOtherStudents(StudentAttributes currentStudent) {
		return testData.students.values().stream().filter(s -> !s.equals(currentStudent)).map(s -> s.getName())
				.collect(Collectors.toList());
	}

	private List<String> getInstructors() {
		return testData.instructors.values().stream().map(i -> i.getName()).collect(Collectors.toList());
	}

	private List<String> getTeammates(StudentAttributes currentStudent) {
		return testData.students.values().stream()
				.filter(s -> !s.equals(currentStudent) && s.getTeam().equals(currentStudent.getTeam()))
				.map(s -> s.getName()).collect(Collectors.toList());
	}

	private List<String> getOtherTeams(StudentAttributes currentStudent) {
		return new ArrayList<>(
				testData.students.values().stream().filter(s -> !s.getTeam().equals(currentStudent.getTeam()))
						.map(s -> s.getTeam()).collect(Collectors.toSet()));
	}

	private FeedbackResponseAttributes getMcqResponse(String questionId, String recipient, boolean isOther,
			String answer) {
		FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
		if (isOther) {
			details.setOther(true);
			details.setOtherFieldContent(answer);
		} else {
			details.setAnswer(answer);
		}
		return FeedbackResponseAttributes.builder(questionId, student.getEmail(), recipient)
				.withResponseDetails(details).build();
	}

	private FeedbackResponseCommentAttributes getFeedbackResponseComment(String responseId, String comment) {
		return FeedbackResponseCommentAttributes.builder().withFeedbackResponseId(responseId)
				.withCommentGiver(student.getEmail()).withCommentFromFeedbackParticipant(true).withCommentText(comment)
				.build();
	}
}
