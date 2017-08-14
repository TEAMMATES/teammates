package teammates.common.datatransfer.attributes;

// CHECKSTYLE.OFF:import illegal package
import com.google.appengine.repackaged.com.google.gson.annotations.SerializedName;

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
