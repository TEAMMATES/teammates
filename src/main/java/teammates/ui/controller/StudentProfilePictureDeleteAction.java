package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class StudentProfilePictureDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        gateKeeper.verifyLoggedInUserPrivileges();
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE;
        }
        logic.deleteStudentProfilePicture(account.googleId);
        statusToUser.add(new StatusMessage(Const.StatusMessages.PROFILE_PICTURE_DELETED, StatusMessageColor.SUCCESS));

        return createRedirectResult(nextUrl);
    }
}
