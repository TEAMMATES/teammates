package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
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

        List<FeedbackQuestion> questionsSelf = List.of(fq1, fq2);
        List<FeedbackQuestion> questionsStudent = List.of(fq3, fq4);

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4);

        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.SELF)).thenReturn(questionsSelf);
        when(fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.STUDENTS)).thenReturn(questionsStudent);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForStudents(fs);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(actualQuestions));
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

        SqlCourseRoster courseRoster = new SqlCourseRoster(studentsInCourse, null);

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
}
