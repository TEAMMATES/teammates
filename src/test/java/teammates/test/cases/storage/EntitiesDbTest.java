package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.CoursesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class EntitiesDbTest extends BaseComponentTestCase {
    
    
    @Test
    public void testCreateEntity() throws EntityAlreadyExistsException, InvalidParametersException {
        //We are using CoursesDb to test EntititesDb here.
        CoursesDb coursesDb = new CoursesDb();
        
        /*Explanation:
         * The SUT (i.e. EntitiesDb::createEntity) has 4 paths. Therefore, we
         * have 4 test cases here, one for each path.
         */

        ______TS("success: typical case");
        CourseAttributes c = new CourseAttributes();
        c.id = "Computing101-fresh";
        c.name = "Basic Computing";
        c.timeZone = "UTC";
        coursesDb.deleteCourse(c.id);
        verifyAbsentInDatastore(c);
        coursesDb.createEntity(c);
        verifyPresentInDatastore(c);
        
        ______TS("fails: entity already exists");
        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(CoursesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, c.getEntityTypeAsString())
                    + c.getIdentificationString(), e.getMessage());
        }
        coursesDb.deleteEntity(c);
        
        ______TS("fails: invalid parameters");
        c.id = "invalid id spaces";
        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(COURSE_ID_ERROR_MESSAGE, c.id, REASON_INCORRECT_FORMAT), 
                    e.getMessage());
        } 
        
        ______TS("fails: null parameter");
        try {
            coursesDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

}
