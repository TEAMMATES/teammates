package teammates.ui.template;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;

public class InstructorStudentRecordsStudentProfile {

    private String pictureUrl;
    private String shortName;
    private String gender;
    private String email;
    private String institute;
    private String nationality;
    private String moreInfo;

    public InstructorStudentRecordsStudentProfile(StudentProfileAttributes spa, AccountAttributes account) {
        this.shortName = convertUnfilledFields(spa.shortName);
        this.gender = convertUnfilledGender(spa.gender);
        this.email = convertUnfilledFields(spa.email);
        this.institute = convertUnfilledFields(spa.institute);
        this.nationality = convertUnfilledFields(spa.nationality);
        this.moreInfo = convertUnfilledFields(spa.moreInfo);
        if (spa.pictureKey.isEmpty()) {
            this.pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        } else {
            this.pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE
                            + "?" + Const.ParamsNames.BLOB_KEY + "=" + spa.pictureKey
                            + "&" + Const.ParamsNames.USER_ID + "=" + account.googleId;
        }
    }

    private String convertUnfilledGender(String gender) {
        return gender.equals(Const.GenderTypes.OTHER) ? "<span class=\"text-muted\">"
                                                              + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</span>"
                                                      : gender;
    }

    private String convertUnfilledFields(String str) {
        return str.isEmpty() ? "<i class=\"text-muted\">" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>"
                             : str;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getShortName() {
        return shortName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getInstitute() {
        return institute;
    }

    public String getNationality() {
        return nationality;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

}
