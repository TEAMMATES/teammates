package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		
		
		// Unit tests
		AllUnitTests.class,
		
		BackDoorTest.class,
		
		// Js tests
//		AllJsUnitTests.class, //TODO: add this back
		
		// Ui test
		AllUiTests.class,
		AccessControlUiTest.class
})

//@formatter:on
public class AllCasesTestSuite {

}
