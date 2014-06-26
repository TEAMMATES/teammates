package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentProfilePictureUploadAjaxData extends PageData {
    
    String pictureKey;
    
    public StudentProfilePictureUploadAjaxData(AccountAttributes account, String pictureKey) {
        super(account);
        this.pictureKey = pictureKey;
    }
}
