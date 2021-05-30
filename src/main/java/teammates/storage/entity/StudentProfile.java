package teammates.storage.entity;

import java.time.Instant;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

/**
 * Represents profile details for student entities associated with an
 * account entity.
 */
@Entity
@Unindex
public class StudentProfile extends BaseEntity {

    // PMD.UnusedPrivateField and Singular field as suppressed
    // as this is used by Objectify to specify Account as @Parent entity
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @Parent
    private Key<Account> account;

    @Id
    private String googleId;

    private String shortName;

    private String email;

    private String institute;

    private String nationality;

    /* only accepts "male", "female" or "other" */
    private String gender;

    @Unindex
    private Text moreInfo;

    @Index
    @Translate(InstantTranslatorFactory.class)
    private Instant modifiedDate;

    @SuppressWarnings("unused")
    private StudentProfile() {
        // required by Objectify
    }

    /**
     * Instantiates a new account.
     *
     * @param googleId
     *            the Google ID of the user.
     * @param shortName
     *            The shortened name of the user.
     * @param email
     *            The long-term (personal) email of the user.
     * @param institute
     *            The university/school/institute the student is from (useful
     *            for exchange students)
     * @param nationality
     *            The nationality the student is from (useful for
     *            exchange/foreign students)
     * @param gender
     *            The student's gender. Allows "other"
     * @param moreInfo
     *            Miscellaneous information, including external profile
     */
    public StudentProfile(String googleId, String shortName, String email, String institute,
                          String nationality, String gender, String moreInfo) {
        this.setGoogleId(googleId);
        this.setShortName(shortName);
        this.setEmail(email);
        this.setInstitute(institute);
        this.setNationality(nationality);
        this.setGender(gender);
        this.setMoreInfo(moreInfo);
        this.setModifiedDate(Instant.now());
    }

    public StudentProfile(String googleId) {
        this.setGoogleId(googleId);
        this.setShortName("");
        this.setEmail("");
        this.setInstitute("");
        this.setNationality("");
        this.setGender("other");
        this.setMoreInfo("");
        this.setModifiedDate(Instant.now());
    }

    public String getGoogleId() {
        return this.googleId;
    }

    /**
     * Sets the googleId.
     */
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
        this.account = Key.create(Account.class, googleId);
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public String getNationality() {
        return this.nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMoreInfo() {
        return this.moreInfo == null ? null : this.moreInfo.getValue();
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo == null ? null : new Text(moreInfo);
    }

    public Instant getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
