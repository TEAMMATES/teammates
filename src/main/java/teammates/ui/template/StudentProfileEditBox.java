package teammates.ui.template;

public class StudentProfileEditBox {

    private String name;
    private String editPicture;
    private String shortName;
    private String email;
    private String institute;
    private String nationality;
    private String gender;
    private String moreInfo;
    private String googleId;
    private String pictureUrl;

    public StudentProfileEditBox(String name, String editPicture, String shortName, String email,
                                 String institute, String nationality, String gender,
                                 String moreInfo, String googleId, String pictureUrl) {
        this.name = name;
        this.editPicture = editPicture;
        this.shortName = convertToEmptyStringIfNull(shortName);
        this.email = convertToEmptyStringIfNull(email);
        this.institute = convertToEmptyStringIfNull(institute);
        this.nationality = convertToEmptyStringIfNull(nationality);
        this.gender = gender;
        this.moreInfo = convertToEmptyStringIfNull(moreInfo);
        this.googleId = googleId;
        this.pictureUrl = pictureUrl;
    }
    
    public String getName() {
        return name;
    }

    public String getEditPicture() {
        return editPicture;
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

    // TODO move this to StringHelper
    private String convertToEmptyStringIfNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
    
}
