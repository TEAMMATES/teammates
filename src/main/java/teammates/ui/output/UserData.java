package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.User;

/**
 * The API output format of {@link User}.
 */
public class UserData extends ApiOutput {
    private final UUID userId;
    private final String email;
    private final String courseId;
    private final String name;

    public UserData(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.courseId = user.getCourseId();
        this.name = user.getName();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }
}
