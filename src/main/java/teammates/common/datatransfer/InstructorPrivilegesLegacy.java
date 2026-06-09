package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Stores the permissions of an instructor using section and session <em>names</em> as map keys.
 * This is the legacy format used exclusively for DB serialization.
 *
 * <p>This class intentionally contains no logic. All privilege logic must be performed using
 * {@link InstructorPrivileges} (the runtime UUID-keyed format). Conversion between the two formats
 * is centralised in {@code InstructorPermissionsLogic}.
 */
public final class InstructorPrivilegesLegacy {

    private final InstructorPermissionSet courseLevel;
    private final Map<String, InstructorPermissionSet> sectionLevel;
    private final Map<String, Map<String, InstructorPermissionSet>> sessionLevel;

    /**
     * Creates a legacy privileges object from all three maps. Used by Jackson for deserialization
     * and by {@code InstructorPermissionsLogic} when converting from the runtime format.
     */
    @JsonCreator
    public InstructorPrivilegesLegacy(
            InstructorPermissionSet courseLevel,
            Map<String, InstructorPermissionSet> sectionLevel,
            Map<String, Map<String, InstructorPermissionSet>> sessionLevel) {
        this.courseLevel = courseLevel != null ? courseLevel : new InstructorPermissionSet();
        this.sectionLevel = sectionLevel != null ? sectionLevel : new LinkedHashMap<>();
        this.sessionLevel = sessionLevel != null ? sessionLevel : new LinkedHashMap<>();
    }

    /**
     * Creates a legacy privileges object with no section or session entries and all course-level
     * privileges set to {@code false}.
     */
    public InstructorPrivilegesLegacy() {
        this(new InstructorPermissionSet(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public InstructorPermissionSet getCourseLevelPrivileges() {
        return courseLevel.getCopy();
    }

    /**
     * Returns the section-level privileges of the instructor, keyed by section name.
     */
    public Map<String, InstructorPermissionSet> getSectionLevelPrivileges() {
        Map<String, InstructorPermissionSet> copy = new LinkedHashMap<>();
        sectionLevel.forEach((key, value) -> copy.put(key, value.getCopy()));
        return copy;
    }

    /**
     * Returns the session-level privileges of the instructor, keyed by section name then session name.
     */
    public Map<String, Map<String, InstructorPermissionSet>> getSessionLevelPrivileges() {
        Map<String, Map<String, InstructorPermissionSet>> copy = new LinkedHashMap<>();
        sessionLevel.forEach((sectionKey, sessionMap) -> {
            Map<String, InstructorPermissionSet> sessionCopy = new LinkedHashMap<>();
            sessionMap.forEach((sessionKey, value) -> sessionCopy.put(sessionKey, value.getCopy()));
            copy.put(sectionKey, sessionCopy);
        });
        return copy;
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof InstructorPrivilegesLegacy)) {
            return false;
        }
        if (another == this) {
            return true;
        }
        InstructorPrivilegesLegacy rhs = (InstructorPrivilegesLegacy) another;
        return this.getCourseLevelPrivileges().equals(rhs.getCourseLevelPrivileges())
                && this.getSectionLevelPrivileges().equals(rhs.getSectionLevelPrivileges())
                && this.getSessionLevelPrivileges().equals(rhs.getSessionLevelPrivileges());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.getCourseLevelPrivileges(),
            this.getSectionLevelPrivileges(),
            this.getSessionLevelPrivileges()
        );
    }
}
