package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    List<ErrorLogEntry> getRecentErrorLogs();

}
