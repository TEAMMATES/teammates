package teammates.test.cases.logic;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;

/**
 * SUT: {@link teammates.logic.backdoor.BackDoorLogic}.
 */
public class BackDoorLogicTest extends BaseLogicTest {

    @Override
    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        // data bundle not persisted before test since the remove/restore data itself is being tested
    }

    @Test
    public void testPersistDataBundle() throws Exception {

        ______TS("empty data bundle");
        String status = backDoorLogic.persistDataBundle(new DataBundle());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        backDoorLogic.removeDataBundle(dataBundle);
        backDoorLogic.persistDataBundle(dataBundle);
        verifyPresentInDatastore(dataBundle);

        ______TS("try to persist while entities exist");

        backDoorLogic.persistDataBundle(loadDataBundle("/FeedbackSessionResultsTest.json"));
        verifyPresentInDatastore(loadDataBundle("/FeedbackSessionResultsTest.json"));

        ______TS("null parameter");
        try {
            backDoorLogic.persistDataBundle(null);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(Const.StatusCodes.NULL_PARAMETER, e.errorCode);
        }

        ______TS("invalid parameters in an entity");
        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id", "valid course name", "UTC")
                .build();
        dataBundle = new DataBundle();
        dataBundle.courses.put("invalid", invalidCourse);
        try {
            backDoorLogic.persistDataBundle(dataBundle);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertTrue(e.getMessage().equals(
                    getPopulatedErrorMessage(FieldValidator.COURSE_ID_ERROR_MESSAGE, "invalid id",
                            FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                            FieldValidator.COURSE_ID_MAX_LENGTH)));
        }

        // Not checking for invalid values in other entities because they
        // should be checked at lower level methods
    }

    /*
     * Following methods are tested by the testPersistDataBundle method
        getAccountAsJson(String)
        getInstructorAsJson(String, String)
        getCourseAsJson(String)
        getStudentAsJson(String, String)
        editAccountAsJson(String)
        editStudentAsJson(String, String)
        createCourse(String, String)
    */

}
