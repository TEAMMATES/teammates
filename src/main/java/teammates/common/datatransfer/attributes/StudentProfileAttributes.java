package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
public class StudentProfileAttributes extends EntityAttributes<StudentProfile> {

    private static final String STUDENT_PROFILE_BACKUP_LOG_MSG = "Recently modified student profile::";
    private static final String ATTRIBUTE_NAME = "Student Profile";

    // Required
    public String googleId;

    // Optional
    public String shortName;
    public String email;
    public String institute;
    public String nationality;
    public Gender gender;
    public String moreInfo;
    public String pictureKey;
    public Instant modifiedDate;

    StudentProfileAttributes(String googleId) {
        this.googleId = googleId;
        this.shortName = "";
        this.email = "";
        this.institute = "";
        this.nationality = "";
        this.gender = Gender.OTHER;
        this.moreInfo = "";
        this.pictureKey = "";
        this.modifiedDate = Instant.now();
    }

    public static StudentProfileAttributes valueOf(StudentProfile sp) {
        return builder(sp.getGoogleId())
                .withShortName(sp.getShortName())
                .withEmail(sp.getEmail())
                .withInstitute(sp.getInstitute())
                .withGender(Gender.getGenderEnumValue(sp.getGender()))
                .withNationality(sp.getNationality())
                .withMoreInfo(sp.getMoreInfo())
                .withPictureKey(sp.getPictureKey())
                .withModifiedDate(sp.getModifiedDate())
                .build();
    }

    /**
     * Return new builder instance all string fields setted to {@code ""}
     * and with {@code gender = Gender.OTHER}.
     */
    public static Builder builder(String googleId) {
        return new Builder(googleId);
    }

    public StudentProfileAttributes getCopy() {
        return builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withGender(gender)
                .withNationality(nationality)
                .withMoreInfo(moreInfo)
                .withPictureKey(pictureKey)
                .withModifiedDate(modifiedDate)
                .build();
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);

        // accept empty string values as it means the user has not specified anything yet.

