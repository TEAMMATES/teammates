package teammates.client.util;

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
        return ClientProperties.API_URL;
    }

    @Override
    protected String getBackdoorKey() {
        return ClientProperties.BACKDOOR_KEY;
    }

    @Override
    protected String getCsrfKey() {
        return ClientProperties.CSRF_KEY;
    }

}
