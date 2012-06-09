package teammates.testing.testcases;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import static teammates.datatransfer.EvalResultData.pointsToString;
import static teammates.datatransfer.EvalResultData.NA;
import static teammates.datatransfer.EvalResultData.NSU;
import static teammates.datatransfer.EvalResultData.NSB;

import com.google.appengine.api.datastore.Text;

import teammates.api.Common;
import teammates.datatransfer.*;

public class EvalResultDataTest {

	private static int NA = Common.UNINITIALIZED_INT;
	
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
		
//		int[][] input = 
//			{{ 100, 100, 100, 100 }, 
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 }};
//		
//		int[][] expected = 
//			{{ 100, 100, 100, 100 }, 
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 
//			 { 100, 100, 100, 100 },
//			 
//			 { 100, 100, 100, 100 }, 
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 }};
//		
//		int[][] output = EvalResultData.calculatePoints(input);
//		assertEquals(pointsToString(expected), pointsToString(output));
//		
//		int[][] input3 = 
//			{{ 100, 100, 100, 100 }, 
//			 { 110, 110, 110, 110 },
//			 {  90,  90,  90,  90 },
//			 {  10,  10,  10,  10 }};
//		
//		int[][] expected3 = 
//			{{ 100, 100, 100, 100 }, 
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 
//			 { 100, 100, 100, 100 },
//			 
//			 { 100, 100, 100, 100 }, 
//			 { 110, 110, 110, 110 },
//			 {  90,  90,  90,  90 },
//			 {  10,  10,  10,  10 }};
//		assertEquals(pointsToString(expected3),
//				pointsToString(EvalResultData.calculatePoints(input3)));
//		int[][] input2 = 
//			{{ 100, 100, 100, 100 }, 
//			 { 110, 110, 110, 110 },
//			 {  90,  90,  90,  90 },
//			 {  70,  80, 110, 120 }};
//		
//		int[][] expected2 = 
//			{{ 100, 100, 100, 100 }, 
//			 { 100, 100, 100, 100 },
//			 { 100, 100, 100, 100 },
//			 {  74,  84, 116, 126 },
//			 
//			 {  94, 97, 109, 100 },
//			 
//			 {  94,  97, 109, 100 }, 
//			 { 103, 107, 120, 110 },
//			 {  85,  87,  98,  90 },
//			 {  89,  92, 104,  95 }};
//		assertEquals(pointsToString(expected2),
//				pointsToString(EvalResultData.calculatePoints(input2)));
//		int[][] input4 = 
//			{{ NSB, NSB, NSB, NSB }, 
//			 { NSU, NSU, NSU, NSU },
//			 { NSU, NSU, NSU, NSU },
//			 { NSB, NSB, NSB, NSB }};
//		
//		int[][] expected4 = 
//			{{ NSB, NSB, NSB, NSB }, 
//			 { NSU, NSU, NSU, NSU },
//			 { NSU, NSU, NSU, NSU },
//			 { NSB, NSB, NSB, NSB },
//			 
//			 { NA, NA, NA, NA },
//			 
//			 { NA, NA, NA, NA }, 
//			 { NA, NA, NA, NA },
//			 { NA, NA, NA, NA },
//			 { NA, NA, NA, NA }};
//		assertEquals(pointsToString(expected4),
//				pointsToString(EvalResultData.calculatePoints(input4)));
//		
//		int[][] input5 = 
//			{{ 0, 0, 0, 0 }, 
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 }};
//		
//		int[][] expected5 = 
//			{{ 0, 0, 0, 0 }, 
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 },
//			 
//			 { 0, 0, 0, 0 },
//			 
//			 { 0, 0, 0, 0 }, 
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 },
//			 { 0, 0, 0, 0 }};
//		assertEquals(pointsToString(expected5),
//				pointsToString(EvalResultData.calculatePoints(input5)));
//		
//		int[][] input6 = 
//			{{   0,   0,   0, NSU }, 
//			 {   0,   0,   0, NSU },
//			 { NSB, NSB, NSB, NSB },
//			 {   0,   0, NSU, NSU }};
//		
//		int[][] expected6 = 
//			{{   0,   0,   0, NSU }, 
//			 {   0,   0,   0, NSU },
//			 { NSB, NSB, NSB, NSB },
//			 {   0,   0, NSU, NSU },
//			 
//			 { 0, 0, 0, NA },
//			 
//			 { 0, 0, 0, NA }, 
//			 { 0, 0, 0, NA },
//			 { 0, 0, 0, NA },
//			 { 0, 0, 0, NA }};
//		assertEquals(pointsToString(expected6),
//				pointsToString(EvalResultData.calculatePoints(input6)));
		
		int[][] input7 = 
			{{  25,  25,  75 }, 
			 { NSB, NSB, NSB },
			 { NSB, NSB, NSB }};
		
		int[][] expected7 = 
			{{  60,  60, 180 }, 
			 { NSB, NSB, NSB },
			 { NSB, NSB, NSB },
			 
			 {  NA,  50, 150 },
			 
			 {  NA,  25,  75 }, 
			 {  NA,  50, 150 },
			 {  NA,  50, 150 }};
		assertEquals(pointsToString(expected7),
				pointsToString(EvalResultData.calculatePoints(input7)));
	}
	
	@Test
	public void testNormalizeValues(){
		verifyNormalized(new double[] {}, new double[] {});
		verifyNormalized(new double[] {100}, new double[] {100});
		verifyNormalized(new double[] {100}, new double[] {50});
		verifyNormalized(new double[] {150,90,60}, new double[] {50,30,20});
		verifyNormalized(new double[] {0,0,0}, new double[] {0,0,0});
		verifyNormalized(new double[] {0,0,300},new double[] {0,0,100});
		verifyNormalized(new double[] {0,NA,200},new double[] {0,NA,100});
		verifyNormalized(new double[] {100,100,100},new double[] {110,110,110});
		verifyNormalized(new double[]{NA,NA},new double[]{NA,NA});
		verifyNormalized(new double[]{NSU,0,NSB},new double[]{NSU,0,NSB});
	}


	@Test 
	public void testExcludeSelfRatings(){
		
		double[][] input = 
			{{ 11, 12, 13, 14 }, 
			 { 21, 22, 23, 24 },
			 { 31, 32, 33, 34 },
			 { 41, 42, 43, 44 }};
		
		double[][] expected = 
			{{ NA, 12, 13, 14 }, 
			 { 21, NA, 23, 24 },
			 { 31, 32, NA, 34 },
			 { 41, 42, 43, NA }};
		assertEquals(pointsToString(expected),
				pointsToString(EvalResultData.excludeSelfRatings(input)));
	}
	
	@Test
	public void testAverageColumns(){
		double[][] input = 
			{{ 10, 20,  0, NA }, 
			 { 10, NA,  0, NA },
			 { 10, 20, NA, NA },
			 { 10, 20,  0, NA }};
		double[] expected = {10, 20, 0, NA};
		assertEquals(Arrays.toString(expected), 
				Arrays.toString(EvalResultData.averageColumns(input)));
		double[][] input2 = 
			{{ NA, NA, NA, NA }, 
			 { NA, NA, NA, NA },
			 { NA, NA, NA, NA },
			 { NA, NA, NA, NA }};
		double[] expected2 = {NA, NA, NA, NA};
		assertEquals(Arrays.toString(expected2), 
				Arrays.toString(EvalResultData.averageColumns(input2)));
		
	}
	
	@Test
	public void testSum(){
		assertEquals(6,EvalResultData.sum(new double[]{1,2,3}),0.001);
		assertEquals(0,EvalResultData.sum(new double[]{}),0.001);
		assertEquals(6,EvalResultData.sum(new double[]{NA, 2, 4}),0.001);
		assertEquals(0,EvalResultData.sum(new double[]{NA, 0, 0}),0.001);
		assertEquals(NA,EvalResultData.sum(new double[]{NA, NA, NA}),0.001);
	}
	
	@Test
	public void testCalculatePerceivedForStudent(){
		
		assertEquals(Arrays.toString(new int[]{}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{}, new double[]{})));
		
		assertEquals(Arrays.toString(new int[]{10}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{10}, new double[]{5})));
		
		assertEquals(Arrays.toString(new int[]{100,50,50}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{50,100,50}, new double[]{50,25,25})));
		
		assertEquals(Arrays.toString(new int[]{200,100,100}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{NA,150,50}, new double[]{50,25,25})));
		
		assertEquals(Arrays.toString(new int[]{NA,NA,NA}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{NA,NA,NA}, new double[]{NA,NA,NA})));
		
		assertEquals(Arrays.toString(new int[]{100,50,50}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{NA,NA,NA}, new double[]{100,50,50})));
		
		assertEquals(Arrays.toString(new int[]{100,100,400}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{50,150,NA}, new double[]{50,50,200})));
		
		assertEquals(Arrays.toString(new int[]{0,0,NA}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{0,0,NA}, new double[]{0,0,NA})));
		
		assertEquals(Arrays.toString(new int[]{NA,25,75}),
				Arrays.toString(EvalResultData.calculatePerceivedForStudent
						(new int[]{25,25,75}, new double[]{NA,50,150})));

	}
	
	@Test
	public void testIsSanitized(){
		assertEquals(true, EvalResultData.isSanitized(new int[]{}));
		assertEquals(true, EvalResultData.isSanitized(new int[]{1, 2, NA}));
		assertEquals(false, EvalResultData.isSanitized(new int[]{1, NSU, 2, NA}));
		assertEquals(false, EvalResultData.isSanitized(new int[]{NSB, 2, -1}));
	}
	
	@Test
	public void testTextractPerceivedValuesWithCorrespondingInputValues(){
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{}, 
				new int[]{}, new double[]{});
		
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{2.0}, 
				new int[]{1}, new double[]{2.0});
		
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, 3.0 }, 
				new int[]{1,2,3}, new double[]{1.0, 2.0, 3.0});
		
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, NA }, 
				new int[]{1,2,NA}, new double[]{1.0, 2.0, 3.0});
		
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, NA }, 
				new int[]{1,2,NSB}, new double[]{1.0, 2.0, 3.0});
		
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, NA }, 
				new int[]{1,2,NSU}, new double[]{1.0, 2.0, 3.0});
		
		//mix of special values
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, NA, 4.0, NA, 6.0, NA}, 
				new int[]{1,2,NSB,4,NSU,6,NA}, 
				new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0});
		
		// perceived values have NA
		verifyExtractPerceivedValuesWithCorrespondingInputValues(
				new double[]{1.0, 2.0, NA, NA, NA, 6.0, NA}, 
				new int[]{1,2,NSB,4,NSU,6,NA}, 
				new double[]{1.0, 2.0, 3.0, NA, 5.0, 6.0});
	}
	// @formatter:on

	
	//--------------------------------------------------------------------
	private void verifyExtractPerceivedValuesWithCorrespondingInputValues(
			double[] expected, int[] filterArray, double[] valueArray) {
		assertEquals(Arrays.toString(expected), 
				Arrays.toString(EvalResultData.extractPerceivedValuesWithCorrespondingInputValues(
						filterArray, valueArray)));
	}
	
	private void verifyNormalized(double[] expected, double[] input) {
		assertEquals(Arrays.toString(expected), 
				Arrays.toString(EvalResultData.normalizeValues(input)));
	}
	

}
