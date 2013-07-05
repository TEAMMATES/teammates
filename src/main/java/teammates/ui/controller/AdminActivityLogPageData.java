package teammates.ui.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.ActivityLogEntry;

public class AdminActivityLogPageData extends PageData {
	
	public String offset;
	public String pageChange;
	public String filterQuery;
	public String queryMessage;
	public List<ActivityLogEntry> logs;
	
	private QueryParameters q;

	public AdminActivityLogPageData(AccountAttributes account) {
		super(account);
	}

	/**
	 * Checks in an array contains a specific value
	 * value is converted to lower case before comparing
	 */
	private boolean arrayContains(String[] arr, String value){
		for (int i = 0; i < arr.length; i++){
			if(arr[i].equals(value.toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Creates a QueryParameters object used for filtering
	 */
	public void generateQueryParameters(String query){
		query = query.toLowerCase();
		
		try{
			q = parseQuery(query);
		} catch (Exception e){
			this.queryMessage = "Error with the query: " + e.getMessage();
		}
	}
	
	
	/**
	 * Performs the actual filtering, based on QueryParameters
	 * returns false if the logEntry fails the filtering process
	 */
	public boolean filterLogs(ActivityLogEntry logEntry){
		if(!logEntry.toShow()){
			return false;
		}
		
		if(q == null){
			if (this.queryMessage == null){
				this.queryMessage = "Error parsing the query. QueryParameters not created.";
			}
			return true;
		}
		
		//Filter based on what is in the query
		if(q.toDate){
			if(logEntry.getTime() > q.toDateValue){
				return false;
			}
		}
		if(q.fromDate){
			if(logEntry.getTime() < q.fromDateValue){
				return false;
			}
		}
		if(q.request){
			if(!arrayContains(q.requestValues, logEntry.getServletName())){
				return false;
			}
		}
		if(q.response){
			if(!arrayContains(q.responseValues, logEntry.getAction())){
				return false;
			}
		}
		if(q.person){
			if(!logEntry.getName().toLowerCase().contains(q.personValue) && 
					!logEntry.getId().toLowerCase().contains(q.personValue) && 
					!logEntry.getEmail().toLowerCase().contains(q.personValue)){
				return false;
			}
		}
		if(q.role){
			if(!arrayContains(q.roleValues, logEntry.getRole())){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Converts the query string into a QueryParameters object
	 * 
	 */
	private QueryParameters parseQuery(String query) throws Exception{
		QueryParameters q = new QueryParameters();
		
		if(query == null || query.equals("")){
			return q;
		}
		
		query = query.replaceAll(" and ", "|");
		query = query.replaceAll(", ", ",");
		query = query.replaceAll(": ", ":");
		String[] tokens = query.split("\\|", -1); 

		for(int i = 0; i < tokens.length; i++){
			String[] pair = tokens[i].split(":", -1);
			if(pair.length != 2){
				throw new Exception("Invalid format");
			}
			String label = pair[0];
			String[] values = pair[1].split(",", -1);

			q.add(label, values);
		}
		
		return q;
	}
	
	
	/**
	 * QueryParameters inner class. Used only within this servlet, to hold the query data once it is parsed
	 * The boolean variables determine if the specific label was within the query
	 * The XXValue variables hold the data linked to the label in the query
	 */
	private class QueryParameters{		
		public boolean toDate;
		public long toDateValue;
		
		public boolean fromDate;
		public long fromDateValue;
		
		public boolean request;
		public String[] requestValues;
		
		public boolean response;
		public String[] responseValues;
		
		public boolean person;
		public String personValue;
		
		public boolean role;
		public String[] roleValues;
		
		public QueryParameters(){
			toDate = false;
			fromDate = false;
			request = false;
			response = false;
			person = false;
			role = false;
		}
		
		/**
		 * add a label and values in
		 */
		public void add(String label, String[] values) throws Exception{
			if(label.equals("from")){
				fromDate = true;				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
				Date d = sdf.parse(values[0] + " 00:00");				
				fromDateValue = d.getTime();
				
			} else if (label.equals("to")){
				toDate = true;
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
				Date d = sdf.parse(values[0] + " 23:59");				
				toDateValue = d.getTime();
				
			} else if (label.equals("request")){
				request = true;
				requestValues = values;
			} else if (label.equals("response")){
				response = true;
				responseValues = values;
			} else if (label.equals("person")){
				person = true;
				personValue = values[0];
			} else if (label.equals("role")){
				role = true;
				roleValues = values;
			} else {
				throw new Exception("Invalid label");
			}
		}
	}

}
