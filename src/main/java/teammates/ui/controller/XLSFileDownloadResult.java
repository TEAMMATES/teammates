package teammates.ui.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;

public class XLSFileDownloadResult extends FileDownloadResult {
    private XSSFWorkbook workBook;

    public XLSFileDownloadResult(String destination, AccountAttributes account, List<StatusMessage> status,
            String fileName, String fileContent, String fileType) {
        super(destination, account, status, fileName, fileContent, fileType);
        workBook = getWorkBook();
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

    private XSSFWorkbook getWorkBook() {
        workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheet1");
        int rowNumber = 0;
        String[] lines = fileContent.split(Const.EOL);
        for (String line : lines) {
            String[] str = line.split(",");
            rowNumber++;
            XSSFRow currentRow = sheet.createRow(rowNumber);

            // Questions are marked bold and with bigger font.
            if (line.matches("Question [0-9]{1,}.{0,}")) {
                currentRow.setRowStyle(getQuestionStyle());
            } else {
                currentRow.setRowStyle(getDefaultStyle());
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

    private XSSFCellStyle getDefaultStyle() {
        XSSFCellStyle defaultStyle = workBook.createCellStyle();
        XSSFFont defaultFont = workBook.createFont();
        defaultFont.setFontHeightInPoints((short) 10);
        defaultFont.setFontName("Arial");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);
        defaultStyle.setFont(defaultFont);
        return defaultStyle;
    }

    private XSSFCellStyle getQuestionStyle() {
        XSSFCellStyle questionStyle = workBook.createCellStyle();
        XSSFFont questionFont = workBook.createFont();
        questionFont.setFontHeightInPoints((short) 15);
        questionFont.setFontName("Arial");
        questionFont.setColor(IndexedColors.BLACK.getIndex());
        questionFont.setBold(true);
        questionFont.setItalic(false);
        questionStyle.setFont(questionFont);
        return questionStyle;
    }
}
