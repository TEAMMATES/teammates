package teammates.storage.api;

import java.util.List;

import javax.jdo.Query;

public class QueryWithParams {
    public QueryWithParams(Query query, Object[] params) {
        this.query = query;
        this.params = params;
    }
    public Query query;
    public Object[] params;
    public List<?> execute() {
        return (List<?>) query.executeWithArray(params);
    }
    public long delete() {
        return query.deletePersistentAll(params);
    }
}
