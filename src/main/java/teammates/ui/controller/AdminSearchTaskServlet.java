package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.StudentData;
import teammates.storage.api.AccountsDb;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.ListRequest;
import com.google.appengine.api.search.ListResponse;
import com.google.appengine.api.search.SearchServiceFactory;

public class AdminSearchTaskServlet extends ActionServlet<AdminHomeHelper> {
	
	protected static final Logger log = Common.getLogger();

	private static final long serialVersionUID = 1L;

	private static final Index INDEX = SearchServiceFactory.getSearchService()
			.getIndex(IndexSpec.newBuilder().setName("coord_search_index"));


	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}

	@Override
	public void doAction(HttpServletRequest req, AdminHomeHelper helper) {
		cleanupExistingSearchIndexes();
	    buildNewSearchIndexes();
	    log.info("done rebuild search document");
		
	}

	/**
	 * Indexes student and coordinator entries to build the table for search
	 */
	private void buildNewSearchIndexes() {

		/**
		 * Insert coordinators
		 */
		AccountsDb accounts = new AccountsDb();
		List<CoordData> coords = accounts.getCoordinators();
		
		Iterator<CoordData> it = coords.iterator();
		while (it.hasNext()) {
			CoordData coord = it.next();
			addDocument(coord.name, coord.email, coord.id, Common.PAGE_COORD_HOME);
		}
		
		/**
		 * Insert students
		 */
		List<StudentData> students = accounts.getStudents();
		Iterator<StudentData> it2 = students.iterator();
		while (it2.hasNext()) {
			StudentData stu = it2.next();
			addDocument(stu.name, stu.email, stu.id, Common.PAGE_STUDENT_HOME);
		}

	}
	
	/**
	 * Add student/coordinator data to search index
	 */
	private void addDocument(String name, String email, String id, String url) {
		Document.Builder docBuilder = Document
				.newBuilder()
				.addField(
						Field.newBuilder().setName("name")
								.setText(name))
				.addField(
						Field.newBuilder().setName("email")
								.setText(email))
				.addField(
						Field.newBuilder().setName("link").setHTML(
							String.format("<a href=\"%s\">View</a>",url+"?"+Common.PARAM_USER_ID+"="+id)
								))
				;

		Document doc = docBuilder.build();
		try {
			INDEX.add(doc);
		} catch (RuntimeException e) {
			log.warning("Failed to add " + doc + e.getLocalizedMessage());
		}
	}


	/**
	 * Clean up existing search indexes 
	 */
	private void cleanupExistingSearchIndexes() {
		
		try {
		    while (true) {
		        List<String> docIds = new ArrayList<String>();
		        // Return a set of document IDs.
		        ListRequest request = ListRequest.newBuilder().build();
		        ListResponse<Document> response = INDEX.listDocuments(request);
		        if (response.getResults().isEmpty()) {
		            break;
		        }
		        for (Document doc : response) {
		            docIds.add(doc.getId());
		        }
		        INDEX.remove(docIds);
		    }
		} catch (RuntimeException e) {
		    log.warning("Failed to remove documents" + e.getLocalizedMessage());
		}
	}


	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_SEARCH;
	}

}