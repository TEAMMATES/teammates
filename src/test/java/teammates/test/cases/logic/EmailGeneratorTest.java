package teammates.test.cases.logic;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogService.LogLevel;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.EmailChecker;
import teammates.test.driver.TestProperties;

/**
 * SUT: {@link EmailGenerator}.
 */
public class EmailGeneratorTest extends BaseLogicTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    /**
     * Reminder to disable GodMode and re-run the test.
     */
    @AfterSuite
    public static void remindUserToDisableGodModeIfRequired() {
        if (TestProperties.IS_GODMODE_ENABLED) {
            print("==========================================================");
            print("IMPORTANT: Remember to disable GodMode and rerun the test!");
            print("==========================================================");
        }
    }

    @Test
    public void testGenerateFeedbackSessionEmails() throws IOException {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());

        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "student1InCourse1@gmail.tmt");

        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");

        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        InstructorAttributes instructorToNotify = instructorsLogic.getInstructorForGoogleId(session.getCourseId(),
                instructor1.getGoogleId());

        ______TS("feedback session opening emails");

        List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);
        assertEquals(10, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject, "/sessionOpeningEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject, "/sessionOpeningEmailForInstructor.html");

        ______TS("feedback session reminders");

        emails = new EmailGenerator().generateFeedbackSessionReminderEmails(session, students, instructors,
                instructorToNotify);
        // (5 instructors, 5 students reminded) and (1 instructor to be notified)
        assertEquals(11, emails.size());

        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        String lineInEmailCopyToInstructor = "The email below has been sent to students of course:";
        // Verify the student reminder email
        verifyEmailReceivedCorrectly(emails, student1.email, subject, "/sessionReminderEmailForStudent.html");
        // Verify the Student email copy send to the instructor
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject,
                "/sessionReminderEmailCopyToInstructor.html", lineInEmailCopyToInstructor);
        // Verify the instructor reminder email
        String lineInEmailToInstructor =
                "/page/instructorFeedbackSubmissionEditPage?courseid=idOfTypicalCourse1&fsname=First+feedback+session";
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject,
                "/sessionReminderEmailForInstructor.html", lineInEmailToInstructor);

        ______TS("feedback session closing alerts");

        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(session);
        assertEquals(8, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        // student1 has completed the feedback session and closing alert is only sent for those who are
        // yet to complete, so we resort to student5
        StudentAttributes student5 = studentsLogic.getStudentForEmail(course.getId(), "student5InCourse1@gmail.tmt");

        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student1.email)) {
                fail("student1 has completed the session and are not supposed to receive email");
            }
        }
        verifyEmailReceivedCorrectly(emails, student5.email, subject, "/sessionClosingEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject, "/sessionClosingEmailForInstructor.html");

        ______TS("feedback session closed alerts");

        emails = new EmailGenerator().generateFeedbackSessionClosedEmails(session);
        assertEquals(10, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject, "/sessionClosedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject, "/sessionClosedEmailForInstructor.html");

        ______TS("feedback session published alerts");

        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(session);
        assertEquals(10, emails.size());

        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject, "/sessionPublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject, "/sessionPublishedEmailForInstructor.html");

        ______TS("feedback session unpublished alerts");

        emails = new EmailGenerator().generateFeedbackSessionUnpublishedEmails(session);
        assertEquals(10, emails.size());

        subject = String.format(EmailType.FEEDBACK_UNPUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject, "/sessionUnpublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject, "/sessionUnpublishedEmailForInstructor.html");

        ______TS("send summary of all feedback sessions of course email");

        EmailWrapper email = new EmailGenerator().generateFeedbackSessionSummaryOfCourse(session.getCourseId(), student1);
        subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student1.email, subject, "/summaryOfFeedbackSessionsOfCourseEmailForStudent.html");

        ______TS("feedback session submission email");

        Instant time = TimeHelper.parseInstant("2016-09-04 05:30 AM +0000");
        email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForStudent(session, student1, time);
        subject = String.format(EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject(), course.getName(),
                                session.getFeedbackSessionName());
        verifyEmail(email, student1.email, subject, "/sessionSubmissionConfirmationEmailPositiveTimeZone.html");

        setTimeZoneButMaintainLocalDate(session, ZoneId.of("Pacific/Marquesas"));
        email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session, instructor1, time);
        subject = String.format(EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject(), course.getName(),
                                session.getFeedbackSessionName());
        verifyEmail(email, instructor1.email, subject, "/sessionSubmissionConfirmationEmailNegativeTimeZone.html");

        setTimeZoneButMaintainLocalDate(session, ZoneId.of("UTC"));
        email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session, instructor1, time);
        subject = String.format(EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject(), course.getName(),
                                session.getFeedbackSessionName());
        verifyEmail(email, instructor1.email, subject, "/sessionSubmissionConfirmationEmailZeroTimeZone.html");

        ______TS("no email alerts sent for sessions not answerable/viewable for students");

        FeedbackSessionAttributes notAnswerableSession =
                fsLogic.getFeedbackSession("Not answerable feedback session", "idOfTypicalCourse2");

        emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = new EmailGenerator().generateFeedbackSessionClosedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = new EmailGenerator().generateFeedbackSessionUnpublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

    }

    @Test
    public void testGenerateFeedbackSessionEmails_testSanitization() throws IOException {

        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("Normal feedback session name",
                                                                       "idOfTestingSanitizationCourse");
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "normal@sanitization.tmt");
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@sanitization.tmt");

        ______TS("feedback session opening emails: sanitization required");

        List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);

        assertEquals(2, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject,
                "/sessionOpeningEmailTestingSanitzationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject,
                "/sessionOpeningEmailTestingSanitizationForInstructor.html");

        ______TS("feedback session closed alerts: sanitization required");

        emails = new EmailGenerator().generateFeedbackSessionClosedEmails(session);
        assertEquals(2, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.email, subject,
                "/sessionClosedEmailTestingSanitizationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.email, subject,
                "/sessionClosedEmailTestingSanitizationForInstructor.html");

        ______TS("feedback sessions summary of course email: sanitization required");

        EmailWrapper email = new EmailGenerator().generateFeedbackSessionSummaryOfCourse(session.getCourseId(), student1);
        subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());
        verifyEmail(email, student1.email, subject,
                "/summaryOfFeedbackSessionsOfCourseEmailTestingSanitizationForStudent.html");

        ______TS("feedback session submission email: sanitization required");

        Instant time = TimeHelper.parseInstant("2016-09-04 05:30 AM +0000");

        email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(session, instructor1, time);
        subject = String.format(EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject(), course.getName(),
                session.getFeedbackSessionName());
        verifyEmail(email, instructor1.email, subject, "/sessionSubmissionConfirmationEmailTestingSanitization.html");
    }

    @Test
    public void testGenerateInstructorJoinEmail() throws IOException {

        ______TS("instructor new account email");

        String instructorEmail = "instructor@email.tmt";
        String instructorName = "Instr";
        String regkey = "skxxxxxxxxxks";

        @SuppressWarnings("deprecation")
        InstructorAttributes instructor = InstructorAttributes
                .builder("googleId", "courseId", "Instructor Name", instructorEmail)
                .withKey(regkey)
                .build();

        AccountAttributes inviter = AccountAttributes.builder()
                .withEmail("instructor-joe@gmail.com")
                .withName("Joe Wilson")
                .build();

        String joinLink = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                .withRegistrationKey(StringHelper.encrypt(regkey))
                                .withInstructorInstitution("Test Institute")
                                .toAbsoluteString();

        EmailWrapper email = new EmailGenerator()
                .generateNewInstructorAccountJoinEmail(instructorEmail, instructorName, joinLink);
        String subject = String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), instructorName);

        verifyEmail(email, instructorEmail, subject, "/instructorNewAccountEmail.html");
        assertEquals(email.getBcc(), Config.SUPPORT_EMAIL);

        ______TS("instructor course join email");

        CourseAttributes course = CourseAttributes
                .builder("course-id", "Course Name", ZoneId.of("UTC"))
                .build();

        email = new EmailGenerator().generateInstructorCourseJoinEmail(inviter, instructor, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, instructor.email, subject, "/instructorCourseJoinEmail.html");

    }

    @Test
    public void testGenerateInstructorJoinEmail_testSanitization() throws IOException {
        ______TS("instructor new account email: sanitization required");
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail("idOfTestingSanitizationCourse", "instructor1@sanitization.tmt");

        String joinLink = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                .withRegistrationKey(StringHelper.encrypt(instructor1.key))
                .withInstructorInstitution("Test Institute")
                .toAbsoluteString();

        EmailWrapper email = new EmailGenerator()
                .generateNewInstructorAccountJoinEmail(instructor1.email, instructor1.name, joinLink);
        // InstructorAttributes sanitizes name before saving
        String subject = String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(),
                SanitizationHelper.sanitizeForHtml(instructor1.name));

        verifyEmail(email, instructor1.email, subject, "/instructorNewAccountEmailTestingSanitization.html");
        assertEquals(email.getBcc(), Config.SUPPORT_EMAIL);

        ______TS("instructor course join email: sanitization required");

        AccountAttributes inviter = dataBundle.accounts.get("instructor1OfTestingSanitizationCourse");

        CourseAttributes course = coursesLogic.getCourse("idOfTestingSanitizationCourse");

        email = new EmailGenerator().generateInstructorCourseJoinEmail(inviter, instructor1, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, instructor1.email, subject, "/instructorCourseJoinEmailTestingSanitization.html");
    }

    @Test
    public void testGenerateStudentCourseJoinEmail() throws IOException {

        ______TS("student course join email");

        CourseAttributes course = CourseAttributes
                .builder("idOfTypicalCourse1", "Course Name", ZoneId.of("UTC"))
                .build();

        StudentAttributes student = StudentAttributes
                .builder("", "Student Name", "student@email.tmt")
                .withKey("skxxxxxxxxxks")
                .build();

        EmailWrapper email = new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student.email, subject, "/studentCourseWithCoOwnersJoinEmail.html");

        ______TS("student course with co-owners join email after Google ID reset");

        email = new EmailGenerator().generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                course.getName(), course.getId());

        verifyEmail(email, student.email, subject, "/studentCourseWithCoOwnersRejoinAfterGoogleIdResetEmail.html");

        ______TS("student course (without co-owners) join email");

        course = CourseAttributes.builder("course-id", "Course Name", ZoneId.of("UTC")).build();

        email = new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student.email, subject, "/studentCourseWithoutCoOwnersJoinEmail.html");

        ______TS("student course (without-co-owners) join email after Google ID reset");

        email = new EmailGenerator().generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                course.getName(), course.getId());

        verifyEmail(email, student.email, subject, "/studentCourseWithoutCoOwnersRejoinAfterGoogleIdResetEmail.html");
    }

    @Test
    public void testGenerateStudentCourseJoinEmail_testSanitization() throws IOException {

        ______TS("student course join email: sanitization required");

        CourseAttributes course = coursesLogic.getCourse("idOfTestingSanitizationCourse");
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "normal@sanitization.tmt");

        EmailWrapper email = new EmailGenerator().generateStudentCourseJoinEmail(course, student1);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student1.email, subject, "/studentCourseJoinEmailTestingSanitization.html");

        ______TS("student course join email after Google ID reset: sanitization required");

        email = new EmailGenerator().generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student1);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, student1.email, subject, "/studentCourseRejoinAfterGoogleIdResetEmailTestingSanitization.html");
    }

    @Test
    public void testGenerateUserCourseRegisterEmail() throws IOException {

        ______TS("student course register email");

        CourseAttributes course = CourseAttributes
                .builder("idOfTypicalCourse1", "Course Name", ZoneId.of("UTC"))
                .build();
        String name = "User Name";
        String emailAddress = "user@email.tmt";
        String googleId = "user.googleid";

        EmailWrapper email =
                new EmailGenerator().generateUserCourseRegisteredEmail(name, emailAddress, googleId, false, course);
        String subject = String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, emailAddress, subject, "/studentCourseRegisterEmail.html");

        ______TS("instructor course register email");

        email = new EmailGenerator().generateUserCourseRegisteredEmail(name, emailAddress, googleId, true, course);
        subject = String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, emailAddress, subject, "/instructorCourseRegisterEmail.html");

    }

    @Test
    public void testGenerateCompiledLogsEmail() throws IOException {
        AppLogLine typicalLogLine = new AppLogLine();
        typicalLogLine.setLogLevel(LogLevel.ERROR);
        typicalLogLine.setLogMessage("Typical log message");

        AppLogLine logLineWithLineBreak = new AppLogLine();
        logLineWithLineBreak.setLogLevel(LogLevel.ERROR);
        logLineWithLineBreak.setLogMessage("Log line \n with line break <br> and also HTML br tag");

        EmailWrapper email = new EmailGenerator().generateCompiledLogsEmail(
                Arrays.asList(typicalLogLine, logLineWithLineBreak));

        String subject = String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(),
                                       Config.getAppVersion());

        verifyEmail(email, Config.SUPPORT_EMAIL, subject, "/severeLogsCompilationEmail.html");
    }

    @Test
    public void testGenerateAdminEmail() {
        String recipient = "recipient@email.com";
        String content = "Generic content";
        String subject = "Generic subject";
        EmailWrapper email = new EmailGenerator().generateAdminEmail(content, subject, recipient);

        // Do not use verify email since the content is not based on any template
        assertEquals(recipient, email.getRecipient());
        assertEquals(subject, email.getSubject());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        assertEquals(content, email.getContent());
    }

    private void setTimeZoneButMaintainLocalDate(FeedbackSessionAttributes session, ZoneId newTimeZone) {
        LocalDateTime localStart = session.getStartTimeLocal();
        LocalDateTime localEnd = session.getEndTimeLocal();
        LocalDateTime localSessionVisibleFrom = session.getSessionVisibleFromTimeLocal();
        LocalDateTime localResultsVisibleFrom = session.getResultsVisibleFromTimeLocal();
        session.setTimeZone(newTimeZone);
        session.setStartTime(TimeHelper.convertLocalDateTimeToInstant(localStart, newTimeZone));
        session.setEndTime(TimeHelper.convertLocalDateTimeToInstant(localEnd, newTimeZone));
        session.setSessionVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(localSessionVisibleFrom, newTimeZone));
        session.setResultsVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(localResultsVisibleFrom, newTimeZone));
    }

    private void verifyEmail(EmailWrapper email, String recipient, String subject, String emailContentFilePath)
            throws IOException {
        // check recipient
        assertEquals(recipient, email.getRecipient());

        // check subject
        assertEquals(subject, email.getSubject());

        // check sender name
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());

        // check sender email
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());

        // check reply to address
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());

        String emailContent = email.getContent();

        // check email body for expected content
        EmailChecker.verifyEmailContent(emailContent, emailContentFilePath);

        // check email body for no left placeholders
        assertFalse(emailContent.contains("${"));
    }

    private void verifyEmailReceivedCorrectly(
            List<EmailWrapper> actualEmails, String recipient, String subject, String emailContentFilePath)
            throws IOException {
        verifyEmailReceivedCorrectly(actualEmails, recipient, subject, emailContentFilePath, "");
    }

    private void verifyEmailReceivedCorrectly(
            List<EmailWrapper> actualEmails, String recipient, String subject,
            String emailContentFilePath, String containsString)
            throws IOException {
        boolean hasReceivedEmailCorrectly = false;
        for (EmailWrapper email : actualEmails) {
            if (email.getRecipient().equals(recipient) && email.getContent().contains(containsString)) {
                verifyEmail(email, recipient, subject, emailContentFilePath);
                hasReceivedEmailCorrectly = true;
            }
        }
        assertTrue(hasReceivedEmailCorrectly);
    }

}
