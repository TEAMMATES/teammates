package teammates.common.util;

import java.io.IOException;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class XlsxUtils {
    
    private XlsxUtils() {
        // utility class
    }

    public static XSSFCellStyle getDefaultStyle(XSSFWorkbook workBook) throws IOException {
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

    public static XSSFCellStyle getQuestionStyle(XSSFWorkbook workBook) throws IOException {
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
