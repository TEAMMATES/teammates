package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

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
        HashMap<String, Boolean> sectionLevelMap;
        
        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        String invalidKey = "invalid key";
        assertEquals(null, courseLevelMap.get(invalidKey));
        assertEquals(null, sectionLevelMap.get(invalidKey));
        assertEquals(true, privileges.getSessionLevelPrivileges().isEmpty());
        
        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // observer: view only
        privileges.setDefaultPrivilegesForObserver();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // tutor
        privileges.setDefaultPrivilegesForTutor();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        // helper
        privileges.setDefaultPrivilegesForHelper();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
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
    public void testUpdatePrivilegeInCourseAndSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges();      
        privileges.setDefaultPrivilegesForCoowner();
        
        privileges.updatePrivilegeInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        assertEquals(Boolean.valueOf(false), privileges.getCourseLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        
        privileges.updatePrivilegeInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        assertEquals(Boolean.valueOf(false), privileges.getCourseLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        
        privileges.updatePrivilegeInSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        assertEquals(Boolean.valueOf(false), privileges.getSectionLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        
        privileges.updatePrivilegeInSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        assertEquals(Boolean.valueOf(false), privileges.getSectionLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        
        HashMap<String, Boolean> courseLevelMap = privileges.getCourseLevelPrivileges();
        HashMap<String, Boolean> sectionLevelMap = privileges.getSectionLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(false), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        assertEquals(Boolean.valueOf(true), sectionLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
    }
    
    @Test
    public void testUpdateSectionInSectionRecord() {
        InstructorPrivileges privileges = new InstructorPrivileges();      
        privileges.setDefaultPrivilegesForCoowner();
        
        String sectionId1 = "newSectionId1";
        privileges.addSectionToSectionRecord(sectionId1, false);
        String sectionId2 = "newSectionId2";
        privileges.addSectionToSectionRecord(sectionId2, true);
        assertEquals(2, privileges.getSectionRecord().size());
        assertEquals(Boolean.valueOf(false), privileges.getSectionRecord().get(sectionId1));
        assertEquals(Boolean.valueOf(true), privileges.getSectionRecord().get(sectionId2));
        
        privileges.updateSectionInSectionRecord(sectionId1, true);
        assertEquals(2, privileges.getSectionRecord().size());
        assertEquals(Boolean.valueOf(true), privileges.getSectionRecord().get(sectionId1));
        assertEquals(Boolean.valueOf(true), privileges.getSectionRecord().get(sectionId2));
    }
    
    @Test
    public void testUpdateSessionInSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges();      
        privileges.setDefaultPrivilegesForCoowner();
        @SuppressWarnings("unchecked")
        HashMap<String, Boolean> sessionLevel = (HashMap<String, Boolean>)privileges.getPrivilegesForSessionsInSections().clone();
        
        String sessionId1 = "sessionId1";
        privileges.addSessionToSessionLevel(sessionId1);
        String sessionId2 = "sessionId2";
        sessionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(false));
        privileges.addSessionToSessionLevel(sessionId2, sessionLevel);
        assertEquals(2, privileges.getSessionLevelPrivileges().size());
        assertEquals(sessionLevel, privileges.getSessionLevelPrivileges().get(sessionId2));
        sessionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(true));
        assertEquals(sessionLevel, privileges.getSessionLevelPrivileges().get(sessionId1));
        
        privileges.updateSessionPrivilegeInSessionLevel(sessionId1, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        String sessionId3 = "sessionId3";
        privileges.updateSessionPrivilegeInSessionLevel(sessionId3, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        assertEquals(3, privileges.getSessionLevelPrivileges().size());
        sessionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(false));
        assertEquals(sessionLevel, privileges.getSessionLevelPrivileges().get(sessionId1));
        assertEquals(sessionLevel, privileges.getSessionLevelPrivileges().get(sessionId3));
        sessionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(true));
        sessionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(false));
        assertEquals(sessionLevel, privileges.getSessionLevelPrivileges().get(sessionId2));
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
