package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.AssertHelper;

/**
 * SUT: {@link StudentsLogic}.
 */
public class StudentsLogicTest extends BaseLogicTest {

    private final AccountsLogic accountsLogic = AccountsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();
    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();

        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testAll() throws Exception {

        testGetStudentForEmail();
        testGetStudentForRegistrationKey();
        testGetStudentsForGoogleId();
        testGetStudentForCourseIdAndGoogleId();
        testGetNumberOfStudentsForCourse();
        testGetStudentsForCourse();
        testVerifyAllStudentsExistInCourse();
        testIsStudentInAnyCourse();
        testIsStudentInTeam();
        testIsStudentsInSameTeam();
        testValidateSections();
        testUpdateStudentCascade();
    }

    private void testValidateSections() throws Exception {

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        String courseId = typicalCourse1.getId();

        ______TS("Typical case");

        List<StudentAttributes> studentList = new ArrayList<>();
        studentList.add(StudentAttributes
                .builder(courseId, "emailNew@com")
                .withName("New Student")
                .withSectionName("Section 3")
                .withTeamName("Team 1.3")
                .withComment("")
                .build());
        studentList.add(StudentAttributes
                .builder(courseId, "student2InCourse1@gmail.tmt")
                .withName("student2 In Course1")
                .withSectionName("Section 2")
                .withTeamName("Team 1.4")
                .withComment("")
                .build());

        studentsLogic.validateSectionsAndTeams(studentList, courseId);

        ______TS("Failure case: invalid section");

        studentList.clear();
        for (int i = 0; i < 100; i++) {
            StudentAttributes addedStudent = StudentAttributes
                    .builder(courseId, "email@com" + i)
                    .withName("Name " + i)
                    .withSectionName("Section 1")
                    .withTeamName("Team " + i)
                    .withComment("cmt" + i)
                    .build();
            studentList.add(addedStudent);
        }
        EnrollException ee = assertThrows(EnrollException.class,
                () -> studentsLogic.validateSectionsAndTeams(studentList, courseId));

        String expectedInvalidSectionError =
                String.format(
                        StudentsLogic.ERROR_ENROLL_EXCEED_SECTION_LIMIT,
                        Const.SECTION_SIZE_LIMIT, "Section 1")
                        + " "
                        + String.format(StudentsLogic.ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION,
                        Const.SECTION_SIZE_LIMIT);

        assertEquals(expectedInvalidSectionError, ee.getMessage());

        ______TS("Failure case: invalid team");

        studentList.clear();
        studentList.add(StudentAttributes
                .builder(courseId, "newemail@com")
                .withName("New Student")
                .withSectionName("Section 2")
                .withTeamName("Team 1.1")
                .withComment("")
                .build());
        studentList.add(StudentAttributes
                .builder(courseId, "newemail2@com")
                .withName("New Student 2")
                .withSectionName("Section 3")
                .withTeamName("Team 1.1")
                .withComment("")
                .build());
        ee = assertThrows(EnrollException.class, () -> studentsLogic.validateSectionsAndTeams(studentList, courseId));

        String expectedInvalidTeamError =
                String.format(StudentsLogic.ERROR_INVALID_TEAM_NAME, "Team 1.1", "Section 2", "Section 3")
                + " "
                + StudentsLogic.ERROR_INVALID_TEAM_NAME_INSTRUCTION;

        assertEquals(expectedInvalidTeamError, ee.getMessage());
    }

