package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;
import teammates.common.datatransfer.UserInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DownloadFileResult extends ActionResult {

    private String destination = "";
    private UserInfo userInfo;
    private String fileContent = "";

    public DownloadFileResult(
            String destination, UserInfo userInfo,
            String fileContent) {
        super(HttpStatus.SC_OK);

        this.destination = destination;
        this.userInfo = userInfo;
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
        System.out.println("supposed writer is: " + writer);
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
