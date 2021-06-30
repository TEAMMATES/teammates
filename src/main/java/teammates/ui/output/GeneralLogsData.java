package teammates.ui.output;

import teammates.common.datatransfer.GeneralLogEntry;

import java.util.List;

public class GeneralLogsData extends ApiOutput{
    private final List<GeneralLogEntry> logEntries;

    public GeneralLogsData(List<GeneralLogEntry> logEntries) {
        this.logEntries = logEntries;
    }
}
