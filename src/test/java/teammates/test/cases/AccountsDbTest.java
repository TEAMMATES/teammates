package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.AccountsDb;
import teammates.storage.datastore.Datastore;

public class AccountsDbTest extends BaseTestCase {

	private AccountsDb accountsDb = new AccountsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(AccountsDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}

	/*
	 * STUDENT TEST
	 */
	@Test
	public void testCreateStudent() {
		// SUCCESS
		StudentData s = new StudentData();
		s.name = "herp derp";
		s.course = "Winzor101";
		s.email = "ching@chang.com";
		
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// FAIL : duplicate
		try {
			accountsDb.createStudent(s);
			fail();
		} catch (EntityAlreadyExistsException e) {
			
		}
		
		// FAIL : invalid params
		s.course = "pwned 101";
		try {
			accountsDb.createStudent(s);
			fail();
		} catch (AssertionError a) {
			
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testGetStudent() {
		// Prepare
		StudentData s = new StudentData();
		s.name = "herp derp";
		s.course = "Winzor101";
		s.email = "ching@chang.com";
		
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		// Get existent
		StudentData retrieved = accountsDb.getStudent(s.course, s.email);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getStudent("Thuum", "dovahkiin@skyrim.com");
		assertNull(retrieved);
	}
	
	@Test
	public void testEditStudent() {
		// Prepare
		StudentData s = new StudentData();
		s.name = "herp derp";
		s.course = "Winzor101";
		s.email = "ching@chang.com";
		
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		// Edit existent
		accountsDb.editStudent(s.course, s.email, s.name, "dark brotherhood", s.email, "lmfao", "lorem ipsum dolor si amet", new Text("geekzor"));
		
		// Edit non-existent
		try {
			accountsDb.editStudent("I", "dont", "exist", "blah", "blah", "blah", "blah", new Text("blah"));
			fail();
		} catch (AssertionError a) {
			
		}
	}
	
	@Test
	public void testDeleteStudent() {
		// Prepare
		StudentData s = new StudentData();
		s.name = "herp derp";
		s.course = "Winzor101";
		s.email = "ching@chang.com";
		
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		// Delete
		accountsDb.deleteStudent(s.course, s.email);
		
		StudentData deleted = accountsDb.getStudent(s.course, s.email);
		assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteStudent(s.course, s.email);
	}
	
	/*
	 * COORDINATOR TEST
	 */
	@Test
	public void testCreateCoord() {
		// SUCCESS
		CoordData c = new CoordData();
		c.id = "herp.derp";
		c.name = "Herp McDerpson";
		c.email = "ching@chang.com";
		
		try {
			accountsDb.createCoord(c);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// FAIL : duplicate
		try {
			accountsDb.createCoord(c);
			fail();
		} catch (EntityAlreadyExistsException e) {
			
		}
		
		// FAIL : invalid params
		c.id = "herp mc derp";
		try {
			accountsDb.createCoord(c);
			fail();
		} catch (AssertionError a) {
			
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testGetCoord() {
		// SUCCESS
		CoordData c = new CoordData();
		c.id = "herp.derp";
		c.name = "Herp McDerpson";
		c.email = "ching@chang.com";
		
		try {
			accountsDb.createCoord(c);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Get existent
		CoordData retrieved = accountsDb.getCoord(c.id);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getCoord("the.dovahkiin");
		assertNull(retrieved);
	}
	
	@Test
	public void testEditCoord() {
		// Not implemented
	}
	
	@Test
	public void testDeleteCoord() {
		// SUCCESS
		CoordData c = new CoordData();
		c.id = "herp.derp";
		c.name = "Herp McDerpson";
		c.email = "ching@chang.com";
		
		try {
			accountsDb.createCoord(c);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Delete
		accountsDb.deleteCoord(c.id);
		
		CoordData deleted = accountsDb.getCoord(c.id);
		assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteCoord(c.id);
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(AccountsDb.class);
		helper.tearDown();
	}
}
