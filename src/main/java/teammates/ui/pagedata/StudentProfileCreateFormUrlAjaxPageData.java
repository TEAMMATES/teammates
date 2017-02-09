package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class StudentProfileCreateFormUrlAjaxPageData extends PageData {

    public String formUrl;
    boolean isError;

    public StudentProfileCreateFormUrlAjaxPageData(AccountAttributes account, String url, boolean hasError) {
        super(account);
        formUrl = url;
        isError = hasError;
    }

}
