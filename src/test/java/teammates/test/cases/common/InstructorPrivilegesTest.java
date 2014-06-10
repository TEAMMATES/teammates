package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

public class InstructorPrivilegesTest extends BaseTestCase {
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testSetDefault() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        HashMap<String, Boolean> courseLevelMap;
        
        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        String invalidKey = "invalid key";
        assertEquals(null, courseLevelMap.get(invalidKey));
        assertEquals(true, privileges.getSessionLevelPrivileges().isEmpty());
        
        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // observer: view only
        privileges.setDefaultPrivilegesForObserver();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // tutor
        privileges.setDefaultPrivilegesForTutor();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // helper
        privileges.setDefaultPrivilegesForHelper();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
    }
    
    @Test
    public void testConstructor() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        InstructorPrivileges privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
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
        
        privileges.setDefaultPrivilegesForHelper();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER);
        assertEquals(privileges, privileges1);
        
        privileges1 = new InstructorPrivileges("random string");
        assertEquals(privileges, privileges1);
    }
    
    @Test
    public void testIsPrivilegeNameValid() {
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        String invalidPrivileName = "invalidPrivilegeName";
        assertFalse(InstructorPrivileges.isPrivilegeNameValid(invalidPrivileName));
        
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(invalidPrivileName));
        
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(invalidPrivileName));
    }
    
    @Test
    public void testUpdatePrivilegeInCourseLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        HashMap<String, Boolean> courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(invalidPrivilegeName, false);
        courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelPrivileges.containsKey(invalidPrivilegeName));
        assertEquals(Boolean.valueOf(false), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
    }
    
    @Test
    public void testUpdatePrivilegeInSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        HashMap<String, Boolean> sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, invalidPrivilegeName, false);
        sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, privileges.getSectionLevelPrivileges().get(sectionId).size());
        
    }
    
    @Test
    public void testUpdatePrivilegesInSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        HashMap<String, Boolean> privilegeMap = new HashMap<String, Boolean>();
        
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        
        privileges.updatePrivilegesInSectionLevel(sectionId, privilegeMap);
        HashMap<String, Boolean> sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, sectionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privilegeMap.put(invalidPrivilegeName, false);
        privileges.updatePrivilegesInSectionLevel(sectionId, privilegeMap);
        sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, sectionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
    }
    
    @Test
    public void testUpdatePrivilegeInSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sessionId = "sessionId";
        
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        HashMap<String, Boolean> sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, sessionId, invalidPrivilegeName, false);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
    }
    
    @Test
    public void testUpdatePrivilegesInSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sessionId = "sessionId";
        HashMap<String, Boolean> privilegeMap = new HashMap<String, Boolean>();
        
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        privileges.updatePrivilegesInSessionLevel(sectionId, sessionId, privilegeMap);
        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        HashMap<String, Boolean> sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privilegeMap.put(invalidPrivilegeName, false);
        privileges.updatePrivilegesInSessionLevel(sectionId, sessionId, privilegeMap);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
    }
    
    @Test
    public void testAddSectionWithDefaultPrivilegesToSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";
        
        privileges.addSectionWithDefaultPrivilegesToSectionLevel(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivilegesToSectionLevel(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivilegesToSectionLevel(sectionId2);
        assertEquals(2, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId2));
        
        //TODO: more checking for this and the method follows
    }
    
    @Test
    public void testAddSessionWithDefaultPrivilegesToSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";
        String sessionId = "sessionId";
        String sessionId2 = "sessionId2";
        
        privileges.addSessionWithDefaultPrivilegesToSessionLevel(sectionId, sessionId);
        privileges.addSessionWithDefaultPrivilegesToSessionLevel(sectionId, sessionId);
        privileges.addSessionWithDefaultPrivilegesToSessionLevel(sectionId, sessionId2);
        privileges.addSessionWithDefaultPrivilegesToSessionLevel(sectionId2, sessionId);
        assertEquals(2, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId2));
        assertEquals(2, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId2).size());
    }
    
    @Test
    public void testIsAllowedForPrivilege() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        String invalidPrivilegeName = "invalidPrivilegeName";
        assertFalse(privileges.isAllowedForPrivilege(invalidPrivilegeName));
        
        String sectionId = "sectionId";
        assertTrue(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        assertFalse(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertFalse(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        
        String sessionId = "sessionId";
        String sessionId2 = "sessionId2";
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.addSessionWithDefaultPrivilegesToSessionLevel(sectionId, sessionId2);
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        assertTrue(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
