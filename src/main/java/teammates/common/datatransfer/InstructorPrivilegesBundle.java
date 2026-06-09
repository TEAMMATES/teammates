package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the custom privileges of a single instructor within a {@link DataBundle}.
 *
 * <p>This is the JSON-facing combined representation of the three instructor privilege tables.
 * Section and session keys, as well as {@link #instructorId}, refer to the (placeholder) ids of
 * the corresponding entities within the same data bundle.
 */
public class InstructorPrivilegesBundle {
    // TODO: combine this with InstructorPrivileges.
    private UUID instructorId;
    private InstructorPermissionSet courseLevel = new InstructorPermissionSet();
    private Map<UUID, InstructorPermissionSet> sectionLevel = new LinkedHashMap<>();
    private Map<UUID, Map<UUID, InstructorPermissionSet>> sessionLevel = new LinkedHashMap<>();

    public UUID getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(UUID instructorId) {
        this.instructorId = instructorId;
    }

    public InstructorPermissionSet getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(InstructorPermissionSet courseLevel) {
        this.courseLevel = courseLevel;
    }

    public Map<UUID, InstructorPermissionSet> getSectionLevel() {
        return sectionLevel;
    }

    public void setSectionLevel(Map<UUID, InstructorPermissionSet> sectionLevel) {
        this.sectionLevel = sectionLevel;
    }

    public Map<UUID, Map<UUID, InstructorPermissionSet>> getSessionLevel() {
        return sessionLevel;
    }

    public void setSessionLevel(Map<UUID, Map<UUID, InstructorPermissionSet>> sessionLevel) {
        this.sessionLevel = sessionLevel;
    }
}