        if (!StringHelper.isEmpty(shortName)) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(shortName), errors);
        }

        if (!StringHelper.isEmpty(email)) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);
        }

        if (!StringHelper.isEmpty(institute)) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(institute), errors);
        }

        if (!StringHelper.isEmpty(nationality)) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForNationality(nationality), errors);
        }

        Assumption.assertNotNull(gender);

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
    public StudentProfile toEntity() {
        return new StudentProfile(googleId, shortName, email, institute, nationality, gender.name().toLowerCase(),
                                  moreInfo, this.pictureKey);
    }

    @Override
    public String getIdentificationString() {
        return this.googleId;
    }

    @Override
    public String getEntityTypeAsString() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public String getBackupIdentifier() {
        return STUDENT_PROFILE_BACKUP_LOG_MSG + googleId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, StudentProfileAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = SanitizationHelper.sanitizeGoogleId(this.googleId);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.shortNameOption.ifPresent(s -> shortName = s);
        updateOptions.emailOption.ifPresent(s -> email = s);
        updateOptions.instituteOption.ifPresent(s -> institute = s);
        updateOptions.nationalityOption.ifPresent(s -> nationality = s);
        updateOptions.genderOption.ifPresent(s -> gender = s);
        updateOptions.moreInfoOption.ifPresent(s -> moreInfo = s);
        updateOptions.pictureKeyOption.ifPresent(s -> pictureKey = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a profile.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String googleId) {
        return new UpdateOptions.Builder(googleId);
    }

    /**
     * A Builder class for {@link StudentProfileAttributes}.
     */
    public static class Builder {
        private static final String REQUIRED_FIELD_CANNOT_BE_NULL = "Required field cannot be null";

        private final StudentProfileAttributes profileAttributes;

        public Builder(String googleId) {
            Assumption.assertNotNull(REQUIRED_FIELD_CANNOT_BE_NULL, googleId);
            profileAttributes = new StudentProfileAttributes(googleId);
        }

        public Builder withShortName(String shortName) {
            if (shortName != null) {
                profileAttributes.shortName = SanitizationHelper.sanitizeName(shortName);
            }
            return this;
        }

        public Builder withEmail(String email) {
            if (email != null) {
                profileAttributes.email = SanitizationHelper.sanitizeEmail(email);
            }
            return this;
        }

        public Builder withInstitute(String institute) {
            if (institute != null) {
                profileAttributes.institute = SanitizationHelper.sanitizeTitle(institute);
            }
            return this;
        }

        public Builder withNationality(String nationality) {
            if (nationality != null) {
                profileAttributes.nationality = SanitizationHelper.sanitizeName(nationality);
            }
            return this;
        }

        public Builder withGender(Gender gender) {
            if (gender != null) {
                profileAttributes.gender = gender;
            }
            return this;
        }

        public Builder withMoreInfo(String moreInfo) {
            if (moreInfo != null) {
                profileAttributes.moreInfo = moreInfo;
            }
            return this;
        }

        public Builder withPictureKey(String pictureKey) {
            if (pictureKey != null) {
                profileAttributes.pictureKey = pictureKey;
            }
            return this;
        }

        public Builder withModifiedDate(Instant modifiedDate) {
            profileAttributes.modifiedDate = modifiedDate == null ? Instant.now() : modifiedDate;
            return this;
        }

        public StudentProfileAttributes build() {
            return profileAttributes;
        }
    }

    /**
     * Represents the gender of a student.
     */
    public enum Gender {
        MALE,
        FEMALE,
        OTHER;

        /**
         * Returns the Gender enum value corresponding to {@code gender}, or OTHER by default.
         */
        public static Gender getGenderEnumValue(String gender) {
            try {
                return Gender.valueOf(gender.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Gender.OTHER;
            }
        }
    }

    /**
     * Helper class to specific the fields to update in {@link StudentProfileAttributes}.
     */
    public static class UpdateOptions {
        private String googleId;

        private UpdateOption<String> shortNameOption = UpdateOption.empty();
        private UpdateOption<String> emailOption = UpdateOption.empty();
        private UpdateOption<String> instituteOption = UpdateOption.empty();
        private UpdateOption<String> nationalityOption = UpdateOption.empty();
        private UpdateOption<Gender> genderOption = UpdateOption.empty();
        private UpdateOption<String> moreInfoOption = UpdateOption.empty();
        private UpdateOption<String> pictureKeyOption = UpdateOption.empty();

        private UpdateOptions(String googleId) {
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, googleId);

            this.googleId = googleId;
        }

        public String getGoogleId() {
            return googleId;
        }

        @Override
        public String toString() {
            return "StudentAttributes.UpdateOptions ["
                    + "googleId = " + googleId
                    + ", shortName = " + shortNameOption
                    + ", email = " + emailOption
                    + ", institute = " + instituteOption
                    + ", nationality = " + nationalityOption
                    + ", gender = " + genderOption
                    + ", moreInfo = " + moreInfoOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder {
            private UpdateOptions updateOptions;

            private Builder(String googleId) {
                updateOptions = new UpdateOptions(googleId);
            }

            public Builder withShortName(String shortName) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, shortName);

                updateOptions.shortNameOption = UpdateOption.of(shortName);
                return this;
            }

            public Builder withEmail(String email) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, email);

                updateOptions.emailOption = UpdateOption.of(email);
                return this;
            }

            public Builder withInstitute(String institute) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, institute);

                updateOptions.instituteOption = UpdateOption.of(institute);
                return this;
            }

            public Builder withNationality(String nationality) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, nationality);

                updateOptions.nationalityOption = UpdateOption.of(nationality);
                return this;
            }

            public Builder withGender(Gender gender) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, gender);

                updateOptions.genderOption = UpdateOption.of(gender);
                return this;
            }

            public Builder withMoreInfo(String moreInfo) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, moreInfo);

                updateOptions.moreInfoOption = UpdateOption.of(moreInfo);
                return this;
            }

            public Builder withPictureKey(String pictureKey) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, pictureKey);

                updateOptions.pictureKeyOption = UpdateOption.of(pictureKey);
                return this;
            }

            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }
}
