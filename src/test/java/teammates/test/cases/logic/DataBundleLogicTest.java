package teammates.test.cases.logic;

import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.logic.core.DataBundleLogic;

/**
 * SUT: {@link DataBundleLogic}.
 */
public class DataBundleLogicTest extends BaseLogicTest {

    private static final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    @Override
    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        // data bundle not persisted before test since the remove/restore data itself is being tested
    }

    @Test
    public void testPersistDataBundle() throws Exception {

        ______TS("empty data bundle");
        dataBundleLogic.persistDataBundle(dataBundle);
        verifyPresentInDatastore(dataBundle);

        ______TS("try to persist while entities exist");

        dataBundleLogic.persistDataBundle(loadDataBundle("/FeedbackSessionResultsTest.json"));
        verifyPresentInDatastore(loadDataBundle("/FeedbackSessionResultsTest.json"));

        ______TS("null parameter");
        assertThrows(InvalidParametersException.class, () -> dataBundleLogic.persistDataBundle(null));

        ______TS("invalid parameters in an entity");
        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id")
                .withName("valid course name")
                .withTimezone(ZoneId.of("UTC"))
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
