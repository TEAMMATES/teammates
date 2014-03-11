package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.LogEntryAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.LogEntryDb;

/**
 * Handles  operations related to logentry.
 */
public class LogEntryLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	private static LogEntryLogic instance = null;
	private LogEntryDb logsDb = new LogEntryDb();
	private static Logger log = Utils.getLogger();
	
	public static LogEntryLogic inst() {
		if (instance == null)
			instance = new LogEntryLogic();
		return instance;
	}
	
	public void createLogEntry(LogEntryAttributes logEntry)
		throws InvalidParametersException, EntityAlreadyExistsException {
		logsDb.createEntity(logEntry);
	}
	
	public List<LogEntryAttributes> getAllLogsFrom(int from){
		return logsDb.getAllLogEntriesFrom(from);
	}
}