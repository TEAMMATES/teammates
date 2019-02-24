package teammates.ui.webapi.output;

/**
 * API output for profile picture results.
 */
public class StudentProfilePictureResults extends ApiOutput {
    private final String message;
    private final String pictureKey;

    public StudentProfilePictureResults(String message, String pictureKey) {
        this.message = message;
        this.pictureKey = pictureKey;
    }

    public String getMessage() {
        return message;
    }

    public String getPictureKey() {
        return pictureKey;
    }
}
