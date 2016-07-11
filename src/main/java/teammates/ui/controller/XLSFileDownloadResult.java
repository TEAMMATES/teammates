package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.StatusMessage;

public class XLSFileDownloadResult extends FileDownloadResult {

    public XLSFileDownloadResult(String destination, AccountAttributes account, List<StatusMessage> status,
            String fileName, String fileContent, String fileType) {
        super(destination, account, status, fileName, fileContent, fileType);
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", getContentDispositionHeader());
        PrintWriter writer = resp.getWriter();
        writer.write("\uFEFF");
        writer.append(fileContent);
    }

}
