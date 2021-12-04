package teammates.logic.api;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.ErrorLogEntry;
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
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailGenerator}.
 */
public class EmailGeneratorTest extends BaseLogicTest {

    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    private final EmailGenerator emailGenerator = EmailGenerator.inst();

    @Override
    public void prepareTestData() {
        dataBundle = loadDataBundle("/EmailGeneratorTest.json");

        FeedbackSessionAttributes session1InCourse3 = dataBundle.feedbackSessions.get("session1InCourse3");
        FeedbackSessionAttributes session2InCourse3 = dataBundle.feedbackSessions.get("session2InCourse3");
        FeedbackSessionAttributes session1InCourse4 = dataBundle.feedbackSessions.get("session1InCourse4");
        FeedbackSessionAttributes session2InCourse4 = dataBundle.feedbackSessions.get("session2InCourse4");
        // opened and unpublished.
        session1InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-20));
        dataBundle.feedbackSessions.put("session1InCourse3", session1InCourse3);

        // closed and unpublished
        session2InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session2InCourse3.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2InCourse3.setDeletedTime(null);
        dataBundle.feedbackSessions.put("session2InCourse3", session2InCourse3);

        // opened and published.
        session1InCourse4.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session1InCourse4.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session1InCourse4", session1InCourse4);

        // closed and published
        session2InCourse4.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-18));
        session2InCourse4.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2InCourse4.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session2InCourse4", session2InCourse4);

        removeAndRestoreDataBundle(dataBundle);
    }

    @Test
    public void testGenerateSessionLinksRecoveryEmail() throws Exception {

        ______TS("invalid email address");

        EmailWrapper email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                "non-existing-student");
        String subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, "non-existing-student", subject,
                "/sessionLinksRecoveryNonExistingStudentEmail.html");

        ______TS("no sessions found");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse1.getEmail());
        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse1.getEmail(), subject,
                "/sessionLinksRecoveryNoSessionsFoundEmail.html");

        ______TS("Typical case: found opened or closed but unpublished Sessions");

        StudentAttributes student1InCourse3 = dataBundle.students.get("student1InCourse3");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse3.getEmail());

        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse3.getEmail(), subject,
                "/sessionLinksRecoveryOpenedOrClosedButUnpublishedSessions.html");

        ______TS("Typical case: found opened or closed and  published Sessions");

        StudentAttributes student1InCourse4 = dataBundle.students.get("student1InCourse4");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse4.getEmail());

        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse4.getEmail(), subject,
                "/sessionLinksRecoveryOpenedOrClosedAndpublishedSessions.html");
    }

    @Test
    public void testGenerateFeedbackSessionEmails() throws Exception {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());

        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "student1InCourse1@gmail.tmt");
        StudentAttributes unregisteredStudent = studentsLogic.getStudentForEmail("idOfTypicalCourse1",
                "student1UnregisteredInCourse1@gmail.tmt");

        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");

        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        InstructorAttributes instructorToNotify = instructorsLogic.getInstructorForGoogleId(session.getCourseId(),
                instructor1.getGoogleId());

        ______TS("feedback session opening emails");

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionOpeningEmails(session);
        assertEquals(11, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionOpeningEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionOpeningEmailForInstructor.html");

        ______TS("feedback session reminders");

        emails = emailGenerator.generateFeedbackSessionReminderEmails(session, students, instructors,
                instructorToNotify);
        // (5 instructors, 6 students reminded) and (1 instructor to be notified)
        assertEquals(12, emails.size());

        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        String lineInEmailCopyToInstructor = "The email below has been sent to students of course:";
        // Verify the student reminder email
        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionReminderEmailForStudent.html");
        // Verify the Student email copy send to the instructor
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject,
                "/sessionReminderEmailCopyToInstructor.html", lineInEmailCopyToInstructor);
        // Verify the instructor reminder email
        String lineInEmailToInstructor =
                "/web/instructor/sessions/submission?courseid=idOfTypicalCourse1&fsname=First%20feedback%20session";
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject,
                "/sessionReminderEmailForInstructor.html", lineInEmailToInstructor);

        InstructorAttributes instructorNotJoinedYet = instructorsLogic.getInstructorForEmail(
                "idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt");
        String instructorReminderToJoinLine = "Note that you will need to join the course as an instructor";

        // Verify that unregistered instructor gets reminder to join course
        verifyEmailReceivedCorrectly(emails, instructorNotJoinedYet.getEmail(), subject,
                "/sessionReminderEmailForInstructorNotJoinedYet.html", instructorReminderToJoinLine);

        ______TS("feedback session closing alerts");

        emails = emailGenerator.generateFeedbackSessionClosingEmails(session);
        assertEquals(8, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        // student1 has completed the feedback session and closing alert is only sent for those who are
        // yet to complete, so we resort to student5
        StudentAttributes student5 = studentsLogic.getStudentForEmail(course.getId(), "student5InCourse1@gmail.tmt");

        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student1.getEmail())) {
                fail("student1 has completed the session and are not supposed to receive email");
            }
        }
        verifyEmailReceivedCorrectly(emails, student5.getEmail(), subject, "/sessionClosingEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionClosingEmailForInstructor.html");

        ______TS("feedback session closed alerts");

        emails = emailGenerator.generateFeedbackSessionClosedEmails(session);
        assertEquals(8, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionClosedEmailForInstructor.html");

        ______TS("feedback session opening soon alerts for co-owners");

        emails = emailGenerator.generateFeedbackSessionOpeningSoonEmails(session);
        List<InstructorAttributes> coOwners = instructorsLogic.getCoOwnersForCourse(course.getId());
        assertNotEquals(coOwners.size(), 0);
        assertEquals(coOwners.size(), emails.size());

        subject = String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), course.getName(),
                session.getFeedbackSessionName());

        // this instructor email has been given co-owner privileges in the test file but has not joined
        InstructorAttributes coOwnerNotJoined =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructorNotYetJoinedCourse1@email.tmt");

        assertTrue(coOwnerNotJoined.hasCoownerPrivileges());

        verifyEmailReceivedCorrectly(emails, coOwnerNotJoined.getEmail(), subject,
                "/sessionOpeningSoonEmailForCoOwnerNotJoined.html");

        // this instructor email has been given co-owner privileges in the test file and has joined
        InstructorAttributes coOwnerJoined =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");

        assertTrue(coOwnerJoined.hasCoownerPrivileges());

        verifyEmailReceivedCorrectly(emails, coOwnerJoined.getEmail(), subject,
                "/sessionOpeningSoonEmailForCoOwnerJoined.html");

        ______TS("feedback session published alerts");

        emails = emailGenerator.generateFeedbackSessionPublishedEmails(session);
        assertEquals(11, emails.size());

        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionPublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionPublishedEmailForInstructor.html");

        ______TS("feedback session unpublished alerts");

        emails = emailGenerator.generateFeedbackSessionUnpublishedEmails(session);
        assertEquals(11, emails.size());

        subject = String.format(EmailType.FEEDBACK_UNPUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionUnpublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionUnpublishedEmailForInstructor.html");

        ______TS("send summary of all feedback sessions of course email to new student. "
                + "Edited student has joined the course");

        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), student1.getEmail(), EmailType.STUDENT_EMAIL_CHANGED);
        subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student1.getEmail(), subject, "/summaryOfFeedbackSessionsOfCourseEmailForStudent.html");

        ______TS("send summary of all feedback sessions of course email to new student. "
                + "Edited student has not joined the course");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), unregisteredStudent.getEmail(),
                EmailType.STUDENT_EMAIL_CHANGED);
        subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, unregisteredStudent.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForUnregisteredStudent.html");

        ______TS("send summary of all regenerated feedback session links of course email to student. "
                + "Student has joined the course");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), student1.getEmail(), EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student1.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForRegeneratedStudent.html");

        ______TS("send summary of all regenerated feedback session links of course email to student. "
                + "Student has not joined the course");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), unregisteredStudent.getEmail(), EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, unregisteredStudent.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForRegeneratedUnregisteredStudent.html");

        ______TS("send summary of all regenerated feedback session links of course email to instructor. "
                + "Instructor has joined the course");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), instructor1.getEmail(), EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, instructor1.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForRegeneratedInstructor.html");

        ______TS("send summary of all regenerated feedback session links of course email to instructor. "
                + "Instructor has not joined the course");

        InstructorAttributes unregisteredInstructor = instructorsLogic.getInstructorForEmail("idOfTypicalCourse1",
                "instructorNotYetJoinedCourse1@email.tmt");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), unregisteredInstructor.getEmail(), EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, unregisteredInstructor.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForRegeneratedUnregisteredInstructor.html");

        ______TS("no email alerts sent for sessions not answerable/viewable for students");

        FeedbackSessionAttributes notAnswerableSession =
                fsLogic.getFeedbackSession("Not answerable feedback session", "idOfTypicalCourse2");

        emails = emailGenerator.generateFeedbackSessionOpeningEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionClosingEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionClosedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionPublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionUnpublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

    }

    @Test
    public void testGenerateFeedbackSessionEmails_testSanitization() throws Exception {

        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("Normal feedback session name",
                                                                       "idOfTestingSanitizationCourse");
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "normal@sanitization.tmt");
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@sanitization.tmt");

        ______TS("feedback session opening emails: sanitization required");

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionOpeningEmails(session);

        assertEquals(2, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject,
                "/sessionOpeningEmailTestingSanitzationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject,
                "/sessionOpeningEmailTestingSanitizationForInstructor.html");

        ______TS("feedback session closed alerts: sanitization required");

        emails = emailGenerator.generateFeedbackSessionClosedEmails(session);
        assertEquals(2, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject,
                "/sessionClosedEmailTestingSanitizationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject,
                "/sessionClosedEmailTestingSanitizationForInstructor.html");

        ______TS("feedback sessions summary of course email: sanitization required");

        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), student1.getEmail(), EmailType.STUDENT_EMAIL_CHANGED);
        subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());
        verifyEmail(email, student1.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailTestingSanitizationForStudent.html");

    }

    @Test
    public void testGenerateInstructorJoinEmail() throws Exception {

        ______TS("instructor new account email");

        String instructorEmail = "instructor@email.tmt";
        String instructorName = "Instr";
        String regkey = StringHelper.encrypt("skxxxxxxxxxks");

        InstructorAttributes instructor = InstructorAttributes
                .builder("courseId", instructorEmail)
                .withGoogleId("googleId")
                .withName("Instructor Name")
                .build();
        instructor.setKey(regkey);

        AccountAttributes inviter = AccountAttributes.builder("otherGoogleId")
                .withEmail("instructor-joe@gmail.com")
                .withName("Joe Wilson")
                .build();

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(regkey)
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();

        EmailWrapper email = emailGenerator
                .generateNewInstructorAccountJoinEmail(instructorEmail, instructorName, joinLink);
        String subject = String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), instructorName);

        verifyEmail(email, instructorEmail, subject, "/instructorNewAccountEmail.html");
        assertEquals(email.getBcc(), Config.SUPPORT_EMAIL);

        ______TS("instructor course join email");

        CourseAttributes course = CourseAttributes
                .builder("course-id")
                .withName("Course Name")
                .withTimezone("UTC")
                .build();

        email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, instructor.getEmail(), subject, "/instructorCourseJoinEmail.html");

    }

    @Test
    public void testGenerateFeedbackSessionSummaryOfCourse_noSessionLinksFound() throws Exception {
        FeedbackSessionAttributes session =
                fsLogic.getFeedbackSession("Feedback session with no emails sent", "idOfTestingNoEmailsSentCourse");

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());

        StudentAttributes noLinksStudent = studentsLogic.getStudentForEmail(course.getId(), "student1@noemailssent.tmt");

        ______TS("send summary of all feedback sessions of course email to new student. "
                + "No feedback session opening or published emails have been sent");

        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), noLinksStudent.getEmail(), EmailType.STUDENT_EMAIL_CHANGED);
        String subject = String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, noLinksStudent.getEmail(), subject,
                    "/summaryOfFeedbackSessionsOfCourseEmailForNoLinksStudent.html");

        ______TS("send summary of all regenerated feedback session links of course email to student. "
                + "No feedback session opening or published emails have been sent");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), noLinksStudent.getEmail(), EmailType.STUDENT_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(), course.getName(), course.getId());

        verifyEmail(email, noLinksStudent.getEmail(), subject,
                    "/summaryOfFeedbackSessionsOfCourseEmailForNoLinksRegeneratedStudent.html");

        ______TS("send summary of all regenerated feedback session links of course email to instructor. "
                + "No feedback session opening or published emails have been sent");

        InstructorAttributes noLinksInstructor = instructorsLogic.getInstructorForEmail(
                course.getId(), "instructor1@noemailssent.tmt");

        email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                session.getCourseId(), noLinksInstructor.getEmail(), EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, noLinksInstructor.getEmail(), subject,
                "/summaryOfFeedbackSessionsOfCourseEmailForNoLinksRegeneratedInstructor.html");
    }

    @Test
    public void testGenerateInstructorJoinEmail_testSanitization() throws Exception {
        ______TS("instructor new account email: sanitization required");
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail("idOfTestingSanitizationCourse", "instructor1@sanitization.tmt");

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(instructor1.getKey())
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();

        EmailWrapper email = emailGenerator
                .generateNewInstructorAccountJoinEmail(instructor1.getEmail(), instructor1.getName(), joinLink);
        // InstructorAttributes sanitizes name before saving
        String subject = String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(),
                SanitizationHelper.sanitizeForHtml(instructor1.getName()));

        verifyEmail(email, instructor1.getEmail(), subject, "/instructorNewAccountEmailTestingSanitization.html");
        assertEquals(email.getBcc(), Config.SUPPORT_EMAIL);

        ______TS("instructor course join email: sanitization required");

        AccountAttributes inviter = dataBundle.accounts.get("instructor1OfTestingSanitizationCourse");
        CourseAttributes course = coursesLogic.getCourse("idOfTestingSanitizationCourse");
        email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructor1, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, instructor1.getEmail(), subject, "/instructorCourseJoinEmailTestingSanitization.html");

        ______TS("instructor course join email after Google ID reset");

        email = emailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor1, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, instructor1.getEmail(), subject,
                "/instructorCourseRejoinAfterGoogleIdResetEmail.html");

        ______TS("instructor course join email after Google ID reset (with institute name set)");

        email = emailGenerator
                .generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor1, course);
        subject = String.format(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, instructor1.getEmail(), subject,
                "/instructorCourseRejoinAfterGoogleIdResetEmailWithInstitute.html");
    }

    @Test
    public void testGenerateStudentCourseJoinEmail() throws Exception {

        ______TS("student course join email");

        CourseAttributes course = CourseAttributes
                .builder("idOfTypicalCourse1")
                .withName("Course Name")
                .withTimezone("UTC")
                .build();

        StudentAttributes student =
                StudentAttributes.builder("", "student@email.tmt")
                        .withName("Student Name")
                        .build();
        student.setKey(StringHelper.encrypt("skxxxxxxxxxks"));

        EmailWrapper email = emailGenerator.generateStudentCourseJoinEmail(course, student);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student.getEmail(), subject, "/studentCourseWithCoOwnersJoinEmail.html");

        ______TS("student course with co-owners join email after Google ID reset");

        email = emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                course.getName(), course.getId());

        verifyEmail(email, student.getEmail(), subject, "/studentCourseWithCoOwnersRejoinAfterGoogleIdResetEmail.html");

        ______TS("student course (without co-owners) join email");

        course = CourseAttributes.builder("course-id")
                .withName("Course Name")
                .withTimezone("UTC")
                .build();

        email = emailGenerator.generateStudentCourseJoinEmail(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student.getEmail(), subject, "/studentCourseWithoutCoOwnersJoinEmail.html");

        ______TS("student course (without-co-owners) join email after Google ID reset");

        email = emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                course.getName(), course.getId());

        verifyEmail(email, student.getEmail(), subject, "/studentCourseWithoutCoOwnersRejoinAfterGoogleIdResetEmail.html");
    }

    @Test
    public void testGenerateStudentCourseJoinEmail_testSanitization() throws Exception {

        ______TS("student course join email: sanitization required");

        CourseAttributes course = coursesLogic.getCourse("idOfTestingSanitizationCourse");
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "normal@sanitization.tmt");

        EmailWrapper email = emailGenerator.generateStudentCourseJoinEmail(course, student1);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());

        verifyEmail(email, student1.getEmail(), subject, "/studentCourseJoinEmailTestingSanitization.html");

        ______TS("student course join email after Google ID reset: sanitization required");

        email = emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student1);
        subject = String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, student1.getEmail(), subject,
                "/studentCourseRejoinAfterGoogleIdResetEmailTestingSanitization.html");
    }

    @Test
    public void testGenerateUserCourseRegisterEmail() throws Exception {

        ______TS("student course register email");

        CourseAttributes course = CourseAttributes
                .builder("idOfTypicalCourse1")
                .withName("Course Name")
                .withTimezone("UTC")
                .build();
        String name = "User Name";
        String emailAddress = "user@email.tmt";
        String googleId = "user.googleid";

        EmailWrapper email =
                emailGenerator.generateUserCourseRegisteredEmail(name, emailAddress, googleId, false, course);
        String subject = String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, emailAddress, subject, "/studentCourseRegisterEmail.html");

        ______TS("instructor course register email");

        email = emailGenerator.generateUserCourseRegisteredEmail(name, emailAddress, googleId, true, course);
        subject = String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                course.getName(), course.getId());

        verifyEmail(email, emailAddress, subject, "/instructorCourseRegisterEmail.html");

    }

    @Test
    public void testGenerateCompiledLogsEmail() throws Exception {
        List<ErrorLogEntry> errorLogs = Arrays.asList(
                new ErrorLogEntry("Typical log message", "ERROR", "123456"),
                new ErrorLogEntry("Log line <br> with line break <br> and also HTML br tag", "ERROR", "abcdef")
        );

        EmailWrapper email = emailGenerator.generateCompiledLogsEmail(errorLogs);

        String subject = String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION);

        verifyEmail(email, Config.SUPPORT_EMAIL, subject, "/severeLogsCompilationEmail.html");
    }

    private void verifyEmail(EmailWrapper email, String recipient, String subject, String emailContentFilePath)
            throws Exception {
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
            throws Exception {
        verifyEmailReceivedCorrectly(actualEmails, recipient, subject, emailContentFilePath, "");
    }

    private void verifyEmailReceivedCorrectly(
            List<EmailWrapper> actualEmails, String recipient, String subject,
            String emailContentFilePath, String containsString)
            throws Exception {
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
