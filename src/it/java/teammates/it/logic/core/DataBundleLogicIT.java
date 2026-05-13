package teammates.it.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.DataBundleLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.FileHelper;

/**
 * SUT: {@link DataBundleLogic}.
 */
public class DataBundleLogicIT extends BaseTestCaseWithDatabaseAccess {

    private final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testCreateDataBundle_typicalValues_createdCorrectly() throws Exception {
        String pathToJsonFile = getTestDataFolder() + "/DataBundleLogicIT.json";
        String jsonString = FileHelper.readFile(pathToJsonFile);
        DataBundle dataBundle = DataBundleLogic.deserializeDataBundle(jsonString);

        ______TS("verify account requests deserialized correctly");

        AccountRequest actualAccountRequest = dataBundle.accountRequests.get("instructor1");
        AccountRequest expectedAccountRequest = new AccountRequest("instr1@teammates.tmt", "Instructor 1",
                "TEAMMATES Test Institute 1", AccountRequestStatus.REGISTERED, "These are some comments.");
        expectedAccountRequest.setId(actualAccountRequest.getId());
        expectedAccountRequest.setRegisteredAt(Instant.parse("2015-02-14T00:00:00Z"));
        expectedAccountRequest.setRegistrationKey(actualAccountRequest.getRegistrationKey());
        verifyEquals(expectedAccountRequest, actualAccountRequest);

        ______TS("verify accounts deserialized correctly");

        Account actualInstructorAccount = dataBundle.accounts.get("instructor1");
        Account expectedInstructorAccount = new Account("idOfInstructor1", "Instructor 1", "instr1@teammates.tmt");
        expectedInstructorAccount.setId(actualInstructorAccount.getId());
        verifyEquals(expectedInstructorAccount, actualInstructorAccount);
        assertTrue(actualInstructorAccount.getReadNotifications().size() == 1);
        assertTrue(List.of(dataBundle.readNotifications.get("notification1Instructor1"))
                .containsAll(actualInstructorAccount.getReadNotifications()));

        Account actualStudentAccount = dataBundle.accounts.get("student1");
        Account expectedStudentAccount = new Account("idOfStudent1", "Student 1", "student1@teammates.tmt");
        expectedStudentAccount.setId(actualStudentAccount.getId());
        verifyEquals(expectedStudentAccount, actualStudentAccount);
        assertTrue(actualStudentAccount.getReadNotifications().size() == 1);
        assertTrue(List.of(dataBundle.readNotifications.get("notification1Student1"))
                .containsAll(actualStudentAccount.getReadNotifications()));

        ______TS("verify notifications deserialized correctly");

        Notification actualNotification = dataBundle.notifications.get("notification1");
        Notification expectedNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        expectedNotification.setId(actualNotification.getId());
        verifyEquals(expectedNotification, actualNotification);

        ______TS("verify read notifications deserialized correctly");

        ReadNotification actualReadNotification = dataBundle.readNotifications.get("notification1Instructor1");
        ReadNotification expectedReadNotification = new ReadNotification();
        expectedInstructorAccount.addReadNotification(expectedReadNotification);
        expectedNotification.addReadNotification(expectedReadNotification);

        expectedNotification.setId(actualNotification.getId());
        verifyEquals(expectedReadNotification, actualReadNotification);

        ______TS("verify courses deserialized correctly");

        Course actualTypicalCourse = dataBundle.courses.get("typicalCourse");
        Course expectedTypicalCourse = new Course("typical-course-id", "Typical Course", "Africa/Johannesburg",
                "TEAMMATES Test Institute");
        verifyEquals(expectedTypicalCourse, actualTypicalCourse);

        ______TS("verify sections deserialized correctly");

        Section actualSection = dataBundle.sections.get("section1InTypicalCourse");
        Section expectedSection = new Section("Section 1");
        expectedTypicalCourse.addSection(expectedSection);
        expectedSection.setId(actualSection.getId());
        verifyEquals(expectedSection, actualSection);

        ______TS("verify teams deserialized correctly");

        Team actualTeam = dataBundle.teams.get("team1InTypicalCourse");
        Team expectedTeam = new Team("Team 1");
        expectedSection.addTeam(expectedTeam);
        expectedTeam.setId(actualTeam.getId());
        verifyEquals(expectedTeam, actualTeam);

        ______TS("verify instructors deserialized correctly");

        Instructor actualInstructor1 = dataBundle.instructors.get("instructor1OfTypicalCourse");
        InstructorPermissionRole coOwner = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges coOwnerPrivileges = new InstructorPrivileges(coOwner.getRoleName());
        Instructor expectedInstructor1 = new Instructor(actualTypicalCourse, "Instructor 1", "instr1@teammates.tmt",
                true, "Instructor", coOwner, coOwnerPrivileges);
        expectedInstructor1.setId(actualInstructor1.getId());
        expectedInstructor1.setRegKey(actualInstructor1.getRegKey());
        expectedInstructor1.setAccount(expectedInstructorAccount);
        verifyEquals(expectedInstructor1, actualInstructor1);

        Instructor actualInstructor2 = dataBundle.instructors.get("instructor2OfTypicalCourse");
        InstructorPermissionRole tutor = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR;
        InstructorPrivileges tutorPrivileges = new InstructorPrivileges(tutor.getRoleName());
        Instructor expectedInstructor2 = new Instructor(actualTypicalCourse, "Instructor 2", "instr2@teammates.tmt",
                true, "Instructor", tutor, tutorPrivileges);
        expectedInstructor2.setId(actualInstructor2.getId());
        expectedInstructor2.setRegKey(actualInstructor2.getRegKey());
        verifyEquals(expectedInstructor2, actualInstructor2);

        ______TS("verify students deserialized correctly");

        Student actualStudent1 = dataBundle.students.get("student1InTypicalCourse");
        Student expectedStudent1 = new Student(expectedTypicalCourse, "student1 In TypicalCourse",
                "student1@teammates.tmt", "comment for student1TypicalCourse");
        expectedStudent1.setAccount(expectedStudentAccount);
        expectedStudent1.setTeam(expectedTeam);
        expectedStudent1.setRegKey(actualStudent1.getRegKey());
        expectedStudent1.setId(actualStudent1.getId());
        verifyEquals(expectedStudent1, actualStudent1);

        Student actualStudent2 = dataBundle.students.get("student2InTypicalCourse");
        Student expectedStudent2 = new Student(expectedTypicalCourse, "student2 In TypicalCourse",
                "student2@teammates.tmt", "");
        expectedStudent2.setTeam(expectedTeam);
        expectedStudent2.setRegKey(actualStudent2.getRegKey());
        expectedStudent2.setId(actualStudent2.getId());
        verifyEquals(expectedStudent2, actualStudent2);

        ______TS("verify feedback sessions");

        FeedbackSession actualSession1 = dataBundle.feedbackSessions.get("session1InTypicalCourse");
        FeedbackSession expectedSession1 = new FeedbackSession("First feedback session",
                "instr1@teammates.tmt", "Please please fill in the following questions.",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"), Duration.ofMinutes(10),
                true, true);
        expectedSession1.setId(actualSession1.getId());
        expectedSession1.setOpenedEmailSent(actualSession1.isOpenedEmailSent());
        expectedSession1.setOpeningSoonEmailSent(actualSession1.isOpeningSoonEmailSent());
        expectedTypicalCourse.addFeedbackSession(expectedSession1);
        verifyEquals(expectedSession1, actualSession1);

        ______TS("verify feedback questions deserialized correctly");

        FeedbackQuestion actualQuestion1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestionDetails questionDetails1 =
                new FeedbackTextQuestionDetails("What is the best selling point of your product?");
        FeedbackQuestion expectedQuestion1 = FeedbackQuestion.makeQuestion(1,
                "This is a text question.", QuestionGiverType.STUDENTS, QuestionRecipientType.SELF,
                1, List.of(ViewerType.INSTRUCTORS), List.of(ViewerType.INSTRUCTORS),
                List.of(ViewerType.INSTRUCTORS), questionDetails1);
        expectedSession1.addFeedbackQuestion(expectedQuestion1);
        expectedQuestion1.setId(actualQuestion1.getId());
        verifyEquals(expectedQuestion1, actualQuestion1);

        ______TS("verify feedback responses deserialized correctly");
        FeedbackResponse actualResponse1 = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackResponseDetails responseDetails1 = new FeedbackTextResponseDetails("Student 1 self feedback.");
        FeedbackResponse expectedResponse1 = FeedbackResponse.makeResponse("student1@teammates.tmt",
                expectedSection, "student1@teammates.tmt", expectedSection, responseDetails1);
        actualQuestion1.addFeedbackResponse(expectedResponse1);
        expectedResponse1.setId(actualResponse1.getId());
        verifyEquals(expectedResponse1, actualResponse1);

        ______TS("verify feedback response comments deserialized correctly");
        FeedbackResponseComment actualComment1 = dataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment expectedComment1 = new FeedbackResponseComment("instr1@teammates.tmt",
                "Instructor 1 comment to student 1 self feedback", false, false,
                new ArrayList<>(), new ArrayList<>(), "instr1@teammates.tmt");
        expectedResponse1.addFeedbackResponseComment(expectedComment1);
        expectedComment1.setId(actualComment1.getId());
        verifyEquals(expectedComment1, actualComment1);
    }

