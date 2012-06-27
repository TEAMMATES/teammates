package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.BackDoorTest;

//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		// Unit tests
		AllUnitTests.class,
		// Js tests
//		AllJsUnitTests.class, //TODO: add this back
		// Api tests
		AccessControlUiTest.class,
		BackDoorTest.class,
		// Ui test
		AllUiTests.class
})

//@formatter:on
public class AllCasesTestSuite {

}
