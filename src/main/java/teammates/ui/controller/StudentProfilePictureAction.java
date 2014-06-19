package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

public class StudentProfilePictureAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        if (isUnregistered) {
            throw new UnauthorizedAccessException("User is not registered");
        }
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        log.info(blobKey);
        return createImageResult(blobKey);
    }
}
