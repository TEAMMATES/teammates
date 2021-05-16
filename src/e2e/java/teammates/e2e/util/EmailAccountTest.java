package teammates.e2e.util;

import org.testng.annotations.Test;

/**
 * Checks that the email account is ready for testing against staging/production server.
 */
public final class EmailAccountTest {

    @Test
    public void checkEmailAccount() throws Exception {
        if (TestProperties.isDevServer()) {
            // Access to actual email account is not necessary for dev server testing
            return;
        }
        new EmailAccount(TestProperties.TEST_EMAIL).getUserAuthenticated();
    }

}
