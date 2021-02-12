package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.test.AssertHelper;
import teammates.test.BaseComponentTestCase;

/**
 * SUT: {@link InstructorsDb}.
 */
public class InstructorsDbTest extends BaseComponentTestCase {

    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private DataBundle dataBundle;

    @BeforeMethod
    public void addInstructorsToDb() throws Exception {
        dataBundle = getTypicalDataBundle();
        for (InstructorAttributes instructor : dataBundle.instructors.values()) {
            instructorsDb.putEntity(instructor);
        }
    }

    private void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {
        instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(courseId, googleId)
                        .withIsArchived(archiveStatus)
                        .build()
        );
    }

    @Test
    public void testCreateInstructor() throws Exception {

        ______TS("Success: create an instructor");

        String googleId = "valid.fresh.id";
        String courseId = "valid.course.Id";
        String name = "valid.name";
        String email = "valid@email.tmt";
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes i = InstructorAttributes.builder(courseId, email)
                .withGoogleId(googleId)
                .withName(name)
                .withRole(role)
                .withDisplayedName(displayedName)
                .withPrivileges(privileges)
                .build();

        instructorsDb.deleteInstructor(i.getCourseId(), i.getEmail());
        instructorsDb.createEntity(i);

        verifyPresentInDatastore(i);

        ______TS("Failure: create a duplicate instructor");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> instructorsDb.createEntity(i));
        assertEquals(
                String.format(InstructorsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, i.toString()), eaee.getMessage());

        ______TS("Failure: create an instructor with invalid parameters");

        i.googleId = "invalid id with spaces";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> instructorsDb.createEntity(i));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.GOOGLE_ID_ERROR_MESSAGE, i.googleId,
                        FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.GOOGLE_ID_MAX_LENGTH),
                ipe.getMessage());

        i.googleId = "valid.fresh.id";
        i.email = "invalid.email.tmt";
        i.role = "role invalid";
        ipe = assertThrows(InvalidParametersException.class, () -> instructorsDb.createEntity(i));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, i.email,
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                        + String.format(FieldValidator.ROLE_ERROR_MESSAGE, i.role),
                ipe.getMessage());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class, () -> instructorsDb.createEntity(null));

    }

    @Test
    public void testGetInstructorForEmail() {

        InstructorAttributes i = dataBundle.instructors.get("instructor1OfCourse1");

        ______TS("Success: get an instructor");

        InstructorAttributes retrieved = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNotNull(retrieved);

        ______TS("Failure: instructor does not exist");

        retrieved = instructorsDb.getInstructorForEmail("non.existent.course", "non.existent");
        assertNull(retrieved);

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class,
                () -> instructorsDb.getInstructorForEmail(null, null));

    }

    @Test
    public void testGetInstructorForGoogleId() {

        InstructorAttributes i = dataBundle.instructors.get("instructor1OfCourse1");

        ______TS("Success: get an instructor");

        InstructorAttributes retrieved = instructorsDb.getInstructorForGoogleId(i.courseId, i.googleId);
        assertNotNull(retrieved);

        ______TS("Failure: instructor does not exist");

        retrieved = instructorsDb.getInstructorForGoogleId("non.existent.course", "non.existent");
        assertNull(retrieved);

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class, () -> instructorsDb.getInstructorForGoogleId(null, null));

    }

    @Test
    public void testGetInstructorForRegistrationKey() {

        InstructorAttributes i = dataBundle.instructors.get("instructorNotYetJoinCourse");
        i = instructorsDb.getInstructorById(i.getCourseId(), i.getEmail());

        ______TS("Success: get an instructor");

        String key = i.key;

        InstructorAttributes retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertEquals(i.courseId, retrieved.courseId);
        assertEquals(i.name, retrieved.name);
        assertEquals(i.email, retrieved.email);

        ______TS("Failure: instructor does not exist");

        key = "non.existent.key";
        retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertNull(retrieved);

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class,
                () -> instructorsDb.getInstructorForRegistrationKey(null));

    }

    @Test
    public void testGetInstructorsForGoogleId() throws Exception {

        ______TS("Success: get instructors with specific googleId");

        String googleId = "idOfInstructor3";

        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForGoogleId(googleId, false);
        assertEquals(2, retrieved.size());

        InstructorAttributes instructor1 = retrieved.get(0);
        InstructorAttributes instructor2 = retrieved.get(1);

        assertEquals("idOfTypicalCourse1", instructor1.courseId);
        assertEquals("idOfTypicalCourse2", instructor2.courseId);

        ______TS("Success: get instructors with specific googleId, with 1 archived course.");

        setArchiveStatusOfInstructor(googleId, instructor1.courseId, true);
        retrieved = instructorsDb.getInstructorsForGoogleId(googleId, true);
        assertEquals(1, retrieved.size());
        setArchiveStatusOfInstructor(googleId, instructor1.courseId, false);

        ______TS("Failure: instructor does not exist");

        retrieved = instructorsDb.getInstructorsForGoogleId("non-exist-id", false);
        assertEquals(0, retrieved.size());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class,
                () -> instructorsDb.getInstructorsForGoogleId(null, false));

    }

    @Test
    public void testGetInstructorsForCourse() {

        ______TS("Success: get instructors of a specific course");

        String courseId = "idOfTypicalCourse1";

        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(5, retrieved.size());

        List<String> idList = new ArrayList<>();
        idList.add("idOfInstructor1OfCourse1");
        idList.add("idOfInstructor2OfCourse1");
        idList.add("idOfInstructor3");
        idList.add("idOfHelperOfCourse1");
        idList.add(null);
        for (InstructorAttributes instructor : retrieved) {
            if (!idList.contains(instructor.googleId)) {
                fail("");
            }
        }

        ______TS("Failure: no instructors for a course");

        retrieved = instructorsDb.getInstructorsForCourse("non-exist-course");
        assertEquals(0, retrieved.size());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class, () -> instructorsDb.getInstructorsForCourse(null));
    }

    @Test
    public void testGetInstructorsDisplayedToStudents() {

        ______TS("Success: get instructors displayed of a specific course to the students");

        String courseId = "idOfTypicalCourse1";

        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsDisplayedToStudents(courseId);
        assertEquals(4, retrieved.size());

        List<String> idListOfInstructorsDisplayed = new ArrayList<>();
        idListOfInstructorsDisplayed.add("idOfInstructor1OfCourse1");
        idListOfInstructorsDisplayed.add("idOfInstructor2OfCourse1");
        idListOfInstructorsDisplayed.add("idOfInstructor3");
        idListOfInstructorsDisplayed.add(null);
        for (InstructorAttributes instructor : retrieved) {
            if (!idListOfInstructorsDisplayed.contains(instructor.googleId)) {
                fail("");
            }
        }

        ______TS("Failure: no instructors displayed to the student for a course");
        retrieved = instructorsDb.getInstructorsDisplayedToStudents("non-exist-course");
        assertEquals(0, retrieved.size());

        ______TS("Failure: null parameters");
        assertThrows(AssertionError.class,
                () -> instructorsDb.getInstructorsDisplayedToStudents(null));
    }

    @Test
    public void testUpdateInstructorByGoogleId() throws Exception {

        InstructorAttributes instructorToEdit = dataBundle.instructors.get("instructor2OfCourse1");

        ______TS("Success: update an instructor");

        instructorToEdit.name = "New Name";
        instructorToEdit.email = "InstrDbT.new-email@email.tmt";
        instructorToEdit.isArchived = true;
        instructorToEdit.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;
        instructorToEdit.isDisplayedToStudents = false;
        instructorToEdit.displayedName = "New Displayed Name";
        instructorToEdit.privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(instructorToEdit.courseId, instructorToEdit.googleId)
                        .withName(instructorToEdit.name)
                        .withEmail(instructorToEdit.email)
                        .withIsArchived(instructorToEdit.isArchived)
                        .withRole(instructorToEdit.role)
                        .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents)
                        .withDisplayedName(instructorToEdit.displayedName)
                        .withPrivileges(instructorToEdit.privileges)
                        .build());

        InstructorAttributes actualInstructor =
                instructorsDb.getInstructorForGoogleId(instructorToEdit.courseId, instructorToEdit.googleId);
        assertEquals(instructorToEdit.name, actualInstructor.name);
        assertEquals(instructorToEdit.name, updatedInstructor.name);
        assertEquals(instructorToEdit.email, actualInstructor.email);
        assertEquals(instructorToEdit.email, updatedInstructor.email);
        assertTrue(actualInstructor.isArchived);
        assertTrue(updatedInstructor.isArchived);
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, actualInstructor.role);
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, updatedInstructor.role);
        assertFalse(actualInstructor.isDisplayedToStudents);
        assertFalse(updatedInstructor.isDisplayedToStudents);
        assertEquals("New Displayed Name", actualInstructor.displayedName);
        assertEquals("New Displayed Name", updatedInstructor.displayedName);
        assertTrue(actualInstructor.getPrivileges().hasObserverPrivileges());
        assertTrue(updatedInstructor.getPrivileges().hasObserverPrivileges());
        // Verifying less privileged 'Observer' role did not return false positive in case old 'Manager' role is unchanged.
        assertFalse(actualInstructor.getPrivileges().hasManagerPrivileges());
        assertFalse(updatedInstructor.getPrivileges().hasManagerPrivileges());

        ______TS("Failure: invalid parameters");

        instructorToEdit.name = "";
        instructorToEdit.email = "aaa";
        instructorToEdit.role = "invalid role";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> instructorsDb.updateInstructorByGoogleId(
                        InstructorAttributes
                                .updateOptionsWithGoogleIdBuilder(instructorToEdit.courseId, instructorToEdit.googleId)
                                .withName(instructorToEdit.name)
                                .withEmail(instructorToEdit.email)
                                .withRole(instructorToEdit.role)
                                .build()));
        AssertHelper.assertContains(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH)
                        + System.lineSeparator()
                        + getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, instructorToEdit.email,
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                        + String.format(FieldValidator.ROLE_ERROR_MESSAGE, instructorToEdit.role),
                ipe.getMessage());

        ______TS("Failure: non-existent entity");

        InstructorAttributes.UpdateOptionsWithGoogleId updateOptions =
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(instructorToEdit.courseId, "idOfInstructor4")
                        .withName("John Doe")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> instructorsDb.updateInstructorByGoogleId(updateOptions));
        assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + updateOptions, ednee.getMessage());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class,
                () -> instructorsDb.updateInstructorByGoogleId(null));
    }

    @Test
    public void testUpdateInstructorByGoogleId_noChangeToInstructor_shouldNotIssueSaveRequest() throws Exception {
        InstructorAttributes instructorToEdit =
                instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");

        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(
                        instructorToEdit.getCourseId(), instructorToEdit.getGoogleId())
                        .build());

        assertEquals(JsonUtils.toJson(instructorToEdit), JsonUtils.toJson(updatedInstructor));

        // please verify that the log message manually to ensure that saving request is not issued

        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(
                        instructorToEdit.getCourseId(), instructorToEdit.getGoogleId())
                        .withName(instructorToEdit.getName())
                        .withEmail(instructorToEdit.getEmail())
                        .withIsArchived(instructorToEdit.isArchived())
                        .withRole(instructorToEdit.getRole())
                        .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents())
                        .withDisplayedName(instructorToEdit.getDisplayedName())
                        .withPrivileges(new InstructorPrivileges(instructorToEdit.getRole()))
                        .build());

        assertEquals(JsonUtils.toJson(instructorToEdit), JsonUtils.toJson(updatedInstructor));

        // please verify that the log message manually to ensure that saving request is not issued
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateInstructorByGoogleId_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        InstructorAttributes typicalInstructor =
                instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");

        assertNotEquals("test@email.com", typicalInstructor.getEmail());
        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withEmail("test@email.com")
                        .build());
        InstructorAttributes actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertEquals("test@email.com", updatedInstructor.getEmail());
        assertEquals("test@email.com", actualInstructor.getEmail());

        assertNotEquals("testName", actualInstructor.getName());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withName("testName")
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertEquals("testName", updatedInstructor.getName());
        assertEquals("testName", actualInstructor.getName());

        assertFalse(actualInstructor.isArchived());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withIsArchived(true)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertTrue(updatedInstructor.isArchived());
        assertTrue(actualInstructor.isArchived());

        assertNotEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, actualInstructor.getRole());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withRole(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, updatedInstructor.getRole());
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, actualInstructor.getRole());

        assertTrue(actualInstructor.isDisplayedToStudents());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withIsDisplayedToStudents(false)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertFalse(updatedInstructor.isDisplayedToStudents());
        assertFalse(actualInstructor.isDisplayedToStudents());

        assertNotEquals("testName", actualInstructor.getDisplayedName());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withDisplayedName("testName")
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertEquals("testName", updatedInstructor.getDisplayedName());
        assertEquals("testName", actualInstructor.getDisplayedName());

        assertNotEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                actualInstructor.getPrivileges());
        updatedInstructor = instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes
                        .updateOptionsWithGoogleIdBuilder(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId())
                        .withPrivileges(
                                new InstructorPrivileges(
                                        Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER))
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId());
        assertEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                updatedInstructor.getPrivileges());
        assertEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                actualInstructor.getPrivileges());
    }

    @Test
    public void testUpdateInstructorByEmail_noChangeToInstructor_shouldNotIssueSaveRequest() throws Exception {
        InstructorAttributes instructorToEdit =
                instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");

        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(instructorToEdit.getCourseId(), instructorToEdit.getEmail())
                        .build());

        assertEquals(JsonUtils.toJson(instructorToEdit), JsonUtils.toJson(updatedInstructor));

        // please verify the log message manually to ensure that saving request is not issued

        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes.updateOptionsWithEmailBuilder(
                        instructorToEdit.getCourseId(), instructorToEdit.getEmail())
                        .withName(instructorToEdit.getName())
                        .withGoogleId(instructorToEdit.getGoogleId())
                        .withIsArchived(instructorToEdit.isArchived())
                        .withRole(instructorToEdit.getRole())
                        .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents())
                        .withDisplayedName(instructorToEdit.getDisplayedName())
                        .withPrivileges(new InstructorPrivileges(instructorToEdit.getRole()))
                        .build());

        assertEquals(JsonUtils.toJson(instructorToEdit), JsonUtils.toJson(updatedInstructor));

        // please verify the log message manually to ensure that saving request is not issued
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateInstructorByEmail_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        InstructorAttributes typicalInstructor =
                instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");

        assertNotNull(typicalInstructor.getGoogleId());
        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withGoogleId(null)
                        .build());
        InstructorAttributes actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertNull(updatedInstructor.getGoogleId());
        assertNull(actualInstructor.getGoogleId());

        assertNotEquals("testName", actualInstructor.getName());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withName("testName")
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertEquals("testName", updatedInstructor.getName());
        assertEquals("testName", actualInstructor.getName());

        assertFalse(actualInstructor.isArchived());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withIsArchived(true)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertTrue(updatedInstructor.isArchived());
        assertTrue(actualInstructor.isArchived());

        assertNotEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, actualInstructor.getRole());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withRole(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, updatedInstructor.getRole());
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, actualInstructor.getRole());

        assertTrue(actualInstructor.isDisplayedToStudents());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withIsDisplayedToStudents(false)
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertFalse(updatedInstructor.isDisplayedToStudents());
        assertFalse(actualInstructor.isDisplayedToStudents());

        assertNotEquals("testName", actualInstructor.getDisplayedName());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withDisplayedName("testName")
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertEquals("testName", updatedInstructor.getDisplayedName());
        assertEquals("testName", actualInstructor.getDisplayedName());

        assertNotEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                actualInstructor.getPrivileges());
        updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(typicalInstructor.getCourseId(), typicalInstructor.getEmail())
                        .withPrivileges(
                                new InstructorPrivileges(
                                        Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER))
                        .build());
        actualInstructor =
                instructorsDb.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        assertEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                updatedInstructor.getPrivileges());
        assertEquals(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER),
                actualInstructor.getPrivileges());
    }

    @Test
    public void testUpdateInstructorByEmail() throws Exception {

        InstructorAttributes instructorToEdit =
                instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");

        ______TS("Success: update an instructor");

        instructorToEdit.googleId = "new-id";
        instructorToEdit.name = "New Name";
        instructorToEdit.isArchived = true;
        instructorToEdit.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;
        instructorToEdit.isDisplayedToStudents = false;
        instructorToEdit.displayedName = "New Displayed Name";
        instructorToEdit.privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByEmail(
                InstructorAttributes.updateOptionsWithEmailBuilder(instructorToEdit.courseId, instructorToEdit.email)
                        .withGoogleId(instructorToEdit.googleId)
                        .withName(instructorToEdit.name)
                        .withIsArchived(instructorToEdit.isArchived)
                        .withRole(instructorToEdit.role)
                        .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents)
                        .withDisplayedName(instructorToEdit.displayedName)
                        .withPrivileges(instructorToEdit.privileges)
                        .build());

        InstructorAttributes actualInstructor =
                instructorsDb.getInstructorForEmail(instructorToEdit.courseId, instructorToEdit.email);
        assertEquals("new-id", actualInstructor.googleId);
        assertEquals("new-id", updatedInstructor.googleId);
        assertEquals("New Name", actualInstructor.name);
        assertEquals("New Name", updatedInstructor.name);
        assertTrue(actualInstructor.isArchived);
        assertTrue(updatedInstructor.isArchived);
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, actualInstructor.role);
        assertEquals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, updatedInstructor.role);
        assertFalse(actualInstructor.isDisplayedToStudents);
        assertFalse(updatedInstructor.isDisplayedToStudents);
        assertEquals("New Displayed Name", actualInstructor.displayedName);
        assertEquals("New Displayed Name", updatedInstructor.displayedName);
        assertTrue(actualInstructor.getPrivileges().hasObserverPrivileges());
        assertTrue(updatedInstructor.getPrivileges().hasObserverPrivileges());
        // Verifying less privileged 'Observer' role did not return false positive in case old 'CoOwner' role is unchanged.
        assertFalse(actualInstructor.hasCoownerPrivileges());
        assertFalse(updatedInstructor.hasCoownerPrivileges());

        ______TS("Failure: invalid parameters");

        instructorToEdit.googleId = "invalid id";
        instructorToEdit.name = "";
        instructorToEdit.role = "invalid role";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> instructorsDb.updateInstructorByEmail(
                        InstructorAttributes.updateOptionsWithEmailBuilder(instructorToEdit.courseId, instructorToEdit.email)
                                .withGoogleId(instructorToEdit.googleId)
                                .withName(instructorToEdit.name)
                                .withRole(instructorToEdit.role)
                                .build()));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.GOOGLE_ID_ERROR_MESSAGE, instructorToEdit.googleId,
                        FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.GOOGLE_ID_MAX_LENGTH) + System.lineSeparator()
                        + getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH)
                        + System.lineSeparator()
                        + String.format(FieldValidator.ROLE_ERROR_MESSAGE, instructorToEdit.role),
                ipe.getMessage());

        ______TS("Failure: non-existent entity");

        instructorToEdit.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER;
        InstructorAttributes.UpdateOptionsWithEmail updateOptions =
                InstructorAttributes.updateOptionsWithEmailBuilder(instructorToEdit.courseId, "random@email.tmt")
                        .withGoogleId("idOfInstructor4")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> instructorsDb.updateInstructorByEmail(updateOptions));
        assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + updateOptions, ednee.getMessage());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class,
                () -> instructorsDb.updateInstructorByEmail(null));

    }

    @Test
    public void testDeleteInstructor() {
        InstructorAttributes i = dataBundle.instructors.get("instructorWithOnlyOneSampleCourse");

        assertNotNull(instructorsDb.getInstructorForEmail(i.getCourseId(), i.getEmail()));

        ______TS("Delete non-existent instructor");

        instructorsDb.deleteInstructor("not_exist", i.getEmail());
        assertNotNull(instructorsDb.getInstructorForEmail(i.getCourseId(), i.getEmail()));

        instructorsDb.deleteInstructor(i.getCourseId(), "notExistent@email.com");
        assertNotNull(instructorsDb.getInstructorForEmail(i.getCourseId(), i.getEmail()));

        instructorsDb.deleteInstructor("not_exist", "notExistent@email.com");
        assertNotNull(instructorsDb.getInstructorForEmail(i.getCourseId(), i.getEmail()));

        ______TS("Success: delete an instructor");

        instructorsDb.deleteInstructor(i.courseId, i.email);

        InstructorAttributes deleted = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNull(deleted);

        ______TS("Failure: delete instructor again, should fail silently");

        instructorsDb.deleteInstructor(i.courseId, i.email);
        assertNull(instructorsDb.getInstructorForEmail(i.getCourseId(), i.getEmail()));

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class, () -> instructorsDb.deleteInstructor(null, null));

    }

    @Test
    public void testDeleteInstructors_byCourseId_shouldDeleteInstructorsAssociatedWithTheCourse() {

        ______TS("Success: delete instructors of a specific course");

        String courseId = "idOfArchivedCourse";
        instructorsDb.deleteInstructors(AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build());

        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(0, retrieved.size());

        // other course is not affected
        assertFalse(instructorsDb.getInstructorsForCourse("idOfTypicalCourse2").isEmpty());

        ______TS("Failure: non-existent course, should fail silently");

        instructorsDb.deleteInstructors(AttributesDeletionQuery.builder()
                .withCourseId("not-exist")
                .build());

        // other course is not affected
        assertFalse(instructorsDb.getInstructorsForCourse("idOfTypicalCourse2").isEmpty());

        ______TS("Failure: no instructor exists for the course, should fail silently");

        instructorsDb.deleteInstructors(AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build());

        assertEquals(0, instructorsDb.getInstructorsForCourse(courseId).size());

        // other course is not affected
        assertFalse(instructorsDb.getInstructorsForCourse("idOfTypicalCourse2").isEmpty());

        ______TS("Failure: null parameters");

        assertThrows(AssertionError.class, () -> instructorsDb.deleteInstructors(null));

    }
}
