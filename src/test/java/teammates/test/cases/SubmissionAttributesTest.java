package teammates.test.cases;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.SubmissionAttributes;

public class SubmissionAttributesTest extends BaseTestCase {

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
		s.justification = new Text("");
		s.p2pFeedback = new Text("");

		// SUCCESS : minimal properties, still valid
		AssertJUnit.assertTrue(s.getInvalidStateInfo(), s.isValid());

		s.points = 10;
		s.justification = new Text("valid-justification");
		s.p2pFeedback = new Text("valid-feedback");

		// SUCCESS : other properties added, still valid
		AssertJUnit.assertTrue(s.getInvalidStateInfo(), s.isValid());

		// FAIL : no course
		s.course = null;
		try {
			s.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
		
		// FAIL : no evaluation
		s.course = "valid-course";
		s.evaluation = null;
		try {
			s.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
		
		// FAIL : no reviewee
		s.evaluation = "valid-evaluation";
		s.reviewee = null;
		try {
			s.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
		
		// FAIL : no reviewer
		s.reviewee = "validreviewee@gmail.com";
		s.reviewer = null;
		try {
			s.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
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
