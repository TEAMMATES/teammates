package teammates.ui.template;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;

public class StudentProfile {
    private String pictureUrl;
    private String name;
    private String shortName;
    private String gender;
    private String email;
    private String institute;
    private String nationality;
    private String moreInfo;

    public StudentProfile(String fullName, StudentProfileAttributes student, String pictureUrl) {
        this.pictureUrl = pictureUrl;
        this.name = fullName;
        this.shortName = student.shortName;
        this.gender = student.gender;
        this.email = student.email;
        this.institute = student.institute;
        this.nationality = student.nationality;
        this.moreInfo = student.moreInfo;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getName() {
        return name;
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
