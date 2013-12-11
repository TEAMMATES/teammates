package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class AdminSearchPageAction extends Action {
	
	private static final Index INDEX = SearchServiceFactory.getSearchService()
			.getIndex(IndexSpec.newBuilder().setName("instructor_search_index"));

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException{
		
		new GateKeeper().verifyAdminPrivileges(account);
		
		String rebuildDoc = getRequestParamValue(ParamsNames.ADMIN_SEARCH_REBUILD_DOC);
		
		AdminSearchPageData data = new AdminSearchPageData(account);
		
		if(rebuildDoc != null) {
			//rebuild document to update search index to latest datastore records.
			//the search indexed will not be updated when a new user is added to the system
			Queue queue = QueueFactory.getQueue("search-document");
			queue.add(TaskOptions.Builder.withUrl("/searchTask").method(TaskOptions.Method.GET));
			statusToUser.add("Rebuild task submitted, please check again in a few minutes.");
			ThreadHelper.waitBriefly();
			String queryStr = getRequestParamValue("query");
			String limitStr = getRequestParamValue("limit");
			search(queryStr, limitStr);
		}else {
			String queryStr = getRequestParamValue("query");
			String limitStr = getRequestParamValue("limit");
			data.results = search(queryStr, limitStr);
			statusToUser.add("Found "+ data.results.size() + " results.");
		}
		
		return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
	}
	
	private List<Document> search(String queryStr, String limitStr) {
		List<Document> found = new ArrayList<Document>();
		if (queryStr == null || queryStr.trim().isEmpty()) {
			return found;
		}
		int limit = 50;
		if (limitStr != null) {
			try {
				limit = Integer.parseInt(limitStr);
			} catch (NumberFormatException e) {
				//TODO: handle this exception
			}
		}
		
		Query query = Query.newBuilder()
				.setOptions(QueryOptions.newBuilder().setLimit(limit).
						build()).build(queryStr);
		Results<ScoredDocument> results = INDEX.search(query);
		for (ScoredDocument scoredDoc : results) {
			String email = scoredDoc.getOnlyField("email").getText();
			  Document derived = Document.newBuilder()
			            .setId(scoredDoc.getId())
			            .addField(Field.newBuilder().setName("id").setText(
			            		scoredDoc.getOnlyField("id").getText()))
			            .addField(Field.newBuilder().setName("name").setText(
			            		scoredDoc.getOnlyField("name").getText()))
			            .addField(Field.newBuilder().setName("email").setHTML(
			            		String.format("<a href=\"mailto:%s\">%s</a>", email,email)))
			            .addField(Field.newBuilder().setName("link").setHTML(
			            		scoredDoc.getOnlyField("link").getHTML()))
			            .build();
	        found.add(derived);
	    }
		return found;
	}


}
