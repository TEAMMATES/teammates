package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.GetResponse;


public class AdminSearchTaskServlet extends HttpServlet {
	
	protected static final Logger log = Utils.getLogger();

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
		ArrayList<Document> docs = new ArrayList<Document>();
		
		/**
		 * Retrieve all instructors
		 */
		@SuppressWarnings("deprecation") //This method is deprecated to prevent unintended usage. This is an intended usage.
		List<InstructorAttributes> instructors = InstructorsLogic.inst().getAllInstructors();
		Iterator<InstructorAttributes> it = instructors.iterator();
		List<StudentAttributes> students = new ArrayList<StudentAttributes>();
		while (it.hasNext()) {
			InstructorAttributes instructor = it.next();
			docs.add(makeDocument(instructor.name, instructor.email, instructor.googleId, Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
			try {
				students.addAll(StudentsLogic.inst().getStudentsForCourse(instructor.courseId));
			} catch (EntityDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Add all students into docs
		 */
		Iterator<StudentAttributes> it2 = students.iterator();
		while (it2.hasNext()) {
			StudentAttributes stu = it2.next();
			docs.add(makeDocument(stu.name, stu.email, stu.googleId, Const.ActionURIs.STUDENT_HOME_PAGE));
		}
		
		/**
		 * Insert all students/instructors
		 */
		addDocument(docs);

	}
	
	/**
	 * Add student/instructor data to search index
	 */
	private void addDocument(ArrayList<Document> docs) {
		// Insert 200 documents each time
		 
		int currentIndex = 0;
		int finishingIndexOfCurrentBatch = 0;
		try {
			while(currentIndex < docs.size()){
				finishingIndexOfCurrentBatch = currentIndex + Const.SystemParams.MAX_NUM_OF_INPUT_FOR_APP_ENGINE_BATCH;
				if(finishingIndexOfCurrentBatch > docs.size()){
					finishingIndexOfCurrentBatch = docs.size();
				} 
				getIndex().put(docs.subList(currentIndex, finishingIndexOfCurrentBatch));
				currentIndex = finishingIndexOfCurrentBatch;
			}
			
		} catch (RuntimeException e) {
			log.warning("Failed to adding documents ");
		}
	}
	
	/**
	 * Create a document for the inputs
	 */
	private Document makeDocument(String name, String email, String id, String url){
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
							String.format("<a href=\"%s\">View</a>",url+"?"+Const.ParamsNames.USER_ID+"="+id)
								))
				;

		Document doc = docBuilder.build();
		return doc;
	}

	/**
	 * Clean up existing search indexes 
	 */
	private void cleanupExistingSearchIndexes() {

		try {
			while (true) {
				ArrayList<String> docIds = new ArrayList<String>();
				// Return a set of document IDs.
				GetRequest request = GetRequest.newBuilder()
						.setReturningIdsOnly(true).build();
				GetResponse<Document> response = getIndex().getRange(request);
				if (response.getResults().isEmpty()) {
					log.info("Empty search result.");
					break;
				}
				for (Document doc : response) {
					log.info("clean document " + doc.getId());
					docIds.add(doc.getId());
					if (docIds.size() == Const.SystemParams.MAX_NUM_OF_INPUT_FOR_APP_ENGINE_BATCH) {
						getIndex().delete(docIds);
						docIds.clear();
					}
				}

				if (!docIds.isEmpty()) {
					getIndex().delete(docIds);
				}
			}
		} catch (RuntimeException e) {
			log.warning("Failed to remove documents" + e.getLocalizedMessage());
		}
	}

}