package teammates.e2e.util;

import teammates.test.AbstractBackDoor;

/**
 * Used to create API calls to the back-end without going through the UI.
 */
public final class E2EBackDoor extends AbstractBackDoor {

    private static E2EBackDoor instance = new E2EBackDoor();

    private E2EBackDoor() {
        // Utility class
    }

    public static E2EBackDoor getInstance() {
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
