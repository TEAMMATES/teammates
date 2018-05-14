package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicTest extends BaseLogicTest {

    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Test
    public void allTests() throws Exception {
        testGetRecipientsForQuestion();
        testGetFeedbackQuestionsForInstructor();
        testGetFeedbackQuestionsForStudents();
        testIsQuestionHasResponses();
        testIsQuestionAnswered();
        testUpdateQuestionNumber();
        testAddQuestion();
        testCopyQuestion();
        testUpdateQuestion();
        testDeleteQuestion();
        testAddQuestionNoIntegrityCheck();
        testDeleteQuestionsForCourse();
    }

    private void testGetRecipientsForQuestion() throws Exception {
        FeedbackQuestionAttributes question;
        String email;
        Map<String, String> recipients;

        ______TS("response to students, total 5");

        question = getQuestionFromDatastore("qn2InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 4); // 5 students minus giver himself

        email = dataBundle.instructors.get("instructor1OfCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 5); // instructor is not student so he can respond to all 5.

        ______TS("response to instructors, total 3");

        question = getQuestionFromDatastore("qn2InSession1InCourse2");
        email = dataBundle.instructors.get("instructor1OfCourse2").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 2); // 3 - giver = 2

        ______TS("empty case: response to team members, but alone");

        question = getQuestionFromDatastore("team.members.feedback");
        email = dataBundle.students.get("student5InCourse1").email;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 0);

        ______TS("response from team to itself");

        question = getQuestionFromDatastore("graceperiod.session.feedbackFromTeamToSelf");
        email = dataBundle.students.get("student1InCourse1").email;
        String teamName = dataBundle.students.get("student1InCourse1").team;
        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.size(), 1);
        assertTrue(recipients.containsKey(teamName));
        assertEquals(recipients.get(teamName), teamName);

        ______TS("special case: response to other team, instructor is also student");
        question = getQuestionFromDatastore("team.feedback");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);

        assertEquals(recipients.size(), 1);

        ______TS("to nobody (general feedback)");
        question = getQuestionFromDatastore("qn3InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.get(Const.GENERAL_QUESTION), Const.GENERAL_QUESTION);
        assertEquals(recipients.size(), 1);

        ______TS("to self");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        email = dataBundle.students.get("student1InCourse1").email;
        AccountsLogic.inst().makeAccountInstructor(dataBundle.students.get("student1InCourse1").googleId);

        recipients = fqLogic.getRecipientsForQuestion(question, email);
        assertEquals(recipients.get(email), Const.USER_NAME_FOR_SELF);
        assertEquals(recipients.size(), 1);

    }

    private void testUpdateQuestionNumber() throws Exception {
        ______TS("shift question up");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 2;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 3;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 1;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        FeedbackQuestionAttributes q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;

        expectedList.add(q3);
        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q4);
        expectedList.add(q5);

        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.questionNumber = 1;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("shift question down");
        expectedList = new ArrayList<>();
        q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.questionNumber = 3;
        fqLogic.updateFeedbackQuestionNumber(questionToUpdate);

        actualList = fqLogic.getFeedbackQuestionsForSession(questionToUpdate.feedbackSessionName, questionToUpdate.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }

    private void testAddQuestion() throws Exception {

        ______TS("Add question for feedback session that does not exist");
        FeedbackQuestionAttributes question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.feedbackSessionName = "non-existent Feedback Session";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), "Session disappeared.");
        }

        ______TS("Add question for course that does not exist");
        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        question.courseId = "non-existent course id";
        question.setId(null);
        try {
            fqLogic.createFeedbackQuestion(question);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), "Session disappeared.");
        }

        ______TS("Add questions sequentially");
        List<FeedbackQuestionAttributes> expectedList = new ArrayList<>();
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;
        FeedbackQuestionAttributes q2 = getQuestionFromDatastore("qn2InSession1InCourse1");
        q2.questionNumber = 2;
        FeedbackQuestionAttributes q3 = getQuestionFromDatastore("qn3InSession1InCourse1");
        q3.questionNumber = 3;
        FeedbackQuestionAttributes q4 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q4.questionNumber = 4;
        FeedbackQuestionAttributes q5 = getQuestionFromDatastore("qn5InSession1InCourse1");
        q5.questionNumber = 5;
        FeedbackQuestionAttributes q6 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q6.questionNumber = 6;

        expectedList.add(q1);
        expectedList.add(q2);
        expectedList.add(q3);
        expectedList.add(q4);
        expectedList.add(q5);
        expectedList.add(q6);

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatastore("qn1InSession1InCourse1");
        newQuestion.questionNumber = 6;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question to the front of the list");

        FeedbackQuestionAttributes q7 = getQuestionFromDatastore("qn4InSession1InCourse1");

        q7.questionNumber = 1;
        q1.questionNumber = 2;
        q2.questionNumber = 3;
        q3.questionNumber = 4;
        q4.questionNumber = 5;
        q5.questionNumber = 6;
        q6.questionNumber = 7;

        expectedList.add(0, q7);

        //Add a question to session1course1 and sets its number to 1
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 1;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }

        ______TS("add new question inbetween 2 existing questions");

        FeedbackQuestionAttributes q8 = getQuestionFromDatastore("qn4InSession1InCourse1");
        q8.questionNumber = 3;
        q2.questionNumber = 4;
        q3.questionNumber = 5;
        q4.questionNumber = 6;
        q5.questionNumber = 7;
        q6.questionNumber = 8;

        expectedList.add(2, q8);

        //Add a question to session1course1 and place it between existing question 2 and 3
        newQuestion = getQuestionFromDatastore("qn4InSession1InCourse1");
        newQuestion.questionNumber = 3;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestion(newQuestion);

        actualList = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), expectedList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(actualList.get(i), expectedList.get(i));
        }
    }

    private void testCopyQuestion() throws Exception {

        InstructorAttributes instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        ______TS("Typical case: copy question successfully");

        FeedbackQuestionAttributes question1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        question1 = fqLogic.getFeedbackQuestion(question1.feedbackSessionName, question1.courseId, question1.questionNumber);

        FeedbackQuestionAttributes copiedQuestion =
                fqLogic.copyFeedbackQuestion(question1.getId(), question1.feedbackSessionName, question1.courseId,
                                             instructor2OfCourse1.email);

        FeedbackQuestionDetails question1Details = question1.getQuestionDetails();
        FeedbackQuestionDetails copiedQuestionDetails = copiedQuestion.getQuestionDetails();

        assertEquals(question1.numberOfEntitiesToGiveFeedbackTo, copiedQuestion.numberOfEntitiesToGiveFeedbackTo);
        assertEquals(question1.questionType, copiedQuestion.questionType);
        assertEquals(question1.giverType, copiedQuestion.giverType);
        assertEquals(question1.recipientType, copiedQuestion.recipientType);
        assertEquals(question1Details.getQuestionText(), copiedQuestionDetails.getQuestionText());

    }

    private void testUpdateQuestion() throws Exception {
        ______TS("standard update, no existing responses, with 'keep existing' policy");
        FeedbackQuestionAttributes questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse2");
        questionToUpdate.questionMetaData = new Text("new question text");
        questionToUpdate.questionNumber = 3;
        List<FeedbackParticipantType> newVisibility = new LinkedList<>();
        newVisibility.add(FeedbackParticipantType.INSTRUCTORS);
        questionToUpdate.showResponsesTo = newVisibility;
        // Check keep existing policy.
        String originalCourseId = questionToUpdate.courseId;
        questionToUpdate.courseId = null;

        fqLogic.updateFeedbackQuestion(questionToUpdate);

        questionToUpdate.courseId = originalCourseId;

        FeedbackQuestionAttributes updatedQuestion =
                fqLogic.getFeedbackQuestion(questionToUpdate.getId());
        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());

        ______TS("cascading update, non-destructive changes, existing responses are preserved");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 2");
        questionToUpdate.numberOfEntitiesToGiveFeedbackTo = 2;

        int numberOfResponses =
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size();

        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(
                frLogic.getFeedbackResponsesForQuestion(
                        questionToUpdate.getId()).size(), numberOfResponses);

        ______TS("cascading update, destructive changes, delete all existing responses");
        questionToUpdate = getQuestionFromDatastore("qn2InSession1InCourse1");
        questionToUpdate.questionMetaData = new Text("new question text 3");
        questionToUpdate.recipientType = FeedbackParticipantType.INSTRUCTORS;

        assertFalse(frLogic.getFeedbackResponsesForQuestion(questionToUpdate.getId()).isEmpty());

        fqLogic.updateFeedbackQuestion(questionToUpdate);
        updatedQuestion = fqLogic.getFeedbackQuestion(questionToUpdate.getId());

        assertEquals(updatedQuestion.toString(), questionToUpdate.toString());
        assertEquals(frLogic.getFeedbackResponsesForQuestion(
                questionToUpdate.getId()).size(), 0);

        ______TS("failure: question does not exist");

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        fqLogic.deleteFeedbackQuestionCascade(questionToUpdate.getId());

        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected EntityDoesNotExistException not caught.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to update a feedback question that does not exist.");
        }

        ______TS("failure: invalid parameters");

        questionToUpdate = getQuestionFromDatastore("qn3InSession1InCourse1");
        questionToUpdate.giverType = FeedbackParticipantType.TEAMS;
        questionToUpdate.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;
        try {
            fqLogic.updateFeedbackQuestion(questionToUpdate);
            signalFailureToDetectException("Expected InvalidParametersException not caught.");
        } catch (InvalidParametersException e) {
            assertEquals(e.getMessage(), String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                                       questionToUpdate.recipientType.toDisplayRecipientName(),
                                                       questionToUpdate.giverType.toDisplayGiverName()));
        }
    }

    private void testDeleteQuestion() {
        //Success case already tested in update
        ______TS("question already does not exist, silently fail");

        fqLogic.deleteFeedbackQuestionCascade("non-existent-question-id");
        //No error should be thrown.

    }

    private void testDeleteQuestionsForCourse() throws EntityDoesNotExistException {
        ______TS("standard case");

        // test that questions are deleted
        String courseId = "idOfTypicalCourse2";
        FeedbackQuestionAttributes deletedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertNotNull(deletedQuestion);

        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertFalse(questions.isEmpty());

        fqLogic.deleteFeedbackQuestionsForCourse(courseId);
        deletedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertNull(deletedQuestion);

        questions = fqLogic.getFeedbackQuestionsForSession("Instructor feedback session", courseId);
        assertEquals(0, questions.size());

        // test that questions in other courses are unaffected
        assertNotNull(getQuestionFromDatastore("qn1InSessionInArchivedCourse"));
        assertNotNull(getQuestionFromDatastore("qn1InSession4InCourse1"));
    }

    private void testGetFeedbackQuestionsForInstructor() throws Exception {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;

        ______TS("Get questions created for instructors and self");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn5InSession1InCourse1"));
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1",
                                                          "instructor1@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors and self by another instructor");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn4InSession1InCourse1"));
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("First feedback session", "idOfTypicalCourse1",
                                                          "instructor2@course1.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors by the creating instructor");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse2"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse2"));

        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "instructor1@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions for creator instructor, verifies that the two methods return the same questions");
        expectedQuestions = new ArrayList<>(actualQuestions);
        actualQuestions =
                fqLogic.getFeedbackQuestionsForCreatorInstructor("Instructor feedback session", "idOfTypicalCourse2");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors not by the creating instructor");

        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "instructor2@course2.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for instructors by non-instructor of the course");

        expectedQuestions = new ArrayList<>();
        actualQuestions =
                fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse2",
                                                          "iwc@yahoo.tmt");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Failure: Getting questions for a non-existent session");

        try {
            fqLogic.getFeedbackQuestionsForInstructor("Instructor feedback session", "idOfTypicalCourse1",
                                                      "instructor1@course1.tmt");
            signalFailureToDetectException("Allowed to get questions for a feedback session that does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals(e.getMessage(), "Trying to get questions for a feedback session that does not exist.");
        }

        ______TS("Get questions created for self from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));

        actualQuestions = fqLogic.getFeedbackQuestionsForInstructor(allQuestions, true);

        assertEquals(actualQuestions, expectedQuestions);
    }

    private void testGetFeedbackQuestionsForStudents() {
        List<FeedbackQuestionAttributes> expectedQuestions;
        List<FeedbackQuestionAttributes> actualQuestions;
        List<FeedbackQuestionAttributes> allQuestions;

        ______TS("Get questions created for students");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("First feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students and teams");

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));
        actualQuestions =
                    fqLogic.getFeedbackQuestionsForStudents("Second feedback session", "idOfTypicalCourse1");

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));
        allQuestions.add(getQuestionFromDatastore("qn3InSession1InCourse1"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("qn1InSession1InCourse1"));
        expectedQuestions.add(getQuestionFromDatastore("qn2InSession1InCourse1"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);

        assertEquals(actualQuestions, expectedQuestions);

        ______TS("Get questions created for students and teams from list of all questions");

        allQuestions = new ArrayList<>();
        allQuestions.add(getQuestionFromDatastore("team.feedback"));
        allQuestions.add(getQuestionFromDatastore("team.members.feedback"));

        expectedQuestions = new ArrayList<>();
        expectedQuestions.add(getQuestionFromDatastore("team.feedback"));
        expectedQuestions.add(getQuestionFromDatastore("team.members.feedback"));

        actualQuestions = fqLogic.getFeedbackQuestionsForStudents(allQuestions);

        assertEquals(actualQuestions, expectedQuestions);
    }

    private void testIsQuestionHasResponses() {
        FeedbackQuestionAttributes questionWithResponse;
        FeedbackQuestionAttributes questionWithoutResponse;

        ______TS("Check that a question has some responses");

        questionWithResponse = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(fqLogic.areThereResponsesForQuestion(questionWithResponse.getId()));

        ______TS("Check that a question has no responses");

        questionWithoutResponse = getQuestionFromDatastore("qn2InSession1InCourse2");
        assertFalse(fqLogic.areThereResponsesForQuestion(questionWithoutResponse.getId()));
    }

    private void testIsQuestionAnswered() throws Exception {
        FeedbackQuestionAttributes question;

        ______TS("test question is fully answered by user");

        question = getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(fqLogic.isQuestionFullyAnsweredByUser(question, "student1InCourse1@gmail.tmt"));

        assertFalse(fqLogic.isQuestionFullyAnsweredByUser(question, "studentWithNoResponses@gmail.tmt"));
    }

    private void testAddQuestionNoIntegrityCheck() throws InvalidParametersException, EntityDoesNotExistException {

        ______TS("Add questions sequentially - test for initial template question");
        FeedbackQuestionAttributes q1 = getQuestionFromDatastore("qn1InSession1InCourse1");
        q1.questionNumber = 1;

        int initialNumQuestions = fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId).size();

        //Appends a question to the back of the current question list
        FeedbackQuestionAttributes newQuestion = getQuestionFromDatastore("qn1InSession1InCourse1");
        newQuestion.questionNumber = initialNumQuestions + 1;
        newQuestion.setId(null); //new question should not have an ID.
        fqLogic.createFeedbackQuestionNoIntegrityCheck(newQuestion, newQuestion.questionNumber);

        List<FeedbackQuestionAttributes> actualList =
                fqLogic.getFeedbackQuestionsForSession(q1.feedbackSessionName, q1.courseId);

        assertEquals(actualList.size(), initialNumQuestions + 1);

        //The list starts from 0, so no need to + 1 here.
        assertEquals(actualList.get(initialNumQuestions), newQuestion);

    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(String questionKey) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionKey);
        question = fqLogic.getFeedbackQuestion(
                question.feedbackSessionName, question.courseId, question.questionNumber);
        return question;
    }

}
