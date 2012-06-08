package teammates.testing.testcases;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import teammates.api.Common;
import teammates.datatransfer.*;

public class EvalResultDataTest {

	private static int NA = Common.UNINITIALIZED_INT;;
	@Test
	public void testSortOutgoingByStudentNameAscending() {
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
	public void testSortIncomingByStudentNameAscending() {
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
	public void testSortIncomingByFeedbackAscending() {
		EvalResultData result = new EvalResultData();

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

		assertEquals("abc", result.incoming.get(0).p2pFeedback.getValue());
		assertEquals("def", result.incoming.get(1).p2pFeedback.getValue());
		assertEquals("ghk", result.incoming.get(2).p2pFeedback.getValue());
	}

	@Test
	
	// @formatter:off
	public void testCalculatePoints() {
		
		int[][] input = 
			{{ 100, 100, 100, 100 }, 
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 }};
		
		int[][] expected = 
			{{ 100, 100, 100, 100 }, 
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 
			 { 100, 100, 100, 100 },
			 
			 { 100, 100, 100, 100 }, 
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 }};
		
		int[][] output = EvalResultData.calculatePoints(input);
		assertEquals(pointsToString(expected), pointsToString(output));
		
		int[][] input3 = 
			{{ 100, 100, 100, 100 }, 
			 { 110, 110, 110, 110 },
			 {  90,  90,  90,  90 },
			 {  10,  10,  10,  10 }};
		
		int[][] expected3 = 
			{{ 100, 100, 100, 100 }, 
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 
			 { 100, 100, 100, 100 },
			 
			 { 100, 100, 100, 100 }, 
			 { 110, 110, 110, 110 },
			 {  90,  90,  90,  90 },
			 {  10,  10,  10,  10 }};
		assertEquals(pointsToString(expected3),
				pointsToString(EvalResultData.calculatePoints(input3)));
		
		int[][] input2 = 
			{{ 100, 100, 100, 100 }, 
			 { 110, 110, 110, 110 },
			 {  90,  90,  90,  90 },
			 {  70,  80, 110, 120 }};
		
		int[][] expected2 = 
			{{ 100, 100, 100, 100 }, 
			 { 100, 100, 100, 100 },
			 { 100, 100, 100, 100 },
			 {  74,  84, 116, 126 },
			 
			 {  93, 97, 109, 100 },
			 
			 {  93,  97, 109, 100 }, 
			 { 102, 106, 120, 110 },
			 {  83,  87,  98,  90 },
			 {  88,  92, 103,  95 }};
		assertEquals(pointsToString(expected2),
				pointsToString(EvalResultData.calculatePoints(input2)));
	}
	
	@Test
	public void testNormalizeValues(){
		verifyNormalized(new int[] {}, new int[] {});
		verifyNormalized(new int[] {100}, new int[] {100});
		verifyNormalized(new int[] {100}, new int[] {50});
		verifyNormalized(new int[] {150,90,60}, new int[] {50,30,20});
		verifyNormalized(new int[] {0,0,0}, new int[] {0,0,0});
		verifyNormalized(new int[] {0,0,300},new int[] {0,0,100});
		verifyNormalized(new int[] {0,NA,200},new int[] {0,NA,100});
		verifyNormalized(new int[] {100,100,100},new int[] {110,110,110});
		verifyNormalized(new int[]{NA,NA},new int[]{NA,NA});
	}


	@Test 
	public void testExcludeSelfRatings(){
		
		int[][] input = 
			{{ 11, 12, 13, 14 }, 
			 { 21, 22, 23, 24 },
			 { 31, 32, 33, 34 },
			 { 41, 42, 43, 44 }};
		
		int[][] expected = 
			{{ NA, 12, 13, 14 }, 
			 { 21, NA, 23, 24 },
			 { 31, 32, NA, 34 },
			 { 41, 42, 43, NA }};
		assertEquals(pointsToString(expected),
				pointsToString(EvalResultData.excludeSelfRatings(input)));
	}
	
	@Test
	public void testAverageColumns(){
		int[][] input = 
			{{ 10, 20,  0, NA }, 
			 { 10, NA,  0, NA },
			 { 10, 20, NA, NA },
			 { 10, 20,  0, NA }};
		int[] expected = {10, 20, 0, NA};
		assertEquals(Arrays.toString(expected), 
				Arrays.toString(EvalResultData.averageColumns(input)));
		
	}
	
	@Test
	public void testSum(){
		assertEquals(6,EvalResultData.sum(new int[]{1,2,3}));
		assertEquals(0,EvalResultData.sum(new int[]{}));
		assertEquals(6,EvalResultData.sum(new int[]{NA, 2, 4}));
		assertEquals(0,EvalResultData.sum(new int[]{NA, 0, 0}));
		assertEquals(NA,EvalResultData.sum(new int[]{NA, NA, NA}));
	}
	
	@Test
	public void testCalculatePerceivedForStudent(){
		
		assertEquals(Arrays.toString(new int[]{}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{}, new int[]{})));
		
		assertEquals(Arrays.toString(new int[]{10}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{10}, new int[]{5})));
		
		assertEquals(Arrays.toString(new int[]{100,50,50}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{50,100,50}, new int[]{50,25,25})));
		
		assertEquals(Arrays.toString(new int[]{100,50,50}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{NA,150,50}, new int[]{50,25,25})));
		
		assertEquals(Arrays.toString(new int[]{NA,NA,NA}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{NA,NA,NA}, new int[]{NA,NA,NA})));
		
//		assertEquals(Arrays.toString(new int[]{100,50,50}),
//				Arrays.toString(EvalResultData.calculatePerceivedForStudent
//						(new int[]{NA,NA,NA}, new int[]{100,50,50})));

	}
	// @formatter:on
	
	//--------------------------------------------------------------------
	
	private void verifyNormalized(int[] expected, int[] input) {
		assertEquals(Arrays.toString(expected), 
				Arrays.toString(EvalResultData.normalizeValues(input)));
	}
	
	private String pointsToString(int[][] array) {
		String returnValue = "";
		int firstDividerLocation = (array.length - 1) / 2 - 1;
		int secondDividerLocation = firstDividerLocation + 1;
		for (int i = 0; i < array.length; i++) {
			returnValue = returnValue + Arrays.toString(array[i]) + Common.EOL;
			if ((i == firstDividerLocation) || (i == secondDividerLocation)) {
				returnValue = returnValue + "======================="
						+ Common.EOL;
			}
		}
		return returnValue;
	}
}
