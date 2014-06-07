package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

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
        assertEquals(null, privileges.getSessionLevelPrivileges());
        
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
    public void testUpdatePrivilegeInCourseAndSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges();      
        privileges.setDefaultPrivilegesForCoowner();
        
        privileges.updatePrivilegeInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        assertEquals(Boolean.valueOf(false), privileges.getCourseLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        
        privileges.updatePrivilegeInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        assertEquals(Boolean.valueOf(false), privileges.getCourseLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        
        privileges.updatePrivilegeInSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        assertEquals(Boolean.valueOf(false), privileges.getSectionLevelPrivileges().get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        
        HashMap<String, Boolean> courseLevelMap;
        HashMap<String, Boolean> sectionLevelMap;
    }
    
    @Test
    public void testUpdateSectionInSectionRecord() {
        
    }
    
    @Test
    public void testUpdateSessionInSessionLevel() {
        
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
