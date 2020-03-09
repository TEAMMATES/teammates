package teammates.ui.webapi.output;

import javax.annotation.Nullable;

/**
 * Contains the common attributes for instructors and students.
 */
public class CommonSearchUserData {
    private final String name;
    private final String email;
    private final String courseId;
    private final JoinState joinState;

    @Nullable
    private final String googleId;

    public CommonSearchUserData(String name, String email, String courseId, String googleId, boolean isRegistered) {
        this.name = name;
        this.email = email;
        this.courseId = courseId;
        this.googleId = googleId;
        this.joinState = isRegistered ? JoinState.JOINED : JoinState.NOT_JOINED;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public JoinState getJoinState() {
        return joinState;
    }
}
