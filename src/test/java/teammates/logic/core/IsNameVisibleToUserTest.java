package teammates.logic.core;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.testng.annotations.BeforeMethod;

import static teammates.common.datatransfer.FeedbackParticipantType.GIVER;
import static teammates.common.datatransfer.FeedbackParticipantType.INSTRUCTORS;
import static teammates.common.datatransfer.FeedbackParticipantType.OWN_TEAM_MEMBERS;
import static teammates.common.datatransfer.FeedbackParticipantType.RECEIVER;
import static teammates.common.datatransfer.FeedbackParticipantType.RECEIVER_TEAM_MEMBERS;
import static teammates.common.datatransfer.FeedbackParticipantType.STUDENTS;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

public class IsNameVisibleToUserTest extends BaseLogicTest{

    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    private DataBundle courseRosterDataBundle;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        courseRosterDataBundle = loadDataBundle("/CourseRosterDataBundle.json");
        removeAndRestoreTypicalDataBundle();
    }

    

    @Test
    public void testIsNameVisibleToUser() {
        String comment1FromT1C1ToR1Q1S1C1 = "comment1FromT1C1ToR1Q1S1C1";
        String comment1FromT1C1ToR1Q1S2C2 = "comment1FromT1C1ToR1Q1S2C2";
        String comment1FromT1C1ToR1Q2S1C1 = "comment1FromT1C1ToR1Q2S1C1";
        String response1ForQ1S1C1 = "response1ForQ1S1C1";
        String response1ForQ2S1C1 = "response1ForQ2S1C1";
        String instructor1InCourse1Email = "instructor1@course1.tmt";
        String instructor3InUnknownCourseEmail = "instructor3@course.tmt";
        String instructor2InCourse1Email = "instructor2@course1.tmt";
        String student1InCourse1Email = "student1InCourse1@gmail.tmt";
        String student3InCourse1Email = "student3InCourse1@gmail.tmt";
        String student4InCourse1Email = "student4InCourse1@gmail.tmt";
        String student5InCourse1Email = "student5InCourse1@gmail.tmt";
        String student6InCourse1Email = "student6InCourse1@gmail.tmt";
        String student1InCourse2Email = "student1InCourse2@gmail.tmt";

        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get(comment1FromT1C1ToR1Q1S1C1);
        FeedbackResponseAttributes relatedResponse = dataBundle.feedbackResponses.get(response1ForQ1S1C1);
        CourseRoster roster = new CourseRoster(new ArrayList<>(courseRosterDataBundle.students.values()),
                new ArrayList<>(courseRosterDataBundle.instructors.values()));

        ______TS("success: the list that comment giver's name shown to is null; always return true");
        comment.setShowGiverNameTo(null);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, "", roster));

        ______TS("success: comment's visibility follows feedback question; always return ture");
        comment.setVisibilityFollowingFeedbackQuestion(true);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, "", roster));

        ______TS("success: comment is always visible to its giver");
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor1InCourse1Email, roster));

        comment = dataBundle.feedbackResponseComments.get(comment1FromT1C1ToR1Q1S2C2);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor3InUnknownCourseEmail, roster));

        // test user's participant type is in the list that comment giver's name is shown to
        comment = dataBundle.feedbackResponseComments.get(comment1FromT1C1ToR1Q2S1C1);
        relatedResponse = dataBundle.feedbackResponses.get(response1ForQ2S1C1);
        relatedResponse.setGiver(student1InCourse1Email);

        ______TS("success: comment is only visible to instructors");
        comment.setCommentGiver(student6InCourse1Email);
        comment.setCommentGiverType(STUDENTS);
        comment.setShowGiverNameTo(Arrays.asList(INSTRUCTORS));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse2Email, roster));

        ______TS("success: comment is only visible to response recipient");
        comment.setCommentGiver(student4InCourse1Email);
        comment.setCommentGiverType(STUDENTS);
        comment.setShowGiverNameTo(Arrays.asList(RECEIVER));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));

        ______TS("success: comment is only visible to response recipient team");
        comment.setShowGiverNameTo(Arrays.asList(RECEIVER_TEAM_MEMBERS));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student6InCourse1Email, roster));

        ______TS("success: comment is only visible to response giver team");
        comment.setShowGiverNameTo(Arrays.asList(OWN_TEAM_MEMBERS));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));

        ______TS("success: comment is only visible to students");
        comment.setShowGiverNameTo(Arrays.asList(STUDENTS));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student6InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse2Email, roster));

        ______TS("success: comment is only visible to response giver");
        comment.setShowGiverNameTo(Arrays.asList(GIVER));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));

        ______TS("success: comment is only visible to instructor, response giver and recipient's team");
        comment.setShowGiverNameTo(Arrays.asList(INSTRUCTORS, GIVER, RECEIVER_TEAM_MEMBERS));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student6InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse2Email, roster));

        ______TS("success: comment is only visible to response giver's team and recipient");
        comment.setShowGiverNameTo(Arrays.asList(OWN_TEAM_MEMBERS, RECEIVER));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student6InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse2Email, roster));

        ______TS("success: comment is visible to everyone");
        comment.setShowGiverNameTo(Arrays.asList(INSTRUCTORS, OWN_TEAM_MEMBERS, RECEIVER_TEAM_MEMBERS, RECEIVER,
                STUDENTS, GIVER));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, instructor2InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student5InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student6InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student3InCourse1Email, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, student1InCourse2Email, roster));

    }

}
