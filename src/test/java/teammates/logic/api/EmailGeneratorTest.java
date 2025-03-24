package teammates.logic.api;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
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
import teammates.storage.sqlentity.Account;
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

        ______TS("feedback session opened emails");

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionOpenedEmails(session);
        // 5 instructors, 6 students, and 3 co-owner instructors to be notified
        assertEquals(14, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENED.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionOpenedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionOpenedEmailCopyToInstructor.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionOpenedEmailForInstructor.html");

        ______TS("feedback session reminders");

        emails = emailGenerator.generateFeedbackSessionReminderEmails(session, students, instructors,
                instructorToNotify);
        // (5 instructors, 6 students reminded) and (1 instructor to be notified)
        assertEquals(12, emails.size());

        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        // Verify the student reminder email
        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionReminderEmailForStudent.html");
        // Verify the student email copy send to the instructor
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionReminderEmailCopyToInstructor.html");
        // Verify the instructor reminder email
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionReminderEmailForInstructor.html");

        ______TS("feedback session closing alerts");

        emails = emailGenerator.generateFeedbackSessionClosingSoonEmails(session);
        // 5 instructors, 6 students, and 3 co-owner in session
        // 2 instructors and 3 students with deadline extensions do not need to be notified
        // 3 instructors, 3 students, and 3 co-owner to be notified
        assertEquals(9, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSING_SOON.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionClosingSoonEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionClosingSoonEmailCopyToInstructor.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionClosingSoonEmailForInstructor.html");

        ______TS("feedback session closed alerts for co-owners");

        emails = emailGenerator.generateFeedbackSessionClosedEmails(session);
        List<InstructorAttributes> coOwners = instructorsLogic.getCoOwnersForCourse(course.getId());
        assertEquals(coOwners.size(), emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        // this instructor email has been given co-owner privileges in the test file and has joined
        InstructorAttributes coOwnerJoined =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");

        assertTrue(coOwnerJoined.hasCoownerPrivileges());

        verifyEmailReceivedCorrectly(emails, coOwnerJoined.getEmail(), subject, "/sessionClosedEmailForCoOwner.html");

        ______TS("feedback session opening soon alerts for co-owners");

        emails = emailGenerator.generateFeedbackSessionOpeningSoonEmails(session);
        assertEquals(coOwners.size(), emails.size());

        subject = String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), course.getName(),
                session.getFeedbackSessionName());

        // this instructor email has been given co-owner privileges in the test file but has not joined
        InstructorAttributes coOwnerNotJoined =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructorNotYetJoinedCourse1@email.tmt");

        assertTrue(coOwnerNotJoined.hasCoownerPrivileges());

        verifyEmailReceivedCorrectly(emails, coOwnerNotJoined.getEmail(), subject,
                "/sessionOpeningSoonEmailForCoOwnerNotJoined.html");

        assertTrue(coOwnerJoined.hasCoownerPrivileges());

        verifyEmailReceivedCorrectly(emails, coOwnerJoined.getEmail(), subject,
                "/sessionOpeningSoonEmailForCoOwnerJoined.html");

        ______TS("feedback session published alerts");

        emails = emailGenerator.generateFeedbackSessionPublishedEmails(session);
        // 5 instructors, 6 students, and 3 co-owner instructors to be notified
        assertEquals(14, emails.size());

        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionPublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionPublishedEmailCopyToInstructor.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), subject, "/sessionPublishedEmailForInstructor.html");

        ______TS("feedback session unpublished alerts");

        emails = emailGenerator.generateFeedbackSessionUnpublishedEmails(session);
        // 5 instructors, 6 students, and 3 co-owner instructors to be notified
        assertEquals(14, emails.size());

        subject = String.format(EmailType.FEEDBACK_UNPUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject, "/sessionUnpublishedEmailForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionUnpublishedEmailCopyToInstructor.html");
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

        emails = emailGenerator.generateFeedbackSessionOpenedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionClosingSoonEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionPublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

        emails = emailGenerator.generateFeedbackSessionUnpublishedEmails(notAnswerableSession);
        assertTrue(emails.isEmpty());

    }

    @Test
    public void testGenerateFeedbackSessionEmails_testUsersWithDeadlineExtensions() throws Exception {
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        DeadlineExtensionAttributes student2 = dataBundle.deadlineExtensions.get("student2InCourse1Session1");
        DeadlineExtensionAttributes student4 = dataBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes student5 = dataBundle.deadlineExtensions.get("student5InCourse1Session1");
        DeadlineExtensionAttributes instructor2 = dataBundle.deadlineExtensions.get("instructor2InCourse1Session1");
        DeadlineExtensionAttributes instructor3 = dataBundle.deadlineExtensions.get("instructor3InCourse1Session1");

        List<DeadlineExtensionAttributes> deadlineExtensions =
                List.of(student2, student4, student5, instructor2, instructor3);

        ______TS("Feedback session closing alerts for users with deadline extensions");

        List<EmailWrapper> emails =
                emailGenerator.generateFeedbackSessionClosingWithExtensionEmails(session, deadlineExtensions);

        assertEquals(deadlineExtensions.size(), emails.size());

        String subject = String.format(EmailType.FEEDBACK_CLOSING_SOON.getSubject(),
                                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student2.getUserEmail(),
                subject, "/sessionClosingSoonEmailForStudentWithExtension.html");
        verifyEmailReceivedCorrectly(emails, instructor2.getUserEmail(),
                subject, "/sessionClosingSoonEmailForInstructorWithExtension.html");

        ______TS("Deadline extension given to student");

        Instant originalEndTime = session.getEndTime();
        Instant newEndTime = TimeHelper.parseInstant("2027-04-30T23:00:00Z");
        emails = emailGenerator.generateDeadlineGrantedEmails(course, session,
                Map.of(student2.getUserEmail(), newEndTime), false);
        subject = String.format(EmailType.DEADLINE_EXTENSION_GRANTED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), student2.getUserEmail(), subject, "/deadlineExtensionGivenStudent.html");

        ______TS("Deadline extension given to instructor");

        emails = emailGenerator.generateDeadlineGrantedEmails(course, session,
                Map.of(instructor2.getUserEmail(), newEndTime), true);
        subject = String.format(EmailType.DEADLINE_EXTENSION_GRANTED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), instructor2.getUserEmail(), subject, "/deadlineExtensionGivenInstructor.html");

        ______TS("Deadline extension updated for student");

        emails = emailGenerator.generateDeadlineUpdatedEmails(course, session,
                Map.of(student2.getUserEmail(), newEndTime), Map.of(student2.getUserEmail(), originalEndTime), false);
        subject = String.format(EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), student2.getUserEmail(), subject, "/deadlineExtensionUpdatedStudent.html");

        ______TS("Deadline extension updated for instructor");

        emails = emailGenerator.generateDeadlineUpdatedEmails(course, session,
                Map.of(instructor2.getUserEmail(), newEndTime), Map.of(instructor2.getUserEmail(), originalEndTime), true);
        subject = String.format(EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), instructor2.getUserEmail(), subject, "/deadlineExtensionUpdatedInstructor.html");

        ______TS("Deadline extension revoked for student");

        emails = emailGenerator.generateDeadlineRevokedEmails(course, session,
                Map.of(student2.getUserEmail(), newEndTime), false);
        subject = String.format(EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), student2.getUserEmail(), subject, "/deadlineExtensionRevokedStudent.html");

        ______TS("Deadline extension revoked for instructor");

        emails = emailGenerator.generateDeadlineRevokedEmails(course, session,
                Map.of(instructor2.getUserEmail(), newEndTime), true);
        subject = String.format(EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmail(emails.get(0), instructor2.getUserEmail(), subject, "/deadlineExtensionRevokedInstructor.html");
    }

    @Test
    public void testGenerateFeedbackSessionEmails_testSanitization() throws Exception {

        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("Normal feedback session name",
                                                                       "idOfTestingSanitizationCourse");
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "normal@sanitization.tmt");
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@sanitization.tmt");

        ______TS("feedback session opened emails: sanitization required");

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionOpenedEmails(session);

        assertEquals(2, emails.size());

        String subject = String.format(EmailType.FEEDBACK_OPENED.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject,
                "/sessionOpenedEmailTestingSanitizationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionOpenedEmailTestingSanitizationCopyToInstructor.html");

        ______TS("feedback session closing alerts: sanitization required");

        emails = emailGenerator.generateFeedbackSessionClosingSoonEmails(session);
        assertEquals(2, emails.size());

        subject = String.format(EmailType.FEEDBACK_CLOSING_SOON.getSubject(),
                course.getName(), session.getFeedbackSessionName());

        verifyEmailReceivedCorrectly(emails, student1.getEmail(), subject,
                "/sessionClosingSoonEmailTestingSanitizationForStudent.html");
        verifyEmailReceivedCorrectly(emails, instructor1.getEmail(), EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX + subject,
                "/sessionClosingSoonEmailTestingSanitizationCopyToInstructor.html");

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

        Account inviter = new Account("otherGoogleId", "Joe Wilson", "instructor-joe@gmail.com");

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
        Account sqlInviter = new Account(inviter.getGoogleId(), inviter.getName(), inviter.getEmail());
        CourseAttributes course = coursesLogic.getCourse("idOfTestingSanitizationCourse");
        email = emailGenerator.generateInstructorCourseJoinEmail(sqlInviter, instructor1, course);
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
        boolean hasReceivedEmailCorrectly = false;
        for (EmailWrapper email : actualEmails) {
            if (email.getRecipient().equals(recipient) && email.getSubject().equals(subject)) {
                verifyEmail(email, recipient, subject, emailContentFilePath);
                hasReceivedEmailCorrectly = true;
                break;
            }
        }
        assertTrue(hasReceivedEmailCorrectly);
    }
}
