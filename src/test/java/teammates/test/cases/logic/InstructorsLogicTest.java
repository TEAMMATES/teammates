package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class InstructorsLogicTest extends BaseComponentTestCase{

    private static DataBundle dataBundle = getTypicalDataBundle();;

    private static InstructorsLogic instructorsLogic = new InstructorsLogic();
    private static InstructorsDb instructorsDb = new InstructorsDb();
    private static CoursesLogic coursesLogic = new CoursesLogic();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorsLogic.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }

    @Test
    public void testAll() throws Exception{
        testGetInstructorForEmail();
        testGetInstructorsForEmail();
        testGetInstructorForGoogleId();
        testGetInstructorsForGoogleId();
        testGetInstructorForRegistrationKey();
        testGetInstructorsForCourse();
        testGetKeyForInstructor();
        testIsGoogleIdOfInstructorOfCourse();
        testIsEmailOfInstructorOfCourse();
        testVerifyInstructorExists();
        testVerifyIsGoogleIdOfInstructorOfCourse();
        testVerifyIsEmailOfInstructorOfCourse();
        testIsNewInstructor();
        testAddInstructor();
        testUpdateInstructorByGoogleId();
        testUpdateInstructorByEmail();
        testDeleteInstructor();
        testDeleteInstructorsForGoogleId();
        testDeleteInstructorsForCourse();
        testSendRegistrationInviteToInstructor();
    }
    
    public void testAddInstructor() throws Exception {
        
        ______TS("success: add an instructor");
        
        String googleId = null;
        String courseId = "test-course";
        String name = "New Instructor";
        String email = "ILT.instr@email.tmt";
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);      
        InstructorAttributes instr = new InstructorAttributes(googleId, courseId, name, email, role, displayedName, privileges);
        
        instructorsLogic.createInstructor(instr);
        
        TestHelper.verifyPresentInDatastore(instr);
        
        ______TS("failure: instructor already exists");
        
        try {
            instructorsLogic.createInstructor(instr);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains("Trying to create a Instructor that exists", e.getMessage());
        }
        
        instructorsLogic.deleteInstructorCascade(instr.courseId, instr.email);
        
        ______TS("failure: invalid parameter");
        
        instr.email = "invalidEmail.tmt";
        
        try {
            instructorsLogic.createInstructor(instr);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("\""+instr.email+"\" is not acceptable to TEAMMATES as an email",
                                e.getMessage());
        }
        
        ______TS("failure: null parameters");
        
        try {
            instructorsLogic.createInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }    
    }   
    
    public void testGetInstructorForEmail() throws Exception {
        
        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForEmail("idOfTypicalCourse1", "non-exist@email.tmt"));

        ______TS("success: get an instructor by using email");

        String courseId = "idOfTypicalCourse1";
        String email = "instructor1@course1.tmt";
        
        InstructorAttributes instr = instructorsLogic.getInstructorForEmail(courseId, email);
        
        assertEquals(courseId, instr.courseId);
        assertEquals(email, instr.email);
        assertEquals("idOfInstructor1OfCourse1", instr.googleId);
        assertEquals("Instructor1 Course1", instr.name);

        ______TS("failure: null parameters");
        
        try {
            instructorsLogic.getInstructorForEmail(null, email);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            instructorsLogic.getInstructorForEmail(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
    }
    
    public void testGetInstructorForGoogleId() throws Exception {
        
        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForGoogleId("idOfTypicalCourse1", "non-exist-id"));

        ______TS("success: typical case");

        String courseId = "idOfTypicalCourse1";
        String googleId = "idOfInstructor1OfCourse1";
        
        InstructorAttributes instr = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        
        assertEquals(courseId, instr.courseId);
        assertEquals(googleId, instr.googleId);
        assertEquals("instructor1@course1.tmt", instr.email);
        assertEquals("Instructor1 Course1", instr.name);

        ______TS("failure: null parameters");
        
        try {
            instructorsLogic.getInstructorForGoogleId(null, googleId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            instructorsLogic.getInstructorForGoogleId(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
    }
    
    public void testGetInstructorForRegistrationKey() throws Exception {
        
        ______TS("failure: instructor doesn't exist");
        String key = "non-existing-key";
        assertNull(instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key)));

        ______TS("success: typical case");

        String courseId = "idOfSampleCourse-demo";
        String email = "instructorNotYetJoined@email.tmt";
        
        InstructorAttributes instr = instructorsDb.getInstructorForEmail(courseId, email);
        key = instr.key;
        
        InstructorAttributes retrieved = instructorsLogic.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        
        assertEquals(instr.courseId, retrieved.courseId);
        assertEquals(instr.name, retrieved.name);
        assertEquals(instr.email, retrieved.email);
        
        ______TS("failure: null parameter");
        try {
            instructorsLogic.getInstructorForRegistrationKey(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testGetInstructorsForCourse() throws Exception {

        ______TS("success: get all instructors for a course");

        String courseId = "idOfTypicalCourse1";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(5, instructors.size());
        
        HashMap<String, Boolean> idMap = new HashMap<String, Boolean>();
        idMap.put("idOfInstructor1OfCourse1", false);
        idMap.put("idOfInstructor2OfCourse1", false);
        idMap.put("idOfInstructor3", false);
        
        for (InstructorAttributes i : instructors) {
            if (idMap.containsKey(i.googleId)) {
                idMap.put(i.googleId, true);
            }
        }
        
        assertTrue(idMap.get("idOfInstructor1OfCourse1").booleanValue());
        assertTrue(idMap.get("idOfInstructor2OfCourse1").booleanValue());
        assertTrue(idMap.get("idOfInstructor3").booleanValue());
        
        ______TS("failure: no instructors for a given course");
        
        courseId = "new-course";
        coursesLogic.createCourse(courseId, "New course");
        
        instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(0, instructors.size());

        ______TS("failure: null parameters");

        try {
            instructorsLogic.getInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e){
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testGetInstructorsForGoogleId() throws Exception {
        
        ______TS("success: get all instructors for a google id");

        String googleId = "idOfInstructor3";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
        assertEquals(2, instructors.size());
        
        InstructorAttributes instructor1 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse1", googleId);
        InstructorAttributes instructor2 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse2", googleId);
        
        verifySameInstructor(instructor1, instructors.get(0));
        verifySameInstructor(instructor2, instructors.get(1));
        
        ______TS("failure: non-exist google id");
        
        googleId = "non-exist-id";
        
        instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
        assertEquals(0, instructors.size());

        ______TS("failure: null parameter");

        try {
            instructorsLogic.getInstructorsForGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError e){
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testGetInstructorsForEmail() throws Exception {
        
        ______TS("success: get all instructors for a google id");

        String email = "instructor3@course1.tmt";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForEmail(email);
        assertEquals(1, instructors.size());
        
        InstructorAttributes instructor1 = instructorsDb.getInstructorForEmail("idOfTypicalCourse1", email); 
        verifySameInstructor(instructor1, instructors.get(0));
        
        ______TS("failure: non-exist email");
        
        email = "non-exist-email@course1.tmt";
        
        instructors = instructorsLogic.getInstructorsForEmail(email);
        assertEquals(0, instructors.size());

        ______TS("failure: null parameter");

        try {
            instructorsLogic.getInstructorsForEmail(null);
            signalFailureToDetectException();
        } catch (AssertionError e){
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testGetKeyForInstructor() throws Exception {
    
        ______TS("success: get encrypted key for instructor");
        
        String courseId = "idOfSampleCourse-demo";
        String email = "instructorNotYetJoined@email.tmt";
        
        InstructorAttributes instructor = instructorsDb.getInstructorForEmail(courseId, email);
        
        String key = instructorsLogic.getKeyForInstructor(instructor.courseId, instructor.email);
        String expected = instructor.key;
        assertEquals(expected, key);
        
        ______TS("failure: non-existent instructor");

        try {
            instructorsLogic.getKeyForInstructor(courseId, "non-existent@email.tmt");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + "non-existent@email.tmt"
                    + " does not belong to course " + courseId, e.getMessage());
        }

        ______TS("failure: null parameter");

        try {
            instructorsLogic.getKeyForInstructor(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.getKeyForInstructor(null, email);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }
    
    public void testIsGoogleIdOfInstructorOfCourse() throws Exception {
        
        ______TS("success: is an instructor of a given course");

        String instructorId = "idOfInstructor1OfCourse1";
        String courseId = "idOfTypicalCourse1";
        
        boolean result = instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId, courseId);
        
        assertEquals(true, result);
        
        ______TS("failure: not an instructor of a given course");

        courseId = "idOfTypicalCourse2";
        
        result = instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId, courseId);
        
        assertEquals(false, result);

        ______TS("failure: null parameter");

        try {
            instructorsLogic.isGoogleIdOfInstructorOfCourse(null, courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testIsEmailOfInstructorOfCourse() throws Exception {
        
        ______TS("success: is an instructor of a given course");

        String instructorEmail = "instructor1@course1.tmt";
        String courseId = "idOfTypicalCourse1";
        
        boolean result = instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail, courseId);
        
        assertEquals(true, result);
        
        ______TS("failure: not an instructor of a given course");

        courseId = "idOfTypicalCourse2";
        
        result = instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail, courseId);
        
        assertEquals(false, result);

        ______TS("failure: null parameter");

        try {
            instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.isEmailOfInstructorOfCourse(null, courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }

    public void testVerifyInstructorExists() throws Exception  {
        
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

        ______TS("failure: null parameter");

        try {
            instructorsLogic.verifyInstructorExists(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testVerifyIsGoogleIdOfInstructorOfCourse() throws Exception  {
        
        ______TS("success: instructor belongs to course");
        
        String instructorId = "idOfInstructor1OfCourse1";
        String courseId = "idOfTypicalCourse1";
        instructorsLogic.verifyIsGoogleIdOfInstructorOfCourse(instructorId, courseId);
        
        ______TS("failure: instructor doesn't belong to course");
        
        instructorId = "nonExistingInstructorId";
        
        try {
            instructorsLogic.verifyIsGoogleIdOfInstructorOfCourse(instructorId, courseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + instructorId
                    + " does not belong to course " + courseId, e.getMessage());
        }

        ______TS("failure: null parameter");

        try {
            instructorsLogic.verifyIsGoogleIdOfInstructorOfCourse(null, courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.verifyIsGoogleIdOfInstructorOfCourse(instructorId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testVerifyIsEmailOfInstructorOfCourse() throws Exception  {
        
        ______TS("success: instructor belongs to course");
        
        String instructorEmail = "instructor1@course1.tmt";
        String courseId = "idOfTypicalCourse1";
        instructorsLogic.verifyIsEmailOfInstructorOfCourse(instructorEmail, courseId);
        
        ______TS("failure: instructor doesn't belong to course");
        instructorEmail = "nonExistingInstructor@email.tmt";
        
        try {
            instructorsLogic.verifyIsEmailOfInstructorOfCourse(instructorEmail, courseId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + instructorEmail
                    + " does not belong to course " + courseId, e.getMessage());
        }
        ______TS("failure: null parameter");

        try {
            instructorsLogic.verifyIsEmailOfInstructorOfCourse(null, courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.verifyIsGoogleIdOfInstructorOfCourse(instructorEmail, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testIsNewInstructor() throws Exception {
        
        ______TS("success: instructor with only 1 sample course");
        
        String instructorId = "idOfInstructorWithOnlyOneSampleCourse";
        assertEquals(true, instructorsLogic.isNewInstructor(instructorId));
        
        ______TS("success: instructor without any course");
        
        instructorId = "instructorWithoutCourses";
        assertEquals(true, instructorsLogic.isNewInstructor(instructorId));
        
        ______TS("failure: instructor with only 1 course, but not a sample course");

        instructorId = "idOfInstructor4";
        assertEquals(false, instructorsLogic.isNewInstructor(instructorId));

        ______TS("failure: instructor is not new user");
        
        instructorId = "idOfInstructor1OfCourse1";
        assertEquals(false, instructorsLogic.isNewInstructor(instructorId));
        
        ______TS("failure: null parameter");

        try {
            instructorsLogic.isNewInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }

    public void testUpdateInstructorByGoogleId() throws Exception {
        
        ______TS("typical case: update an instructor");
        
        String courseId = "idOfTypicalCourse1";
        String googleId = "idOfInstructor2OfCourse1";
        
        InstructorAttributes instructorToBeUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        instructorToBeUpdated.name = "New Name";
        instructorToBeUpdated.email = "new-email@course1.tmt";
        
        instructorsLogic.updateInstructorByGoogleId(googleId, instructorToBeUpdated);
        
        InstructorAttributes instructorUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        verifySameInstructor(instructorToBeUpdated, instructorUpdated);

        ______TS("failure: instructor doesn't exist");
        
        instructorsLogic.deleteInstructorCascade(courseId, instructorUpdated.email);
        
        try {
            instructorsLogic.updateInstructorByGoogleId(googleId, instructorUpdated);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor "+googleId+" does not belong to course "+courseId, e.getMessage());
        }
        
        ______TS("failure: course doesn't exist");
        
        courseId = "random-course";
        instructorToBeUpdated.courseId = courseId;
        
        try {
            instructorsLogic.updateInstructorByGoogleId(googleId, instructorToBeUpdated);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Course does not exist: " + courseId, e.getMessage());
        }

        ______TS("failure: null parameter");

        try {
            instructorsLogic.updateInstructorByGoogleId(googleId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
    }
    
    public void testUpdateInstructorByEmail() throws Exception {
        
        ______TS("typical case: update an instructor");
        
        String email = "instructor1@course1.tmt";
        String courseId = "idOfTypicalCourse1";
        
        String newName = "New name for instructor 1";
        String newGoogleId = "newIdForInstructor1";
        
        InstructorAttributes instructorToBeUpdated = instructorsLogic.getInstructorForEmail(courseId, email);
        instructorToBeUpdated.googleId = newGoogleId;
        instructorToBeUpdated.name = newName;
        
        instructorsLogic.updateInstructorByEmail(email, instructorToBeUpdated);
        
        InstructorAttributes instructorUpdated = instructorsLogic.getInstructorForEmail(courseId, email);
        verifySameInstructor(instructorToBeUpdated, instructorUpdated);

        ______TS("failure: instructor doesn't belong to course");
        
        instructorsLogic.deleteInstructorCascade(courseId, instructorToBeUpdated.email);
        
        try {
            instructorsLogic.updateInstructorByEmail(email, instructorToBeUpdated);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + email + " does not belong to course " + courseId, e.getMessage());
        }
        
        ______TS("failure: course doesn't exist");
        
        courseId = "random-course";
        instructorToBeUpdated.courseId = courseId;
        
        try {
            instructorsLogic.updateInstructorByEmail(email, instructorToBeUpdated);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Course does not exist: " + courseId, e.getMessage());
        }
        
        ______TS("failure: null parameter");

        try {
            instructorsLogic.updateInstructorByEmail(email, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }
    
    public void testDeleteInstructor() throws Exception {
        
        ______TS("typical case: delete an instructor for specific course");
        
        String courseId = "idOfTypicalCourse1";
        String email = "instructor3@course1.tmt";
        
        InstructorAttributes instructorDeleted = instructorsLogic.getInstructorForEmail(courseId, email);
        
        instructorsLogic.deleteInstructorCascade(courseId, email);
        
        TestHelper.verifyAbsentInDatastore(instructorDeleted);
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment1FromI3C1toS2C1"));

        ______TS("typical case: delete a non-existent instructor");

        instructorsLogic.deleteInstructorCascade(courseId, "non-existent@course1.tmt"); 

        ______TS("failure: null parameter");

        try {
            instructorsLogic.deleteInstructorCascade(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

        try {
            instructorsLogic.deleteInstructorCascade(null, email);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        // restore deleted instructor
        instructorsLogic.createInstructor(instructorDeleted);
    }

    public void testDeleteInstructorsForGoogleId() throws Exception {
        
        ______TS("typical case: delete all instructors for a given googleId");
        
        String googleId = "idOfInstructor1";
        
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
        
        instructorsLogic.deleteInstructorsForGoogleIdAndCascade(googleId);
        
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);      
        assertEquals(instructorList.isEmpty(), true);
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment1FromI1C1toS1C1"));
        TestHelper.verifyAbsentInDatastore(dataBundle.comments.get("comment2FromI1C1toS1C1"));
        
        ______TS("typical case: delete an non-existent googleId");

        instructorsLogic.deleteInstructorsForGoogleIdAndCascade("non-existent");

        ______TS("failure: null parameter");

        try {
            instructorsLogic.deleteInstructorsForGoogleIdAndCascade(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        // restore deleted instructors
        for (InstructorAttributes instructor : instructors) {
            instructorsLogic.createInstructor(instructor);
        }
    }

    public void testDeleteInstructorsForCourse() throws Exception {
        
        ______TS("typical case: delete all instructors of a given course");
        
        String courseId = "idOfTypicalCourse1";
        
        instructorsLogic.deleteInstructorsForCourse(courseId);
        
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
        
        assertEquals(true, instructorList.isEmpty());

        ______TS("typical case: delete all instructors for a non-existent course");

        instructorsLogic.deleteInstructorsForCourse("non-existent");

        ______TS("failure case: null parameter");

        try {
            instructorsLogic.deleteInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    
    }

    public void testSendRegistrationInviteToInstructor() throws Exception {
       
        ______TS("success: send invite to instructor using instructor email");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
    
        MimeMessage email = instructorsLogic.sendRegistrationInviteToInstructor(instructor.courseId, instructor.email);
    
        TestHelper.verifyJoinInviteToInstructor(instructor, email);
    
        
        ______TS("success: send invite to instructor using instructor attributes");

        email = instructorsLogic.sendRegistrationInviteToInstructor(instructor.courseId, instructor);
    
        TestHelper.verifyJoinInviteToInstructor(instructor, email);
        
        ______TS("failure: send to non-existing instructor");
    
        String instrEmail = "non-existing-instr@email.tmt";
        
        try {
            instructorsLogic.sendRegistrationInviteToInstructor(instructor.courseId, instrEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Instructor [" + instrEmail + "] does not exist in course",
                        e.getMessage());
        }

        ______TS("failure: send invitation for a non-existent course");

        try {
            instructorsLogic.sendRegistrationInviteToInstructor("non-existent-courseId", instructor.email);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist [non-existent-courseId]", e.getMessage());
        }
        
        ______TS("failure: null parameters");
        
        try {
            instructorsLogic.sendRegistrationInviteToInstructor(null, instructor.email);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            String instructorEmail = null;
            instructorsLogic.sendRegistrationInviteToInstructor(instructor.courseId, instructorEmail);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

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
