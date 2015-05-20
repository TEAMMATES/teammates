package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import org.testng.Assert;
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
import teammates.common.util.Utils;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.common.CourseAttributesTest;
import teammates.test.util.TestHelper;

import com.google.gson.Gson;

public class BackDoorLogicTest extends BaseComponentTestCase {
    Gson gson = Utils.getTeammatesGson();
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(BackDoorLogic.class);
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
            Assert.fail();
        } catch (InvalidParametersException e) {
            assertEquals(Const.StatusCodes.NULL_PARAMETER, e.errorCode);
        }

        ______TS("invalid parameters in an entity");
        CourseAttributes invalidCourse = CourseAttributesTest.generateValidCourseAttributesObject();
        invalidCourse.id = "invalid id";
        dataBundle = new DataBundle();
        dataBundle.courses.put("invalid", invalidCourse);
        try {
            logic.persistDataBundle(dataBundle);
            Assert.fail();
        } catch (InvalidParametersException e) {
            assertTrue(e.getMessage().contains("not acceptable to TEAMMATES as a Course ID because it is not in the correct format"));
        }

        // Not checking for invalid values in other entities because they
        // should be checked at lower level methods
    }
    
    private void verifyPresentInDatastore(DataBundle data) throws Exception {
        HashMap<String, AccountAttributes> accounts = data.accounts;
        for(AccountAttributes expectedAccount : accounts.values()) {
            TestHelper.verifyPresentInDatastore(expectedAccount);
        }
        
        HashMap<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes expectedInstructor : instructors.values()) {
            TestHelper.verifyPresentInDatastore(expectedInstructor);
        }
    
        HashMap<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes expectedCourse : courses.values()) {
            TestHelper.verifyPresentInDatastore(expectedCourse);
        }
    
        HashMap<String, StudentAttributes> students = data.students;
        for (StudentAttributes expectedStudent : students.values()) {
            TestHelper.verifyPresentInDatastore(expectedStudent);
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
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(BackDoorLogic.class);
    }

}
