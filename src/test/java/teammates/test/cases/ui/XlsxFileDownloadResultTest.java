package teammates.test.cases.ui;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.XlsxFileDownloadResult;

public class XlsxFileDownloadResultTest extends BaseTestCase {

    @Test
    public void testEmptyContentXlsFileDownloadResult() {
        String fileContent = "";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(1, firstSheet.getLastRowNum());
    }

    @Test
    public void testOneWordContentXlsFileDownloadResult() {
        String fileContent = "one";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(1, firstSheet.getLastRowNum());
        assertEquals(1, firstSheet.getRow(1).getLastCellNum());
    }

    @Test
    public void testTwoWordsContentXlsFileDownloadResult() {
        String fileContent = "one,two";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(1, firstSheet.getLastRowNum());
        assertEquals(2, firstSheet.getRow(1).getLastCellNum());

    }

    @Test
    public void testTwoRowsContentXlsFileDownloadResult() {
        String fileContent = "one,two" + Const.EOL + "three, four";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(2, firstSheet.getLastRowNum());
        assertEquals(2, firstSheet.getRow(1).getLastCellNum());
        assertEquals(2, firstSheet.getRow(2).getLastCellNum());
    }

    @Test
    public void testWordsWithCommaContentXlsFileDownloadResult() {
        String fileContent = "one,two" + Const.EOL + "three, four" + Const.EOL
                + Sanitizer.sanitizeForCsv("five, six");
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(3, firstSheet.getLastRowNum());
        assertEquals(2, firstSheet.getRow(1).getLastCellNum());
        assertEquals(2, firstSheet.getRow(2).getLastCellNum());
        // Third row should only have one cell since it a single word with a
        // comma included.
        assertEquals(1, firstSheet.getRow(3).getLastCellNum());

    }

    @Test
    public void testQuestionFormattingChanges() throws IOException {
        String fileContent = "one,two" + Const.EOL + "Question 1,"
                + Sanitizer.sanitizeForCsv("Hi What do you think about Calculus, Differential Classes ?");
        XlsxFileDownloadResult download = new XlsxFileDownloadResult(
                "/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        XSSFSheet firstSheet = download.getWorkBook().getSheetAt(0);
        assertEquals(2, firstSheet.getLastRowNum());
        assertEquals(2, firstSheet.getRow(1).getLastCellNum());
        XSSFRow questionRow = firstSheet.getRow(2);
        assertEquals(2, questionRow.getLastCellNum());
        assertTrue(questionRow.getRowStyle().getFont().getBold());
        assertEquals(15, questionRow.getRowStyle().getFont().getFontHeightInPoints());
    }
}
