package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.StudentData;
import teammates.storage.api.AccountsDb;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.ListRequest;
import com.google.appengine.api.search.ListResponse;
import com.google.appengine.api.search.SearchServiceFactory;

public class AdminSearchTaskServlet extends HttpServlet {
	
	protected static final Logger log = Common.getLogger();

	private static final long serialVersionUID = 1L;


	public Index getIndex() {
	    IndexSpec indexSpec = IndexSpec.newBuilder().setName("instructor_search_index").build();
	    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			log.info("run rebuild index task");
			cleanupExistingSearchIndexes();
		    buildNewSearchIndexes();
			resp.setStatus(HttpServletResponse.SC_OK);
			
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected exception while rebuild search index"
							+ e);
		}
	}


	/**
	 * Indexes student and instructor entries to build the table for search
	 */
	private void buildNewSearchIndexes() {

		/**
		 * Insert instructors
		 */
		AccountsDb accounts = new AccountsDb();
		List<InstructorData> instructors = accounts.getInstructors();
		
		Iterator<InstructorData> it = instructors.iterator();
		while (it.hasNext()) {
			InstructorData instructor = it.next();
			addDocument(instructor.name, instructor.email, instructor.id, Common.PAGE_INSTRUCTOR_HOME);
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
	 * Add student/instructor data to search index
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
						Field.newBuilder().setName("id")
								.setText(id))
				.addField(
						Field.newBuilder().setName("link").setHTML(
							String.format("<a href=\"%s\">View</a>",url+"?"+Common.PARAM_USER_ID+"="+id)
								))
				;

		Document doc = docBuilder.build();
		try {
			getIndex().add(doc);
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
		        ArrayList<String> docIds = new ArrayList<String>();
		        // Return a set of document IDs.
		        ListRequest request = ListRequest.newBuilder().build();
		        ListResponse<Document> response = getIndex().listDocuments(request);
		        if (response.getResults().isEmpty()) {
		        	log.info("Empty search result.");
		            return;
		        }
		        for (Document doc : response) {
		        	log.info("clean document "+doc.getId());
		            docIds.add(doc.getId());
		        }
		        
					getIndex().remove(docIds);
				
		    }
		} catch (RuntimeException e) {
		    log.warning("Failed to remove documents" +e.getLocalizedMessage());
		}
		
	}

}