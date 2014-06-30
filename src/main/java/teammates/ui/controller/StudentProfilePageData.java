package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentProfilePageData extends PageData {

    public String editPicture;
    
    public StudentProfilePageData(AccountAttributes account, String editPicture) {
        super(account);
        this.editPicture = editPicture;
    }

}
