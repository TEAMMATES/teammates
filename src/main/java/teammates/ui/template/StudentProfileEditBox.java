package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.PageData;

public class StudentProfileEditBox {

    private String name;
    private String editingPhoto;
    private String shortName;
    private String email;
    private String institute;
    private String nationality;
    /** option to select the nationality. */
    private List<ElementTag> nationalitySelectField;
    private String legacyNationalityInstructions;
    private String gender;
    private String moreInfo;
    private String googleId;
    private String pictureUrl;

    public StudentProfileEditBox(String name, String isEditingPhoto, StudentProfileAttributes profile,
                                 String googleId, String pictureUrl) {
        this.name = name;
        this.editingPhoto = isEditingPhoto;
        this.shortName = StringHelper.convertToEmptyStringIfNull(profile.shortName);
        this.email = StringHelper.convertToEmptyStringIfNull(profile.email);
        this.institute = StringHelper.convertToEmptyStringIfNull(profile.institute);
        this.nationality = StringHelper.convertToEmptyStringIfNull(profile.nationality);
        this.nationalitySelectField = PageData.getNationalitiesAsElementTags(profile.nationality);
        this.legacyNationalityInstructions = PageData.getLegacyNationalityInstructions(profile.nationality);
        this.gender = profile.gender;
        this.moreInfo = StringHelper.convertToEmptyStringIfNull(profile.moreInfo);
        this.googleId = googleId;
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getEditingPhoto() {
        return editingPhoto;
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

    public String getNationality() {
        return nationality;
    }

    public List<ElementTag> getNationalitySelectField() {
        return nationalitySelectField;
    }

    public String getGender() {
        return gender;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getLegacyNationalityInstructions() {
        return legacyNationalityInstructions;
    }

}
