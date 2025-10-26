package teammates.logic.core;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

import teammates.storage.api.CoursesDb;
import teammates.storage.api.NotificationsDb;
import teammates.storage.entity.AccountRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SUT: {@link DeletionService}.
 */
public class DeletionServiceTest extends BaseLogicTest{
    private final DeletionService deletionService = DeletionService.inst();

    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();
    private final AccountsLogic accountsLogic = AccountsLogic.inst();
    private final NotificationsDb notifDb = NotificationsDb.inst();
    private final NotificationsLogic notifLogic = NotificationsLogic.inst();
    private final Map<String, NotificationAttributes> typicalNotifications = getTypicalDataBundle().notifications;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
        instructorsLogic.deleteInstructorCascade("FSQTT.idOfTypicalCourse1", "instructor3@course1.tmt");
    }

    @Test
    public void testDeleteStudentsForGoogleIdCascade_typicalCase_shouldDoCascadeDeletion() {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");

        assertNotNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail()));
        assertNotNull(student1InCourse1.getGoogleId());

        // the student has response
        assertFalse(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        student1InCourse1.getCourse(), student1InCourse1.getEmail()).isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        student1InCourse1.getCourse(), student1InCourse1.getEmail()).isEmpty());

        deletionService.deleteStudentsForGoogleIdCascade(student1InCourse1.getGoogleId());

        // verify that the student is deleted
        assertNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail()));

        // his responses should also be deleted
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        student1InCourse1.getCourse(), student1InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        student1InCourse1.getCourse(), student1InCourse1.getEmail()).isEmpty());
    }

    @Test
    public void testDeleteStudentsForGoogleIdCascade_nonExistentGoogleId_shouldPass() {

        deletionService.deleteStudentsForGoogleIdCascade("not_exist");

        // other students are not affected
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertNotNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail()));
    }

    @Test
    public void testDeleteStudentsInCourseCascade_typicalCase_shouldDoCascadeDeletion() {
        var student1InCourse1 = dataBundle.students.get("student1InCourse1");
        var student2InCourse1 = dataBundle.students.get("student2InCourse1");

        var courseId = student1InCourse1.getCourse();

        // there are 5 students in the course initially
        assertEquals(5, studentsLogic.getStudentsForCourse(courseId).size());

        // student 1 of course 1 has given/received responses
        assertFalse(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        courseId, student1InCourse1.getEmail()).isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        courseId, student1InCourse1.getEmail()).isEmpty());

        // student 2 of course 1 has given/received responses
        assertFalse(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        courseId, student2InCourse1.getEmail()).isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        courseId, student2InCourse1.getEmail()).isEmpty());

        var deleteLimit = 2;
        deletionService.deleteStudentsInCourseCascade(courseId, deleteLimit);

        // 3 students remaining after deletion of 2 students
        assertEquals(3, studentsLogic.getStudentsForCourse(courseId).size());

        // course still exists
        assertNotNull(coursesLogic.getCourse(courseId));

        // responses to and from student 1 and 2 are deleted
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        courseId, student1InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        courseId, student1InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        courseId, student2InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        courseId, student2InCourse1.getEmail()).isEmpty());
    }

    @Test
    public void testDeleteStudents_byCourseId_shouldDeleteAllStudents() {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student1InArchivedCourse = dataBundle.students.get("student1InArchivedCourse");
        // the two are in different course
        assertNotEquals(student1InCourse1.getCourse(), student1InArchivedCourse.getCourse());

        assertNotNull(studentsLogic.getStudentForEmail(student1InArchivedCourse.getCourse(),
                student1InArchivedCourse.getEmail()));
        // there are students in the course
        assertFalse(studentsLogic.getStudentsForCourse(student1InCourse1.getCourse()).isEmpty());

        deletionService.deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId(student1InCourse1.getCourse())
                        .build());

        // students are deleted
        assertTrue(studentsLogic.getStudentsForCourse(student1InCourse1.getCourse()).isEmpty());
        // students in other courses are not affected
        assertNotNull(studentsLogic.getStudentForEmail(student1InArchivedCourse.getCourse(),
                student1InArchivedCourse.getEmail()));
    }

    @Test
    public void testDeleteStudentsInCourseCascade_nonExistCourse_shouldPass() {
        // large limit which is guaranteed to be bigger than the number of students in any course
        var deleteLimit = dataBundle.students.size();
        deletionService.deleteStudentsInCourseCascade("not_exist", deleteLimit);

        // other students are not affected
        var student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertNotNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail()));
    }

    @Test
    public void testDeleteCourseCascade() {

        ______TS("non-existent");

        deletionService.deleteCourseCascade("not_exist");

        ______TS("typical case");

        CourseAttributes course1OfInstructor = dataBundle.courses.get("typicalCourse1");
        StudentAttributes studentInCourse = dataBundle.students.get("student1InCourse1");

        // Ensure there are entities in the database under this course
        assertFalse(studentsLogic.getStudentsForCourse(course1OfInstructor.getId()).isEmpty());

        verifyPresentInDatabase(course1OfInstructor);
        verifyPresentInDatabase(studentInCourse);
        verifyPresentInDatabase(dataBundle.instructors.get("instructor1OfCourse1"));
        verifyPresentInDatabase(dataBundle.instructors.get("instructor3OfCourse1"));
        verifyPresentInDatabase(dataBundle.students.get("student1InCourse1"));
        verifyPresentInDatabase(dataBundle.students.get("student5InCourse1"));
        verifyPresentInDatabase(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyPresentInDatabase(dataBundle.feedbackSessions.get("session2InCourse1"));
        verifyPresentInDatabase(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        FeedbackResponseAttributes typicalResponse = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackQuestionAttributes typicalQuestion =
                fqLogic.getFeedbackQuestion(typicalResponse.getFeedbackSessionName(), typicalResponse.getCourseId(),
                        Integer.parseInt(typicalResponse.getFeedbackQuestionId()));
        typicalResponse = frLogic
                .getFeedbackResponse(typicalQuestion.getId(), typicalResponse.getGiver(), typicalResponse.getRecipient());
        verifyPresentInDatabase(typicalResponse);
        FeedbackResponseCommentAttributes typicalComment =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        typicalComment = frcLogic
                .getFeedbackResponseComment(typicalResponse.getId(),
                        typicalComment.getCommentGiver(), typicalComment.getCreatedAt());
        verifyPresentInDatabase(typicalComment);

        deletionService.deleteCourseCascade(course1OfInstructor.getId());

        // Ensure the course and related entities are deleted
        verifyAbsentInDatabase(course1OfInstructor);
        verifyAbsentInDatabase(studentInCourse);
        verifyAbsentInDatabase(dataBundle.instructors.get("instructor1OfCourse1"));
        verifyAbsentInDatabase(dataBundle.instructors.get("instructor3OfCourse1"));
        verifyAbsentInDatabase(dataBundle.students.get("student1InCourse1"));
        verifyAbsentInDatabase(dataBundle.students.get("student5InCourse1"));
        verifyAbsentInDatabase(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyAbsentInDatabase(dataBundle.feedbackSessions.get("session2InCourse1"));
        verifyAbsentInDatabase(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        verifyAbsentInDatabase(typicalQuestion);
        verifyAbsentInDatabase(typicalResponse);
        verifyAbsentInDatabase(typicalComment);

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> deletionService.deleteCourseCascade(null));
    }

    @Test
    public void testDeleteInstructorCascade() {

        String courseId = "idOfTypicalCourse1";
        String email = "instructor1@course1.tmt";

        ______TS("typical case: delete a non-existent instructor");

        deletionService.deleteInstructorCascade(courseId, "non-existent@course1.tmt");

        ______TS("typical case: delete an instructor for specific course");

        InstructorAttributes instructorDeleted = instructorsLogic.getInstructorForEmail(courseId, email);
        assertNotNull(instructorDeleted);
        // the instructors has some responses in course
        assertFalse(frLogic.getFeedbackResponsesFromGiverForCourse(courseId, email).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForReceiverForCourse(courseId, email).isEmpty());

        // The instructor should have selective deadlines.
        Set<FeedbackSessionAttributes> oldSessionsWithInstructor1Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getInstructorDeadlines()
                        .containsKey(email))
                .collect(Collectors.toSet());
        Map<FeedbackSessionAttributes, Integer> oldSessionsDeadlineCounts = oldSessionsWithInstructor1Deadlines
                .stream()
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getInstructorDeadlines().size()));
        assertEquals(2, oldSessionsWithInstructor1Deadlines.size());

        deletionService.deleteInstructorCascade(courseId, email);

        verifyAbsentInDatabase(instructorDeleted);
        // there should be no response of the instructor
        assertTrue(frLogic.getFeedbackResponsesFromGiverForCourse(courseId, email).isEmpty());
        assertTrue(frLogic.getFeedbackResponsesForReceiverForCourse(courseId, email).isEmpty());

        // The instructor should have no more selective deadlines.
        Set<FeedbackSessionAttributes> newSessionsWithInstructor1Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getInstructorDeadlines()
                        .containsKey(email))
                .collect(Collectors.toSet());
        assertTrue(newSessionsWithInstructor1Deadlines.isEmpty());
        Map<FeedbackSessionAttributes, Integer> expectedSessionsDeadlineCounts = oldSessionsDeadlineCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() - 1));
        Map<FeedbackSessionAttributes, Integer> newSessionsDeadlineCounts = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(oldSessionsWithInstructor1Deadlines::contains)
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getInstructorDeadlines().size()));
        assertEquals(expectedSessionsDeadlineCounts, newSessionsDeadlineCounts);

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class,
                () -> deletionService.deleteInstructorCascade(courseId, null));

        assertThrows(AssertionError.class, () -> instructorsLogic.deleteInstructorCascade(null, email));
    }

    @Test
    public void testDeleteInstructors_byCourseId_shouldDeleteInstructorsAssociatedWithTheCourse() {

        ______TS("typical case: delete all instructors for a non-existent course");

        deletionService.deleteInstructors(AttributesDeletionQuery.builder()
                .withCourseId("non-existent")
                .build());

        ______TS("typical case: delete all instructors of a given course");

        String courseId = "idOfTypicalCourse1";

        // the course is not empty at the beginning
        assertFalse(instructorsLogic.getInstructorsForCourse(courseId).isEmpty());

        deletionService.deleteInstructors(AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build());

        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
        assertTrue(instructorList.isEmpty());

        // other course is not affected
        assertFalse(instructorsLogic.getInstructorsForCourse("idOfTypicalCourse2").isEmpty());

        ______TS("failure case: null parameter");

        assertThrows(AssertionError.class, () -> instructorsLogic.deleteInstructors(null));

    }

    @Test
    public void testDeleteInstructorsForGoogleIdCascade_archivedInstructor_shouldDeleteAlso() throws Exception {
        InstructorAttributes instructor5 = dataBundle.instructors.get("instructor5");

        assertNotNull(instructor5.getGoogleId());
        instructorsLogic.setArchiveStatusOfInstructor(instructor5.getGoogleId(), instructor5.getCourseId(), true);

        // this is an archived instructor
        assertTrue(
                instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()).isArchived());

        deletionService.deleteInstructorsForGoogleIdCascade(instructor5.getGoogleId());

        // the instructor should be deleted also
        assertNull(instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()));
    }

    @Test
    public void testDeleteInstructorsForGoogleIdCascade() throws Exception {

        ______TS("typical case: delete non-existent googleId");

        deletionService.deleteInstructorsForGoogleIdCascade("not_exist");

        ______TS("typical case: delete all instructors of a given googleId");

        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        // make instructor1OfCourse1 to have the same googleId with instructor1OfCourse2
        instructorsLogic.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .withGoogleId(instructor1OfCourse2.getGoogleId())
                        .build());

        instructor1OfCourse1 = instructorsLogic.getInstructorForEmail(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail());
        assertNotNull(instructor1OfCourse1);

        // instructor1OfCourse1 has some responses in course
        assertFalse(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .isEmpty());

        instructor1OfCourse2 = instructorsLogic.getInstructorForEmail(
                instructor1OfCourse2.getCourseId(), instructor1OfCourse2.getEmail());
        assertNotNull(instructor1OfCourse2);

        // the two instructors have the same googleId but in different courses
        assertEquals(instructor1OfCourse1.getGoogleId(), instructor1OfCourse2.getGoogleId());
        assertNotEquals(instructor1OfCourse1.getCourseId(), instructor1OfCourse2.getCourseId());

        // delete instructors for google ID
        deletionService.deleteInstructorsForGoogleIdCascade(instructor1OfCourse1.getGoogleId());

        // the two instructors should gone
        assertNull(instructorsLogic.getInstructorForEmail(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
        // instructor1OfCourse1's responses should be deleted also
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .isEmpty());
        assertNull(instructorsLogic.getInstructorForEmail(
                instructor1OfCourse2.getCourseId(), instructor1OfCourse2.getEmail()));
    }

    @Test
    public void testDeleteAccountCascade_lastInstructorInCourse_shouldDeleteOrphanCourse() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor5");
        AccountAttributes account = dataBundle.accounts.get("instructor5");

        // verify the instructor is the last instructor of a course
        assertEquals(1, instructorsLogic.getInstructorsForCourse(instructor.getCourseId()).size());

        // Make instructor account id a student too.
        StudentAttributes student = StudentAttributes
                .builder(instructor.getCourseId(), "email@test.com")
                .withName(instructor.getName())
                .withSectionName("section")
                .withTeamName("team")
                .withComment("")
                .withGoogleId(instructor.getGoogleId())
                .build();
        studentsLogic.createStudent(student);
        verifyPresentInDatabase(account);
        verifyPresentInDatabase(instructor);
        verifyPresentInDatabase(student);

        accountsLogic.deleteAccountCascade(instructor.getGoogleId());

        verifyAbsentInDatabase(account);
        verifyAbsentInDatabase(instructor);
        verifyAbsentInDatabase(student);
        // course is deleted because it is the last instructor of the course
        assertNull(coursesLogic.getCourse(instructor.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_notLastInstructorInCourse_shouldNotDeleteCourse() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        // verify the instructor is not the last instructor of a course
        assertTrue(instructorsLogic.getInstructorsForCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        assertNotNull(instructor1OfCourse1.getGoogleId());
        deletionService.deleteAccountCascade(instructor1OfCourse1.getGoogleId());

        // course is not deleted
        assertNotNull(coursesLogic.getCourse(instructor1OfCourse1.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_instructorArchivedAsLastInstructor_shouldDeleteCourseAlso() throws Exception {
        InstructorAttributes instructor5 = dataBundle.instructors.get("instructor5");

        assertNotNull(instructor5.getGoogleId());
        instructorsLogic.setArchiveStatusOfInstructor(instructor5.getGoogleId(), instructor5.getCourseId(), true);

        // verify the instructor is the last instructor of a course
        assertEquals(1, instructorsLogic.getInstructorsForCourse(instructor5.getCourseId()).size());

        assertTrue(
                instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()).isArchived());

        deletionService.deleteAccountCascade(instructor5.getGoogleId());

        // the archived instructor is also deleted
        assertNull(instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()));
        // the course is also deleted
        assertNull(coursesLogic.getCourse(instructor5.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_nonExistentAccount_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        deletionService.deleteAccountCascade("not_exist");

        // other irrelevant instructors remain
        assertNotNull(instructorsLogic.getInstructorForEmail(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        // This ensures the AccountRequestAttributes has the correct ID.
        AccountRequestAttributes accountRequestAttributes = dataBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = accountRequestAttributes.toEntity();
        AccountRequestAttributes a = AccountRequestAttributes.valueOf(accountRequest);

        ______TS("silent deletion of non-existent account request");

        deletionService.deleteAccountRequest("not_exist", "not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(a);

        deletionService.deleteAccountRequest(a.getEmail(), a.getInstitute());

        verifyAbsentInDatabase(a);

        ______TS("silent deletion of same account request");

        deletionService.deleteAccountRequest(a.getEmail(), a.getInstitute());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> deletionService.deleteAccountRequest(null, null));
    }

    @Test
    public void testDeleteDeadlineExtension() {
        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student3InCourse1Session1");

        ______TS("silent deletion of non-existent deadline extension");

        deletionService.deleteDeadlineExtension("unknown-course-id", "unknown-fs-name", "not-found@test.com", false);

        ______TS("typical success case");

        verifyPresentInDatabase(deadlineExtension);

        deletionService.deleteDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        verifyAbsentInDatabase(deadlineExtension);

        ______TS("silent deletion of same deadline extension");

        deletionService.deleteDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> deletionService.deleteDeadlineExtension(null, null, null, false));
    }

    @Test
    public void testDeleteDeadlineExtensions_byCourseIdAndUserDetails() throws Exception {

        ______TS("Typical success case");

        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes deadlineExtensionDifferentFs =
                dataBundle.deadlineExtensions.get("student4InCourse1Session2");
        DeadlineExtensionAttributes deadlineExtensionDifferentCourse = DeadlineExtensionAttributes
                .builder("different-course-id", deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor())
                .build();
        DeadlineExtensionAttributes deadlineExtensionDifferentUserType = DeadlineExtensionAttributes
                .builder(deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), true)
                .build();
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentCourse);
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentUserType);

        deadlineExtensionsLogic.deleteDeadlineExtensions(deadlineExtension.getCourseId(),
                deadlineExtension.getUserEmail(), false);

        ______TS("Deadline extension with same course id deleted");

        verifyAbsentInDatabase(deadlineExtension);
        verifyAbsentInDatabase(deadlineExtensionDifferentFs);

        ______TS("Deadline extension with different course id not deleted");

        verifyPresentInDatabase(deadlineExtensionDifferentCourse);

        ______TS("Deadline extension with different user type not deleted");

        verifyPresentInDatabase(deadlineExtensionDifferentUserType);

        ______TS("Delete single deadline extension");

        deletionService.deleteDeadlineExtensions("different-course-id", deadlineExtension.getUserEmail(), false);
        verifyAbsentInDatabase(deadlineExtensionDifferentCourse);

        deletionService.deleteDeadlineExtensions(
                deadlineExtension.getCourseId(), deadlineExtension.getUserEmail(), true);
        verifyAbsentInDatabase(deadlineExtensionDifferentUserType);
    }

    @Test
    public void testDeleteFeedbackQuestions_byCourseIdAndSessionName_shouldDeleteQuestions() {
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        assertFalse(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        FeedbackSessionAttributes anotherFsa = dataBundle.feedbackSessions.get("session2InCourse1");
        assertFalse(
                fqLogic.getFeedbackQuestionsForSession(anotherFsa.getFeedbackSessionName(), anotherFsa.getCourseId())
                        .isEmpty());

        deletionService.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .build());

        assertTrue(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        // other sessions are not affected
        assertFalse(
                fqLogic.getFeedbackQuestionsForSession(anotherFsa.getFeedbackSessionName(), anotherFsa.getCourseId())
                        .isEmpty());

    }

    @Test
    public void testDeleteFeedbackQuestionCascade_existentQuestion_shouldDoCascadeDeletion() {
        FeedbackQuestionAttributes typicalQuestion = getQuestionFromDatabase("qn3InSession1InCourse1");
        assertEquals(3, typicalQuestion.getQuestionNumber());
        assertEquals(4, getQuestionFromDatabase("qn4InSession1InCourse1").getQuestionNumber());

        // the question has some responses and comments
        assertFalse(frLogic.getFeedbackResponsesForQuestion(typicalQuestion.getId()).isEmpty());
        assertFalse(
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                                typicalQuestion.getCourseId(), typicalQuestion.getFeedbackSessionName(), null).stream()
                        .noneMatch(comment -> comment.getFeedbackQuestionId().equals(typicalQuestion.getId())));

        deletionService.deleteFeedbackQuestionCascade(typicalQuestion.getId());

        assertNull(fqLogic.getFeedbackQuestion(typicalQuestion.getId()));
        // the responses and comments should gone
        assertTrue(frLogic.getFeedbackResponsesForQuestion(typicalQuestion.getId()).isEmpty());
        assertTrue(
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                                typicalQuestion.getCourseId(), typicalQuestion.getFeedbackSessionName(), null).stream()
                        .noneMatch(comment -> comment.getFeedbackQuestionId().equals(typicalQuestion.getId())));

        // verify that questions are shifted
        List<FeedbackQuestionAttributes> questionsOfSessions =
                fqLogic.getFeedbackQuestionsForSession(
                        typicalQuestion.getFeedbackSessionName(), typicalQuestion.getCourseId());
        for (int i = 1; i <= questionsOfSessions.size(); i++) {
            assertEquals(i, questionsOfSessions.get(i - 1).getQuestionNumber());
        }
    }

    @Test
    public void testDeleteFeedbackQuestionCascade_nonExistentQuestion_shouldFailSilently() {
        deletionService.deleteFeedbackQuestionCascade("non-existent-question-id");

        // other questions not get affected
        assertNotNull(getQuestionFromDatabase("qn3InSession1InCourse1"));
    }

    @Test
    public void testDeleteFeedbackQuestionCascade_cascadeDeleteResponseOfStudent_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackQuestionAttributes fqa = fqLogic.getFeedbackQuestion(
                fra.getFeedbackSessionName(), fra.getCourseId(), Integer.parseInt(fra.getFeedbackQuestionId()));
        FeedbackResponseAttributes responseInDb = frLogic.getFeedbackResponse(
                fqa.getId(), fra.getGiver(), fra.getRecipient());
        assertNotNull(responseInDb);

        // the student only gives this response for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(responseInDb.getCourseId(), responseInDb.getGiver())
                .stream()
                .filter(response -> response.getFeedbackSessionName().equals(responseInDb.getFeedbackSessionName()))
                .count());
        // he is in the giver set
        assertTrue(frLogic.getGiverSetThatAnswerFeedbackSession(fqa.getCourseId(), fqa.getFeedbackSessionName())
                .contains(responseInDb.getGiver()));

        // after deletion the question
        deletionService.deleteFeedbackQuestionCascade(responseInDb.getFeedbackQuestionId());

        // the student should not in the giver set
        assertFalse(frLogic.getGiverSetThatAnswerFeedbackSession(fqa.getCourseId(), fqa.getFeedbackSessionName())
                .contains(responseInDb.getGiver()));
    }

    @Test
    public void testDeleteFeedbackQuestions_byCourseId_shouldDeleteQuestions() {
        String courseId = "idOfTypicalCourse2";
        FeedbackQuestionAttributes deletedQuestion = getQuestionFromDatabase("qn1InSession1InCourse2");
        assertNotNull(deletedQuestion);

        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertFalse(questions.isEmpty());

        deletionService.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build());
        deletedQuestion = getQuestionFromDatabase("qn1InSession1InCourse2");
        assertNull(deletedQuestion);

        questions = fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertEquals(0, questions.size());

        // test that questions in other courses are unaffected
        assertNotNull(getQuestionFromDatabase("qn1InSessionInArchivedCourse"));
        assertNotNull(getQuestionFromDatabase("qn1InSession4InCourse1"));
    }

    @Test
    public void testDeleteFeedbackResponseComment() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes actualFrComment =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.getCourseId(), frComment.getFeedbackSessionName(), null).get(1);

        ______TS("silent fail nothing to delete");

        assertNull(frcLogic.getFeedbackResponseComment(1234567L));
        deletionService.deleteFeedbackResponseComment(1234567L);

        ______TS("typical success case");

        verifyPresentInDatabase(actualFrComment);
        deletionService.deleteFeedbackResponseComment(actualFrComment.getId());
        verifyAbsentInDatabase(actualFrComment);
    }

    @Test
    public void testDeleteFeedbackResponseComments_deleteByResponseId() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        verifyPresentInDatabase(frComment);
        deletionService.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(frComment.getFeedbackResponseId())
                        .build());
        verifyAbsentInDatabase(frComment);
    }

    @Test
    public void testDeleteFeedbackResponseComments_deleteByCourseId() {

        ______TS("typical case");
        String courseId = "idOfTypicalCourse1";

        List<FeedbackResponseCommentAttributes> frcList =
                frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, "First feedback session", null);
        assertFalse(frcList.isEmpty());

        deletionService.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(courseId)
                        .build());

        frcList = frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, "First feedback session", null);
        assertEquals(0, frcList.size());
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelatedResponses() {
        StudentAttributes studentToDelete = dataBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");

        // the responses also have some associated comments
        List<FeedbackResponseAttributes> remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        assertFalse(remainingResponses.isEmpty());

        // the student has some responses
        List<FeedbackResponseAttributes> responsesForStudent1 =
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail());
        responsesForStudent1.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        assertFalse(responsesForStudent1.isEmpty());
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(session1InCourse1.getCourseId(),
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.getEmail()));

        deletionService.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                studentToDelete.getCourse(), studentToDelete.getEmail());

        // responses should be deleted
        remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        assertEquals(0, remainingResponses.size());

        // comments should also be deleted
        List<FeedbackResponseCommentAttributes> remainingComments = new ArrayList<>();
        for (FeedbackResponseAttributes response : responsesForStudent1) {
            remainingComments.addAll(frcLogic.getFeedbackResponseCommentForResponse(response.getId()));
        }
        assertEquals(0, remainingComments.size());

        // the student no longer has responses for the session
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(session1InCourse1.getCourseId(),
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.getEmail()));
    }

    @Test
    public void testDeleteFeedbackResponseCascade() {
        ______TS("non-existent response");

        // should pass silently
        deletionService.deleteFeedbackResponseCascade("not-exist");

        ______TS("standard delete");

        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S1C1");
        assertNotNull(fra);
        // the response has comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra.getId()).isEmpty());

        deletionService.deleteFeedbackResponseCascade(fra.getId());

        assertNull(frLogic.getFeedbackResponse(fra.getId()));
        // associated comments are deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra.getId()).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponses_byCourseId() {
        ______TS("standard delete");

        // test that responses are deleted
        String courseId = "idOfTypicalCourse1";
        assertFalse(frLogic.getFeedbackResponsesForSession("First feedback session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Closed Session", courseId).isEmpty());

        deletionService.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId(courseId)
                        .build());

        assertEquals(0, frLogic.getFeedbackResponsesForSession("First feedback session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Closed Session", courseId).size());

        // test that responses from other courses are unaffected
        String otherCourse = "idOfTypicalCourse2";
        assertFalse(frLogic.getFeedbackResponsesForSession("Instructor feedback session", otherCourse).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_studentsQuestion_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S1C1");

        // this is the only response the student has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        deletionService.deleteFeedbackResponsesForQuestionCascade(fra.getFeedbackQuestionId());

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_instructorsQuestion_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ3S1C1");

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        deletionService.deleteFeedbackResponsesForQuestionCascade(fra.getFeedbackQuestionId());

        // there is not instructor X in instructor respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsStudent_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response3ForQ2S1C1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        // giver is student
        assertEquals(FeedbackParticipantType.STUDENTS,
                fqLogic.getFeedbackQuestion(fra.getFeedbackQuestionId()).getGiverType());
        // student is the recipient
        assertEquals(fra.getRecipient(), student2InCourse1.getEmail());

        // this is the only response the giver has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        // after the giver is removed from the course
        deletionService.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student2InCourse1.getCourse(), student2InCourse1.getEmail());

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsInstructor_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S2C2");
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");
        // giver is instructor
        assertEquals(FeedbackParticipantType.SELF,
                fqLogic.getFeedbackQuestion(fra.getFeedbackQuestionId()).getGiverType());
        // student is the recipient
        assertEquals(fra.getRecipient(), student1InCourse2.getEmail());

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        // after the giver is removed from the course
        deletionService.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student1InCourse2.getCourse(), student1InCourse2.getEmail());

        // there is no instructor X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelevantResponsesAsRecipient()
            throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        FeedbackResponseAttributes fra1ReceivedByTeam = getResponseFromDatabase("response1ForQ1S2C1");
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra1ReceivedByTeam.getCourseId())
                        .withFeedbackSessionName(fra1ReceivedByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 1")
                        .withFeedbackQuestionId(fra1ReceivedByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra1ReceivedByTeam.getId())
                        .withGiverSection(fra1ReceivedByTeam.getGiverSection())
                        .withReceiverSection(fra1ReceivedByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());
        FeedbackResponseAttributes fra2ReceivedByTeam = getResponseFromDatabase("response1GracePeriodFeedback");
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra2ReceivedByTeam.getCourseId())
                        .withFeedbackSessionName(fra2ReceivedByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 2")
                        .withFeedbackQuestionId(fra2ReceivedByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra2ReceivedByTeam.getId())
                        .withGiverSection(fra2ReceivedByTeam.getGiverSection())
                        .withReceiverSection(fra2ReceivedByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());

        String teamName = "Team 1.2";
        assertEquals(teamName, fra1ReceivedByTeam.getRecipient());
        assertEquals(teamName, fra2ReceivedByTeam.getRecipient());

        // both responses got some comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra1ReceivedByTeam.getId()).isEmpty());
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra2ReceivedByTeam.getId()).isEmpty());

        deletionService.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(fra1ReceivedByTeam.getCourseId(), teamName);

        // responses received by the team should be deleted
        assertNull(frLogic.getFeedbackResponse(fra1ReceivedByTeam.getId()));
        assertNull(frLogic.getFeedbackResponse(fra2ReceivedByTeam.getId()));

        // their associated comments should be deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra1ReceivedByTeam.getId()).isEmpty());
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra2ReceivedByTeam.getId()).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelevantResponsesAsGiver()
            throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");

        // the following two responses are given by student4InCourse1 as a representative of his team
        FeedbackResponseAttributes fra1GivenByTeam = getResponseFromDatabase("response1ForQ1S2C1");
        // update the response's giver to the team name
        fra1GivenByTeam = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(fra1GivenByTeam.getId())
                        .withGiver(student4InCourse1.getTeam())
                        .build());
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra1GivenByTeam.getCourseId())
                        .withFeedbackSessionName(fra1GivenByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 1")
                        .withFeedbackQuestionId(fra1GivenByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra1GivenByTeam.getId())
                        .withGiverSection(fra1GivenByTeam.getGiverSection())
                        .withReceiverSection(fra1GivenByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());
        FeedbackResponseAttributes fra2GivenByTeam = getResponseFromDatabase("response1GracePeriodFeedback");
        // update the response's giver to the team name
        fra2GivenByTeam = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(fra2GivenByTeam.getId())
                        .withGiver(student4InCourse1.getTeam())
                        .build());
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra2GivenByTeam.getCourseId())
                        .withFeedbackSessionName(fra2GivenByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 2")
                        .withFeedbackQuestionId(fra2GivenByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra2GivenByTeam.getId())
                        .withGiverSection(fra2GivenByTeam.getGiverSection())
                        .withReceiverSection(fra2GivenByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());

        String teamName = student4InCourse1.getTeam();
        assertEquals(teamName, fra1GivenByTeam.getGiver());
        assertEquals(teamName, fra2GivenByTeam.getGiver());

        // both responses got some comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra1GivenByTeam.getId()).isEmpty());
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra2GivenByTeam.getId()).isEmpty());

        deletionService.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(fra1GivenByTeam.getCourseId(), teamName);

        // responses received by the team should be deleted
        assertNull(frLogic.getFeedbackResponse(fra1GivenByTeam.getId()));
        assertNull(frLogic.getFeedbackResponse(fra2GivenByTeam.getId()));

        // their associated comments should be deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra1GivenByTeam.getId()).isEmpty());
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra2GivenByTeam.getId()).isEmpty());
    }

    @Test
    public void testDeleteNotification() {
        ______TS("success: delete corresponding notification");

        NotificationAttributes n = typicalNotifications.get("notification1");
        deletionService.deleteNotification(n.getNotificationId());

        verifyAbsentInDatabase(n);

        ______TS("failure: silent deletion of the same notification twice");

        deletionService.deleteNotification(n.getNotificationId());

        ______TS("failure: silent deletion of non-existent notification");

        int expectedLength = notifDb.getAllNotifications().size();
        deletionService.deleteNotification("invalid-id");
        int actualLength = notifDb.getAllNotifications().size();

        assertEquals(expectedLength, actualLength);

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> deletionService.deleteNotification(null));
    }

    @AfterClass
    public void classTearDown() {
        deletionService.deleteAccountCascade(dataBundle.students.get("student4InCourse1").getGoogleId());
    }

    private FeedbackQuestionAttributes getQuestionFromDatabase(String questionKey) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionKey);
        question = fqLogic.getFeedbackQuestion(
                question.getFeedbackSessionName(), question.getCourseId(), question.getQuestionNumber());
        return question;
    }

    private FeedbackResponseCommentAttributes restoreFrCommentFromDataBundle(String existingFrCommentInDataBundle) {

        FeedbackResponseCommentAttributes existingFrComment =
                dataBundle.feedbackResponseComments.get(existingFrCommentInDataBundle);

        FeedbackResponseCommentAttributes frComment = FeedbackResponseCommentAttributes.builder()
                .withCourseId(existingFrComment.getCourseId())
                .withFeedbackSessionName(existingFrComment.getFeedbackSessionName())
                .withCommentGiver(existingFrComment.getCommentGiver())
                .withCommentText(existingFrComment.getCommentText())
                .withFeedbackQuestionId(existingFrComment.getFeedbackQuestionId())
                .withFeedbackResponseId(existingFrComment.getFeedbackResponseId())
                .withCommentGiverType(existingFrComment.getCommentGiverType())
                .withCommentFromFeedbackParticipant(false)
                .build();
        frComment.setCreatedAt(existingFrComment.getCreatedAt());

        restoreFrCommentIdFromExistingOne(frComment, existingFrComment);

        return frComment;
    }

    private void restoreFrCommentIdFromExistingOne(
            FeedbackResponseCommentAttributes frComment,
            FeedbackResponseCommentAttributes existingFrComment) {

        List<FeedbackResponseCommentAttributes> existingFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        existingFrComment.getCourseId(),
                        existingFrComment.getFeedbackSessionName(), null);

        FeedbackResponseCommentAttributes existingFrCommentWithId = null;
        for (FeedbackResponseCommentAttributes c : existingFrComments) {
            if (c.getCommentText().equals(existingFrComment.getCommentText())) {
                existingFrCommentWithId = c;
                break;
            }
        }
        if (existingFrCommentWithId != null) {
            frComment.setId(existingFrCommentWithId.getId());
            frComment.setFeedbackResponseId(existingFrCommentWithId.getFeedbackResponseId());
        }
    }

    private FeedbackResponseAttributes getResponseFromDatabase(DataBundle dataBundle, String jsonId) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(jsonId);

        String qnId;
        try {
            int qnNumber = Integer.parseInt(response.getFeedbackQuestionId());
            qnId = fqLogic.getFeedbackQuestion(response.getFeedbackSessionName(), response.getCourseId(), qnNumber).getId();
        } catch (NumberFormatException e) {
            qnId = response.getFeedbackQuestionId();
        }

        return frLogic.getFeedbackResponse(
                qnId, response.getGiver(), response.getRecipient());
    }

    private FeedbackResponseAttributes getResponseFromDatabase(String jsonId) {
        return getResponseFromDatabase(dataBundle, jsonId);
    }

}