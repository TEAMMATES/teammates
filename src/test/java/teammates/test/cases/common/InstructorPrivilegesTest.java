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
        
        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
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
        assertEquals(true, privileges.getSessionLevelPrivileges().isEmpty());
        
        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
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
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
