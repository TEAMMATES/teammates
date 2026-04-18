package teammates.logic.external;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.QueryLogsParams;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    /**
     * Gets the list of logs satisfying the given criteria.
     */
    QueryLogsResults queryLogs(QueryLogsParams queryLogsParams);

}
