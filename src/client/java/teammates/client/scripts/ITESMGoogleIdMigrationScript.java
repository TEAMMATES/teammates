package teammates.client.scripts;

import java.io.IOException;
import java.util.regex.Pattern;

import teammates.storage.entity.Account;

/**
 * Script to change the googleId of instructor accounts in ITESM.
 */
public class ITESMGoogleIdMigrationScript extends GoogleIdMigrationBaseScript {

    private static final Pattern STUDENT_GOOGLE_ID_PATTERN = Pattern.compile("A\\d{8}@itesm\\.mx");

    public static void main(String[] args) throws IOException {
        new ITESMGoogleIdMigrationScript().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected String getLastPositionOfCursor() {
        return "";
    }

    @Override
    protected boolean isMigrationOfGoogleIdNeeded(Account account) {
        if (!account.isInstructor()) {
            // note that only ITESM instructors have been migrated to tec.mx
            return false;
        }

        String googleId = account.getGoogleId();
        // only in ITESM institute
        // the googleId should NOT be of student pattern
        return googleId.endsWith("@itesm.mx") && !STUDENT_GOOGLE_ID_PATTERN.matcher(googleId).matches();
    }

    @Override
    protected String generateNewGoogleId(Account oldAccount) {
        return oldAccount.getGoogleId().replace("@itesm.mx", "@tec.mx");
    }
}
