package teammates.test.cases.ui;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.CsvFileDownloadResult;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.ShowPageResult;

public class ActionResultTest extends BaseTestCase {
    
    @Test
    public void testAppendParameters() {
        ActionResult svr = new ShowPageResult("/page/instructorHome", null, null, null);
        assertEquals("/page/instructorHome", svr.getDestinationWithParams());
        svr.addResponseParam(Const.ParamsNames.USER_ID, "david");
        assertEquals("/page/instructorHome?user=david", svr.getDestinationWithParams());
    }
    
    @Test
    public void testFileDownloadResult() {
        FileDownloadResult download = new CsvFileDownloadResult("/page/instructorFeedbackResultsDownload",
                                                                null, null, "Normal name",
                                                                "abcabc", Const.FeedbackSessionResultsDownloadTypes.CSV);
        assertEquals("attachment; filename=\"Normal name.csv\";filename*= UTF-8''Normal+name.csv",
                     download.getContentDispositionHeader());
    }
    
    @Test
    public void testFileDownloadResult_fileNameWithUnicodeCharacters_hasCorrectContentDesposition() {
        FileDownloadResult download = new CsvFileDownloadResult("/page/instructorFeedbackResultsDownload",
                                                                null, null, "£ rates",
                                                                "abcabc", Const.FeedbackSessionResultsDownloadTypes.CSV);
        assertEquals("attachment; filename=\" rates.csv\";filename*= UTF-8''%C2%A3+rates.csv",
                     download.getContentDispositionHeader());
    }

}
