package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminEmailTrashPageData extends AdminEmailPageData {
    public List<AdminEmailAttributes> adminTrashEmailList;

    public AdminEmailTrashPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
        this.state = AdminEmailPageState.TRASH;
    }

    public String getEmptyTrashBinActionUrl() {
        String url = Const.ActionURIs.ADMIN_EMAIL_TRASH_DELETE;
        url = Url.addParamToUrl(url, Const.ParamsNames.ADMIN_EMAIL_EMPTY_TRASH_BIN, "true");
        url = addSessionTokenToUrl(url);
        return url;
    }
}
