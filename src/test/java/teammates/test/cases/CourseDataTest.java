package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.InvalidParametersException;

public class CourseDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(CourseData.class);
	}

	@Test
	public void testValidate() throws InvalidParametersException {
		CourseData c = new CourseData();
		
		// SUCCESS: Basic Success Case
		c.id = "valid-id-$_abc";
		c.coord = "valid-coord";
		c.name = "valid-name";
		
		assertTrue(c.isValid());
		
		// FAIL: ID null
		c.id = null;
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_FIELD_ID);
		
		// FAIL: ID too long
		String veryLongId = "";
		for (int i=0; i< Common.COURSE_ID_MAX_LENGTH + 5; i++) {
			veryLongId += "a";
		}
		c.id = veryLongId;
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_ID_TOOLONG);
		
		// FAIL : ID with invalid chars
		c.id = "my-uber-id!";
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_ID_INVALIDCHARS);
		
		// FAIL : Coord null
		c.id = "valid-id";
		c.coord = null;
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_FIELD_COORD);
		
		// FAIL : Name null
		c.coord = "valid-coord";
		c.name = null;
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_FIELD_NAME);
		
		// FAIL : Name too long
		String veryLongName = "";
		for (int i=0; i< Common.COURSE_NAME_MAX_LENGTH + 5; i++) {
			veryLongName += "e";
		}
		c.name = veryLongName;
		assertFalse(c.isValid());
		assertEquals(c.getInvalidStateInfo(), CourseData.ERROR_NAME_TOOLONG);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(CourseData.class);
	}
}
