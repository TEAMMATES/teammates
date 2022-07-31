package teammates.logic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicTest extends BaseLogicTest {

    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected void prepareTestData() {
        // see beforeMethod()
    }

    @BeforeMethod
    public void beforeMethod() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
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

        fqLogic.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
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
    public void allTests() throws Exception {
        testHasFeedbackQuestionsForInstructor();
        testGetFeedbackQuestionsForInstructor();
        testHasFeedbackQuestionsForStudents();
        testGetFeedbackQuestionsForStudents();
        testAddQuestion();
    }

    @Test
    public void testGetRecipientsOfQuestion() throws Exception {
        FeedbackQuestionAttributes question;
        StudentAttributes studentGiver;
        InstructorAttributes instructorGiver;
        CourseRoster courseRoster;
        Map<String, FeedbackQuestionRecipient> recipients;

        ______TS("response to students, total 5");

        question = getQuestionFromDatabase("qn2InSession1InCourse1");
        studentGiver = dataBundle.students.get("student1InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.size(), 4); // 5 students minus giver himself
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.size(), 4); // should produce the same answer

        instructorGiver = dataBundle.instructors.get("instructor1OfCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(instructorGiver.getCourseId()),
                instructorsLogic.getInstructorsForCourse(instructorGiver.getCourseId()));

        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, null);
        assertEquals(recipients.size(), 5); // instructor is not student so he can respond to all 5.
        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, courseRoster);
        assertEquals(recipients.size(), 5); // should produce the same answer

        ______TS("response to instructors, total 3");

        question = getQuestionFromDatabase("qn2InSession1InCourse2");
        instructorGiver = dataBundle.instructors.get("instructor1OfCourse2");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(instructorGiver.getCourseId()),
                instructorsLogic.getInstructorsForCourse(instructorGiver.getCourseId()));

        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, null);
        assertEquals(recipients.size(), 2); // 3 - giver = 2
        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, courseRoster);
        assertEquals(recipients.size(), 2); // should produce the same answer

        ______TS("empty case: response to team members, but alone");

        question = getQuestionFromDatabase("team.members.feedback");
        studentGiver = dataBundle.students.get("student5InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.size(), 0);
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.size(), 0); // should produce the same answer

        ______TS("response from team to itself");

        question = getQuestionFromDatabase("graceperiod.session.feedbackFromTeamToSelf");
        studentGiver = dataBundle.students.get("student1InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.size(), 1);
        assertTrue(recipients.containsKey(studentGiver.getTeam()));
        assertEquals(recipients.get(studentGiver.getTeam()).getName(), studentGiver.getTeam());
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.size(), 1);
        assertTrue(recipients.containsKey(studentGiver.getTeam()));
        assertEquals(recipients.get(studentGiver.getTeam()).getName(), studentGiver.getTeam());

        ______TS("response to other teams from instructor");
        question = getQuestionFromDatabase("team.instructor.feedback");
        instructorGiver = dataBundle.instructors.get("instructor1OfCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, null);
        assertEquals(recipients.size(), 2);
        recipients = fqLogic.getRecipientsOfQuestion(question, instructorGiver, null, courseRoster);
        assertEquals(recipients.size(), 2);

        ______TS("special case: response to other team, instructor is also student");
        question = getQuestionFromDatabase("team.feedback");
        studentGiver = dataBundle.students.get("student1InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.size(), 1);
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.size(), 1);

        ______TS("to nobody (general feedback)");
        question = getQuestionFromDatabase("qn3InSession1InCourse1");
        studentGiver = dataBundle.students.get("student1InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.get(Const.GENERAL_QUESTION).getName(), Const.GENERAL_QUESTION);
        assertEquals(recipients.size(), 1);
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.get(Const.GENERAL_QUESTION).getName(), Const.GENERAL_QUESTION);
        assertEquals(recipients.size(), 1);

        ______TS("to self");
        question = getQuestionFromDatabase("qn1InSession1InCourse1");
        studentGiver = dataBundle.students.get("student1InCourse1");
        courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse(studentGiver.getCourse()),
                instructorsLogic.getInstructorsForCourse(studentGiver.getCourse()));

        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, null);
        assertEquals(recipients.get(studentGiver.getEmail()).getName(), FeedbackQuestionsLogic.USER_NAME_FOR_SELF);
        assertEquals(recipients.size(), 1);
        recipients = fqLogic.getRecipientsOfQuestion(question, null, studentGiver, courseRoster);
        assertEquals(recipients.get(studentGiver.getEmail()).getName(), FeedbackQuestionsLogic.USER_NAME_FOR_SELF);
        assertEquals(recipients.size(), 1);
    }

    @Test
    public void testUpdateQuestionCascade_shouldShiftQuestionNumberCorrectly() throws Exception {
        ______TS("shift question up");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatabase("qn1InSession1InCourse1");
        q1.setQuestionNumber(2);
        FeedbackQuestionAttributes q2 = getQuestionFromDatabase("qn2InSession1InCourse1");
        q2.setQuestionNumber(3);
        FeedbackQuestionAttributes q3 = getQuestionFromDatabase("qn3InSession1InCourse1");
        q3.setQuestionNumber(1);
        FeedbackQuestionAttributes q4 = getQuestionFromDatabase("qn4InSession1InCourse1");
        q4.setQuestionNumber(4);
        FeedbackQuestionAttributes q5 = getQuestionFromDatabase("qn5InSession1InCourse1");
        q5.setQuestionNumber(5);

        expectedList.add(q3);
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q4);
        expectedList.add(q5);

        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatabase("qn3InSession1InCourse1");
        questionToUpdate.setQuestionNumber(1);
        fqLogic.updateFeedbackQuestionCascade(
                FeedbackQuestionAttributes.updateOptionsBuilder(questionToUpdate.getId())
                        .withQuestionNumber(questionToUpdate.getQuestionNumber())
                        .build());

        List<FeedbackQuestionAttributes> actualList = fqLogic.getFeedbackQuestionsForSession(
                questionToUpdate.getFeedbackSessionName(), questionToUpdate.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("shift question down");
        expectedList = new ArrayList<>();
        q1 = getQuestionFromDatabase("qn1InSession1InCourse1");
        q1.setQuestionNumber(1);
        q2 = getQuestionFromDatabase("qn2InSession1InCourse1");
        q2.setQuestionNumber(2);
        q3 = getQuestionFromDatabase("qn3InSession1InCourse1");
        q3.setQuestionNumber(3);
        q4 = getQuestionFromDatabase("qn4InSession1InCourse1");
        q4.setQuestionNumber(4);
        q5 = getQuestionFromDatabase("qn5InSession1InCourse1");
        q5.setQuestionNumber(5);

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);

        questionToUpdate = getQuestionFromDatabase("qn3InSession1InCourse1");
        questionToUpdate.setQuestionNumber(3);
        fqLogic.updateFeedbackQuestionCascade(
                FeedbackQuestionAttributes.updateOptionsBuilder(questionToUpdate.getId())
                        .withQuestionNumber(questionToUpdate.getQuestionNumber())
                        .build());

        actualList = fqLogic.getFeedbackQuestionsForSession(
                questionToUpdate.getFeedbackSessionName(), questionToUpdate.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }

        ______TS("try to shift question up, invalid attributes, questions order remains");
        questionToUpdate = getQuestionFromDatabase("qn3InSession1InCourse1");
        questionToUpdate.setQuestionNumber(1);
        String questionId = questionToUpdate.getId();
        assertThrows(InvalidParametersException.class, () -> {
            fqLogic.updateFeedbackQuestionCascade(
                            FeedbackQuestionAttributes.updateOptionsBuilder(questionId)
                                    .withQuestionNumber(1)
                                    .withGiverType(FeedbackParticipantType.TEAMS)
                                    .withRecipientType(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                                    .build());
        });

        actualList = fqLogic.getFeedbackQuestionsForSession(
                questionToUpdate.getFeedbackSessionName(), questionToUpdate.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }

    private void testAddQuestion() throws Exception {

        ______TS("Add questions sequentially");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatabase("qn1InSession1InCourse1");
        q1.setQuestionNumber(1);
        FeedbackQuestionAttributes q2 = getQuestionFromDatabase("qn2InSession1InCourse1");
        q2.setQuestionNumber(2);
        FeedbackQuestionAttributes q3 = getQuestionFromDatabase("qn3InSession1InCourse1");
        q3.setQuestionNumber(3);
        FeedbackQuestionAttributes q4 = getQuestionFromDatabase("qn4InSession1InCourse1");
        q4.setQuestionNumber(4);
        FeedbackQuestionAttributes q5 = getQuestionFromDatabase("qn5InSession1InCourse1");
        q5.setQuestionNumber(5);
        FeedbackQuestionAttributes q6 = getQuestionFromDatabase("qn1InSession1InCourse1");
        q6.setQuestionNumber(6);

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        expectedList.add(q6);

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatabase("qn1InSession1InCourse1");
        newQuestion.setQuestionNumber(6);
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(q1.getFeedbackSessionName(), q1.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question to the front of the list");

        FeedbackQuestionAttributes q7 = getQuestionFromDatabase("qn4InSession1InCourse1");

        q7.setQuestionNumber(1);
        q1.setQuestionNumber(2);
        q2.setQuestionNumber(3);
        q3.setQuestionNumber(4);
        q4.setQuestionNumber(5);
        q5.setQuestionNumber(6);
        q6.setQuestionNumber(7);

        expectedList.add(0, q7);

        //Add a question to session1course1 and sets its number to 1
        newQuestion = getQuestionFromDatabase("qn4InSession1InCourse1");
        newQuestion.setQuestionNumber(1);
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.getFeedbackSessionName(), q1.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question inbetween 2 existing questions");

        FeedbackQuestionAttributes q8 = getQuestionFromDatabase("qn4InSession1InCourse1");
        q8.setQuestionNumber(3);
        q2.setQuestionNumber(4);
        q3.setQuestionNumber(5);
        q4.setQuestionNumber(6);
        q5.setQuestionNumber(7);
        q6.setQuestionNumber(8);

        expectedList.add(2, q8);

        //Add a question to session1course1 and place it between existing question 2 and 3
        newQuestion = getQuestionFromDatabase("qn4InSession1InCourse1");
        newQuestion.setQuestionNumber(3);
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.getFeedbackSessionName(), q1.getCourseId());

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }
    }

    @Test
    public void testUpdateQuestionCascade() throws Exception {
        ______TS("standard update, no existing responses");
        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatabase("qn2InSession1InCourse2");

        FeedbackQuestionDetails fqd = new FeedbackTextQuestionDetails("new question text");
        questionToUpdate.setQuestionDetails(fqd);
        questionToUpdate.setQuestionNumber(3);
        List<FeedbackParticipantType> newVisibility = new LinkedList<>();
        newVisibility.add(FeedbackParticipantType.INSTRUCTORS);
        questionToUpdate.setShowResponsesTo(newVisibility);

        FeedbackQuestionAttributes updatedQuestion = fqLogic.updateFeedbackQuestionCascade(
                FeedbackQuestionAttributes.updateOptionsBuilder(questionToUpdate.getId())
                        .withQuestionDetails(fqd)
                        .withQuestionNumber(questionToUpdate.getQuestionNumber())
                        .withShowResponsesTo(questionToUpdate.getShowResponsesTo())
                        .build());

        FeedbackQuestionAttributes actualQuestion =
                fqLogic.getFeedbackQuestion(questionToUpdate.getId());
        assertEquals(questionToUpdate.toString(), actualQuestion.toString());
        assertEquals(questionToUpdate.toString(), updatedQuestion.toString());

        ______TS("cascading update, non-destructive changes, existing responses are preserved");
        questionToUpdate = getQuestionFromDatabase("qn2InSession1InCourse1");
        fqd = new FeedbackTextQuestionDetails("new question text 2");
        questionToUpdate.setQuestionDetails(fqd);
        questionToUpdate.setNumberOfEntitiesToGiveFeedbackTo(2);

        int numberOfResponses =
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size();

        fqLogic.updateFeedbackQuestionCascade(
                FeedbackQuestionAttributes.updateOptionsBuilder(questionToUpdate.getId())
                        .withQuestionDetails(fqd)
                        .withNumberOfEntitiesToGiveFeedbackTo(questionToUpdate.getNumberOfEntitiesToGiveFeedbackTo())
                        .build());
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size(), numberOfResponses);

        ______TS("cascading update, destructive changes, delete all existing responses");
        questionToUpdate = getQuestionFromDatabase("qn2InSession1InCourse1");
        fqd = new FeedbackTextQuestionDetails("new question text 3");
        questionToUpdate.setQuestionDetails(fqd);
        questionToUpdate.setRecipientType(FeedbackParticipantType.INSTRUCTORS);

        assertFalse(frLogic.getFeedbackResponsesForQuestion(questionToUpdate.getId()).isEmpty());

        fqLogic.updateFeedbackQuestionCascade(
                FeedbackQuestionAttributes.updateOptionsBuilder(questionToUpdate.getId())
                        .withQuestionDetails(fqd)
                        .withRecipientType(questionToUpdate.getRecipientType())
                        .build());
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(frLogic.getFeedbackResponsesForQuestion(
                questionToUpdate.getId()).size(), 0);

        ______TS("failure: question does not exist");

        questionToUpdate = getQuestionFromDatabase("qn3InSession1InCourse1");
        FeedbackQuestionAttributes[] finalFq = new FeedbackQuestionAttributes[] { questionToUpdate };
        fqLogic.deleteFeedbackQuestionCascade(questionToUpdate.getId());

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fqLogic.updateFeedbackQuestionCascade(
                        FeedbackQuestionAttributes.updateOptionsBuilder(finalFq[0].getId())
                                .withQuestionDetails(new FeedbackTextQuestionDetails("test"))
                                .build()));
        assertEquals("Trying to update a feedback question that does not exist.", ednee.getMessage());

        ______TS("failure: invalid parameters");

        questionToUpdate = getQuestionFromDatabase("qn3InSession1InCourse1");
        questionToUpdate.setGiverType(FeedbackParticipantType.TEAMS);
        questionToUpdate.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        finalFq[0] = questionToUpdate;
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> fqLogic.updateFeedbackQuestionCascade(
                        FeedbackQuestionAttributes.updateOptionsBuilder(finalFq[0].getId())
                                .withGiverType(finalFq[0].getGiverType())
                                .withRecipientType(finalFq[0].getRecipientType())
                                .build()));
        assertEquals(
                String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                        "Giver's team members",
                        "Teams in this course"),
                ipe.getMessage());
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

        fqLogic.deleteFeedbackQuestionCascade(typicalQuestion.getId());

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
        fqLogic.deleteFeedbackQuestionCascade("non-existent-question-id");

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
        fqLogic.deleteFeedbackQuestionCascade(responseInDb.getFeedbackQuestionId());

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

        fqLogic.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
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
    public void testPopulateFieldsToGenerateInQuestion_mcqQuestionDifferentGenerateOptions_shouldPopulateCorrectly() {
        StudentAttributes typicalStudent = dataBundle.students.get("student1InCourse1");
        InstructorAttributes typicalInstructor = dataBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestionAttributes fqa = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        // construct a typical question
        fqa = FeedbackQuestionAttributes.builder()
                        .withCourseId(fqa.getCourseId())
                        .withFeedbackSessionName(fqa.getFeedbackSessionName())
                        .withNumberOfEntitiesToGiveFeedbackTo(2)
                        .withQuestionDescription("test")
                        .withQuestionNumber(fqa.getQuestionNumber())
                        .withGiverType(FeedbackParticipantType.STUDENTS)
                        .withRecipientType(FeedbackParticipantType.STUDENTS)
                        .withQuestionDetails(new FeedbackMcqQuestionDetails())
                        .withShowResponsesTo(new ArrayList<>())
                        .withShowGiverNameTo(new ArrayList<>())
                        .withShowRecipientNameTo(new ArrayList<>())
                        .build();

        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();

        // NONE
        List<String> expected = Arrays.asList("test");

        feedbackMcqQuestionDetails.setMcqChoices(Arrays.asList("test"));
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(Arrays.asList("test"));
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // STUDENTS
        expected = Arrays.asList("student1 In Course1</td></div>'\" (Team 1.1</td></div>'\")",
                        "student2 In Course1 (Team 1.1</td></div>'\")",
                        "student3 In Course1 (Team 1.1</td></div>'\")",
                        "student4 In Course1 (Team 1.1</td></div>'\")",
                        "student5 In Course1 (Team 1.2)");

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // STUDENTS_IN_SAME_SECTION
        expected = Arrays.asList("student1 In Course1</td></div>'\" (Team 1.1</td></div>'\")",
                        "student2 In Course1 (Team 1.1</td></div>'\")",
                        "student3 In Course1 (Team 1.1</td></div>'\")",
                        "student4 In Course1 (Team 1.1</td></div>'\")");

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // STUDENTS_EXCLUDING_SELF
        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(Arrays.asList("student2 In Course1 (Team 1.1</td></div>'\")",
                "student3 In Course1 (Team 1.1</td></div>'\")",
                "student4 In Course1 (Team 1.1</td></div>'\")",
                "student5 In Course1 (Team 1.2)"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(Arrays.asList("student1 In Course1</td></div>'\" (Team 1.1</td></div>'\")",
                "student2 In Course1 (Team 1.1</td></div>'\")",
                "student3 In Course1 (Team 1.1</td></div>'\")",
                "student4 In Course1 (Team 1.1</td></div>'\")",
                "student5 In Course1 (Team 1.2)"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // TEAM MEMBERS
        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(Arrays.asList("student1 In Course1</td></div>'\"",
                "student2 In Course1",
                "student3 In Course1",
                "student4 In Course1"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(new ArrayList<>(), ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // TEAM MEMBERS EXCLUDING SELF
        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(Arrays.asList("student2 In Course1",
                "student3 In Course1",
                "student4 In Course1"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(new ArrayList<>(), ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // TEAMS
        expected = Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2");

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // TEAMS_IN_SAME_SECTION
        expected = Arrays.asList("Team 1.1</td></div>'\"");

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS_IN_SAME_SECTION);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // TEAMS_EXCLUDING_SELF
        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(Arrays.asList("Team 1.2"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        // INSTRUCTORS
        expected = Arrays.asList("Helper Course1",
                "Instructor Not Yet Joined Course 1",
                "Instructor1 Course1",
                "Instructor2 Course1",
                "Instructor3 Course1");

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.INSTRUCTORS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());

        feedbackMcqQuestionDetails.setMcqChoices(new ArrayList<>());
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.INSTRUCTORS);
        fqa.setQuestionDetails(feedbackMcqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(expected, ((FeedbackMcqQuestionDetails) fqa.getQuestionDetailsCopy()).getMcqChoices());
    }

    @Test
    public void testPopulateFieldsToGenerateInQuestion_msqQuestionDifferentGenerateOptions_shouldPopulateCorrectly() {
        StudentAttributes typicalStudent = dataBundle.students.get("student1InCourse1");
        InstructorAttributes typicalInstructor = dataBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestionAttributes fqa = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        // construct a typical question
        fqa = FeedbackQuestionAttributes.builder()
                .withCourseId(fqa.getCourseId())
                .withFeedbackSessionName(fqa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(fqa.getQuestionNumber())
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withQuestionDetails(new FeedbackMsqQuestionDetails())
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build();

        FeedbackMsqQuestionDetails feedbackMsqQuestionDetails = new FeedbackMsqQuestionDetails();

        // TEAMS_EXCLUDING_SELF
        feedbackMsqQuestionDetails.setMsqChoices(new ArrayList<>());
        feedbackMsqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMsqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalStudent.getEmail(), typicalStudent.getTeam());
        assertEquals(Arrays.asList("Team 1.2"),
                ((FeedbackMsqQuestionDetails) fqa.getQuestionDetailsCopy()).getMsqChoices());

        feedbackMsqQuestionDetails.setMsqChoices(new ArrayList<>());
        feedbackMsqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS_EXCLUDING_SELF);
        fqa.setQuestionDetails(feedbackMsqQuestionDetails);

        fqLogic.populateFieldsToGenerateInQuestion(fqa, typicalInstructor.getEmail(), null);
        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMsqQuestionDetails) fqa.getQuestionDetailsCopy()).getMsqChoices());
    }

    @Test
    public void testBuildCompleteGiverRecipientMap_studentQuestion_shouldBuildMapCorrectly() {
        CourseRoster courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse("idOfTypicalCourse1"),
                instructorsLogic.getInstructorsForCourse("idOfTypicalCourse1"));
        FeedbackQuestionAttributes qn1InSession1InCourse1 = getQuestionFromDatabase("qn1InSession1InCourse1");

        Map<String, Set<String>> completeGiverRecipientMap =
                fqLogic.buildCompleteGiverRecipientMap(qn1InSession1InCourse1, courseRoster);

        assertEquals(5, completeGiverRecipientMap.size());
        assertEquals(1, completeGiverRecipientMap.get("student1InCourse1@gmail.tmt").size());
        assertTrue(completeGiverRecipientMap.get("student1InCourse1@gmail.tmt").contains("student1InCourse1@gmail.tmt"));
        assertEquals(1, completeGiverRecipientMap.get("student2InCourse1@gmail.tmt").size());
        assertTrue(completeGiverRecipientMap.get("student2InCourse1@gmail.tmt").contains("student2InCourse1@gmail.tmt"));
        assertEquals(1, completeGiverRecipientMap.get("student3InCourse1@gmail.tmt").size());
        assertTrue(completeGiverRecipientMap.get("student3InCourse1@gmail.tmt").contains("student3InCourse1@gmail.tmt"));
        assertEquals(1, completeGiverRecipientMap.get("student4InCourse1@gmail.tmt").size());
        assertTrue(completeGiverRecipientMap.get("student4InCourse1@gmail.tmt").contains("student4InCourse1@gmail.tmt"));
        assertEquals(1, completeGiverRecipientMap.get("student5InCourse1@gmail.tmt").size());
        assertTrue(completeGiverRecipientMap.get("student5InCourse1@gmail.tmt").contains("student5InCourse1@gmail.tmt"));
    }

    @Test
    public void testBuildCompleteGiverRecipientMap_instructorQuestion_shouldBuildMapCorrectly() {
        CourseRoster courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse("idOfTypicalCourse1"),
                instructorsLogic.getInstructorsForCourse("idOfTypicalCourse1"));
        FeedbackQuestionAttributes qn4InSession1InCourse1 = getQuestionFromDatabase("qn4InSession1InCourse1");

        Map<String, Set<String>> completeGiverRecipientMap =
                fqLogic.buildCompleteGiverRecipientMap(qn4InSession1InCourse1, courseRoster);

        assertEquals(5, completeGiverRecipientMap.size());
        assertEquals(1, completeGiverRecipientMap.get("instructor1@course1.tmt").size());
        assertTrue(completeGiverRecipientMap.get("instructor1@course1.tmt").contains(Const.GENERAL_QUESTION));
        assertEquals(1, completeGiverRecipientMap.get("instructor2@course1.tmt").size());
        assertTrue(completeGiverRecipientMap.get("instructor2@course1.tmt").contains(Const.GENERAL_QUESTION));
        assertEquals(1, completeGiverRecipientMap.get("instructor3@course1.tmt").size());
        assertTrue(completeGiverRecipientMap.get("instructor3@course1.tmt").contains(Const.GENERAL_QUESTION));
        assertEquals(1, completeGiverRecipientMap.get("helper@course1.tmt").size());
        assertTrue(completeGiverRecipientMap.get("helper@course1.tmt").contains(Const.GENERAL_QUESTION));
        assertEquals(1, completeGiverRecipientMap.get("instructorNotYetJoinedCourse1@email.tmt").size());
        assertTrue(completeGiverRecipientMap.get("instructorNotYetJoinedCourse1@email.tmt")
                .contains(Const.GENERAL_QUESTION));
    }

    @Test
    public void testBuildCompleteGiverRecipientMap_selfQuestion_shouldBuildMapCorrectly() {
        CourseRoster courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse("idOfTypicalCourse1"),
                instructorsLogic.getInstructorsForCourse("idOfTypicalCourse1"));
        FeedbackQuestionAttributes qn3InSession1InCourse1 = getQuestionFromDatabase("qn3InSession1InCourse1");
        FeedbackSessionAttributes session1 = fsLogic.getFeedbackSession(
                qn3InSession1InCourse1.getFeedbackSessionName(), qn3InSession1InCourse1.getCourseId());

        Map<String, Set<String>> completeGiverRecipientMap =
                fqLogic.buildCompleteGiverRecipientMap(qn3InSession1InCourse1, courseRoster);

        assertEquals(1, completeGiverRecipientMap.size());
        assertEquals(1, completeGiverRecipientMap.get(session1.getCreatorEmail()).size());
        assertTrue(completeGiverRecipientMap.get(session1.getCreatorEmail()).contains(Const.GENERAL_QUESTION));
    }

    @Test
    public void testBuildCompleteGiverRecipientMap_teamQuestion_shouldBuildMapCorrectly() {
        CourseRoster courseRoster = new CourseRoster(
                studentsLogic.getStudentsForCourse("idOfTypicalCourse1"),
                instructorsLogic.getInstructorsForCourse("idOfTypicalCourse1"));
        FeedbackQuestionAttributes teamFeedbackQuestion = getQuestionFromDatabase("team.feedback");

        Map<String, Set<String>> completeGiverRecipientMap =
                fqLogic.buildCompleteGiverRecipientMap(teamFeedbackQuestion, courseRoster);

        assertEquals(2, completeGiverRecipientMap.size());
        assertEquals(1, completeGiverRecipientMap.get("Team 1.1</td></div>'\"").size());
        assertTrue(completeGiverRecipientMap.get("Team 1.1</td></div>'\"").contains("Team 1.2"));
        assertEquals(1, completeGiverRecipientMap.get("Team 1.2").size());
        assertTrue(completeGiverRecipientMap.get("Team 1.2").contains("Team 1.1</td></div>'\""));
    }

    private void testHasFeedbackQuestionsForInstructor() {
        ______TS("Valid session valid instructor should have questions");
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        assertTrue(fqLogic.hasFeedbackQuestionsForInstructors(fsa, false));

        ______TS("Valid session without questions for instructor should return false");
        fsa = fsLogic.getFeedbackSession("session without instructor questions", "idOfArchivedCourse");
        assertFalse(fqLogic.hasFeedbackQuestionsForInstructors(fsa, false));

        ______TS("Invalid session should not have questions");
        fsa.setFeedbackSessionName("non-existent session");
        assertFalse(fqLogic.hasFeedbackQuestionsForInstructors(fsa, false));
    }

    private void testGetFeedbackQuestionsForInstructor() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;

        ______TS("Get questions created for instructors and self");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn3InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatabase("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatabase("qn5InSession1InCourse1"));
        actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(
                "First feedback session", "idOfTypicalCourse1", "instructor1@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors and self by another instructor");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn4InSession1InCourse1"));
        actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(
                "First feedback session", "idOfTypicalCourse1", "instructor2@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors by the creating instructor");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn1InSession1InCourse2"));
        expectedQuestions.add(getQuestionFromDatabase("qn2InSession1InCourse2"));

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(
                "Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors not by the creating instructor");

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(
                "Instructor feedback session", "idOfTypicalCourse2", "instructor2@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for self from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatabase("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatabase("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatabase("qn3InSession1InCourse1"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn3InSession1InCourse1"));

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(allQuestions, true);

        assertEquals(actualQuestions, expectedQuestions);
    }

    private void testHasFeedbackQuestionsForStudents() {
        ______TS("Valid session should have questions");
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        assertTrue(fqLogic.hasFeedbackQuestionsForStudents(fsa));

        ______TS("Valid session without questions for students should return false");
        fsa = fsLogic.getFeedbackSession("session without student questions", "idOfArchivedCourse");
        assertFalse(fqLogic.hasFeedbackQuestionsForStudents(fsa));

        ______TS("Invalid session should not have questions");
        fsa.setFeedbackSessionName("non-existent session");
        assertFalse(fqLogic.hasFeedbackQuestionsForStudents(fsa));
    }

    private void testGetFeedbackQuestionsForStudents() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;

        ______TS("Get questions created for students");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatabase("qn2InSession1InCourse1"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("First feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students and teams");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("team.feedback"));
        expectedQuestions.add(getQuestionFromDatabase("team.members.feedback"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("Second feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatabase("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatabase("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatabase("qn3InSession1InCourse1"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatabase("qn2InSession1InCourse1"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students and teams from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatabase("team.feedback"));
        allQuestions.add(getQuestionFromDatabase("team.members.feedback"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatabase("team.feedback"));
        expectedQuestions.add(getQuestionFromDatabase("team.members.feedback"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);

        assertEquals(actualQuestions, expectedQuestions);
    }

    private FeedbackQuestionAttributes getQuestionFromDatabase(String questionKey) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionKey);
        question = fqLogic.getFeedbackQuestion(
                question.getFeedbackSessionName(), question.getCourseId(), question.getQuestionNumber());
        return question;
    }

}
