package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

/**
 * Action result in the form of csv text.
 */
public class CsvResult extends ActionResult {

    private String fileContent;

    public CsvResult(String fileContent) {

        super(HttpStatus.SC_OK);
        this.fileContent = fileContent;
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
        writer.append(fileContent);
    }

    public String getFileContent() {
        return this.fileContent;
    }

}
