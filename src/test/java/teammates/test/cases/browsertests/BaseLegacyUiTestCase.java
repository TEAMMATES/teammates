package teammates.test.cases.browsertests;

import java.time.LocalDateTime;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.TimeHelper;
import teammates.e2e.cases.e2e.BaseE2ETestCase;

/**
 * Base class for all legacy UI tests.
 */
@Deprecated
public abstract class BaseLegacyUiTestCase extends BaseE2ETestCase {

    protected <T extends teammates.test.pageobjects.AppPage> T loginAdminToPageOld(AppUrl url, Class<T> typeOfPage) {
        return teammates.test.pageobjects.AppPage.getNewPageInstance(browser, typeOfPage);
    }

    protected LocalDateTime getStartTimeLocal(FeedbackSessionAttributes fs) {
        return TimeHelper.convertInstantToLocalDateTime(fs.getStartTime(), fs.getTimeZone());
    }

    protected LocalDateTime getEndTimeLocal(FeedbackSessionAttributes fs) {
        return TimeHelper.convertInstantToLocalDateTime(fs.getEndTime(), fs.getTimeZone());
    }

    protected LocalDateTime getSessionVisibleFromTimeLocal(FeedbackSessionAttributes fs) {
        return TimeHelper.convertInstantToLocalDateTime(fs.getSessionVisibleFromTime(), fs.getTimeZone());
    }

    protected LocalDateTime getResultsVisibleFromTimeLocal(FeedbackSessionAttributes fs) {
        return TimeHelper.convertInstantToLocalDateTime(fs.getResultsVisibleFromTime(), fs.getTimeZone());
    }

}
