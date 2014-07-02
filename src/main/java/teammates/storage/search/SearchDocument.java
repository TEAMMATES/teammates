package teammates.storage.search;

import com.google.appengine.api.search.Document;

public interface SearchDocument {
    public Document toDocument();
}
