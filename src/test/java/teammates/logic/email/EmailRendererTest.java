package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.EmailType;
import teammates.common.util.LinksUtil;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.InstructorCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
import teammates.logic.email.model.StudentCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailRenderer}.
 */
public class EmailRendererTest extends BaseTestCase {

    private static final UUID FEEDBACK_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String REG_KEY = "ABCDEF0123456789ABCDEF0123456789";

    @Test
    public void renderSessionLinksRecoveryEmail_courseSectionsExist_returnsRecoveryEmailBody() throws IOException {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of(
                        new RecoverableCourseLinks(
                                "CS101",
                                "Software Engineering",
                                List.of(
                                        new RecoverableSessionLink(
                                                "Midterm Feedback",
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
                        "maya.bennett@westhaven.edu",
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
                        "owen.frost@oakridge.edu",
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
                        LinksUtil.getStudentCourseJoinUrl(REG_KEY)));

        verifyEmailContent(actual.htmlContent(), "/studentCourseJoinEmail.html");
        assertFalse(actual.htmlContent().contains("${"));
    }

    @Test
    public void renderStudentCourseRejoinAfterUnlinkAccountEmail_student_returnsRejoinEmailBody() throws IOException {
        RenderedEmail actual = EmailRenderer.renderStudentCourseRejoinAfterUnlinkAccountEmail(
                buildCourseContext(),
                new StudentCourseRejoinAfterUnlinkEmailContext(
                        "student@email.tmt",
                        "Student Name",
                        LinksUtil.getStudentCourseJoinUrl(REG_KEY)));

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
                        LinksUtil.getInstructorCourseJoinUrl(REG_KEY),
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
                new InstructorCourseRejoinAfterUnlinkEmailContext(
                        "instructor@email.tmt",
                        "Instructor Name",
                        LinksUtil.getInstructorCourseJoinUrl(REG_KEY)));

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
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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
                        LinksUtil.getStudentSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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
                        LinksUtil.getInstructorSessionSubmitUrl(FEEDBACK_SESSION_ID, REG_KEY),
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

}
