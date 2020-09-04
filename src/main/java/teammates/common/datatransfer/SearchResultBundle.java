package teammates.common.datatransfer;

/**
 * The basic search result bundle object.
 */
public abstract class SearchResultBundle {

    public int numberOfResults;

    public SearchResultBundle() {
        // prevents instantiation; to be instantiated as children classes
    }

}
