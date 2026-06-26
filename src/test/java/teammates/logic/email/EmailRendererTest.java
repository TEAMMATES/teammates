package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestRejectionType;
import teammates.common.util.EmailType;
import teammates.common.util.LinksUtil;
import teammates.logic.email.model.AccountVerificationApprovedEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
import teammates.logic.email.model.AccountVerificationRejectedEmailContext;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.CourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.CourseSessionLinks;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.FeedbackSessionOwnerReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionParticipantReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionPreviewReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsParticipantEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsPreviewEmailContext;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionAccessLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailRenderer}.
 */
public class EmailRendererTest extends BaseTestCase {

    private static final UUID FEEDBACK_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String REG_KEY = "ABCDEF0123456789ABCDEF0123456789";

    @Test
    public void renderSessionLinksRecoveryEmail_courseSectionsExist_returnsRecoveryEmailBody() throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of(
                        new CourseSessionLinks(
                                "CS101",
                                "Software Engineering",
                                "Africa/Johannesburg",
                                List.of(
                                        new SessionAccessLink(
                                                "Midterm Feedback",
                                                Instant.parse("2027-04-30T21:59:00Z"),
                                                "https://example.com/submission",
                                                "https://example.com/results")))));

        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryEmail(context);

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerCourseSectionsExistEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderSessionLinksRecoveryEmail_courseSectionsDoNotExist_returnsRecoveryEmailBodyWithoutLinks()
            throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of());

        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryEmail(context);

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerNoCourseSectionsEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderSessionLinksRecoveryNotFoundEmail_emailDoesNotMatchStudent_returnsNotFoundEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderSessionLinksRecoveryNotFoundEmail("missing@teammates.tmt");

        verifyEmailContent(actual.htmlContent(), "/sessionLinksRecoveryComposerNotFoundEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionOpenedParticipantEmail_student_returnsOpenedEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionOpenedParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "student1@course1.tmt",
                        "student1 In Course1</td></div>'\"",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        false,
                        "Please please fill in the following questions.",
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        false,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionOpenedEmailForStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionOpenedParticipantEmail_instructor_returnsOpenedEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionOpenedParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        false,
                        "Please please fill in the following questions.",
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        true,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionOpenedEmailForInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionOpenedPreviewEmail_coOwner_returnsPreviewEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionOpenedPreviewEmail(
                new FeedbackSessionPreviewReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionOpenedEmailCopyToInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionClosingSoonParticipantEmail_student_returnsClosingSoonEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionClosingSoonParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "student1@course1.tmt",
                        "student1 In Course1</td></div>'\"",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        true,
                        "Please please fill in the following questions.",
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        false,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionClosingSoonEmailForStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionClosingSoonParticipantEmail_instructor_returnsClosingSoonEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionClosingSoonParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        false,
                        "Please please fill in the following questions.",
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        true,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionClosingSoonEmailForInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionClosingSoonPreviewEmail_coOwner_returnsClosingSoonPreviewEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionClosingSoonPreviewEmail(
                new FeedbackSessionPreviewReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionClosingSoonEmailCopyToInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionReminderParticipantEmail_student_returnsReminderEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionReminderParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "student1@course1.tmt",
                        "student1 In Course1</td></div>'\"",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        false,
                        "Please please fill in the following questions.",
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        false,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionReminderEmailForStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionReminderParticipantEmail_instructor_returnsReminderEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionReminderParticipantEmail(
                new FeedbackSessionParticipantReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        false,
                        "Please please fill in the following questions.",
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        true,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionReminderEmailForInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionReminderPreviewEmail_instructor_returnsReminderPreviewEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionReminderPreviewEmail(
                new FeedbackSessionPreviewReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionReminderEmailCopyToInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionPublishedParticipantEmail_student_returnsPublishedEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionPublishedParticipantEmail(
                new FeedbackSessionResultsParticipantEmailContext(
                        "student1@course1.tmt",
                        "student1 In Course1</td></div>'\"",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        LinksUtil.getStudentSessionResultsUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        false,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionPublishedEmailForStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionPublishedParticipantEmail_instructor_returnsPublishedEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionPublishedParticipantEmail(
                new FeedbackSessionResultsParticipantEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        LinksUtil.getInstructorSessionResultsUrl(FEEDBACK_SESSION_ID),
                        true,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionPublishedEmailForInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionPublishedPreviewEmail_coOwner_returnsPublishedPreviewEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionPublishedPreviewEmail(
                new FeedbackSessionResultsPreviewEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionPublishedEmailCopyToInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionUnpublishedParticipantEmail_student_returnsUnpublishedEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionUnpublishedParticipantEmail(
                new FeedbackSessionResultsParticipantEmailContext(
                        "student1@course1.tmt",
                        "student1 In Course1</td></div>'\"",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        null,
                        false,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionUnpublishedEmailForStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionUnpublishedParticipantEmail_instructor_returnsUnpublishedEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionUnpublishedParticipantEmail(
                new FeedbackSessionResultsParticipantEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        null,
                        true,
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionUnpublishedEmailForInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionUnpublishedPreviewEmail_coOwner_returnsUnpublishedPreviewEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionUnpublishedPreviewEmail(
                new FeedbackSessionResultsPreviewEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "First feedback session",
                        buildCourseContext().coOwnerContacts()));

        verifyEmailContent(actual.htmlContent(), "/sessionUnpublishedEmailCopyToInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionOpeningSoonEmail_joinedCoOwner_returnsOpeningSoonEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionOpeningSoonEmail(
                new FeedbackSessionOwnerReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-29T18:00:00Z"),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        LinksUtil.getInstructorSessionEditUrl(FEEDBACK_SESSION_ID),
                        null,
                        null));

        verifyEmailContent(actual.htmlContent(), "/sessionOpeningSoonEmailForCoOwnerJoined.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionOpeningSoonEmail_notJoinedCoOwner_returnsOpeningSoonEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionOpeningSoonEmail(
                new FeedbackSessionOwnerReminderEmailContext(
                        "instructorNotYetJoinedCourse1@email.tmt",
                        "Instructor Not Yet Joined Course 1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-29T18:00:00Z"),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        LinksUtil.getInstructorSessionEditUrl(FEEDBACK_SESSION_ID),
                        null,
                        LinksUtil.getInstructorCourseJoinUrl(USER_ID, REG_KEY)));

        verifyEmailContent(actual.htmlContent(), "/sessionOpeningSoonEmailForCoOwnerNotJoined.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionClosedEmail_coOwner_returnsClosedEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionClosedEmail(
                new FeedbackSessionOwnerReminderEmailContext(
                        "instructor1@course1.tmt",
                        "Instructor1 Course1",
                        "idOfTypicalCourse1",
                        "Typical Course 1 with 2 Evals",
                        "Africa/Johannesburg",
                        "First feedback session",
                        Instant.parse("2027-04-29T18:00:00Z"),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        "Please please fill in the following questions.",
                        null,
                        LinksUtil.getInstructorSessionReportUrl(FEEDBACK_SESSION_ID),
                        null));

        verifyEmailContent(actual.htmlContent(), "/sessionClosedEmailForCoOwner.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionSummaryEmail_studentEmailChanged_rendersTableAndJoinPrompt() throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionSummaryEmail(
                buildFeedbackSessionSummaryContext(false, true, List.of(
                        new SessionAccessLink(
                                "Midterm Feedback",
                                Instant.parse("2027-04-30T21:59:00Z"),
                                "https://example.com/submission",
                                "https://example.com/results"))),
                EmailType.STUDENT_EMAIL_CHANGED);

        verifyEmailContent(actual.htmlContent(), "/feedbackSessionSummaryEmailForUpdatedStudent.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderFeedbackSessionSummaryEmail_instructorLinksRegenerated_withoutAccessibleLinksRendersFallbacks()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderFeedbackSessionSummaryEmail(
                buildFeedbackSessionSummaryContext(true, false, List.of(
                        new SessionAccessLink(
                                "Final Reflection",
                                Instant.parse("2026-04-28T21:59:00Z"),
                                null,
                                null))),
                EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);

        verifyEmailContent(actual.htmlContent(), "/feedbackSessionSummaryEmailForRegeneratedInstructor.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderUserCourseRegisteredEmail_student_returnsRegisteredEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderUserCourseRegisteredEmail(
                new CourseEmailContext("idOfTypicalCourse1", "Course Name", List.of()),
                new UserCourseRegisteredEmailContext(
                        "student@email.tmt",
                        "User Name",
                        false,
                        LinksUtil.getStudentHomePageUrl()));

        verifyEmailContent(actual.htmlContent(), "/studentCourseRegisterEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderUserCourseRegisteredEmail_instructor_returnsRegisteredEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderUserCourseRegisteredEmail(
                new CourseEmailContext("idOfTypicalCourse1", "Course Name", List.of()),
                new UserCourseRegisteredEmailContext(
                        "instructor@email.tmt",
                        "User Name",
                        true,
                        LinksUtil.getInstructorHomePageUrl()));

        verifyEmailContent(actual.htmlContent(), "/instructorCourseRegisterEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationApprovedEmail_typicalCase_returnsApprovalEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationApprovedEmail(
                new AccountVerificationApprovedEmailContext(
                        "elena.hart@northbridge.edu",
                        "Dr Elena Hart",
                        LinksUtil.getInstructorWelcomeUrl(UUID.fromString("33333333-3333-3333-3333-333333333333"))));

        verifyEmailContent(actual.htmlContent(), "/accountVerificationApprovedEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationApprovedEmail_nameNeedsSanitization_returnsSanitizedApprovalEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationApprovedEmail(
                new AccountVerificationApprovedEmailContext(
                        "elena.hart@northbridge.edu",
                        "Instructor<script> alert('hi!'); </script>",
                        LinksUtil.getInstructorWelcomeUrl(UUID.fromString("33333333-3333-3333-3333-333333333333"))));

        verifyEmailContent(actual.htmlContent(), "/accountVerificationApprovedEmailTestingSanitization.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationRejectedEmail_withOthersType_returnsRejectedEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationRejectedEmail(
                new AccountVerificationRejectedEmailContext(
                        "elena.hart@northbridge.edu",
                        "Northbridge Institute of Technology",
                        AccountVerificationRequestRejectionType.OTHERS,
                        null));

        verifyEmailContent(actual.htmlContent(), "/accountVerificationRejectedEmailOthers.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationRejectedEmail_withAdditionalComments_includesComments() throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationRejectedEmail(
                new AccountVerificationRejectedEmailContext(
                        "elena.hart@northbridge.edu",
                        "Northbridge Institute of Technology",
                        AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL,
                        "Please use your official university email address."));

        verifyEmailContent(actual.htmlContent(), "/accountVerificationRejectedEmailWithAdditionalComments.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationCreatedAdminAlertEmail_withComments_returnsAlertEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationCreatedAdminAlertEmail(
                new AccountVerificationCreatedAdminAlertEmailContext(
                        "admin@teammates.tmt",
                        "Dr Elena Hart",
                        "Northbridge Institute of Technology",
                        "elena.hart@northbridge.edu",
                        "I will be using TEAMMATES for peer evaluation in introductory software design courses.",
                        LinksUtil.getAdminHomePageUrl()));

        verifyEmailContent(actual.htmlContent(), "/adminNewAccountVerificationRequestAlertEmailWithComments.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationCreatedAdminAlertEmail_withoutComments_returnsAlertEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationCreatedAdminAlertEmail(
                new AccountVerificationCreatedAdminAlertEmailContext(
                        "admin@teammates.tmt",
                        "Prof Adrian Cole",
                        "Riverview School of Business",
                        "adrian.cole@riverview.edu",
                        null,
                        LinksUtil.getAdminHomePageUrl()));

        verifyEmailContent(actual.htmlContent(), "/adminNewAccountVerificationRequestAlertEmailWithNoComments.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationCreatedAcknowledgementEmail_withComments_returnsAcknowledgementEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationCreatedAcknowledgementEmail(
                new AccountVerificationCreatedAcknowledgementEmailContext(
                        "maya.bennett@westhaven.edu",
                        "Dr Maya Bennett",
                        "Westhaven College",
                        "I will be using TEAMMATES for peer evaluation in my communication studies classes."));

        verifyEmailContent(actual.htmlContent(),
                "/instructorNewAccountVerificationRequestAcknowledgementEmailWithComments.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderAccountVerificationCreatedAcknowledgementEmail_withoutComments_returnsAcknowledgementEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderAccountVerificationCreatedAcknowledgementEmail(
                new AccountVerificationCreatedAcknowledgementEmailContext(
                        "owen.frost@oakridge.edu",
                        "Dr Owen Frost",
                        "Oakridge University",
                        null));

        verifyEmailContent(actual.htmlContent(),
                "/instructorNewAccountVerificationRequestAcknowledgementEmailWithNoComments.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderStudentCourseJoinEmail_student_returnsJoinEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderStudentCourseJoinEmail(
                buildCourseContext(),
                new StudentCourseJoinEmailContext(
                        "student@email.tmt",
                        "Student Name",
                        LinksUtil.getStudentCourseJoinUrl(USER_ID, REG_KEY)));

        verifyEmailContent(actual.htmlContent(), "/studentCourseJoinEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderStudentCourseRejoinAfterUnlinkAccountEmail_student_returnsRejoinEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderStudentCourseRejoinAfterUnlinkAccountEmail(
                buildCourseContext(),
                new CourseRejoinAfterUnlinkEmailContext(
                        "student@email.tmt",
                        "Student Name",
                        LinksUtil.getStudentCourseJoinUrl(USER_ID, REG_KEY)));

        verifyEmailContent(actual.htmlContent(), "/studentCourseRejoinAfterUnlinkAccountEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderInstructorCourseJoinEmail_instructor_returnsJoinEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderInstructorCourseJoinEmail(
                buildCourseContext(),
                new InstructorCourseJoinEmailContext(
                        "instructor@email.tmt",
                        "Instructor Name",
                        LinksUtil.getInstructorCourseJoinUrl(USER_ID, REG_KEY),
                        "Joe Wilson",
                        "instructor-joe@gmail.com"));

        verifyEmailContent(actual.htmlContent(), "/instructorCourseJoinEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderInstructorCourseRejoinAfterUnlinkAccountEmail_instructor_returnsRejoinEmailBody()
            throws IOException {
        RenderedEmail actual = EmailRenderer.renderInstructorCourseRejoinAfterUnlinkAccountEmail(
                buildCourseContext(),
                new CourseRejoinAfterUnlinkEmailContext(
                        "instructor@email.tmt",
                        "Instructor Name",
                        LinksUtil.getInstructorCourseJoinUrl(USER_ID, REG_KEY)));

        verifyEmailContent(actual.htmlContent(), "/instructorCourseRejoinAfterUnlinkAccountEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_studentGrantedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionGivenStudent.html",
                buildDeadlineExtensionContext(
                        "student2@course1.tmt",
                        "student2 In Course1",
                        false,
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        EmailType.DEADLINE_EXTENSION_GRANTED));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_studentUpdatedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionUpdatedStudent.html",
                buildDeadlineExtensionContext(
                        "student2@course1.tmt",
                        "student2 In Course1",
                        false,
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        EmailType.DEADLINE_EXTENSION_UPDATED));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_studentRevokedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionRevokedStudent.html",
                buildDeadlineExtensionContext(
                        "student2@course1.tmt",
                        "student2 In Course1",
                        false,
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, USER_ID, REG_KEY),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        EmailType.DEADLINE_EXTENSION_REVOKED));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_instructorGrantedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionGivenInstructor.html",
                buildDeadlineExtensionContext(
                        "instructor2@course1.tmt",
                        "Instructor2 Course1",
                        true,
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        EmailType.DEADLINE_EXTENSION_GRANTED));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_instructorUpdatedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionUpdatedInstructor.html",
                buildDeadlineExtensionContext(
                        "instructor2@course1.tmt",
                        "Instructor2 Course1",
                        true,
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        EmailType.DEADLINE_EXTENSION_UPDATED));
    }

    @Test
    public void renderDeadlineExtensionUpdateEmail_instructorRevokedDeadline_returnsDeadlineExtensionEmailBody()
            throws IOException {
        verifyDeadlineExtensionEmail("/deadlineExtensionRevokedInstructor.html",
                buildDeadlineExtensionContext(
                        "instructor2@course1.tmt",
                        "Instructor2 Course1",
                        true,
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID),
                        Instant.parse("2027-04-30T23:00:00Z"),
                        Instant.parse("2027-04-30T21:59:00Z"),
                        EmailType.DEADLINE_EXTENSION_REVOKED));
    }

    private static void verifyEmailContent(String actual, String expectedEmailContentFilePathname)
            throws IOException {
        EmailChecker.verifyEmailContent(actual, expectedEmailContentFilePathname);
    }

    private static void verifyDeadlineExtensionEmail(
            String expectedEmailContentFilePathname, DeadlineExtensionUpdateEmailContext context) throws IOException {
        RenderedEmail actual = EmailRenderer.renderDeadlineExtensionUpdateEmail(buildFeedbackSessionContext(), context);
        verifyEmailContent(actual.htmlContent(), expectedEmailContentFilePathname);
        assertFalse(actual.htmlContent().contains("${"));
    }

    private static FeedbackSessionEmailContext buildFeedbackSessionContext() {
        return new FeedbackSessionEmailContext(
                FEEDBACK_SESSION_ID,
                "idOfTypicalCourse1",
                "Typical Course 1 with 2 Evals",
                "Africa/Johannesburg",
                "First feedback session",
                "Please please fill in the following questions.",
                List.of(
                        new EmailContact("Instructor Not Yet Joined Course 1",
                                "instructorNotYetJoinedCourse1@email.tmt"),
                        new EmailContact("Instructor1 Course1", "instructor1@course1.tmt"),
                        new EmailContact("Instructor3 Course1", "instructor3@course1.tmt")));
    }

    private static CourseEmailContext buildCourseContext() {
        return new CourseEmailContext(
                "idOfTypicalCourse1",
                "Course Name",
                List.of(
                        new EmailContact("Instructor Not Yet Joined Course 1",
                                "instructorNotYetJoinedCourse1@email.tmt"),
                        new EmailContact("Instructor1 Course1", "instructor1@course1.tmt"),
                        new EmailContact("Instructor3 Course1", "instructor3@course1.tmt")));
    }

    private static DeadlineExtensionUpdateEmailContext buildDeadlineExtensionContext(
            String recipientEmailAddress, String recipientName, boolean isInstructor, String submitUrl,
            Instant oldEndTime, Instant newEndTime, EmailType emailType) {
        return new DeadlineExtensionUpdateEmailContext(
                recipientEmailAddress,
                recipientName,
                isInstructor,
                submitUrl,
                oldEndTime,
                newEndTime,
                emailType);
    }

    private static FeedbackSessionSummaryEmailContext buildFeedbackSessionSummaryContext(
            boolean isInstructor, boolean isYetToJoinCourse, List<SessionAccessLink> sessionLinks) {
        return new FeedbackSessionSummaryEmailContext(
                isInstructor ? "instructor@email.tmt" : "student@email.tmt",
                isInstructor ? "Instructor Name" : "Student Name",
                "CS101",
                "Software Engineering",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")),
                isInstructor,
                isYetToJoinCourse,
                isInstructor
                        ? LinksUtil.getInstructorCourseJoinUrl(USER_ID, REG_KEY)
                        : LinksUtil.getStudentCourseJoinUrl(USER_ID, REG_KEY),
                List.of(new CourseSessionLinks("CS101", "Software Engineering", "Africa/Johannesburg", sessionLinks)));
    }

}
