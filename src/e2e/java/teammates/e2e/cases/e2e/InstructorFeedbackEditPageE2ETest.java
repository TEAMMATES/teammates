package teammates.e2e.cases.e2e;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}.
 */
public class InstructorFeedbackEditPageE2ETest extends BaseE2ETestCase {
	private InstructorAttributes instructor;
	private FeedbackSessionAttributes feedbackSession;
	private CourseAttributes course;
	private CourseAttributes copiedCourse;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.INSTRUCTOR_FEEDBACK_EDIT_PAGE_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		instructor = testData.instructors.get(Const.TestCase.INSTRUCTOR_CONTENT);
		feedbackSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SESSION);
		course = testData.courses.get(Const.TestCase.COURSE_CONTENT);
		copiedCourse = testData.courses.get(Const.TestCase.COURSE_CONTENT2);
	}

	@Test
	public void allTests() throws Exception {
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE).withUserId(instructor.googleId)
				.withCourseId(course.getId()).withSessionName(feedbackSession.getFeedbackSessionName());
		InstructorFeedbackEditPage feedbackEditPage = loginAdminToPage(url, InstructorFeedbackEditPage.class);

		______TS(Const.TestCase.VERIFY_LOADED_DATA);
		feedbackEditPage.verifySessionDetails(course, feedbackSession);

		______TS(Const.TestCase.EDIT_SESSION_DETAILS);
		feedbackSession.setInstructions(Const.TestCase.P_STRONG_NEW_INSTRUCTIONS_STRONG_P);
		feedbackSession.setStartTime(feedbackSession.getEndTime().minus(30, ChronoUnit.DAYS));
		feedbackSession.setEndTime(feedbackSession.getEndTime().plus(30, ChronoUnit.DAYS));
		feedbackSession.setGracePeriodMinutes(30);
		feedbackSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
		feedbackSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
		feedbackSession.setClosingEmailEnabled(false);

		feedbackEditPage.editSessionDetails(feedbackSession);
		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_UPDATED);
		feedbackEditPage.verifySessionDetails(course, feedbackSession);
		verifyPresentInDatastore(feedbackSession);

		______TS(Const.TestCase.ADD_TEMPLATE_QUESTION);
		FeedbackQuestionAttributes templateQuestion = getTemplateQuestion();
		feedbackEditPage.addTemplateQuestion(1);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_QUESTION_HAS_BEEN_ADDED_TO_THIS_FEEDBACK_SESSION);
		feedbackEditPage.verifyNumQuestions(1);
		feedbackEditPage.verifyQuestionDetails(1, templateQuestion);
		verifyPresentInDatastore(templateQuestion);

		______TS(Const.TestCase.COPY_QUESTION_FROM_OTHER_SESSION);
		FeedbackQuestionAttributes questionToCopy = testData.feedbackQuestions.get(Const.TestCase.QN1);
		questionToCopy.courseId = course.getId();
		questionToCopy.feedbackSessionName = feedbackSession.getFeedbackSessionName();
		questionToCopy.questionNumber = 2;
		feedbackEditPage.copyQuestion(copiedCourse.getId(), questionToCopy.getQuestionDetails().getQuestionText());

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_QUESTION_HAS_BEEN_ADDED_TO_THIS_FEEDBACK_SESSION);
		feedbackEditPage.verifyNumQuestions(2);
		feedbackEditPage.verifyQuestionDetails(2, questionToCopy);
		verifyPresentInDatastore(questionToCopy);

		______TS(Const.TestCase.REORDER_QUESTIONS);
		questionToCopy.setQuestionNumber(1);
		templateQuestion.setQuestionNumber(2);
		feedbackEditPage.editQuestionNumber(2, 1);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_CHANGES_TO_THE_QUESTION_HAVE_BEEN_UPDATED);
		verifyReorder(questionToCopy);
		verifyReorder(templateQuestion);
		feedbackEditPage.verifyQuestionDetails(1, questionToCopy);
		feedbackEditPage.verifyQuestionDetails(2, templateQuestion);

		______TS(Const.TestCase.EDIT_QUESTION);
		FeedbackQuestionAttributes editedQuestion = getTemplateQuestion();
		editedQuestion.setQuestionNumber(1);
		String questionBrief = editedQuestion.getQuestionDetails().getQuestionText();
		editedQuestion.setQuestionDetails(new FeedbackTextQuestionDetails(questionBrief));
		editedQuestion.setQuestionDescription(Const.TestCase.P_EM_NEW_DESCRIPTION_EM_P);
		feedbackEditPage.editQuestionDetails(1, editedQuestion);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_CHANGES_TO_THE_QUESTION_HAVE_BEEN_UPDATED);
		feedbackEditPage.verifyQuestionDetails(1, editedQuestion);
		verifyPresentInDatastore(editedQuestion);

		______TS(Const.TestCase.DUPLICATE_QUESTION);
		editedQuestion.setQuestionNumber(3);
		feedbackEditPage.duplicateQuestion(1);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_QUESTION_HAS_BEEN_DUPLICATED_BELOW);
		feedbackEditPage.verifyNumQuestions(3);
		feedbackEditPage.verifyQuestionDetails(3, editedQuestion);
		verifyPresentInDatastore(editedQuestion);

		______TS(Const.TestCase.DELETE_QUESTION);
		templateQuestion.setQuestionNumber(1);
		feedbackEditPage.deleteQuestion(1);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_QUESTION_HAS_BEEN_DELETED);
		feedbackEditPage.verifyNumQuestions(2);
		feedbackEditPage.verifyQuestionDetails(1, templateQuestion);
		// verify qn 1 has been replaced in datastore by qn 2
		verifyReorder(templateQuestion);

		______TS(Const.TestCase.PREVIEW_SESSION_AS_STUDENT);
		FeedbackSubmitPage previewPage = feedbackEditPage
				.previewAsStudent(testData.students.get(Const.TestCase.BENNY_TMMS_C_FEEDBACK_EDIT_E2E_T_CS2104));
		previewPage.closeCurrentWindowAndSwitchToParentWindow();

		______TS(Const.TestCase.PREVIEW_SESSION_AS_INSTRUCTOR);
		previewPage = feedbackEditPage.previewAsInstructor(instructor);
		previewPage.closeCurrentWindowAndSwitchToParentWindow();

		______TS(Const.TestCase.COPY_SESSION_TO_OTHER_COURSE);
		feedbackSession.setCourseId(copiedCourse.getId());
		String copiedSessionName = Const.TestCase.COPIED_SESSION;
		feedbackSession.setFeedbackSessionName(copiedSessionName);
		feedbackEditPage.copySessionToOtherCourse(copiedCourse, copiedSessionName);

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_COPIED
				+ Const.TestCase.PLEASE_MODIFY_SETTINGS_QUESTIONS_AS_NECESSARY);
		verifyPresentInDatastore(feedbackSession);

		______TS(Const.TestCase.DELETE_SESSION);
		feedbackEditPage.deleteSession();

		feedbackEditPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_DELETED
				+ Const.TestCase.YOU_CAN_RESTORE_IT_FROM_THE_DELETED_SESSIONS_TABLE_BELOW);
		assertNotNull(getSoftDeletedSession(copiedSessionName, instructor.googleId));
	}

	private void verifyReorder(FeedbackQuestionAttributes question) {
		int retryLimit = 5;
		FeedbackQuestionAttributes actual = getFeedbackQuestion(question);
		while (!actual.equals(question) && retryLimit > 0) {
			retryLimit--;
			ThreadHelper.waitFor(1000);
			actual = getFeedbackQuestion(question);
		}
		assertEquals(question, actual);
	}

	private FeedbackQuestionAttributes getTemplateQuestion() {
		FeedbackContributionQuestionDetails detail = new FeedbackContributionQuestionDetails();
		detail.setQuestionText(Const.TestCase.YOUR_ESTIMATE_OF_HOW_MUCH_EACH_TEAM_MEMBER_HAS_CONTRIBUTED);
		detail.setNotSureAllowed(false);

		return FeedbackQuestionAttributes.builder().withCourseId(course.getId())
				.withFeedbackSessionName(feedbackSession.getFeedbackSessionName()).withQuestionDetails(detail)
				.withQuestionDescription("").withQuestionNumber(1).withGiverType(FeedbackParticipantType.STUDENTS)
				.withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
				.withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
				.withShowResponsesTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
						FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER))
				.withShowGiverNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS)).withShowRecipientNameTo(
						Arrays.asList(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER))
				.build();
	}
}
