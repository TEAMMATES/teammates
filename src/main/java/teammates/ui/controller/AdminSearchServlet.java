// Copyright 2011 Google Inc. All Rights Reserved.

package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

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

/**
 * A demo servlet showing basic text search capabilities. This servlet has a
 * single index shared between all users. It illustrates how to add, search for
 * and remove documents from the shared index.
 * 
 */
public class AdminSearchServlet extends ActionServlet<AdminHomeHelper> {

	private static final long serialVersionUID = 1L;

	/**
	 * The index used by this application. Since we only have one index we
	 * create one instance only. We build an index with the default consistency,
	 * which is Consistency.PER_DOCUMENT. These types of indexes are most
	 * suitable for streams and feeds, and can cope with a high rate of updates.
	 */
	private static final Index INDEX = SearchServiceFactory.getSearchService()
			.getIndex(IndexSpec.newBuilder().setName("coord_search_index"));

	

	private static final Logger LOG = Logger.getLogger(AdminSearchServlet.class
			.getName());

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}

	@Override
	public void doAction(HttpServletRequest req, AdminHomeHelper helper) {
		System.out.println("admin search");
		
		String rebuildDoc = req.getParameter("build_doc");
		System.out.println("erbuild:"+rebuildDoc);
		
		//rebuild document
		if(rebuildDoc != null) {
			Queue queue = QueueFactory.getQueue("search-document");
			queue.add(TaskOptions.Builder.withUrl("/page/searchTask"));
			helper.statusMessage = "Rebuild task submitted, please check again in a few minutes.";
			Common.waitBriefly();
			search(req);
		}else {
			
			long count = search(req);
			helper.statusMessage = "Found "+Long.toString(count) + " results.";
		}
		
		
		
	}


	
	/**
	 * Searches the index for matching documents. If the query is not specified
	 * in the request, we search for any documents.
	 */
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
				LOG.severe("Failed to parse " + limitStr);
			}
		}
		List<Document> found = new ArrayList<Document>();
		String outcome = null;
		// Rather than just using a query we build a search request.
		// This allows us to specify other attributes, such as the
		// number of documents to be returned by search.
		Query query = Query.newBuilder()
				.setOptions(QueryOptions.newBuilder().setLimit(limit).
				// for deployed apps, uncomment the line below to demo
				// snippeting.
				// This will not work on the dev_appserver.
				// setFieldsToSnippet("content").
						build()).build(queryStr);
		LOG.info("Sending query " + query);
		Results<ScoredDocument> results = INDEX.search(query);
		for (ScoredDocument scoredDoc : results) {
			String email = scoredDoc.getOnlyField("email").getText();
			  Document derived = Document.newBuilder()
			            .setId(scoredDoc.getId())
			            .addField(Field.newBuilder().setName("id").setText(scoredDoc.getOnlyField("id").getText()))
			            .addField(Field.newBuilder().setName("name").setText(
			            		scoredDoc.getOnlyField("name").getText()))
			            .addField(Field.newBuilder().setName("email").setHTML(String.format("<a href=\"mailto:%s\">%s</a>", email,email)))
			            .addField(Field.newBuilder().setName("link").setHTML(scoredDoc.getOnlyField("link").getHTML()))
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

}