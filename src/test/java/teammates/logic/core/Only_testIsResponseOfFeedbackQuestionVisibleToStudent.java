package teammates.logic.core;

import java.util.Arrays;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class Only_testIsResponseOfFeedbackQuestionVisibleToStudent extends BaseLogicTest {

    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    private DataBundle questionTypeBundle;
    private DataBundle responseVisibilityBundle;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        questionTypeBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        responseVisibilityBundle = loadDataBundle("/FeedbackResponseVisibilityTest.json");

        removeAndRestoreTypicalDataBundle();
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));
        removeAndRestoreDataBundle(questionTypeBundle);
        removeAndRestoreDataBundle(responseVisibilityBundle);
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT01() {
        List<FeedbackParticipantType> list1 = Arrays.asList(FeedbackParticipantType.STUDENTS);
        FeedbackQuestionAttributes question1 = FeedbackQuestionAttributes.builder()
                .withShowResponsesTo(list1).build();
        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question1));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT02() {
        List<FeedbackParticipantType> list2 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question2 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list2).build();
        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question2));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT03() {
        List<FeedbackParticipantType> list3 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question3 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list3).build();

        boolean isStudentRecipientType = true;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question3));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT04() {
        List<FeedbackParticipantType> list4 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question4 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list4).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question4));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT05() {
        List<FeedbackParticipantType> list5 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question5 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list5).build();
        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question5));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT06() {
        List<FeedbackParticipantType> list6 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question6 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list6).build();
        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question6));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT07() {
        List<FeedbackParticipantType> list7 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question7 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list7)
                .withGiverType(FeedbackParticipantType.SELF).build();

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question7));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT08() {
        List<FeedbackParticipantType> list8 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question8 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list8)
                .withGiverType(FeedbackParticipantType.SELF).build();
        assertFalse(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question8));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT09() {
        List<FeedbackParticipantType> list9 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question9 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list9).build();

        boolean isStudentRecipientType = true;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question9));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT10() {
        List<FeedbackParticipantType> list10 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question10 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list10).build();

        boolean isStudentRecipientType = true;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question10));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT11() {
        List<FeedbackParticipantType> list11 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question11 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list11)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = true;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question11));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT12() {
        List<FeedbackParticipantType> list12 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question12 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list12)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = true;
        boolean isRecipientTypeTeam = false;

        assertFalse(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question12));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT13() {
        List<FeedbackParticipantType> list13 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question13 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list13).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question13));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT14() {
        List<FeedbackParticipantType> list14 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question14 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list14).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question14));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT15() {
        List<FeedbackParticipantType> list15 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question15 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list15)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question15));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT16() {
        List<FeedbackParticipantType> list16 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question16 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list16)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertFalse(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question16));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT17() {
        List<FeedbackParticipantType> list17 = Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question17 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list17).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question17));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT18() {
        List<FeedbackParticipantType> list18 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question18 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list18).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question18));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT19() {
        List<FeedbackParticipantType> list19 = Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question19 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list19)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question19));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT20() {
        List<FeedbackParticipantType> list20 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question20 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list20)
                .withGiverType(FeedbackParticipantType.SELF).build();
        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = true;

        assertFalse(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question20));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT21() {
        List<FeedbackParticipantType> list21 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question21 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list21).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question21));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT22() {
        List<FeedbackParticipantType> list22 = Arrays.asList(FeedbackParticipantType.SELF);
        FeedbackQuestionAttributes question22 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withShowResponsesTo(list22).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question22));

    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT23() {
        List<FeedbackParticipantType> list23 = Arrays.asList(FeedbackParticipantType.SELF,
                FeedbackParticipantType.OWN_TEAM_MEMBERS);
        FeedbackQuestionAttributes question23 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list23)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertTrue(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question23));
    }

    @Test
    void testIsResponseOfFeedbackQuestionVisibleToStudent_CT24() {
        List<FeedbackParticipantType> list24 = Arrays.asList(FeedbackParticipantType.RECEIVER);
        FeedbackQuestionAttributes question24 = FeedbackQuestionAttributes.builder()
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withShowResponsesTo(list24)
                .withGiverType(FeedbackParticipantType.SELF).build();

        boolean isStudentRecipientType = false;
        boolean isRecipientTypeTeam = false;

        assertFalse(frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question24));
    }

}