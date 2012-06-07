package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import teammates.datatransfer.*;

public class EvalResultDataTest {

	@Test
	public void testSortOutgoingByStudentNameAscending(){
		EvalResultData result = new EvalResultData();
		
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
		
		assertEquals("Alice", result.outgoing.get(0).revieweeName);
		assertEquals("Benny", result.outgoing.get(1).revieweeName);
		assertEquals("Charlie", result.outgoing.get(2).revieweeName);
	}
	
	@Test
	public void testSortIncomingByStudentNameAscending(){
		EvalResultData result = new EvalResultData();
		
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
		
		assertEquals("Alice", result.incoming.get(0).reviewerName);
		assertEquals("Benny", result.incoming.get(1).reviewerName);
		assertEquals("Charlie", result.incoming.get(2).reviewerName);
	}
	
	@Test
	public void testSortIncomingByFeedbackAscending(){
		EvalResultData result = new EvalResultData();
		
		SubmissionData s1 = new SubmissionData();
		s1.p2pFeedback = new Text ("ghk");
		result.incoming.add(s1);
		
		SubmissionData s2 = new SubmissionData();
		s2.p2pFeedback = new Text ("def");
		result.incoming.add(s2);
		
		SubmissionData s3 = new SubmissionData();
		s3.p2pFeedback = new Text ("abc");
		result.incoming.add(s3);
		
		result.sortIncomingByFeedbackAscending();
		
		assertEquals("abc", result.incoming.get(0).p2pFeedback.getValue());
		assertEquals("def", result.incoming.get(1).p2pFeedback.getValue());
		assertEquals("ghk", result.incoming.get(2).p2pFeedback.getValue());
	}
	
	@Test
	public void testCalculateInputNormalizer(){
		
	}
}
