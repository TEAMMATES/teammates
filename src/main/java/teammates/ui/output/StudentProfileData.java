package teammates.ui.output;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes.Gender;

/**
 * The API output for the student profile.
 */
public class StudentProfileData extends ApiOutput {
    private String name;
    private String shortName;
    private String email;
    private String institute;
    private String nationality;
    private Gender gender;
    private String moreInfo;

    public StudentProfileData(String name, StudentProfileAttributes profileAttributes) {
        this.name = name;
        this.shortName = profileAttributes.shortName;
        this.email = profileAttributes.email;
        this.institute = profileAttributes.institute;
        this.nationality = profileAttributes.nationality;
        this.gender = profileAttributes.gender;
        this.moreInfo = profileAttributes.moreInfo;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Gender getGender() {
        return gender;
    }

    public String getNationality() {
        return nationality;
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    /**
     * Hides certain fields when profile is requested by another student.
     */
    public void hideInformationWhenViewedByOtherStudent() {
        this.email = null;
        this.shortName = null;
    }
}
