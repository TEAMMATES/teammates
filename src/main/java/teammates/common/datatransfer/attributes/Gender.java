package teammates.common.datatransfer.attributes;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the gender of a student.
 */
public enum Gender {
    @SerializedName("male")
    MALE,
    @SerializedName("female")
    FEMALE,
    @SerializedName("other")
    OTHER,
    ;

    /**
     * Returns the Gender enum value corresponding to {@code gender} or OTHER by default.
     */
    public static Gender getGenderEnumValue(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Gender.OTHER;
        }
    }
}
