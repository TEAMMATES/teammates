package teammates.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


//@formatter:off

@RunWith(Suite.class)
@Suite.SuiteClasses({
		
		
		// Unit tests
		AllComponentTestsSuite.class,
		
		BackDoorTest.class,
		
		// Js tests
		AllJsTests.class, 
		
		// Ui test
		AllUiTestsSuite.class,
		AllAccessControlUiTests.class
})

//@formatter:on
public class AllTestsSuite {

}
