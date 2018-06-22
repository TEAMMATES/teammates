package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackMsqQuestionDetails}.
 */
public class FeedbackMsqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        assertEquals(FeedbackQuestionType.MSQ, msqDetails.getQuestionType());
        assertTrue(msqDetails instanceof FeedbackMsqQuestionDetails);
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

}