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
import teammates.common.datatransfer.InstructorPrivileges;
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
        InstructorPrivileges privileges = new InstructorPrivileges("Manager");
        String gsonString = gson.toJson(privileges, InstructorPrivileges.class);
        InstructorPrivileges data = gson.fromJson(gsonString, InstructorPrivileges.class);
        assertEquals(privileges, data);
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
