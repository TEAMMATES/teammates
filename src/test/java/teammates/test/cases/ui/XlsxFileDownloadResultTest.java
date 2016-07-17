package teammates.test.cases.ui;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.XlsxFileDownloadResult;

public class XlsxFileDownloadResultTest extends BaseTestCase {

    @Test
    public void testNullContentXslFileDownloadResult() {
        String fileContent = null;
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals("attachment; filename=\"Normal name.xlsx\";filename*= UTF-8''Normal+name.xlsx",
                download.getContentDispositionHeader());
        assertEquals(0, download.getWorkBook().getSheetAt(0).getLastRowNum());
    }

    @Test
    public void testEmptyContentXslFileDownloadResult() {
        String fileContent = "";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals(0, download.getWorkBook().getSheetAt(0).getLastRowNum());
    }

    @Test
    public void testOneWordContentXslFileDownloadResult() {
        String fileContent = "one";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals(1, download.getWorkBook().getSheetAt(0).getLastRowNum());
        assertEquals(1, download.getWorkBook().getSheetAt(0).getRow(1).getLastCellNum());
    }

    @Test
    public void testTwoWordsContentXslFileDownloadResult() {
        String fileContent = "one,two";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals(1, download.getWorkBook().getSheetAt(0).getLastRowNum());
        assertEquals(2, download.getWorkBook().getSheetAt(0).getRow(1).getLastCellNum());

    }

    @Test
    public void testTwoRowsContentXslFileDownloadResult() {
        String fileContent = "one,two" + Const.EOL + "three, four";
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals(2, download.getWorkBook().getSheetAt(0).getLastRowNum());
        assertEquals(2, download.getWorkBook().getSheetAt(0).getRow(1).getLastCellNum());
        assertEquals(2, download.getWorkBook().getSheetAt(0).getRow(2).getLastCellNum());
    }

    @Test
    public void testWordsWithCommaContentXslFileDownloadResult() {
        String fileContent = "one,two" + Const.EOL + "three, four" + Const.EOL
                + Sanitizer.sanitizeForCsv("five, six");
        XlsxFileDownloadResult download = new XlsxFileDownloadResult("/page/instructorFeedbackResultsDownload",
                null, null, "Normal name",
                fileContent, Const.FeedbackSessionResultsDownloadTypes.XLSX);
        assertEquals(3, download.getWorkBook().getSheetAt(0).getLastRowNum());
        assertEquals(2, download.getWorkBook().getSheetAt(0).getRow(1).getLastCellNum());
        assertEquals(2, download.getWorkBook().getSheetAt(0).getRow(2).getLastCellNum());
        // Third row should only have one cell since it a single word with a
        // comma included.
        assertEquals(1, download.getWorkBook().getSheetAt(0).getRow(3).getLastCellNum());

    }
}
