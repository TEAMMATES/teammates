package teammates.e2e.cases;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;
import teammates.test.ThreadHelper;

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
        testData = loadDataBundle("/InstructorFeedbackEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        feedbackSession = testData.feedbackSessions.get("openSession");
        course = testData.courses.get("course");
        copiedCourse = testData.courses.get("course2");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        InstructorFeedbackEditPage feedbackEditPage =
                loginToPage(url, InstructorFeedbackEditPage.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        feedbackEditPage.verifySessionDetails(course, feedbackSession);

        ______TS("edit session details");
        feedbackSession.setInstructions("<p><strong>new instructions</strong></p>");
        feedbackSession.setStartTime(ZonedDateTime.now(ZoneId.of(feedbackSession.getTimeZone())).plus(Duration.ofDays(2))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        feedbackSession.setEndTime(ZonedDateTime.now(ZoneId.of(feedbackSession.getTimeZone())).plus(Duration.ofDays(7))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        feedbackSession.setGracePeriodMinutes(30);
        feedbackSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        feedbackSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
        feedbackSession.setClosingEmailEnabled(false);

        feedbackEditPage.editSessionDetails(feedbackSession);
        feedbackEditPage.verifyStatusMessage("The feedback session has been updated.");
        feedbackEditPage.verifySessionDetails(course, feedbackSession);
        verifyPresentInDatabase(feedbackSession);

        ______TS("add template question");
        FeedbackQuestionAttributes templateQuestion = getTemplateQuestion();
        feedbackEditPage.addTemplateQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been added to this feedback session.");
        feedbackEditPage.verifyNumQuestions(1);
        feedbackEditPage.verifyQuestionDetails(1, templateQuestion);
        verifyPresentInDatabase(templateQuestion);

        ______TS("copy question from other session");
        FeedbackQuestionAttributes questionToCopy = testData.feedbackQuestions.get("qn1");
        questionToCopy.setCourseId(course.getId());
        questionToCopy.setFeedbackSessionName(feedbackSession.getFeedbackSessionName());
        questionToCopy.setQuestionNumber(2);
        feedbackEditPage.copyQuestion(copiedCourse.getId(), questionToCopy.getQuestionDetailsCopy().getQuestionText());

        feedbackEditPage.verifyStatusMessage("The selected question(s) have been added to this feedback session.");
        feedbackEditPage.verifyNumQuestions(2);
        feedbackEditPage.verifyQuestionDetails(2, questionToCopy);
        verifyPresentInDatabase(questionToCopy);

        ______TS("reorder questions");
        questionToCopy.setQuestionNumber(1);
        templateQuestion.setQuestionNumber(2);
        feedbackEditPage.editQuestionNumber(2, 1);

        feedbackEditPage.verifyStatusMessage("The changes to the question have been updated.");
        verifyReorder(questionToCopy);
        verifyReorder(templateQuestion);
        feedbackEditPage.verifyQuestionDetails(1, questionToCopy);
        feedbackEditPage.verifyQuestionDetails(2, templateQuestion);

        ______TS("edit question");
        FeedbackQuestionAttributes editedQuestion = getTemplateQuestion();
        editedQuestion.setQuestionNumber(1);
        String questionBrief = editedQuestion.getQuestionDetailsCopy().getQuestionText();
        editedQuestion.setQuestionDetails(new FeedbackTextQuestionDetails(questionBrief));
        editedQuestion.setQuestionDescription("<p><em>New Description</em></p>");
        feedbackEditPage.editQuestionDetails(1, editedQuestion);

        feedbackEditPage.verifyStatusMessage("The changes to the question have been updated.");
        feedbackEditPage.verifyQuestionDetails(1, editedQuestion);
        verifyPresentInDatabase(editedQuestion);

        ______TS("duplicate question");
        editedQuestion.setQuestionNumber(3);
        feedbackEditPage.duplicateQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been duplicated below.");
        feedbackEditPage.verifyNumQuestions(3);
        feedbackEditPage.verifyQuestionDetails(3, editedQuestion);
        verifyPresentInDatabase(editedQuestion);

        ______TS("delete question");
        templateQuestion.setQuestionNumber(1);
        feedbackEditPage.deleteQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been deleted.");
        feedbackEditPage.verifyNumQuestions(2);
        feedbackEditPage.verifyQuestionDetails(1, templateQuestion);
        // verify qn 1 has been replaced in database by qn 2
        verifyReorder(templateQuestion);

        ______TS("preview session as student");
        FeedbackSubmitPage previewPage = feedbackEditPage.previewAsStudent(
                testData.students.get("benny.tmms@IFEdit.CS2104"));
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("preview session as instructor");
        previewPage = feedbackEditPage.previewAsInstructor(instructor);
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("copy session to other course");
        feedbackSession.setCourseId(copiedCourse.getId());
        String copiedSessionName = "Copied Session";
        feedbackSession.setFeedbackSessionName(copiedSessionName);
        feedbackEditPage.copySessionToOtherCourse(copiedCourse, copiedSessionName);

        feedbackEditPage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        verifyPresentInDatabase(feedbackSession);

        ______TS("delete session");
        feedbackEditPage.deleteSession();

        feedbackEditPage.verifyStatusMessage("The feedback session has been deleted. "
                + "You can restore it from the deleted sessions table below.");
        assertNotNull(getSoftDeletedSession(copiedSessionName,
                instructor.getGoogleId()));
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
        detail.setQuestionText("How much work did each team member contribute?"
                + " (response will be shown anonymously to each team member).");

        return FeedbackQuestionAttributes.builder()
                .withCourseId(course.getId())
                .withFeedbackSessionName(feedbackSession.getFeedbackSessionName())
                .withQuestionDetails(detail)
                .withQuestionDescription("")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .withShowResponsesTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                        FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER))
                .withShowGiverNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withShowRecipientNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                        FeedbackParticipantType.RECEIVER))
                .build();
    }
}
