package teammates.storage.search;

import com.google.appengine.api.search.Document;

public abstract class SearchDocument {
    public abstract Document toDocument();
}
