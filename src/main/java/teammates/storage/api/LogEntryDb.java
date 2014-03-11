package teammates.storage.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;


import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.LogEntryAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.LogEntry;

public class LogEntryDb extends EntitiesDb {

	private static final int MAX_LOGSEARCH_LIMIT = 10000;
	private static final Logger log = Utils.getLogger();
		
	@Override
	protected Object getEntity(EntityAttributes entity) {
		LogEntryAttributes logEntryToGet = (LogEntryAttributes) entity;
		return getLogAttributesForEmailAtTime(logEntryToGet.getEmail(), logEntryToGet.getId(), logEntryToGet.getTime());
	}
	
	public LogEntryAttributes getLogAttributesForEmailAtTime(String email, String googleId, long time) {
		LogEntry l = getLogForEmailAtTime(email, googleId, time);

		if (l == null) {
			if (email != null){
				log.info("Trying to get non-existent Student: " + email);
			} else {
				log.info("Trying to get non-existent Student: " + googleId);
			}
			
			return null;
		}
	
		return new LogEntryAttributes(l);
	}
	
	/**
	 * This method is not scalable. Not to be used unless for admin features.
	 * @return the list of all LogEntry in the database. 
	 */
	@Deprecated
	public List<LogEntryAttributes> getAllLogEntriesFrom(int from) { 
		List<LogEntryAttributes> list = new LinkedList<LogEntryAttributes>();
		List<LogEntry> entities = getLogEntryEntities(from, MAX_LOGSEARCH_LIMIT);
		Iterator<LogEntry> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new LogEntryAttributes(it.next()));
		}
		return list;
	}
	
	public Query getQueryWithRange(int from, int to){		
		Query q = getPM().newQuery(LogEntry.class);
		q.setOrdering("createdAt desc");
		q.setRange(from, to);
	
		return q;
	}
			
	private LogEntry getLogForEmailAtTime(String email, String googleId, long atTime) {
		
		Query q = getPM().newQuery(LogEntry.class);
		q.declareParameters("long createdAtParam");
		q.setFilter("createdAt == createdAtParam");
		
		
		if(email != null){
			q.declareParameters("String emailParam");
			q.setFilter("email == emailParam");
			@SuppressWarnings("unchecked")
			List<LogEntry> logList = (List<LogEntry>)q.execute(email);
			if (logList.isEmpty() || JDOHelper.isDeleted(logList.get(0))) {
				return null;
			}
			return logList.get(0);
		} else {
			q.declareParameters("String googleIdParam");
			q.setFilter("googleId == googleIdParam");			
			@SuppressWarnings("unchecked")
			List<LogEntry> logList = (List<LogEntry>)q.execute(googleId);
			if (logList.isEmpty() || JDOHelper.isDeleted(logList.get(0))) {
				return null;
			}
			return logList.get(0);
		}
	}
	
	private List<LogEntry> getLogEntryEntities(int from, int to) { 
		Query q = getQueryWithRange(from , to);
		
		@SuppressWarnings("unchecked")
		List<LogEntry> logList = (List<LogEntry>) q.execute();
		
		return logList;
	}

}
