package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.StatusMessage;

public class CsvFileDownloadResult extends FileDownloadResult {

    public CsvFileDownloadResult(String destination, AccountAttributes account, List<StatusMessage> status,
            String fileName, String fileContent, String fileType) {
        super(destination, account, status, fileName, fileContent, fileType);
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
}
