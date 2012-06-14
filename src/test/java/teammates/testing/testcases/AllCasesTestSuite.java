package teammates.testing.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import teammates.testing.junit.TMAPITest;

//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		// Unit tests
		AllUnitTests.class,
		// Js tests
		AllJsUnitTests.class,
		// Api tests
		TMAPITest.class,
		// Ui test
		AllUiTests.class
})

//@formatter:on
public class AllCasesTestSuite {

}
