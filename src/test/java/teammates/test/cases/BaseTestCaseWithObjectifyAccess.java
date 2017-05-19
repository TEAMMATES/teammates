package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.StudentProfile;

/**
 * Test cases that need access to the Objectify service.
 */
public abstract class BaseTestCaseWithObjectifyAccess extends BaseTestCase {
    private Closeable closeable;

    @BeforeClass
    public void setupObjectify() {
        ObjectifyService.register(Account.class);
        ObjectifyService.register(Course.class);
        ObjectifyService.register(Instructor.class);
        ObjectifyService.register(StudentProfile.class);
        closeable = ObjectifyService.begin();
    }

    @AfterClass
    public void tearDownObjectify() {
        closeable.close();
    }

}
