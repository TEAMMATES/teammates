package teammates.storage.api;

import java.util.List;

import javax.jdo.Query;

public class QueryWithParams {
    public QueryWithParams(Query query, Object[] params, String expectedResult) {
        this.query = query;
        this.params = params;
        this.expectedResultField = expectedResult;
    }
    public Query query;
    public Object[] params;
    public String expectedResultField;
    public List<?> execute() {
        query.setResult(expectedResultField);
        return (List<?>) query.executeWithArray(params);
    }
    public long delete() {
        return query.deletePersistentAll(params);
    }
}
