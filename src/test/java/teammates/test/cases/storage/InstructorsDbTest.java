package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.GOOGLE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.PERSON_NAME_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_EMPTY;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.logic.LogicTest;
import teammates.test.driver.AssertHelper;

public class InstructorsDbTest extends BaseComponentTestCase {
    
    private InstructorsDb instructorsDb = new InstructorsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorsDb.class);
    }
    
    @Test
    public void testCreateInstructor() 
            throws EntityAlreadyExistsException, InvalidParametersException {
        
        ______TS("success: create an instructor");
        
        InstructorAttributes i = new InstructorAttributes();
        i.googleId = "valid.fresh.id";
        i.courseId = "valid.course.Id";
        i.name = "valid.name";
        i.email = "valid@email.com";
        
        instructorsDb.deleteEntity(i);
        instructorsDb.createEntity(i);
        
        LogicTest.verifyPresentInDatastore(i);
        
        ______TS("failure: instructor already exists");

        try {
            instructorsDb.createEntity(i);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(InstructorsDb.ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS, e.getMessage());
        }
        
        ______TS("failure: instructor with invalid parameters");

        i.googleId = "invalid id with spaces";
        try {
            instructorsDb.createEntity(i);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT),
                    e.getMessage());
        } catch (EntityAlreadyExistsException e) {
            Assumption.fail();
        }
        
        ______TS("failure: null parameters");
        
        try {
            instructorsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForEmail() throws InvalidParametersException {
        InstructorAttributes i = createNewInstructor();
        
        ______TS("success: get an instructor");
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNotNull(retrieved);
        
        ______TS("failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorForEmail("non.existent.course", "non.existent");
        assertNull(retrieved);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.getInstructorForEmail(null, null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForGoogleId() throws InvalidParametersException {
        InstructorAttributes i = createNewInstructor();
        
        ______TS("successe: get an instructor");
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForGoogleId(i.courseId, i.googleId);
        assertNotNull(retrieved);
        
        ______TS("failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorForGoogleId("non.existent.course", "non.existent");
        assertNull(retrieved);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.getInstructorForGoogleId(null, null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForRegistrationKey() throws InvalidParametersException {
        InstructorAttributes i = createNewInstructor();
        
        ______TS("success: get an instructor");
        String key = i.key;
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertEquals(i.courseId, retrieved.courseId);
        assertEquals(i.name, retrieved.name);
        assertEquals(i.email, retrieved.email);
        
        ______TS("failure: instructor does not exist");
        
        key = "non.existent.key";
        retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertNull(retrieved);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.getInstructorForRegistrationKey(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    @Test
    public void testGetInstructorsForGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: get instructors with specific googleId");
        
        String googleId = "idOfInstructor3";
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForGoogleId(googleId);
        assertEquals(2, retrieved.size());
        
        InstructorAttributes instructor1 = retrieved.get(0);
        InstructorAttributes instructor2 = retrieved.get(1);
        
        assertEquals("idOfTypicalCourse1", instructor1.courseId);
        assertEquals("idOfTypicalCourse2", instructor2.courseId);
        
        ______TS("failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorsForGoogleId("non-exist-id");
        assertEquals(0, retrieved.size());
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.getInstructorsForGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorsForCourse() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: get instructors of a specific course");
        
        String courseId = "idOfTypicalCourse1";
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(3, retrieved.size());
        
        InstructorAttributes instructor1 = retrieved.get(0);
        InstructorAttributes instructor2 = retrieved.get(1);
        InstructorAttributes instructor3 = retrieved.get(2);
        
        assertEquals("idOfInstructor1OfCourse1", instructor1.googleId);
        assertEquals("idOfInstructor2OfCourse1", instructor2.googleId);
        assertEquals("idOfInstructor3", instructor3.googleId);
        
        ______TS("failure: no instructors for a course");
        
        retrieved = instructorsDb.getInstructorsForCourse("non-exist-course");
        assertEquals(0, retrieved.size());
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.getInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testUpdateInstructorByGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        InstructorAttributes instructorToEdit = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse1", "idOfInstructor1OfCourse1");
        
        ______TS("success: update an instructor");

        instructorToEdit.name = "New Name";
        instructorToEdit.email = "InstrDbT.new-email@email.com";
        instructorsDb.updateInstructorByGoogleId(instructorToEdit);
        
        InstructorAttributes instructorUpdated = instructorsDb.getInstructorForGoogleId(instructorToEdit.courseId, instructorToEdit.googleId);
        assertEquals(instructorToEdit.name, instructorUpdated.name);
        assertEquals(instructorToEdit.email, instructorUpdated.email);
        
        ______TS("failure: invalid parameters");
        
        instructorToEdit.name = "";
        instructorToEdit.email = "aaa";
        try {
            instructorsDb.updateInstructorByGoogleId(instructorToEdit);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                        String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name, REASON_EMPTY) + Const.EOL 
                        + String.format(EMAIL_ERROR_MESSAGE, instructorToEdit.email, REASON_INCORRECT_FORMAT),
                        e.getMessage());
        }
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.updateInstructorByGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    @Test
    public void testUpdateInstructorByEmail() throws Exception {
        restoreTypicalDataInDatastore();
        
        InstructorAttributes instructorToEdit = instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.com");
        
        ______TS("success: update an instructor");
        
        instructorToEdit.googleId = "new-id";
        instructorToEdit.name = "New Name";
        instructorsDb.updateInstructorByEmail(instructorToEdit);
        
        InstructorAttributes instructorUpdated = instructorsDb.getInstructorForEmail(instructorToEdit.courseId, instructorToEdit.email);
        assertEquals("new-id", instructorUpdated.googleId);
        assertEquals("New Name", instructorUpdated.name);

        ______TS("failure: invalid parameters");

        instructorToEdit.googleId = "invalid id";
        instructorToEdit.name = "";
        try {
            instructorsDb.updateInstructorByEmail(instructorToEdit);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(GOOGLE_ID_ERROR_MESSAGE, instructorToEdit.googleId, REASON_INCORRECT_FORMAT)
                            + Const.EOL
                            + String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name, REASON_EMPTY),
                    e.getMessage());
        }

        ______TS("failure: null parameters");
        try {
            instructorsDb.updateInstructorByEmail(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructor() throws InvalidParametersException {
        InstructorAttributes i = createNewInstructor();
        
        ______TS("success: delete an instructor");
        
        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        InstructorAttributes deleted = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNull(deleted);
        
        ______TS("delete a non-exist instructor, should fail silently");
        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.deleteInstructor(null, null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructorsForGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: delete instructors with specific googleId");
        
        String googleId = "idOfInstructor3";
        instructorsDb.deleteInstructorsForGoogleId(googleId);
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForGoogleId(googleId);
        assertEquals(0, retrieved.size());
        
        ______TS("try to delete where there's no instructors associated with the googleId, should fail silently");
        instructorsDb.deleteInstructorsForGoogleId(googleId);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.deleteInstructorsForGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructorsForCourse() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: delete instructors of a specific course");
        
        String courseId = "idOfTypicalCourse1";
        instructorsDb.deleteInstructorsForCourse(courseId);
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(0, retrieved.size());
        
        ______TS("failure: no instructor exists for the course, should fail silently");
        instructorsDb.deleteInstructorsForCourse(courseId);
        
        ______TS("failure: null parameters");
        try {
            instructorsDb.deleteInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    private InstructorAttributes createNewInstructor() throws InvalidParametersException {
        InstructorAttributes i = new InstructorAttributes();
        i.googleId = "InstrDbT.valid.id";
        i.courseId = "InstrDbT.valid.course";
        i.name = "InstrDbT.valid.name";
        i.email = "InstrDbT.valid@email.com";
        i.key = "InstrDbT.validKey";
        
        try {
            instructorsDb.createEntity(i);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it already exists
            ignoreExpectedException();
        }
        
        return i;
    }
}
