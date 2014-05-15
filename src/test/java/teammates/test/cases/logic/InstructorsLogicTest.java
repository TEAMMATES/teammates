package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class InstructorsLogicTest extends BaseComponentTestCase{

    //TODO: add missing test cases. Some of the test content can be transferred from LogicTest.
    
    private static InstructorsLogic instructorsLogic = new InstructorsLogic();
    private static InstructorsDb instructorsDb = new InstructorsDb();
    private static CoursesLogic coursesLogic = new CoursesLogic();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorsLogic.class);
    }
    
    @Test
    public void testAddInstructor() throws Exception {
        
        ______TS("success: add an instructor");
        
        InstructorAttributes instr = new InstructorAttributes(
                null, "test-course", "New Instructor", "ILT.instr@email.com");
        
        instructorsLogic.addInstructor(instr.courseId, instr.name, instr.email);
        
        TestHelper.verifyPresentInDatastore(instr);
        
        ______TS("failure: instructor already exists");
        
        try {
            instructorsLogic.addInstructor(instr.courseId, instr.name, instr.email);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains("Trying to create a Instructor that exists", e.getMessage());
        }
        
        ______TS("failure: invalid parameter");
        
        instr.email = "invalidEmail.com";
        
        try {
            instructorsLogic.addInstructor(instr.courseId, instr.name, instr.email);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("\""+instr.email+"\" is not acceptable to TEAMMATES as an email",
                                e.getMessage());
        }
        
    }
    
    @Test
    public void testGetInstructorForEmail() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForEmail("idOfTypicalCourse1", "non-exist@email.com"));

        ______TS("success: get an instructor by using email");

        String courseId = "idOfTypicalCourse1";
        String email = "instructor1@course1.com";
        
        InstructorAttributes instr = instructorsLogic.getInstructorForEmail(courseId, email);
        
        assertEquals(courseId, instr.courseId);
        assertEquals(email, instr.email);
        assertEquals("idOfInstructor1OfCourse1", instr.googleId);
        assertEquals("Instructor1 Course1", instr.name);
    }
    
    @Test
    public void testGetInstructorForGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForGoogleId("idOfTypicalCourse1", "non-exist-id"));

        ______TS("success: typical case");

        String courseId = "idOfTypicalCourse1";
        String googleId = "idOfInstructor1OfCourse1";
        
        InstructorAttributes instr = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        
        assertEquals(courseId, instr.courseId);
        assertEquals(googleId, instr.googleId);
        assertEquals("instructor1@course1.com", instr.email);
        assertEquals("Instructor1 Course1", instr.name);
    }
    
    @Test
    public void testGetInstructorForRegistrationKey() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("failure: instructor doesn't exist");
        String key = "non-existing-key";
        assertNull(instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key)));

        ______TS("success: typical case");

        String courseId = "idOfSampleCourse-demo";
        String email = "instructorNotYetJoined@email.com";
        
        InstructorAttributes instr = instructorsDb.getInstructorForEmail(courseId, email);
        key = instr.key;
        
        InstructorAttributes retrieved = instructorsLogic.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        
        assertEquals(instr.courseId, retrieved.courseId);
        assertEquals(instr.name, retrieved.name);
        assertEquals(instr.email, retrieved.email);
    }

    @Test
    public void testGetInstructorsForCourse() throws Exception {
        restoreTypicalDataInDatastore();

        ______TS("success: get all instructors for a course");

        String courseId = "idOfTypicalCourse1";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(3, instructors.size());
        
        InstructorAttributes instructor1 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor1OfCourse1");
        InstructorAttributes instructor2 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor2OfCourse1");
        InstructorAttributes instructor3 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor3");
        
        verifySameInstructor(instructor1, instructors.get(0));
        verifySameInstructor(instructor2, instructors.get(1));
        verifySameInstructor(instructor3, instructors.get(2));
        
        ______TS("failure: no instructors for a given course");
        
        courseId = "new-course";
        coursesLogic.createCourse(courseId, "New course");
        
        instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(0, instructors.size());
    }

    @Test
    public void testGetInstructorsForGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: get all instructors for a google id");

        String googleId = "idOfInstructor3";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
        assertEquals(2, instructors.size());
        
        InstructorAttributes instructor1 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse1", "idOfInstructor3");
        InstructorAttributes instructor2 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse2", "idOfInstructor3");
        
        verifySameInstructor(instructor1, instructors.get(0));
        verifySameInstructor(instructor2, instructors.get(1));
        
        ______TS("failure: non-exist google id");
        
        googleId = "non-exist-id";
        
        instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
        assertEquals(0, instructors.size());
    }
    
    @Test
    public void testGetKeyForInstructor() throws Exception {
        restoreTypicalDataInDatastore();
    
        ______TS("success: get encrypted key for instructor");
        
        String courseId = "idOfSampleCourse-demo";
        String email = "instructorNotYetJoined@email.com";
        
        InstructorAttributes instructor = instructorsDb.getInstructorForEmail(courseId, email);
        
        String key = instructorsLogic.getKeyForInstructor(instructor.courseId, instructor.email);
        String expected = instructor.key;
        assertEquals(expected, key);
        
        ______TS("non-existent instructor");

        try {
            instructorsLogic.getKeyForInstructor(courseId, "non-existent@email.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor does not exist :non-existent@email.com", e.getMessage());
        }
    }
    
    @Test
    public void testIsInstructorOfCourse() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: is an instructor of a given course");

        String instructorId = "idOfInstructor1OfCourse1";
        String courseId = "idOfTypicalCourse1";
        
        boolean result = instructorsLogic.isInstructorOfCourse(instructorId, courseId);
        
        assertEquals(true, result);
        
        ______TS("failure: not an instructor of a given course");

        courseId = "idOfTypicalCourse2";
        
        result = instructorsLogic.isInstructorOfCourse(instructorId, courseId);
        
        assertEquals(false, result);
    }
    
    @Test
    public void testIsInstructorEmailOfCourse() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: is an instructor of a given course");

        String instructorEmail = "instructor1@course1.com";
        String courseId = "idOfTypicalCourse1";
        
        boolean result = instructorsLogic.isInstructorEmailOfCourse(instructorEmail, courseId);
        
        assertEquals(true, result);
        
        ______TS("failure: not an instructor of a given course");

        courseId = "idOfTypicalCourse2";
        
        result = instructorsLogic.isInstructorEmailOfCourse(instructorEmail, courseId);
        
        assertEquals(false, result);
    }

    @Test
    public void testVerifyInstructorExists() throws Exception  {
        restoreTypicalDataInDatastore();
        
        ______TS("success: instructor does exist");
        
        String instructorId = "idOfInstructor1OfCourse1";
        instructorsLogic.verifyInstructorExists(instructorId);
        
        ______TS("failure: instructor doesn't exist");
        
        instructorId = "nonExistingInstructor";
        
        try {
            instructorsLogic.verifyInstructorExists(instructorId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Instructor does not exist", e.getMessage());
        }
    }
    
    @Test
    public void testVerifyIsInstructorOfCourse() throws Exception  {
        restoreTypicalDataInDatastore();
        
        ______TS("success: instructor belongs to course");
        
        String instructorId = "idOfInstructor1OfCourse1";
        String courseId = "idOfTypicalCourse1";
        instructorsLogic.verifyIsInstructorOfCourse(instructorId, courseId);
        
        ______TS("failure: instructor doesn't belong to course");
        instructorId = "nonExistingInstructorId";
        
        try {
            instructorsLogic.verifyIsInstructorOfCourse(instructorId, courseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + instructorId
                    + " does not belong to course " + courseId, e.getMessage());
        }
    }
    
    @Test
    public void testVerifyIsInstructorEmailOfCourse() throws Exception  {
        restoreTypicalDataInDatastore();
        
        ______TS("success: instructor belongs to course");
        
        String instructorEmail = "instructor1@course1.com";
        String courseId = "idOfTypicalCourse1";
        instructorsLogic.verifyIsInstructorEmailOfCourse(instructorEmail, courseId);
        
        ______TS("failure: instructor doesn't belong to course");
        instructorEmail = "nonExistingInstructor@email.com";
        
        try {
            instructorsLogic.verifyIsInstructorEmailOfCourse(instructorEmail, courseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + instructorEmail
                    + " does not belong to course " + courseId, e.getMessage());
        }
    }
    
    @Test
    public void testIsNewInstructor() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("success: instructor with only 1 sample course");
        String instructorId = "idOfInstructorWithOnlyOneSampleCourse";
        assertEquals(true, instructorsLogic.isNewInstructor(instructorId));
        
        ______TS("success: instructor without any course");
        instructorId = "instructorWithoutCourses";
        assertEquals(true, instructorsLogic.isNewInstructor(instructorId));
        
        ______TS("failure: instructor is not new user");
        instructorId = "idOfInstructor1OfCourse1";
        assertEquals(false, instructorsLogic.isNewInstructor(instructorId));
        
    }

    @Test
    public void testUpdateInstructorByGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("typical case: update an instructor");
        
        String courseId = "idOfTypicalCourse1";
        String googleId = "idOfInstructor1OfCourse1";
        
        InstructorAttributes instructorToBeUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        instructorToBeUpdated.name = "New Name";
        instructorToBeUpdated.email = "new-email@course1.com";
        
        instructorsLogic.updateInstructorByGoogleId(googleId, instructorToBeUpdated);
        
        InstructorAttributes instructorUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        verifySameInstructor(instructorToBeUpdated, instructorUpdated);
    }
    
    @Test
    public void testUpdateInstructorByEmail() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("typical case: update an instructor");
        
        String email = "instructor1@course1.com";
        String courseId = "idOfTypicalCourse1";
        
        String newName = "New name for instructor 1";
        String newGoogleId = "newIdForInstructor1";
        
        InstructorAttributes instructorToBeUpdated = instructorsLogic.getInstructorForEmail(courseId, email);
        instructorToBeUpdated.googleId = newGoogleId;
        instructorToBeUpdated.name = newName;
        
        instructorsLogic.updateInstructorByEmail(email, instructorToBeUpdated);
        
        InstructorAttributes instructorUpdated = instructorsLogic.getInstructorForEmail(courseId, email);
        verifySameInstructor(instructorToBeUpdated, instructorUpdated);
    }
    
    @Test
    public void testDeleteInstructor() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("typical case: delete an instructor for specific course");
        
        String courseId = "idOfTypicalCourse1";
        String email = "instructor1@course1.com";
        
        InstructorAttributes instructorDeleted = instructorsLogic.getInstructorForEmail(courseId, email);
        
        instructorsLogic.deleteInstructor(courseId, email);
        
        TestHelper.verifyAbsentInDatastore(instructorDeleted);
    }

    @Test
    public void testDeleteInstructorsForGoogleId() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("typical case: delete all instructors for a given googleId");
        
        String googleId = "idOfInstructor3";
        
        instructorsLogic.deleteInstructorsForGoogleId(googleId);
        
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);
        
        assertEquals(true, instructorList.isEmpty());
    }

    @Test
    public void testDeleteInstructorsForCourse() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("typical case: delete all instructors of a given course");
        
        String courseId = "idOfTypicalCourse1";
        
        instructorsLogic.deleteInstructorsForCourse(courseId);
        
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
        
        assertEquals(true, instructorList.isEmpty());
    }

    
    private void verifySameInstructor(InstructorAttributes instructor1, InstructorAttributes instructor2) {
        assertEquals(instructor1.googleId, instructor2.googleId);
        assertEquals(instructor1.courseId, instructor2.courseId);
        assertEquals(instructor1.name, instructor2.name);
        assertEquals(instructor1.email, instructor2.email);
    }
    
    @AfterClass()
    public static void classTearDown() throws Exception {
        turnLoggingDown(InstructorsLogic.class);
    }

}
