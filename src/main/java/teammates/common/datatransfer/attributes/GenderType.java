package teammates.common.datatransfer.attributes;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a student's gender.
 */
public enum GenderType {
    @SerializedName("male")
    MALE,

    @SerializedName("female")
    FEMALE,

    @SerializedName("other")
    OTHER
}
