package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Datastore;
import teammates.TeammatesServlet;
import teammates.api.Common;
import teammates.testing.config.Config;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Tests API (TeammatesServlet) for Course - Enroll Page
 * 
 * @author Aldrian Obaja
 */
public class CoordCourseEnrollApiTest extends BaseTestCase{
	private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final static TeammatesServlet ts = new TeammatesServlet();
	
	@BeforeClass
	public static void setUp() {
		assertTrue(true);
		printTestClassHeader("CoordCourseEnrollApiTest");
		helper.setUp();
		try{
			ts.init();
			Datastore.initialize();
		}catch(IllegalStateException e){
			System.out.println("Error in initializing local datastore: (this is usually OK)");
			e.printStackTrace();
		} catch (ServletException e) {
			System.out.println("Error in initializing servlet");
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		ts.destroy();
		helper.tearDown();
		printTestClassFooter("CoordCourseEnrollApiTest");
	}

	@Test
	public void testCoordCourseEnroll(){
		// TODO CoordCourseEnroll test API
	}
}