    @Test
    public void testPersistDataBundle_typicalValues_persistedToDbCorrectly() throws Exception {
        DataBundle dataBundle = loadDataBundle("/DataBundleLogicIT.json");
        dataBundleLogic.persistDataBundle(dataBundle);

        ______TS("verify notifications persisted correctly");
        Notification notification1 = dataBundle.notifications.get("notification1");

        verifyPresentInDatabase(notification1);

        ______TS("verify course persisted correctly");
        Course typicalCourse = dataBundle.courses.get("typicalCourse");

        verifyPresentInDatabase(typicalCourse);

        ______TS("verify feedback sessions persisted correctly");
        FeedbackSession session1InTypicalCourse = dataBundle.feedbackSessions.get("session1InTypicalCourse");

        verifyPresentInDatabase(session1InTypicalCourse);

        ______TS("verify accounts persisted correctly");
        Account instructor1Account = dataBundle.accounts.get("instructor1");
        Account student1Account = dataBundle.accounts.get("student1");

        verifyPresentInDatabase(instructor1Account);
        verifyPresentInDatabase(student1Account);

        // TODO: incomplete
    }

    @Test
    public void testRemoveDataBundle_typicalValues_removedCorrectly()
                throws InvalidParametersException {
        DataBundle dataBundle = loadDataBundle("/DataBundleLogicIT.json");
        dataBundleLogic.persistDataBundle(dataBundle);
        HibernateUtil.flushSession();

        ______TS("verify notifications persisted correctly");
        Notification notification1 = dataBundle.notifications.get("notification1");

        verifyPresentInDatabase(notification1);

        ______TS("verify course persisted correctly");
        Course typicalCourse = dataBundle.courses.get("typicalCourse");

        verifyPresentInDatabase(typicalCourse);

        ______TS("verify feedback session persisted correctly");
        FeedbackSession session1InTypicalCourse = dataBundle.feedbackSessions.get("session1InTypicalCourse");

        verifyPresentInDatabase(session1InTypicalCourse);

        ______TS("verify accounts persisted correctly");
        Account instructor1Account = dataBundle.accounts.get("instructor1");
        Account student1Account = dataBundle.accounts.get("student1");

        verifyPresentInDatabase(instructor1Account);
        verifyPresentInDatabase(student1Account);

        ______TS("verify account request persisted correctly");
        AccountRequest accountRequest = dataBundle.accountRequests.get("instructor1");

        verifyPresentInDatabase(accountRequest);

        dataBundleLogic.removeDataBundle(dataBundle);

        ______TS("verify notification removed correctly");

        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(notification1));

