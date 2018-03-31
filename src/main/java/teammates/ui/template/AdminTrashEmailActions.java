package teammates.ui.template;

import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminTrashEmailActions {
    private ElementTag editButton;
    private ElementTag moveOutOfTrashButton;

    public AdminTrashEmailActions(String emailId, String sessionToken) {
        this.editButton = createEditButton(emailId);
        this.moveOutOfTrashButton = createMoveOutOfTrashButton(emailId, sessionToken);
    }

    public ElementTag getEditButton() {
        return editButton;
    }

    public ElementTag getMoveOutOfTrashButton() {
        return moveOutOfTrashButton;
    }

    private ElementTag createEditButton(String emailId) {
        String content = "<span class=\"glyphicon glyphicon-edit\"></span>";
        String href = Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE
                    + "?" + Const.ParamsNames.ADMIN_EMAIL_ID + "=" + emailId;

        return new ElementTag(content, "target", "blank", "href", href);
    }

    private ElementTag createMoveOutOfTrashButton(String emailId, String sessionToken) {
        String content = "<span class=\"glyphicon glyphicon-step-backward\"></span>";
        String href = Const.ActionURIs.ADMIN_EMAIL_MOVE_OUT_TRASH;
        href = Url.addParamToUrl(href, Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        href = Url.addParamToUrl(href, Const.ParamsNames.SESSION_TOKEN, sessionToken);

        return new ElementTag(content, "target", "blank", "href", href);
    }

}
