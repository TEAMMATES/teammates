package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.logic.InstructorsLogic;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class InstructorsLogicTest extends BaseTestCase{
	
	//TODO: add missing test cases. Some of the test content can be transferred from LogicTest.
	
	protected static InstructorsLogic instructorsLogic = InstructorsLogic.inst();
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsLogic.class);
		Datastore.initialize();
	}
	
	@BeforeMethod
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	@Test
	public void testParseInstructorLines() throws Exception {
		
		Method method = InstructorsLogic.class.getDeclaredMethod("parseInstructorLines",
				new Class[] { String.class, String.class });
		method.setAccessible(true);
		
		______TS("typical case");
		
		Object[] params = new Object[] { "private.course",
				"test1.googleId \t test1.name \t test1.email" + Common.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result1 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result1.size(), 2);
		assertEquals(result1.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result1.get(1).email, "test2.email");
		
		______TS("blank space in first line");
		
		params = new Object[] { "private.course",
				Common.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Common.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result2 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result2.size(), 2);
		assertEquals(result2.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result2.get(1).email, "test2.email");
		
		______TS("blank space in between lines");
		
		params = new Object[] { "private.course",
				Common.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Common.EOL
			+	Common.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result3 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result3.size(), 2);
		assertEquals(result3.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result3.get(1).email, "test2.email");
		
		______TS("trailing blank lines");
		
		params = new Object[] { "private.course",
				Common.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Common.EOL
			+	Common.EOL
			+	"test2.googleId | test2.name | test2.email"
			+	Common.EOL + Common.EOL
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result4 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result4.size(), 2);
		assertEquals(result4.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result4.get(1).email, "test2.email");
		
		______TS("Instructor Lines information incorrect");
		
		// Too many
		try {
			params = new Object[] { "private.course",
				"test2.googleId | test2.name | test2.email | Something extra"
			};
			method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorAttributes.ERROR_INFORMATION_INCORRECT));
		}
		
		// Too few
		try {
			params = new Object[] { "private.course",
					"test2.googleId | "
				};
				method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorAttributes.ERROR_INFORMATION_INCORRECT));
		}
		
		______TS("lines is empty");
		
		try {
			params = new Object[] { "private.course",
					""
				};
				method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorsLogic.ERROR_NO_INSTRUCTOR_LINES));
		}
	}
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationOpeningRemindersServlet.class);
	}

	@AfterMethod
	public void caseTearDown() {
		helper.tearDown();
	}

}
