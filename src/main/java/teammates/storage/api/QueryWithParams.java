package teammates.storage.api;

import java.util.List;

import javax.jdo.Query;

/**
 * Struct for bundling a Query together with parameters and expected result field.
 */
public class QueryWithParams {

    public Query query;
    public Object[] params;
    public String expectedResultField;

    public QueryWithParams(Query query, Object[] params) {
        this(query, params, null);
    }

    public QueryWithParams(Query query, Object[] params, String expectedResult) {
        this.query = query;
        this.params = params;
        this.expectedResultField = expectedResult;
    }

    public List<?> execute() {
        if (expectedResultField != null) {
            query.setResult(expectedResultField);
        }
        return (List<?>) query.executeWithArray(params);
    }

    public long deletePersistentAll() {
        return query.deletePersistentAll(params);
    }
}
