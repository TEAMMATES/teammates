package teammates.common.datatransfer;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link InstructorPrivileges}.
 */
public class InstructorPrivilegesTest extends BaseTestCase {

    @Test
    public void testSetDefault() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        Map<String, Boolean> courseLevelMap;

        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidKey = "invalid key";
        assertNull(courseLevelMap.get(invalidKey));
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // observer: view only
        privileges.setDefaultPrivilegesForObserver();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // tutor
        privileges.setDefaultPrivilegesForTutor();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // helper
        privileges.setDefaultPrivilegesForCustom();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testConstructor() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        InstructorPrivileges privileges1 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        privileges.setDefaultPrivilegesForCoowner();
        assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForManager();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForObserver();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForTutor();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForCustom();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        assertEquals(privileges, privileges1);

        privileges1 = new InstructorPrivileges("random string");
        assertEquals(privileges, privileges1);
    }

    @Test
    public void testIsPrivilegeNameValid() {
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidPrivileName = "invalidPrivilegeName";
        assertFalse(InstructorPrivileges.isPrivilegeNameValid(invalidPrivileName));

        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(invalidPrivileName));

        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(invalidPrivileName));
    }

    @Test
    public void testUpdatePrivilegeInCourseLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        Map<String, Boolean> courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(invalidPrivilegeName, false);
        courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelPrivileges.containsKey(invalidPrivilegeName));
        assertFalse(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testUpdatePrivilegeInSectionLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";

        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));

        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        Map<String, Boolean> sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertNull(sectionPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(sectionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, invalidPrivilegeName, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(4, privileges.getSectionLevelPrivileges().get(sectionId).size());

    }

    @Test
    public void testUpdatePrivilegeInSessionLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sessionId = "sessionId";

        privileges.updatePrivilege(sectionId, sessionId,
                                   Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionId, sessionId,
                                   Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, false);
        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        Map<String, Boolean> sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, sessionId, invalidPrivilegeName, false);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
    }

    @Test
    public void testAddSectionWithDefaultPrivilegesToSectionLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";

        privileges.addSectionWithDefaultPrivileges(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId2);
        assertEquals(2, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId2));

        //TODO: more checking for this and the method follows
    }

    @Test
    public void testIsAllowedForPrivilege() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE));

        String sectionId = "sectionId";
        assertTrue(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        assertFalse(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));

        String sessionId = "sessionId";
        assertFalse(privileges.isAllowedForPrivilege(
                sectionId, sessionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
    }

    @Test
    public void testValidatePrivileges() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.validatePrivileges();

        assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));

        // restore courseLevel to pre-validate
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        String sectionName = "section";
        privileges.addSectionWithDefaultPrivileges(sectionName);
        privileges.validatePrivileges();
        assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.validatePrivileges();
        assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
    }

    @Test
    public void testHasDefaultPrivileges() {

        InstructorPrivileges coownerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        assertTrue(coownerPrivileges.hasCoownerPrivileges());
        assertFalse(coownerPrivileges.hasManagerPrivileges());
        assertFalse(coownerPrivileges.hasObserverPrivileges());
        assertFalse(coownerPrivileges.hasTutorPrivileges());

        InstructorPrivileges managerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        assertTrue(managerPrivileges.hasManagerPrivileges());
        assertFalse(managerPrivileges.hasCoownerPrivileges());
        assertFalse(managerPrivileges.hasObserverPrivileges());
        assertFalse(managerPrivileges.hasTutorPrivileges());

        InstructorPrivileges observerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        assertTrue(observerPrivileges.hasObserverPrivileges());
        assertFalse(observerPrivileges.hasCoownerPrivileges());
        assertFalse(observerPrivileges.hasManagerPrivileges());
        assertFalse(observerPrivileges.hasTutorPrivileges());

        InstructorPrivileges tutorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        assertTrue(tutorPrivileges.hasTutorPrivileges());
        assertFalse(tutorPrivileges.hasCoownerPrivileges());
        assertFalse(tutorPrivileges.hasManagerPrivileges());
        assertFalse(tutorPrivileges.hasObserverPrivileges());

        InstructorPrivileges nonDefaultPrivileges = new InstructorPrivileges();
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, false);

        assertFalse(nonDefaultPrivileges.hasCoownerPrivileges());
        assertFalse(nonDefaultPrivileges.hasManagerPrivileges());
        assertFalse(nonDefaultPrivileges.hasObserverPrivileges());
        assertFalse(nonDefaultPrivileges.hasTutorPrivileges());
    }

}
