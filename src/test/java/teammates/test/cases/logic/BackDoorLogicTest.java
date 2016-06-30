package teammates.test.cases.logic;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.cases.BaseComponentTestCase;

public class BackDoorLogicTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }

    @Test
    public void testPersistDataBundle() throws Exception {

        BackDoorLogic logic = new BackDoorLogic();
        
        ______TS("empty data bundle");
        String status = logic.persistDataBundle(new DataBundle());
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        logic.deleteExistingData(dataBundle);
        logic.persistDataBundle(dataBundle);
        verifyPresentInDatastore(dataBundle);

        ______TS("try to persist while entities exist");
        
        logic.persistDataBundle(loadDataBundle("/FeedbackSessionResultsTest.json"));
        verifyPresentInDatastore(loadDataBundle("/FeedbackSessionResultsTest.json"));
        
        ______TS("null parameter");
        DataBundle nullDataBundle = null;
        try {
            logic.persistDataBundle(nullDataBundle);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(Const.StatusCodes.NULL_PARAMETER, e.errorCode);
        }

        ______TS("invalid parameters in an entity");
        CourseAttributes invalidCourse = new CourseAttributes("invalid id", "valid course name");
        dataBundle = new DataBundle();
        dataBundle.courses.put("invalid", invalidCourse);
        try {
            logic.persistDataBundle(dataBundle);
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
    
    private void verifyPresentInDatastore(DataBundle data) {
        HashMap<String, AccountAttributes> accounts = data.accounts;
        for (AccountAttributes expectedAccount : accounts.values()) {
            verifyPresentInDatastore(expectedAccount);
        }
        
        HashMap<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes expectedInstructor : instructors.values()) {
            verifyPresentInDatastore(expectedInstructor);
        }
    
        HashMap<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes expectedCourse : courses.values()) {
            verifyPresentInDatastore(expectedCourse);
        }
    
        HashMap<String, StudentAttributes> students = data.students;
        for (StudentAttributes expectedStudent : students.values()) {
            verifyPresentInDatastore(expectedStudent);
        }
    
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

    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }

}
