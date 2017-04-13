package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Page data for a page with created image URL.
 */
public class InstructorFeedbackRemindAjaxPageData extends PageData {
    public String ajaxStatus;

    public InstructorFeedbackRemindAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public String getStatusForAjax() {
        return ajaxStatus;
    }
}
