package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.*;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class FeedbackQuestionAttributesTest extends BaseTestCase {

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        FeedbackQuestionAttributes fq = new FeedbackQuestionAttributes();

        fq.feedbackSessionName = "";
        fq.courseId = "";
        fq.creatorEmail = "";
        fq.questionType = FeedbackQuestionType.TEXT;
        fq.giverType = FeedbackParticipantType.NONE;
        fq.recipientType = FeedbackParticipantType.RECEIVER;

        fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showGiverNameTo.add(FeedbackParticipantType.SELF);
        fq.showGiverNameTo.add(FeedbackParticipantType.STUDENTS);

        fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showRecipientNameTo.add(FeedbackParticipantType.SELF);
        fq.showRecipientNameTo.add(FeedbackParticipantType.STUDENTS);

        fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        fq.showResponsesTo.add(FeedbackParticipantType.NONE);
        fq.showResponsesTo.add(FeedbackParticipantType.SELF);

        assertFalse(fq.isValid());

        String errorMessage = String.format(FEEDBACK_SESSION_NAME_ERROR_MESSAGE, fq.creatorEmail, REASON_EMPTY) + EOL
                              + String.format(COURSE_ID_ERROR_MESSAGE, fq.courseId, REASON_EMPTY) + EOL
                              + String.format("Invalid creator's email: " + EMAIL_ERROR_MESSAGE, fq.creatorEmail, REASON_EMPTY) + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.giverType.toString(), GIVER_TYPE_NAME) + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.recipientType.toString(), RECIPIENT_TYPE_NAME) + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                              + "Trying to show giver name to STUDENTS without showing response first." + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showRecipientNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                              + "Trying to show recipient name to STUDENTS without showing response first." + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,fq.showResponsesTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                              + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(), VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.feedbackSessionName = "First Feedback Session";
        fq.courseId = "CS1101";
        fq.creatorEmail = "instructor1@course1.com";
        fq.giverType = FeedbackParticipantType.TEAMS;
        fq.recipientType = FeedbackParticipantType.OWN_TEAM;

        assertFalse(fq.isValid());

        errorMessage = String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE, fq.recipientType.toDisplayRecipientName(), fq.giverType.toDisplayGiverName()) + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + "Trying to show giver name to STUDENTS without showing response first." + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showRecipientNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + "Trying to show recipient name to STUDENTS without showing response first." + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(), VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        assertFalse(fq.isValid());

        errorMessage = String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE, fq.recipientType.toDisplayRecipientName(), fq.giverType.toDisplayGiverName()) + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + "Trying to show giver name to STUDENTS without showing response first." + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showRecipientNameTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + "Trying to show recipient name to STUDENTS without showing response first." + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(), VIEWER_TYPE_NAME) + EOL
                       + String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(), VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.TEAMS;

        fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showGiverNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);

        assertTrue(fq.isValid());
    }

    @Test
    public void testRemoveIrrelevantVisibilityOptions() {

        ______TS("test teams->none");

        FeedbackQuestionAttributes question = new FeedbackQuestionAttributes();
        List<FeedbackParticipantType> participants = new ArrayList<FeedbackParticipantType>();

        question.feedbackSessionName = "test session";
        question.courseId = "some course";
        question.creatorEmail = "test@case.com";
        question.questionMetaData = new Text("test qn from teams->none.");
        question.questionNumber = 1;
        question.questionType = FeedbackQuestionType.TEXT;
        question.giverType = FeedbackParticipantType.TEAMS;
        question.recipientType = FeedbackParticipantType.NONE;
        question.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<FeedbackParticipantType>(participants);
        question.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showResponsesTo = new ArrayList<FeedbackParticipantType>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertTrue(question.showGiverNameTo.isEmpty());
        assertTrue(question.showRecipientNameTo.isEmpty());
        // check that other types are not removed
        assertTrue(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS));
        assertEquals(question.showResponsesTo.size(), 1);

        ______TS("test students->teams");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.TEAMS;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<FeedbackParticipantType>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(participants);
        question.showResponsesTo = new ArrayList<FeedbackParticipantType>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(),2);
        assertEquals(question.showRecipientNameTo.size(),3);
        assertEquals(question.showResponsesTo.size(), 3);
        assertTrue(!question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertTrue(!question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertTrue(!question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->team members including giver");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<FeedbackParticipantType>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<FeedbackParticipantType>(participants);
        question.showResponsesTo = new ArrayList<FeedbackParticipantType>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(),3);
        assertEquals(question.showRecipientNameTo.size(),4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertTrue(!question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertTrue(!question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertTrue(!question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
}
