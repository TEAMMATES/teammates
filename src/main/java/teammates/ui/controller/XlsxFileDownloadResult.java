package teammates.ui.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.XlsxUtils;

public class XlsxFileDownloadResult extends FileDownloadResult {
    public static final String CSV_LINE_DELIMITTER_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String QUESTION_LINE_REGEX = "Question [0-9]{1,}.{0,}";
    private XSSFWorkbook workBook;

    public XlsxFileDownloadResult(String destination, AccountAttributes account, List<StatusMessage> status,
            String fileName, String fileContent, String fileType) {
        super(destination, account, status, fileName, fileContent, fileType);
        try {
            createWorkBook();
        } catch (IOException e) {
            log.severe("Unexpected error while creating the xlsx file" + e.getMessage());
        }
    }

    public XSSFWorkbook getWorkBook() {
        return workBook;
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", getContentDispositionHeader());
        ServletOutputStream outStream = resp.getOutputStream();
        workBook.write(outStream);
        outStream.flush();
        outStream.close();
    }

    public XSSFWorkbook createWorkBook() throws IOException {
        workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheet1");
        int rowNumber = 0;
        String[] lines = fileContent.split(Const.EOL);
        for (String line : lines) {
            // Split by comma only if comma has a zero or even number of quotes
            // infront.
            String[] str = line.split(CSV_LINE_DELIMITTER_REGEX, -1);
            rowNumber++;
            XSSFRow currentRow = sheet.createRow(rowNumber);

            // Questions are marked bold and with bigger font.
            if (line.matches(QUESTION_LINE_REGEX)) {
                currentRow.setRowStyle(XlsxUtils.getQuestionStyle(workBook));
            } else {
                currentRow.setRowStyle(XlsxUtils.getDefaultStyle(workBook));
            }

            XSSFCell currentCell;
            for (int i = 0; i < str.length; i++) {
                currentCell = currentRow.createCell(i);
                currentCell.setCellStyle(currentRow.getRowStyle());
                currentCell.setCellValue(str[i]);
            }
        }
        return workBook;
    }
}
