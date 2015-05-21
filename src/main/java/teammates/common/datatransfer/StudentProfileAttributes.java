package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
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
        this.shortName = Sanitizer.sanitizeName(shortName);
        this.email = Sanitizer.sanitizeEmail(email);
        this.institute = Sanitizer.sanitizeTitle(institute);
        this.nationality = Sanitizer.sanitizeName(nationality);
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
        } else {
            if (this.shortName.isEmpty()) {
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
    }

    private boolean isMultipleFieldsEmpty() {
        int numEmptyFields = StringHelper.countEmptyStrings(shortName, email, nationality, moreInfo, pictureKey);
        return numEmptyFields > 1;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        error = validator.getInvalidityInfo(FieldValidator.FieldType.GOOGLE_ID, googleId);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        // accept empty string values as it means the user has not specified anything yet.

        if (!shortName.isEmpty()) {
            error = validator.getInvalidityInfo(FieldValidator.FieldType.PERSON_NAME, shortName);
            if (!error.isEmpty()) {
                errors.add(error);
            }
        }

        if (!email.isEmpty()) {
            error = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email);
            if (!error.isEmpty()) {
                errors.add(error);
            }
        }

        if (!institute.isEmpty()) {
            error = validator.getInvalidityInfo(FieldValidator.FieldType.INSTITUTE_NAME, institute);
            if (!error.isEmpty()) {
                errors.add(error);
            }
        }

        if (!nationality.isEmpty()) {
            error = validator.getInvalidityInfo(FieldValidator.FieldType.NATIONALITY, nationality);
            if (!error.isEmpty()) {
                errors.add(error);
            }
        }

        error = validator.getInvalidityInfo(FieldValidator.FieldType.GENDER, gender);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        Assumption.assertNotNull(this.pictureKey);

        // No validation for modified date as it is determined by the system.
        // No validation for More Info. It will properly sanitized.

        return errors;
    }

    public String toString() {
        return Utils.getTeammatesGson().toJson(this, StudentProfileAttributes.class);
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
        return Utils.getTeammatesGson().toJson(this, StudentProfileAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = Sanitizer.sanitizeGoogleId(this.googleId);
        this.shortName = Sanitizer.sanitizeForHtml(this.shortName);
        this.email = Sanitizer.sanitizeForHtml(this.email);
        this.institute = Sanitizer.sanitizeForHtml(this.institute);
        this.nationality = Sanitizer.sanitizeForHtml(this.nationality);
        this.gender = Sanitizer.sanitizeForHtml(this.gender);
        this.moreInfo = Sanitizer.sanitizeForHtml(this.moreInfo);
    }

}
