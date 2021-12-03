package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Legacy format of instructor privileges object, where map of string to boolean is used.
 *
 * @deprecated This legacy format exists only for database backward-compatibility. All business logic
 *         should be conducted using the most recent format.
 */
@Deprecated
public class InstructorPrivilegesLegacy {

    private final Map<String, Boolean> courseLevel;
    private final Map<String, Map<String, Boolean>> sectionLevel;
    private final Map<String, Map<String, Map<String, Boolean>>> sessionLevel;

    public InstructorPrivilegesLegacy() {
        this.courseLevel = new LinkedHashMap<>();
        this.sectionLevel = new LinkedHashMap<>();
        this.sessionLevel = new LinkedHashMap<>();
    }

    public Map<String, Boolean> getCourseLevel() {
        return courseLevel;
    }

    public Map<String, Map<String, Boolean>> getSectionLevel() {
        return sectionLevel;
    }

    public Map<String, Map<String, Map<String, Boolean>>> getSessionLevel() {
        return sessionLevel;
    }

}
