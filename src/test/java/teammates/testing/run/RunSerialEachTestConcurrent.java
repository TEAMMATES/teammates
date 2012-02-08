package teammates.testing.run;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
//	TestCoordCourseConcurrent.class,
//	TestCoordDeleteStudentsConcurrent.class,
//	TestStudentsSubmitFeedbackConcurrent.class, 
	})
/**
 * Run the tests serially, but using BrowserPool and support 
 * concurrent browsers in individual test class  
 *
 */
public class RunSerialEachTestConcurrent {

}
