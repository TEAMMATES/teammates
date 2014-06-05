package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents profile details for 
 * student entities associated with
 * an account entity 
 *
 */
@PersistenceCapable
public class StudentProfile {
    @PrimaryKey
    @Persistent
    private String googleId;

    @Persistent
    private String shortName;

    @Persistent
    private String email;

    @Persistent
    private String institute;
    
    @Persistent
    private String country;
    
    @Persistent
    /* only accepts "male", "female" or "other" */
    private String gender;
    
    @Persistent
    /* must be html sanitized before saving */
    private String moreInfo;
    
    /**
     * Instantiates a new account. 
     * 
     * @param googleId
     *            the Google ID of the user. 
     * @param name
     *            The shortened name of the user.
     * @param email
     *            The long-term (personal) email of the user.
     * @param institute
     *            The university/school/institute the student is from
     *            (useful for exchange students)
     * @param country
     *            The country the student is from
     *            (useful for exchange/foreign students)
     * @param gender
     *            The student's gender. Allows "other"
     * @param moreInfo
     *            Miscellaneous information, including external profile
     */
    public StudentProfile(String googleId, String shortName, String email,
            String institute, String country, String gender, String moreInfo) {
        this.setGoogleId(googleId);
        this.setShortName(shortName);
        this.setEmail(email);
        this.setInstitute(institute);
        this.setCountry(country);
        this.setGender(gender);
        this.setMoreInfo(moreInfo);
    }
    
    public String getGoogleId() {
        return this.googleId;
    }
    
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    public void setShortName(String shortName) {
        this.shortName =shortName;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getInstitute() {
        return this.institute;
    }
    
    public void setInstitute(String institute) {
        this.institute = institute;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getMoreInfo() {
        return this.moreInfo;
    }
    
    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }
}
    