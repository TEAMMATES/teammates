package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.SubmissionAttributes;

public class SubmissionDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(SubmissionAttributes.class);
	}

	@Test
	public void testValidate() {
		SubmissionAttributes s = new SubmissionAttributes();
		
		s.course = "valid-course";
		s.evaluation = "valid-evaluation";
		s.reviewer = "valid.reviewer@gmail.com";
		s.reviewee = "valid.reviewee@gmail.com";
		s.team = "valid-team";

		// SUCCESS : minimal properties, still valid
		AssertJUnit.assertTrue(s.getInvalidStateInfo(), s.isValid());

		s.points = 10;
		s.justification = new Text("valid-justification");
		s.p2pFeedback = new Text("valid-feedback");

		// SUCCESS : other properties added, still valid
		AssertJUnit.assertTrue(s.getInvalidStateInfo(), s.isValid());

		// FAIL : no course
		s.course = null;
		AssertJUnit.assertFalse(s.isValid());
		AssertJUnit.assertEquals(s.getInvalidStateInfo(), SubmissionAttributes.ERROR_FIELD_COURSE);
		
		// FAIL : no evaluation
		s.course = "valid-course";
		s.evaluation = null;
		AssertJUnit.assertFalse(s.isValid());
		AssertJUnit.assertEquals(s.getInvalidStateInfo(), SubmissionAttributes.ERROR_FIELD_EVALUATION);
		
		// FAIL : no reviewee
		s.evaluation = "valid-evaluation";
		s.reviewee = null;
		AssertJUnit.assertFalse(s.isValid());
		AssertJUnit.assertEquals(s.getInvalidStateInfo(), SubmissionAttributes.ERROR_FIELD_REVIEWEE);
		
		// FAIL : no reviewer
		s.reviewee = "validreviewee@gmail.com";
		s.reviewer = null;
		AssertJUnit.assertFalse(s.isValid());
		AssertJUnit.assertEquals(s.getInvalidStateInfo(), SubmissionAttributes.ERROR_FIELD_REVIEWER);
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionAttributes.class);
	}
}
