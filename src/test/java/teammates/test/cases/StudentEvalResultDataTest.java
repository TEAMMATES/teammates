package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.StudentEvalResultData;
import teammates.common.datatransfer.SubmissionData;

public class StudentEvalResultDataTest {

	
	@Test
	public void testSortOutgoingByStudentNameAscending() {
		StudentEvalResultData result = new StudentEvalResultData();

		SubmissionData s1 = new SubmissionData();
		s1.revieweeName = "Benny";
		result.outgoing.add(s1);

		SubmissionData s2 = new SubmissionData();
		s2.revieweeName = "Alice";
		result.outgoing.add(s2);

		SubmissionData s3 = new SubmissionData();
		s3.revieweeName = "Charlie";
		result.outgoing.add(s3);

		result.sortOutgoingByStudentNameAscending();

		AssertJUnit.assertEquals("Alice", result.outgoing.get(0).revieweeName);
		AssertJUnit.assertEquals("Benny", result.outgoing.get(1).revieweeName);
		AssertJUnit.assertEquals("Charlie", result.outgoing.get(2).revieweeName);
	}

	@Test
	public void testSortIncomingByStudentNameAscending() {
		StudentEvalResultData result = new StudentEvalResultData();

		SubmissionData s1 = new SubmissionData();
		s1.reviewerName = "Benny";
		result.incoming.add(s1);

		SubmissionData s2 = new SubmissionData();
		s2.reviewerName = "Alice";
		result.incoming.add(s2);

		SubmissionData s3 = new SubmissionData();
		s3.reviewerName = "Charlie";
		result.incoming.add(s3);

		result.sortIncomingByStudentNameAscending();

		AssertJUnit.assertEquals("Alice", result.incoming.get(0).reviewerName);
		AssertJUnit.assertEquals("Benny", result.incoming.get(1).reviewerName);
		AssertJUnit.assertEquals("Charlie", result.incoming.get(2).reviewerName);
	}

	@Test
	public void testSortIncomingByFeedbackAscending() {
		StudentEvalResultData result = new StudentEvalResultData();

		SubmissionData s1 = new SubmissionData();
		s1.p2pFeedback = new Text("ghk");
		result.incoming.add(s1);

		SubmissionData s2 = new SubmissionData();
		s2.p2pFeedback = new Text("def");
		result.incoming.add(s2);

		SubmissionData s3 = new SubmissionData();
		s3.p2pFeedback = new Text("abc");
		result.incoming.add(s3);

		result.sortIncomingByFeedbackAscending();

		AssertJUnit.assertEquals("abc", result.incoming.get(0).p2pFeedback.getValue());
		AssertJUnit.assertEquals("def", result.incoming.get(1).p2pFeedback.getValue());
		AssertJUnit.assertEquals("ghk", result.incoming.get(2).p2pFeedback.getValue());
	}

	

}
