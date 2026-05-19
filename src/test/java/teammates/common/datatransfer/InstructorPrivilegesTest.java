package teammates.common.datatransfer;

import org.junit.jupiter.api.Assertions;
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
        InstructorPermissionSet courseLevelMap;

        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidKey = "invalid key";
        Assertions.assertFalse(courseLevelMap.get(invalidKey));
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // observer: view only
        privileges.setDefaultPrivilegesForObserver();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // tutor
        privileges.setDefaultPrivilegesForTutor();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        // helper
        privileges.setDefaultPrivilegesForCustom();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(courseLevelMap.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testConstructor() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        InstructorPrivileges privileges1 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        privileges.setDefaultPrivilegesForCoowner();
        Assertions.assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForManager();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        Assertions.assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForObserver();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        Assertions.assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForTutor();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        Assertions.assertEquals(privileges, privileges1);

        privileges.setDefaultPrivilegesForCustom();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        Assertions.assertEquals(privileges, privileges1);

        privileges1 = new InstructorPrivileges("random string");
        Assertions.assertEquals(privileges, privileges1);
    }

    @Test
    public void testIsPrivilegeNameValid() {
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValid(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidPrivileName = "invalidPrivilegeName";
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValid(invalidPrivileName));

        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(invalidPrivileName));

        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        Assertions.assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(invalidPrivileName));
    }

    @Test
    public void testUpdatePrivilegeInCourseLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        InstructorPermissionSet courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(invalidPrivilegeName, false);
        courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        Assertions.assertFalse(courseLevelPrivileges.get(invalidPrivilegeName));
        Assertions.assertFalse(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(courseLevelPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testUpdatePrivilegeInSectionLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";

        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        Assertions.assertEquals(1, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));

        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        InstructorPermissionSet sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        Assertions.assertEquals(1, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        Assertions.assertFalse(sectionPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(sectionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(sectionPrivileges.get(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, invalidPrivilegeName, false);
        Assertions.assertEquals(1, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));

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
        Assertions.assertEquals(1, privileges.getSessionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        Assertions.assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        InstructorPermissionSet sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        Assertions.assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));

        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, sessionId, invalidPrivilegeName, false);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        Assertions.assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(sessionPrivileges.get(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
    }

    @Test
    public void testAddSectionWithDefaultPrivilegesToSectionLevel() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";

        privileges.addSectionWithDefaultPrivileges(sectionId);
        Assertions.assertEquals(1, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId);
        Assertions.assertEquals(1, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId2);
        Assertions.assertEquals(2, privileges.getSectionLevelPrivileges().size());
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        Assertions.assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId2));

        //TODO: more checking for this and the method follows
    }

    @Test
    public void testIsAllowedForPrivilege() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        Assertions.assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE));

        String sectionId = "sectionId";
        Assertions.assertTrue(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        privileges.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        Assertions.assertFalse(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(privileges.isAllowedForPrivilege(
                sectionId, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));

        String sessionId = "sessionId";
        Assertions.assertFalse(privileges.isAllowedForPrivilege(
                sectionId, sessionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
    }

    @Test
    public void testValidatePrivileges() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.validatePrivileges();

        Assertions.assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));

        // restore courseLevel to pre-validate
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        String sectionName = "section";
        privileges.addSectionWithDefaultPrivileges(sectionName);
        privileges.validatePrivileges();
        Assertions.assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.validatePrivileges();
        Assertions.assertTrue(privileges.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(privileges.isAllowedForPrivilege(
                sectionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
    }

    @Test
    public void testHasDefaultPrivileges() {

        InstructorPrivileges coownerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Assertions.assertTrue(coownerPrivileges.hasCoownerPrivileges());
        Assertions.assertFalse(coownerPrivileges.hasManagerPrivileges());
        Assertions.assertFalse(coownerPrivileges.hasObserverPrivileges());
        Assertions.assertFalse(coownerPrivileges.hasTutorPrivileges());

        InstructorPrivileges managerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        Assertions.assertTrue(managerPrivileges.hasManagerPrivileges());
        Assertions.assertFalse(managerPrivileges.hasCoownerPrivileges());
        Assertions.assertFalse(managerPrivileges.hasObserverPrivileges());
        Assertions.assertFalse(managerPrivileges.hasTutorPrivileges());

        InstructorPrivileges observerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        Assertions.assertTrue(observerPrivileges.hasObserverPrivileges());
        Assertions.assertFalse(observerPrivileges.hasCoownerPrivileges());
        Assertions.assertFalse(observerPrivileges.hasManagerPrivileges());
        Assertions.assertFalse(observerPrivileges.hasTutorPrivileges());

        InstructorPrivileges tutorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        Assertions.assertTrue(tutorPrivileges.hasTutorPrivileges());
        Assertions.assertFalse(tutorPrivileges.hasCoownerPrivileges());
        Assertions.assertFalse(tutorPrivileges.hasManagerPrivileges());
        Assertions.assertFalse(tutorPrivileges.hasObserverPrivileges());

        InstructorPrivileges nonDefaultPrivileges = new InstructorPrivileges();
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        nonDefaultPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, false);

        Assertions.assertFalse(nonDefaultPrivileges.hasCoownerPrivileges());
        Assertions.assertFalse(nonDefaultPrivileges.hasManagerPrivileges());
        Assertions.assertFalse(nonDefaultPrivileges.hasObserverPrivileges());
        Assertions.assertFalse(nonDefaultPrivileges.hasTutorPrivileges());
    }

}
