package teammates.ui.webapi.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

/**
 * Action result in the form of csv text.
 */
public class CsvResult extends ActionResult {

    private String content;

    public CsvResult(String content) {

        super(HttpStatus.SC_OK);
        this.content = content;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        /*
         * We have to call setContentType() instead of setHeader() in order
         *     to make the servlet aware of the specified charset encoding
         */
        resp.setContentType("text/csv; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("\uFEFF");
        writer.append(content);
    }

    public String getContent() {
        return this.content;
    }

}
