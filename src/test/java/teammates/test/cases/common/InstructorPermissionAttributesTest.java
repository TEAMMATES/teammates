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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testValidate() {
        String instrEmail = "valid@google.com";
        String courseId = "validCourseId";
        String role = "Co-owner";
        String accessString = "";
        HashMap privileges = new HashMap();
        HashMap courseLevelHashMap = new HashMap();
        HashMap courseLevelPrileges = new HashMap();
        courseLevelPrileges.put("canmodifycourse", new Boolean(false));
        courseLevelHashMap.put("privileges", courseLevelPrileges);
        privileges.put("course-level", courseLevelHashMap);
        String gsonString = gson.toJson(privileges, HashMap.class);
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
