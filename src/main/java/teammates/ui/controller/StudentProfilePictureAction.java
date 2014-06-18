package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

public class StudentProfilePictureAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        log.info(blobKey);
        return createImageResult(blobKey);
    }
}
