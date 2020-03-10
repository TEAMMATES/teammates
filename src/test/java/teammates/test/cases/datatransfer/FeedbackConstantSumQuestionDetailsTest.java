package teammates.common.datatransfer.questions;

import java.util.*;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

public class FeedbackConstantSumQuestionDetailsTest extends BaseTestCase {
    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();

        assertEquals(FeedbackQuestionType.CONSTSUM, sumDetails.getQuestionType());
        assertEquals(0, sumDetails.getNumOfConstSumOptions());
        assertTrue(sumDetails.getConstSumOptions().isEmpty());
        assertFalse(sumDetails.isDistributeToRecipients());
        assertFalse(sumDetails.isPointsPerOption());
        assertEquals(100, sumDetails.getPoints());
        assertFalse(sumDetails.isForceUnevenDistribution());
    }

    @Test
    public void testConstructorParamaters_fieldsShouldHaveCorrectValues(){
        String questionText = "TEST";
        int numOfConstSumOptions = 2;
        List<String> constSumOptions = new ArrayList<>();
        constSumOptions.add("Option-1");
        constSumOptions.add("Option-2");
        boolean pointsPerOption = true;
        int points = 50;
        boolean unevenDistribution = true;
        String distributePointsFor = "Student";
        FeedbackConstantSumQuestionDetails sumDetails = new  FeedbackConstantSumQuestionDetails(questionText,
                constSumOptions, pointsPerOption, points, unevenDistribution, distributePointsFor);

        assertEquals(2, sumDetails.getNumOfConstSumOptions());
        assertEquals("TEST", sumDetails.getQuestionText());
        assertEquals(constSumOptions, sumDetails.getConstSumOptions());
        assertFalse(sumDetails.isDistributeToRecipients());
        assertTrue(sumDetails.isPointsPerOption());
        assertEquals(50, sumDetails.getPoints());
        assertTrue(sumDetails.isForceUnevenDistribution());
        assertEquals("Student", sumDetails.getDistributePointsFor());
    }

    @Test
    public void testExtractQuestionDetails_distributeToRecipientsTrue_noChoicesSelected(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "true" });
        //ConstSumOptions are not set since distributeToRecipients is true
        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        assertEquals(FeedbackQuestionType.CONSTSUM, sumDetails.getQuestionType());
        assertEquals(0, sumDetails.getNumOfConstSumOptions());
        assertTrue(sumDetails.getConstSumOptions().isEmpty());
        assertTrue(sumDetails.isDistributeToRecipients());
        assertFalse(sumDetails.isPointsPerOption());
        assertEquals(50, sumDetails.getPoints());
        assertTrue(sumDetails.isForceUnevenDistribution());
    }

    @Test
    public void testExtractQuestionDetails_distributeToRecipientsFalse_noChoicesSelected(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "NULL" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "25" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "5" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "true" });
        //
        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        assertEquals(FeedbackQuestionType.CONSTSUM, sumDetails.getQuestionType());
        assertEquals(0, sumDetails.getNumOfConstSumOptions());
        assertTrue(sumDetails.getConstSumOptions().isEmpty());
        assertFalse(sumDetails.isDistributeToRecipients());
        assertFalse(sumDetails.isPointsPerOption());
        assertEquals(25, sumDetails.getPoints());
        assertFalse(sumDetails.isForceUnevenDistribution());
    }

    @Test
    public void testExtractQuestionDetails_pointsForEachRecipientTrue_noChoicesSelected(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "5" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "true" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        assertEquals(FeedbackQuestionType.CONSTSUM, sumDetails.getQuestionType());
        assertEquals(0, sumDetails.getNumOfConstSumOptions());
        assertTrue(sumDetails.getConstSumOptions().isEmpty());
        assertTrue(sumDetails.isDistributeToRecipients());
        assertTrue(sumDetails.isPointsPerOption());
        assertEquals(5, sumDetails.getPoints());
        assertTrue(sumDetails.isForceUnevenDistribution());
    }

    @Test
    public void testExtractQuestionDetails_pointsPerOptionTrue_choicesSelected(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-0", new String[] { "Option 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-1", new String[] { "Option 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "3" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "Teacher" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        assertEquals(FeedbackQuestionType.CONSTSUM, sumDetails.getQuestionType());
        assertEquals(2, sumDetails.getNumOfConstSumOptions());
        assertFalse(sumDetails.getConstSumOptions().isEmpty());
        assertFalse(sumDetails.isDistributeToRecipients());
        assertEquals("Teacher", sumDetails.getDistributePointsFor());
        assertTrue(sumDetails.isPointsPerOption());
        assertEquals(3, sumDetails.getPoints());
        assertFalse(sumDetails.isForceUnevenDistribution());
    }

    @Test
    public void testSetAndGetMethods_allTrue(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        int numOfConstSumOptions = 5;
        List<String> constSumOptions = new ArrayList<>();
        constSumOptions.add("Option");
        boolean distributeToRecipients = true;
        int points = 1000;
        boolean pointsPerOption = true;
        boolean forceUnevenDistribution = true;
        String distributePointsFor = "Teacher";

        sumDetails.setNumOfConstSumOptions(numOfConstSumOptions);
        sumDetails.setConstSumOptions(constSumOptions);
        sumDetails.setDistributeToRecipients(distributeToRecipients);
        sumDetails.setPointsPerOption(pointsPerOption);
        sumDetails.setForceUnevenDistribution(forceUnevenDistribution);
        sumDetails.setDistributePointsFor(distributePointsFor);
        sumDetails.setPoints(points);

        assertEquals(5, sumDetails.getNumOfConstSumOptions());
        assertEquals(constSumOptions, sumDetails.getConstSumOptions());
        assertTrue(sumDetails.isDistributeToRecipients());
        assertTrue(sumDetails.isPointsPerOption());
        assertTrue(sumDetails.isForceUnevenDistribution());
        assertEquals("Teacher", sumDetails.getDistributePointsFor());
        assertEquals(1000, sumDetails.getPoints());
        assertEquals(Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT, sumDetails.getQuestionTypeDisplayName());
        assertNull(sumDetails.getInstructions());
    }

    @Test
    public void testSetAndGetMethods_allFalse(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        int numOfConstSumOptions = 2;
        List<String> constSumOptions = new ArrayList<>();
        constSumOptions.add("Option");
        boolean distributeToRecipients = false;
        int points = -99;
        boolean pointsPerOption = false;
        boolean forceUnevenDistribution = false;
        String distributePointsFor = "Student";

        sumDetails.setNumOfConstSumOptions(numOfConstSumOptions);
        sumDetails.setConstSumOptions(constSumOptions);
        sumDetails.setDistributeToRecipients(distributeToRecipients);
        sumDetails.setPointsPerOption(pointsPerOption);
        sumDetails.setForceUnevenDistribution(forceUnevenDistribution);
        sumDetails.setDistributePointsFor(distributePointsFor);
        sumDetails.setPoints(points);

        assertEquals(2, sumDetails.getNumOfConstSumOptions());
        assertEquals(constSumOptions, sumDetails.getConstSumOptions());
        assertFalse(sumDetails.isDistributeToRecipients());
        assertFalse(sumDetails.isPointsPerOption());
        assertFalse(sumDetails.isForceUnevenDistribution());
        assertEquals("Student", sumDetails.getDistributePointsFor());
        assertEquals(-99, sumDetails.getPoints());
        assertEquals(Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION, sumDetails.getQuestionTypeDisplayName());
        assertFalse(sumDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

    @Test
    public void testValidateQuestionDetails_distributeToRecipientsFalse_pointsGreaterThan(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "Option" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "3" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "Teacher" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        List<String> errors = sumDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_distributeToRecipientsTrue_notEnoughPoints(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "Option" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "3" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "Teacher" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        List<String> errors = sumDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_distributeToRecipientsTrue_pointsLessThan(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "100" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "Option" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "-1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "-1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "-1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "Teacher" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        List<String> errors = sumDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_duplicateSumOptions_errorReturned() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        int numOfConstSumOptions = 2;
        List<String> constSumOptions = new ArrayList<>();
        constSumOptions.add("choice 1");
        constSumOptions.add("choice 1");
        boolean distributeToRecipients = true;
        sumDetails.setDistributeToRecipients(distributeToRecipients);
        sumDetails.setNumOfConstSumOptions(numOfConstSumOptions);
        sumDetails.setConstSumOptions(constSumOptions);
        List<String> errors = sumDetails.validateQuestionDetails(dummySessionToken);

        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.CONST_SUM_ERROR_DUPLICATE_OPTIONS, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noErrorsReturned() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION, new String[] { "Option1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, new String[] { "true" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, new String[] { "false" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, new String[] { "100" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, new String[] { "0" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS, new String[] { "Teacher" });

        assertTrue(sumDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.CONSTSUM));
        List<String> errors = sumDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(0, errors.size());
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_numOfConstSumOptionsNotEqual_firstIfStatementFirstCondition(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setNumOfConstSumOptions(1);
        newSumDetails.setNumOfConstSumOptions(2);

        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_constSumOptionsNotEqual_firstIfStatementSecondCondition(){
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        List<String> sumDetailsList = new ArrayList<>();
        sumDetailsList.add("Option 1");
        sumDetailsList.add("Option 2");

        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        List<String> newSumDetailsList = new ArrayList<>();
        newSumDetailsList.add("Option 1");


        sumDetails.setConstSumOptions(sumDetailsList);
        newSumDetails.setConstSumOptions(newSumDetailsList);

        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_constSumOptionsNotEqual_firstIfStatementThirdCondition(){
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();

        List<String> newSumDetailsList = new ArrayList<>();
        newSumDetailsList.add("Option 1");
        newSumDetailsList.add("Option 2");

        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        List<String> sumDetailsList = new ArrayList<>();
        sumDetailsList.add("Option 1");


        sumDetails.setConstSumOptions(sumDetailsList);
        newSumDetails.setConstSumOptions(newSumDetailsList);

        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_distributeToRecipientsNotEqual_secondIfCondition() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setDistributeToRecipients(true);
        newSumDetails.setDistributeToRecipients(false);
        //Recipients not equal
        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_pointsNotEqual_thirdIfCondition() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setPoints(100);
        newSumDetails.setPoints(50);
        //Points not equal
        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_pointsPerOptionNotEqual_fourthIfCondition() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setPointsPerOption(true);
        newSumDetails.setPointsPerOption(false);
        //Points Per Option not equal
        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_forceUnevenDistributionNotEqual_fifthIfCondition() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setForceUnevenDistribution(true);
        newSumDetails.setForceUnevenDistribution(false);
        //forceUnevenDistribution values not equal
        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_distributePointsForNotEqual_sixthIfCondition() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();
        sumDetails.setDistributePointsFor("Teacher");
        newSumDetails.setDistributePointsFor("Student");
        //distributePointsFor strings not equal
        assertTrue(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_constSumQuestionDetails_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails sumDetails = new FeedbackConstantSumQuestionDetails();
        FeedbackConstantSumQuestionDetails newSumDetails = new FeedbackConstantSumQuestionDetails();

        assertFalse(sumDetails.shouldChangesRequireResponseDeletion(newSumDetails));
    }

}
