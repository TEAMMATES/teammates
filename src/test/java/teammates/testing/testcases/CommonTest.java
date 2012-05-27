package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.Test;

import teammates.Common;

public class CommonTest {
	@Test
	public void testGenerateStringOfLength(){
		assertEquals(5, Common.generateStringOfLength(5).length());
		assertEquals(0, Common.generateStringOfLength(0).length());
	}

}
