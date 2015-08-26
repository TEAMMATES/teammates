package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.StatusMessage;

public class FileDownloadResult extends ActionResult {
    
    String fileContent = "";
    String fileName ="";

    public FileDownloadResult(String destination, AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest,
            List<StatusMessage> status) {
        super(destination, account, parametersFromPreviousRequest, status);
    }
    
    public FileDownloadResult(
            String destination, AccountAttributes account,
            Map<String, String[]> parametersFromPreviousRequest, List<StatusMessage> status, 
            String fileName, String fileContent) {
        super(destination, account, parametersFromPreviousRequest, status);
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
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
        PrintWriter writer = resp.getWriter();
        writer.write("\uFEFF");
        writer.append(fileContent);
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getFileContent() {
        return this.fileContent;
    }

}
