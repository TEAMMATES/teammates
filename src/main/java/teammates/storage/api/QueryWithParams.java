package teammates.storage.api;

import java.util.List;

import javax.jdo.Query;

/**
 * Data transfer object for bundling a Query together with parameters and expected result field.
 */
public class QueryWithParams {

    private Query query;
    private Object[] params;
    private String expectedResultField;

    QueryWithParams(Query query, Object[] params) {
        this(query, params, null);
    }

    QueryWithParams(Query query, Object[] params, String expectedResultField) {
        this.query = query;
        this.params = params;
        this.expectedResultField = expectedResultField;
    }

    void setExpectedResultField(String expectedResultField) {
        this.expectedResultField = expectedResultField;
    }

    List<?> execute() {
        if (expectedResultField != null) {
            query.setResult(expectedResultField);
        }
        return (List<?>) query.executeWithArray(params);
    }

    long deletePersistentAll() {
        return query.deletePersistentAll(params);
    }
}
