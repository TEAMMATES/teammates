package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;

public class FileDownloadResult extends ActionResult {

    private String fileContent = "";
    private String fileName = "";

    public FileDownloadResult(String destination, AccountAttributes account,
            List<StatusMessage> status) {
        super(destination, account, status);
    }

    public FileDownloadResult(
            String destination, AccountAttributes account,
            List<StatusMessage> status,
            String fileName, String fileContent) {
        super(destination, account, status);
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*
         * We have to call setContentType() instead of setHeader() in order
         *     to make the servlet aware of the specified charset encoding
         */
        resp.setContentType("text/csv; charset=UTF-8");
        // Content-Disposition is a header on the HTTP response to suggest a filename
        // if the contents of the response is saved to a file.
        resp.setHeader("Content-Disposition", getContentDispositionHeader());
        PrintWriter writer = resp.getWriter();
        writer.write("\uFEFF");
        writer.append(fileContent);
    }

    /**
     * Suggests a filename for the content of the response to be saved as.
     * @return value of the HTTP Content-Disposition header
     */
    public String getContentDispositionHeader() {
        return "attachment; filename=\"" + getAsciiOnlyCsvFileName() + "\";"
               + "filename*= UTF-8''" + getUrlEscapedCsvFileName();
    }

    private String getAsciiOnlyCsvFileName() {
        return StringHelper.removeNonAscii(fileName) + ".csv";
    }

    private String getUrlEscapedCsvFileName() {
        return SanitizationHelper.sanitizeForUri(fileName) + ".csv";
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileContent() {
        return this.fileContent;
    }

}
