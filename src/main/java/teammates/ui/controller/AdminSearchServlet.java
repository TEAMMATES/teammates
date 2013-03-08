
package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;

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

public class AdminSearchServlet extends ActionServlet<AdminHomeHelper> {

	private static final long serialVersionUID = 1L;

	private static final Index INDEX = SearchServiceFactory.getSearchService()
			.getIndex(IndexSpec.newBuilder().setName("instructor_search_index"));

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}

	@Override
	public void doAction(HttpServletRequest req, AdminHomeHelper helper) {
		
		String rebuildDoc = req.getParameter("build_doc");
		
		if(rebuildDoc != null) {
			//rebuild document to update search index to latest datastore records.
			//the search indexed will not be updated when a new user is added to the system
			Queue queue = QueueFactory.getQueue("search-document");
			queue.add(TaskOptions.Builder.withUrl("/searchTask").method(TaskOptions.Method.GET));
			helper.statusMessage = "Rebuild task submitted, please check again in a few minutes.";
			Common.waitBriefly();
			search(req);
		}else {
			
			long count = search(req);
			helper.statusMessage = "Found "+Long.toString(count) + " results.";
		}
		
		String url = req.getRequestURI();
		if (req.getQueryString() != null){
			url += "?" + req.getQueryString();
		}
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_SEARCH_SERVLET, Common.ADMIN_SEARCH_SERVLET_PAGE_LOAD,
				false, helper, url, null);
		
		
	}


	private long search(HttpServletRequest req) {
		String queryStr = req.getParameter("query");
		if (queryStr == null) {
			return 0;
		}
		String limitStr = req.getParameter("limit");
		int limit = 50;
		if (limitStr != null) {
			try {
				limit = Integer.parseInt(limitStr);
			} catch (NumberFormatException e) {
			}
		}
		List<Document> found = new ArrayList<Document>();
		
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
		req.setAttribute("found", found);
		return results.getNumberFound();
	}


	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_SEARCH;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.ADMIN_SEARCH_SERVLET_PAGE_LOAD){
			params = "adminSearch Page Load";
		} else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
            String e = (String)data.get(0);
            params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            params += e + "</span>";
        } else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}