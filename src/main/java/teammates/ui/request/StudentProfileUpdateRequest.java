package teammates.ui.request;

/**
 * The request of updating a student profile.
 */
public class StudentProfileUpdateRequest extends BasicRequest {

    private String shortName;
    private String email;
    private String institute;
    private String nationality;
    private String gender;
    private String moreInfo;
    private String existingNationality;

    public String getEmail() {
        return email;
    }

    public String getExistingNationality() {
        return existingNationality;
    }

    public String getGender() {
        return gender;
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

    public String getShortName() {
        return shortName;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExistingNationality(String existingNationality) {
        this.existingNationality = existingNationality;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public void validate() {
        // no specific validation needed here.
    }
}
