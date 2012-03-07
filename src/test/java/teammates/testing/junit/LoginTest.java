package teammates.testing.junit;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.TeammatesServlet;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class LoginTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalUserServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCoordinatorLogin() throws IOException, ServletException {
        TeammatesServlet ts = new TeammatesServlet();
        HttpServletRequest req = null;
        req.setAttribute("operation", "coordinator_login");
        HttpServletResponse resp = null;
        resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
        ts.doPost(req, resp);
    }
}