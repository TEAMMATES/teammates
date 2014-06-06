package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.HashMap;

import teammates.common.util.FieldValidator;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class InstructorPermissionAttributesTest extends BaseTestCase {
    private static Gson gson = Utils.getTeammatesGson();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        String instrEmail = "valid@google.com";
        String courseId = "validCourseId";
        String role = "Co-owner";
        String accessString = "";
        HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges = constructBasicPrivilegesHashMap();
        String gsonString = gson.toJson(privileges, HashMap.class);
    }
    
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> constructBasicPrivilegesHashMap() {
        HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges = new HashMap<String, HashMap<String, HashMap<String, Boolean>>>();
        HashMap<String, HashMap<String, Boolean>> courseLevelHashMap = new HashMap<String, HashMap<String, Boolean>>();
        HashMap<String, Boolean> courseLevelPrileges = new HashMap<String, Boolean>();
        HashMap<String, HashMap<String, Boolean>> sectionLevelHashMap = new HashMap<String, HashMap<String, Boolean>>();
        HashMap<String, Boolean> sectionLevelPrileges = new HashMap<String, Boolean>();
        HashMap<String, HashMap<String, Boolean>> sessionLevelHashMap = new HashMap<String, HashMap<String, Boolean>>();
        HashMap<String, Boolean> session1LevelPrileges = new HashMap<String, Boolean>();
        
        courseLevelPrileges.put("canmodifycourse", new Boolean(false));
        courseLevelPrileges.put("canmodifyinstructor", new Boolean(false));
        courseLevelPrileges.put("canmodifysession", new Boolean(false));
        courseLevelPrileges.put("canmodifystudent", new Boolean(false));
        courseLevelHashMap.put("privileges", courseLevelPrileges);
        privileges.put("course-level", courseLevelHashMap);
        
        sectionLevelPrileges.put("canmodifycourse", new Boolean(false));
        sectionLevelHashMap.put("privileges", courseLevelPrileges);
        privileges.put("section-level", sectionLevelHashMap);
        
        session1LevelPrileges.put("canmodifycourse", new Boolean(false));
        sectionLevelHashMap.put("privileges", courseLevelPrileges);
        privileges.put("section-level", sectionLevelHashMap);
        
        return privileges;
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