    @Test
    public void testUpdateStudentCascade() throws Exception {

        ______TS("typical edit");

        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        verifyPresentInDatabase(student4InCourse1);
        String originalEmail = student4InCourse1.getEmail();
        student4InCourse1 = studentsLogic.getStudentForEmail(student4InCourse1.getCourse(), student4InCourse1.getEmail());
        student4InCourse1.setName(student4InCourse1.getName() + "y");
        student4InCourse1.setGoogleId(student4InCourse1.getGoogleId() + "y");
        student4InCourse1.setComments(student4InCourse1.getComments() + "y");
        student4InCourse1.setEmail(student4InCourse1.getEmail() + "y");
        student4InCourse1.setSection("Section 2");
        student4InCourse1.setTeam("Team 1.2"); // move to a different team

        StudentAttributes updatedStudent = studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student4InCourse1.getCourse(), originalEmail)
                        .withName(student4InCourse1.getName())
                        .withGoogleId(student4InCourse1.getGoogleId())
                        .withComment(student4InCourse1.getComments())
                        .withNewEmail(student4InCourse1.getEmail())
                        .withSectionName(student4InCourse1.getSection())
                        .withTeamName(student4InCourse1.getTeam())
                        .build()
        );
        StudentAttributes actualStudent =
                studentsLogic.getStudentForEmail(student4InCourse1.getCourse(), student4InCourse1.getEmail());
        assertFalse(student4InCourse1.getUpdatedAt().equals(actualStudent.getUpdatedAt()));
        assertEquals(student4InCourse1.getName(), actualStudent.getName());
        assertEquals(student4InCourse1.getName(), updatedStudent.getName());
        assertEquals(student4InCourse1.getEmail(), actualStudent.getEmail());
        assertEquals(student4InCourse1.getEmail(), updatedStudent.getEmail());
        assertEquals(student4InCourse1.getGoogleId(), actualStudent.getGoogleId());
        assertEquals(student4InCourse1.getGoogleId(), updatedStudent.getGoogleId());
        assertEquals(student4InCourse1.getSection(), actualStudent.getSection());
        assertEquals(student4InCourse1.getSection(), updatedStudent.getSection());
        assertEquals(student4InCourse1.getTeam(), actualStudent.getTeam());
        assertEquals(student4InCourse1.getTeam(), updatedStudent.getTeam());
        assertEquals(student4InCourse1.getComments(), actualStudent.getComments());
        assertEquals(student4InCourse1.getComments(), updatedStudent.getComments());

        ______TS("change email only");

        originalEmail = student4InCourse1.getEmail();
        student4InCourse1.setEmail(student4InCourse1.getEmail() + "y");

        studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student4InCourse1.getCourse(), originalEmail)
                        .withNewEmail(student4InCourse1.getEmail())
                        .build()
        );
        verifyPresentInDatabase(student4InCourse1);

        ______TS("update nothing");

        studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student4InCourse1.getCourse(), student4InCourse1.getEmail())
                        .build()
        );
        verifyPresentInDatabase(student4InCourse1);

        ______TS("non-existent student");

        StudentAttributes finalStudent4InCourse1 = student4InCourse1;
        StudentAttributes.UpdateOptions updateOptions =
                StudentAttributes.updateOptionsBuilder(finalStudent4InCourse1.getCourse(), "non-existent@email")
                        .withName("test")
                        .build();
        assertThrows(EntityDoesNotExistException.class,
                () -> studentsLogic.updateStudentCascade(updateOptions));

        ______TS("check for InvalidParameters");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> studentsLogic.updateStudentCascade(
                        StudentAttributes.updateOptionsBuilder(finalStudent4InCourse1.getCourse(),
                                finalStudent4InCourse1.getEmail())
                                .withNewEmail("invalid email")
                                .build()
                ));
        AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT, ipe.getMessage());

    }

    @Test
    public void testUpdateStudentCascade_emailChanged_shouldUpdateStudentDeadlineMaps() throws Exception {
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        String courseId = student4InCourse1.getCourse();
        String oldEmailAddress = student4InCourse1.getEmail();
        String newEmailAddress = "new@email.tmt";

        Map<Instant, Integer> oldDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getStudentDeadlines)
                .filter(studentDeadlines -> studentDeadlines.containsKey(oldEmailAddress))
                .map(studentDeadlines -> studentDeadlines.get(oldEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(2, oldDeadlineCounts.values()
                .stream()
                .reduce(0, Integer::sum)
                .intValue());

        studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student4InCourse1.getCourse(), oldEmailAddress)
                        .withNewEmail(newEmailAddress)
                        .build()
        );

        assertTrue(fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .noneMatch(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(oldEmailAddress)));
        Map<Instant, Integer> newDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getStudentDeadlines)
                .filter(studentDeadlines -> studentDeadlines.containsKey(newEmailAddress))
                .map(studentDeadlines -> studentDeadlines.get(newEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(oldDeadlineCounts, newDeadlineCounts);
    }

    @Test
    public void testUpdateStudentCascade_teamChanged_shouldDeleteOldResponsesWithinTheTeam() throws Exception {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");

        FeedbackResponseAttributes responseToBeDeleted = dataBundle.feedbackResponses.get("response2ForQ2S2C1");
        FeedbackQuestionAttributes feedbackQuestionInDb =
                fqLogic.getFeedbackQuestion(responseToBeDeleted.getFeedbackSessionName(),
                        responseToBeDeleted.getCourseId(),
                        Integer.parseInt(responseToBeDeleted.getFeedbackQuestionId()));
        responseToBeDeleted =
                frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                        responseToBeDeleted.getGiver(), responseToBeDeleted.getRecipient());

        // response exist
        assertNotNull(responseToBeDeleted);

        studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student1InCourse1.getCourse(), student1InCourse1.getEmail())
                        .withTeamName(student1InCourse1.getTeam() + "tmp")
                        .build());

        responseToBeDeleted =
                frLogic.getFeedbackResponse(feedbackQuestionInDb.getId(),
                        responseToBeDeleted.getGiver(), responseToBeDeleted.getRecipient());

        // response should not exist
        assertNull(responseToBeDeleted);
    }

    @Test
    public void testRegenerateStudentRegistrationKey() throws Exception {
        ______TS("typical regeneration of course student's registration key");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        verifyPresentInDatabase(student1InCourse1);

        StudentAttributes updatedStudent =
                studentsLogic.regenerateStudentRegistrationKey(student1InCourse1.getCourse(), student1InCourse1.getEmail());

        assertNotEquals(student1InCourse1.getKey(), updatedStudent.getKey());

        ______TS("non-existent student");

        String nonExistentEmail = "non-existent@email";
        assertNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), nonExistentEmail));

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> studentsLogic.regenerateStudentRegistrationKey(student1InCourse1.getCourse(), nonExistentEmail));
        assertEquals("The student with the email " + nonExistentEmail + " could not be found for the course "
                        + "with ID [" + student1InCourse1.getCourse() + "].",
                ednee.getMessage());
    }

    private void testGetStudentForEmail() {

        ______TS("null parameters");

        assertThrows(AssertionError.class,
                () -> studentsLogic.getStudentForEmail(null, "valid@email.tmt"));

        ______TS("non-exist student");

        String nonExistStudentEmail = "nonExist@google.tmt";
        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        assertNull(studentsLogic.getStudentForEmail(course1Id, nonExistStudentEmail));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertEquals(student1InCourse1.getGoogleId(),
                studentsLogic.getStudentForEmail(course1Id, student1InCourse1.getEmail()).getGoogleId());
    }

    private void testGetStudentForRegistrationKey() {

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> studentsLogic.getStudentForRegistrationKey(null));

        ______TS("non-exist student");

        String nonExistStudentKey = StringHelper.encrypt("nonExistKey");
        assertNull(studentsLogic.getStudentForRegistrationKey(nonExistStudentKey));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String course1Id = dataBundle.courses.get("typicalCourse1").getId();
        String studentKey = studentsLogic.getStudentForCourseIdAndGoogleId(
                course1Id, student1InCourse1.getGoogleId()).getKey();
        StudentAttributes actualStudent = studentsLogic.getStudentForRegistrationKey(studentKey);
        assertEquals(student1InCourse1.getGoogleId(), actualStudent.getGoogleId());
    }

    private void testGetStudentsForGoogleId() {

        ______TS("student in one course");

        StudentAttributes studentInCourse1 = dataBundle.students.get("student1InCourse1");
        assertEquals(1, studentsLogic.getStudentsForGoogleId(studentInCourse1.getGoogleId()).size());
        assertEquals(studentInCourse1.getEmail(),
                studentsLogic.getStudentsForGoogleId(studentInCourse1.getGoogleId()).get(0).getEmail());
        assertEquals(studentInCourse1.getName(),
                studentsLogic.getStudentsForGoogleId(studentInCourse1.getGoogleId()).get(0).getName());
        assertEquals(studentInCourse1.getCourse(),
                studentsLogic.getStudentsForGoogleId(studentInCourse1.getGoogleId()).get(0).getCourse());

        ______TS("student in two courses");

        // this student is in two courses, course1 and course 2.

        // get list using student data from course 1
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
        List<StudentAttributes> listReceivedUsingStudentInCourse1 = studentsLogic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse1.getGoogleId());
        assertEquals(2, listReceivedUsingStudentInCourse1.size());

        // get list using student data from course 2
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        List<StudentAttributes> listReceivedUsingStudentInCourse2 = studentsLogic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse2.getGoogleId());
        assertEquals(2, listReceivedUsingStudentInCourse2.size());

        // check the content from first list (we assume the content of the
        // second list is similar.

        listReceivedUsingStudentInCourse1.sort(Comparator.comparing(student -> student.getCourse()));

        StudentAttributes firstStudentReceived = listReceivedUsingStudentInCourse1.get(1);
        // First student received turned out to be the one from course 2
        assertEquals(studentInTwoCoursesInCourse2.getEmail(),
                firstStudentReceived.getEmail());
        assertEquals(studentInTwoCoursesInCourse2.getName(),
                firstStudentReceived.getName());
        assertEquals(studentInTwoCoursesInCourse2.getCourse(),
                firstStudentReceived.getCourse());

        // then the second student received must be from course 1
        StudentAttributes secondStudentReceived = listReceivedUsingStudentInCourse1
                .get(0);
        assertEquals(studentInTwoCoursesInCourse1.getEmail(),
                secondStudentReceived.getEmail());
        assertEquals(studentInTwoCoursesInCourse1.getName(),
                secondStudentReceived.getName());
        assertEquals(studentInTwoCoursesInCourse1.getCourse(),
                secondStudentReceived.getCourse());

        ______TS("non existent student");

        assertEquals(0, studentsLogic.getStudentsForGoogleId("non-existent").size());

        ______TS("null parameters");

        assertThrows(AssertionError.class, () -> studentsLogic.getStudentsForGoogleId(null));
    }

    private void testGetStudentForCourseIdAndGoogleId() {

        ______TS("student in two courses");

        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");

        String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.getGoogleId();
        assertEquals(studentInTwoCoursesInCourse1.getEmail(),
                studentsLogic.getStudentForCourseIdAndGoogleId(
                        studentInTwoCoursesInCourse1.getCourse(),
                        googleIdOfstudentInTwoCourses).getEmail());

        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        assertEquals(studentInTwoCoursesInCourse2.getEmail(),
                studentsLogic.getStudentForCourseIdAndGoogleId(
                        studentInTwoCoursesInCourse2.getCourse(),
                        googleIdOfstudentInTwoCourses).getEmail());

        ______TS("student in zero courses");

        assertNull(studentsLogic.getStudentForCourseIdAndGoogleId("non-existent",
                "random-google-id"));

        ______TS("null parameters");

        assertThrows(AssertionError.class,
                () -> studentsLogic.getStudentForCourseIdAndGoogleId("valid.course", null));
    }

    private void testGetNumberOfStudentsForCourse() {

        ______TS("course with multiple students");

        CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
        int numOfStudents = studentsLogic.getNumberOfStudentsForCourse(course1OfInstructor1.getId());
        assertEquals(5, numOfStudents);

        ______TS("course with 0 students");

        CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
        numOfStudents = studentsLogic.getNumberOfStudentsForCourse(course2OfInstructor1.getId());
        assertEquals(0, numOfStudents);

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> studentsLogic.getNumberOfStudentsForCourse(null));

        ______TS("non-existent course");

        numOfStudents = studentsLogic.getNumberOfStudentsForCourse("non-existent");
        assertEquals(0, numOfStudents);

    }

    private void testGetStudentsForCourse() {

        ______TS("course with multiple students");

        CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
        List<StudentAttributes> studentList = studentsLogic
                .getStudentsForCourse(course1OfInstructor1.getId());
        assertEquals(5, studentList.size());
        for (StudentAttributes s : studentList) {
            assertEquals(course1OfInstructor1.getId(), s.getCourse());
        }

        ______TS("course with 0 students");

        CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
        studentList = studentsLogic.getStudentsForCourse(course2OfInstructor1.getId());
        assertEquals(0, studentList.size());

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> studentsLogic.getStudentsForCourse(null));

        ______TS("non-existent course");

        studentList = studentsLogic.getStudentsForCourse("non-existent");
        assertEquals(0, studentList.size());

        ______TS("with delete limit");

        var deleteLimit = 2;

        studentList = studentsLogic.getStudentsForCourse(course1OfInstructor1.getId(), deleteLimit);

        // number of students retrieved should be equal to the limit
        assertEquals(deleteLimit, studentList.size());
    }

    private void testVerifyAllStudentsExistInCourse() throws Exception {

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        String courseId = student.getCourse();

        Collection<String> studentEmailAddresses = new ArrayList<>();
        studentEmailAddresses.add(student.getEmail());

        ______TS("existing student email address in existing course");

        // should not throw an exception
        studentsLogic.verifyAllStudentsExistInCourse(courseId, studentEmailAddresses);

        ______TS("existing student email address in non-existent course");

        assertThrows(EntityDoesNotExistException.class, () ->
                studentsLogic.verifyAllStudentsExistInCourse("non-existent-course", studentEmailAddresses));

        ______TS("non-existent student email address in existing course");

        studentEmailAddresses.add("non-existent.student@email.com");

        assertThrows(EntityDoesNotExistException.class, () ->
                studentsLogic.verifyAllStudentsExistInCourse(courseId, studentEmailAddresses));

        ______TS("non-existent student email address in non-existent course");

        assertThrows(EntityDoesNotExistException.class, () ->
                studentsLogic.verifyAllStudentsExistInCourse("non-existent-course", studentEmailAddresses));
    }

    private void testIsStudentInAnyCourse() {

        ______TS("non-existent student");

        String nonExistStudentGoogleId = "nonExistGoogleId";
        assertFalse(studentsLogic.isStudentInAnyCourse(nonExistStudentGoogleId));

        ______TS("typical case");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentInAnyCourse(student1InCourse1.getGoogleId()));
    }

    private void testIsStudentInTeam() {

        ______TS("non-existent student");

        String nonExistStudentEmail = "nonExist@google.tmt";
        String teamName = "Team 1";
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.getId(), teamName, nonExistStudentEmail));

        ______TS("student not in given team");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertFalse(studentsLogic.isStudentInTeam(course1.getId(), teamName, nonExistStudentEmail));

        ______TS("typical case");
        teamName = student1InCourse1.getTeam();
        assertTrue(studentsLogic.isStudentInTeam(course1.getId(), teamName, student1InCourse1.getEmail()));
    }

    private void testIsStudentsInSameTeam() {

        ______TS("non-existent student1");

        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        String nonExistStudentEmail = "nonExist@google.tmt";
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.getId(), nonExistStudentEmail,
                student2InCourse1.getEmail()));

        ______TS("students of different teams");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertTrue(studentsLogic.isStudentsInSameTeam(course1.getId(), student2InCourse1.getEmail(),
                student1InCourse1.getEmail()));

        StudentAttributes student5InCourse1 = dataBundle.students.get("student5InCourse1");
        assertFalse(studentsLogic.isStudentsInSameTeam(course1.getId(), student2InCourse1.getEmail(),
                student5InCourse1.getEmail()));
    }

    @Test
    public void testDeleteStudentCascade_lastPersonInTeam_shouldDeleteTeamResponses() {
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");
        StudentAttributes student2InCourse2 = dataBundle.students.get("student2InCourse2");
        // they are in the same team
        assertEquals(student1InCourse2.getTeam(), student2InCourse2.getTeam());

        // delete the second student
        studentsLogic.deleteStudentCascade(student1InCourse2.getCourse(), student1InCourse2.getEmail());
        // there is only one student in the team
        assertEquals(1,
                studentsLogic.getStudentsForTeam(student2InCourse2.getTeam(), student2InCourse2.getCourse()).size());

        // get the response from DB
        FeedbackResponseAttributes fra = dataBundle.feedbackResponses.get("response1ForQ1S1C2");
        int qnNumber = Integer.parseInt(fra.getFeedbackQuestionId());
        String qnId = fqLogic.getFeedbackQuestion(fra.getFeedbackSessionName(), fra.getCourseId(), qnNumber).getId();
        fra = frLogic.getFeedbackResponse(qnId, fra.getGiver(), fra.getRecipient());
        assertNotNull(fra);
        // the team is the recipient of the response
        assertEquals(student2InCourse2.getTeam(), fra.getRecipient());
        // this is the only response the instructor has given for the session
        String feedbackSessionName = fra.getFeedbackSessionName();
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(feedbackSessionName))
                .count());
        // suppose the instructor has responses for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        // after the student is moved from the course
        // team response will also be removed
        studentsLogic.deleteStudentCascade(student2InCourse2.getCourse(), student2InCourse2.getEmail());

        // this will delete the response to the team
        assertNull(frLogic.getFeedbackResponse(fra.getId()));
        // the instructor no longer has responses for the session
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteStudentCascade_withSelectiveDeadlines_shouldDeleteDeadlines() {
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        verifyPresentInDatabase(student4InCourse1);

        String courseId = student4InCourse1.getCourse();
        String emailAddress = student4InCourse1.getEmail();

        // The student should have selective deadlines.
        Set<FeedbackSessionAttributes> oldSessionsWithStudent4Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        Map<FeedbackSessionAttributes, Integer> oldSessionsDeadlineCounts = oldSessionsWithStudent4Deadlines
                .stream()
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getStudentDeadlines().size()));
        assertEquals(2, oldSessionsWithStudent4Deadlines.size());

        studentsLogic.deleteStudentCascade(student4InCourse1.getCourse(), student4InCourse1.getEmail());

        // The student should have no more selective deadlines.
        Set<FeedbackSessionAttributes> newSessionsWithStudent4Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        assertTrue(newSessionsWithStudent4Deadlines.isEmpty());
        Map<FeedbackSessionAttributes, Integer> expectedSessionsDeadlineCounts = oldSessionsDeadlineCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() - 1));
        Map<FeedbackSessionAttributes, Integer> newSessionsDeadlineCounts = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(oldSessionsWithStudent4Deadlines::contains)
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getStudentDeadlines().size()));
        assertEquals(expectedSessionsDeadlineCounts, newSessionsDeadlineCounts);
    }

    @Test
    public void testDeleteStudentCascade() {
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        verifyPresentInDatabase(student2InCourse1);

        ______TS("delete non-existent student");

        // should fail silently.
        studentsLogic.deleteStudentCascade(student2InCourse1.getCourse(), student2InCourse1.getEmail());

        ______TS("typical delete");

        // the student has response
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        student2InCourse1.getCourse(), student2InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        student2InCourse1.getCourse(), student2InCourse1.getEmail()).isEmpty());

        studentsLogic.deleteStudentCascade(student2InCourse1.getCourse(), student2InCourse1.getEmail());

        verifyAbsentInDatabase(student2InCourse1);
        // verify responses of the student are gone
        assertTrue(
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        student2InCourse1.getCourse(), student2InCourse1.getEmail()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        student2InCourse1.getCourse(), student2InCourse1.getEmail()).isEmpty());

        // verify that other students in the course are intact
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        verifyPresentInDatabase(student1InCourse1);

        ______TS("null parameters");

        assertThrows(AssertionError.class,
                () -> studentsLogic.deleteStudentCascade(null, "valid@email.tmt"));
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

        studentsLogic.deleteStudentsForGoogleIdCascade(student1InCourse1.getGoogleId());

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

        studentsLogic.deleteStudentsForGoogleIdCascade("not_exist");

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
        studentsLogic.deleteStudentsInCourseCascade(courseId, deleteLimit);

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

        studentsLogic.deleteStudents(
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
        studentsLogic.deleteStudentsInCourseCascade("not_exist", deleteLimit);

        // other students are not affected
        var student1InCourse1 = dataBundle.students.get("student1InCourse1");
        assertNotNull(studentsLogic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail()));
    }

    @AfterClass
    public void classTearDown() {
        accountsLogic.deleteAccountCascade(dataBundle.students.get("student4InCourse1").getGoogleId());
    }
}
