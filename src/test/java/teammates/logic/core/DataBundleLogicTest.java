package teammates.logic.core;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;

/**
 * SUT: {@link DataBundleLogic}.
 */
public class DataBundleLogicTest extends BaseLogicTest {

    private final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    @Override
    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        // data bundle not persisted before test since the remove/restore data itself is being tested
    }

    @Test
    public void testPersistDataBundle() throws Exception {
        ______TS("empty data bundle");
        dataBundleLogic.persistDataBundle(dataBundle);
        verifyPresentInDatabase(dataBundle);

        ______TS("try to persist while entities exist");
        dataBundleLogic.persistDataBundle(loadDataBundle("/FeedbackSessionResultsTest.json"));
        verifyPresentInDatabase(loadDataBundle("/FeedbackSessionResultsTest.json"));

        // Only FeedbackQuestions is tested because currently, it is the only data that has a value
        // updated only in the server. If there are more in the future, they should be added in this
        // test as well.
        ______TS("data values are updated to server values");
        DataBundle typicalDataBundle = getTypicalDataBundle();
        Map.Entry<String, FeedbackQuestionAttributes> originalFeedbackQuestionEntry = typicalDataBundle
                .feedbackQuestions.entrySet().iterator().next();
        FeedbackQuestionAttributes originalFeedbackQuestion = originalFeedbackQuestionEntry.getValue();
        dataBundleLogic.persistDataBundle(typicalDataBundle);
        assertFalse(typicalDataBundle.feedbackQuestions.get(originalFeedbackQuestionEntry.getKey()).getId()
                .equals(originalFeedbackQuestion.getId()));

        ______TS("null parameter");
        assertThrows(InvalidParametersException.class, () -> dataBundleLogic.persistDataBundle(null));
        ______TS("invalid parameters in an entity");
        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id")
                .withName("valid course name")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        dataBundle = new DataBundle();
        dataBundle.courses.put("invalid", invalidCourse);
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> dataBundleLogic.persistDataBundle(dataBundle));
        assertEquals(
                getPopulatedErrorMessage(FieldValidator.COURSE_ID_ERROR_MESSAGE, "invalid id",
                        FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());

        // Not checking for invalid values in other entities because they
        // should be checked at lower level methods
    }

}
