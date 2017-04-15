package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.StudentProfile;

/**
 * The data transfer object for StudentProfile entities.
 */
public class StudentProfileAttributes extends EntityAttributes {

    public String googleId;
    public String shortName;
    public String email;
    public String institute;
    public String nationality;
    public String gender; // only accepts "male", "female" or "other"
    public String moreInfo;
    public String pictureKey;
    public Date modifiedDate;

    public StudentProfileAttributes(String googleId, String shortName, String email, String institute,
                                    String nationality, String gender, String moreInfo, String pictureKey) {
        this.googleId = googleId;
        this.shortName = SanitizationHelper.sanitizeName(shortName);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
        this.nationality = SanitizationHelper.sanitizeName(nationality);
        this.gender = gender;
        this.moreInfo = moreInfo;
        this.pictureKey = pictureKey;
    }

    public StudentProfileAttributes(StudentProfile sp) {
        this.googleId = sp.getGoogleId();
        this.shortName = sp.getShortName();
        this.email = sp.getEmail();
        this.institute = sp.getInstitute();
        this.nationality = sp.getNationality();
        this.gender = sp.getGender();
        this.moreInfo = sp.getMoreInfo().getValue();
        this.pictureKey = sp.getPictureKey().getKeyString();
        this.modifiedDate = sp.getModifiedDate();
    }

    public StudentProfileAttributes() {
        // just a container so all can be null
        this.googleId = "";
        this.shortName = "";
        this.email = "";
        this.institute = "";
        this.nationality = "";
        this.gender = "other";
        this.moreInfo = "";
        this.pictureKey = "";
        this.modifiedDate = null;
    }

    // branch is not fully tested here: part of StudentCourseJoinAuthenticatedAction
    public String generateUpdateMessageForStudent() {
        if (isMultipleFieldsEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE;
        } else if (this.shortName.isEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE_SHORTNAME;
        } else if (this.email.isEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE_EMAIL;
        } else if (this.pictureKey.isEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE_PICTURE;
        } else if (this.moreInfo.isEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE_MOREINFO;
        } else if (this.nationality.isEmpty()) {
            return Const.StatusMessages.STUDENT_UPDATE_PROFILE_NATIONALITY;
        }
        return "";
    }

    private boolean isMultipleFieldsEmpty() {
        int numEmptyFields = StringHelper.countEmptyStrings(shortName, email, nationality, moreInfo, pictureKey);
        return numEmptyFields > 1;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();

        addNonEmptyError(validator.getInvalidityInfoForGoogleId(googleId), errors);

        // accept empty string values as it means the user has not specified anything yet.

        if (!shortName.isEmpty()) {
            addNonEmptyError(validator.getInvalidityInfoForPersonName(shortName), errors);
        }

        if (!email.isEmpty()) {
            addNonEmptyError(validator.getInvalidityInfoForEmail(email), errors);
        }

        if (!institute.isEmpty()) {
            addNonEmptyError(validator.getInvalidityInfoForInstituteName(institute), errors);
        }

        if (!nationality.isEmpty()) {
            addNonEmptyError(validator.getInvalidityInfoForNationality(nationality), errors);
        }

        addNonEmptyError(validator.getInvalidityInfoForGender(gender), errors);

        Assumption.assertNotNull(this.pictureKey);

        // No validation for modified date as it is determined by the system.
        // No validation for More Info. It will properly sanitized.

        return errors;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, StudentProfileAttributes.class);
    }

    @Override
    public Object toEntity() {
        return new StudentProfile(googleId, shortName, email, institute, nationality, gender,
                                  new Text(moreInfo), new BlobKey(this.pictureKey));
    }

    @Override
    public String getIdentificationString() {
        return this.googleId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "StudentProfile";
    }

    @Override
    public String getBackupIdentifier() {
        return "Student profile modified";
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, StudentProfileAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = SanitizationHelper.sanitizeGoogleId(this.googleId);
        this.shortName = SanitizationHelper.sanitizeForHtml(this.shortName);
        this.email = SanitizationHelper.sanitizeForHtml(this.email);
        this.institute = SanitizationHelper.sanitizeForHtml(this.institute);
        this.nationality = SanitizationHelper.sanitizeForHtml(this.nationality);
        this.gender = SanitizationHelper.sanitizeForHtml(this.gender);
        this.moreInfo = SanitizationHelper.sanitizeForHtml(this.moreInfo);
    }

}
