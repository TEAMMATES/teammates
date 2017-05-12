package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

/**
 * Represents profile details for student entities associated with an
 * account entity.
 */
@PersistenceCapable
public class StudentProfile extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(StudentProfile.class);

    // PMD.UnusedPrivateField is suppressed as profileId is persisted to the database
    @SuppressWarnings("PMD.UnusedPrivateField")
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String profileId;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
    private String googleId;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String shortName;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String email;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String institute;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String nationality;

    @Persistent
    /* only accepts "male", "female" or "other" */
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String gender;

    @Persistent
    /* must be html sanitized before saving */
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Text moreInfo;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private BlobKey pictureKey;

    @Persistent
    private Date modifiedDate;

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
        this.setModifiedDate(new Date());
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
        this.setModifiedDate(new Date());
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

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
