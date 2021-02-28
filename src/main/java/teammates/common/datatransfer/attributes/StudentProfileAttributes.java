package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.StudentProfile;

/**
 * The data transfer object for StudentProfile entities.
 */
public class StudentProfileAttributes extends EntityAttributes<StudentProfile> {

    public String googleId;

    public String shortName;
    public String email;
    public String institute;
    public String nationality;
    public Gender gender;
    public String moreInfo;
    public Instant modifiedDate;

    private StudentProfileAttributes(String googleId) {
        this.googleId = googleId;
        this.shortName = "";
        this.email = "";
        this.institute = "";
        this.nationality = "";
        this.gender = Gender.OTHER;
        this.moreInfo = "";
        this.modifiedDate = Instant.now();
    }

    public static StudentProfileAttributes valueOf(StudentProfile sp) {
        StudentProfileAttributes studentProfileAttributes = new StudentProfileAttributes(sp.getGoogleId());

        if (sp.getShortName() != null) {
            studentProfileAttributes.shortName = sp.getShortName();
        }
        if (sp.getEmail() != null) {
            studentProfileAttributes.email = sp.getEmail();
        }
        if (sp.getInstitute() != null) {
            studentProfileAttributes.institute = sp.getInstitute();
        }
        studentProfileAttributes.gender = Gender.getGenderEnumValue(sp.getGender());
        if (sp.getNationality() != null) {
            studentProfileAttributes.nationality = sp.getNationality();
        }
        if (sp.getMoreInfo() != null) {
            studentProfileAttributes.moreInfo = sp.getMoreInfo();
        }
        if (sp.getModifiedDate() != null) {
            studentProfileAttributes.modifiedDate = sp.getModifiedDate();
        }

        return studentProfileAttributes;
    }

    /**
     * Return a builder for {@link StudentProfileAttributes}.
     */
    public static Builder builder(String googleId) {
        return new Builder(googleId);
    }

    public StudentProfileAttributes getCopy() {
        StudentProfileAttributes studentProfileAttributes = new StudentProfileAttributes(googleId);

        studentProfileAttributes.shortName = shortName;
        studentProfileAttributes.email = email;
        studentProfileAttributes.institute = institute;
        studentProfileAttributes.gender = gender;
        studentProfileAttributes.nationality = nationality;
        studentProfileAttributes.moreInfo = moreInfo;
        studentProfileAttributes.modifiedDate = modifiedDate;

        return studentProfileAttributes;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getShortName() {
        return shortName;
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

    public Gender getGender() {
        return gender;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
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

        // No validation for modified date as it is determined by the system.
        // No validation for More Info. It will properly sanitized.

        return errors;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, StudentProfileAttributes.class);
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.email).append(this.shortName).append(this.institute)
                .append(this.googleId).append(this.gender.toString());
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            StudentProfileAttributes otherProfile = (StudentProfileAttributes) other;
            return Objects.equals(this.email, otherProfile.email)
                    && Objects.equals(this.shortName, otherProfile.shortName)
                    && Objects.equals(this.institute, otherProfile.institute)
                    && Objects.equals(this.googleId, otherProfile.googleId)
                    && Objects.equals(this.gender, otherProfile.gender);
        } else {
            return false;
        }
    }

    @Override
    public StudentProfile toEntity() {
        return new StudentProfile(googleId, shortName, email, institute, nationality, gender.name().toLowerCase(),
                                  moreInfo);
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
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a profile.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String googleId) {
        return new UpdateOptions.Builder(googleId);
    }

    /**
     * A builder class for {@link StudentProfileAttributes}.
     */
    public static class Builder extends BasicBuilder<StudentProfileAttributes, Builder> {
        private final StudentProfileAttributes profileAttributes;

        private Builder(String googleId) {
            super(new UpdateOptions(googleId));
            thisBuilder = this;

            profileAttributes = new StudentProfileAttributes(googleId);
        }

        @Override
        public StudentProfileAttributes build() {
            profileAttributes.update(updateOptions);

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
            if (gender == null) {
                return Gender.OTHER;
            }
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

        private UpdateOptions(String googleId) {
            Assumption.assertNotNull(googleId);

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
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String googleId) {
                super(new UpdateOptions(googleId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }
        }

    }

    /**
     * Basic builder to build {@link StudentProfileAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withShortName(String shortName) {
            Assumption.assertNotNull(shortName);

            updateOptions.shortNameOption = UpdateOption.of(shortName);
            return thisBuilder;
        }

        public B withEmail(String email) {
            Assumption.assertNotNull(email);

            updateOptions.emailOption = UpdateOption.of(email);
            return thisBuilder;
        }

        public B withInstitute(String institute) {
            Assumption.assertNotNull(institute);

            updateOptions.instituteOption = UpdateOption.of(institute);
            return thisBuilder;
        }

        public B withNationality(String nationality) {
            Assumption.assertNotNull(nationality);

            updateOptions.nationalityOption = UpdateOption.of(nationality);
            return thisBuilder;
        }

        public B withGender(Gender gender) {
            Assumption.assertNotNull(gender);

            updateOptions.genderOption = UpdateOption.of(gender);
            return thisBuilder;
        }

        public B withMoreInfo(String moreInfo) {
            Assumption.assertNotNull(moreInfo);

            updateOptions.moreInfoOption = UpdateOption.of(moreInfo);
            return thisBuilder;
        }

        public abstract T build();

    }
}
