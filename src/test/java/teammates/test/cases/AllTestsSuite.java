package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		
		
		// Unit tests
		AllUnitTestsSuite.class,
		
		BackDoorTest.class,
		
		// Js tests
//		AllJsUnitTests.class, //TODO: add this back
		
		// Ui test
		AllUiTestsSuite.class,
		AllAccessControlUiTests.class
})

//@formatter:on
public class AllTestsSuite {

}
