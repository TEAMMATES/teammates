package teammates.e2e.cases.sql;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}.
 */
public class InstructorFeedbackEditPageE2ETest extends BaseE2ETestCase {
    private Course course;
    private Course copiedCourse;
    private Instructor instructor;
    private FeedbackSession feedbackSession;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/InstructorFeedbackEditPageE2ETestSql.json"));
        course = testData.courses.get("InstFEP.CS2104");
        instructor = testData.instructors.get("InstFEP.instr");
        feedbackSession = testData.feedbackSessions.get("openSession");
        copiedCourse = testData.courses.get("InstFEP.CS1101");
    }

    @Test
    @Override
    protected void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());
        InstructorFeedbackEditPageSql feedbackEditPage =
                loginToPage(url, InstructorFeedbackEditPageSql.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        feedbackEditPage.verifySessionDetails(course, feedbackSession);

        ______TS("edit session details");
        feedbackSession.setStartTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(2))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        feedbackSession.setEndTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(7))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        feedbackSession.setGracePeriod(Duration.ofMinutes(20));
        feedbackSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        feedbackSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
        feedbackSession.setClosingSoonEmailEnabled(false);

        feedbackEditPage.editSessionDetails(feedbackSession, course);
        feedbackEditPage.verifyStatusMessage("The feedback session has been updated.");
        feedbackEditPage.verifySessionDetails(course, feedbackSession);
        verifyPresentInDatabase(feedbackSession);

        ______TS("add template question");
        FeedbackQuestion templateQuestion = getTemplateQuestion();
        FeedbackQuestion feedbackQuestion = testData.feedbackQuestions.get("qn1ForFirstCourseFirstSession");
        feedbackEditPage.addTemplateQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been added to this feedback session.");
        feedbackEditPage.verifyNumQuestions(2);
        feedbackEditPage.verifyQuestionDetails(2, templateQuestion);
        feedbackEditPage.verifyQuestionDetails(1, feedbackQuestion);
        verifyPresentInDatabase(feedbackQuestion);

        ______TS("copy question from other session");
        FeedbackQuestion questionToCopy = testData.feedbackQuestions.get("qn1ForSecondCourseFirstSession");
        questionToCopy.setFeedbackSession(feedbackSession);
        questionToCopy.setQuestionNumber(3);
        feedbackEditPage.copyQuestion(copiedCourse.getId(), questionToCopy.getQuestionDetailsCopy().getQuestionText());

        feedbackEditPage.verifyStatusMessage("The selected question(s) have been added to this feedback session.");
        feedbackEditPage.verifyNumQuestions(3);
        feedbackEditPage.verifyQuestionDetails(3, questionToCopy);
        verifyPresentInDatabase(questionToCopy);

        ______TS("reorder questions");
        feedbackQuestion.setQuestionNumber(2);
        templateQuestion.setQuestionNumber(1);
        feedbackEditPage.editQuestionNumber(2, 1);
        feedbackEditPage.verifyStatusMessage("The changes to the question have been updated.");

        feedbackEditPage.verifyQuestionDetails(1, templateQuestion);
        feedbackEditPage.verifyQuestionDetails(2, feedbackQuestion);
        feedbackEditPage.verifyQuestionDetails(3, questionToCopy);

        ______TS("edit question");
        FeedbackQuestion editedQuestion = getTemplateQuestion();
        editedQuestion.setQuestionNumber(1);
        editedQuestion.setDescription("<p><em>New Description</em></p>");
        feedbackEditPage.editQuestionDetails(1, editedQuestion);

        feedbackEditPage.verifyStatusMessage("The changes to the question have been updated.");
        feedbackEditPage.verifyQuestionDetails(1, editedQuestion);
        verifyPresentInDatabase(editedQuestion);

        ______TS("duplicate question");
        editedQuestion.setQuestionNumber(4);
        feedbackEditPage.duplicateQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been duplicated below.");
        feedbackEditPage.verifyNumQuestions(4);
        feedbackEditPage.verifyQuestionDetails(4, editedQuestion);
        verifyPresentInDatabase(editedQuestion);

        ______TS("delete question");
        feedbackEditPage.deleteQuestion(1);

        feedbackEditPage.verifyStatusMessage("The question has been deleted.");
        feedbackEditPage.verifyNumQuestions(3);

        feedbackQuestion.setQuestionNumber(feedbackQuestion.getQuestionNumber() - 1);
        editedQuestion.setQuestionNumber(editedQuestion.getQuestionNumber() - 1);
        questionToCopy.setQuestionNumber(questionToCopy.getQuestionNumber() - 1);
        feedbackEditPage.verifyQuestionDetails(1, feedbackQuestion);
        feedbackEditPage.verifyQuestionDetails(2, questionToCopy);
        feedbackEditPage.verifyQuestionDetails(3, editedQuestion);

        ______TS("preview session as student");
        FeedbackSubmitPage previewPage = feedbackEditPage.previewAsStudent(testData.students.get("InstFEP.jose.tmms"));
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("preview session as instructor");
        previewPage = feedbackEditPage.previewAsInstructor(instructor);
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("copy session to other course");
        feedbackSession.setCourse(copiedCourse);
        String copiedSessionName = "Copied Session";
        feedbackSession.setName(copiedSessionName);
        feedbackEditPage.copySessionToOtherCourse(copiedCourse, copiedSessionName);

        feedbackEditPage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        verifyPresentInDatabase(feedbackSession);

        ______TS("delete session");

        // TODO: uncomment when deleteSession() for sql is fixed
        //feedbackEditPage.deleteSession();
        //feedbackEditPage.verifyStatusMessage("The feedback session has been deleted. "
        //        + "You can restore it from the deleted sessions table below.");
        //assertNotNull(getSoftDeletedSession(copiedSessionName,
        //        instructor.getGoogleId()));

    }

    private FeedbackQuestion getTemplateQuestion() {
        FeedbackContributionQuestionDetails detail = new FeedbackContributionQuestionDetails();
        detail.setQuestionText("How much work did each team member contribute?"
                + " (response will be shown anonymously to each team member).");

        return FeedbackQuestion.makeQuestion(
                feedbackSession,
                2,
                "",
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
                Const.MAX_POSSIBLE_RECIPIENTS,
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                        FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS,
                        FeedbackParticipantType.RECEIVER), detail);

    }
}
