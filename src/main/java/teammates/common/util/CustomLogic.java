package teammates.common.util;

/**
 * Stores various custom logic that are used across classes.
 */
public final class CustomLogic {

    private CustomLogic() {
        // utility class
    }

    /**
     * Custom equals method that is able to handle null objects.
     * @param first first object to be compared
     * @param second second object to be compared
     * @return true of both objects are null or equal, false otherwise.
     */
    public static boolean customEquals(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        } else if (first == null || second == null) {
            return false;
        }
        return first.equals(second);
    }
}
