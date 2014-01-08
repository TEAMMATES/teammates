package teammates.common.datatransfer;

import java.util.Comparator;
import java.util.Date;

import teammates.common.util.Assumption;

/**
 * Interface for Sessions, to be used for grouping
 * sessions together and sort them based on common attributes (time and name)
 * Current implementing classes:
 * - {@link EvaluationAttributes}
 * - {@link FeedbackSessionAttributes}
 */
public interface SessionAttributes {
	public Date getSessionStartTime();
	public Date getSessionEndTime();
	public String getSessionName();
	
	/**
	 * Comparator to sort SessionAttributes on ASCENDING order based on
	 * end time, followed by start time and session name
	 */
	static final Comparator<SessionAttributes> ASCENDING_ORDER = new Comparator<SessionAttributes>(){
		@Override
		public int compare(SessionAttributes session1, SessionAttributes session2) {
			Assumption.assertNotNull(session1.getSessionName());
			Assumption.assertNotNull(session1.getSessionStartTime());
			Assumption.assertNotNull(session1.getSessionEndTime());
			Assumption.assertNotNull(session2.getSessionName());
			Assumption.assertNotNull(session2.getSessionStartTime());
			Assumption.assertNotNull(session2.getSessionEndTime());
			int result = 0;
			
			//Compares end times
			result = session1.getSessionEndTime().after(session2.getSessionEndTime()) ? 1
					: (session1.getSessionEndTime().before(session2.getSessionEndTime()) ? -1 : 0);
			
			//If the end time is same, compares start times
			if (result == 0) {
				result = session1.getSessionStartTime().after(session2.getSessionStartTime()) ? 1
						: (session1.getSessionStartTime().before(session2.getSessionStartTime()) ? -1 : 0);
			}
			
			//if both end and start time is same, compares session name
			if (result == 0) {
				result = session1.getSessionName().compareTo(session2.getSessionName());
			}
			return result;
		}
	};
	
	
	/**
	 * Comparator to sort SessionAttributes on DESCENDING order based on
	 * end time, followed by start time and session name
	 */
	static final Comparator<SessionAttributes> DESCENDING_ORDER = new Comparator<SessionAttributes>(){
		@Override
		public int compare(SessionAttributes session1, SessionAttributes session2) {
			Assumption.assertNotNull(session1.getSessionName());
			Assumption.assertNotNull(session1.getSessionStartTime());
			Assumption.assertNotNull(session1.getSessionEndTime());
			Assumption.assertNotNull(session2.getSessionName());
			Assumption.assertNotNull(session2.getSessionStartTime());
			Assumption.assertNotNull(session2.getSessionEndTime());
			int result = 0;
			
			//Compares end times
			result = session1.getSessionEndTime().after(session2.getSessionEndTime()) ? -1
					: (session1.getSessionEndTime().before(session2.getSessionEndTime()) ? 1 : 0);
			
			//If the end time is same, compares start times
			if (result == 0) {
				result = session1.getSessionStartTime().after(session2.getSessionStartTime()) ? -1
						: (session1.getSessionStartTime().before(session2.getSessionStartTime()) ? 1 : 0);
			}
			
			//if both end and start time is same, compares session name
			if (result == 0) {
				result = session1.getSessionName().compareTo(session2.getSessionName());
			}
			return result;
		}
	};
}
