package teammates.ui.servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

/**
 * Servlet that handles server health check.
 *
 * <p>Note that "health" here is only defined as the server being reachable. It does not
 * indicate whether other dependent components such as DB connection and Google Cloud libraries
 * are working as expected.
 */
public class HealthCheckServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.SC_OK);
        resp.getWriter().write("OK");
    }

}
