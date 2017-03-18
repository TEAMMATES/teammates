package teammates.storage.api;

import java.util.List;

import javax.jdo.Query;

/**
 * Struct for bundling a Query together with parameters and expected result field.
 */
public class QueryWithParams {

    private Query query;
    private Object[] params;
    private String expectedResultField;

    public QueryWithParams(Query query, Object[] params) {
        this(query, params, null);
    }

    public QueryWithParams(Query query, Object[] params, String expectedResultField) {
        this.query = query;
        this.params = params;
        this.expectedResultField = expectedResultField;
    }

    public void setExpectedResultField(String expectedResultField) {
        this.expectedResultField = expectedResultField;
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
