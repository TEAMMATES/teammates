package teammates.testing.testcases;

import java.util.Calendar;

import org.junit.Test;

import teammates.api.InvalidParametersException;
import teammates.persistent.Evaluation;

public class EvaluationTest extends BaseTestCase {

	@Test
	public void testIsReady() throws InvalidParametersException {
		
		//Create evaluation object to use as the test object
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		new Evaluation("course1", "evalution 1", "instructions", true,
				start.getTime(), end.getTime(), 0.0, 0);
		
		
		______TS("ready, after start time");
		
		//TODO: complete this

	}

}
