package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.core.StudentsLogic;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.GetResponse;


public class AdminSearchTaskServlet extends HttpServlet {
    
    protected static final Logger log = Utils.getLogger();

    private static final long serialVersionUID = 1L;


    public Index getIndex() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(Const.SearchIndex.STUDENT).build();
        return SearchServiceFactory.getSearchService().getIndex(indexSpec);
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.info("run rebuild index task");
            cleanupExistingSearchIndexes();
            buildNewSearchIndexes();
            resp.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            log.severe("Unexpected error while rebuild search index " + e);
        }
    }


    /**
     * Indexes student and instructor entries to build the table for search
     */
    private void buildNewSearchIndexes() {
        
        /**
         * Retrieve all students
         */   
        List<StudentAttributes> students = StudentsLogic.inst().getAllStudents();
        Iterator<StudentAttributes> it = students.iterator();
        
        /**
         *  add all students into document
         */
        
        while (it.hasNext()){            
            StudentAttributes student = it.next();
            StudentsLogic.inst().putDocument(student);
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