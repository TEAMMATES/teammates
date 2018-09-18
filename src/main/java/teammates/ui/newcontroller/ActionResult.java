package teammates.ui.newcontroller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents the result of executing an {@link Action}.
 */
public abstract class ActionResult {

    /**
     * Packages and forwards the action result to the HTTP response.
     */
    public abstract void send(HttpServletResponse resp) throws IOException;

}
