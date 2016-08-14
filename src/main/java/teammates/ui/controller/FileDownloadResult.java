package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;

public abstract class FileDownloadResult extends ActionResult {
    
    String fileContent = "";
    String fileName = "";
    String fileType = "";

    public FileDownloadResult(String destination, AccountAttributes account,
            List<StatusMessage> status) {
        super(destination, account, status);
    }
    
    public FileDownloadResult(
            String destination, AccountAttributes account,
            List<StatusMessage> status,
            String fileName, String fileContent, String fileType) {
        super(destination, account, status);
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.fileType = fileType;
    }
    
    /**
     * Suggests a filename for the content of the response to be saved as.
     * @return value of the HTTP Content-Disposition header
     */
    public String getContentDispositionHeader() {
        return "attachment; filename=\"" + getAsciiOnlyFileName() + "\";"
               + "filename*= UTF-8''" + getUrlEscapedFileName();
    }
    
    private String getAsciiOnlyFileName() {
        return Sanitizer.removeNonAscii(fileName) + "." + fileType;
    }
    
    private String getUrlEscapedFileName() {
        return Sanitizer.sanitizeForUri(fileName) + "." + fileType;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getFileContent() {
        return this.fileContent;
    }
    
    public String getFileType() {
        return this.fileType;
    }

}
