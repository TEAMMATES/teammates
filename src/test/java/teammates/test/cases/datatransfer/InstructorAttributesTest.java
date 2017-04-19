package teammates.test.cases.datatransfer;

import static teammates.common.util.Const.EOL;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link InstructorAttributes}.
 */
public class InstructorAttributesTest extends BaseTestCase {

    @Test
    public void testConstructor() {
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor =
                new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        assertEquals(roleName, instructor.role);
        assertEquals(displayedName, instructor.displayedName);
        assertEquals(privileges, instructor.privileges);

        InstructorAttributes instructor1 =
                new InstructorAttributes(instructor.googleId, instructor.courseId, instructor.name, instructor.email,
                                         instructor.role, instructor.displayedName,
                                         instructor.getTextFromInstructorPrivileges());

        assertEquals(privileges, instructor1.privileges);

        InstructorAttributes instructor2 =
                new InstructorAttributes(instructor.googleId, instructor.courseId, instructor.name, instructor.email,
                                         instructor.role, instructor.displayedName, instructor1.privileges);

        assertEquals(instructor1.privileges, instructor2.privileges);

        InstructorAttributes instructorNew =
                new InstructorAttributes(instructor.googleId, instructor.courseId, instructor.name, instructor.email,
                                         instructor.role, false, instructor.displayedName, instructor1.privileges);

        assertFalse(instructorNew.isDisplayedToStudents);

        Instructor entity = instructor2.toEntity();
        InstructorAttributes instructor3 = new InstructorAttributes(entity);

        assertEquals(instructor2.googleId, instructor3.googleId);
        assertEquals(instructor2.courseId, instructor3.courseId);
        assertEquals(instructor2.name, instructor3.name);
        assertEquals(instructor2.email, instructor3.email);
        assertEquals(instructor2.role, instructor3.role);
        assertEquals(instructor2.displayedName, instructor3.displayedName);
        assertEquals(instructor2.privileges, instructor3.privileges);

        entity.setRole(null);
        entity.setDisplayedName(null);
        entity.setInstructorPrivilegeAsText(null);
        InstructorAttributes instructor4 = new InstructorAttributes(entity);

        assertEquals(instructor2.googleId, instructor4.googleId);
        assertEquals(instructor2.courseId, instructor4.courseId);
        assertEquals(instructor2.name, instructor4.name);
        assertEquals(instructor2.email, instructor4.email);
        // default values for these
        assertEquals(instructor2.role, instructor4.role);
        assertEquals(instructor2.displayedName, instructor4.displayedName);
        assertEquals(instructor2.privileges, instructor4.privileges);
    }

    @Test
    public void testIsRegistered() {
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor =
                new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");
        assertTrue(instructor.isRegistered());

        instructor.googleId = null;
        assertFalse(instructor.isRegistered());
    }

    @Test
    public void testToEntity() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor =
                new InstructorAttributes(googleId, courseId, name, email, roleName, displayedName, privileges);
        String key = "randomKey";
        instructor.key = key;

        Instructor entity = instructor.toEntity();
        assertEquals(key, entity.getRegistrationKey());
    }

    @Test
    public void testGetInvalidityInfo() throws Exception {

        @SuppressWarnings("deprecation")
        InstructorAttributes i =
                new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");

        assertTrue(i.isValid());

        i.googleId = "invalid@google@id";
        i.name = "";
        i.email = "invalid email";
        i.courseId = "";

        assertFalse("invalid value", i.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.GOOGLE_ID_ERROR_MESSAGE, i.googleId,
                    FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.GOOGLE_ID_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.COURSE_ID_ERROR_MESSAGE, i.courseId,
                      FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_EMPTY,
                      FieldValidator.COURSE_ID_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, i.name,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_EMPTY,
                      FieldValidator.PERSON_NAME_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, i.email,
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));

        i.googleId = null;

        assertFalse("invalid value", i.isValid());
        errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE, i.courseId,
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_EMPTY,
                    FieldValidator.COURSE_ID_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, i.name,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_EMPTY,
                      FieldValidator.PERSON_NAME_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, i.email,
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));
    }

    @Test
    public void testSanitizeForSaving() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor =
                new InstructorAttributes(googleId, courseId, name, email, roleName, displayedName, privileges);

        instructor.sanitizeForSaving();
        assertEquals(privileges, instructor.privileges);

        instructor.role = null;
        instructor.displayedName = null;
        instructor.privileges = null;
        instructor.sanitizeForSaving();
        assertEquals(privileges, instructor.privileges);
    }

    @Test
    public void testIsAllowedForPrivilege() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor =
                new InstructorAttributes(googleId, courseId, name, email, roleName, displayedName, privileges);

        assertFalse(instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        instructor.privileges = null;
        assertTrue(instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));

        String sectionId = "sectionId";
        assertTrue(instructor.isAllowedForPrivilege(
                sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        instructor.privileges = null;
        assertTrue(instructor.isAllowedForPrivilege(
                sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));

        String sessionId = "sessionId";
        assertTrue(instructor.isAllowedForPrivilege(sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        instructor.privileges = null;
        assertTrue(instructor.isAllowedForPrivilege(sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testIsEqualToAnotherInstructor() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor =
                new InstructorAttributes(googleId, courseId, name, email, roleName, displayedName, privileges);
        InstructorPrivileges privileges2 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor2 =
                new InstructorAttributes(googleId, courseId, name, email, roleName, displayedName, privileges2);

        assertTrue(instructor.isEqualToAnotherInstructor(instructor2));
        instructor2.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        assertFalse(instructor.isEqualToAnotherInstructor(instructor2));
        instructor2.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        assertTrue(instructor.isEqualToAnotherInstructor(instructor2));
        // TODO: find ways to test this method more thoroughly
    }

}