        ______TS("verify course removed correctly");

        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(typicalCourse));

        ______TS("verify feedback session removed correctly");

        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(session1InTypicalCourse));

        ______TS("verify feedback questions, responses, response comments and deadline extensions "
                + "related to session1InTypicalCourse are removed correctly");

        Set<FeedbackQuestion> fqs = session1InTypicalCourse.getFeedbackQuestions();
        Set<DeadlineExtension> des = session1InTypicalCourse.getDeadlineExtensions();
        List<FeedbackResponse> frs = new ArrayList<>();
        List<FeedbackResponseComment> frcs = new ArrayList<>();

        for (DeadlineExtension de : des) {
            assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(de));
        }

        for (FeedbackQuestion fq : fqs) {
            frs.addAll(fq.getFeedbackResponses());
            assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(fq));
        }

        for (FeedbackResponse fr : frs) {
            frcs.addAll(fr.getFeedbackResponseComments());
            assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(fr));
        }

        for (FeedbackResponseComment frc : frcs) {
            assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(frc));
        }

        ______TS("verify accounts removed correctly");

        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(instructor1Account));
        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(student1Account));

        ______TS("verify account request removed correctly");

        assertThrows(NullPointerException.class, () -> verifyPresentInDatabase(accountRequest));
    }

}
