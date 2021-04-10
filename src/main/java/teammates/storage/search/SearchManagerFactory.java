package teammates.storage.search;

/**
 * Factory that returns search manager implementation.
 */
public final class SearchManagerFactory {

    private static SearchManager instance;

    private SearchManagerFactory() {
        // prevents initialization
    }

    public static SearchManager getSearchManager() {
        return instance;
    }

    /**
     * Registers the search service into the factory.
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton") // ok to ignore as method is only invoked at application startup
    public static void registerSearchManager(SearchManager searchManager) {
        if (instance == null) {
            instance = searchManager;
        }
    }

    /**
     * Deletes all search documents in the search manager instance.
     */
    public static void resetSearchManager() {
        instance.resetCollections();
    }

}
