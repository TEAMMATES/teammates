package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.BackDoorTest;

//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		BackDoorTest.class,
		
		// Unit tests
		AllUnitTests.class,
		
		// Js tests
//		AllJsUnitTests.class, //TODO: add this back
		
		// Ui test
		AllUiTests.class,
		AccessControlUiTest.class
})

//@formatter:on
public class AllCasesTestSuite {

}
