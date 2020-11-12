package teammates.lnp.util;

import teammates.test.AbstractBackDoor;

/**
 * Used to create API calls to the back-end without going through the UI.
 */
public final class BackDoor extends AbstractBackDoor {

    private static BackDoor instance = new BackDoor();

    private BackDoor() {
        // Utility class
    }

    public static BackDoor getInstance() {
        return instance;
    }

    @Override
    protected String getAppUrl() {
        return TestProperties.TEAMMATES_URL;
    }

    @Override
    protected String getBackdoorKey() {
        return TestProperties.BACKDOOR_KEY;
    }

    @Override
    protected String getCsrfKey() {
        return TestProperties.CSRF_KEY;
    }

}
