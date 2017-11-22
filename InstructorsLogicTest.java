package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link InstructorsLogic}.
 */
public class InstructorsLogicTest extends BaseLogicTest {

    private static InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static InstructorsDb instructorsDb = new InstructorsDb();
    private static CoursesLogic coursesLogic = CoursesLogic.inst();

    @BeforeClass
    public void classSetup() {
        instructorsLogic.deleteInstructorCascade("FSQTT.idOfTypicalCourse1", "instructor3@course1.tmt");
    }

    @Test
    public void testAll() throws Exception {
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
        testVerifyIsEmailOfInstructorOfCourse();
        testIsNewInstructor();
        testAddInstructor();
        testGetCoOwnersForCourse();
        testUpdateInstructorByGoogleId();
        testUpdateInstructorByEmail();
        testDeleteInstructor();
        testDeleteInstructorsForGoogleId();
        testDeleteInstructorsForCourse();
        testGetAllInstructors();
        testPutSearchandDropDocument();
    }

    private void testAddInstructor() throws Exception {

        ______TS("success: add an instructor");

        String courseId = "test-course";
        String name = "New Instructor";
        String email = "ILT.instr@email.tmt";
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instr = InstructorAttributes.builder(null, courseId, name, email)
                .withRole(role)
                .withDisplayedName(displayedName)
                .withPrivileges(privileges)
                .build();

        instructorsLogic.createInstructor(instr);

        verifyPresentInDatastore(instr);

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
        String expectedError =
                "\"" + instr.email + "\" is not acceptable to TEAMMATES as a/an email "
                + "because it is not in the correct format. An email address contains "
                + "some text followed by one '@' sign followed by some more text. "
                + "It cannot be longer than 254 characters, cannot be empty and "
                + "cannot contain spaces.";
        try {
            instructorsLogic.createInstructor(instr);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(expectedError, e.getMessage());
        }

        ______TS("failure: null parameters");

        try {
            instructorsLogic.createInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    private void testGetInstructorForEmail() {

        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForEmail("idOfTypicalCourse1", "non-exist@email.tmt"));

        ______TS("success: get an instructor by using email");

        String[] courseId = {null, "idOfTypicalCourse1"};
        String[] email = {"instructor1@course1.tmt", null};

        InstructorAttributes instr = instructorsLogic.getInstructorForEmail(courseId[1], email[0]);
        assertEquals(courseId[1], instr.courseId);
        assertEquals(email[0], instr.email);
        assertEquals("idOfInstructor1OfCourse1", instr.googleId);
        assertEquals("Instructor1 Course1", instr.name);

        ______TS("failure: null parameters");

        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.getInstructorForEmail(courseId[i], email[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testGetInstructorForGoogleId() {

        ______TS("failure: instructor doesn't exist");

        assertNull(instructorsLogic.getInstructorForGoogleId("idOfTypicalCourse1", "non-exist-id"));

        ______TS("success: typical case");

        String[] courseId = {null, "idOfTypicalCourse1"};
        String[] googleId = {"idOfInstructor1OfCourse1", null};

        InstructorAttributes instr = instructorsLogic.getInstructorForGoogleId(courseId[1], googleId[0]);

        assertEquals(courseId[1], instr.courseId);
        assertEquals(googleId[0], instr.googleId);
        assertEquals("instructor1@course1.tmt", instr.email);
        assertEquals("Instructor1 Course1", instr.name);

        ______TS("failure: null parameters");

        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.getInstructorForGoogleId(courseId[i], googleId[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testGetInstructorForRegistrationKey() {

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

    private void testGetInstructorsForCourse() throws Exception {

        ______TS("success: get all instructors for a course");

        String courseId = "idOfTypicalCourse1";

        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(5, instructors.size());

        HashMap<String, Boolean> idMap = new HashMap<>();
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
        coursesLogic.createCourse(courseId, "New course", "UTC");

        instructors = instructorsLogic.getInstructorsForCourse(courseId);
        assertEquals(0, instructors.size());

        ______TS("failure: null parameters");

        try {
            instructorsLogic.getInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    private void testGetInstructorsForGoogleId() {

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
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    private void testGetInstructorsForEmail() {

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
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    private void testGetKeyForInstructor() throws Exception {

        ______TS("success: get encrypted key for instructor");

        String[] courseId = {null, "idOfSampleCourse-demo"};
        String[] email = {"instructorNotYetJoined@email.tmt", null, "non-existent@email.tmt"};

        InstructorAttributes instructor = instructorsDb.getInstructorForEmail(courseId[1], email[0]);

        String key = instructorsLogic.getEncryptedKeyForInstructor(instructor.courseId, instructor.email);
        String expected = StringHelper.encrypt(instructor.key);
        assertEquals(expected, key);

        ______TS("failure: non-existent instructor");

        try {
            instructorsLogic.getEncryptedKeyForInstructor(courseId[1], email[2]);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + "non-existent@email.tmt"
                    + " does not belong to course " + courseId[1], e.getMessage());
        }

        ______TS("failure: null parameter");
        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.getEncryptedKeyForInstructor(courseId[i], email[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testIsGoogleIdOfInstructorOfCourse() {

        ______TS("success: is an instructor of a given course");

        String[] courseId = {null, "idOfTypicalCourse1"};
        String[] instructorId = {"idOfInstructor1OfCourse1", null};

        boolean result = instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId[0], courseId[1]);

        assertTrue(result);

        ______TS("failure: not an instructor of a given course");

        courseId[1] = "idOfTypicalCourse2";

        result = instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId[0], courseId[1]);

        assertFalse(result);

        ______TS("failure: null parameter");
        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorId[i], courseId[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testIsEmailOfInstructorOfCourse() {

        ______TS("success: is an instructor of a given course");

        String[] instructorEmail = {"instructor1@course1.tmt", null};
        String[] courseId = {null, "idOfTypicalCourse1"};

        boolean result = instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail[0], courseId[1]);

        assertTrue(result);

        ______TS("failure: not an instructor of a given course");

        courseId[1] = "idOfTypicalCourse2";

        result = instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail[0], courseId[1]);

        assertFalse(result);

        ______TS("failure: null parameter");

        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.isEmailOfInstructorOfCourse(instructorEmail[i], courseId[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testVerifyInstructorExists() throws Exception {

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

    private void testVerifyIsEmailOfInstructorOfCourse() throws Exception {

        ______TS("success: instructor belongs to course");

        String[] instructorEmail = {null, "instructor1@course1.tmt"};
        String[] courseId = {"idOfTypicalCourse1", null};

        instructorsLogic.verifyIsEmailOfInstructorOfCourse(instructorEmail[1], courseId[0]);

        ______TS("failure: instructor doesn't belong to course");
        instructorEmail[1] = "nonExistingInstructor@email.tmt";

        try {
            instructorsLogic.verifyIsEmailOfInstructorOfCourse(instructorEmail[1], courseId[0]);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals("Instructor " + instructorEmail[1]
                    + " does not belong to course " + courseId[0], e.getMessage());
        }
        ______TS("failure: null parameter");

        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.verifyIsEmailOfInstructorOfCourse(instructorEmail[i], courseId[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }
    }

    private void testIsNewInstructor() {

        ______TS("success: instructor with only 1 sample course");

        String instructorId = "idOfInstructorWithOnlyOneSampleCourse";
        assertTrue(instructorsLogic.isNewInstructor(instructorId));

        ______TS("success: instructor without any course");

        instructorId = "instructorWithoutCourses";
        assertTrue(instructorsLogic.isNewInstructor(instructorId));

        ______TS("failure: instructor with only 1 course, but not a sample course");

        instructorId = "idOfInstructor4";
        assertFalse(instructorsLogic.isNewInstructor(instructorId));

        ______TS("failure: instructor is not new user");

        instructorId = "idOfInstructor1OfCourse1";
        assertFalse(instructorsLogic.isNewInstructor(instructorId));

        ______TS("failure: null parameter");

        try {
            instructorsLogic.isNewInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }

    private void testUpdateInstructorByGoogleId() throws Exception {

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
            assertEquals("Instructor " + googleId + " does not belong to course " + courseId, e.getMessage());
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

    private void testUpdateInstructorByEmail() throws Exception {

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

    private void testDeleteInstructor() throws Exception {

        ______TS("typical case: delete an instructor for specific course");

        String[] courseId = {"idOfTypicalCourse1", null};
        String[] email = {null, "instructor3@course1.tmt", "non-existent@course1.tmt"};

        InstructorAttributes instructorDeleted = instructorsLogic.getInstructorForEmail(courseId[0], email[1]);

        instructorsLogic.deleteInstructorCascade(courseId[0], email[1]);

        verifyAbsentInDatastore(instructorDeleted);

        ______TS("typical case: delete a non-existent instructor");

        instructorsLogic.deleteInstructorCascade(courseId[0], email[2]);

        ______TS("failure: null parameter");

        for (int i = 0; i < 2; i++) {

            try {
                instructorsLogic.deleteInstructorCascade(courseId[i], email[i]);
                signalFailureToDetectException();
            } catch (AssertionError e) {
                AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
            }
        }

        // restore deleted instructor
        instructorsLogic.createInstructor(instructorDeleted);
    }

    private void testDeleteInstructorsForGoogleId() throws Exception {
        ______TS("typical case: delete all instructors for a given googleId");

        String googleId = "idOfInstructor1";

        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForGoogleId(googleId);

        instructorsLogic.deleteInstructorsForGoogleIdAndCascade(googleId);

        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);
        assertTrue(instructorList.isEmpty());

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

    private void testDeleteInstructorsForCourse() {

        ______TS("typical case: delete all instructors of a given course");

        String courseId = "idOfTypicalCourse1";

        instructorsLogic.deleteInstructorsForCourse(courseId);

        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);

        assertTrue(instructorList.isEmpty());

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

    private void verifySameInstructor(InstructorAttributes instructor1, InstructorAttributes instructor2) {
        assertEquals(instructor1.googleId, instructor2.googleId);
        assertEquals(instructor1.courseId, instructor2.courseId);
        assertEquals(instructor1.name, instructor2.name);
        assertEquals(instructor1.email, instructor2.email);
    }

    private void testGetCoOwnersForCourse() {
        ______TS("Verify co-owner status of generated co-owners list");
        String courseId = "idOfTypicalCourse1";
        List<InstructorAttributes> generatedCoOwners = instructorsLogic.getCoOwnersForCourse(courseId);
        for (InstructorAttributes generatedCoOwner : generatedCoOwners) {
            assertTrue(generatedCoOwner.hasCoownerPrivileges());
        }

        ______TS("Verify all co-owners present in generated co-owners list");

        // Generate ArrayList<String> of emails of all coOwners in course from data bundle
        List<String> coOwnersEmailsFromDataBundle = new ArrayList<>();
        for (InstructorAttributes instructor : new ArrayList<>(dataBundle.instructors.values())) {
            if (!(instructor.getCourseId().equals(courseId) && instructor.hasCoownerPrivileges())) {
                continue;
            }
            coOwnersEmailsFromDataBundle.add(instructor.email);
        }

        // Generate ArrayList<String> of emails of all coOwners from instructorsLogic.getCoOwnersForCourse
        List<String> generatedCoOwnersEmails = new ArrayList<>();
        for (InstructorAttributes generatedCoOwner : generatedCoOwners) {
            generatedCoOwnersEmails.add(generatedCoOwner.email);
        }

        assertTrue(coOwnersEmailsFromDataBundle.containsAll(generatedCoOwnersEmails)
                && generatedCoOwnersEmails.containsAll(coOwnersEmailsFromDataBundle));
    }
    
    private void testGetAllInstructors() {
        instructorsLogic.getAllInstructors();
    }

    private void testPutSearchandDropDocument() throws InvalidParametersException, EntityAlreadyExistsException {
        String courseId = "test-PSD-ID";
        String name = "Test-PSD Name";
        String email = "Test-PSD.instr@email.tmt";
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instrPsd = InstructorAttributes.builder("testID", courseId, name, email)
                .withRole(role)
                .withDisplayedName(displayedName)
                .withPrivileges(privileges)
                .build();
        instructorsLogic.createInstructor(instrPsd);
        instructorsLogic.putDocument(instrPsd);
        instructorsLogic.searchInstructorsInWholeSystem(instrPsd.name);
        instructorsLogic.deleteDocument(instrPsd);
    }

}
