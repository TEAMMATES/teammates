package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;

public class StudentProfilePageData extends PageData {

    public String name;
    public String editPicture;
    public StudentProfileAttributes profile;
    public String shortName;
    public String email;
    public String institute;
    public String nationality;
    public String gender;
    public String moreInfo;
    public String googleId;
    public String pictureUrl;
    
    public StudentProfilePageData(AccountAttributes account, String editPicture) {
        super(account);
        this.editPicture = editPicture;
        this.name = account.name;
        this.googleId = account.googleId;
        init(account.studentProfile);
    }
    
    private void init(StudentProfileAttributes profile) {
        this.profile = profile;
        this.shortName = convertToEmptyStringIfNull(profile.shortName);
        this.email = convertToEmptyStringIfNull(profile.email);
        this.institute = convertToEmptyStringIfNull(profile.institute);
        this.nationality = convertToEmptyStringIfNull(profile.nationality);
        this.gender = profile.gender;
        this.moreInfo = convertToEmptyStringIfNull(profile.moreInfo);
        if (profile.pictureKey == "") {
            this.pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        } else {
            this.pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE +
                                            "?" + Const.ParamsNames.BLOB_KEY + "="
                                            + profile.pictureKey +
                                            "&" + Const.ParamsNames.USER_ID + "=" + googleId;
        }

    }
    
    public StudentProfileAttributes getProfile() {
        return profile;
    }

    public String getShortName() {
        return shortName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getInstitute() {
        return institute;
    }
    
    public String getMoreInfo() {
        return moreInfo;
    }
    
    public String getNationality() {
        return nationality;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public String getPictureUrl() {
        return pictureUrl;
    }
    
    public String getName() {
        return name;
    }
    
    private String convertToEmptyStringIfNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

}
