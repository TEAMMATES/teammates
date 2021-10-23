package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;

/**
 * Default data structures for instructor permissions.
 */
public enum DefaultInstructorPermissions {
    // CHECKSTYLE.OFF:JavadocVariable
    COOWNER(getPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER)),
    MANAGER(getPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER)),
    OBSERVER(getPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER)),
    TUTOR(getPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR)),
    CUSTOM(getPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM));
    // CHECKSTYLE.ON:JavadocVariable

    @JsonValue
    private final String value;

    DefaultInstructorPermissions(Object value) {
        this.value = JsonUtils.toCompactJson(value).replace("\"", "\\\"");
    }

    public String getValue() {
        return value;
    }

    private static InstructorPermissionSet getPrivileges(String roleName) {
        return new InstructorPrivileges(roleName).getCourseLevelPrivileges();
    }

}
