package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;

public class StudentProfileForInstructorAjaxPageData extends PageData {
    
    public String shortName;
    public String email;
    public String institute;
    public String nationality;
    public String gender; // only accepts "male", "female" or "other"
    public String moreInfo;

    public StudentProfileForInstructorAjaxPageData(AccountAttributes account, StudentProfileAttributes profile) {
        super(account);
        shortName = profile.shortName;
        email = profile.email;
        institute = profile.institute;
        nationality = profile.nationality;
        gender = profile.gender;
        moreInfo = profile.moreInfo;
    }
}
