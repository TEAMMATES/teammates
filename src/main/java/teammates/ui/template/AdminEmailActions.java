package teammates.ui.template;

import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminEmailActions {
    private ElementTag editButton;
    private ElementTag deleteButton;

    public AdminEmailActions(String emailId, String currentPage, String sessionToken) {
        this.editButton = createEditButton(emailId);
        this.deleteButton = createDeleteButton(emailId, currentPage, sessionToken);
    }

    public ElementTag getEditButton() {
        return editButton;
    }

    public ElementTag getDeleteButton() {
        return deleteButton;
    }

    private ElementTag createEditButton(String emailId) {
        String content = "<span class=\"glyphicon glyphicon-edit\"></span>";
        String href = Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE
                + "?" + Const.ParamsNames.ADMIN_EMAIL_ID + "=" + emailId;

        return new ElementTag(content, "target", "blank", "href", href);
    }

    private ElementTag createDeleteButton(String emailId, String currentPage, String sessionToken) {
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>";
        String href = Const.ActionURIs.ADMIN_EMAIL_MOVE_TO_TRASH;
        href = Url.addParamToUrl(href, Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        href = Url.addParamToUrl(href, Const.ParamsNames.ADMIN_EMAIL_TRASH_ACTION_REDIRECT, currentPage);
        href = Url.addParamToUrl(href, Const.ParamsNames.SESSION_TOKEN, sessionToken);

        return new ElementTag(content, "target", "blank", "href", href);
    }
}
