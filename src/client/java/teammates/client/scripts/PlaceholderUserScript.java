package teammates.client.scripts;

/**
 * Placeholder Script to configure execScript task when not run by user.
 */
public final class PlaceholderUserScript {
    private PlaceholderUserScript() {

    }

    public static void main(String[] args) {
        System.out.println("Please specify the script to be run using the -P flag "
                + "e.g ./gradlew -PuserScript=MyScript execScript");
    }
}
