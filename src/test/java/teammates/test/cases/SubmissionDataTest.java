package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.SubmissionData;

public class SubmissionDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(SubmissionData.class);
	}

	@Test
	public void testValidate() {
		SubmissionData s = new SubmissionData();
		
		s.course = "valid-course";
		s.evaluation = "valid-evaluation";
		s.reviewer = "valid-reviewer";
		s.reviewee = "valid-reviewee";
		s.team = "valid-team";

		// minimal properties, still valid
		assertTrue(s.getInvalidStateInfo(), s.isValid());

		s.points = 10;
		s.justification = new Text("valid-justification");
		s.p2pFeedback = new Text("valid-feedback");

		// other properties added, still valid
		assertTrue(s.getInvalidStateInfo(), s.isValid());

		// no course: invalid
		s.course = null;
		assertFalse(s.isValid());
		assertEquals(s.getInvalidStateInfo(), SubmissionData.ERROR_FIELD_COURSE);
		
		// no evaluation: invalid
		s.course = "valid-course";
		s.evaluation = null;
		assertFalse(s.isValid());
		assertEquals(s.getInvalidStateInfo(), SubmissionData.ERROR_FIELD_EVALUATION);
		
		// no reviewee : invalid
		s.evaluation = "valid-evaluation";
		s.reviewee = null;
		assertFalse(s.isValid());
		assertEquals(s.getInvalidStateInfo(), SubmissionData.ERROR_FIELD_REVIEWEE);
		
		// no reviewer : invalid
		s.reviewee = "valid-reviewee";
		s.reviewer = null;
		assertFalse(s.isValid());
		assertEquals(s.getInvalidStateInfo(), SubmissionData.ERROR_FIELD_REVIEWER);
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionData.class);
	}
}
