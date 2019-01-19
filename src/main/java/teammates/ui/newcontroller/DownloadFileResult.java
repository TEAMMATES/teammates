package teammates.ui.newcontroller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.UserInfo;

/**
 * Action result in the form of csv text.
 */
public class DownloadFileResult extends ActionResult {

    private UserInfo userInfo;
    private String destination = "";
    private String fileContent = "";

    public DownloadFileResult(
            String destination, UserInfo userInfo, String fileContent) {
        super(HttpStatus.SC_OK);

        this.userInfo = userInfo;
        this.destination = destination;
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

    public String getDestination() {
        return this.destination;
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

}
