package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import teammates.common.Common;
import teammates.common.datatransfer.CourseData;

public class CourseDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testValidate() {
		CourseData c = new CourseData();
		
		// SUCCESS: Basic Success Case
		c.id = "valid-id-$_abc";
		c.name = "valid-name";
		
		AssertJUnit.assertTrue(c.isValid());
		
		// FAIL: ID null
		c.id = null;
		AssertJUnit.assertFalse(c.isValid());
		AssertJUnit.assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_FIELD_ID);
		
		// SUCCESS: ID at max length
		String veryLongId = Common.generateStringOfLength(Common.COURSE_ID_MAX_LENGTH);
		c.id = veryLongId;
		AssertJUnit.assertTrue(c.isValid());
		
		// FAIL: ID too long
		c.id += "a";
		AssertJUnit.assertFalse(c.isValid());
		AssertJUnit.assertTrue(c.getInvalidStateInfo().contains(CourseData.ERROR_ID_TOOLONG));
		
		// FAIL : ID with invalid chars
		c.id = "my-uber-id!";
		AssertJUnit.assertFalse(c.isValid());
		AssertJUnit.assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_ID_INVALIDCHARS);
		
		// FAIL : Name null
		c.id = "valid-id";
		c.name = null;
		AssertJUnit.assertFalse(c.isValid());
		AssertJUnit.assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_FIELD_NAME);
	
		
		// SUCCESS: Name at max length
		String veryLongName = Common.generateStringOfLength(CourseData.COURSE_NAME_MAX_LENGTH);
		c.name = veryLongName;
		AssertJUnit.assertTrue(c.isValid());
		
		// FAIL : Name too long
		c.name += "e";
		AssertJUnit.assertFalse(c.isValid());
		AssertJUnit.assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_NAME_TOOLONG);
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}

}
