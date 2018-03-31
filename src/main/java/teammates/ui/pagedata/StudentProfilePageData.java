package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.template.StudentProfileEditBox;
import teammates.ui.template.StudentProfileUploadPhotoModal;

public class StudentProfilePageData extends PageData {

    private StudentProfileEditBox profileEditBox;
    private StudentProfileUploadPhotoModal uploadPhotoModal;

    public StudentProfilePageData(AccountAttributes account, String sessionToken, String isEditingPhoto) {
        super(account, sessionToken);
        StudentProfileAttributes profile = account.studentProfile;
        String pictureUrl;
        if (profile.pictureKey.isEmpty()) {
            pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        } else {
            pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE
                       + "?" + Const.ParamsNames.BLOB_KEY + "=" + profile.pictureKey
                       + "&" + Const.ParamsNames.USER_ID + "=" + account.googleId;
        }
        this.profileEditBox = new StudentProfileEditBox(account.name, isEditingPhoto, profile,
                                                        account.googleId, pictureUrl);
        this.uploadPhotoModal = new StudentProfileUploadPhotoModal(account.googleId, pictureUrl, profile.pictureKey);

    }

    public StudentProfileEditBox getProfileEditBox() {
        return profileEditBox;
    }

    public StudentProfileUploadPhotoModal getUploadPhotoModal() {
        return uploadPhotoModal;
    }

}
