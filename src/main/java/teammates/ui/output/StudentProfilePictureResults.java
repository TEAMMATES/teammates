package teammates.ui.output;

/**
 * API output for profile picture results.
 */
public class StudentProfilePictureResults extends ApiOutput {
    private final String pictureKey;

    public StudentProfilePictureResults(String pictureKey) {
        this.pictureKey = pictureKey;
    }

    public String getPictureKey() {
        return pictureKey;
    }
}
