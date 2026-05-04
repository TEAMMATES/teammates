package teammates.logic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.logic.entity.Team;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicTest extends BaseTestCase {

    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private FeedbackQuestionsDb fqDb;

    private UsersLogic usersLogic;

    @BeforeMethod
    public void setUpMethod() {
        fqDb = mock(FeedbackQuestionsDb.class);
        CoursesLogic coursesLogic = mock(CoursesLogic.class);
        usersLogic = mock(UsersLogic.class);
        FeedbackResponsesLogic frLogic = mock(FeedbackResponsesLogic.class);
        FeedbackSessionsLogic feedbackSessionsLogic = mock(FeedbackSessionsLogic.class);
        fqLogic.initLogicDependencies(fqDb, coursesLogic, frLogic, usersLogic, feedbackSessionsLogic);
    }

    @Test
    public void testGetFeedbackQuestionsForSession_questionNumbersInOrder_success() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);

        List<FeedbackQuestion> questions = createQuestionList(fs, 5);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questions);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(questions.size(), actualQuestions.size());
        assertTrue(questions.containsAll(actualQuestions));
    }

    @Test
    public void testGetFeedbackQuestionsForSession_questionNumbersOutOfOrder_success() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq5 = getTypicalFeedbackQuestionForSession(fs);

        fq1.setQuestionNumber(1);
        fq2.setQuestionNumber(2);
        fq3.setQuestionNumber(3);
        fq4.setQuestionNumber(4);
        fq5.setQuestionNumber(5);

        List<FeedbackQuestion> questions = new ArrayList<>(List.of(fq2, fq4, fq3, fq1, fq5));
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questions);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(questions.size(), actualQuestions.size());
        assertTrue(questions.containsAll(actualQuestions));
    }

    @Test
    public void testCreateFeedbackQuestion_questionNumbersAreConsistent_canCreateFeedbackQuestion()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion newQuestion = getTypicalFeedbackQuestionForSession(fs);

        newQuestion.setQuestionNumber(5);
        List<FeedbackQuestion> questionsBefore = createQuestionList(fs, 4);

        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);
        when(fqDb.createFeedbackQuestion(newQuestion)).thenReturn(newQuestion);

        FeedbackQuestion createdQuestion = fqLogic.createFeedbackQuestion(newQuestion);
        assertEquals(newQuestion, createdQuestion);
    }

    @Test
    public void testCreateFeedbackQuestion_questionNumbersAreInconsistent_canCreateFeedbackQuestion()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq5 = getTypicalFeedbackQuestionForSession(fs);
        fq1.setQuestionNumber(2);
        fq2.setQuestionNumber(3);
        fq3.setQuestionNumber(4);
        fq4.setQuestionNumber(5);

        List<FeedbackQuestion> questionsBefore = new ArrayList<>(List.of(fq1, fq2, fq3, fq4));
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);
        when(fqDb.createFeedbackQuestion(fq5)).thenReturn(fq5);

        FeedbackQuestion createdQuestion = fqLogic.createFeedbackQuestion(fq5);

        assertEquals(fq5, createdQuestion);
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_oldQuestionNumberLargerThanNewQuestionNumber_adjustQuestionNumberCorrectly()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq5 = getTypicalFeedbackQuestionForSession(fs);
        fq1.setQuestionNumber(2);
        fq2.setQuestionNumber(3);
        fq3.setQuestionNumber(4);
        fq4.setQuestionNumber(5);
        fq5.setQuestionNumber(1);

        List<FeedbackQuestion> questionsBefore = new ArrayList<>(List.of(fq1, fq2, fq3, fq4));
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);
        when(fqDb.createFeedbackQuestion(fq5)).thenReturn(fq5);

        fqLogic.createFeedbackQuestion(fq5);

        assertEquals(1, fq1.getQuestionNumber().intValue());
        assertEquals(2, fq2.getQuestionNumber().intValue());
        assertEquals(3, fq3.getQuestionNumber().intValue());
        assertEquals(4, fq4.getQuestionNumber().intValue());
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_oldQuestionNumberSmallerThanNewQuestionNumber_adjustQuestionNumberCorrectly()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq5 = getTypicalFeedbackQuestionForSession(fs);
        fq1.setQuestionNumber(0);
        fq2.setQuestionNumber(1);
        fq3.setQuestionNumber(2);
        fq4.setQuestionNumber(3);

        List<FeedbackQuestion> questionsBefore = new ArrayList<>(List.of(fq1, fq2, fq3, fq4));
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);
        when(fqDb.createFeedbackQuestion(fq5)).thenReturn(fq5);

        fqLogic.createFeedbackQuestion(fq5);

        assertEquals(1, fq1.getQuestionNumber().intValue());
        assertEquals(2, fq2.getQuestionNumber().intValue());
        assertEquals(3, fq3.getQuestionNumber().intValue());
        assertEquals(4, fq4.getQuestionNumber().intValue());
    }

    @Test
    public void testGetFeedbackQuestionsForStudents_success() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);

        List<FeedbackQuestion> questionsTeam = List.of(fq1, fq2);
        List<FeedbackQuestion> questionsStudent = List.of(fq3, fq4);

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4);

        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.TEAMS)).thenReturn(questionsTeam);
        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.STUDENTS)).thenReturn(questionsStudent);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForStudents(fs);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(expectedQuestions));
    }

    @Test
    public void testGetFeedbackQuestionsForInstructors_instructorIsCreator_success() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        fs.setCreatorEmail("instr1@teammates.tmt");
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);

        List<FeedbackQuestion> questionsInstructors = List.of(fq1, fq2);
        List<FeedbackQuestion> questionsSelf = List.of(fq3, fq4);

        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.INSTRUCTORS))
                .thenReturn(questionsInstructors);
        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.SELF)).thenReturn(questionsSelf);

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4);
        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(fs, "instr1@teammates.tmt");

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testGetFeedbackQuestionsForInstructors_instructorIsNotCreator_success() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        fs.setCreatorEmail("instr1@teammates.tmt");
        FeedbackQuestion fq1 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq2 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq3 = getTypicalFeedbackQuestionForSession(fs);
        FeedbackQuestion fq4 = getTypicalFeedbackQuestionForSession(fs);

        List<FeedbackQuestion> questionsInstructors = List.of(fq1, fq2);
        List<FeedbackQuestion> questionsSelf = List.of(fq3, fq4);

        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.INSTRUCTORS))
                .thenReturn(questionsInstructors);
        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.SELF)).thenReturn(questionsSelf);

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2);
        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(fs, "instr2@teammates.tmt");

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(actualQuestions));
    }

    @Test(enabled = false)
    public void testGetRecipientsOfQuestion_giverTypeStudents() {
        Course c = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(c);
        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);

        Student s1 = getTypicalStudent();
        Student s2 = getTypicalStudent();
        List<Student> studentsInCourse = List.of(s1, s2);

        CourseRoster courseRoster = new CourseRoster(studentsInCourse, null);

        when(usersLogic.getStudentsForCourse("course-1")).thenReturn(studentsInCourse);

        ______TS("response to students except self");
        assertEquals(fqLogic.getRecipientsOfQuestion(fq, null, s2, null).size(), studentsInCourse.size() - 1);
        assertEquals(fqLogic.getRecipientsOfQuestion(fq, null, s2, courseRoster).size(), studentsInCourse.size() - 1);

    }

    private List<FeedbackQuestion> createQuestionList(FeedbackSession fs, int numOfQuestions) {
        List<FeedbackQuestion> questions = new ArrayList<>();
        for (int i = 1; i <= numOfQuestions; i++) {
            FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
            fq.setQuestionNumber(i);
            questions.add(fq);
        }
        return questions;
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_mcqStudents_returnsSortedStudentOptions() {
        FeedbackMcqQuestionDetails mcqDetails =
                getMockMcqQuestionDetails(FeedbackParticipantType.STUDENTS);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(mcqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MCQ);

        Student student1 = getMockStudent(UUID.randomUUID(),
                "Charlie", "charlie@teammates.tmt", "Section A", "Team 2");
        Student student2 = getMockStudent(UUID.randomUUID(),
                "Alice", "alice@teammates.tmt", "Section A", "Team 1");

        when(usersLogic.getStudentsForCourse("course-1")).thenReturn(List.of(student1, student2));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, student1);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Alice (Team 1)", "Charlie (Team 2)"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_mcqStudentsExcludingSelf_excludesCurrentStudent() {
        FeedbackMcqQuestionDetails mcqDetails =
                getMockMcqQuestionDetails(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(mcqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MCQ);

        Student currentStudent = getMockStudent(UUID.randomUUID(),
                "Alice", "alice@teammates.tmt", "Section A", "Team 1");
        Student otherStudent = getMockStudent(UUID.randomUUID(),
                "Bob", "bob@teammates.tmt", "Section A", "Team 2");

        when(usersLogic.getStudentsForCourse("course-1")).thenReturn(List.of(currentStudent, otherStudent));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, currentStudent);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Bob (Team 2)"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_msqStudentsInSameSection_returnsSortedSectionStudentOptions() {
        FeedbackMsqQuestionDetails msqDetails =
                getMockMsqQuestionDetails(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(msqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MSQ);

        Student currentStudent = getMockStudent(UUID.randomUUID(),
                "Current", "current@teammates.tmt", "Section A", "Team 0");
        Student student1 = getMockStudent(UUID.randomUUID(),
                "Charlie", "charlie@teammates.tmt", "Section A", "Team 2");
        Student student2 = getMockStudent(UUID.randomUUID(),
                "Alice", "alice@teammates.tmt", "Section A", "Team 1");

        when(usersLogic.getStudentsForSection("Section A", "course-1")).thenReturn(List.of(student1, student2));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, currentStudent);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Alice (Team 1)", "Charlie (Team 2)"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_mcqOwnTeamMembersIncludingSelf_returnsAllTeamMemberNames() {
        FeedbackMcqQuestionDetails mcqDetails =
                getMockMcqQuestionDetails(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(mcqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MCQ);

        Student currentStudent = mock(Student.class);
        Team team = mock(Team.class);
        Student user1 = getMockStudent(UUID.randomUUID(), "Bob");
        Student user2 = getMockStudent(UUID.randomUUID(), "Alice");

        when(currentStudent.getTeam()).thenReturn(team);
        when(team.getUsers()).thenReturn(List.of(user1, user2));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, currentStudent);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Alice", "Bob"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_mcqOwnTeamMembers_excludesCurrentStudent() {
        FeedbackMcqQuestionDetails mcqDetails =
                getMockMcqQuestionDetails(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(mcqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MCQ);

        Student currentStudent = mock(Student.class);
        Team team = mock(Team.class);
        UUID currentStudentId = UUID.randomUUID();
        Student currentUser = getMockStudent(currentStudentId, "Alice");
        Student otherUser = getMockStudent(UUID.randomUUID(), "Bob");

        when(currentStudent.getId()).thenReturn(currentStudentId);
        when(currentStudent.getTeam()).thenReturn(team);
        when(team.getUsers()).thenReturn(List.of(currentUser, otherUser));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, currentStudent);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Bob"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_msqInstructors_returnsSortedInstructorNames() {
        FeedbackMsqQuestionDetails msqDetails =
                getMockMsqQuestionDetails(FeedbackParticipantType.INSTRUCTORS);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(msqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MSQ);

        Instructor instructor1 = mock(Instructor.class);
        Instructor instructor2 = mock(Instructor.class);
        when(instructor1.getName()).thenReturn("Charlie");
        when(instructor2.getName()).thenReturn("Alice");

        when(usersLogic.getInstructorsForCourse("course-1")).thenReturn(List.of(instructor1, instructor2));

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, null);

        assertTrue(actualOptions.isPresent());
        assertEquals(List.of("Alice", "Charlie"), actualOptions.get());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_mcqNone_returnsEmptyOptional() {
        FeedbackMcqQuestionDetails mcqDetails =
                getMockMcqQuestionDetails(FeedbackParticipantType.NONE);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(mcqDetails, "course-1");
        when(question.getQuestionType()).thenReturn(FeedbackQuestionType.MCQ);

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, null);

        assertTrue(actualOptions.isEmpty());
    }

    @Test
    public void testGetDynamicallyGeneratedOptions_nonMcqMsqQuestionType_returnsEmptyOptional() {
        FeedbackQuestionDetails questionDetails =
                mock(FeedbackQuestionDetails.class);
        FeedbackQuestion question = getMockFeedbackQuestionWithDetails(questionDetails, "course-1");

        when(question.getQuestionType()).thenReturn(
                FeedbackQuestionType.TEXT);

        Optional<List<String>> actualOptions = fqLogic.getDynamicallyGeneratedOptions(question, null);

        assertTrue(actualOptions.isEmpty());
    }

    private FeedbackQuestion getMockFeedbackQuestionWithDetails(FeedbackQuestionDetails questionDetails, String courseId) {
        FeedbackQuestion question = mock(FeedbackQuestion.class);
        when(question.getQuestionDetailsCopy()).thenReturn(questionDetails);
        when(question.getCourseId()).thenReturn(courseId);
        return question;
    }

    private FeedbackMcqQuestionDetails getMockMcqQuestionDetails(
            FeedbackParticipantType generateOptionsFor) {
        FeedbackMcqQuestionDetails mcqDetails =
                mock(FeedbackMcqQuestionDetails.class);
        when(mcqDetails.getGenerateOptionsFor()).thenReturn(generateOptionsFor);
        return mcqDetails;
    }

    private FeedbackMsqQuestionDetails getMockMsqQuestionDetails(
            FeedbackParticipantType generateOptionsFor) {
        FeedbackMsqQuestionDetails msqDetails =
                mock(FeedbackMsqQuestionDetails.class);
        when(msqDetails.getGenerateOptionsFor()).thenReturn(generateOptionsFor);
        return msqDetails;
    }

    private Student getMockStudent(UUID id, String name, String email, String sectionName, String teamName) {
        Student student = mock(Student.class);
        Team team = mock(Team.class);

        when(student.getId()).thenReturn(id);
        when(student.getName()).thenReturn(name);
        when(student.getEmail()).thenReturn(email);
        when(student.getSectionName()).thenReturn(sectionName);
        when(student.getTeamName()).thenReturn(teamName);
        when(student.getTeam()).thenReturn(team);
        when(team.getName()).thenReturn(teamName);

        return student;
    }

    private Student getMockStudent(UUID id, String name) {
        Student student = mock(Student.class);
        when(student.getId()).thenReturn(id);
        when(student.getName()).thenReturn(name);
        return student;
    }
}
