package teammates.ui.template;

import teammates.common.util.Const;

public class AdminTrashEmailActions {
    private ElementTag editButton;
    private ElementTag moveOutOfTrashButton;

    public AdminTrashEmailActions(String emailId) {
        this.editButton = createEditButton(emailId);
        this.moveOutOfTrashButton = createMoveOutOfTrashButton(emailId);
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

    private ElementTag createMoveOutOfTrashButton(String emailId) {
        String content = "<span class=\"glyphicon glyphicon-step-backward\"></span>";
        String href = Const.ActionURIs.ADMIN_EMAIL_MOVE_OUT_TRASH + "?"
                          + Const.ParamsNames.ADMIN_EMAIL_ID + "=" + emailId;

        return new ElementTag(content, "target", "blank", "href", href);
    }

}
