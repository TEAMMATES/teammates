package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorFeedbackQuestionCopyPageData;
import teammates.ui.template.FeedbackQuestionCopyTable;

/**
 * SUT: {@link InstructorFeedbackQuestionCopyPageData}.
 */
public class InstructorFeedbackQuestionCopyPageDataTest extends BaseTestCase {

    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void allTests() {
        ______TS("Typical case");

        List<FeedbackQuestionAttributes> copiableQuestions = new ArrayList<>();
        copiableQuestions.addAll(dataBundle.feedbackQuestions.values());

        InstructorFeedbackQuestionCopyPageData data = new InstructorFeedbackQuestionCopyPageData(
                dataBundle.accounts.get("instructor1OfCourse1"), dummySessionToken, copiableQuestions);
        FeedbackQuestionCopyTable copyForm = data.getCopyQnForm();
        assertEquals(dataBundle.feedbackQuestions.size(), copyForm.getQuestionRows().size());
    }

}
