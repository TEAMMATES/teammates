package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.Instructor;

/**
 * The data transfer class for Instructor entities.
 */
public class InstructorAttributes extends EntityAttributes {
    private static Gson gson = Utils.getTeammatesGson();
    
    // Note: be careful when changing these variables as their names are used in *.json files.
    public String googleId;
    public String name;
    public String email;
    public String courseId;
    public Boolean isArchived;
    public String key;
    public String role;
    public boolean isDisplayedToStudents;
    public String displayedName;
    
    /**
     * The json representation of privileges, used for storing the 
     * instructorPrivilege content in instructor entity
     */
    public String instructorPrivilegesAsText;
    public transient InstructorPrivileges privileges;
    
    public static final String DEFAULT_DISPLAY_NAME = "Instructor";
    
    /**
     * Creates a new instructor with default access level and default displayedName
     * Deprecated as it is only to be used for testing
     * 
     * @param googleId
     * @param courseId
     * @param name
     * @param email
     */
    @Deprecated
    public InstructorAttributes(String googleId, String courseId, String name, String email) {
        this(googleId, courseId, name, email,
             Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, DEFAULT_DISPLAY_NAME,
             new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
    }

    /**
     * Creates a new instructor with params specified(isDisplayedToStudent is set to true by default)
     * 
     * @param googleId
     * @param courseId
     * @param name
     * @param email
     * @param role
     * @param displayedName
     * @param instructorPrivilegesAsText
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                String displayedName, String instructorPrivilegesAsText) {        
        this.googleId = Sanitizer.sanitizeGoogleId(googleId);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.isArchived = false;
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
        this.role = Sanitizer.sanitizeName(role);
        this.isDisplayedToStudents = true;
        this.displayedName = Sanitizer.sanitizeName(displayedName);
        this.instructorPrivilegesAsText = instructorPrivilegesAsText;
        this.privileges = this.getInstructorPrivilegesFromText();
    }
    
    /**
     * Create an instructor(isDisplayedToStudent is set to true by default)
     * 
     * @param googleId
     * @param courseId
     * @param name
     * @param email
     * @param role
     * @param displayedName
     * @param privileges
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                String displayedName, InstructorPrivileges privileges) {        
        this.googleId = Sanitizer.sanitizeGoogleId(googleId);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.isArchived = false;
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
        this.role = Sanitizer.sanitizeName(role);
        this.isDisplayedToStudents = true;
        this.displayedName = Sanitizer.sanitizeName(displayedName);
        this.privileges = privileges;
        this.instructorPrivilegesAsText = this.getTextFromInstructorPrivileges();     
    }
    
    /**
     * create an instructor
     * 
     * @param googleId
     * @param courseId
     * @param name
     * @param email
     * @param role
     * @param isDisplayedToStudents
     * @param displayName
     * @param privileges
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                boolean isDisplayedToStudents, String displayName, InstructorPrivileges privileges) {
        this(googleId, courseId, name, email, role, displayName, privileges);
        this.isDisplayedToStudents = isDisplayedToStudents;
        this.isArchived = false;
    }
    
    public InstructorAttributes(Instructor instructor) {
        this.googleId = instructor.getGoogleId();
        this.courseId = instructor.getCourseId();
        this.isArchived = instructor.getIsArchived();
        this.name = instructor.getName();
        this.email = instructor.getEmail();
        
        if (instructor.getRegistrationKey() == null) {
            instructor.setGeneratedKeyIfNull();
        }
        
        this.key = instructor.getRegistrationKey();
        
        if (instructor.getRole() == null) {
            this.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            this.role = instructor.getRole();
        }
        
        this.isDisplayedToStudents = instructor.isDisplayedToStudents();
        
        if (instructor.getDisplayedName() == null) {
            this.displayedName = DEFAULT_DISPLAY_NAME;
        } else {
            this.displayedName = instructor.getDisplayedName();
        }
        
        if (instructor.getInstructorPrivilegesAsText() == null) {
            this.privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
            this.instructorPrivilegesAsText = this.getTextFromInstructorPrivileges();
        } else {
            this.instructorPrivilegesAsText = instructor.getInstructorPrivilegesAsText();
        }
        
        this.privileges = this.getInstructorPrivilegesFromText();
    }
    
    @Deprecated
    public InstructorAttributes() {

    }
    
    public String getTextFromInstructorPrivileges() {
        return gson.toJson(privileges, InstructorPrivileges.class);
    }
    
    public InstructorPrivileges getInstructorPrivilegesFromText() {
        return gson.fromJson(instructorPrivilegesAsText, InstructorPrivileges.class);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayedName() {
        return displayedName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isDisplayedToStudents() {
        return isDisplayedToStudents;
    }
    
    public boolean isRegistered() {
        return googleId != null;
    }

    public Instructor toEntity() {
        if (key != null) {
            return new Instructor(googleId, courseId, name, email, key, role,
                                  isDisplayedToStudents, displayedName, instructorPrivilegesAsText);
        } else {
            return new Instructor(googleId, courseId, isArchived, name, email, role,
                                  isDisplayedToStudents, displayedName, instructorPrivilegesAsText);
        }
    }

    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        if (googleId != null) {
            error = validator.getInvalidityInfo(FieldType.GOOGLE_ID, googleId);
            if (!error.isEmpty()) { errors.add(error); }
        }
        
        error = validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.PERSON_NAME, name);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.EMAIL, email);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.PERSON_NAME, displayedName);
        if (!error.isEmpty()) { errors.add(error); }
        
        return errors;
    }
    
    public String toString() {
        return gson.toJson(this, InstructorAttributes.class);
    }
 
    @Override
    public String getIdentificationString() {
        return courseId + "/" + email;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Instructor";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, InstructorAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        googleId = Sanitizer.sanitizeGoogleId(googleId);
        name = Sanitizer.sanitizeHtmlForSaving(Sanitizer.sanitizeName(name));
        email = Sanitizer.sanitizeEmail(email);
        courseId = Sanitizer.sanitizeTitle(courseId);
        
        if (role == null) {
            role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            role = Sanitizer.sanitizeHtmlForSaving(Sanitizer.sanitizeName(role));
        }
        
        if (displayedName == null) {
            displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            displayedName = Sanitizer.sanitizeHtmlForSaving(Sanitizer.sanitizeName(displayedName));
        }
        
        if (instructorPrivilegesAsText == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
            instructorPrivilegesAsText = getTextFromInstructorPrivileges();
        }
    }
    
    public boolean isAllowedForPrivilege(String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(sectionName, privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String sectionName, String sessionName, String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(sectionName, sessionName, privilegeName);
    }
    
    /**
     * pre-condition: instructorPrivilegesAsText and privileges should be non-null
     * @param instructor
     * @return
     */
    public boolean isEqualToAnotherInstructor(InstructorAttributes instructor) {
        if (gson.toJson(this).equals(gson.toJson(instructor))) {
            return true;
        } else {
            return !instructorPrivilegesAsText.equals(instructor.instructorPrivilegesAsText) 
                   && privileges.equals(instructor.privileges);
        }
    }
    
    public boolean isCustomRole() {
        return Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM.equals(role);
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public String getRole() {
        return role;
    }
}
