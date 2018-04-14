package teammates.storage.entity;

import java.time.Instant;
import java.util.Date;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.util.TimeHelper;

/**
 * Represents profile details for student entities associated with an
 * account entity.
 */
@Entity
@Unindex
public class StudentProfile extends BaseEntity {

    @Parent
    private Key<Account> account; // NOPMD - specifies parent as Account; used by Objectify

    @Id
    private String googleId;

    private String shortName;

    private String email;

    private String institute;

    private String nationality;

    /* only accepts "male", "female" or "other" */
    private String gender;

    /* must be html sanitized before saving */
    private Text moreInfo;

    private BlobKey pictureKey;

    @Index
    private Date modifiedDate;

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
                          String nationality, String gender, Text moreInfo, BlobKey pictureKey) {
        this.setGoogleId(googleId);
        this.setShortName(shortName);
        this.setEmail(email);
        this.setInstitute(institute);
        this.setNationality(nationality);
        this.setGender(gender);
        this.setMoreInfo(moreInfo);
        this.setModifiedDate(Instant.now());
        this.setPictureKey(pictureKey);
    }

    public StudentProfile(String googleId) {
        this.setGoogleId(googleId);
        this.setShortName("");
        this.setEmail("");
        this.setInstitute("");
        this.setNationality("");
        this.setGender("other");
        this.setMoreInfo(new Text(""));
        this.setPictureKey(new BlobKey(""));
        this.setModifiedDate(Instant.now());
    }

    public String getGoogleId() {
        return this.googleId;
    }

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

    public Text getMoreInfo() {
        return this.moreInfo;
    }

    public void setMoreInfo(Text moreInfo) {
        this.moreInfo = moreInfo;
    }

    public BlobKey getPictureKey() {
        return this.pictureKey;
    }

    public void setPictureKey(BlobKey pictureKey) {
        this.pictureKey = pictureKey;
    }

    public Instant getModifiedDate() {
        return TimeHelper.convertDateToInstant(this.modifiedDate);
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = TimeHelper.convertInstantToDate(modifiedDate);
    }

}
